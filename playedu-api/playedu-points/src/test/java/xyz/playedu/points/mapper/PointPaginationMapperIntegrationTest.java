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
package xyz.playedu.points.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import xyz.playedu.points.domain.PointCode;
import xyz.playedu.points.domain.PointCodeStatus;
import xyz.playedu.points.domain.PointLedger;
import xyz.playedu.points.domain.PointLedgerType;
import xyz.playedu.points.domain.PointProduct;
import xyz.playedu.points.domain.PointProductStatus;
import xyz.playedu.points.domain.PointRedemption;

@SpringBootTest(classes = PointPaginationMapperIntegrationTest.TestApplication.class)
@Testcontainers(disabledWithoutDocker = true)
class PointPaginationMapperIntegrationTest {

    @Container static final MySQLContainer<?> MYSQL = new MySQLContainer<>("mysql:8.0");

    @Autowired private JdbcTemplate jdbcTemplate;
    @Autowired private PointProductMapper products;
    @Autowired private PointCodeMapper codes;
    @Autowired private PointLedgerMapper ledgers;
    @Autowired private PointRedemptionMapper redemptions;

    @DynamicPropertySource
    static void databaseProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", MYSQL::getJdbcUrl);
        registry.add("spring.datasource.username", MYSQL::getUsername);
        registry.add("spring.datasource.password", MYSQL::getPassword);
        registry.add("spring.datasource.driver-class-name", MYSQL::getDriverClassName);
    }

    @BeforeEach
    void setUp() {
        for (String table :
                new String[] {
                    "point_products", "point_codes", "point_ledgers", "point_redemptions"
                }) {
            jdbcTemplate.execute("DROP TABLE IF EXISTS " + table);
        }
        jdbcTemplate.execute(
                """
                CREATE TABLE point_products (
                    id int PRIMARY KEY, name varchar(191), points_price int, status varchar(20),
                    created_at datetime, updated_at datetime
                ) ENGINE=InnoDB
                """);
        jdbcTemplate.execute(
                """
                CREATE TABLE point_codes (
                    id int PRIMARY KEY, product_id int, code_ciphertext text, code_digest varchar(191),
                    status varchar(20), delivered_at datetime, created_at datetime, updated_at datetime
                ) ENGINE=InnoDB
                """);
        jdbcTemplate.execute(
                """
                CREATE TABLE point_ledgers (
                    id int PRIMARY KEY, user_id int, delta int, balance_after int, type varchar(32),
                    source_key varchar(191), reason varchar(191), operator_admin_id int, created_at datetime
                ) ENGINE=InnoDB
                """);
        jdbcTemplate.execute(
                """
                CREATE TABLE point_redemptions (
                    id int PRIMARY KEY, user_id int, product_id int, code_id int, request_key varchar(191),
                    points_cost int, created_at datetime
                ) ENGINE=InnoDB
                """);
        jdbcTemplate.update(
                """
                INSERT INTO point_products VALUES
                    (1, 'alpha', 10, 'ON_SALE', '2026-09-01', '2026-09-01'),
                    (2, 'alpha extra', 20, 'OFF_SALE', '2026-09-02', '2026-09-02'),
                    (3, 'beta', 30, 'ON_SALE', '2026-09-03', '2026-09-03')
                """);
        jdbcTemplate.update(
                """
                INSERT INTO point_codes VALUES
                    (1, 1, 'cipher-1', 'digest-1', 'AVAILABLE', NULL, '2026-09-01', '2026-09-01'),
                    (2, 1, 'cipher-2', 'digest-2', 'DELIVERED', '2026-09-02', '2026-09-02', '2026-09-02'),
                    (3, 2, 'cipher-3', 'digest-3', 'AVAILABLE', NULL, '2026-09-03', '2026-09-03')
                """);
        jdbcTemplate.update(
                """
                INSERT INTO point_ledgers VALUES
                    (1, 1, 10, 10, 'COURSE_COMPLETION', 'course:alpha', NULL, NULL, '2026-09-01'),
                    (2, 1, -5, 5, 'MANUAL_ADJUSTMENT', 'manual:two', 'alpha reason', 7, '2026-09-02'),
                    (3, 2, -10, 20, 'REDEMPTION', 'redeem:three', NULL, NULL, '2026-09-02')
                """);
        jdbcTemplate.update(
                """
                INSERT INTO point_redemptions VALUES
                    (1, 1, 1, 1, 'request-1', 10, '2026-09-01'),
                    (2, 1, 2, 2, 'request-2', 20, '2026-09-02'),
                    (3, 2, 1, 3, 'request-3', 30, '2026-09-02')
                """);
    }

    @Test
    void filtersProductsAndPreservesDescendingPagesAndStatusMapping() {
        assertThat(products.paginate(null, null, 0, 10))
                .extracting(PointProduct::getId)
                .containsExactly(3, 2, 1);
        assertThat(products.paginate("", null, 1, 1))
                .extracting(PointProduct::getId)
                .containsExactly(2);
        assertThat(products.paginateCount("", null)).isEqualTo(3);
        assertThat(products.paginate("alpha", null, 0, 10))
                .extracting(PointProduct::getId)
                .containsExactly(2, 1);
        assertThat(products.paginateCount("alpha", null)).isEqualTo(2);
        assertThat(products.paginate(null, PointProductStatus.ON_SALE, 0, 10))
                .extracting(PointProduct::getId)
                .containsExactly(3, 1);
        var result = products.paginate("alpha", PointProductStatus.ON_SALE, 0, 10);
        assertThat(result).extracting(PointProduct::getId).containsExactly(1);
        assertThat(products.paginateCount("alpha", PointProductStatus.ON_SALE)).isEqualTo(1);
        assertThat(result.get(0).getStatus()).isEqualTo(PointProductStatus.ON_SALE);
        assertThat(result.get(0).getPointsPrice()).isEqualTo(10);
        assertThat(result.get(0).getCreatedAt()).isNotNull();
        assertThat(products.paginateCount("missing", null)).isZero();
    }

    @Test
    void filtersCodesByProductStatusAndExactDigestAndMapsCiphertext() {
        assertThat(codes.paginate(null, null, "", 0, 10))
                .extracting(PointCode::getId)
                .containsExactly(3, 2, 1);
        assertThat(codes.paginate(null, null, null, 1, 1))
                .extracting(PointCode::getId)
                .containsExactly(2);
        assertThat(codes.paginateCount(null, null, "")).isEqualTo(3);
        assertThat(codes.paginate(1, null, null, 0, 10))
                .extracting(PointCode::getId)
                .containsExactly(2, 1);
        assertThat(codes.paginateCount(1, null, null)).isEqualTo(2);
        assertThat(codes.paginate(null, PointCodeStatus.AVAILABLE, null, 0, 10))
                .extracting(PointCode::getId)
                .containsExactly(3, 1);
        assertThat(codes.paginateCount(null, PointCodeStatus.AVAILABLE, null)).isEqualTo(2);
        assertThat(codes.paginate(null, null, "digest-2", 0, 10))
                .extracting(PointCode::getId)
                .containsExactly(2);
        var result = codes.paginate(1, PointCodeStatus.DELIVERED, "digest-2", 0, 10);
        assertThat(result).extracting(PointCode::getId).containsExactly(2);
        assertThat(codes.paginateCount(1, PointCodeStatus.DELIVERED, "digest-2")).isEqualTo(1);
        assertThat(result.get(0).getStatus()).isEqualTo(PointCodeStatus.DELIVERED);
        assertThat(result.get(0).getProductId()).isEqualTo(1);
        assertThat(result.get(0).getCodeCiphertext()).isEqualTo("cipher-2");
        assertThat(result.get(0).getDeliveredAt()).isNotNull();
        assertThat(codes.paginateCount(null, null, "digest")).isZero();
    }

    @Test
    void filtersLedgersIncludingKeywordAlternativesAndInclusiveTimeBounds() {
        assertThat(ledgers.paginate(null, null, null, "", "", "", 0, 10))
                .extracting(PointLedger::getId)
                .containsExactly(3, 2, 1);
        assertThat(ledgers.paginate(null, null, null, null, null, null, 1, 1))
                .extracting(PointLedger::getId)
                .containsExactly(2);
        assertThat(ledgers.paginateCount(null, null, null, "", "", "")).isEqualTo(3);
        assertThat(ledgers.paginate(1, null, null, null, null, null, 0, 10))
                .extracting(PointLedger::getId)
                .containsExactly(2, 1);
        assertThat(ledgers.paginateCount(1, null, null, null, null, null)).isEqualTo(2);
        assertThat(
                        ledgers.paginate(
                                null, PointLedgerType.REDEMPTION, null, null, null, null, 0, 10))
                .extracting(PointLedger::getId)
                .containsExactly(3);
        assertThat(ledgers.paginate(null, null, 7, null, null, null, 0, 10))
                .extracting(PointLedger::getId)
                .containsExactly(2);
        assertThat(ledgers.paginate(null, null, null, "alpha", null, null, 0, 10))
                .extracting(PointLedger::getId)
                .containsExactly(2, 1);
        assertThat(ledgers.paginateCount(null, null, null, "alpha", null, null)).isEqualTo(2);
        assertThat(ledgers.paginateCount(null, null, null, null, "2026-09-02", null)).isEqualTo(2);
        assertThat(ledgers.paginateCount(null, null, null, null, null, "2026-09-01")).isEqualTo(1);
        var result =
                ledgers.paginate(
                        1,
                        PointLedgerType.MANUAL_ADJUSTMENT,
                        7,
                        "alpha",
                        "2026-09-02",
                        "2026-09-02",
                        0,
                        10);
        assertThat(result).extracting(PointLedger::getId).containsExactly(2);
        assertThat(
                        ledgers.paginateCount(
                                1,
                                PointLedgerType.MANUAL_ADJUSTMENT,
                                7,
                                "alpha",
                                "2026-09-02",
                                "2026-09-02"))
                .isEqualTo(1);
        assertThat(result.get(0).getType()).isEqualTo(PointLedgerType.MANUAL_ADJUSTMENT);
        assertThat(result.get(0).getBalanceAfter()).isEqualTo(5);
        assertThat(result.get(0).getOperatorAdminId()).isEqualTo(7);
        assertThat(ledgers.paginateCount(null, null, null, "missing", null, null)).isZero();
    }

    @Test
    void filtersRedemptionsAndOrdersEqualTimestampsByDescendingId() {
        assertThat(redemptions.paginate(null, null, null, "", "", 0, 10))
                .extracting(PointRedemption::getId)
                .containsExactly(3, 2, 1);
        assertThat(redemptions.paginate(null, null, null, null, null, 1, 1))
                .extracting(PointRedemption::getId)
                .containsExactly(2);
        assertThat(redemptions.paginateCount(null, null, null, "", "")).isEqualTo(3);
        assertThat(redemptions.paginate(1, null, null, null, null, 0, 10))
                .extracting(PointRedemption::getId)
                .containsExactly(2, 1);
        assertThat(redemptions.paginateCount(1, null, null, null, null)).isEqualTo(2);
        assertThat(redemptions.paginate(null, 1, null, null, null, 0, 10))
                .extracting(PointRedemption::getId)
                .containsExactly(3, 1);
        assertThat(redemptions.paginateCount(null, 1, null, null, null)).isEqualTo(2);
        assertThat(redemptions.paginate(null, null, 2, null, null, 0, 10))
                .extracting(PointRedemption::getId)
                .containsExactly(2);
        assertThat(redemptions.paginateCount(null, null, null, "2026-09-02", null)).isEqualTo(2);
        assertThat(redemptions.paginateCount(null, null, null, null, "2026-09-01")).isEqualTo(1);
        var result = redemptions.paginate(1, 2, 2, "2026-09-02", "2026-09-02", 0, 10);
        assertThat(result).extracting(PointRedemption::getId).containsExactly(2);
        assertThat(redemptions.paginateCount(1, 2, 2, "2026-09-02", "2026-09-02")).isEqualTo(1);
        assertThat(result.get(0).getRequestKey()).isEqualTo("request-2");
        assertThat(result.get(0).getPointsCost()).isEqualTo(20);
        assertThat(result.get(0).getCodeId()).isEqualTo(2);
        assertThat(redemptions.paginateCount(99, null, null, null, null)).isZero();
    }

    @SpringBootConfiguration
    @EnableAutoConfiguration
    @MapperScan("xyz.playedu.points.mapper")
    static class TestApplication {}
}
