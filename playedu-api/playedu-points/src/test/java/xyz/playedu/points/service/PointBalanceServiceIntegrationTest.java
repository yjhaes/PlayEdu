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
package xyz.playedu.points.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
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
import xyz.playedu.points.domain.PointLedgerType;
import xyz.playedu.points.service.impl.PointBalanceServiceImpl;

@SpringBootTest(classes = PointBalanceServiceIntegrationTest.TestApplication.class)
@Testcontainers(disabledWithoutDocker = true)
class PointBalanceServiceIntegrationTest {

    @Container static final MySQLContainer<?> MYSQL = new MySQLContainer<>("mysql:8.0");

    @Autowired private JdbcTemplate jdbcTemplate;

    @Autowired private PointBalanceService pointBalanceService;

    @DynamicPropertySource
    static void databaseProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", MYSQL::getJdbcUrl);
        registry.add("spring.datasource.username", MYSQL::getUsername);
        registry.add("spring.datasource.password", MYSQL::getPassword);
        registry.add("spring.datasource.driver-class-name", MYSQL::getDriverClassName);
    }

    @BeforeEach
    void setUp() {
        jdbcTemplate.execute("DROP TABLE IF EXISTS point_ledgers");
        jdbcTemplate.execute("DROP TABLE IF EXISTS users");
        jdbcTemplate.execute(
                """
                CREATE TABLE users (
                    id int unsigned NOT NULL,
                    credit1 int NOT NULL DEFAULT 0,
                    updated_at datetime NULL,
                    PRIMARY KEY (id),
                    CONSTRAINT chk_fail_balance_update CHECK (id <> 2 OR credit1 <> 10)
                ) ENGINE=InnoDB
                """);
        jdbcTemplate.execute(
                """
                CREATE TABLE point_ledgers (
                    id int unsigned NOT NULL AUTO_INCREMENT,
                    user_id int unsigned NOT NULL,
                    delta int NOT NULL,
                    balance_after int NOT NULL,
                    type varchar(32) NOT NULL,
                    source_key varchar(191) NOT NULL,
                    reason text NULL,
                    operator_admin_id int unsigned NULL,
                    created_at datetime NOT NULL,
                    PRIMARY KEY (id),
                    UNIQUE KEY uk_point_ledgers_source_key (source_key)
                ) ENGINE=InnoDB
                """);
        jdbcTemplate.update("INSERT INTO users (id, credit1) VALUES (1, 0), (2, 0)");
    }

    @Test
    void commitsTheBalanceAndLedgerTogether() {
        PointBalanceChangeResult result =
                pointBalanceService.apply(
                        new PointBalanceChange(
                                1,
                                10,
                                PointLedgerType.COURSE_COMPLETION,
                                "course-completion:1:7",
                                null,
                                null,
                                false));

        assertThat(result.applied()).isTrue();
        assertThat(result.balanceAfter()).isEqualTo(10);
        assertThat(
                        jdbcTemplate.queryForObject(
                                "SELECT credit1 FROM users WHERE id = 1", Integer.class))
                .isEqualTo(10);
        assertThat(jdbcTemplate.queryForObject("SELECT COUNT(*) FROM point_ledgers", Integer.class))
                .isEqualTo(1);
        assertThat(
                        jdbcTemplate.queryForObject(
                                "SELECT balance_after FROM point_ledgers WHERE source_key = ?",
                                Integer.class,
                                "course-completion:1:7"))
                .isEqualTo(10);
    }

    @Test
    void rollsBackTheLedgerWhenTheBalanceWriteFails() {
        assertThatThrownBy(
                        () ->
                                pointBalanceService.apply(
                                        new PointBalanceChange(
                                                2,
                                                10,
                                                PointLedgerType.COURSE_COMPLETION,
                                                "course-completion:1:8",
                                                null,
                                                null,
                                                false)))
                .isInstanceOf(Exception.class);

        assertThat(
                        jdbcTemplate.queryForObject(
                                "SELECT credit1 FROM users WHERE id = 2", Integer.class))
                .isZero();
        assertThat(jdbcTemplate.queryForObject("SELECT COUNT(*) FROM point_ledgers", Integer.class))
                .isZero();
    }

    @Test
    void returnsTheOriginalResultForARepeatedSourceKey() {
        PointBalanceChange change =
                new PointBalanceChange(
                        1,
                        10,
                        PointLedgerType.COURSE_COMPLETION,
                        "course-completion:1:9",
                        null,
                        null,
                        false);

        PointBalanceChangeResult first = pointBalanceService.apply(change);
        PointBalanceChangeResult repeated = pointBalanceService.apply(change);

        assertThat(first.ledger().getId()).isEqualTo(repeated.ledger().getId());
        assertThat(repeated.applied()).isFalse();
        assertThat(repeated.balanceAfter()).isEqualTo(10);
        assertThat(
                        jdbcTemplate.queryForObject(
                                "SELECT credit1 FROM users WHERE id = 1", Integer.class))
                .isEqualTo(10);
        assertThat(jdbcTemplate.queryForObject("SELECT COUNT(*) FROM point_ledgers", Integer.class))
                .isEqualTo(1);
    }

    @Test
    void returnsOneOriginalResultForConcurrentRequestsWithTheSameSourceKey() throws Exception {
        int requests = 12;
        ExecutorService executor = Executors.newFixedThreadPool(requests);
        CountDownLatch ready = new CountDownLatch(requests);
        CountDownLatch start = new CountDownLatch(1);
        List<Future<PointBalanceChangeResult>> writes = new ArrayList<>();
        try {
            for (int index = 0; index < requests; index++) {
                writes.add(
                        executor.submit(
                                () -> {
                                    ready.countDown();
                                    start.await();
                                    return pointBalanceService.apply(
                                            new PointBalanceChange(
                                                    1,
                                                    10,
                                                    PointLedgerType.COURSE_COMPLETION,
                                                    "course-completion:1:concurrent",
                                                    null,
                                                    null,
                                                    false));
                                }));
            }
            assertThat(ready.await(5, TimeUnit.SECONDS)).isTrue();
            start.countDown();

            long applied =
                    writes.stream()
                            .map(this::getResult)
                            .filter(PointBalanceChangeResult::applied)
                            .count();
            assertThat(applied).isEqualTo(1);
        } finally {
            executor.shutdownNow();
        }

        assertThat(
                        jdbcTemplate.queryForObject(
                                "SELECT credit1 FROM users WHERE id = 1", Integer.class))
                .isEqualTo(10);
        assertThat(jdbcTemplate.queryForObject("SELECT COUNT(*) FROM point_ledgers", Integer.class))
                .isEqualTo(1);
    }

    @Test
    void allowsManualDebtAndLetsLaterRewardsOffsetIt() {
        PointBalanceChangeResult adjustment =
                pointBalanceService.apply(
                        new PointBalanceChange(
                                1,
                                -30,
                                PointLedgerType.MANUAL_ADJUSTMENT,
                                "manual:1:1",
                                "纠正重复发放",
                                99,
                                true));

        PointBalanceChangeResult reward =
                pointBalanceService.apply(
                        new PointBalanceChange(
                                1,
                                10,
                                PointLedgerType.COURSE_COMPLETION,
                                "course-completion:1:10",
                                null,
                                null,
                                false));

        assertThat(adjustment.balanceAfter()).isEqualTo(-30);
        assertThat(reward.balanceAfter()).isEqualTo(-20);
        assertThat(
                        jdbcTemplate.queryForObject(
                                "SELECT credit1 FROM users WHERE id = 1", Integer.class))
                .isEqualTo(-20);
        assertThat(
                        jdbcTemplate.queryForObject(
                                "SELECT operator_admin_id FROM point_ledgers WHERE source_key = ?",
                                Integer.class,
                                "manual:1:1"))
                .isEqualTo(99);
    }

    @Test
    void rejectsARegularRedemptionThatWouldMakeTheBalanceNegative() {
        assertThatThrownBy(
                        () ->
                                pointBalanceService.apply(
                                        new PointBalanceChange(
                                                1,
                                                -1,
                                                PointLedgerType.REDEMPTION,
                                                "redemption:1:1",
                                                null,
                                                null,
                                                false)))
                .isInstanceOf(PointBalanceException.class);

        assertThat(
                        jdbcTemplate.queryForObject(
                                "SELECT credit1 FROM users WHERE id = 1", Integer.class))
                .isZero();
        assertThat(jdbcTemplate.queryForObject("SELECT COUNT(*) FROM point_ledgers", Integer.class))
                .isZero();
    }

    @Test
    void serializesConcurrentChangesForOneUserWithoutLosingBalances() throws Exception {
        int writers = 12;
        ExecutorService executor = Executors.newFixedThreadPool(writers);
        CountDownLatch ready = new CountDownLatch(writers);
        CountDownLatch start = new CountDownLatch(1);
        List<Future<PointBalanceChangeResult>> writes = new ArrayList<>();
        try {
            for (int index = 0; index < writers; index++) {
                int sourceNumber = index;
                writes.add(
                        executor.submit(
                                () -> {
                                    ready.countDown();
                                    start.await();
                                    return pointBalanceService.apply(
                                            new PointBalanceChange(
                                                    1,
                                                    10,
                                                    PointLedgerType.COURSE_COMPLETION,
                                                    "course-completion:1:" + sourceNumber,
                                                    null,
                                                    null,
                                                    false));
                                }));
            }
            assertThat(ready.await(5, TimeUnit.SECONDS)).isTrue();
            start.countDown();
            for (Future<PointBalanceChangeResult> write : writes) {
                write.get();
            }
        } finally {
            executor.shutdownNow();
        }

        assertThat(
                        jdbcTemplate.queryForObject(
                                "SELECT credit1 FROM users WHERE id = 1", Integer.class))
                .isEqualTo(writers * 10);
        assertThat(jdbcTemplate.queryForObject("SELECT COUNT(*) FROM point_ledgers", Integer.class))
                .isEqualTo(writers);
        assertThat(
                        jdbcTemplate.queryForObject(
                                "SELECT COUNT(DISTINCT balance_after) FROM point_ledgers",
                                Integer.class))
                .isEqualTo(writers);
    }

    @Test
    void serializesConcurrentRewardsAndRedemptionsWithoutLosingEitherChange() throws Exception {
        jdbcTemplate.update("UPDATE users SET credit1 = 100 WHERE id = 1");

        int writers = 12;
        ExecutorService executor = Executors.newFixedThreadPool(writers);
        CountDownLatch ready = new CountDownLatch(writers);
        CountDownLatch start = new CountDownLatch(1);
        List<Future<?>> writes = new ArrayList<>();
        try {
            for (int index = 0; index < writers; index++) {
                int sourceNumber = index;
                writes.add(
                        executor.submit(
                                () -> {
                                    ready.countDown();
                                    start.await();
                                    PointLedgerType type =
                                            sourceNumber % 2 == 0
                                                    ? PointLedgerType.COURSE_COMPLETION
                                                    : PointLedgerType.REDEMPTION;
                                    int delta = sourceNumber % 2 == 0 ? 10 : -10;
                                    pointBalanceService.apply(
                                            new PointBalanceChange(
                                                    1,
                                                    delta,
                                                    type,
                                                    "mixed-change:1:" + sourceNumber,
                                                    null,
                                                    null,
                                                    false));
                                    return null;
                                }));
            }
            assertThat(ready.await(5, TimeUnit.SECONDS)).isTrue();
            start.countDown();
            for (Future<?> write : writes) {
                write.get();
            }
        } finally {
            executor.shutdownNow();
        }

        assertThat(
                        jdbcTemplate.queryForObject(
                                "SELECT credit1 FROM users WHERE id = 1", Integer.class))
                .isEqualTo(100);
        assertThat(jdbcTemplate.queryForObject("SELECT COUNT(*) FROM point_ledgers", Integer.class))
                .isEqualTo(writers);
    }

    @Test
    void requiresAReasonAndSuperAdminMarkerForManualAdjustments() {
        assertThatThrownBy(
                        () ->
                                pointBalanceService.apply(
                                        new PointBalanceChange(
                                                1,
                                                10,
                                                PointLedgerType.MANUAL_ADJUSTMENT,
                                                "manual:1:2",
                                                " ",
                                                99,
                                                true)))
                .isInstanceOf(PointBalanceException.class);
        assertThatThrownBy(
                        () ->
                                pointBalanceService.apply(
                                        new PointBalanceChange(
                                                1,
                                                10,
                                                PointLedgerType.MANUAL_ADJUSTMENT,
                                                "manual:1:3",
                                                "运营补偿",
                                                99,
                                                false)))
                .isInstanceOf(PointBalanceException.class);
    }

    private PointBalanceChangeResult getResult(Future<PointBalanceChangeResult> write) {
        try {
            return write.get();
        } catch (Exception exception) {
            throw new AssertionError("并发积分变更失败", exception);
        }
    }

    @SpringBootConfiguration
    @EnableAutoConfiguration
    @MapperScan("xyz.playedu.points.mapper")
    @Import(PointBalanceServiceImpl.class)
    static class TestApplication {}
}
