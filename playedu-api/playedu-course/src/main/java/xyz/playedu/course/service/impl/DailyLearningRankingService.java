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
package xyz.playedu.course.service.impl;

import java.sql.Date;
import java.time.Clock;
import java.time.Duration;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Service;
import xyz.playedu.common.redis.RedisDistributedLock;
import xyz.playedu.common.redis.RedisKeyspace;
import xyz.playedu.course.domain.DailyLearningRankingEntry;
import xyz.playedu.course.domain.UserLearnDurationStats;
import xyz.playedu.course.mapper.UserLearnDurationStatsMapper;

/** Redis query projection for today's and yesterday's learning rankings. */
@Service
public class DailyLearningRankingService {

    public static final String REDIS_MODULE = "learning-ranking";
    public static final int TOP_LIMIT = 10;
    public static final Duration KEY_RETENTION = Duration.ofDays(7);
    private static final String READY_MARKER = "ready";
    private static final String READY_VALUE = "ready";
    private static final String EMPTY_VALUE = "empty";
    private static final RedisScript<Long> REBUILD_SCRIPT =
            new DefaultRedisScript<>(
                    """
                    local ttl = ARGV[#ARGV]
                    redis.call('DEL', KEYS[1])
                    local index = 1
                    while index < #ARGV do
                        redis.call('ZADD', KEYS[1], ARGV[index], ARGV[index + 1])
                        index = index + 2
                    end
                    if index > 1 then
                        if redis.call('EXPIRE', KEYS[1], ttl) == 0 then
                            return 0
                        end
                        redis.call('SET', KEYS[2], 'ready', 'EX', ttl)
                    else
                        redis.call('SET', KEYS[2], 'empty', 'EX', ttl)
                    end
                    return 1
                    """,
                    Long.class);
    private static final RedisScript<Long> RECONCILE_SCRIPT =
            new DefaultRedisScript<>(
                    """
                    local target = tonumber(ARGV[1])
                    local member = ARGV[2]
                    local ttl = ARGV[3]
                    local current = redis.call('ZSCORE', KEYS[1], member)
                    if current == false then
                        current = 0
                    else
                        current = tonumber(current)
                    end
                    if target == 0 then
                        redis.call('ZREM', KEYS[1], member)
                    elseif target ~= current then
                        redis.call('ZINCRBY', KEYS[1], target - current, member)
                    end
                    if target > 0 then
                        if redis.call('EXPIRE', KEYS[1], ttl) == 0 then
                            return 0
                        end
                        redis.call('SET', KEYS[2], 'ready', 'EX', ttl)
                    elseif redis.call('ZCARD', KEYS[1]) > 0 then
                        if redis.call('EXPIRE', KEYS[1], ttl) == 0 then
                            return 0
                        end
                        redis.call('SET', KEYS[2], 'ready', 'EX', ttl)
                    else
                        redis.call('SET', KEYS[2], 'empty', 'EX', ttl)
                    end
                    return 1
                    """,
                    Long.class);

    private final StringRedisTemplate redisTemplate;
    private final RedisKeyspace keyspace;
    private final UserLearnDurationStatsMapper statsMapper;
    private final RedisDistributedLock distributedLock;
    private final Clock clock;

    @Autowired
    public DailyLearningRankingService(
            StringRedisTemplate redisTemplate,
            RedisKeyspace keyspace,
            UserLearnDurationStatsMapper statsMapper,
            RedisDistributedLock distributedLock) {
        this(redisTemplate, keyspace, statsMapper, distributedLock, Clock.systemDefaultZone());
    }

    DailyLearningRankingService(
            StringRedisTemplate redisTemplate,
            RedisKeyspace keyspace,
            UserLearnDurationStatsMapper statsMapper,
            RedisDistributedLock distributedLock,
            Clock clock) {
        this.redisTemplate = redisTemplate;
        this.keyspace = keyspace;
        this.statsMapper = statsMapper;
        this.distributedLock = distributedLock;
        this.clock = clock;
    }

    public List<DailyLearningRankingEntry> todayTop10() {
        return top10For(currentDate());
    }

    public List<DailyLearningRankingEntry> yesterdayTop10() {
        return top10For(currentDate().minusDays(1));
    }

    /** Applies one committed MySQL increment to the daily Redis projection. */
    public void project(Integer userId, LocalDate learningDate, long duration) {
        if (duration <= 0) {
            return;
        }
        Objects.requireNonNull(userId, "userId must not be null");
        Objects.requireNonNull(learningDate, "learningDate must not be null");

        distributedLock.execute(
                REDIS_MODULE,
                learningDate.toString(),
                () -> {
                    reconcile(learningDate, userId, duration);
                    return null;
                });
    }

    /** Rebuilds both product-visible ranking buckets from the MySQL source of truth. */
    public void rebuildTodayAndYesterday() {
        LocalDate today = currentDate();
        rebuildForDate(today);
        rebuildForDate(today.minusDays(1));
    }

    /** Rebuilds only buckets whose Redis projection is missing. */
    public void rebuildMissing() {
        LocalDate today = currentDate();
        rebuildIfMissing(today);
        rebuildIfMissing(today.minusDays(1));
    }

