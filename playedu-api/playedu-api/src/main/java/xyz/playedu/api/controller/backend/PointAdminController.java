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

import java.util.HashMap;
import java.util.UUID;
import org.apache.commons.collections4.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import xyz.playedu.api.request.backend.PointAdjustmentRequest;
import xyz.playedu.api.request.backend.PointCodeImportRequest;
import xyz.playedu.api.request.backend.PointProductRequest;
import xyz.playedu.common.annotation.Log;
import xyz.playedu.common.bus.BackendBus;
import xyz.playedu.common.constant.BusinessTypeConstant;
import xyz.playedu.common.context.BCtx;
import xyz.playedu.common.exception.NotFoundException;
import xyz.playedu.common.exception.ServiceException;
import xyz.playedu.common.types.JsonResponse;
import xyz.playedu.common.types.paginate.PaginationResult;
import xyz.playedu.points.domain.PointCode;
import xyz.playedu.points.domain.PointCodeStatus;
import xyz.playedu.points.domain.PointLedger;
import xyz.playedu.points.domain.PointLedgerType;
import xyz.playedu.points.domain.PointProduct;
import xyz.playedu.points.domain.PointProductStatus;
import xyz.playedu.points.domain.PointRedemption;
import xyz.playedu.points.service.PointBalanceService;
import xyz.playedu.points.service.PointCodeService;
import xyz.playedu.points.service.PointLedgerService;
import xyz.playedu.points.service.PointProductService;
import xyz.playedu.points.service.PointRedemptionService;
import xyz.playedu.points.service.PointSourceKeys;
import xyz.playedu.points.types.PointBalanceChange;
import xyz.playedu.points.types.PointBalanceChangeResult;
import xyz.playedu.points.types.PointCodeImportResult;

/** Super-admin-only operations for points, products and voucher-code inventory. */
@RestController
@RequestMapping("/backend/v1/points")
public class PointAdminController {

    private final BackendBus backendBus;
    private final PointBalanceService balanceService;
    private final PointProductService productService;
    private final PointCodeService codeService;
    private final PointLedgerService ledgerService;
    private final PointRedemptionService redemptionService;

    @Autowired
    public PointAdminController(
            BackendBus backendBus,
            PointBalanceService balanceService,
            PointProductService productService,
            PointCodeService codeService,
            PointLedgerService ledgerService,
            PointRedemptionService redemptionService) {
        this.backendBus = backendBus;
        this.balanceService = balanceService;
        this.productService = productService;
        this.codeService = codeService;
        this.ledgerService = ledgerService;
        this.redemptionService = redemptionService;
    }

    @GetMapping("/products/index")
    public JsonResponse products(@RequestParam HashMap<String, Object> params) {
        if (!backendBus.isSuperAdmin()) {
            return forbidden();
        }

        PaginationResult<PointProduct> result =
                productService.paginate(
                        page(params),
                        size(params),
                        MapUtils.getString(params, "name"),
                        productStatus(MapUtils.getString(params, "status")));
        return pageData(result);
    }

    @GetMapping("/products/{id}")
    public JsonResponse product(@PathVariable Integer id) throws NotFoundException {
        if (!backendBus.isSuperAdmin()) {
            return forbidden();
        }
        return JsonResponse.data(productService.findOrFail(id));
    }

    @PostMapping("/products/create")
    @Log(title = "积分商品-新建", businessType = BusinessTypeConstant.INSERT)
    public JsonResponse createProduct(@RequestBody @Validated PointProductRequest request) {
        if (!backendBus.isSuperAdmin()) {
            return forbidden();
        }
        return JsonResponse.data(
                productService.create(request.getName(), request.getPointsPrice()));
    }

