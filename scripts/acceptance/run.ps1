[CmdletBinding()]
param(
    [ValidateSet("start", "stop", "status", "probe", "load", "report", "full")]
    [string]$Action = "full",
    [string]$GatewayUrl = "http://127.0.0.1:9701",
    [string]$Api1Url = "http://127.0.0.1:9702",
    [string]$Api2Url = "http://127.0.0.1:9703",
    [string]$ComposeFile = "",
    [string]$ResultsDirectory = "",
    [string]$JMeterCommand = "jmeter"
)

Set-StrictMode -Version Latest
$ErrorActionPreference = "Stop"

$scriptDirectory = Split-Path -Parent $MyInvocation.MyCommand.Path
$repositoryRoot = (Resolve-Path (Join-Path $scriptDirectory "../..")).Path
$GatewayUrl = $GatewayUrl.TrimEnd("/")
$Api1Url = $Api1Url.TrimEnd("/")
$Api2Url = $Api2Url.TrimEnd("/")
if ([string]::IsNullOrWhiteSpace($ComposeFile)) {
    $ComposeFile = Join-Path $repositoryRoot "docker/acceptance/compose.yml"
}
if ([string]::IsNullOrWhiteSpace($ResultsDirectory)) {
    $ResultsDirectory = Join-Path $repositoryRoot ".scratch/redis-distributed-learning/evidence"
}

New-Item -ItemType Directory -Force -Path $ResultsDirectory | Out-Null

$composeProject = "playedu-redis-acceptance"
$composeArguments = @("--project-name", $composeProject, "-f", $ComposeFile)
$gatewayUri = [Uri]$GatewayUrl

function Invoke-Compose {
    param([Parameter(Mandatory = $true)][string[]]$Arguments)

    & docker compose @composeArguments @Arguments
    if ($LASTEXITCODE -ne 0) {
        throw "docker compose failed with exit code $LASTEXITCODE"
    }
}

function Invoke-JsonRequest {
    param(
        [Parameter(Mandatory = $true)][Uri]$Uri,
        [ValidateSet("GET", "POST")][string]$Method = "GET",
        [string]$Body = "",
        [hashtable]$Headers = @{}
    )

    $requestParameters = @{
        Uri                = $Uri
        Method             = $Method
        Headers            = $Headers
        TimeoutSec         = 20
        SkipHttpErrorCheck = $true
    }
    if (-not [string]::IsNullOrWhiteSpace($Body)) {
        $requestParameters.Body = $Body
        $requestParameters.ContentType = "application/json"
    }

    $response = Invoke-WebRequest @requestParameters
    $content = $response.Content
    if ($content -is [byte[]]) {
        $content = [Text.Encoding]::UTF8.GetString($content)
    } elseif ($null -ne $content) {
        $content = [string]$content
    }
    $json = $null
    if (-not [string]::IsNullOrWhiteSpace($content)) {
        try {
            $json = $content | ConvertFrom-Json
        } catch {
            $json = $null
        }
    }
    [PSCustomObject]@{
        StatusCode = [int]$response.StatusCode
        Headers    = $response.Headers
        Content    = $content
        Json       = $json
    }
}

function Wait-ForHealth {
    param(
        [Parameter(Mandatory = $true)][Uri]$Uri,
        [int]$TimeoutSeconds = 180
    )

    $deadline = (Get-Date).AddSeconds($TimeoutSeconds)
    while ((Get-Date) -lt $deadline) {
        try {
            $response = Invoke-JsonRequest -Uri $Uri -Headers @{
                "X-Forwarded-For" = "198.51.100.41"
            }
            if ($response.StatusCode -eq 200 -and $null -ne $response.Json -and $response.Json.status -eq "UP") {
                return
            }
        } catch {
            # The container is still starting or the application is still running migrations.
        }
        Start-Sleep -Seconds 2
    }
    throw "Timed out waiting for $Uri to report UP"
}

function Wait-ForGateway {
    Wait-ForHealth -Uri ([Uri]($GatewayUrl + "/actuator/health"))
}

function Reset-Redis {
    Invoke-Compose -Arguments @("exec", "-T", "redis", "redis-cli", "FLUSHDB")
}

