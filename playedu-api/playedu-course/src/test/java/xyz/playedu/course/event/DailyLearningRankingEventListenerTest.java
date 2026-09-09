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
package xyz.playedu.course.event;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;

import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import xyz.playedu.course.service.impl.DailyLearningRankingService;

class DailyLearningRankingEventListenerTest {

    @Test
    void recordsProjectionFailureWithoutPropagatingAfterMySqlCommit() {
        DailyLearningRankingService rankingService = mock(DailyLearningRankingService.class);
        SimpleMeterRegistry meterRegistry = new SimpleMeterRegistry();
        DailyLearningRankingEventListener listener =
                new DailyLearningRankingEventListener(rankingService, meterRegistry);
        LocalDate learningDate = LocalDate.of(2026, 9, 9);
        DailyLearningDurationConfirmedEvent event =
                new DailyLearningDurationConfirmedEvent(this, 7, learningDate, 1_000L);
        doThrow(new IllegalStateException("Redis is unavailable"))
                .when(rankingService)
                .project(7, learningDate, 1_000L);

        listener.project(event);

        assertThat(
                        meterRegistry
                                .get(DailyLearningRankingEventListener.PROJECTION_FAILURE_METRIC)
                                .counter()
                                .count())
                .isEqualTo(1);
    }
}
