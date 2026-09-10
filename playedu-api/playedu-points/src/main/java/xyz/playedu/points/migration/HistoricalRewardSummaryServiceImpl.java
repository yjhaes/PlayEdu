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
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import xyz.playedu.common.exception.ServiceException;

/** Stores and atomically consumes one aggregate historical-reward notice per learner. */
@Service
public class HistoricalRewardSummaryServiceImpl implements HistoricalRewardSummaryService {

    private final JdbcTemplate jdbcTemplate;
    private final PointsMigrationStateService migrationStateService;

    public HistoricalRewardSummaryServiceImpl(
            JdbcTemplate jdbcTemplate, PointsMigrationStateService migrationStateService) {
        this.jdbcTemplate = jdbcTemplate;
        this.migrationStateService = migrationStateService;
    }

    @Override
    public void store(Integer userId, long completionCount, long pointsAwarded) {
        if (userId == null || completionCount <= 0 || pointsAwarded <= 0) {
            throw new ServiceException("历史积分汇总参数无效");
        }
        jdbcTemplate.update(
                """
                INSERT INTO point_historical_reward_summaries
                    (user_id, completion_count, points_awarded, created_at)
                VALUES (?, ?, ?, CURRENT_TIMESTAMP)
                ON DUPLICATE KEY UPDATE
                    completion_count = VALUES(completion_count),
                    points_awarded = VALUES(points_awarded)
                """,
                userId,
                completionCount,
                pointsAwarded);
    }

    @Override
    @Transactional
    public Optional<HistoricalRewardSummary> claimForDisplay(Integer userId) {
        if (userId == null) {
            throw new ServiceException("领取历史积分汇总必须提供学员ID");
        }
        migrationStateService.requireHistoricalBackfillComplete();

        List<HistoricalRewardSummary> summaries =
                jdbcTemplate.query(
                        """
                        SELECT user_id, completion_count, points_awarded
                        FROM point_historical_reward_summaries
                        WHERE user_id = ? AND acknowledged_at IS NULL
                        FOR UPDATE
                        """,
                        (resultSet, rowNumber) ->
                                new HistoricalRewardSummary(
                                        resultSet.getInt("user_id"),
                                        resultSet.getLong("completion_count"),
                                        resultSet.getLong("points_awarded")),
                        userId);
        if (summaries.isEmpty()) {
            return Optional.empty();
        }

        if (jdbcTemplate.update(
                        """
                        UPDATE point_historical_reward_summaries
                        SET acknowledged_at = CURRENT_TIMESTAMP
                        WHERE user_id = ? AND acknowledged_at IS NULL
                        """,
                        userId)
                != 1) {
            throw new ServiceException("历史积分汇总状态更新失败");
        }
        return Optional.of(summaries.get(0));
    }
}
