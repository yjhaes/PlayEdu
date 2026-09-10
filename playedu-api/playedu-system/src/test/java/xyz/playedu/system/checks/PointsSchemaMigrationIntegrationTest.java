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
package xyz.playedu.system.checks;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers(disabledWithoutDocker = true)
class PointsSchemaMigrationIntegrationTest {

    private static final List<String> POINT_TABLES =
            List.of("point_ledgers", "point_products", "point_codes", "point_redemptions");

    @Container static final MySQLContainer<?> MYSQL = new MySQLContainer<>("mysql:8.0");

    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName(MYSQL.getDriverClassName());
        dataSource.setUrl(MYSQL.getJdbcUrl());
        dataSource.setUsername(MYSQL.getUsername());
        dataSource.setPassword(MYSQL.getPassword());
        jdbcTemplate = new JdbcTemplate(dataSource);

        jdbcTemplate.execute("DROP TABLE IF EXISTS point_redemptions");
        jdbcTemplate.execute("DROP TABLE IF EXISTS point_codes");
        jdbcTemplate.execute("DROP TABLE IF EXISTS point_products");
        jdbcTemplate.execute("DROP TABLE IF EXISTS point_ledgers");
    }

    @Test
    void createsThePointsSchemaOnceAndEnforcesItsDataInvariants() {
        List<Map<String, String>> migrations = pointMigrations();
        assertThat(migrations).hasSize(5);

        Set<String> appliedMigrations = new HashSet<>();
        applyUnappliedMigrations(migrations, appliedMigrations);
        applyUnappliedMigrations(migrations, appliedMigrations);

        assertThat(tableNames()).containsExactlyInAnyOrderElementsOf(POINT_TABLES);
        assertThat(indexColumns("point_ledgers", "uk_point_ledgers_source_key"))
                .containsExactly("source_key");
        assertThat(indexColumns("point_ledgers", "idx_point_ledgers_user_created_id"))
                .containsExactly("user_id", "created_at", "id");
        assertThat(indexColumns("point_codes", "uk_point_codes_code_digest"))
                .containsExactly("code_digest");
        assertThat(indexColumns("point_redemptions", "uk_point_redemptions_code_id"))
                .containsExactly("code_id");
        assertThat(indexColumns("point_redemptions", "uk_point_redemptions_user_request_key"))
                .containsExactly("user_id", "request_key");
        assertThat(columnNames("point_codes"))
                .doesNotContain(
                        "user_id", "batch", "batch_id", "expires_at", "revoked_at", "valid_until");

        jdbcTemplate.update(
                """
                INSERT INTO point_ledgers (user_id, delta, balance_after, type, source_key)
                VALUES (1, 10, 10, 'COURSE_COMPLETION', 'course-completion:1:1')
                """);
        assertThatThrownBy(
                        () ->
                                jdbcTemplate.update(
                                        """
                                        INSERT INTO point_ledgers
                                            (user_id, delta, balance_after, type, source_key)
                                        VALUES (1, 10, 20, 'COURSE_COMPLETION', 'course-completion:1:1')
                                        """))
                .isInstanceOf(DataAccessException.class);

        jdbcTemplate.update(
                """
                INSERT INTO point_products (id, name, points_price, status)
                VALUES (1, '试运行兑换商品', 50, 'ON_SALE')
                """);
        assertThatThrownBy(
                        () ->
                                jdbcTemplate.update(
                                        """
                                        INSERT INTO point_products (id, name, points_price, status)
                                        VALUES (2, '无效状态商品', 50, 'on_sale')
                                        """))
                .isInstanceOf(DataAccessException.class);

        jdbcTemplate.update(
                """
                INSERT INTO point_codes (id, product_id, code_ciphertext, code_digest, status)
                VALUES (1, 1, 'ciphertext', 'digest-1', 'AVAILABLE')
                """);
        assertThatThrownBy(
                        () ->
                                jdbcTemplate.update(
                                        """
                                        INSERT INTO point_codes
                                            (id, product_id, code_ciphertext, code_digest, status)
                                        VALUES (2, 1, 'ciphertext', 'digest-2', 'delivered')
                                        """))
                .isInstanceOf(DataAccessException.class);
        assertThatThrownBy(
                        () ->
                                jdbcTemplate.update(
                                        """
                                        INSERT INTO point_codes
                                            (id, product_id, code_ciphertext, code_digest, status)
                                        VALUES (3, 1, 'ciphertext', 'digest-1', 'AVAILABLE')
                                        """))
                .isInstanceOf(DataAccessException.class);

        jdbcTemplate.update(
                """
                INSERT INTO point_redemptions
                    (id, user_id, product_id, code_id, request_key, points_cost)
                VALUES (1, 1, 1, 1, 'request-1', 50)
                """);
        assertThatThrownBy(
                        () ->
                                jdbcTemplate.update(
                                        """
                                        INSERT INTO point_redemptions
                                            (id, user_id, product_id, code_id, request_key, points_cost)
                                        VALUES (2, 2, 1, 1, 'request-2', 50)
                                        """))
                .isInstanceOf(DataAccessException.class);
        assertThatThrownBy(
                        () ->
                                jdbcTemplate.update(
                                        """
                                        INSERT INTO point_redemptions
                                            (id, user_id, product_id, code_id, request_key, points_cost)
                                        VALUES (3, 1, 1, 2, 'request-1', 50)
                                        """))
                .isInstanceOf(DataAccessException.class);
    }

    private void applyUnappliedMigrations(
            List<Map<String, String>> migrations, Set<String> appliedMigrations) {
        for (Map<String, String> migration : migrations) {
            if (appliedMigrations.add(migration.get("name"))) {
                jdbcTemplate.execute(migration.get("sql"));
            }
        }
    }

    private List<Map<String, String>> pointMigrations() {
        return MigrationCheck.TABLE_SQL.stream()
                .filter(migration -> POINT_TABLES.contains(migration.get("table")))
                .toList();
    }

    private List<String> tableNames() {
        return jdbcTemplate.queryForList("SHOW TABLES", String.class);
    }

    private List<String> indexColumns(String table, String index) {
        return jdbcTemplate.queryForList(
                """
                SELECT column_name
                FROM information_schema.statistics
                WHERE table_schema = DATABASE()
                  AND table_name = ?
                  AND index_name = ?
                ORDER BY seq_in_index
                """,
                String.class,
                table,
                index);
    }

    private List<String> columnNames(String table) {
        return jdbcTemplate.queryForList(
                """
                SELECT column_name
                FROM information_schema.columns
                WHERE table_schema = DATABASE()
                  AND table_name = ?
                """,
                String.class,
                table);
    }
}
