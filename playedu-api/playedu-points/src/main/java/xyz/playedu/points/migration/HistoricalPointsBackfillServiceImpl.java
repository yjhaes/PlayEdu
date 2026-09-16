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
import java.util.Map;
import java.util.TreeMap;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import xyz.playedu.common.exception.ServiceException;
import xyz.playedu.points.domain.PointLedgerType;
import xyz.playedu.points.mapper.PointBalanceMapper;
import xyz.playedu.points.service.PointBalanceService;
import xyz.playedu.points.service.PointSourceKeys;
import xyz.playedu.points.types.PointBalanceChange;
import xyz.playedu.points.types.PointBalanceChangeResult;

/** Resets legacy credit and idempotently awards currently persisted course completions. */
@Service
public class HistoricalPointsBackfillServiceImpl implements HistoricalPointsBackfillService {

    public static final int HISTORICAL_REWARD_POINTS = 10;

    private final JdbcTemplate jdbcTemplate;
    private final PointBalanceMapper pointBalanceMapper;
    private final PointBalanceService pointBalanceService;
    private final PointsMigrationStateService migrationStateService;
    private final HistoricalRewardSummaryService historicalRewardSummaryService;

    public HistoricalPointsBackfillServiceImpl(
            JdbcTemplate jdbcTemplate,
            PointBalanceMapper pointBalanceMapper,
            PointBalanceService pointBalanceService,
            PointsMigrationStateService migrationStateService,
            HistoricalRewardSummaryService historicalRewardSummaryService) {
        this.jdbcTemplate = jdbcTemplate;
        this.pointBalanceMapper = pointBalanceMapper;
        this.pointBalanceService = pointBalanceService;
        this.migrationStateService = migrationStateService;
        this.historicalRewardSummaryService = historicalRewardSummaryService;
    }

    @Override
    public HistoricalPointsBackfillReport dryRun() {
        return reportFor(currentCompletions());
    }

    @Override
    @Transactional
    public HistoricalPointsBackfillReport resetAndBackfill(boolean backupConfirmed) {
        HistoricalPointsBackfillReport completedReport =
                migrationStateService.completedReport().orElse(null);
        if (completedReport != null) {
            return completedReport;
        }
        if (!backupConfirmed) {
            throw new ServiceException("清零旧积分前必须确认已完成可恢复数据库备份");
        }

        List<HistoricalCourseCompletion> completions = currentCompletions();
        HistoricalPointsBackfillReport report = reportFor(completions);

        // The old value is intentionally discarded. Existing point ledgers, if any, are
        // authoritative new-system facts and are rebuilt after the destructive reset.
        pointBalanceMapper.resetLegacyCredit1();
        pointBalanceMapper.rebuildCredit1FromPointLedger();

        Map<Integer, Long> awardedByUser = new TreeMap<>();
        for (HistoricalCourseCompletion completion : completions) {
            PointBalanceChangeResult result =
                    pointBalanceService.apply(
                            new PointBalanceChange(
                                    completion.userId(),
                                    HISTORICAL_REWARD_POINTS,
                                    PointLedgerType.HISTORICAL_COURSE_COMPLETION,
                                    PointSourceKeys.courseCompletion(
                                            completion.userId(), completion.courseId()),
                                    null,
                                    null,
                                    false));
            if (result.applied()) {
                awardedByUser.merge(completion.userId(), 1L, Long::sum);
            }
        }

        for (Map.Entry<Integer, Long> entry : awardedByUser.entrySet()) {
            long pointsAwarded;
            try {
                pointsAwarded = Math.multiplyExact(entry.getValue(), HISTORICAL_REWARD_POINTS);
            } catch (ArithmeticException exception) {
                throw new ServiceException("历史积分汇总超出整数范围", exception);
            }
            historicalRewardSummaryService.store(entry.getKey(), entry.getValue(), pointsAwarded);
        }
        migrationStateService.markHistoricalBackfillComplete(report);
        return report;
    }

    private HistoricalPointsBackfillReport reportFor(List<HistoricalCourseCompletion> completions) {
        Number legacyUserCount =
                jdbcTemplate.queryForObject(
                        "SELECT COUNT(*) FROM users WHERE credit1 <> 0", Number.class);
        Number legacyTotal =
                jdbcTemplate.queryForObject(
                        "SELECT COALESCE(SUM(credit1), 0) FROM users WHERE credit1 <> 0",
                        Number.class);
        long expectedRewardTotal;
        try {
            expectedRewardTotal =
                    Math.multiplyExact((long) completions.size(), HISTORICAL_REWARD_POINTS);
        } catch (ArithmeticException exception) {
            throw new ServiceException("预计补发积分超出整数范围", exception);
        }

        return new HistoricalPointsBackfillReport(
                legacyUserCount.longValue(),
                legacyTotal.longValue(),
                completions.stream().map(HistoricalCourseCompletion::userId).distinct().count(),
                completions.size(),
                expectedRewardTotal);
    }

    private List<HistoricalCourseCompletion> currentCompletions() {
        return jdbcTemplate.query(
                """
                SELECT DISTINCT records.user_id, records.course_id
                FROM user_course_records records
                INNER JOIN users ON users.id = records.user_id
                WHERE records.is_finished = 1
                ORDER BY records.user_id, records.course_id
                """,
                (resultSet, rowNumber) ->
                        new HistoricalCourseCompletion(
                                resultSet.getInt("user_id"), resultSet.getInt("course_id")));
    }

    private record HistoricalCourseCompletion(Integer userId, Integer courseId) {}
}
