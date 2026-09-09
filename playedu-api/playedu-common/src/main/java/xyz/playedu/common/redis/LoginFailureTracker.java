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
import java.util.List;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Component;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;

/** Tracks failed login credentials in Redis without sharing request-rate-limit semantics. */
@Component
public class LoginFailureTracker {

    public static final int MAX_FAILURES = 10;

    private static final String UNAVAILABLE_MESSAGE = "登录保护服务暂不可用，请稍后重试";
    private static final DefaultRedisScript<String> RECORD_FAILURE =
            new DefaultRedisScript<>(
                    "local count = tonumber(redis.call('GET', KEYS[1]) or '0')\n"
                        + "if count >= tonumber(ARGV[2]) then\n"
                        + "  return tostring(count) .. ':' .. tostring(redis.call('PTTL',"
                        + " KEYS[1]))\n"
                        + "end\n"
                        + "count = redis.call('INCR', KEYS[1])\n"
                        + "if count == 1 then redis.call('EXPIRE', KEYS[1], ARGV[1]) end\n"
                        + "return tostring(count) .. ':' .. tostring(redis.call('PTTL', KEYS[1]))",
                    String.class);
    private static final DefaultRedisScript<String> STATUS =
            new DefaultRedisScript<>(
                    "local count = redis.call('GET', KEYS[1])\n"
                            + "if not count then return '0:0' end\n"
                            + "return count .. ':' .. tostring(redis.call('PTTL', KEYS[1]))",
                    String.class);
    private static final DefaultRedisScript<String> RESET =
            new DefaultRedisScript<>(
                    "local count = tonumber(redis.call('GET', KEYS[1]) or '0')\n"
                            + "if count >= tonumber(ARGV[1]) then\n"
                            + "  return tostring(count) .. ':' .. tostring(redis.call('PTTL',"
                            + " KEYS[1]))\n"
                            + "end\n"
                            + "redis.call('DEL', KEYS[1])\n"
                            + "return '0:0'",
                    String.class);

    private final StringRedisTemplate redisTemplate;
    private final RedisKeyspace keyspace;

    public LoginFailureTracker(StringRedisTemplate redisTemplate, RedisKeyspace keyspace) {
        this.redisTemplate = redisTemplate;
        this.keyspace = keyspace;
    }

    public void assertNotLocked(LoginType loginType, String subject) {
        assertUnlocked(status(loginType, subject));
    }

    public LoginFailureStatus recordFailure(LoginType loginType, String subject) {
        return execute(
                RECORD_FAILURE,
                key(loginType, subject),
                Long.toString(loginType.lockWindowSeconds()),
                Integer.toString(MAX_FAILURES));
    }

    public void reset(LoginType loginType, String subject) {
        assertUnlocked(execute(RESET, key(loginType, subject), Integer.toString(MAX_FAILURES)));
    }

    public LoginFailureStatus status(LoginType loginType, String subject) {
        return execute(STATUS, key(loginType, subject));
    }

    private void assertUnlocked(LoginFailureStatus status) {
        if (status.failureCount() >= MAX_FAILURES) {
            throw new LoginFailureLimitException(
                    "您的账号已被锁定，请" + waitingTime(status.remainingSeconds()) + "后重试");
        }
    }

    private LoginFailureStatus execute(
            DefaultRedisScript<String> script, String key, String... arguments) {
        try {
            String result = redisTemplate.execute(script, List.of(key), (Object[]) arguments);
            return parseStatus(result);
        } catch (RuntimeException exception) {
            throw new LoginFailureTrackingUnavailableException(UNAVAILABLE_MESSAGE, exception);
        }
    }

    private String key(LoginType loginType, String subject) {
        if (!StringUtils.hasText(subject)) {
            throw new IllegalArgumentException("Login failure subject must not be blank");
        }
        String hash = DigestUtils.md5DigestAsHex(subject.getBytes(StandardCharsets.UTF_8));
        return keyspace.key("login-failure", loginType.keySegment(), hash);
    }

    private LoginFailureStatus parseStatus(String response) {
        if (response == null) {
            throw new IllegalStateException("Redis login failure script returned no status");
        }
        String[] parts = response.split(":", -1);
        if (parts.length != 2) {
            throw new IllegalStateException(
                    "Redis login failure script returned an invalid status");
        }
        try {
            long failureCount = Math.max(0, Long.parseLong(parts[0]));
            long remainingMilliseconds = Long.parseLong(parts[1]);
            return new LoginFailureStatus(failureCount, remainingSeconds(remainingMilliseconds));
        } catch (NumberFormatException exception) {
            throw new IllegalStateException(
                    "Redis login failure script returned a non-numeric status", exception);
        }
    }

    private long remainingSeconds(long milliseconds) {
        if (milliseconds <= 0) {
            return 0;
        }
        return (milliseconds + 999) / 1_000;
    }

    private String waitingTime(long seconds) {
        long minutes = seconds / 60;
        long remainingSeconds = seconds % 60;
        if (minutes == 0) {
            return remainingSeconds + "秒";
        }
        if (remainingSeconds == 0) {
            return minutes + "分钟";
        }
        return minutes + "分钟" + remainingSeconds + "秒";
    }

    public enum LoginType {
        LEARNER("learner", 600),
        ADMINISTRATOR("administrator", 3_600);

        private final String keySegment;
        private final long lockWindowSeconds;

        LoginType(String keySegment, long lockWindowSeconds) {
            this.keySegment = keySegment;
            this.lockWindowSeconds = lockWindowSeconds;
        }

        private String keySegment() {
            return keySegment;
        }

        private long lockWindowSeconds() {
            return lockWindowSeconds;
        }
    }

    public record LoginFailureStatus(long failureCount, long remainingSeconds) {}
}
