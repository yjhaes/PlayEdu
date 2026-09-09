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

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import xyz.playedu.course.service.impl.DailyLearningRankingService;

/** Restores missing product-visible ranking buckets after the application has started. */
@Component
@Order(20)
@Slf4j
public class DailyLearningRankingStartupRebuilder implements CommandLineRunner {

    private final DailyLearningRankingService rankingService;

    public DailyLearningRankingStartupRebuilder(DailyLearningRankingService rankingService) {
        this.rankingService = rankingService;
    }

    @Override
    public void run(String... args) {
        try {
            rankingService.rebuildMissing();
        } catch (RuntimeException exception) {
            log.error("learning_ranking_startup_rebuild_failed", exception);
            throw exception;
        }
    }
}
