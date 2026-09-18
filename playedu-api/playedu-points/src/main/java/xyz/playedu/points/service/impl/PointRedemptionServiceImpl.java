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
package xyz.playedu.points.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import java.util.Date;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import xyz.playedu.common.exception.NotFoundException;
import xyz.playedu.common.exception.ServiceException;
import xyz.playedu.common.types.paginate.PaginationResult;
import xyz.playedu.points.domain.PointCode;
import xyz.playedu.points.domain.PointLedgerType;
import xyz.playedu.points.domain.PointProduct;
import xyz.playedu.points.domain.PointProductStatus;
import xyz.playedu.points.domain.PointRedemption;
import xyz.playedu.points.mapper.PointBalanceMapper;
import xyz.playedu.points.mapper.PointCodeMapper;
import xyz.playedu.points.mapper.PointProductMapper;
import xyz.playedu.points.mapper.PointRedemptionMapper;
import xyz.playedu.points.service.PointBalanceService;
import xyz.playedu.points.service.PointRedemptionService;
import xyz.playedu.points.types.PointBalanceChange;
import xyz.playedu.points.types.PointBalanceChangeResult;

/** Default persistence service for completed points redemptions. */
@Service
public class PointRedemptionServiceImpl extends ServiceImpl<PointRedemptionMapper, PointRedemption>
        implements PointRedemptionService {

    private static final int REQUEST_KEY_MAX_LENGTH = 160;

    private final PointRedemptionMapper redemptionMapper;
    private final PointProductMapper productMapper;
    private final PointCodeMapper codeMapper;
    private final PointBalanceMapper balanceMapper;
    private final PointBalanceService balanceService;

    public PointRedemptionServiceImpl(
            PointRedemptionMapper redemptionMapper,
            PointProductMapper productMapper,
            PointCodeMapper codeMapper,
            PointBalanceMapper balanceMapper,
            PointBalanceService balanceService) {
        this.redemptionMapper = redemptionMapper;
        this.productMapper = productMapper;
        this.codeMapper = codeMapper;
        this.balanceMapper = balanceMapper;
        this.balanceService = balanceService;
    }

    @Override
    public void removeByUserId(Integer userId) {
        if (userId == null) {
            return;
        }
        remove(query().getWrapper().eq("user_id", userId));
    }

    @Override
    public PointRedemption findForUser(Integer redemptionId, Integer userId)
            throws NotFoundException {
        if (redemptionId == null || userId == null) {
            throw new NotFoundException("兑换记录不存在");
        }
        PointRedemption redemption = redemptionMapper.findByIdAndUserId(redemptionId, userId);
        if (redemption == null) {
            throw new NotFoundException("兑换记录不存在");
        }
        return redemption;
    }

    @Override
    public PaginationResult<PointRedemption> paginate(
            int page,
            int size,
            Integer userId,
            Integer productId,
            Integer codeId,
            String startTime,
            String endTime) {
        int pageSize = normalizedPageSize(size);
        int offset = pageOffset(page, pageSize);
        List<PointRedemption> redemptions =
                redemptionMapper.paginate(
                        userId, productId, codeId, startTime, endTime, offset, pageSize);

        PaginationResult<PointRedemption> result = new PaginationResult<>();
        result.setData(redemptions == null ? List.of() : redemptions);
        result.setTotal(
                redemptionMapper.paginateCount(userId, productId, codeId, startTime, endTime));
        return result;
    }

    @Override
    @Transactional
    public PointRedemption redeem(Integer userId, Integer productId, String requestKey) {
        validateRequest(userId, productId, requestKey);

        PointRedemption existing = redemptionMapper.findByUserIdAndRequestKey(userId, requestKey);
        if (existing != null) {
            return existing;
        }

        PointProduct product = productMapper.selectByIdForUpdate(productId);
        if (product == null) {
            throw new ServiceException("兑换商品不存在");
        }

        existing = redemptionMapper.findByUserIdAndRequestKeyForUpdate(userId, requestKey);
        if (existing != null) {
            return existing;
        }
        validateProduct(product);
        validateLearner(userId);

        PointCode code = codeMapper.findAvailableForUpdate(productId);
        if (code == null) {
            throw new ServiceException("兑换商品已兑完");
        }

        PointBalanceChangeResult balanceChange =
                balanceService.apply(
                        new PointBalanceChange(
                                userId,
                                -product.getPointsPrice(),
                                PointLedgerType.REDEMPTION,
                                sourceKey(userId, requestKey),
                                null,
                                null,
                                false));
        if (!balanceChange.applied()) {
            existing = redemptionMapper.findByUserIdAndRequestKeyForUpdate(userId, requestKey);
            if (existing != null) {
                return existing;
            }
            throw new ServiceException("兑换请求状态不一致");
        }

        Date deliveredAt = new Date();
        if (codeMapper.markDelivered(code.getId(), deliveredAt) != 1) {
            throw new ServiceException("兑换码发放失败");
        }

        PointRedemption redemption = new PointRedemption();
        redemption.setUserId(userId);
        redemption.setProductId(productId);
        redemption.setCodeId(code.getId());
        redemption.setRequestKey(requestKey);
        redemption.setPointsCost(product.getPointsPrice());
        redemption.setCreatedAt(deliveredAt);
        if (redemptionMapper.insert(redemption) != 1) {
            throw new ServiceException("兑换记录创建失败");
        }
        return redemption;
    }

    private void validateRequest(Integer userId, Integer productId, String requestKey) {
        if (userId == null) {
            throw new ServiceException("兑换必须提供学员ID");
        }
        if (productId == null) {
            throw new ServiceException("兑换必须提供商品ID");
        }
        if (requestKey == null || requestKey.isBlank()) {
            throw new ServiceException("兑换必须提供幂等键");
        }
        if (requestKey.length() > REQUEST_KEY_MAX_LENGTH) {
            throw new ServiceException("兑换幂等键长度不能超过160个字符");
        }
    }

    private void validateProduct(PointProduct product) {
        if (product.getStatus() != PointProductStatus.ON_SALE) {
            throw new ServiceException("兑换商品未在售");
        }
        if (product.getPointsPrice() == null || product.getPointsPrice() <= 0) {
            throw new ServiceException("兑换商品积分价格无效");
        }
    }

    private void validateLearner(Integer userId) {
        Integer isLock = balanceMapper.lockIsLock(userId);
        if (isLock == null) {
            throw new ServiceException("学员不存在");
        }
        if (isLock == 1) {
            throw new ServiceException("锁定学员不能兑换");
        }
    }

    private String sourceKey(Integer userId, String requestKey) {
        return "redemption:" + userId + ":" + requestKey;
    }

    private int normalizedPageSize(int size) {
        if (size <= 0) {
            return 10;
        }
        return Math.min(size, 100);
    }

    private int pageOffset(int page, int pageSize) {
        if (page <= 1) {
            return 0;
        }
        long offset = (long) (page - 1) * pageSize;
        return offset > Integer.MAX_VALUE ? Integer.MAX_VALUE : (int) offset;
    }
}
