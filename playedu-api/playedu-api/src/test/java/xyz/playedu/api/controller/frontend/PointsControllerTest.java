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
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import xyz.playedu.api.controller.ExceptionController;
import xyz.playedu.api.request.frontend.PointRedemptionRequest;
import xyz.playedu.common.context.FCtx;
import xyz.playedu.common.domain.User;
import xyz.playedu.common.exception.NotFoundException;
import xyz.playedu.common.service.UserService;
import xyz.playedu.common.types.paginate.PaginationResult;
import xyz.playedu.points.domain.PointLedger;
import xyz.playedu.points.domain.PointLedgerType;
import xyz.playedu.points.domain.PointProduct;
import xyz.playedu.points.domain.PointProductStatus;
import xyz.playedu.points.domain.PointRedemption;
import xyz.playedu.points.service.PointCodeService;
import xyz.playedu.points.service.PointLedgerService;
import xyz.playedu.points.service.PointProductService;
import xyz.playedu.points.service.PointRedemptionService;

class PointsControllerTest {

    private UserService userService;
    private PointProductService productService;
    private PointCodeService codeService;
    private PointLedgerService ledgerService;
    private PointRedemptionService redemptionService;
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        userService = mock(UserService.class);
        productService = mock(PointProductService.class);
        codeService = mock(PointCodeService.class);
        ledgerService = mock(PointLedgerService.class);
        redemptionService = mock(PointRedemptionService.class);
        mockMvc =
                MockMvcBuilders.standaloneSetup(
                                new PointsController(
                                        userService,
                                        productService,
                                        codeService,
                                        ledgerService,
                                        redemptionService))
                        .setControllerAdvice(new ExceptionController())
                        .build();
        FCtx.setId(7);
        User contextUser = new User();
        contextUser.setId(7);
        contextUser.setIsLock(0);
        FCtx.setUser(contextUser);
    }

    @AfterEach
    void tearDown() {
        FCtx.remove();
    }

    @Test
    void returnsTheCurrentBalanceWithoutMigration() throws Exception {
        User user = new User();
        user.setId(7);
        user.setCredit1(20);
        when(userService.find(7)).thenReturn(user);

        mockMvc.perform(get("/api/v1/points/summary"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data.credit1").value(20));
    }

    @Test
    void paginatesOnlyTheCurrentLearnersLedger() throws Exception {
        PointLedger ledger = new PointLedger();
        ledger.setId(9);
        ledger.setUserId(7);
        ledger.setDelta(10);
        ledger.setBalanceAfter(10);
        ledger.setType(PointLedgerType.COURSE_COMPLETION);
        PaginationResult<PointLedger> result = new PaginationResult<>();
        result.setData(List.of(ledger));
        result.setTotal(1L);
        when(ledgerService.paginate(2, 25, 7, null, null, null, null, null)).thenReturn(result);

        mockMvc.perform(get("/api/v1/points/ledgers/index?page=2&size=25"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.data[0].delta").value(10))
                .andExpect(jsonPath("$.data.data[0].balance_after").value(10))
                .andExpect(jsonPath("$.data.data[0].type").value("COURSE_COMPLETION"))
                .andExpect(jsonPath("$.data.data[0].id").doesNotExist())
                .andExpect(jsonPath("$.data.data[0].user_id").doesNotExist())
                .andExpect(jsonPath("$.data.data[0].source_key").doesNotExist())
                .andExpect(jsonPath("$.data.data[0].operator_admin_id").doesNotExist())
                .andExpect(jsonPath("$.data.total").value(1));

        verify(ledgerService).paginate(2, 25, 7, null, null, null, null, null);
    }

    @Test
    void exposesOnSaleAndSoldOutProductsWithExactInventory() throws Exception {
        PointProduct onSale = product(1, "在售商品", 50, PointProductStatus.ON_SALE, 2L);
        PointProduct soldOut = product(2, "售罄商品", 50, PointProductStatus.ON_SALE, 0L);
        PointProduct offSale = product(4, "下架商品", 50, PointProductStatus.OFF_SALE, 3L);
        PaginationResult<PointProduct> result = new PaginationResult<>();
        result.setData(List.of(onSale, soldOut, offSale));
        result.setTotal(3L);
        when(productService.paginate(1, 10, null, null)).thenReturn(result);

        mockMvc.perform(get("/api/v1/points/products/index"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.data[0].available_count").value(2))
                .andExpect(jsonPath("$.data.data[1].available_count").value(0))
                .andExpect(jsonPath("$.data.data[1].status").value("ON_SALE"))
                .andExpect(jsonPath("$.data.data[2].status").value("OFF_SALE"));

        verify(productService).paginate(1, 10, null, null);
    }

    @Test
    void returnsProductDetailWithZeroInventoryInsteadOfHidingSoldOutProduct() throws Exception {
        PointProduct product = product(3, "售罄商品", 50, PointProductStatus.ON_SALE, null);
        when(productService.findOrFail(3)).thenReturn(product);
        when(productService.availableCount(3)).thenReturn(0L);

        mockMvc.perform(get("/api/v1/points/products/3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(3))
                .andExpect(jsonPath("$.data.available_count").value(0));
    }

    @Test
    void redeemsForTheCurrentLearnerAndRevealsTheDeliveredCode() throws Exception {
        PointRedemptionRequest request = new PointRedemptionRequest();
        request.setRequestKey("request-1");
        PointRedemption redemption = new PointRedemption();
        redemption.setId(11);
        redemption.setUserId(7);
        redemption.setProductId(3);
        redemption.setCodeId(21);
        redemption.setPointsCost(50);
        when(redemptionService.redeem(7, 3, "request-1")).thenReturn(redemption);
        when(codeService.reveal(21)).thenReturn("SECRET-CODE");

        mockMvc.perform(
                        post("/api/v1/points/products/3/redeem")
                                .contentType("application/json")
                                .content(new ObjectMapper().writeValueAsBytes(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.redemption.id").value(11))
                .andExpect(jsonPath("$.data.code").value("SECRET-CODE"));

        verify(redemptionService).redeem(7, 3, "request-1");
        verify(codeService).reveal(21);
    }

    @Test
    void requiresAnIdempotencyKeyBeforeCallingTheRedemptionService() throws Exception {
        mockMvc.perform(post("/api/v1/points/products/3/redeem"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(1))
                .andExpect(jsonPath("$.msg").value("兑换必须提供幂等键"));

        verify(redemptionService, never()).redeem(7, 3, null);
    }

    @Test
    void acceptsAnIdempotencyHeaderWhenTheBodyOnlyContainsTheProduct() throws Exception {
        PointRedemptionRequest request = new PointRedemptionRequest();
        request.setProductId(3);
        PointRedemption redemption = new PointRedemption();
        redemption.setId(12);
        redemption.setUserId(7);
        redemption.setProductId(3);
        redemption.setCodeId(22);
        when(redemptionService.redeem(7, 3, "header-request")).thenReturn(redemption);
        when(codeService.reveal(22)).thenReturn("HEADER-CODE");

        mockMvc.perform(
                        post("/api/v1/points/redeem")
                                .header("Idempotency-Key", "header-request")
                                .contentType("application/json")
                                .content(new ObjectMapper().writeValueAsBytes(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.redemption.id").value(12))
                .andExpect(jsonPath("$.data.code").value("HEADER-CODE"));

        verify(redemptionService).redeem(7, 3, "header-request");
    }

    @Test
    void listsAndDetailsOnlyTheCurrentLearnersRedemptions() throws Exception {
        PointRedemption redemption = new PointRedemption();
        redemption.setId(11);
        redemption.setUserId(7);
        redemption.setProductId(3);
        redemption.setCodeId(21);
        PaginationResult<PointRedemption> result = new PaginationResult<>();
        result.setData(List.of(redemption));
        result.setTotal(1L);
        when(redemptionService.paginate(1, 10, 7, null, null, null, null)).thenReturn(result);
        when(redemptionService.findForUser(11, 7)).thenReturn(redemption);
        when(codeService.reveal(21)).thenReturn("SECRET-CODE");

        mockMvc.perform(get("/api/v1/points/redemptions/index"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.data[0].user_id").value(7))
                .andExpect(jsonPath("$.data.total").value(1));
        mockMvc.perform(get("/api/v1/points/redemptions/11"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.redemption.id").value(11))
                .andExpect(jsonPath("$.data.code").value("SECRET-CODE"));

        verify(redemptionService).paginate(1, 10, 7, null, null, null, null);
        verify(redemptionService).findForUser(11, 7);
    }

    @Test
    void rejectsAnotherLearnersRedemptionAtTheApiBoundary() throws Exception {
        when(redemptionService.findForUser(11, 7)).thenThrow(new NotFoundException("兑换记录不存在"));

        mockMvc.perform(get("/api/v1/points/redemptions/11"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(404))
                .andExpect(jsonPath("$.msg").value("兑换记录不存在"));

        verify(redemptionService).findForUser(11, 7);
        verify(codeService, never()).reveal(11);
    }

    @Test
    void doesNotAllowLockedLearnersToUsePointsApis() throws Exception {
        User lockedUser = new User();
        lockedUser.setId(7);
        lockedUser.setIsLock(1);
        FCtx.setUser(lockedUser);

        mockMvc.perform(get("/api/v1/points/ledgers/index"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(1));

        verify(ledgerService, never()).paginate(2, 25, 7, null, null, null, null, null);
    }

    @Test
    void checksTheLockWhenOnlyTheCurrentLearnerIdIsInContext() throws Exception {
        User lockedUser = new User();
        lockedUser.setId(7);
        lockedUser.setIsLock(1);
        FCtx.setUser(null);
        when(userService.find(7)).thenReturn(lockedUser);

        mockMvc.perform(get("/api/v1/points/ledgers/index"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(1));

        verify(ledgerService, never()).paginate(1, 10, 7, null, null, null, null, null);
    }

    @Test
    void returnsTheSharedPermanentPointsRules() throws Exception {
        mockMvc.perform(get("/api/v1/points/rules"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.points_never_expire").value(true))
                .andExpect(jsonPath("$.data.redemption_cancelable").value(false))
                .andExpect(jsonPath("$.data.invalid_code_after_delivery_support").value(false))
                .andExpect(
                        jsonPath("$.data.manual_deduction_can_create_negative_balance").value(true))
                .andExpect(jsonPath("$.data.descriptions").isArray());
    }

    private PointProduct product(
            int id, String name, int price, PointProductStatus status, Long availableCount) {
        PointProduct product = new PointProduct();
        product.setId(id);
        product.setName(name);
        product.setPointsPrice(price);
        product.setStatus(status);
        product.setAvailableCount(availableCount);
        return product;
    }
}
