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

import java.util.Optional;

/** Stores and acknowledges the aggregate historical-reward notice for each learner. */
public interface HistoricalRewardSummaryService {

    void store(Integer userId, long completionCount, long pointsAwarded);

    /** Reads a pending notice without marking it as acknowledged. */
    Optional<HistoricalRewardSummary> pendingForDisplay(Integer userId);

    /** Marks the current learner's pending notice as acknowledged, idempotently. */
    void acknowledge(Integer userId);

    /** Legacy atomic read-and-acknowledge seam used by migration-level callers. */
    Optional<HistoricalRewardSummary> claimForDisplay(Integer userId);
}
