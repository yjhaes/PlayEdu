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

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import org.apache.ibatis.annotations.Mapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import xyz.playedu.course.service.impl.UserLearnDurationStatsServiceImpl;

@SpringBootTest(classes = UserLearnDurationStatsServiceIntegrationTest.TestApplication.class)
@Testcontainers(disabledWithoutDocker = true)
class UserLearnDurationStatsServiceIntegrationTest {

    private static final int CONCURRENT_WRITERS = 20;

    @Container static final MySQLContainer<?> MYSQL = new MySQLContainer<>("mysql:8.0");

    @Autowired private JdbcTemplate jdbcTemplate;

    @Autowired private UserLearnDurationStatsService userLearnDurationStatsService;

    @DynamicPropertySource
    static void databaseProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", MYSQL::getJdbcUrl);
        registry.add("spring.datasource.username", MYSQL::getUsername);
        registry.add("spring.datasource.password", MYSQL::getPassword);
        registry.add("spring.datasource.driver-class-name", MYSQL::getDriverClassName);
        registry.add("mybatis.mapper-locations", () -> "classpath:mapper/*.xml");
    }

    @BeforeEach
    void setUp() {
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
    void atomicallyKeepsEveryConcurrentIncrementInOneDailyRow() throws Exception {
        ExecutorService executor = Executors.newFixedThreadPool(CONCURRENT_WRITERS);
        CountDownLatch ready = new CountDownLatch(CONCURRENT_WRITERS);
        CountDownLatch start = new CountDownLatch(1);
        List<Future<?>> writes = new ArrayList<>();
        try {
            for (int index = 0; index < CONCURRENT_WRITERS; index++) {
                writes.add(
                        executor.submit(
                                () -> {
                                    ready.countDown();
                                    start.await();
                                    userLearnDurationStatsService.storeOrUpdate(42, 100L, 110L);
                                    return null;
                                }));
            }
            ready.await();
            start.countDown();
            for (Future<?> write : writes) {
                write.get();
            }
        } finally {
            executor.shutdownNow();
        }

        assertThat(
                        jdbcTemplate.queryForObject(
                                "SELECT COUNT(*) FROM user_learn_duration_stats WHERE user_id = 42",
                                Integer.class))
                .isEqualTo(1);
        assertThat(
                        jdbcTemplate.queryForObject(
                                "SELECT duration FROM user_learn_duration_stats WHERE user_id = 42",
                                Long.class))
                .isEqualTo(CONCURRENT_WRITERS * 10L);
    }

    @Test
    void reportsTheSumOfTodaysConfirmedDurations() {
        jdbcTemplate.update(
                "INSERT INTO user_learn_duration_stats (user_id, duration, created_date) VALUES (1,"
                        + " 100, CURRENT_DATE())");
        jdbcTemplate.update(
                "INSERT INTO user_learn_duration_stats (user_id, duration, created_date) VALUES (2,"
                        + " 250, CURRENT_DATE())");

        assertThat(userLearnDurationStatsService.todayTotal()).isEqualTo(350L);
    }

    @Test
    void splitsAnIntervalAtTheNaturalDayBoundary() {
        long start =
                LocalDate.of(2026, 9, 8)
                        .atTime(23, 59, 59)
                        .atZone(ZoneId.systemDefault())
                        .toInstant()
                        .toEpochMilli();
        long end = start + 2_000;

        userLearnDurationStatsService.storeOrUpdate(42, start, end);

        assertThat(
                        jdbcTemplate.queryForObject(
                                "SELECT COUNT(*) FROM user_learn_duration_stats WHERE user_id = 42",
                                Integer.class))
                .isEqualTo(2);
        assertThat(
                        jdbcTemplate.queryForObject(
                                "SELECT SUM(duration) FROM user_learn_duration_stats WHERE user_id"
                                        + " = 42",
                                Long.class))
                .isEqualTo(2_000L);
    }

    @SpringBootConfiguration
    @EnableAutoConfiguration
    @MapperScan(basePackages = "xyz.playedu.course.mapper", annotationClass = Mapper.class)
    @Import(UserLearnDurationStatsServiceImpl.class)
    static class TestApplication {}
}
