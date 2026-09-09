/*
 * Copyright (C) 2023 杭州白书科技有限公司
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package xyz.playedu.api.controller.acceptance;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import xyz.playedu.common.redis.RedisDistributedLock;
import xyz.playedu.common.redis.RedisLockException;
import xyz.playedu.common.types.JsonResponse;
import xyz.playedu.course.domain.DailyLearningRankingEntry;
import xyz.playedu.course.service.ActiveLearningLeaseService;
import xyz.playedu.course.service.impl.DailyLearningRankingService;

/**
 * HTTP seams used by the reproducible two-instance acceptance test.
 *
 * <p>The controller is disabled by default and must never be enabled on a public deployment.
 */
@RestController
@RequestMapping("/acceptance/v1")
@ConditionalOnProperty(prefix = "playedu.acceptance", name = "enabled", havingValue = "true")
public class AcceptanceProbeController {

    private static final int INVALID_REQUEST_CODE = 40001;
    private static final int LOCK_CONTENTION_CODE = 42301;
    private static final int ACTIVE_LEARNING_CONFLICT_CODE = 40901;
    private static final int INVALID_LEARNING_SESSION_CODE = 40902;

    private final String instanceId;
    private final RedisDistributedLock distributedLock;
    private final ActiveLearningLeaseService activeLearningLeaseService;
    private final DailyLearningRankingService dailyLearningRankingService;

    public AcceptanceProbeController(
            Environment environment,
            RedisDistributedLock distributedLock,
            ActiveLearningLeaseService activeLearningLeaseService,
            DailyLearningRankingService dailyLearningRankingService) {
        this.instanceId = environment.getProperty("playedu.instance-id", "local");
        this.distributedLock = distributedLock;
        this.activeLearningLeaseService = activeLearningLeaseService;
        this.dailyLearningRankingService = dailyLearningRankingService;
    }

    @GetMapping("/instance")
    public JsonResponse instance() {
        return JsonResponse.data(Map.of("instance_id", instanceId));
    }

    @PostMapping("/lock")
    public JsonResponse lock(@RequestBody LockRequest request) {
        if (request == null || request.subject() == null || request.subject().isBlank()) {
            return JsonResponse.error("subject 不能为空", INVALID_REQUEST_CODE);
        }
        long holdMillis = request.holdMillis() == null ? 0 : request.holdMillis();
        if (holdMillis < 0 || holdMillis > 15_000) {
            return JsonResponse.error("hold_millis 必须在 0 到 15000 之间", INVALID_REQUEST_CODE);
        }

        try {
            Map<String, Object> data =
                    distributedLock.execute(
                            "acceptance",
                            request.subject(),
                            () -> {
                                sleep(holdMillis);
                                return Map.of(
                                        "entered", true,
                                        "instance_id", instanceId,
                                        "hold_millis", holdMillis);
                            });
            return JsonResponse.data(data);
        } catch (RedisLockException exception) {
            return new JsonResponse(
                    LOCK_CONTENTION_CODE,
                    "受保护区竞争失败",
                    Map.of("entered", false, "instance_id", instanceId));
        }
    }

    @PostMapping("/lease/heartbeat")
    public JsonResponse leaseHeartbeat(@RequestBody LeaseHeartbeatRequest request) {
        if (request == null || !request.isValid()) {
            return JsonResponse.error(
                    "user_id、course_id、hour_id 和 hour_duration 不能为空", INVALID_REQUEST_CODE);
        }

        ActiveLearningLeaseService.HeartbeatResult result =
                activeLearningLeaseService.heartbeat(
                        request.userId(),
                        request.courseId(),
                        request.hourId(),
                        request.sessionId(),
                        request.hourDuration());
        Map<String, Object> data = heartbeatData(result);
        if (result.outcome() == ActiveLearningLeaseService.Outcome.CONFLICT) {
            return new JsonResponse(ACTIVE_LEARNING_CONFLICT_CODE, "当前存在活跃学习课时", data);
        }
        if (result.outcome() == ActiveLearningLeaseService.Outcome.INVALID_SESSION) {
            return new JsonResponse(INVALID_LEARNING_SESSION_CODE, "学习会话已失效", data);
        }
        return JsonResponse.data(data);
    }

    @PostMapping("/lease/stop")
    public JsonResponse leaseStop(@RequestBody LeaseStopRequest request) {
        if (request == null || !request.isValid()) {
            return JsonResponse.error(
                    "user_id、course_id、hour_id 和 session_id 不能为空", INVALID_REQUEST_CODE);
        }

        ActiveLearningLeaseService.Outcome outcome =
                activeLearningLeaseService.stop(
                        request.userId(),
                        request.courseId(),
                        request.hourId(),
                        request.sessionId());
        Map<String, Object> data = Map.of("outcome", outcome.name(), "instance_id", instanceId);
        if (outcome == ActiveLearningLeaseService.Outcome.INVALID_SESSION
                || outcome == ActiveLearningLeaseService.Outcome.CONFLICT) {
            return new JsonResponse(INVALID_LEARNING_SESSION_CODE, "学习会话已失效", data);
        }
        return JsonResponse.data(data);
    }

    @GetMapping("/ranking")
    public JsonResponse ranking() {
        Map<String, List<DailyLearningRankingEntry>> data = new LinkedHashMap<>();
        data.put("today", dailyLearningRankingService.todayTop10());
        data.put("yesterday", dailyLearningRankingService.yesterdayTop10());
        return JsonResponse.data(data);
    }

    @PostMapping("/ranking/rebuild")
    public JsonResponse rebuildRanking() {
        dailyLearningRankingService.rebuildTodayAndYesterday();
        return JsonResponse.data(Map.of("rebuilt", true, "instance_id", instanceId));
    }

    private Map<String, Object> heartbeatData(ActiveLearningLeaseService.HeartbeatResult result) {
        return Map.of(
                "outcome", result.outcome().name(),
                "session_id", result.sessionId(),
                "added_duration", result.addedDuration(),
                "active_course_id", result.activeCourseId(),
                "active_hour_id", result.activeHourId(),
                "instance_id", instanceId);
    }

    private void sleep(long holdMillis) {
        if (holdMillis == 0) {
            return;
        }
        try {
            Thread.sleep(holdMillis);
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("验收锁探针被中断", exception);
        }
    }

    public record LockRequest(String subject, Long holdMillis) {}

    public record LeaseHeartbeatRequest(
            Integer userId,
            Integer courseId,
            Integer hourId,
            String sessionId,
            Integer hourDuration) {

        private boolean isValid() {
            return userId != null && courseId != null && hourId != null && hourDuration != null;
        }
    }

    public record LeaseStopRequest(
            Integer userId, Integer courseId, Integer hourId, String sessionId) {

        private boolean isValid() {
            return userId != null
                    && courseId != null
                    && hourId != null
                    && sessionId != null
                    && !sessionId.isBlank();
        }
    }
}