    @PutMapping("/products/{id}")
    @Log(title = "积分商品-编辑", businessType = BusinessTypeConstant.UPDATE)
    public JsonResponse updateProduct(
            @PathVariable Integer id, @RequestBody @Validated PointProductRequest request)
            throws NotFoundException {
        if (!backendBus.isSuperAdmin()) {
            return forbidden();
        }
        return JsonResponse.data(
                productService.update(id, request.getName(), request.getPointsPrice()));
    }

    @PutMapping("/products/{id}/status")
    @Log(title = "积分商品-状态修改", businessType = BusinessTypeConstant.UPDATE)
    public JsonResponse updateProductStatus(
            @PathVariable Integer id, @RequestParam("status") String status)
            throws NotFoundException {
        if (!backendBus.isSuperAdmin()) {
            return forbidden();
        }
        return JsonResponse.data(productService.changeStatus(id, productStatus(status)));
    }

    @PutMapping("/products/{id}/off-sale")
    @Log(title = "积分商品-下架", businessType = BusinessTypeConstant.UPDATE)
    public JsonResponse offSaleProduct(@PathVariable Integer id) throws NotFoundException {
        if (!backendBus.isSuperAdmin()) {
            return forbidden();
        }
        return JsonResponse.data(productService.offSale(id));
    }

    @PutMapping("/products/{id}/on-sale")
    @Log(title = "积分商品-上架", businessType = BusinessTypeConstant.UPDATE)
    public JsonResponse onSaleProduct(@PathVariable Integer id) throws NotFoundException {
        if (!backendBus.isSuperAdmin()) {
            return forbidden();
        }
        return JsonResponse.data(productService.changeStatus(id, PointProductStatus.ON_SALE));
    }

    @DeleteMapping("/products/{id}")
    @Log(title = "积分商品-删除", businessType = BusinessTypeConstant.DELETE)
    public JsonResponse deleteProduct(@PathVariable Integer id) throws NotFoundException {
        if (!backendBus.isSuperAdmin()) {
            return forbidden();
        }
        productService.deleteById(id);
        return JsonResponse.success();
    }

    @PostMapping("/products/{productId}/codes/import")
    @Log(title = "积分兑换码-导入", businessType = BusinessTypeConstant.INSERT)
    public JsonResponse importCodes(
            @PathVariable Integer productId, @RequestBody PointCodeImportRequest request)
            throws NotFoundException {
        if (!backendBus.isSuperAdmin()) {
            return forbidden();
        }
        if (request == null || request.codeText() == null || request.codeText().isBlank()) {
            return JsonResponse.error("请粘贴兑换码");
        }
        PointCodeImportResult result = codeService.importCodes(productId, request.codeText());
        return JsonResponse.data(result);
    }

    @GetMapping("/codes/index")
    public JsonResponse codes(@RequestParam HashMap<String, Object> params) {
        if (!backendBus.isSuperAdmin()) {
            return forbidden();
        }
        PaginationResult<PointCode> result =
                codeService.paginate(
                        page(params),
                        size(params),
                        MapUtils.getInteger(params, "product_id"),
                        codeStatus(MapUtils.getString(params, "status")),
                        MapUtils.getString(params, "code"));
        return pageData(result);
    }

    /** Reveals a code only when the super-admin explicitly asks for this detail. */
    @GetMapping("/codes/{id}")
    public JsonResponse revealCode(@PathVariable Integer id) throws NotFoundException {
        if (!backendBus.isSuperAdmin()) {
            return forbidden();
        }
        HashMap<String, Object> data = new HashMap<>();
        data.put("id", id);
        data.put("code", codeService.reveal(id));
        return JsonResponse.data(data);
    }

    @DeleteMapping("/codes/{id}")
    @Log(title = "积分兑换码-删除", businessType = BusinessTypeConstant.DELETE)
    public JsonResponse deleteCode(@PathVariable Integer id) throws NotFoundException {
        if (!backendBus.isSuperAdmin()) {
            return forbidden();
        }
        codeService.deleteAvailable(id);
        return JsonResponse.success();
    }

