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
package xyz.playedu.api.controller.frontend;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;
import xyz.playedu.common.context.FCtx;
import xyz.playedu.common.redis.RedisKeyspace;
import xyz.playedu.common.redis.RedisRuntimeConfiguration;
import xyz.playedu.course.bus.UserBus;
import xyz.playedu.course.domain.CourseHour;
import xyz.playedu.course.service.ActiveLearningLeaseService;
import xyz.playedu.course.service.CourseHourService;
import xyz.playedu.course.service.CourseService;
import xyz.playedu.course.service.LearningFactPersistenceService;
import xyz.playedu.course.service.UserCourseHourRecordService;
import xyz.playedu.resource.service.ResourceService;

@SpringBootTest(classes = HourControllerLearningLeaseRedisIntegrationTest.TestApplication.class)
@AutoConfigureMockMvc
@Testcontainers(disabledWithoutDocker = true)
class HourControllerLearningLeaseRedisIntegrationTest {

    @Container
    static final GenericContainer<?> REDIS =
            new GenericContainer<>(DockerImageName.parse("redis:7.4-alpine"))
                    .withExposedPorts(6379);

    @Autowired private MockMvc mockMvc;

    @Autowired private ObjectMapper objectMapper;

    @Autowired private MutableClock clock;

    @Autowired private StringRedisTemplate redisTemplate;

    @Autowired private RedisKeyspace keyspace;

    @Autowired private LearningFactPersistenceService learningFactPersistenceService;

    @Autowired private CourseHourService courseHourService;

    @MockBean private UserBus userBus;

    @DynamicPropertySource
    static void redisProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.redis.host", REDIS::getHost);
        registry.add("spring.data.redis.port", () -> REDIS.getMappedPort(6379));
        registry.add("spring.data.redis.timeout", () -> "2s");
        registry.add("playedu.redis.key-prefix", () -> "playedu:test");
    }

    @BeforeEach
    void setUp() throws Exception {
        redisTemplate.getConnectionFactory().getConnection().serverCommands().flushDb();
        clock.set(Instant.parse("2026-09-09T00:00:00Z"));
        FCtx.setId(7);
        when(userBus.canSeeCourse(7, 8)).thenReturn(true);
        when(courseHourService.findOrFail(9, 8)).thenReturn(hour(9));
        when(courseHourService.findOrFail(10, 8)).thenReturn(hour(10));
    }

    @AfterEach
    void tearDown() {
        FCtx.remove();
    }

    @Test
    void confirmsTheLeaseAcrossHttpRedisAndTheAuthoritativePersistenceSeam() throws Exception {
        String sessionId = sessionId(postHeartbeat(9, "{}"));
        assertThat(redisTemplate.getExpire(keyspace.key("learning-lease", "7")))
                .isBetween(29L, 30L);

        clock.advance(Duration.ofSeconds(10));
        mockMvc.perform(
                        post("/api/v1/course/8/hour/9/ping")
                                .contentType("application/json")
                                .content("{\"session_id\":\"" + sessionId + "\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data.added_duration").value(10));
        verify(learningFactPersistenceService).recordIncrement(7, 8, 9, 10, 100);

        mockMvc.perform(
                        post("/api/v1/course/8/hour/10/ping")
                                .contentType("application/json")
                                .content("{}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(40901))
                .andExpect(jsonPath("$.data.active_hour_id").value(9));

        mockMvc.perform(
                        post("/api/v1/course/8/hour/9/ping")
                                .contentType("application/json")
                                .content("{\"session_id\":\"forged\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(40902));

        redisTemplate.expire(keyspace.key("learning-lease", "7"), Duration.ZERO);
        String replacementSessionId = sessionId(postHeartbeat(10, "{}"));
        assertThat(replacementSessionId).isNotEqualTo(sessionId);

        mockMvc.perform(
                        delete("/api/v1/course/8/hour/9/ping")
                                .contentType("application/json")
                                .content("{\"session_id\":\"" + sessionId + "\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(40902));
    }

    private MvcResult postHeartbeat(Integer hourId, String requestBody) throws Exception {
        return mockMvc.perform(
                        post("/api/v1/course/8/hour/" + hourId + "/ping")
                                .contentType("application/json")
                                .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andReturn();
    }

    private String sessionId(MvcResult result) throws Exception {
        JsonNode response = objectMapper.readTree(result.getResponse().getContentAsString());
        return response.path("data").path("session_id").asText();
    }

    private CourseHour hour(Integer id) {
        CourseHour hour = new CourseHour();
        hour.setId(id);
        hour.setDuration(100);
        return hour;
    }

    @SpringBootConfiguration
    @EnableAutoConfiguration(exclude = DataSourceAutoConfiguration.class)
    @Import({
        HourController.class,
        RedisRuntimeConfiguration.class,
        RedisKeyspace.class,
        TestDependencies.class
    })
    static class TestApplication {}

    @TestConfiguration
    static class TestDependencies {

        @Bean
        MutableClock clock() {
            return new MutableClock();
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

        @Bean
        CourseService courseService() {
            return mock(CourseService.class);
        }

        @Bean
        CourseHourService courseHourService() {
            return mock(CourseHourService.class);
        }

        @Bean
        ResourceService resourceService() {
            return mock(ResourceService.class);
        }

        @Bean
        UserCourseHourRecordService userCourseHourRecordService() {
            return mock(UserCourseHourRecordService.class);
        }

        @Bean
        LearningFactPersistenceService learningFactPersistenceService() {
            return mock(LearningFactPersistenceService.class);
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
        public ZoneId getZone() {
            return ZoneOffset.UTC;
        }

        @Override
        public Clock withZone(ZoneId zone) {
            return this;
        }

        @Override
        public Instant instant() {
            return instant;
        }
    }
}
