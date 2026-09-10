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

class HistoricalPointsMigrationSchemaDefinitionTest {

    @Test
    void registersDurableMigrationStateAndOneTimeLearnerSummaries() {
        List<Map<String, String>> migrations =
                MigrationCheck.TABLE_SQL.stream()
                        .filter(
                                migration ->
                                        List.of(
                                                        "point_migration_state",
                                                        "point_historical_reward_summaries")
                                                .contains(migration.get("table")))
                        .toList();

        assertThat(migrations)
                .extracting(migration -> migration.get("name"))
                .containsExactly(
                        "20260910_00_00_05_point_migration_state",
                        "20260910_00_00_06_point_historical_reward_summaries");
        assertThat(sqlFor(migrations, "point_migration_state"))
                .contains(
                        "PRIMARY KEY (`migration_key`)",
                        "legacy_credit1_non_zero_user_count",
                        "historical_course_completion_count",
                        "expected_reward_total");
        assertThat(sqlFor(migrations, "point_historical_reward_summaries"))
                .contains(
                        "PRIMARY KEY (`user_id`)",
                        "completion_count",
                        "points_awarded",
                        "acknowledged_at");
    }

    private String sqlFor(List<Map<String, String>> migrations, String table) {
        return migrations.stream()
                .filter(migration -> table.equals(migration.get("table")))
                .findFirst()
                .orElseThrow()
                .get("sql");
    }
}
