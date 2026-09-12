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
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import xyz.playedu.common.exception.NotFoundException;
import xyz.playedu.common.exception.ServiceException;
import xyz.playedu.points.domain.PointCodeStatus;
import xyz.playedu.points.domain.PointLedgerType;
import xyz.playedu.points.domain.PointRedemption;
import xyz.playedu.points.migration.PointsFeatureGate;
import xyz.playedu.points.service.impl.PointBalanceServiceImpl;
import xyz.playedu.points.service.impl.PointRedemptionServiceImpl;

@SpringBootTest(classes = PointRedemptionServiceIntegrationTest.TestApplication.class)
@Testcontainers(disabledWithoutDocker = true)
class PointRedemptionServiceIntegrationTest {

    @Container static final MySQLContainer<?> MYSQL = new MySQLContainer<>("mysql:8.0");

    @Autowired private JdbcTemplate jdbcTemplate;

    @Autowired private PointRedemptionService pointRedemptionService;

    @DynamicPropertySource
    static void databaseProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", MYSQL::getJdbcUrl);
        registry.add("spring.datasource.username", MYSQL::getUsername);
        registry.add("spring.datasource.password", MYSQL::getPassword);
        registry.add("spring.datasource.driver-class-name", MYSQL::getDriverClassName);
    }

    @BeforeEach
    void setUp() {
        jdbcTemplate.execute("DROP TABLE IF EXISTS point_redemptions");
        jdbcTemplate.execute("DROP TABLE IF EXISTS point_ledgers");
        jdbcTemplate.execute("DROP TABLE IF EXISTS point_codes");
        jdbcTemplate.execute("DROP TABLE IF EXISTS point_products");
        jdbcTemplate.execute("DROP TABLE IF EXISTS users");
        jdbcTemplate.execute(
                """
                CREATE TABLE users (
                    id int unsigned NOT NULL,
                    credit1 int NOT NULL DEFAULT 0,
                    is_lock tinyint NOT NULL DEFAULT 0,
                    updated_at datetime NULL,
                    PRIMARY KEY (id)
                ) ENGINE=InnoDB
                """);
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
                CREATE TABLE point_redemptions (
                    id int unsigned NOT NULL AUTO_INCREMENT,
                    user_id int unsigned NOT NULL,
                    product_id int unsigned NOT NULL,
                    code_id int unsigned NOT NULL,
                    request_key varchar(160) NOT NULL,
                    points_cost int NOT NULL,
                    created_at datetime NOT NULL,
                    PRIMARY KEY (id),
                    UNIQUE KEY uk_point_redemptions_code_id (code_id),
                    UNIQUE KEY uk_point_redemptions_user_request_key (user_id, request_key)
                ) ENGINE=InnoDB
                """);
        jdbcTemplate.update("INSERT INTO users (id, credit1, is_lock) VALUES (1, 100, 0)");
        jdbcTemplate.update(
                "INSERT INTO point_products (id, name, points_price, status, created_at,"
                        + " updated_at) VALUES (1, '试运行商品', 50, 'ON_SALE', NOW(), NOW())");
        insertAvailableCodes(1, 2);
    }

    @Test
    void deliversOneCodeAndReturnsTheOriginalResultForARetry() {
        PointRedemption first = pointRedemptionService.redeem(1, 1, "request-1");
        PointRedemption retried = pointRedemptionService.redeem(1, 1, "request-1");

        assertThat(retried.getId()).isEqualTo(first.getId());
        assertThat(first.getPointsCost()).isEqualTo(50);
        assertThat(balanceOf(1)).isEqualTo(50);
        assertThat(count("SELECT COUNT(*) FROM point_redemptions")).isEqualTo(1);
        assertThat(count("SELECT COUNT(*) FROM point_ledgers")).isEqualTo(1);
        assertThat(jdbcTemplate.queryForObject("SELECT type FROM point_ledgers", String.class))
                .isEqualTo(PointLedgerType.REDEMPTION.name());
        assertThat(countByStatus(PointCodeStatus.DELIVERED)).isEqualTo(1);
        assertThat(countByStatus(PointCodeStatus.AVAILABLE)).isEqualTo(1);
    }

    @Test
    void findsARedemptionOnlyForItsOwningLearner() throws Exception {
        PointRedemption redemption = pointRedemptionService.redeem(1, 1, "detail-request");

        assertThat(pointRedemptionService.findForUser(redemption.getId(), 1).getId())
                .isEqualTo(redemption.getId());
        assertThatThrownBy(() -> pointRedemptionService.findForUser(redemption.getId(), 2))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("兑换记录不存在");
    }

    @Test
    void deliversOnlyOnceForConcurrentRetriesWithTheSameRequestKey() throws Exception {
        int requests = 12;
        ExecutorService executor = Executors.newFixedThreadPool(requests);
        CountDownLatch ready = new CountDownLatch(requests);
        CountDownLatch start = new CountDownLatch(1);
        List<Future<PointRedemption>> redemptions = new ArrayList<>();
        try {
            for (int index = 0; index < requests; index++) {
                redemptions.add(
                        executor.submit(
                                () -> {
                                    ready.countDown();
                                    start.await();
                                    return pointRedemptionService.redeem(1, 1, "same-request");
                                }));
            }
            assertThat(ready.await(5, TimeUnit.SECONDS)).isTrue();
            start.countDown();

            int redemptionId = redemptions.get(0).get().getId();
            for (Future<PointRedemption> redemption : redemptions) {
                assertThat(redemption.get().getId()).isEqualTo(redemptionId);
            }
        } finally {
            executor.shutdownNow();
        }

        assertThat(balanceOf(1)).isEqualTo(50);
        assertThat(count("SELECT COUNT(*) FROM point_redemptions")).isEqualTo(1);
        assertThat(count("SELECT COUNT(*) FROM point_ledgers")).isEqualTo(1);
        assertThat(countByStatus(PointCodeStatus.DELIVERED)).isEqualTo(1);
    }

    @Test
    void leavesNoChangesWhenTheLearnerIsLockedOrTheBalanceOrInventoryIsInsufficient() {
        jdbcTemplate.update("UPDATE users SET is_lock = 1 WHERE id = 1");
        assertThatThrownBy(() -> pointRedemptionService.redeem(1, 1, "locked"))
                .isInstanceOf(ServiceException.class);
        assertNoDeliveryOrDebit();

        jdbcTemplate.update("UPDATE users SET is_lock = 0, credit1 = 49 WHERE id = 1");
        assertThatThrownBy(() -> pointRedemptionService.redeem(1, 1, "insufficient-balance"))
                .isInstanceOf(ServiceException.class);
        assertThat(balanceOf(1)).isEqualTo(49);
        assertThat(count("SELECT COUNT(*) FROM point_redemptions")).isZero();
        assertThat(count("SELECT COUNT(*) FROM point_ledgers")).isZero();
        assertThat(countByStatus(PointCodeStatus.DELIVERED)).isZero();

        jdbcTemplate.update("UPDATE users SET credit1 = 100 WHERE id = 1");
        jdbcTemplate.update("DELETE FROM point_codes");
        assertThatThrownBy(() -> pointRedemptionService.redeem(1, 1, "out-of-stock"))
                .isInstanceOf(ServiceException.class);
        assertThat(balanceOf(1)).isEqualTo(100);
        assertThat(count("SELECT COUNT(*) FROM point_redemptions")).isZero();
        assertThat(count("SELECT COUNT(*) FROM point_ledgers")).isZero();
    }

    @Test
    void rollsBackCodeClaimAndDebitWhenRedemptionRecordCannotBeWritten() {
        jdbcTemplate.execute(
                "ALTER TABLE point_redemptions ADD CONSTRAINT chk_point_redemption_rollback"
                        + " CHECK (points_cost < 0)");
        try {
            assertThatThrownBy(() -> pointRedemptionService.redeem(1, 1, "transaction-failure"))
                    .isInstanceOf(Exception.class);

            assertThat(balanceOf(1)).isEqualTo(100);
            assertThat(count("SELECT COUNT(*) FROM point_redemptions")).isZero();
            assertThat(count("SELECT COUNT(*) FROM point_ledgers")).isZero();
            assertThat(countByStatus(PointCodeStatus.DELIVERED)).isZero();
            assertThat(countByStatus(PointCodeStatus.AVAILABLE)).isEqualTo(2);
        } finally {
            jdbcTemplate.execute(
                    "ALTER TABLE point_redemptions DROP CHECK chk_point_redemption_rollback");
        }
    }

    @Test
    void serializesConcurrentRedemptionsWithoutOverspendingOrReusingCodes() throws Exception {
        jdbcTemplate.update("DELETE FROM point_codes");
        jdbcTemplate.update("UPDATE point_products SET points_price = 10 WHERE id = 1");
        jdbcTemplate.update("UPDATE users SET credit1 = 10 WHERE id = 1");
        for (int userId = 2; userId <= 12; userId++) {
            jdbcTemplate.update(
                    "INSERT INTO users (id, credit1, is_lock) VALUES (?, 10, 0)", userId);
        }
        insertAvailableCodes(1, 10);

        int requests = 12;
        ExecutorService executor = Executors.newFixedThreadPool(requests);
        CountDownLatch ready = new CountDownLatch(requests);
        CountDownLatch start = new CountDownLatch(1);
        List<Future<PointRedemption>> redemptions = new ArrayList<>();
        try {
            for (int userId = 1; userId <= requests; userId++) {
                int requestUserId = userId;
                redemptions.add(
                        executor.submit(
                                () -> {
                                    ready.countDown();
                                    start.await();
                                    return pointRedemptionService.redeem(
                                            requestUserId,
                                            1,
                                            "concurrent-request-" + requestUserId);
                                }));
            }
            assertThat(ready.await(5, TimeUnit.SECONDS)).isTrue();
            start.countDown();

            int delivered = 0;
            for (Future<PointRedemption> redemption : redemptions) {
                try {
                    redemption.get();
                    delivered++;
                } catch (Exception ignored) {
                    // Two requests must fail because there are only ten available codes.
                }
            }
            assertThat(delivered).isEqualTo(10);
        } finally {
            executor.shutdownNow();
        }

        assertThat(count("SELECT COUNT(*) FROM point_redemptions")).isEqualTo(10);
        assertThat(count("SELECT COUNT(*) FROM point_ledgers")).isEqualTo(10);
        assertThat(count("SELECT COUNT(DISTINCT code_id) FROM point_redemptions")).isEqualTo(10);
        assertThat(countByStatus(PointCodeStatus.DELIVERED)).isEqualTo(10);
        assertThat(countByStatus(PointCodeStatus.AVAILABLE)).isZero();
        assertThat(count("SELECT COUNT(*) FROM users WHERE credit1 < 0")).isZero();
    }

    private void assertNoDeliveryOrDebit() {
        assertThat(balanceOf(1)).isEqualTo(100);
        assertThat(count("SELECT COUNT(*) FROM point_redemptions")).isZero();
        assertThat(count("SELECT COUNT(*) FROM point_ledgers")).isZero();
        assertThat(countByStatus(PointCodeStatus.DELIVERED)).isZero();
    }

    private void insertAvailableCodes(int productId, int count) {
        for (int index = 1; index <= count; index++) {
            jdbcTemplate.update(
                    "INSERT INTO point_codes (product_id, code_ciphertext, code_digest, status,"
                        + " created_at, updated_at) VALUES (?, ?, ?, 'AVAILABLE', NOW(), NOW())",
                    productId,
                    "ciphertext-" + productId + "-" + index,
                    "digest-" + productId + "-" + index);
        }
    }

    private int balanceOf(int userId) {
        return jdbcTemplate.queryForObject(
                "SELECT credit1 FROM users WHERE id = ?", Integer.class, userId);
    }

    private int countByStatus(PointCodeStatus status) {
        return jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM point_codes WHERE status = ?", Integer.class, status.name());
    }

    private int count(String sql) {
        return jdbcTemplate.queryForObject(sql, Integer.class);
    }

    @SpringBootConfiguration
    @EnableAutoConfiguration
    @MapperScan("xyz.playedu.points.mapper")
    @Import({PointBalanceServiceImpl.class, PointRedemptionServiceImpl.class})
    static class TestApplication {

        @Bean
        PointsFeatureGate pointsFeatureGate() {
            return new PointsFeatureGate() {
                @Override
                public boolean isOpen() {
                    return true;
                }

                @Override
                public void requireOpen() {}
            };
        }
    }
}
