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
package xyz.playedu.api.controller.backend;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import xyz.playedu.api.controller.ExceptionController;
import xyz.playedu.api.request.backend.PointAdjustmentRequest;
import xyz.playedu.common.bus.BackendBus;
import xyz.playedu.common.context.BCtx;
import xyz.playedu.points.domain.PointLedger;
import xyz.playedu.points.domain.PointLedgerType;
import xyz.playedu.points.service.PointBalanceChange;
import xyz.playedu.points.service.PointBalanceChangeResult;
import xyz.playedu.points.service.PointBalanceService;
import xyz.playedu.points.service.PointCodeService;
import xyz.playedu.points.service.PointLedgerService;
import xyz.playedu.points.service.PointProductService;
import xyz.playedu.points.service.PointRedemptionService;

class PointAdminControllerTest {

    private BackendBus backendBus;
    private PointBalanceService balanceService;
    private PointProductService productService;
    private PointCodeService codeService;
    private PointLedgerService ledgerService;
    private PointRedemptionService redemptionService;
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        backendBus = org.mockito.Mockito.mock(BackendBus.class);
        balanceService = org.mockito.Mockito.mock(PointBalanceService.class);
        productService = org.mockito.Mockito.mock(PointProductService.class);
        codeService = org.mockito.Mockito.mock(PointCodeService.class);
        ledgerService = org.mockito.Mockito.mock(PointLedgerService.class);
        redemptionService = org.mockito.Mockito.mock(PointRedemptionService.class);
        mockMvc =
                MockMvcBuilders.standaloneSetup(
                                new PointAdminController(
                                        backendBus,
                                        balanceService,
                                        productService,
                                        codeService,
                                        ledgerService,
                                        redemptionService))
                        .setControllerAdvice(new ExceptionController())
                        .build();
        BCtx.setId(42);
    }

    @AfterEach
    void tearDown() {
        BCtx.remove();
    }

    @Test
    void rejectsEverySensitiveEntryForOrdinaryAdministrators() throws Exception {
        when(backendBus.isSuperAdmin()).thenReturn(false);

        mockMvc.perform(get("/backend/v1/points/products/index"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(403));
        mockMvc.perform(get("/backend/v1/points/codes/7"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(403));

        PointAdjustmentRequest request = new PointAdjustmentRequest();
        request.setUserId(7);
        request.setDelta(10);
        request.setReason("纠正导入错误");
        mockMvc.perform(
                        post("/backend/v1/points/adjust")
                                .contentType("application/json")
                                .content(new ObjectMapper().writeValueAsBytes(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(403));

        verifyNoInteractions(balanceService, productService, codeService, ledgerService);
    }

    @Test
    void sendsManualAdjustmentWithTheAuthenticatedOperatorToTheBalanceEngine() throws Exception {
        when(backendBus.isSuperAdmin()).thenReturn(true);
        PointLedger ledger = new PointLedger();
        ledger.setId(9);
        ledger.setBalanceAfter(-10);
        when(balanceService.apply(any(PointBalanceChange.class)))
                .thenReturn(new PointBalanceChangeResult(ledger, true));

        PointAdjustmentRequest request = new PointAdjustmentRequest();
        request.setUserId(7);
        request.setDelta(-10);
        request.setReason("人工纠错");
        request.setRequestKey("adjustment-1");

        mockMvc.perform(
                        post("/backend/v1/points/adjust")
                                .contentType("application/json")
                                .content(new ObjectMapper().writeValueAsBytes(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data.ledger.balance_after").value(-10))
                .andExpect(jsonPath("$.data.applied").value(true));

        ArgumentCaptor<PointBalanceChange> captor =
                ArgumentCaptor.forClass(PointBalanceChange.class);
        verify(balanceService).apply(captor.capture());
        PointBalanceChange change = captor.getValue();
        assertThat(change.userId()).isEqualTo(7);
        assertThat(change.delta()).isEqualTo(-10);
        assertThat(change.type()).isEqualTo(PointLedgerType.MANUAL_ADJUSTMENT);
        assertThat(change.reason()).isEqualTo("人工纠错");
        assertThat(change.operatorAdminId()).isEqualTo(42);
        assertThat(change.operatorIsSuperAdmin()).isTrue();
        assertThat(change.sourceKey()).isEqualTo("manual-adjustment:7:42:adjustment-1");
    }
}
