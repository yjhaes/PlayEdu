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

import java.util.Locale;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/** Explicit startup entry point for the read-only or destructive points migration. */
@Component
@Order(20)
@Slf4j
public class HistoricalPointsBackfillRunner implements CommandLineRunner {

    private final HistoricalPointsBackfillService backfillService;
    private final String mode;
    private final boolean backupConfirmed;

    public HistoricalPointsBackfillRunner(
            HistoricalPointsBackfillService backfillService,
            @Value("${playedu.points.migration.mode:disabled}") String mode,
            @Value("${playedu.points.migration.backup-confirmed:false}") boolean backupConfirmed) {
        this.backfillService = backfillService;
        this.mode = mode;
        this.backupConfirmed = backupConfirmed;
    }

    @Override
    public void run(String... args) {
        switch (normalizedMode()) {
            case "disabled" -> {}
            case "dry-run", "dryrun" -> logReport("积分历史迁移干跑报告", backfillService.dryRun());
            case "execute", "backfill" -> logReport(
                    "积分历史迁移完成报告", backfillService.resetAndBackfill(backupConfirmed));
            default -> throw new IllegalStateException(
                    "不支持的积分迁移模式: " + mode + "，可选值为 disabled、dry-run、execute");
        }
    }

    private String normalizedMode() {
        if (mode == null || mode.isBlank()) {
            return "disabled";
        }
        return mode.strip().toLowerCase(Locale.ROOT).replace('_', '-');
    }

    private void logReport(String prefix, HistoricalPointsBackfillReport report) {
        log.info(
                "{}: legacy_credit1_non_zero_user_count={}, legacy_credit1_total={}, "
                        + "historical_learner_count={}, historical_course_completion_count={}, "
                        + "expected_reward_total={}",
                prefix,
                report.legacyCredit1NonZeroUserCount(),
                report.legacyCredit1Total(),
                report.historicalLearnerCount(),
                report.historicalCourseCompletionCount(),
                report.expectedRewardTotal());
    }
}