    @PostMapping({"/adjust", "/adjustments"})
    @Log(title = "积分-人工调整", businessType = BusinessTypeConstant.UPDATE)
    public JsonResponse adjust(@RequestBody @Validated PointAdjustmentRequest request) {
        if (!backendBus.isSuperAdmin()) {
            return forbidden();
        }

        Integer operatorAdminId = BCtx.getId();
        String requestKey = request.getRequestKey();
        if (requestKey == null || requestKey.isBlank()) {
            requestKey = UUID.randomUUID().toString();
        } else {
            requestKey = requestKey.strip();
        }

        PointBalanceChangeResult result =
                balanceService.apply(
                        new PointBalanceChange(
                                request.getUserId(),
                                request.getDelta(),
                                PointLedgerType.MANUAL_ADJUSTMENT,
                                PointSourceKeys.manualAdjustment(
                                        request.getUserId(), operatorAdminId, requestKey),
                                request.getReason().strip(),
                                operatorAdminId,
                                true));
        HashMap<String, Object> data = new HashMap<>();
        data.put("ledger", result.ledger());
        data.put("applied", result.applied());
        return JsonResponse.data(data);
    }

    @GetMapping("/ledgers/index")
    public JsonResponse ledgers(@RequestParam HashMap<String, Object> params) {
        if (!backendBus.isSuperAdmin()) {
            return forbidden();
        }
        PaginationResult<PointLedger> result =
                ledgerService.paginate(
                        page(params),
                        size(params),
                        MapUtils.getInteger(params, "user_id"),
                        ledgerType(MapUtils.getString(params, "type")),
                        MapUtils.getInteger(params, "operator_admin_id"),
                        MapUtils.getString(params, "keyword"),
                        MapUtils.getString(params, "start_time"),
                        MapUtils.getString(params, "end_time"));
        return pageData(result);
    }

    @GetMapping("/redemptions/index")
    public JsonResponse redemptions(@RequestParam HashMap<String, Object> params) {
        if (!backendBus.isSuperAdmin()) {
            return forbidden();
        }
        PaginationResult<PointRedemption> result =
                redemptionService.paginate(
                        page(params),
                        size(params),
                        MapUtils.getInteger(params, "user_id"),
                        MapUtils.getInteger(params, "product_id"),
                        MapUtils.getInteger(params, "code_id"),
                        MapUtils.getString(params, "start_time"),
                        MapUtils.getString(params, "end_time"));
        return pageData(result);
    }

    private JsonResponse pageData(PaginationResult<?> result) {
        HashMap<String, Object> data = new HashMap<>();
        data.put("data", result.getData());
        data.put("total", result.getTotal());
        return JsonResponse.data(data);
    }

    private JsonResponse forbidden() {
        return JsonResponse.error("只有积分超级管理员可以操作", 403);
    }

    private int page(HashMap<String, Object> params) {
        return Math.max(MapUtils.getIntValue(params, "page", 1), 1);
    }

    private int size(HashMap<String, Object> params) {
        return Math.min(Math.max(MapUtils.getIntValue(params, "size", 10), 1), 100);
    }

    private PointProductStatus productStatus(String value) {
        return parseStatus(value, PointProductStatus.class, "兑换商品");
    }

    private PointCodeStatus codeStatus(String value) {
        return parseStatus(value, PointCodeStatus.class, "兑换码");
    }

    private PointLedgerType ledgerType(String value) {
        return parseStatus(value, PointLedgerType.class, "积分流水");
    }

    private <T extends Enum<T>> T parseStatus(String value, Class<T> type, String label) {
        if (value == null || value.isBlank()) {
            return null;
        }
        String normalized = value.strip().replace('-', '_').toUpperCase();
        try {
            return Enum.valueOf(type, normalized);
        } catch (IllegalArgumentException exception) {
            throw new ServiceException(label + "状态无效");
        }
    }
}
