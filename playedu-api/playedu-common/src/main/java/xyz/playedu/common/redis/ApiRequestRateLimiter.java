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
package xyz.playedu.common.redis;

import java.nio.charset.StandardCharsets;
import org.redisson.api.RRateLimiter;
import org.redisson.api.RateIntervalUnit;
import org.redisson.api.RateType;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;
import org.springframework.util.DigestUtils;

/** Shares API request quotas between application instances through a Redis token bucket. */
@Component
public class ApiRequestRateLimiter {

    private final RedisKeyspace keyspace;
    private final RedissonClient redisson;

    public ApiRequestRateLimiter(RedisKeyspace keyspace, RedissonClient redisson) {
        this.keyspace = keyspace;
        this.redisson = redisson;
    }

    public RateLimitDecision acquire(String clientId, long permitsPerWindow, long windowSeconds) {
        if (permitsPerWindow <= 0 || windowSeconds <= 0) {
            throw new IllegalArgumentException("Rate limit permits and window must be positive");
        }

        try {
            RRateLimiter limiter = redisson.getRateLimiter(keyspace.rateLimiter(subject(clientId)));
            limiter.trySetRate(
                    RateType.OVERALL, permitsPerWindow, windowSeconds, RateIntervalUnit.SECONDS);
            boolean allowed = limiter.tryAcquire();
            return new RateLimitDecision(allowed, Math.max(0, limiter.availablePermits()));
        } catch (RuntimeException exception) {
            throw new ApiRateLimitUnavailableException(
                    "API request rate limiting is unavailable because Redis cannot be reached",
                    exception);
        }
    }

    private String subject(String clientId) {
        return "api-" + DigestUtils.md5DigestAsHex(clientId.getBytes(StandardCharsets.UTF_8));
    }

    public record RateLimitDecision(boolean allowed, long remaining) {}
}