function Start-Topology {
    Write-Host "Starting MySQL, Redis and both API instances..."
    Invoke-Compose -Arguments @("up", "-d", "mysql", "redis", "api-1", "api-2")
    Wait-ForHealth -Uri ([Uri]($Api1Url + "/actuator/health"))
    Wait-ForHealth -Uri ([Uri]($Api2Url + "/actuator/health"))

    Write-Host "Starting the load-balancing gateway after both APIs are healthy..."
    Invoke-Compose -Arguments @("up", "-d", "gateway")
    Wait-ForGateway
    Reset-Redis

    Write-Host "Waiting for the schema migration, then loading deterministic acceptance data..."
    Invoke-Compose -Arguments @("--profile", "seed", "run", "--rm", "seed")
    $rebuild = Invoke-JsonRequest -Uri ([Uri]($GatewayUrl + "/acceptance/v1/ranking/rebuild")) -Method POST -Headers @{
        "X-Forwarded-For" = "198.51.100.42"
    }
    if ($rebuild.StatusCode -ne 200 -or $null -eq $rebuild.Json -or $rebuild.Json.code -ne 0) {
        throw "Initial ranking rebuild failed: $($rebuild.Content)"
    }
    Write-Host "Acceptance topology is ready at $GatewayUrl"
}

function Stop-Topology {
    Invoke-Compose -Arguments @("down", "--remove-orphans")
}

function Show-Status {
    Invoke-Compose -Arguments @("ps")
}

function New-ProbeRequest {
    param(
        [string]$Path,
        [string]$Method = "GET",
        [string]$Body = "",
        [string]$ClientIp = "198.51.100.60"
    )

    Invoke-JsonRequest -Uri ([Uri]($GatewayUrl + $Path)) -Method $Method -Body $Body -Headers @{
        "X-Forwarded-For" = $ClientIp
    }
}

function Assert-ProbeResponse {
    param(
        [object]$Response,
        [string]$Name,
        [int]$ExpectedCode,
        [string]$ExpectedOutcome = ""
    )
    if ($null -eq $Response -or $null -eq $Response.Json) {
        throw "$Name returned an invalid JSON response"
    }
    if ($Response.StatusCode -ne 200 -or $Response.Json.code -ne $ExpectedCode) {
        throw "$Name returned an unexpected response: $($Response.Content)"
    }
    if (-not [string]::IsNullOrWhiteSpace($ExpectedOutcome) -and
        $Response.Json.data.outcome -ne $ExpectedOutcome) {
        throw "$Name returned outcome $($Response.Json.data.outcome), expected $ExpectedOutcome"
    }
}

function Get-RankingUserDuration {
    param(
        [Parameter(Mandatory = $true)][object]$Response,
        [Parameter(Mandatory = $true)][int]$UserId
    )

    $entries = @($Response.Json.data.today | Where-Object { $_.user_id -eq $UserId })
    if ($entries.Count -eq 0) {
        return $null
    }
    return [long]$entries[0].duration
}

function Wait-ForRankingDuration {
    param(
        [Parameter(Mandatory = $true)][int]$UserId,
        [Parameter(Mandatory = $true)][long]$MinimumDuration,
        [int]$TimeoutSeconds = 10
    )

    $startedAt = Get-Date
    $deadline = $startedAt.AddSeconds($TimeoutSeconds)
    while ((Get-Date) -lt $deadline) {
        $response = New-ProbeRequest -Path "/acceptance/v1/ranking" -ClientIp "198.51.100.80"
        if ($response.StatusCode -eq 200 -and $null -ne $response.Json -and $response.Json.code -eq 0) {
            $duration = Get-RankingUserDuration -Response $response -UserId $UserId
            if ($null -ne $duration -and $duration -ge $MinimumDuration) {
                $completedAt = Get-Date
                return [PSCustomObject]@{
                    Response    = $response
                    Duration    = $duration
                    StartedAt   = $startedAt
                    CompletedAt = $completedAt
                    LatencyMs   = [Math]::Round(($completedAt - $startedAt).TotalMilliseconds, 0)
                }
            }
        }
        Start-Sleep -Seconds 1
    }
    throw "Timed out waiting for user $UserId ranking duration to reach $MinimumDuration"
}