    private List<DailyLearningRankingEntry> top10For(LocalDate learningDate) {
        String key = key(learningDate);
        if (!projectionReady(learningDate)) {
            rebuildForDate(learningDate);
        }

        Set<ZSetOperations.TypedTuple<String>> entries =
                redisTemplate.opsForZSet().reverseRangeWithScores(key, 0, TOP_LIMIT - 1);
        if (entries == null || entries.isEmpty()) {
            return new ArrayList<>();
        }

        List<DailyLearningRankingEntry> result = new ArrayList<>(entries.size());
        for (ZSetOperations.TypedTuple<String> entry : entries) {
            if (entry.getValue() == null || entry.getScore() == null) {
                continue;
            }
            DailyLearningRankingEntry rankingEntry = new DailyLearningRankingEntry();
            rankingEntry.setUserId(Integer.valueOf(entry.getValue()));
            rankingEntry.setDuration(Math.round(entry.getScore()));
            rankingEntry.setCreatedDate(Date.valueOf(learningDate));
            result.add(rankingEntry);
        }
        return result;
    }

    private void rebuildIfMissing(LocalDate learningDate) {
        if (!projectionReady(learningDate)) {
            rebuildForDate(learningDate);
        }
    }

    /** Removes a deleted student's member from the two dashboard-visible buckets. */
    public void removeUser(Integer userId) {
        Objects.requireNonNull(userId, "userId must not be null");
        LocalDate today = currentDate();
        removeUserFromDate(userId, today);
        removeUserFromDate(userId, today.minusDays(1));
    }

    private boolean projectionReady(LocalDate learningDate) {
        String marker = redisTemplate.opsForValue().get(readyKey(learningDate));
        if (EMPTY_VALUE.equals(marker)) {
            return !Boolean.TRUE.equals(redisTemplate.hasKey(key(learningDate)));
        }
        return READY_VALUE.equals(marker)
                && Boolean.TRUE.equals(redisTemplate.hasKey(key(learningDate)));
    }

    private void removeUserFromDate(Integer userId, LocalDate learningDate) {
        distributedLock.execute(
                REDIS_MODULE,
                learningDate.toString(),
                () -> {
                    if (!projectionReady(learningDate)) {
                        replaceFromAuthority(learningDate);
                    }
                    reconcileProjection(learningDate, userId, 0L);
                    return null;
                });
    }

    private void rebuildForDate(LocalDate learningDate) {
        distributedLock.execute(
                REDIS_MODULE,
                learningDate.toString(),
                () -> {
                    replaceFromAuthority(learningDate);
                    return null;
                });
    }

    private void replaceFromAuthority(LocalDate learningDate) {
        List<UserLearnDurationStats> records =
                statsMapper.rankingByDate(Date.valueOf(learningDate));
        if (records == null) {
            records = List.of();
        }
        Map<String, Double> entries = new LinkedHashMap<>();
        for (UserLearnDurationStats record : records) {
            if (record.getUserId() == null || record.getDuration() == null) {
                continue;
            }
            entries.merge(
                    record.getUserId().toString(), record.getDuration().doubleValue(), Double::sum);
        }

        List<String> scriptArguments = new ArrayList<>(entries.size() * 2 + 1);
        entries.forEach(
                (member, score) -> {
                    scriptArguments.add(score.toString());
                    scriptArguments.add(member);
                });
        scriptArguments.add(Long.toString(KEY_RETENTION.toSeconds()));
        Long rebuilt =
                redisTemplate.execute(
                        REBUILD_SCRIPT,
                        List.of(key(learningDate), readyKey(learningDate)),
                        scriptArguments.toArray());
        if (rebuilt == null || rebuilt <= 0) {
            throw new IllegalStateException("Could not rebuild the learning ranking projection");
        }
    }

    private void reconcile(LocalDate learningDate, Integer userId, long duration) {
        // The event delta is only the trigger; the source total prevents a rebuild race
        // from applying the same committed increment twice.
        if (!projectionReady(learningDate)) {
            replaceFromAuthority(learningDate);
        }
        Long authoritativeDuration =
                statsMapper.durationByUserAndDate(userId, Date.valueOf(learningDate));
        long targetScore = authoritativeDuration == null ? 0L : authoritativeDuration;
        reconcileProjection(learningDate, userId, targetScore);
    }

    private void reconcileProjection(LocalDate learningDate, Integer userId, long targetScore) {
        Long reconciled =
                redisTemplate.execute(
                        RECONCILE_SCRIPT,
                        List.of(key(learningDate), readyKey(learningDate)),
                        Long.toString(targetScore),
                        userId.toString(),
                        Long.toString(KEY_RETENTION.toSeconds()));
        if (reconciled == null || reconciled <= 0) {
            throw new IllegalStateException("Could not update the learning ranking projection");
        }
    }

    private String key(LocalDate learningDate) {
        return keyspace.key(REDIS_MODULE, learningDate.toString());
    }

    private String readyKey(LocalDate learningDate) {
        return keyspace.key(REDIS_MODULE, learningDate.toString(), READY_MARKER);
    }

    private LocalDate currentDate() {
        return LocalDate.now(clock);
    }
}
