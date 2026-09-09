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

import static org.assertj.core.api.Assertions.assertThat;

import java.sql.Date;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.apache.ibatis.annotations.Mapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;
import xyz.playedu.common.redis.RedisDistributedLock;
import xyz.playedu.common.redis.RedisKeyspace;
import xyz.playedu.common.redis.RedisRuntimeConfiguration;
import xyz.playedu.course.domain.UserLearnDurationStats;
import xyz.playedu.course.service.impl.DailyLearningRankingService;

@SpringBootTest(classes = DailyLearningRankingServiceIntegrationTest.TestApplication.class)
@Testcontainers(disabledWithoutDocker = true)
class DailyLearningRankingServiceIntegrationTest {

    @Container static final MySQLContainer<?> MYSQL = new MySQLContainer<>("mysql:8.0");

    @Container
    static final GenericContainer<?> REDIS =
            new GenericContainer<>(DockerImageName.parse("redis:7.4-alpine"))
                    .withExposedPorts(6379);

    @Autowired private JdbcTemplate jdbcTemplate;

    @Autowired private StringRedisTemplate redisTemplate;

    @Autowired private DailyLearningRankingService rankingService;

    @Autowired private RedisKeyspace keyspace;

    @DynamicPropertySource
    static void databaseProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", MYSQL::getJdbcUrl);
        registry.add("spring.datasource.username", MYSQL::getUsername);
        registry.add("spring.datasource.password", MYSQL::getPassword);
        registry.add("spring.datasource.driver-class-name", MYSQL::getDriverClassName);
        registry.add("mybatis.mapper-locations", () -> "classpath:mapper/*.xml");
        registry.add("spring.data.redis.host", REDIS::getHost);
        registry.add("spring.data.redis.port", () -> REDIS.getMappedPort(6379));
        registry.add("spring.data.redis.timeout", () -> "2s");
        registry.add("playedu.redis.key-prefix", () -> "playedu:test");
    }

    @BeforeEach
    void setUp() {
        redisTemplate.getConnectionFactory().getConnection().serverCommands().flushDb();
        jdbcTemplate.execute("DROP TABLE IF EXISTS user_learn_duration_stats");
        jdbcTemplate.execute(
                """
                CREATE TABLE user_learn_duration_stats (
                    id int unsigned NOT NULL AUTO_INCREMENT,
                    user_id int NOT NULL,
                    duration bigint NOT NULL DEFAULT 0,
                    created_date date NOT NULL,
                    PRIMARY KEY (id),
                    UNIQUE KEY uk_user_learn_duration_stats_user_date (user_id, created_date)
                ) ENGINE=InnoDB
                """);
    }

    @Test
    void projectsTodayAndYesterdayIntoIndependentTopTenZSets() {
        LocalDate today = LocalDate.now(ZoneId.systemDefault());
        LocalDate yesterday = today.minusDays(1);

        for (int userId = 1; userId <= 12; userId++) {
            insertAuthoritativeDuration(userId, userId * 1_000L, today);
            rankingService.project(userId, today, userId * 1_000L);
        }
        insertAuthoritativeDuration(13, 13_000L, today);
        rankingService.project(13, today, 13_000L);
        insertAuthoritativeDuration(14, 99_000L, yesterday);
        rankingService.project(14, yesterday, 99_000L);

        List<UserLearnDurationStats> todayTop = rankingService.todayTop10();
        List<UserLearnDurationStats> yesterdayTop = rankingService.yesterdayTop10();

        assertThat(todayTop).hasSize(10);
        assertThat(todayTop.get(0).getDuration()).isEqualTo(13_000L);
        assertThat(todayTop).extracting(UserLearnDurationStats::getUserId).doesNotContain(1, 2);
        assertThat(todayTop)
                .allSatisfy(
                        record ->
                                assertThat(record.getCreatedDate()).isEqualTo(Date.valueOf(today)));

        assertThat(yesterdayTop)
                .singleElement()
                .satisfies(
                        record -> {
                            assertThat(record.getUserId()).isEqualTo(14);
                            assertThat(record.getDuration()).isEqualTo(99_000L);
                            assertThat(record.getCreatedDate()).isEqualTo(Date.valueOf(yesterday));
                        });
    }

    @Test
    void appliesRepeatedIncrementsAndKeepsEqualScoresInTheRanking() {
        LocalDate today = LocalDate.now(ZoneId.systemDefault());

        insertAuthoritativeDuration(7, 750L, today);
        insertAuthoritativeDuration(8, 750L, today);
        insertAuthoritativeDuration(9, 750L, today);
        rankingService.project(7, today, 500L);
        rankingService.project(7, today, 250L);
        rankingService.project(8, today, 750L);
        rankingService.project(9, today, 750L);

        List<UserLearnDurationStats> top = rankingService.todayTop10();

        assertThat(top).extracting(UserLearnDurationStats::getUserId).contains(7, 8, 9);
        assertThat(
                        top.stream()
                                .filter(record -> record.getUserId().equals(7))
                                .findFirst()
                                .orElseThrow()
                                .getDuration())
                .isEqualTo(750L);
        assertThat(
                        top.stream()
                                .filter(record -> record.getUserId().equals(8))
                                .findFirst()
                                .orElseThrow()
                                .getDuration())
                .isEqualTo(750L);
        assertThat(
                        top.stream()
                                .filter(record -> record.getUserId().equals(9))
                                .findFirst()
                                .orElseThrow()
                                .getDuration())
                .isEqualTo(750L);
    }

    @Test
    void doesNotDoubleCountAnEventAfterTheProjectionWasRebuilt() {
        LocalDate today = LocalDate.now(ZoneId.systemDefault());
        insertAuthoritativeDuration(7, 1_000L, today);

        assertThat(rankingService.todayTop10())
                .singleElement()
                .extracting(UserLearnDurationStats::getDuration)
                .isEqualTo(1_000L);

        rankingService.project(7, today, 1_000L);

        assertThat(rankingService.todayTop10())
                .singleElement()
                .extracting(UserLearnDurationStats::getDuration)
                .isEqualTo(1_000L);
    }

    @Test
    void initializesTheWholeMissingProjectionBeforeApplyingTheFirstEvent() {
        LocalDate today = LocalDate.now(ZoneId.systemDefault());
        insertAuthoritativeDuration(7, 1_000L, today);
        insertAuthoritativeDuration(8, 2_000L, today);

        rankingService.project(7, today, 1_000L);

        assertThat(rankingService.todayTop10())
                .extracting(UserLearnDurationStats::getUserId)
                .containsExactly(8, 7);
        assertThat(rankingService.todayTop10())
                .extracting(UserLearnDurationStats::getDuration)
                .containsExactly(2_000L, 1_000L);
    }

    @Test
    void removesDeletedStudentsFromVisibleRankings() {
        LocalDate today = LocalDate.now(ZoneId.systemDefault());
        insertAuthoritativeDuration(7, 1_000L, today);
        insertAuthoritativeDuration(8, 2_000L, today);
        rankingService.rebuildTodayAndYesterday();

        rankingService.removeUser(8);

        assertThat(rankingService.todayTop10())
                .extracting(UserLearnDurationStats::getUserId)
                .containsExactly(7);
    }

    @Test
    void rebuildsBothDaysAfterRedisIsClearedAndIsIdempotent() {
        LocalDate today = LocalDate.now(ZoneId.systemDefault());
        LocalDate yesterday = today.minusDays(1);
        insertAuthoritativeDuration(7, 4_000L, today);
        insertAuthoritativeDuration(8, 8_000L, today);
        insertAuthoritativeDuration(9, 2_000L, yesterday);

        rankingService.rebuildTodayAndYesterday();
        List<UserLearnDurationStats> firstToday = rankingService.todayTop10();
        List<UserLearnDurationStats> firstYesterday = rankingService.yesterdayTop10();

        redisTemplate.getConnectionFactory().getConnection().serverCommands().flushDb();
        assertThat(rankingService.todayTop10()).usingRecursiveComparison().isEqualTo(firstToday);
        assertThat(rankingService.yesterdayTop10())
                .usingRecursiveComparison()
                .isEqualTo(firstYesterday);

        redisTemplate.getConnectionFactory().getConnection().serverCommands().flushDb();
        rankingService.rebuildTodayAndYesterday();

        assertThat(rankingService.todayTop10()).usingRecursiveComparison().isEqualTo(firstToday);
        assertThat(rankingService.yesterdayTop10())
                .usingRecursiveComparison()
                .isEqualTo(firstYesterday);
        assertThat(redisTemplate.hasKey(keyspace.key("learning-ranking", today.toString())))
                .isTrue();
        assertThat(redisTemplate.hasKey(keyspace.key("learning-ranking", yesterday.toString())))
                .isTrue();
        assertThat(
                        redisTemplate.getExpire(
                                keyspace.key("learning-ranking", today.toString()),
                                TimeUnit.SECONDS))
                .isBetween(1L, DailyLearningRankingService.KEY_RETENTION.getSeconds());
        assertThat(
                        redisTemplate.getExpire(
                                keyspace.key("learning-ranking", yesterday.toString()),
                                TimeUnit.SECONDS))
                .isBetween(1L, DailyLearningRankingService.KEY_RETENTION.getSeconds());
    }

    private void insertAuthoritativeDuration(Integer userId, Long duration, LocalDate date) {
        jdbcTemplate.update(
                "INSERT INTO user_learn_duration_stats (user_id, duration, created_date) VALUES (?,"
                        + " ?, ?)",
                userId,
                duration,
                Date.valueOf(date));
    }

    @SpringBootConfiguration
    @EnableAutoConfiguration
    @MapperScan(basePackages = "xyz.playedu.course.mapper", annotationClass = Mapper.class)
    @Import({
        DailyLearningRankingService.class,
        RedisDistributedLock.class,
        RedisKeyspace.class,
        RedisRuntimeConfiguration.class
    })
    static class TestApplication {}
}