function Invoke-ConcurrentLockProbe {
    $client = [System.Net.Http.HttpClient]::new()
    $requests = @()
    try {
        foreach ($clientTag in @("61", "62")) {
            $request = [System.Net.Http.HttpRequestMessage]::new(
                [System.Net.Http.HttpMethod]::Post,
                [Uri]($GatewayUrl + "/acceptance/v1/lock"))
            $request.Headers.Add("X-Forwarded-For", "198.51.100.$clientTag")
            $request.Content = [System.Net.Http.StringContent]::new(
                '{"subject":"script-lock-subject","holdMillis":7000}',
                [Text.Encoding]::UTF8,
                "application/json")
            $requests += $request
        }

        [Threading.Tasks.Task[]]$tasks = @($requests | ForEach-Object { $client.SendAsync($_) })
        [Threading.Tasks.Task]::WaitAll($tasks)
        $results = @()
        foreach ($task in $tasks) {
            $content = $task.Result.Content.ReadAsStringAsync().Result
            $instance = ""
            if ($task.Result.Headers.Contains("X-PlayEdu-Instance")) {
                $instance = ($task.Result.Headers.GetValues("X-PlayEdu-Instance") -join ",")
            }
            $results += [PSCustomObject]@{
                StatusCode = [int]$task.Result.StatusCode
                InstanceId = $instance
                Json       = $content | ConvertFrom-Json
            }
        }
        return $results
    } finally {
        foreach ($request in $requests) {
            $request.Dispose()
        }
        $client.Dispose()
    }
}

function Invoke-RouteProbe {
    $clients = @()
    $requests = @()
    try {
        foreach ($index in 1..8) {
            $client = [System.Net.Http.HttpClient]::new()
            $request = [System.Net.Http.HttpRequestMessage]::new(
                [System.Net.Http.HttpMethod]::Get,
                [Uri]($GatewayUrl + "/acceptance/v1/instance"))
            $request.Headers.Add("X-Forwarded-For", "198.51.100.70")
            $clients += $client
            $requests += [PSCustomObject]@{
                Client  = $client
                Request = $request
                Task    = $client.SendAsync($request)
            }
        }

        [Threading.Tasks.Task[]]$tasks = @($requests | ForEach-Object { $_.Task })
        [Threading.Tasks.Task]::WaitAll($tasks)
        $results = @()
        foreach ($item in $requests) {
            $response = $item.Task.Result
            $instance = ""
            if ($response.Headers.Contains("X-PlayEdu-Instance")) {
                $instance = ($response.Headers.GetValues("X-PlayEdu-Instance") -join ",")
            }
            $results += [PSCustomObject]@{
                StatusCode = [int]$response.StatusCode
                InstanceId = $instance
                Json       = $response.Content.ReadAsStringAsync().Result | ConvertFrom-Json
            }
            $response.Dispose()
        }
        return $results
    } finally {
        foreach ($item in $requests) {
            $item.Request.Dispose()
        }
        foreach ($client in $clients) {
            $client.Dispose()
        }
    }
}

