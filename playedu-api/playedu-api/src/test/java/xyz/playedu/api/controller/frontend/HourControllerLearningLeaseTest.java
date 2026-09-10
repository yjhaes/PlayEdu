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

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import xyz.playedu.api.controller.ExceptionController;
import xyz.playedu.common.context.FCtx;
import xyz.playedu.common.redis.LearningLeaseUnavailableException;
import xyz.playedu.course.bus.UserBus;
import xyz.playedu.course.domain.CourseHour;
import xyz.playedu.course.service.ActiveLearningLeaseService;
import xyz.playedu.course.service.CourseHourService;

class HourControllerLearningLeaseTest {

    private final ActiveLearningLeaseService activeLearningLeaseService =
            mock(ActiveLearningLeaseService.class);

    private UserBus userBus;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() throws Exception {
        HourController controller = new HourController();
        CourseHourService hourService = mock(CourseHourService.class);
        userBus = mock(UserBus.class);
        CourseHour hour = new CourseHour();
        hour.setId(9);
        hour.setDuration(100);
        when(hourService.findOrFail(9, 8)).thenReturn(hour);
        when(userBus.canSeeCourse(7, 8)).thenReturn(true);
        ReflectionTestUtils.setField(controller, "hourService", hourService);
        ReflectionTestUtils.setField(controller, "userBus", userBus);
        ReflectionTestUtils.setField(
                controller, "activeLearningLeaseService", activeLearningLeaseService);
        mockMvc =
                MockMvcBuilders.standaloneSetup(controller)
                        .setControllerAdvice(new ExceptionController())
                        .build();
        FCtx.setId(7);
    }

    @AfterEach
    void tearDown() {
        FCtx.remove();
    }

    @Test
    void returnsTheServerIssuedSessionAndAStableConflictCode() throws Exception {
        when(activeLearningLeaseService.heartbeat(7, 8, 9, null, 100))
                .thenReturn(
                        new ActiveLearningLeaseService.HeartbeatResult(
                                ActiveLearningLeaseService.Outcome.CREATED, "session-1", 0, 8, 9));

        mockMvc.perform(
                        post("/api/v1/course/8/hour/9/ping")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data.session_id").value("session-1"))
                .andExpect(jsonPath("$.data.added_duration").value(0));

        when(activeLearningLeaseService.heartbeat(7, 8, 9, "session-2", 100))
                .thenReturn(
                        new ActiveLearningLeaseService.HeartbeatResult(
                                ActiveLearningLeaseService.Outcome.CONFLICT,
                                "session-1",
                                0,
                                8,
                                10));

        mockMvc.perform(
                        post("/api/v1/course/8/hour/9/ping")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{\"session_id\":\"session-2\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(40901))
                .andExpect(jsonPath("$.data.active_hour_id").value(10));
    }

    @Test
    void refusesAnOldSessionToStopTheCurrentLease() throws Exception {
        when(activeLearningLeaseService.stop(7, 8, 9, "old-session"))
                .thenReturn(ActiveLearningLeaseService.Outcome.INVALID_SESSION);

        mockMvc.perform(
                        delete("/api/v1/course/8/hour/9/ping")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{\"session_id\":\"old-session\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(40902));
    }

    @Test
    void returnsTheCompletionRewardOnlyWhenItWasActuallyIssued() throws Exception {
        when(activeLearningLeaseService.heartbeat(7, 8, 9, null, 100))
                .thenReturn(
                        new ActiveLearningLeaseService.HeartbeatResult(
                                ActiveLearningLeaseService.Outcome.CONTINUED,
                                "session-1",
                                10,
                                8,
                                9,
                                10));

        mockMvc.perform(
                        post("/api/v1/course/8/hour/9/ping")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.earned_points").value(10));
    }

    @Test
    void exposesRedisLeaseFailuresAsRetryableServiceUnavailable() throws Exception {
        when(activeLearningLeaseService.heartbeat(7, 8, 9, null, 100))
                .thenThrow(
                        new LearningLeaseUnavailableException(
                                "Redis unavailable", new RuntimeException()));

        mockMvc.perform(
                        post("/api/v1/course/8/hour/9/ping")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{}"))
                .andExpect(status().isServiceUnavailable())
                .andExpect(jsonPath("$.code").value(503));
    }

    @Test
    void rechecksAuthoritativeCourseAccessForEachProgressRequest() throws Exception {
        when(userBus.canSeeCourse(7, 8)).thenReturn(true, false);

        mockMvc.perform(post("/api/v1/course/8/hour/9/record"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(400));

        mockMvc.perform(post("/api/v1/course/8/hour/9/record"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(1))
                .andExpect(jsonPath("$.msg").value("无权限观看"));

        verify(userBus, times(2)).canSeeCourse(7, 8);
    }
}
