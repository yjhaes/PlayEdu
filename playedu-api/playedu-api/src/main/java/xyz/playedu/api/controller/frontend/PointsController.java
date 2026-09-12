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

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import org.apache.commons.collections4.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import xyz.playedu.api.request.frontend.PointRedemptionRequest;
import xyz.playedu.common.context.FCtx;
import xyz.playedu.common.domain.User;
import xyz.playedu.common.exception.NotFoundException;
import xyz.playedu.common.exception.ServiceException;
import xyz.playedu.common.service.UserService;
import xyz.playedu.common.types.JsonResponse;
import xyz.playedu.common.types.paginate.PaginationResult;
import xyz.playedu.points.domain.PointLedger;
import xyz.playedu.points.domain.PointLedgerType;
import xyz.playedu.points.domain.PointProduct;
import xyz.playedu.points.domain.PointRedemption;
import xyz.playedu.points.migration.HistoricalRewardSummary;
import xyz.playedu.points.migration.HistoricalRewardSummaryService;
import xyz.playedu.points.migration.PointsFeatureGate;
import xyz.playedu.points.service.PointCodeService;
import xyz.playedu.points.service.PointLedgerService;
import xyz.playedu.points.service.PointProductService;
import xyz.playedu.points.service.PointRedemptionService;

/** Learner-facing points center APIs shared by the PC and H5 clients. */
@RestController
@RequestMapping("/api/v1/points")
public class PointsController {

    private static final int COURSE_COMPLETION_REWARD_POINTS = 10;

    private static final List<String> RULE_DESCRIPTIONS =
            List.of(
                    "首次完成任一可学习课程获得10积分，同一学员同一课程只奖励一次，重学或重置不会重复获得。",
                    "积分不会仅因时间经过而失效；商品售罄或下架也不影响课程完成奖励。",
                    "每次兑换只交付一枚兑换码，兑换成功后不可取消或退回积分。",
                    "兑换码失效后不提供系统内补码、售后或退回积分。",
                    "仅积分超级管理员的人工扣分可以产生负积分余额，后续课程完成奖励会自然抵扣。",
                    "积分不能购买、转让、提现或兑换现金等价物。");

    private final UserService userService;
    private final PointsFeatureGate pointsFeatureGate;
    private final HistoricalRewardSummaryService historicalRewardSummaryService;
    private final PointProductService productService;
    private final PointCodeService codeService;
    private final PointLedgerService ledgerService;
    private final PointRedemptionService redemptionService;

    @Autowired
    public PointsController(
            UserService userService,
            PointsFeatureGate pointsFeatureGate,
            HistoricalRewardSummaryService historicalRewardSummaryService,
            PointProductService productService,
            PointCodeService codeService,
            PointLedgerService ledgerService,
            PointRedemptionService redemptionService) {
        this.userService = userService;
        this.pointsFeatureGate = pointsFeatureGate;
        this.historicalRewardSummaryService = historicalRewardSummaryService;
        this.productService = productService;
        this.codeService = codeService;
        this.ledgerService = ledgerService;
        this.redemptionService = redemptionService;
    }

    @GetMapping("/summary")
    public JsonResponse summary() {
        pointsFeatureGate.requireOpen();
        Integer userId = currentLearnerId();
        User user = userService.find(userId);
        if (user == null) {
            throw new ServiceException("学员不存在");
        }
        ensureUnlocked(user);

        Optional<HistoricalRewardSummary> pendingSummary =
                historicalRewardSummaryService.pendingForDisplay(userId);
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("credit1", user.getCredit1());
        data.put("historical_reward_summary", pendingSummary.orElse(null));
        data.put("historical_reward_summary_pending", pendingSummary.isPresent());
        return JsonResponse.data(data);
    }

    @PostMapping({"/historical-reward-summary/acknowledge", "/historical-reward-summary/ack"})
    public JsonResponse acknowledgeHistoricalReward() {
        pointsFeatureGate.requireOpen();
        Integer userId = currentLearnerId();
        historicalRewardSummaryService.acknowledge(userId);
        return JsonResponse.data(Map.of("acknowledged", true));
    }

    @GetMapping({"/ledgers/index", "/ledgers"})
    public JsonResponse ledgers(@RequestParam HashMap<String, Object> params) {
        pointsFeatureGate.requireOpen();
        Integer userId = currentLearnerId();
        PaginationResult<PointLedger> result =
                ledgerService.paginate(
                        page(params),
                        size(params),
                        userId,
                        ledgerType(MapUtils.getString(params, "type")),
                        null,
                        null,
                        null,
                        null);
        return pageData(learnerLedgerResult(result));
    }

    @GetMapping({"/products/index", "/products"})
    public JsonResponse products(@RequestParam HashMap<String, Object> params) {
        pointsFeatureGate.requireOpen();
        currentLearnerId();
        // Both on-sale and off-sale products remain visible; the status and exact inventory
        // tell the learner whether a new redemption can currently be made.
        return pageData(productService.paginate(page(params), size(params), null, null));
    }