function Invoke-Probe {
    Reset-Redis

    $routes = Invoke-RouteProbe

    $lockResults = Invoke-ConcurrentLockProbe

    $ownerBody = '{"userId":7001,"courseId":8001,"hourId":9001,"sessionId":"script-owner-session","hourDuration":3600}'
    $conflictBody = '{"userId":7001,"courseId":8001,"hourId":9002,"sessionId":"","hourDuration":3600}'
    $newOwnerBody = '{"userId":7001,"courseId":8001,"hourId":9002,"sessionId":"script-new-session","hourDuration":3600}'
    $owner = New-ProbeRequest -Path "/acceptance/v1/lease/heartbeat" -Method POST -Body $ownerBody -ClientIp "198.51.100.71"
    $conflict = New-ProbeRequest -Path "/acceptance/v1/lease/heartbeat" -Method POST -Body $conflictBody -ClientIp "198.51.100.72"
    $rankingBaseline = New-ProbeRequest -Path "/acceptance/v1/ranking" -ClientIp "198.51.100.78"
    Assert-ProbeResponse -Response $owner -Name "lease owner" -ExpectedCode 0 -ExpectedOutcome "CREATED"
    Assert-ProbeResponse -Response $conflict -Name "lease conflict" -ExpectedCode 40901 -ExpectedOutcome "CONFLICT"
    Assert-ProbeResponse -Response $rankingBaseline -Name "ranking baseline" -ExpectedCode 0
    $baselineDuration = Get-RankingUserDuration -Response $rankingBaseline -UserId 7001
    if ($null -eq $baselineDuration) {
        throw "The ranking baseline does not contain user 7001"
    }

    Start-Sleep -Seconds 10
    $renewalSubmittedAt = Get-Date
    $renewal = New-ProbeRequest -Path "/acceptance/v1/lease/heartbeat" -Method POST -Body $ownerBody -ClientIp "198.51.100.73"
    $renewalCommittedAt = Get-Date
    Assert-ProbeResponse -Response $renewal -Name "lease renewal" -ExpectedCode 0 -ExpectedOutcome "CONTINUED"
    $renewalIncrement = [long]$renewal.Json.data.added_duration
    if ($renewalIncrement -le 0) {
        throw "The lease renewal did not commit a positive duration increment"
    }
    $rankingTargetDuration = $baselineDuration + $renewalIncrement
    $rankingPropagation = Wait-ForRankingDuration -UserId 7001 -MinimumDuration $rankingTargetDuration
    $rankingAfterLearning = $rankingPropagation.Response
    $submissionToQueryLatencyMs = [Math]::Round(
        ($rankingPropagation.CompletedAt - $renewalSubmittedAt).TotalMilliseconds,
        0)
    $commitToQueryLatencyMs = [Math]::Round(
        ($rankingPropagation.CompletedAt - $renewalCommittedAt).TotalMilliseconds,
        0)

    $stop = New-ProbeRequest -Path "/acceptance/v1/lease/stop" -Method POST -Body '{"userId":7001,"courseId":8001,"hourId":9001,"sessionId":"script-owner-session"}' -ClientIp "198.51.100.74"
    $expiryOwner = New-ProbeRequest -Path "/acceptance/v1/lease/heartbeat" -Method POST -Body $ownerBody -ClientIp "198.51.100.75"
    Start-Sleep -Seconds 31
    $afterExpiry = New-ProbeRequest -Path "/acceptance/v1/lease/heartbeat" -Method POST -Body $newOwnerBody -ClientIp "198.51.100.76"
    $newStop = New-ProbeRequest -Path "/acceptance/v1/lease/stop" -Method POST -Body '{"userId":7001,"courseId":8001,"hourId":9002,"sessionId":"script-new-session"}' -ClientIp "198.51.100.77"

    Assert-ProbeResponse -Response $stop -Name "lease stop" -ExpectedCode 0 -ExpectedOutcome "STOPPED"
    Assert-ProbeResponse -Response $expiryOwner -Name "lease expiry owner" -ExpectedCode 0 -ExpectedOutcome "CREATED"
    Assert-ProbeResponse -Response $afterExpiry -Name "lease after expiry" -ExpectedCode 0 -ExpectedOutcome "CREATED"
    Assert-ProbeResponse -Response $newStop -Name "lease new stop" -ExpectedCode 0 -ExpectedOutcome "STOPPED"

    Start-Sleep -Seconds 2
    $rankingBefore = New-ProbeRequest -Path "/acceptance/v1/ranking" -ClientIp "198.51.100.78"
    Reset-Redis
    $rebuildStarted = Get-Date
    $rankingAfter = New-ProbeRequest -Path "/acceptance/v1/ranking" -ClientIp "198.51.100.79"
    $rebuildLatencyMs = [Math]::Round(((Get-Date) - $rebuildStarted).TotalMilliseconds, 0)

    Assert-ProbeResponse -Response $rankingBefore -Name "ranking before Redis flush" -ExpectedCode 0
    Assert-ProbeResponse -Response $rankingAfter -Name "ranking after Redis flush" -ExpectedCode 0

    $rankingBeforeCanonical = $rankingBefore.Json.data | ConvertTo-Json -Depth 12 -Compress
    $rankingAfterCanonical = $rankingAfter.Json.data | ConvertTo-Json -Depth 12 -Compress

    $evidence = [PSCustomObject]@{
        generated_at          = (Get-Date).ToUniversalTime().ToString("o")
        gateway               = $GatewayUrl
        routed_instances      = @($routes | ForEach-Object { $_.Json.data.instance_id } | Sort-Object -Unique)
        route_responses       = $routes
        lock_responses        = $lockResults
        lease                 = [PSCustomObject]@{
            owner       = $owner.Json
            conflict    = $conflict.Json
            renewal     = $renewal.Json
            stop        = $stop.Json
            expiryOwner = $expiryOwner.Json
            afterExpiry = $afterExpiry.Json
            newStop     = $newStop.Json
        }
        ranking_baseline             = $rankingBaseline.Json
        ranking_after_learning       = $rankingAfterLearning.Json
        ranking_baseline_duration    = $baselineDuration
        ranking_target_duration      = $rankingTargetDuration
        ranking_submission_to_query_latency_ms = $submissionToQueryLatencyMs
        ranking_commit_to_query_latency_ms     = $commitToQueryLatencyMs
        ranking_before_redis_flush = $rankingBefore.Json
        ranking_after_redis_flush  = $rankingAfter.Json
        ranking_rebuild_latency_ms = $rebuildLatencyMs
        ranking_consistent_after_rebuild = $rankingBeforeCanonical -eq $rankingAfterCanonical
    }
    $probePath = Join-Path $ResultsDirectory "probe.json"
    $evidence | ConvertTo-Json -Depth 12 | Set-Content -LiteralPath $probePath -Encoding utf8NoBOM

    $routeCount = @($evidence.routed_instances).Count
    $lockEntered = @($lockResults | Where-Object { $_.Json.data.entered -eq $true }).Count
    $lockRejected = @($lockResults | Where-Object { $_.Json.code -eq 42301 }).Count
    Write-Host "Probe evidence: $probePath"
    Write-Host "Routed instances: $routeCount; lock entered: $lockEntered; lock contention responses: $lockRejected"
    if ($routeCount -lt 2) {
        throw "The gateway did not route probe traffic to both API instances"
    }
    if ($lockEntered -ne 1 -or $lockRejected -ne 1) {
        throw "The lock probe did not observe exactly one holder and one contention response"
    }
    if (-not $evidence.ranking_consistent_after_rebuild) {
        throw "The ranking changed after rebuilding from MySQL"
    }
}

