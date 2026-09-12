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
package xyz.playedu.points.migration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

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
import xyz.playedu.points.service.PointBalanceChange;
import xyz.playedu.points.service.PointBalanceService;
import xyz.playedu.points.service.PointSourceKeys;
import xyz.playedu.points.service.impl.PointBalanceServiceImpl;

@SpringBootTest(classes = HistoricalPointsBackfillServiceIntegrationTest.TestApplication.class)
@Testcontainers(disabledWithoutDocker = true)
class HistoricalPointsBackfillServiceIntegrationTest {

    @Container static final MySQLContainer<?> MYSQL = new MySQLContainer<>("mysql:8.0");

    @Autowired private JdbcTemplate jdbcTemplate;

    @Autowired private HistoricalPointsBackfillService backfillService;

    @Autowired private HistoricalRewardSummaryService summaryService;

    @Autowired private PointsFeatureGate pointsFeatureGate;

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
        jdbcTemplate.execute("DROP TABLE IF EXISTS point_historical_reward_summaries");
        jdbcTemplate.execute("DROP TABLE IF EXISTS point_migration_state");
        jdbcTemplate.execute("DROP TABLE IF EXISTS point_ledgers");
        jdbcTemplate.execute("DROP TABLE IF EXISTS user_course_records");
        jdbcTemplate.execute("DROP TABLE IF EXISTS users");
        jdbcTemplate.execute(
                """
                CREATE TABLE users (
                    id int unsigned NOT NULL,
                    credit1 int NOT NULL DEFAULT 0,
                    updated_at datetime NULL,
                    PRIMARY KEY (id)
                ) ENGINE=InnoDB
                """);
        jdbcTemplate.execute(
                """
                CREATE TABLE user_course_records (
                    id int unsigned NOT NULL AUTO_INCREMENT,
                    user_id int unsigned NOT NULL,
                    course_id int unsigned NOT NULL,
                    is_finished tinyint NOT NULL DEFAULT 0,
                    PRIMARY KEY (id)
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
        jdbcTemplate.execute(
                """
                CREATE TABLE point_migration_state (
                    migration_key varchar(191) NOT NULL,
                    completed_at datetime NOT NULL,
                    legacy_credit1_non_zero_user_count bigint unsigned NOT NULL,
                    legacy_credit1_total bigint NOT NULL,
                    historical_learner_count bigint unsigned NOT NULL,
                    historical_course_completion_count bigint unsigned NOT NULL,
                    expected_reward_total bigint unsigned NOT NULL,
                    PRIMARY KEY (migration_key)
                ) ENGINE=InnoDB
                """);
        jdbcTemplate.execute(
                """
                CREATE TABLE point_historical_reward_summaries (
                    user_id int unsigned NOT NULL,
                    completion_count bigint unsigned NOT NULL,
                    points_awarded bigint unsigned NOT NULL,
                    acknowledged_at datetime NULL,
                    created_at datetime NOT NULL,
                    PRIMARY KEY (user_id)
                ) ENGINE=InnoDB
                """);

        jdbcTemplate.update(
                "INSERT INTO users (id, credit1) VALUES (1, 25), (2, 0), (3, -5), (4, 0)");
        jdbcTemplate.update(
                "INSERT INTO user_course_records (user_id, course_id, is_finished) VALUES "
                        + "(1, 101, 1), (1, 102, 0), (2, 101, 1)");
    }

    @Test
    void dryRunReportsAggregatesWithoutChangingLegacyBalancesOrWritingPoints() {
        HistoricalPointsBackfillReport report = backfillService.dryRun();

        assertThat(report.legacyCredit1NonZeroUserCount()).isEqualTo(2);
        assertThat(report.legacyCredit1Total()).isEqualTo(20);
        assertThat(report.historicalLearnerCount()).isEqualTo(2);
        assertThat(report.historicalCourseCompletionCount()).isEqualTo(2);
        assertThat(report.expectedRewardTotal()).isEqualTo(20);
        assertThat(
                        jdbcTemplate.queryForObject(
                                "SELECT credit1 FROM users WHERE id = 1", Integer.class))
                .isEqualTo(25);
        assertThat(
                        jdbcTemplate.queryForObject(
                                "SELECT credit1 FROM users WHERE id = 3", Integer.class))
                .isEqualTo(-5);
        assertThat(jdbcTemplate.queryForObject("SELECT COUNT(*) FROM point_ledgers", Integer.class))
                .isZero();
        assertThat(
                        jdbcTemplate.queryForObject(
                                "SELECT COUNT(*) FROM point_migration_state", Integer.class))
                .isZero();
    }

    @Test
    void resetsLegacyCreditAndAwardsEachCurrentCompletionExactlyOnce() {
        HistoricalPointsBackfillReport report = backfillService.resetAndBackfill(true);

        assertThat(report.expectedRewardTotal()).isEqualTo(20);
        assertThat(
                        jdbcTemplate.queryForObject(
                                "SELECT credit1 FROM users WHERE id = 1", Integer.class))
                .isEqualTo(10);
        assertThat(
                        jdbcTemplate.queryForObject(
                                "SELECT credit1 FROM users WHERE id = 2", Integer.class))
                .isEqualTo(10);
        assertThat(
                        jdbcTemplate.queryForObject(
                                "SELECT credit1 FROM users WHERE id = 3", Integer.class))
                .isZero();
        assertThat(jdbcTemplate.queryForObject("SELECT COUNT(*) FROM point_ledgers", Integer.class))
                .isEqualTo(2);
        assertThat(
                        jdbcTemplate.queryForObject(
                                "SELECT COUNT(*) FROM point_ledgers WHERE type ="
                                        + " 'HISTORICAL_COURSE_COMPLETION'",
                                Integer.class))
                .isEqualTo(2);

        HistoricalPointsBackfillReport repeated = backfillService.resetAndBackfill(true);
        assertThat(repeated).isEqualTo(report);
        assertThat(backfillService.resetAndBackfill(false)).isEqualTo(report);
        assertThat(jdbcTemplate.queryForObject("SELECT COUNT(*) FROM point_ledgers", Integer.class))
                .isEqualTo(2);
        assertThat(summaryService.claimForDisplay(1))
                .hasValueSatisfying(
                        summary -> {
                            assertThat(summary.completionCount()).isEqualTo(1);
                            assertThat(summary.pointsAwarded()).isEqualTo(10);
                        });
        assertThat(summaryService.claimForDisplay(1)).isEmpty();
    }

    @Test
    void keepsTheHistoricalNoticePendingUntilTheLearnerAcknowledgesIt() {
        backfillService.resetAndBackfill(true);

        assertThat(summaryService.pendingForDisplay(1))
                .hasValueSatisfying(
                        summary -> {
                            assertThat(summary.completionCount()).isEqualTo(1);
                            assertThat(summary.pointsAwarded()).isEqualTo(10);
                        });
        assertThat(summaryService.pendingForDisplay(1)).isPresent();

        summaryService.acknowledge(1);

        assertThat(summaryService.pendingForDisplay(1)).isEmpty();
        summaryService.acknowledge(1);
    }

    @Test
    void refusesToResetLegacyCreditWithoutARecoverableBackupConfirmation() {
        assertThatThrownBy(() -> backfillService.resetAndBackfill(false))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("备份");

        assertThat(
                        jdbcTemplate.queryForObject(
                                "SELECT credit1 FROM users WHERE id = 1", Integer.class))
                .isEqualTo(25);
        assertThat(jdbcTemplate.queryForObject("SELECT COUNT(*) FROM point_ledgers", Integer.class))
                .isZero();
        assertThat(pointsFeatureGate.isOpen()).isFalse();
    }

    @Test
    void rollsBackAPartialBackfillSoASecondRunCanSafelyRetry() {
        jdbcTemplate.execute(
                """
                ALTER TABLE point_ledgers
                ADD CONSTRAINT chk_fail_historical_reward CHECK (user_id <> 2)
                """);

        assertThatThrownBy(() -> backfillService.resetAndBackfill(true))
                .isInstanceOf(RuntimeException.class);
        assertThat(
                        jdbcTemplate.queryForObject(
                                "SELECT credit1 FROM users WHERE id = 1", Integer.class))
                .isEqualTo(25);
        assertThat(
                        jdbcTemplate.queryForObject(
                                "SELECT credit1 FROM users WHERE id = 3", Integer.class))
                .isEqualTo(-5);
        assertThat(jdbcTemplate.queryForObject("SELECT COUNT(*) FROM point_ledgers", Integer.class))
                .isZero();
        assertThat(pointsFeatureGate.isOpen()).isFalse();

        jdbcTemplate.execute("ALTER TABLE point_ledgers DROP CHECK chk_fail_historical_reward");
        backfillService.resetAndBackfill(true);

        assertThat(
                        jdbcTemplate.queryForObject(
                                "SELECT credit1 FROM users WHERE id = 1", Integer.class))
                .isEqualTo(10);
        assertThat(
                        jdbcTemplate.queryForObject(
                                "SELECT credit1 FROM users WHERE id = 2", Integer.class))
                .isEqualTo(10);
        assertThat(jdbcTemplate.queryForObject("SELECT COUNT(*) FROM point_ledgers", Integer.class))
                .isEqualTo(2);
        assertThat(pointsFeatureGate.isOpen()).isTrue();
    }

    @Test
    void sharesTheOnlineSourceKeyWhenMigrationAndOnlineRewardRace() throws Exception {
        ExecutorService executor = Executors.newFixedThreadPool(2);
        CountDownLatch ready = new CountDownLatch(2);
        CountDownLatch start = new CountDownLatch(1);
        try {
            Future<?> migration =
                    executor.submit(
                            () -> {
                                ready.countDown();
                                await(start);
                                backfillService.resetAndBackfill(true);
                            });
            Future<?> onlineReward =
                    executor.submit(
                            () -> {
                                ready.countDown();
                                await(start);
                                pointBalanceService.apply(
                                        new PointBalanceChange(
                                                1,
                                                10,
                                                PointLedgerType.COURSE_COMPLETION,
                                                PointSourceKeys.courseCompletion(1, 101),
                                                null,
                                                null,
                                                false));
                            });
            assertThat(ready.await(5, TimeUnit.SECONDS)).isTrue();
            start.countDown();
            migration.get();
            onlineReward.get();
        } finally {
            executor.shutdownNow();
        }

        assertThat(
                        jdbcTemplate.queryForObject(
                                "SELECT credit1 FROM users WHERE id = 1", Integer.class))
                .isEqualTo(10);
        assertThat(jdbcTemplate.queryForObject("SELECT COUNT(*) FROM point_ledgers", Integer.class))
                .isEqualTo(2);
        assertThat(
                        jdbcTemplate.queryForObject(
                                "SELECT COUNT(*) FROM point_ledgers WHERE source_key ="
                                        + " 'course-completion:1:101'",
                                Integer.class))
                .isEqualTo(1);
    }

    private void await(CountDownLatch latch) {
        try {
            if (!latch.await(5, TimeUnit.SECONDS)) {
                throw new AssertionError("并发积分迁移未能开始");
            }
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            throw new AssertionError("并发积分迁移被中断", exception);
        }
    }

    @SpringBootConfiguration
    @EnableAutoConfiguration
    @MapperScan("xyz.playedu.points.mapper")
    @Import({
        HistoricalPointsBackfillServiceImpl.class,
        HistoricalPointsMigrationStateServiceImpl.class,
        HistoricalRewardSummaryServiceImpl.class,
        PointsFeatureGateImpl.class,
        PointBalanceServiceImpl.class
    })
    static class TestApplication {}
}
