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
package xyz.playedu.course.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;
import xyz.playedu.common.redis.RedisKeyspace;
import xyz.playedu.common.redis.RedisRuntimeConfiguration;

@SpringBootTest(classes = ActiveLearningLeaseServiceIntegrationTest.TestApplication.class)
@Testcontainers(disabledWithoutDocker = true)
class ActiveLearningLeaseServiceIntegrationTest {

    @Container
    static final GenericContainer<?> REDIS =
            new GenericContainer<>(DockerImageName.parse("redis:7.4-alpine"))
                    .withExposedPorts(6379);

    @Autowired private ActiveLearningLeaseService activeLearningLeaseService;

    @Autowired private LearningFactPersistenceService learningFactPersistenceService;

    @Autowired private RedisKeyspace keyspace;

    @Autowired private StringRedisTemplate redisTemplate;

    @Autowired private MutableClock clock;

    @DynamicPropertySource
    static void redisProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.redis.host", REDIS::getHost);
        registry.add("spring.data.redis.port", () -> REDIS.getMappedPort(6379));
        registry.add("spring.data.redis.timeout", () -> "2s");
        registry.add("playedu.redis.key-prefix", () -> "playedu:test");
    }

    @BeforeEach
    void setUp() {
        redisTemplate.getConnectionFactory().getConnection().serverCommands().flushDb();
        clock.set(Instant.parse("2026-09-09T00:00:00Z"));
        when(learningFactPersistenceService.recordIncrement(
                        anyInt(), anyInt(), anyInt(), anyInt(), anyInt()))
                .thenReturn(LearningFactPersistenceResult.NO_REWARD);
    }

    @Test
    void confirmsOneContinuousLeaseAndRejectsConflictingOrStaleSessions() {
        ActiveLearningLeaseService.HeartbeatResult created =
                activeLearningLeaseService.heartbeat(7, 8, 9, null, 100);

        assertThat(created.outcome()).isEqualTo(ActiveLearningLeaseService.Outcome.CREATED);
        assertThat(created.sessionId()).isNotBlank();
        assertThat(created.addedDuration()).isZero();
        assertThat(redisTemplate.getExpire(keyspace.key("learning-lease", "7")))
                .isBetween(29L, 30L);

        clock.advance(Duration.ofSeconds(10));
        ActiveLearningLeaseService.HeartbeatResult continuedLease =
                activeLearningLeaseService.heartbeat(7, 8, 9, created.sessionId(), 100);

        assertThat(continuedLease.outcome())
                .isEqualTo(ActiveLearningLeaseService.Outcome.CONTINUED);
        assertThat(continuedLease.addedDuration()).isEqualTo(10);
        verify(learningFactPersistenceService).recordIncrement(7, 8, 9, 10, 100);

        ActiveLearningLeaseService.HeartbeatResult conflict =
                activeLearningLeaseService.heartbeat(7, 8, 10, null, 100);
        assertThat(conflict.outcome()).isEqualTo(ActiveLearningLeaseService.Outcome.CONFLICT);
        assertThat(conflict.activeCourseId()).isEqualTo(8);
        assertThat(conflict.activeHourId()).isEqualTo(9);

        ActiveLearningLeaseService.HeartbeatResult forged =
                activeLearningLeaseService.heartbeat(7, 8, 9, "forged-session", 100);
        assertThat(forged.outcome()).isEqualTo(ActiveLearningLeaseService.Outcome.INVALID_SESSION);

        assertThat(activeLearningLeaseService.stop(7, 8, 9, "forged-session"))
                .isEqualTo(ActiveLearningLeaseService.Outcome.INVALID_SESSION);
        assertThat(activeLearningLeaseService.stop(7, 8, 9, created.sessionId()))
                .isEqualTo(ActiveLearningLeaseService.Outcome.STOPPED);
        verifyNoMoreInteractions(learningFactPersistenceService);
    }

    @Test
    void doesNotCountEarlyOrBrokenIntervalsAndLetsAnotherHourStartAfterExpiry() {
        ActiveLearningLeaseService.HeartbeatResult created =
                activeLearningLeaseService.heartbeat(7, 8, 9, null, 100);

        assertThat(
                        activeLearningLeaseService
                                .heartbeat(7, 8, 9, created.sessionId(), 100)
                                .outcome())
                .isEqualTo(ActiveLearningLeaseService.Outcome.TOO_EARLY);

        clock.advance(Duration.ofSeconds(21));
        assertThat(
                        activeLearningLeaseService
                                .heartbeat(7, 8, 9, created.sessionId(), 100)
                                .outcome())
                .isEqualTo(ActiveLearningLeaseService.Outcome.BASELINE_RESET);
        verifyNoMoreInteractions(learningFactPersistenceService);

        redisTemplate.expire(keyspace.key("learning-lease", "7"), Duration.ZERO);
        ActiveLearningLeaseService.HeartbeatResult replacement =
                activeLearningLeaseService.heartbeat(7, 8, 10, null, 100);

        assertThat(replacement.outcome()).isEqualTo(ActiveLearningLeaseService.Outcome.CREATED);
        assertThat(replacement.sessionId()).isNotEqualTo(created.sessionId());
    }

    @SpringBootConfiguration
    @EnableAutoConfiguration(exclude = DataSourceAutoConfiguration.class)
    @Import({RedisRuntimeConfiguration.class, RedisKeyspace.class, TestDependencies.class})
    static class TestApplication {}

    @TestConfiguration
    static class TestDependencies {

        @Bean
        MutableClock clock() {
            return new MutableClock();
        }

        @Bean
        LearningFactPersistenceService learningFactPersistenceService() {
            return mock(LearningFactPersistenceService.class);
        }

        @Bean
        ActiveLearningLeaseService activeLearningLeaseService(
                StringRedisTemplate redisTemplate,
                RedisKeyspace keyspace,
                LearningFactPersistenceService learningFactPersistenceService,
                MutableClock clock) {
            return new ActiveLearningLeaseService(
                    redisTemplate,
                    keyspace,
                    learningFactPersistenceService,
                    clock,
                    Duration.ofSeconds(30),
                    Duration.ofSeconds(10),
                    Duration.ofSeconds(20));
        }
    }

    static class MutableClock extends Clock {
        private Instant instant = Instant.parse("2026-09-09T00:00:00Z");

        void set(Instant instant) {
            this.instant = instant;
        }

        void advance(Duration duration) {
            instant = instant.plus(duration);
        }

        @Override
        public ZoneOffset getZone() {
            return ZoneOffset.UTC;
        }

        @Override
        public Clock withZone(java.time.ZoneId zone) {
            return this;
        }

        @Override
        public Instant instant() {
            return instant;
        }
    }
}