function Invoke-JMeterLoad {
    Wait-ForGateway
    $jmxPath = Join-Path $repositoryRoot "docker/acceptance/jmeter/multi-instance.jmx"
    $jtlPath = Join-Path $ResultsDirectory "multi-instance.jtl"
    $jmeterLogPath = Join-Path $ResultsDirectory "jmeter.log"
    foreach ($path in @($jtlPath, $jmeterLogPath)) {
        if (Test-Path -LiteralPath $path) {
            Remove-Item -LiteralPath $path -Force
        }
    }
    $jmeterArguments = @(
        "-n",
        "-t", $jmxPath,
        "-l", $jtlPath,
        "-j", $jmeterLogPath,
        "-Jgateway_host=$($gatewayUri.Host)",
        "-Jgateway_port=$($gatewayUri.Port)",
        "-Jjmeter.save.saveservice.output_format=csv",
        "-Jjmeter.save.saveservice.response_data=true",
        "-Jjmeter.save.saveservice.responseHeaders=true"
    )

    if (Get-Command $JMeterCommand -ErrorAction SilentlyContinue) {
        Write-Host "Running local JMeter..."
        & $JMeterCommand @jmeterArguments
        if ($LASTEXITCODE -ne 0) {
            throw "JMeter failed with exit code $LASTEXITCODE"
        }
    } else {
        Write-Host "JMeter was not found locally; running the pinned JMeter container..."
        Invoke-Compose -Arguments @(
            "--profile", "load", "run", "--rm", "jmeter",
            "-n", "-t", "/tests/multi-instance.jmx", "-l", "/results/multi-instance.jtl",
            "-j", "/results/jmeter.log",
            "-Jgateway_host=gateway",
            "-Jgateway_port=80",
            "-Jjmeter.save.saveservice.output_format=csv",
            "-Jjmeter.save.saveservice.response_data=true",
            "-Jjmeter.save.saveservice.responseHeaders=true"
        )
    }
    Assert-JMeterResults
    Write-Host "JMeter results: $jtlPath"
}

