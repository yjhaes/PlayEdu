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
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import java.time.LocalDate;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import xyz.playedu.course.service.impl.DailyLearningRankingService;

@SpringJUnitConfig(DailyLearningRankingAsyncEventListenerTest.TestApplication.class)
class DailyLearningRankingAsyncEventListenerTest {

    @Autowired private DailyLearningDurationEventPublisher durationEventPublisher;

    @Autowired private DailyLearningRankingService rankingService;

    @Test
    void publishesOnlyAfterCommitAndInvokesTheListenerAsynchronously() throws Exception {
        LocalDate learningDate = LocalDate.of(2026, 9, 9);
        CountDownLatch projectionStarted = new CountDownLatch(1);
        doAnswer(
                        invocation -> {
                            projectionStarted.countDown();
                            return null;
                        })
                .when(rankingService)
                .project(7, learningDate, 1_000L);

        TransactionSynchronizationManager.initSynchronization();
        TransactionSynchronizationManager.setActualTransactionActive(true);
        try {
            durationEventPublisher.publishAfterCommit(7, learningDate, 1_000L);
            assertThat(projectionStarted.await(0, TimeUnit.MILLISECONDS)).isFalse();
            for (TransactionSynchronization synchronization :
                    TransactionSynchronizationManager.getSynchronizations()) {
                synchronization.afterCommit();
            }
        } finally {
            TransactionSynchronizationManager.clearSynchronization();
            TransactionSynchronizationManager.setActualTransactionActive(false);
        }

        assertThat(projectionStarted.await(5, TimeUnit.SECONDS)).isTrue();
        verify(rankingService).project(7, learningDate, 1_000L);
    }

    @Configuration(proxyBeanMethods = false)
    @EnableAsync
    @Import({DailyLearningDurationEventPublisher.class, DailyLearningRankingEventListener.class})
    static class TestApplication {

        @Bean
        DailyLearningRankingService rankingService() {
            return mock(DailyLearningRankingService.class);
        }

        @Bean
        MeterRegistry meterRegistry() {
            return new SimpleMeterRegistry();
        }
    }
}
