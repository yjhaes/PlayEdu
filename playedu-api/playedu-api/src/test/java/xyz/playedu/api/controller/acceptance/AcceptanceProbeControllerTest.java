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
package xyz.playedu.api.controller.acceptance;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.mock.env.MockEnvironment;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import xyz.playedu.api.controller.ExceptionController;
import xyz.playedu.common.redis.RedisDistributedLock;
import xyz.playedu.course.service.ActiveLearningLeaseService;
import xyz.playedu.course.service.impl.DailyLearningRankingService;

class AcceptanceProbeControllerTest {

    private RedisDistributedLock distributedLock;
    private ActiveLearningLeaseService activeLearningLeaseService;
    private DailyLearningRankingService dailyLearningRankingService;
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        distributedLock = mock(RedisDistributedLock.class);
        activeLearningLeaseService = mock(ActiveLearningLeaseService.class);
        dailyLearningRankingService = mock(DailyLearningRankingService.class);
        AcceptanceProbeController controller =
                new AcceptanceProbeController(
                        new MockEnvironment().withProperty("playedu.instance-id", "api-1"),
                        distributedLock,
                        activeLearningLeaseService,
                        dailyLearningRankingService);
        mockMvc =
                MockMvcBuilders.standaloneSetup(controller)
                        .setControllerAdvice(new ExceptionController())
                        .build();
    }

    @Test
    void exposesTheConfiguredInstanceId() throws Exception {
        mockMvc.perform(get("/acceptance/v1/instance"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data.instance_id").value("api-1"));
    }

    @Test
    void rejectsInvalidLockRequestsBeforeCallingRedis() throws Exception {
        mockMvc.perform(
                        post("/acceptance/v1/lock")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(40001));

        verifyNoInteractions(distributedLock);
    }

    @Test
    void mapsAnActiveLearningConflictToTheAcceptanceContract() throws Exception {
        when(activeLearningLeaseService.heartbeat(7001, 8001, 9001, "session-b", 3600))
                .thenReturn(
                        new ActiveLearningLeaseService.HeartbeatResult(
                                ActiveLearningLeaseService.Outcome.CONFLICT,
                                "session-a",
                                0,
                                8001,
                                9001));

        mockMvc.perform(
                        post("/acceptance/v1/lease/heartbeat")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        "{\"userId\":7001,\"courseId\":8001,"
                                                + "\"hourId\":9001,\"sessionId\":\"session-b\","
                                                + "\"hourDuration\":3600}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(40901))
                .andExpect(jsonPath("$.data.outcome").value("CONFLICT"))
                .andExpect(jsonPath("$.data.active_hour_id").value(9001))
                .andExpect(jsonPath("$.data.instance_id").value("api-1"));
    }

    @Test
    void servesBothRankingWindowsFromTheRankingService() throws Exception {
        when(dailyLearningRankingService.todayTop10()).thenReturn(List.of());
        when(dailyLearningRankingService.yesterdayTop10()).thenReturn(List.of());

        mockMvc.perform(get("/acceptance/v1/ranking"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data.today").isEmpty())
                .andExpect(jsonPath("$.data.yesterday").isEmpty());
    }
}