function Assert-JMeterResults {
    $jtlPath = Join-Path $ResultsDirectory "multi-instance.jtl"
    if (-not (Test-Path -LiteralPath $jtlPath)) {
        throw "JMeter result file does not exist: $jtlPath"
    }
    $samples = @(Import-Csv -LiteralPath $jtlPath)
    $routeSamples = @($samples | Where-Object { $_.label -like "*route / instance*" })
    $lockSamples = @($samples | Where-Object { $_.label -like "*lock / acceptance probe*" })
    $rateSamples = @($samples | Where-Object { $_.label -like "*rate-limit /" })
    $leaseSamples = @($samples | Where-Object { $_.label -like "*lease*" })
    $expected429 = @($rateSamples | Where-Object { $_.responseCode -eq "429" }).Count
    $leaseFailures = @($leaseSamples | Where-Object { $_.success -ne "true" }).Count

    if ($routeSamples.Count -ne 8) {
        throw "JMeter route scenario produced $($routeSamples.Count) samples, expected 8"
    }
    if ($lockSamples.Count -ne 2) {
        throw "JMeter lock scenario produced $($lockSamples.Count) samples, expected 2"
    }
    if ($rateSamples.Count -ne 24 -or $expected429 -ne 12) {
        throw "JMeter rate scenario produced $($rateSamples.Count) samples and $expected429 HTTP 429 responses, expected 24 and 12"
    }
    if ($leaseSamples.Count -ne 5 -or $leaseFailures -ne 0) {
        throw "JMeter lease scenario produced $($leaseSamples.Count) samples and $leaseFailures assertion failures"
    }
    Write-Host "JMeter assertions: route=8, lock=2, rate=24/12 expected 429, lease=5/0 failures"
}

function Get-Percentile {
    param(
        [double[]]$Values,
        [double]$Percentile
    )
    if ($Values.Count -eq 0) {
        return 0
    }
    $ordered = @($Values | Sort-Object)
    $rank = [Math]::Max(1, [Math]::Ceiling($Percentile * $ordered.Count))
    return [Math]::Round([double]$ordered[$rank - 1], 0)
}

function Get-Rate {
    param([object[]]$Samples)
    if ($Samples.Count -lt 2) {
        return 0
    }
    $timestamps = @($Samples | ForEach-Object { [Int64]$_.timeStamp })
    $durationSeconds = ([double](($timestamps | Measure-Object -Maximum).Maximum - ($timestamps | Measure-Object -Minimum).Minimum)) / 1000
    if ($durationSeconds -le 0) {
        return 0
    }
    return [Math]::Round($Samples.Count / $durationSeconds, 2)
}

function Get-ResponseHeader {
    param([string]$ResponseHeaders, [string]$HeaderName)
    if ([string]::IsNullOrWhiteSpace($ResponseHeaders)) {
        return ""
    }
    $match = [Regex]::Match($ResponseHeaders, "(?im)^" + [Regex]::Escape($HeaderName) + ":\s*(.+)$")
    if ($match.Success) {
        return $match.Groups[1].Value.Trim()
    }
    return ""
}

function Get-JtlProperty {
    param([object]$Sample, [string]$Name)
    if ($null -eq $Sample) {
        return ""
    }
    $property = $Sample.PSObject.Properties[$Name]
    if ($null -eq $property -or $null -eq $property.Value) {
        return ""
    }
    return [string]$property.Value
}

