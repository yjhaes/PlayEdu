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

import java.util.List;
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
import xyz.playedu.points.crypto.PointCodeCryptoService;
import xyz.playedu.points.domain.PointCodeStatus;
import xyz.playedu.points.domain.PointProductStatus;
import xyz.playedu.points.service.impl.PointCodeServiceImpl;
import xyz.playedu.points.service.impl.PointProductServiceImpl;
import xyz.playedu.points.types.PointCodeImportLineStatus;
import xyz.playedu.points.types.PointCodeImportResult;

@SpringBootTest(classes = PointCodeInventoryIntegrationTest.TestApplication.class)
@Testcontainers(disabledWithoutDocker = true)
class PointCodeInventoryIntegrationTest {

    private static final String KEY = "integration-code-encryption-key";

    @Container static final MySQLContainer<?> MYSQL = new MySQLContainer<>("mysql:8.0");

    @Autowired private JdbcTemplate jdbcTemplate;

    @Autowired private PointCodeService pointCodeService;

    @Autowired private PointProductService pointProductService;

    @Autowired private PointCodeCryptoService cryptoService;

    @DynamicPropertySource
    static void databaseProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", MYSQL::getJdbcUrl);
        registry.add("spring.datasource.username", MYSQL::getUsername);
        registry.add("spring.datasource.password", MYSQL::getPassword);
        registry.add("spring.datasource.driver-class-name", MYSQL::getDriverClassName);
        registry.add("playedu.points.code-encryption-key", () -> KEY);
    }

    @BeforeEach
    void setUp() {
        jdbcTemplate.execute("DROP TABLE IF EXISTS point_codes");
        jdbcTemplate.execute("DROP TABLE IF EXISTS point_products");
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
                "INSERT INTO point_products (id, name, points_price, status, created_at,"
                        + " updated_at) VALUES (1, '试运行商品', 50, 'ON_SALE', NOW(), NOW())");
    }

    @Test
    void encryptsReplenishesAndSkipsDuplicateCodesWithExactInventory() throws Exception {
        PointCodeImportResult first = pointCodeService.importCodes(1, " CODE-1 \n\nCODE-2 ");
        PointCodeImportResult replenishment =
                pointCodeService.importCodes(1, List.of("CODE-2", " CODE-3 "));

        assertThat(first.getImportedCount()).isEqualTo(2);
        assertThat(first.getDuplicateCount()).isZero();
        assertThat(replenishment.getImportedCount()).isEqualTo(1);
        assertThat(replenishment.getDuplicateCount()).isEqualTo(1);
        assertThat(replenishment.getResults().get(0).getStatus())
                .isEqualTo(PointCodeImportLineStatus.DUPLICATE);
        assertThat(pointCodeService.availableCount(1)).isEqualTo(3);
        assertThat(jdbcTemplate.queryForObject("SELECT COUNT(*) FROM point_codes", Integer.class))
                .isEqualTo(3);

        String storedCiphertext =
                jdbcTemplate.queryForObject(
                        "SELECT code_ciphertext FROM point_codes WHERE code_digest = ?",
                        String.class,
                        cryptoService.digest("CODE-1"));
        assertThat(storedCiphertext).doesNotContain("CODE-1");
        assertThat(cryptoService.decrypt(storedCiphertext)).isEqualTo("CODE-1");
    }

    @Test
    void onlyAllowsDeletingAvailableCodesAndDeletingProductsWithoutDeliveredCodes()
            throws Exception {
        pointCodeService.importCodes(1, "AVAILABLE-CODE\nDELIVERED-CODE");
        Integer deliveredCodeId =
                jdbcTemplate.queryForObject(
                        "SELECT id FROM point_codes WHERE code_digest = ?",
                        Integer.class,
                        cryptoService.digest("DELIVERED-CODE"));
        jdbcTemplate.update(
                "UPDATE point_codes SET status = ?, delivered_at = NOW() WHERE id = ?",
                PointCodeStatus.DELIVERED.name(),
                deliveredCodeId);

        Integer availableCodeId =
                jdbcTemplate.queryForObject(
                        "SELECT id FROM point_codes WHERE code_digest = ?",
                        Integer.class,
                        cryptoService.digest("AVAILABLE-CODE"));
        pointCodeService.deleteAvailable(availableCodeId);
        assertThatThrownBy(() -> pointCodeService.deleteAvailable(deliveredCodeId))
                .hasMessage("已发放兑换码不可删除");
        assertThat(pointCodeService.availableCount(1)).isZero();

        assertThatThrownBy(() -> pointProductService.deleteById(1)).hasMessage("商品已有已发放兑换码，只能下架");
        assertThat(pointProductService.offSale(1).getStatus())
                .isEqualTo(PointProductStatus.OFF_SALE);
    }

    @SpringBootConfiguration
    @EnableAutoConfiguration
    @MapperScan("xyz.playedu.points.mapper")
    @Import({
        PointCodeCryptoService.class,
        PointCodeServiceImpl.class,
        PointProductServiceImpl.class
    })
    static class TestApplication {}
}
