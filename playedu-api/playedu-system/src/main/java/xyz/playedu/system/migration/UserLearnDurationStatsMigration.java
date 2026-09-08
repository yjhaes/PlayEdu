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
package xyz.playedu.system.migration;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

/** Consolidates historical daily-duration rows before installing their natural key. */
@Component
public class UserLearnDurationStatsMigration {

    public static final String MIGRATION_NAME =
            "20260908_00_00_00_user_learn_duration_stats_unique_user_date";

    private static final String TABLE_NAME = "user_learn_duration_stats";
    private static final String UNIQUE_INDEX = "uk_user_learn_duration_stats_user_date";

    public void migrate(JdbcTemplate jdbcTemplate) {
        jdbcTemplate.update(
                """
                UPDATE %s AS stats
                JOIN (
                    SELECT *
                    FROM (
                        SELECT user_id, created_date, MIN(id) AS canonical_id, SUM(duration) AS total_duration
                        FROM %s
                        GROUP BY user_id, created_date
                    ) AS aggregated_rows
                ) AS daily_totals ON stats.id = daily_totals.canonical_id
                SET stats.duration = daily_totals.total_duration
                """
                        .formatted(TABLE_NAME, TABLE_NAME));
        jdbcTemplate.update(
                """
                DELETE duplicate_stats
                FROM %s AS duplicate_stats
                JOIN %s AS canonical_stats
                  ON duplicate_stats.user_id = canonical_stats.user_id
                 AND duplicate_stats.created_date = canonical_stats.created_date
                 AND duplicate_stats.id > canonical_stats.id
                """
                        .formatted(TABLE_NAME, TABLE_NAME));
        if (!hasUniqueIndex(jdbcTemplate)) {
            jdbcTemplate.execute(
                    "ALTER TABLE %s ADD UNIQUE KEY %s (user_id, created_date)"
                            .formatted(TABLE_NAME, UNIQUE_INDEX));
        }
    }

    private boolean hasUniqueIndex(JdbcTemplate jdbcTemplate) {
        Integer count =
                jdbcTemplate.queryForObject(
                        """
                        SELECT COUNT(*)
                        FROM information_schema.statistics
                        WHERE table_schema = DATABASE()
                          AND table_name = ?
                          AND index_name = ?
                          AND non_unique = 0
                        """,
                        Integer.class,
                        TABLE_NAME,
                        UNIQUE_INDEX);
        return count != null && count > 0;
    }
}
