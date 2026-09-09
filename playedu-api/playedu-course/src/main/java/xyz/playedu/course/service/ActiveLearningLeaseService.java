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
package xyz.playedu.course.service;

import java.time.Clock;
import java.time.Duration;
import java.util.List;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import xyz.playedu.common.redis.LearningLeaseUnavailableException;
import xyz.playedu.common.redis.RedisKeyspace;

/** Coordinates the one active course-hour that can accrue learning time for a learner. */
@Service
public class ActiveLearningLeaseService {

    static final Duration LEASE_TTL = Duration.ofSeconds(30);
    static final Duration HEARTBEAT_INTERVAL = Duration.ofSeconds(10);
    static final Duration MAX_CONTINUOUS_INTERVAL = Duration.ofSeconds(20);

    private static final DefaultRedisScript<List> HEARTBEAT_SCRIPT =
            new DefaultRedisScript<>(
                    """
                    local key = KEYS[1]
                    local session = ARGV[1]
                    local course = ARGV[2]
                    local hour = ARGV[3]
                    local now = tonumber(ARGV[4])
                    local ttl = tonumber(ARGV[5])
                    local minimum = tonumber(ARGV[6])
                    local maximum = tonumber(ARGV[7])
                    if redis.call('EXISTS', key) == 0 then
                      redis.call('HSET', key, 'session', session, 'course', course, 'hour', hour, 'last', now)
                      redis.call('PEXPIRE', key, ttl)
                      return {'CREATED', session, 0, course, hour, now}
                    end
                    local activeSession = redis.call('HGET', key, 'session')
                    local activeCourse = redis.call('HGET', key, 'course')
                    local activeHour = redis.call('HGET', key, 'hour')
                    if activeCourse ~= course or activeHour ~= hour then
                      return {'CONFLICT', activeSession, 0, activeCourse, activeHour, 0}
                    end
                    if session == '' or activeSession ~= session then
                      return {'INVALID_SESSION', activeSession, 0, activeCourse, activeHour, 0}
                    end
                    local previous = tonumber(redis.call('HGET', key, 'last'))
                    local elapsed = now - previous
                    if elapsed < minimum then
                      redis.call('PEXPIRE', key, ttl)
                      return {'TOO_EARLY', activeSession, 0, activeCourse, activeHour, previous}
                    end
                    if elapsed > maximum then
                      redis.call('HSET', key, 'last', now)
                      redis.call('PEXPIRE', key, ttl)
                      return {'BASELINE_RESET', activeSession, 0, activeCourse, activeHour, previous}
                    end
                    redis.call('HSET', key, 'last', now)
                    redis.call('PEXPIRE', key, ttl)
                    return {'CONTINUED', activeSession, math.floor(elapsed / 1000), activeCourse, activeHour, previous}
                    """,
                    List.class);

    private static final DefaultRedisScript<List> STOP_SCRIPT =
            new DefaultRedisScript<>(
                    """
                    local key = KEYS[1]
                    local session = ARGV[1]
                    local course = ARGV[2]
                    local hour = ARGV[3]
                    if redis.call('EXISTS', key) == 0 then
                      return {'NOT_ACTIVE'}
                    end
                    if redis.call('HGET', key, 'session') ~= session then
                      return {'INVALID_SESSION'}
                    end
                    if redis.call('HGET', key, 'course') ~= course or redis.call('HGET', key, 'hour') ~= hour then
                      return {'CONFLICT'}
                    end
                    redis.call('DEL', key)
                    return {'STOPPED'}
                    """,
                    List.class);

    private final StringRedisTemplate redisTemplate;
    private final RedisKeyspace keyspace;
    private final LearningFactPersistenceService learningFactPersistenceService;
    private final Clock clock;
    private final Duration leaseTtl;
    private final Duration heartbeatInterval;
    private final Duration maxContinuousInterval;

    @Autowired
    public ActiveLearningLeaseService(
            StringRedisTemplate redisTemplate,
            RedisKeyspace keyspace,
            LearningFactPersistenceService learningFactPersistenceService) {
        this(
                redisTemplate,
                keyspace,
                learningFactPersistenceService,
                Clock.systemUTC(),
                LEASE_TTL,
                HEARTBEAT_INTERVAL,
                MAX_CONTINUOUS_INTERVAL);
    }

    public ActiveLearningLeaseService(
            StringRedisTemplate redisTemplate,
            RedisKeyspace keyspace,
            LearningFactPersistenceService learningFactPersistenceService,
            Clock clock,
            Duration leaseTtl,
            Duration heartbeatInterval,
            Duration maxContinuousInterval) {
        this.redisTemplate = redisTemplate;
        this.keyspace = keyspace;
        this.learningFactPersistenceService = learningFactPersistenceService;
        this.clock = clock;
        this.leaseTtl = leaseTtl;
        this.heartbeatInterval = heartbeatInterval;
        this.maxContinuousInterval = maxContinuousInterval;
    }

    public HeartbeatResult heartbeat(
            Integer userId,
            Integer courseId,
            Integer hourId,
            String suppliedSessionId,
            Integer hourDuration) {
        String sessionId =
                StringUtils.hasText(suppliedSessionId)
                        ? suppliedSessionId
                        : UUID.randomUUID().toString();
        long now = clock.millis();
        List<?> response =
                execute(
                        HEARTBEAT_SCRIPT,
                        List.of(leaseKey(userId)),
                        sessionId,
                        courseId.toString(),
                        hourId.toString(),
                        Long.toString(now),
                        Long.toString(leaseTtl.toMillis()),
                        Long.toString(heartbeatInterval.toMillis()),
                        Long.toString(maxContinuousInterval.toMillis()));
        HeartbeatResult result = HeartbeatResult.from(response);
        if (result.outcome() == Outcome.CONTINUED && result.addedDuration() > 0) {
            learningFactPersistenceService.recordIncrement(
                    userId, courseId, hourId, result.addedDuration(), hourDuration);
        }
        return result;
    }

    public Outcome stop(Integer userId, Integer courseId, Integer hourId, String sessionId) {
        List<?> response =
                execute(
                        STOP_SCRIPT,
                        List.of(leaseKey(userId)),
                        sessionId,
                        courseId.toString(),
                        hourId.toString());
        return Outcome.valueOf(response.get(0).toString());
    }

    private List<?> execute(
            DefaultRedisScript<List> script, List<String> keys, String... arguments) {
        try {
            List<?> response = redisTemplate.execute(script, keys, (Object[]) arguments);
            if (response == null || response.isEmpty()) {
                throw new IllegalStateException("Redis learning lease script returned no result");
            }
            return response;
        } catch (DataAccessException | IllegalStateException exception) {
            throw new LearningLeaseUnavailableException("学习心跳服务暂不可用，请稍后重试", exception);
        }
    }

    private String leaseKey(Integer userId) {
        return keyspace.key("learning-lease", userId.toString());
    }

    public enum Outcome {
        CREATED,
        CONTINUED,
        TOO_EARLY,
        BASELINE_RESET,
        CONFLICT,
        INVALID_SESSION,
        STOPPED,
        NOT_ACTIVE
    }

    public record HeartbeatResult(
            Outcome outcome,
            String sessionId,
            int addedDuration,
            Integer activeCourseId,
            Integer activeHourId) {

        private static HeartbeatResult from(List<?> response) {
            return new HeartbeatResult(
                    Outcome.valueOf(response.get(0).toString()),
                    response.get(1).toString(),
                    Integer.parseInt(response.get(2).toString()),
                    Integer.valueOf(response.get(3).toString()),
                    Integer.valueOf(response.get(4).toString()));
        }
    }
}
