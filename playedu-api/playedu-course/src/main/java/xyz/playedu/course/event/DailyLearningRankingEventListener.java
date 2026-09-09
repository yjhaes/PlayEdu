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

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import xyz.playedu.course.service.impl.DailyLearningRankingService;

/** Projects committed learning facts asynchronously without changing MySQL transaction results. */
@Component
@Slf4j
public class DailyLearningRankingEventListener {

    static final String PROJECTION_FAILURE_METRIC = "playedu.learning.ranking.projection.failures";

    private final DailyLearningRankingService rankingService;
    private final Counter projectionFailureCounter;

    public DailyLearningRankingEventListener(
            DailyLearningRankingService rankingService, MeterRegistry meterRegistry) {
        this.rankingService = rankingService;
        this.projectionFailureCounter =
                Counter.builder(PROJECTION_FAILURE_METRIC)
                        .description("Number of asynchronous daily learning ranking failures")
                        .register(meterRegistry);
    }

    @Async
    @EventListener
    public void project(DailyLearningDurationConfirmedEvent event) {
        try {
            rankingService.project(event.getUserId(), event.getLearningDate(), event.getDuration());
        } catch (RuntimeException exception) {
            projectionFailureCounter.increment();
            log.atError()
                    .setMessage("learning_ranking_projection_failed")
                    .addKeyValue(
                            "event_type", DailyLearningDurationConfirmedEvent.class.getSimpleName())
                    .addKeyValue("user_id", event.getUserId())
                    .addKeyValue("learning_date", event.getLearningDate())
                    .addKeyValue("duration", event.getDuration())
                    .setCause(exception)
                    .log();
        }
    }
}
