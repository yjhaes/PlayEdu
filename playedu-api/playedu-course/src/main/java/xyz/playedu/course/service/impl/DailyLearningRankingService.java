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
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.DefaultTypedTuple;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;
import xyz.playedu.common.redis.RedisDistributedLock;
import xyz.playedu.common.redis.RedisKeyspace;
import xyz.playedu.course.domain.UserLearnDurationStats;
import xyz.playedu.course.mapper.UserLearnDurationStatsMapper;

/** Redis query projection for today's and yesterday's learning rankings. */
@Service
public class DailyLearningRankingService {

    public static final String REDIS_MODULE = "learning-ranking";
    public static final int TOP_LIMIT = 10;
    public static final Duration KEY_RETENTION = Duration.ofDays(7);

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

    public List<UserLearnDurationStats> todayTop10() {
        return top10For(currentDate());
    }

    public List<UserLearnDurationStats> yesterdayTop10() {
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

    private List<UserLearnDurationStats> top10For(LocalDate learningDate) {
        String key = key(learningDate);
        if (!Boolean.TRUE.equals(redisTemplate.hasKey(key))) {
            rebuildForDate(learningDate);
        }

        Set<ZSetOperations.TypedTuple<String>> entries =
                redisTemplate.opsForZSet().reverseRangeWithScores(key, 0, TOP_LIMIT - 1);
        if (entries == null || entries.isEmpty()) {
            return new ArrayList<>();
        }

        List<UserLearnDurationStats> result = new ArrayList<>(entries.size());
        for (ZSetOperations.TypedTuple<String> entry : entries) {
            if (entry.getValue() == null || entry.getScore() == null) {
                continue;
            }
            UserLearnDurationStats stats = new UserLearnDurationStats();
            stats.setUserId(Integer.valueOf(entry.getValue()));
            stats.setDuration(Math.round(entry.getScore()));
            stats.setCreatedDate(Date.valueOf(learningDate));
            result.add(stats);
        }
        return result;
    }

    private void rebuildIfMissing(LocalDate learningDate) {
        if (!Boolean.TRUE.equals(redisTemplate.hasKey(key(learningDate)))) {
            rebuildForDate(learningDate);
        }
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
        Map<String, Double> entries = new LinkedHashMap<>();
        for (UserLearnDurationStats record : records) {
            if (record.getUserId() == null || record.getDuration() == null) {
                continue;
            }
            entries.merge(
                    record.getUserId().toString(), record.getDuration().doubleValue(), Double::sum);
        }

        String key = key(learningDate);
        redisTemplate.delete(key);
        if (entries.isEmpty()) {
            return;
        }

        Set<ZSetOperations.TypedTuple<String>> redisEntries = new LinkedHashSet<>();
        entries.forEach(
                (member, score) -> redisEntries.add(new DefaultTypedTuple<>(member, score)));
        Long added = redisTemplate.opsForZSet().add(key, redisEntries);
        if (added == null || added <= 0) {
            throw new IllegalStateException("Could not rebuild the learning ranking projection");
        }
        Boolean expired = redisTemplate.expire(key, KEY_RETENTION);
        if (!Boolean.TRUE.equals(expired)) {
            throw new IllegalStateException("Could not retain the learning ranking projection");
        }
    }

    private void reconcile(LocalDate learningDate, Integer userId, long duration) {
        // The event delta is only the trigger; the source total prevents a rebuild race
        // from applying the same committed increment twice.
        String key = key(learningDate);
        String member = userId.toString();
        Long authoritativeDuration =
                statsMapper.durationByUserAndDate(userId, Date.valueOf(learningDate));
        long targetScore = authoritativeDuration == null ? 0L : authoritativeDuration;
        Double projectedScore = redisTemplate.opsForZSet().score(key, member);
        long currentScore = projectedScore == null ? 0L : Math.round(projectedScore);
        long correction = targetScore - currentScore;

        if (correction > 0) {
            Double newScore = redisTemplate.opsForZSet().incrementScore(key, member, correction);
            if (newScore == null) {
                throw new IllegalStateException("Could not update the learning ranking projection");
            }
        } else if (correction < 0) {
            if (targetScore == 0) {
                redisTemplate.opsForZSet().remove(key, member);
            } else {
                redisTemplate.opsForZSet().add(key, member, targetScore);
            }
        }

        if (targetScore > 0) {
            Boolean expired = redisTemplate.expire(key, KEY_RETENTION);
            if (!Boolean.TRUE.equals(expired)) {
                throw new IllegalStateException("Could not retain the learning ranking projection");
            }
        }
    }

    private String key(LocalDate learningDate) {
        return keyspace.key(REDIS_MODULE, learningDate.toString());
    }

    private LocalDate currentDate() {
        return LocalDate.now(clock);
    }
}