    @GetMapping("/products/{id}")
    public JsonResponse product(@PathVariable Integer id) throws NotFoundException {
        pointsFeatureGate.requireOpen();
        currentLearnerId();
        PointProduct product = productService.findOrFail(id);
        product.setAvailableCount(productService.availableCount(id));
        return JsonResponse.data(product);
    }

    @PostMapping({"/redeem", "/products/{productId}/redeem"})
    public JsonResponse redeem(
            @PathVariable(name = "productId", required = false) Integer pathProductId,
            @RequestBody(required = false) PointRedemptionRequest request,
            @RequestHeader(name = "Idempotency-Key", required = false) String headerRequestKey)
            throws NotFoundException {
        pointsFeatureGate.requireOpen();
        Integer userId = currentLearnerId();
        Integer productId =
                pathProductId != null
                        ? pathProductId
                        : request == null ? null : request.getProductId();
        if (productId == null) {
            throw new ServiceException("兑换必须提供商品ID");
        }

        String requestKey = request == null ? null : request.getRequestKey();
        if (requestKey == null || requestKey.isBlank()) {
            requestKey = headerRequestKey;
        }
        if (requestKey == null || requestKey.isBlank()) {
            throw new ServiceException("兑换必须提供幂等键");
        }
        requestKey = requestKey.strip();
        if (requestKey.length() > 160) {
            throw new ServiceException("兑换幂等键长度不能超过160个字符");
        }

        PointRedemption redemption = redemptionService.redeem(userId, productId, requestKey);
        return JsonResponse.data(deliveredRedemptionData(redemption));
    }

    @GetMapping({"/redemptions/index", "/redemptions"})
    public JsonResponse redemptions(@RequestParam HashMap<String, Object> params) {
        pointsFeatureGate.requireOpen();
        Integer userId = currentLearnerId();
        PaginationResult<PointRedemption> result =
                redemptionService.paginate(
                        page(params), size(params), userId, null, null, null, null);
        return pageData(result);
    }

    @GetMapping({"/redemptions/{id}", "/redemptions/{id}/detail"})
    public JsonResponse redemption(@PathVariable Integer id) throws NotFoundException {
        pointsFeatureGate.requireOpen();
        Integer userId = currentLearnerId();
        PointRedemption redemption = redemptionService.findForUser(id, userId);
        return JsonResponse.data(deliveredRedemptionData(redemption));
    }

    @GetMapping({"/rules", "/rules/index"})
    public JsonResponse rules() {
        pointsFeatureGate.requireOpen();
        currentLearnerId();
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("course_completion_reward_points", COURSE_COMPLETION_REWARD_POINTS);
        data.put("points_never_expire", true);
        data.put("redemption_cancelable", false);
        data.put("invalid_code_after_delivery_support", false);
        data.put("manual_deduction_can_create_negative_balance", true);
        data.put("descriptions", RULE_DESCRIPTIONS);
        return JsonResponse.data(data);
    }

    private Map<String, Object> deliveredRedemptionData(PointRedemption redemption)
            throws NotFoundException {
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("redemption", redemption);
        data.put("code", codeService.reveal(redemption.getCodeId()));
        return data;
    }

    private Integer currentLearnerId() {
        Integer userId = FCtx.getId();
        if (userId == null) {
            throw new ServiceException("请登录后查看积分");
        }
        User contextUser = FCtx.getUser();
        if (contextUser == null) {
            contextUser = userService.find(userId);
        }
        if (contextUser != null) {
            ensureUnlocked(contextUser);
        }
        return userId;
    }

    private void ensureUnlocked(User user) {
        if (Objects.equals(user.getIsLock(), 1)) {
            throw new ServiceException("当前学员已锁定无法访问积分");
        }
    }

    private JsonResponse pageData(PaginationResult<?> result) {
        HashMap<String, Object> data = new HashMap<>();
        data.put("data", result.getData());
        data.put("total", result.getTotal());
        return JsonResponse.data(data);
    }

    private PaginationResult<Map<String, Object>> learnerLedgerResult(
            PaginationResult<PointLedger> result) {
        PaginationResult<Map<String, Object>> learnerResult = new PaginationResult<>();
        List<PointLedger> ledgers = result.getData() == null ? List.of() : result.getData();
        learnerResult.setData(ledgers.stream().map(this::learnerLedgerData).toList());
        learnerResult.setTotal(result.getTotal());
        return learnerResult;
    }

    private Map<String, Object> learnerLedgerData(PointLedger ledger) {
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("delta", ledger.getDelta());
        data.put("balance_after", ledger.getBalanceAfter());
        data.put("type", ledger.getType());
        data.put("reason", ledger.getReason());
        data.put("created_at", ledger.getCreatedAt());
        return data;
    }

    private int page(HashMap<String, Object> params) {
        return Math.max(MapUtils.getIntValue(params, "page", 1), 1);
    }

    private int size(HashMap<String, Object> params) {
        return Math.min(Math.max(MapUtils.getIntValue(params, "size", 10), 1), 100);
    }

    private PointLedgerType ledgerType(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        try {
            return PointLedgerType.valueOf(
                    value.strip().replace('-', '_').toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException exception) {
            throw new ServiceException("积分流水类型无效");
        }
    }
}
