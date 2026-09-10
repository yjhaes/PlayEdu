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
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.event.EventListener;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.support.TransactionTemplate;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;
import xyz.playedu.common.redis.RedisDistributedLock;
import xyz.playedu.common.redis.RedisKeyspace;
import xyz.playedu.common.redis.RedisLockException;
import xyz.playedu.common.redis.RedisRuntimeConfiguration;
import xyz.playedu.course.event.DailyLearningDurationConfirmedEvent;
import xyz.playedu.course.event.DailyLearningDurationEventPublisher;
import xyz.playedu.course.event.DailyLearningRankingEventListener;
import xyz.playedu.course.service.impl.DailyLearningRankingService;
import xyz.playedu.course.service.impl.UserCourseHourRecordServiceImpl;
import xyz.playedu.course.service.impl.UserCourseRecordServiceImpl;
import xyz.playedu.course.service.impl.UserLearnDurationRecordServiceImpl;
import xyz.playedu.course.service.impl.UserLearnDurationStatsServiceImpl;
import xyz.playedu.points.service.impl.PointBalanceServiceImpl;

@SpringBootTest(classes = LearningFactPersistenceServiceIntegrationTest.TestApplication.class)
@Testcontainers(disabledWithoutDocker = true)
class LearningFactPersistenceServiceIntegrationTest {

    @Container static final MySQLContainer<?> MYSQL = new MySQLContainer<>("mysql:8.0");

    @Container
    static final GenericContainer<?> REDIS =
            new GenericContainer<>(DockerImageName.parse("redis:7.4-alpine"))
                    .withExposedPorts(6379);

    @Autowired private JdbcTemplate jdbcTemplate;

    @Autowired private LearningFactPersistenceService learningFactPersistenceService;

    @Autowired private LearningDurationEventCollector eventCollector;

    @Autowired private DailyLearningRankingService rankingService;

    @Autowired private MeterRegistry meterRegistry;

    @Autowired private RedisDistributedLock distributedLock;

