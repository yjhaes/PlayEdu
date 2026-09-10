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

import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

class PointsSchemaMigrationDefinitionTest {

    private static final List<String> POINT_TABLES =
            List.of("point_ledgers", "point_products", "point_codes", "point_redemptions");

    @Test
    void registersThePointsSchemaWithItsRequiredConstraints() {
        List<Map<String, String>> pointMigrations =
                MigrationCheck.TABLE_SQL.stream()
                        .filter(migration -> POINT_TABLES.contains(migration.get("table")))
                        .toList();

        assertThat(pointMigrations).hasSize(4);
        assertThat(pointMigrations)
                .extracting(migration -> migration.get("name"))
                .containsExactly(
                        "20260910_00_00_00_point_ledgers",
                        "20260910_00_00_01_point_products",
                        "20260910_00_00_02_point_codes",
                        "20260910_00_00_03_point_redemptions");
        assertThat(MigrationCheck.TABLE_SQL)
                .extracting(migration -> migration.get("name"))
                .doesNotHaveDuplicates();

        String ledgerSql = sqlFor(pointMigrations, "point_ledgers");
        assertThat(ledgerSql)
                .contains(
                        "UNIQUE KEY `uk_point_ledgers_source_key` (`source_key`)",
                        "KEY `idx_point_ledgers_user_created_id` (`user_id`, `created_at`, `id`)");

        String productSql = sqlFor(pointMigrations, "point_products");
        assertThat(productSql).contains("ON_SALE", "OFF_SALE");

        String codeSql = sqlFor(pointMigrations, "point_codes");
        assertThat(codeSql)
                .contains(
                        "UNIQUE KEY `uk_point_codes_code_digest` (`code_digest`)",
                        "AVAILABLE",
                        "DELIVERED")
                .doesNotContain(
                        "`user_id`", "`batch`", "`batch_id`", "`expires_at`", "`revoked_at`");

        String redemptionSql = sqlFor(pointMigrations, "point_redemptions");
        assertThat(redemptionSql).contains("UNIQUE KEY `uk_point_redemptions_code_id` (`code_id`)");
    }

    private String sqlFor(List<Map<String, String>> migrations, String table) {
        return migrations.stream()
                .filter(migration -> table.equals(migration.get("table")))
                .findFirst()
                .orElseThrow()
                .get("sql");
    }
}
