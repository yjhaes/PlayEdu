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

import java.util.List;
import java.util.Optional;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import xyz.playedu.common.exception.ServiceException;

/** JDBC-backed state for the one-time points rollout migration. */
@Service
public class HistoricalPointsMigrationStateServiceImpl implements PointsMigrationStateService {

    public static final String MIGRATION_KEY =
            "20260910_00_00_05_reset_credit1_and_backfill_historical_completions";

    private final JdbcTemplate jdbcTemplate;

    public HistoricalPointsMigrationStateServiceImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Optional<HistoricalPointsBackfillReport> completedReport() {
        try {
            List<HistoricalPointsBackfillReport> reports =
                    jdbcTemplate.query(
                            """
                            SELECT legacy_credit1_non_zero_user_count,
                                   legacy_credit1_total,
                                   historical_learner_count,
                                   historical_course_completion_count,
                                   expected_reward_total
                            FROM point_migration_state
                            WHERE migration_key = ?
                            """,
                            (resultSet, rowNumber) ->
                                    new HistoricalPointsBackfillReport(
                                            resultSet.getLong("legacy_credit1_non_zero_user_count"),
                                            resultSet.getLong("legacy_credit1_total"),
                                            resultSet.getLong("historical_learner_count"),
                                            resultSet.getLong("historical_course_completion_count"),
                                            resultSet.getLong("expected_reward_total")),
                            MIGRATION_KEY);
            return reports.stream().findFirst();
        } catch (DataAccessException exception) {
            // A missing state table means the points feature must remain closed.
            return Optional.empty();
        }
    }

    @Override
    public void requireHistoricalBackfillComplete() {
        if (!isHistoricalBackfillComplete()) {
            throw new ServiceException("积分历史迁移尚未完成，积分功能暂未开放");
        }
    }

    @Override
    public void markHistoricalBackfillComplete(HistoricalPointsBackfillReport report) {
        if (report == null) {
            throw new ServiceException("积分历史迁移报告不能为空");
        }
        jdbcTemplate.update(
                """
                INSERT INTO point_migration_state
                    (migration_key, completed_at, legacy_credit1_non_zero_user_count,
                     legacy_credit1_total, historical_learner_count,
                     historical_course_completion_count, expected_reward_total)
                VALUES (?, CURRENT_TIMESTAMP, ?, ?, ?, ?, ?)
                ON DUPLICATE KEY UPDATE migration_key = migration_key
                """,
                MIGRATION_KEY,
                report.legacyCredit1NonZeroUserCount(),
                report.legacyCredit1Total(),
                report.historicalLearnerCount(),
                report.historicalCourseCompletionCount(),
                report.expectedRewardTotal());
    }
}