    @Autowired private TransactionTemplate transactionTemplate;

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
        eventCollector.clear();
        jdbcTemplate.execute("DROP TABLE IF EXISTS point_ledgers");
        jdbcTemplate.execute("DROP TABLE IF EXISTS point_codes");
        jdbcTemplate.execute("DROP TABLE IF EXISTS point_products");
        jdbcTemplate.execute("DROP TABLE IF EXISTS users");
        jdbcTemplate.execute("DROP TABLE IF EXISTS user_learn_duration_records");
        jdbcTemplate.execute("DROP TABLE IF EXISTS user_learn_duration_stats");
        jdbcTemplate.execute("DROP TABLE IF EXISTS user_course_hour_records");
        jdbcTemplate.execute("DROP TABLE IF EXISTS user_course_records");
        jdbcTemplate.execute(
                """
                CREATE TABLE users (
                    id int NOT NULL,
                    credit1 int NOT NULL DEFAULT 0,
                    updated_at timestamp NULL DEFAULT NULL,
                    PRIMARY KEY (id)
                ) ENGINE=InnoDB
                """);
        jdbcTemplate.execute("INSERT INTO users (id, credit1) VALUES (7, 0)");
        jdbcTemplate.execute(
                """
                CREATE TABLE point_products (
                    id int unsigned NOT NULL AUTO_INCREMENT,
                    name varchar(191) NOT NULL,
                    points_price int unsigned NOT NULL,
                    status varchar(20) NOT NULL,
                    created_at datetime NOT NULL,
                    updated_at datetime NOT NULL,
                    PRIMARY KEY (id)
                ) ENGINE=InnoDB
                """);
        jdbcTemplate.execute(
                """
                CREATE TABLE point_codes (
                    id int unsigned NOT NULL AUTO_INCREMENT,
                    product_id int unsigned NOT NULL,
                    code_ciphertext text NOT NULL,
                    code_digest varchar(191) NOT NULL,
                    status varchar(20) NOT NULL,
                    delivered_at datetime NULL,
                    created_at datetime NOT NULL,
                    updated_at datetime NOT NULL,
                    PRIMARY KEY (id),
                    UNIQUE KEY uk_point_codes_code_digest (code_digest)
                ) ENGINE=InnoDB
                """);
        jdbcTemplate.update(
                """
                INSERT INTO point_products (name, points_price, status, created_at, updated_at)
                VALUES (?, ?, ?, NOW(), NOW())
                """,
                "售罄商品",
                10,
                "ON_SALE");
        jdbcTemplate.update(
                """
                INSERT INTO point_products (name, points_price, status, created_at, updated_at)
                VALUES (?, ?, ?, NOW(), NOW())
                """,
                "下架商品",
                10,
                "OFF_SALE");
        jdbcTemplate.execute(
                """
                CREATE TABLE point_ledgers (
                    id int unsigned NOT NULL AUTO_INCREMENT,
                    user_id int NOT NULL,
                    delta int NOT NULL,
                    balance_after int NOT NULL,
                    type varchar(64) NOT NULL,
                    source_key varchar(191) NOT NULL,
                    reason varchar(255) DEFAULT NULL,
                    operator_admin_id int DEFAULT NULL,
                    created_at timestamp NULL DEFAULT NULL,
                    PRIMARY KEY (id),
                    UNIQUE KEY uk_point_ledgers_source_key (source_key)
                ) ENGINE=InnoDB
                """);
        jdbcTemplate.execute(
                """
                CREATE TABLE user_course_hour_records (
                    id int unsigned NOT NULL AUTO_INCREMENT,
                    user_id int NOT NULL,
                    course_id int NOT NULL,
                    hour_id int NOT NULL,
                    total_duration int NOT NULL,
                    finished_duration int NOT NULL,
                    real_duration int DEFAULT NULL,
                    is_finished tinyint NOT NULL,
                    finished_at timestamp NULL DEFAULT NULL,
                    created_at timestamp NULL DEFAULT NULL,
                    updated_at timestamp NULL DEFAULT NULL,
                    PRIMARY KEY (id)
                ) ENGINE=InnoDB
                """);
        jdbcTemplate.execute(
                """
                CREATE TABLE user_course_records (
                    id int unsigned NOT NULL AUTO_INCREMENT,
                    user_id int NOT NULL,
                    course_id int NOT NULL,
                    hour_count int NOT NULL,
                    finished_count int NOT NULL,
                    progress int NOT NULL,
                    is_finished tinyint NOT NULL,
                    finished_at timestamp NULL DEFAULT NULL,
                    created_at timestamp NULL DEFAULT NULL,
                    updated_at timestamp NULL DEFAULT NULL,
                    PRIMARY KEY (id)
                ) ENGINE=InnoDB
                """);
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
        jdbcTemplate.execute(
                """
                CREATE TABLE user_learn_duration_records (
                    id bigint unsigned NOT NULL AUTO_INCREMENT,
                    user_id int NOT NULL,
                    created_date date NOT NULL,
                    duration int unsigned NOT NULL,
                    start_at timestamp NULL DEFAULT NULL,
                    end_at timestamp NULL DEFAULT NULL,
                    from_id varchar(64) NOT NULL,
                    from_scene varchar(20) NOT NULL,
                    PRIMARY KEY (id)
                ) ENGINE=InnoDB
                """);
    }

    @AfterEach
    void tearDown() {
        reset(rankingService);
    }

    @Test
    void commitsProgressAndDurationTogetherThenPublishesTheConfirmedIncrement() {
        learningFactPersistenceService.record(7, 8, 9, 10, 100);

        assertThat(
                        jdbcTemplate.queryForObject(
                                "SELECT COUNT(*) FROM user_course_hour_records", Integer.class))
                .isEqualTo(1);
        assertThat(
                        jdbcTemplate.queryForObject(
                                "SELECT COUNT(*) FROM user_course_records", Integer.class))
                .isEqualTo(1);
        assertThat(
                        jdbcTemplate.queryForObject(
                                "SELECT duration FROM user_learn_duration_stats WHERE user_id = 7",
                                Long.class))
                .isEqualTo(10_000L);
        assertThat(
                        jdbcTemplate.queryForObject(
                                "SELECT COUNT(*) FROM user_learn_duration_records", Integer.class))
                .isEqualTo(1);
        assertThat(eventCollector.events)
                .singleElement()
                .satisfies(
                        event -> {
                            assertThat(event.getUserId()).isEqualTo(7);
                            assertThat(event.getDuration()).isEqualTo(10_000L);
                        });
    }

    @Test
    void keepsCommittedMySqlFactsWhenAsyncRankingProjectionFails() throws Exception {
        reset(rankingService);
        CountDownLatch projectionFailed = new CountDownLatch(1);
        doAnswer(
                        invocation -> {
                            projectionFailed.countDown();
                            throw new IllegalStateException("Redis unavailable");
                        })
                .when(rankingService)
                .project(anyInt(), any(LocalDate.class), anyLong());

        learningFactPersistenceService.record(7, 8, 9, 10, 100);

        assertThat(projectionFailed.await(5, TimeUnit.SECONDS)).isTrue();
        assertThat(
                        jdbcTemplate.queryForObject(
                                "SELECT duration FROM user_learn_duration_stats WHERE user_id = 7",
                                Long.class))
                .isEqualTo(10_000L);
        assertThat(
                        meterRegistry
                                .get("playedu.learning.ranking.projection.failures")
                                .counter()
                                .count())
                .isEqualTo(1.0);
    }

    @Test
    void awardsPointsWithoutRedeemableProductInventoryOnlyForTheFirstCourseCompletion() {
        assertThat(
                        jdbcTemplate.queryForObject(
                                "SELECT COUNT(*) FROM point_codes WHERE status = 'AVAILABLE'",
                                Integer.class))
                .isZero();
        assertThat(learningFactPersistenceService.record(7, 8, 9, 100, 100).earnedPoints())
                .isEqualTo(10);
        assertThat(queryCredit1()).isEqualTo(10);
        assertThat(pointLedgerCount()).isEqualTo(1);

        assertThat(learningFactPersistenceService.record(7, 8, 9, 100, 100).earnedPoints())
                .isZero();

        jdbcTemplate.execute("DELETE FROM user_course_hour_records");
        jdbcTemplate.execute("DELETE FROM user_course_records");

        assertThat(learningFactPersistenceService.record(7, 8, 9, 100, 100).earnedPoints())
                .isZero();
        assertThat(queryCredit1()).isEqualTo(10);
        assertThat(pointLedgerCount()).isEqualTo(1);
    }

    @Test
    void rollsBackEveryAuthoritativeWriteAndDoesNotPublishWhenTheDurationWriteFails() {
        jdbcTemplate.execute("DROP TABLE user_learn_duration_stats");

        assertThatThrownBy(() -> learningFactPersistenceService.record(7, 8, 9, 100, 100))
                .isInstanceOf(Exception.class);

        assertThat(
                        jdbcTemplate.queryForObject(
                                "SELECT COUNT(*) FROM user_course_hour_records", Integer.class))
                .isZero();
        assertThat(
                        jdbcTemplate.queryForObject(
                                "SELECT COUNT(*) FROM user_course_records", Integer.class))
                .isZero();
        assertThat(
                        jdbcTemplate.queryForObject(
                                "SELECT COUNT(*) FROM user_learn_duration_records", Integer.class))
                .isZero();
        assertThat(queryCredit1()).isZero();
        assertThat(pointLedgerCount()).isZero();
        assertThat(eventCollector.events).isEmpty();
    }

    @Test
    void serializesDuplicateConcurrentHeartbeatsBeforeWritingTheirFacts() throws Exception {
        ExecutorService executor = Executors.newFixedThreadPool(2);
        CountDownLatch ready = new CountDownLatch(2);
        CountDownLatch start = new CountDownLatch(1);
        try {
            List<Future<LearningFactPersistenceResult>> writes =
                    List.of(
                            executor.submit(
                                    () -> {
                                        ready.countDown();
                                        start.await();
                                        return learningFactPersistenceService.record(
                                                7, 8, 9, 100, 100);
                                    }),
                            executor.submit(
                                    () -> {
                                        ready.countDown();
                                        start.await();
                                        return learningFactPersistenceService.record(
                                                7, 8, 9, 100, 100);
                                    }));

            ready.await();
            start.countDown();
            assertThat(
                            writes.stream()
                                    .map(this::getResult)
                                    .mapToInt(LearningFactPersistenceResult::earnedPoints))
                    .containsExactlyInAnyOrder(0, 10);
        } finally {
            executor.shutdownNow();
        }

        assertThat(
                        jdbcTemplate.queryForObject(
                                "SELECT COUNT(*) FROM user_course_hour_records", Integer.class))
                .isEqualTo(1);
        assertThat(
                        jdbcTemplate.queryForObject(
                                "SELECT COUNT(*) FROM user_learn_duration_stats", Integer.class))
                .isEqualTo(1);
        assertThat(eventCollector.events).hasSize(1);
        assertThat(queryCredit1()).isEqualTo(10);
        assertThat(pointLedgerCount()).isEqualTo(1);
    }

    @Test
    void keepsTheLearningLockUntilTheSurroundingTransactionCompletes() {
        Boolean acquiredBeforeCommit =
                transactionTemplate.execute(
                        status -> {
                            learningFactPersistenceService.record(7, 8, 9, 10, 100);
                            return tryAcquireLearningLockFromAnotherThread();
                        });

        assertThat(acquiredBeforeCommit).isFalse();
        assertThat(tryAcquireLearningLockFromAnotherThread()).isTrue();
    }

    private boolean tryAcquireLearningLockFromAnotherThread() {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        try {
            return executor.submit(
                            () -> {
                                try {
                                    distributedLock.execute("learning-fact", "7", () -> true);
                                    return true;
                                } catch (RedisLockException exception) {
                                    return false;
                                }
                            })
                    .get();
        } catch (Exception exception) {
            throw new IllegalStateException("Could not probe the learning Redis lock", exception);
        } finally {
            executor.shutdownNow();
        }
    }

    private int queryCredit1() {
        return jdbcTemplate.queryForObject("SELECT credit1 FROM users WHERE id = 7", Integer.class);
    }

    private int pointLedgerCount() {
        return jdbcTemplate.queryForObject("SELECT COUNT(*) FROM point_ledgers", Integer.class);
    }

    private LearningFactPersistenceResult getResult(Future<LearningFactPersistenceResult> write) {
        try {
            return write.get();
        } catch (Exception exception) {
            throw new AssertionError("并发课程完成失败", exception);
        }
    }

    @SpringBootConfiguration
    @EnableAutoConfiguration
    @EnableTransactionManagement
    @EnableAsync
    @MapperScan({"xyz.playedu.course.mapper", "xyz.playedu.points.mapper"})
    @Import({
        DailyLearningDurationEventPublisher.class,
        DailyLearningRankingEventListener.class,
        LearningFactPersistenceService.class,
        RedisDistributedLock.class,
        RedisKeyspace.class,
        RedisRuntimeConfiguration.class,
        UserCourseHourRecordServiceImpl.class,
        UserCourseRecordServiceImpl.class,
        UserLearnDurationRecordServiceImpl.class,
        UserLearnDurationStatsServiceImpl.class,
        PointBalanceServiceImpl.class,
        TestDependencies.class
    })
    static class TestApplication {}

    @TestConfiguration
    static class TestDependencies {
        @Bean
        CourseHourService courseHourService() {
            CourseHourService courseHourService = mock(CourseHourService.class);
            when(courseHourService.getCountByCourseId(8)).thenReturn(1);
            return courseHourService;
        }

        @Bean
        LearningDurationEventCollector learningDurationEventCollector() {
            return new LearningDurationEventCollector();
        }

        @Bean
        DailyLearningRankingService rankingService() {
            return mock(DailyLearningRankingService.class);
        }

        @Bean
        MeterRegistry meterRegistry() {
            return new SimpleMeterRegistry();
        }
    }

    static class LearningDurationEventCollector {
        private final List<DailyLearningDurationConfirmedEvent> events = new ArrayList<>();

        @EventListener
        public void collect(DailyLearningDurationConfirmedEvent event) {
            events.add(event);
        }

        void clear() {
            events.clear();
        }
    }
}
