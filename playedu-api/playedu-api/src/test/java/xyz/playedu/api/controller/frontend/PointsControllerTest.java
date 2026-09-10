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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Optional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import xyz.playedu.api.controller.ExceptionController;
import xyz.playedu.common.context.FCtx;
import xyz.playedu.common.domain.User;
import xyz.playedu.common.service.UserService;
import xyz.playedu.points.migration.HistoricalRewardSummary;
import xyz.playedu.points.migration.HistoricalRewardSummaryService;
import xyz.playedu.points.migration.PointsFeatureGate;

class PointsControllerTest {

    private UserService userService;
    private PointsFeatureGate pointsFeatureGate;
    private HistoricalRewardSummaryService historicalRewardSummaryService;
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        userService = mock(UserService.class);
        pointsFeatureGate = mock(PointsFeatureGate.class);
        historicalRewardSummaryService = mock(HistoricalRewardSummaryService.class);
        mockMvc =
                MockMvcBuilders.standaloneSetup(
                                new PointsController(
                                        userService,
                                        pointsFeatureGate,
                                        historicalRewardSummaryService))
                        .setControllerAdvice(new ExceptionController())
                        .build();
        FCtx.setId(7);
    }

    @AfterEach
    void tearDown() {
        FCtx.remove();
    }

    @Test
    void returnsTheCurrentBalanceAndConsumesTheHistoricalNoticeOnce() throws Exception {
        User user = new User();
        user.setId(7);
        user.setCredit1(20);
        when(userService.find(7)).thenReturn(user);
        when(historicalRewardSummaryService.claimForDisplay(7))
                .thenReturn(Optional.of(new HistoricalRewardSummary(7, 2, 20)));

        mockMvc.perform(get("/api/v1/points/summary"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data.credit1").value(20))
                .andExpect(jsonPath("$.data.historical_reward_summary.completion_count").value(2))
                .andExpect(jsonPath("$.data.historical_reward_summary.points_awarded").value(20));

        verify(pointsFeatureGate).requireOpen();
        verify(historicalRewardSummaryService).claimForDisplay(7);
    }

    @Test
    void returnsNoHistoricalNoticeAfterItHasAlreadyBeenAcknowledged() throws Exception {
        User user = new User();
        user.setId(7);
        user.setCredit1(0);
        when(userService.find(7)).thenReturn(user);
        when(historicalRewardSummaryService.claimForDisplay(7)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/v1/points/summary"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.credit1").value(0))
                .andExpect(jsonPath("$.data.historical_reward_summary").doesNotExist());
    }
}