function Invoke-Report {
    $jtlPath = Join-Path $ResultsDirectory "multi-instance.jtl"
    if (-not (Test-Path -LiteralPath $jtlPath)) {
        throw "JMeter result file does not exist: $jtlPath"
    }
    $samples = @(Import-Csv -LiteralPath $jtlPath)
    $definitions = @(
        [PSCustomObject]@{ Name = "路由分布"; Pattern = "route / instance" },
        [PSCustomObject]@{ Name = "分布式锁"; Pattern = "lock / acceptance probe" },
        [PSCustomObject]@{ Name = "共享限流"; Pattern = "rate-limit /" },
        [PSCustomObject]@{ Name = "学习租约"; Pattern = "lease" },
        [PSCustomObject]@{ Name = "排行榜查询"; Pattern = "ranking / snapshot" }
    )
    $probeEvidence = $null
    $probePath = Join-Path $ResultsDirectory "probe.json"
    if (Test-Path -LiteralPath $probePath) {
        try {
            $probeEvidence = Get-Content -LiteralPath $probePath -Raw | ConvertFrom-Json
        } catch {
            $probeEvidence = $null
        }
    }
    $probeLockInstances = @()
    $probeLeaseInstances = @()
    if ($null -ne $probeEvidence) {
        $probeLockInstances = @(
            $probeEvidence.lock_responses |
                ForEach-Object { $_.InstanceId } |
                Where-Object { $_ } |
                Sort-Object -Unique
        )
        if ($null -ne $probeEvidence.lease) {
            $probeLeaseInstances = @(
                $probeEvidence.lease.PSObject.Properties |
                    ForEach-Object { $_.Value.data.instance_id } |
                    Where-Object { $_ } |
                    Sort-Object -Unique
            )
        }
    }
    $rows = @()
    foreach ($definition in $definitions) {
        $selected = @($samples | Where-Object { $_.label -like "*$($definition.Pattern)*" })
        $elapsed = @($selected | ForEach-Object { [double]$_.elapsed })
        $failed = @($selected | Where-Object { $_.success -ne "true" }).Count
        $expected429 = @($selected | Where-Object { $_.responseCode -eq "429" }).Count
        $routes = @($selected | ForEach-Object {
                $responseHeaders = Get-JtlProperty -Sample $_ -Name "responseHeaders"
                if (-not [string]::IsNullOrWhiteSpace($responseHeaders)) {
                    Get-ResponseHeader -ResponseHeaders $responseHeaders -HeaderName "X-PlayEdu-Instance"
                }
            } | Where-Object { $_ } | Sort-Object -Unique)
        if ($routes.Count -eq 0 -and $definition.Name -eq "路由分布" -and $null -ne $probeEvidence) {
            $routes = @($probeEvidence.routed_instances | Sort-Object -Unique)
        }
        if ($routes.Count -eq 0 -and $definition.Name -eq "分布式锁") {
            $routes = $probeLockInstances
        }
        if ($routes.Count -eq 0 -and $definition.Name -eq "学习租约") {
            $routes = $probeLeaseInstances
        }
        $rows += [PSCustomObject]@{
            场景 = $definition.Name
            请求数 = $selected.Count
            吞吐量每秒 = Get-Rate -Samples $selected
            错误率百分比 = if ($selected.Count -eq 0) { 0 } else { [Math]::Round(($failed * 100) / $selected.Count, 2) }
            P95毫秒 = Get-Percentile -Values $elapsed -Percentile 0.95
            P99毫秒 = Get-Percentile -Values $elapsed -Percentile 0.99
            预期429 = $expected429
            实例 = ($routes -join ", ")
        }
    }

    $lockSamples = @($samples | Where-Object { $_.label -like "*lock / acceptance probe*" })
    $lockEntered = 0
    $lockRejected = 0
    foreach ($sample in $lockSamples) {
        try {
            $responseData = Get-JtlProperty -Sample $sample -Name "responseData"
            if ([string]::IsNullOrWhiteSpace($responseData)) { continue }
            $body = $responseData | ConvertFrom-Json
            if ($body.data.entered -eq $true) { $lockEntered++ }
            if ($body.code -eq 42301) { $lockRejected++ }
        } catch {
            # Keep the latency table useful even if an old JMeter version omitted responseData.
        }
    }

    $leaseOutcomes = @{}
    foreach ($sample in @($samples | Where-Object { $_.label -like "*lease*" })) {
        try {
            $responseData = Get-JtlProperty -Sample $sample -Name "responseData"
            if ([string]::IsNullOrWhiteSpace($responseData)) { continue }
            $outcome = ($responseData | ConvertFrom-Json).data.outcome
            if ($outcome) {
                if (-not $leaseOutcomes.ContainsKey($outcome)) { $leaseOutcomes[$outcome] = 0 }
                $leaseOutcomes[$outcome]++
            }
        } catch {
        }
    }
    if ($null -ne $probeEvidence) {
        if ($lockEntered -eq 0) {
            $lockEntered = @($probeEvidence.lock_responses | Where-Object { $_.Json.data.entered -eq $true }).Count
        }
        if ($lockRejected -eq 0) {
            $lockRejected = @($probeEvidence.lock_responses | Where-Object { $_.Json.code -eq 42301 }).Count
        }
        if ($leaseOutcomes.Count -eq 0 -and $null -ne $probeEvidence.lease) {
            foreach ($property in $probeEvidence.lease.PSObject.Properties) {
                $outcome = $property.Value.data.outcome
                if ($outcome) {
                    if (-not $leaseOutcomes.ContainsKey($outcome)) { $leaseOutcomes[$outcome] = 0 }
                    $leaseOutcomes[$outcome]++
                }
            }
        }
    }

    $reportPath = Join-Path $ResultsDirectory "multi-instance-report.md"
    $markdownTick = [char]96
    $codeFence = ([char]96).ToString() * 3
    $leaseOutcomeSummary =
        (($leaseOutcomes.GetEnumerator() | ForEach-Object { "$($_.Key)=$($_.Value)" }) -join ", ")
    $submissionToQueryLatency = "未记录"
    $commitToQueryLatency = "未记录"
    if ($null -ne $probeEvidence) {
        $submissionLatencyProperty =
            $probeEvidence.PSObject.Properties["ranking_submission_to_query_latency_ms"]
        $commitLatencyProperty =
            $probeEvidence.PSObject.Properties["ranking_commit_to_query_latency_ms"]
        if ($null -ne $submissionLatencyProperty) {
            $submissionToQueryLatency = "$($submissionLatencyProperty.Value) ms"
        }
        if ($null -ne $commitLatencyProperty) {
            $commitToQueryLatency = "$($commitLatencyProperty.Value) ms"
        }
    }
    $lines = @(
        "# PlayEdu 双 API 实例验收报告",
        "",
        "> 此文件由 scripts/acceptance/run.ps1 -Action report 根据 JMeter JTL 自动生成。429 在共享限流场景中是预期结果，同时保留在 HTTP 错误率中以避免掩盖真实响应。",
        "",
        "- 生成时间：$(Get-Date -Format o)",
        "- 入口：$markdownTick$GatewayUrl$markdownTick",
        "- JTL：$markdownTick$jtlPath$markdownTick",
        "",
        "## 核心指标",
        "",
        "| 场景 | 请求数 | 吞吐量/s | HTTP 错误率 | P95/ms | P99/ms | 预期 429 | 实例 |",
        "| --- | ---: | ---: | ---: | ---: | ---: | ---: | --- |"
    )
    foreach ($row in $rows) {
        $lines += "| $($row.场景) | $($row.请求数) | $($row.吞吐量每秒) | $($row.错误率百分比)% | $($row.P95毫秒) | $($row.P99毫秒) | $($row.预期429) | $($row.实例) |"
    }
    $lines += @(
        "",
        "## 正确性证据",
        "",
        "- 锁探针进入受保护区次数：$lockEntered；竞争失败次数：$lockRejected；验收条件是 1/1。",
        "- 租约结果：$leaseOutcomeSummary。",
        "- 路由实例来自响应头 X-PlayEdu-Instance；应同时出现 api-1 与 api-2。",
        "- 权威 MySQL 学习提交到榜单可查询：$submissionToQueryLatency；提交完成后到查询可见：$commitToQueryLatency。",
        "- Redis 清空后的排行榜重建延迟由 HTTP 探针写入 probe.json；权威来源仍是 MySQL。",
        "- JMeter 版本可能省略 responseData/responseHeaders 列；JTL 用于延迟与 HTTP 状态，锁/租约语义和实例证据由同轮 probe.json 补充。",
        "",
        "## 复现",
        "",
        "$codeFence`powershell",
        "pwsh -NoProfile -File scripts/acceptance/run.ps1 -Action full",
        "pwsh -NoProfile -File scripts/acceptance/run.ps1 -Action stop",
        $codeFence
    )
    $lines | Set-Content -LiteralPath $reportPath -Encoding utf8NoBOM
    Write-Host "Report: $reportPath"
}

switch ($Action) {
    "start"  { Start-Topology }
    "stop"   { Stop-Topology }
    "status" { Show-Status }
    "probe"  { Wait-ForGateway; Invoke-Probe }
    "load"   { Invoke-JMeterLoad }
    "report" { Invoke-Report }
    "full"   {
        Start-Topology
        Invoke-Probe
        Invoke-JMeterLoad
        Invoke-Report
    }
}
