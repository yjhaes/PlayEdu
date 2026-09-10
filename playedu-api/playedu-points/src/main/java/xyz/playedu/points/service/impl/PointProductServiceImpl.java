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

import java.util.Date;
import java.util.Objects;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import xyz.playedu.common.exception.NotFoundException;
import xyz.playedu.common.exception.ServiceException;
import xyz.playedu.points.domain.PointProduct;
import xyz.playedu.points.domain.PointProductStatus;
import xyz.playedu.points.mapper.PointCodeMapper;
import xyz.playedu.points.mapper.PointProductMapper;
import xyz.playedu.points.service.PointProductService;

/** Default persistence service for redeemable points products. */
@Service
public class PointProductServiceImpl implements PointProductService {

    private final PointProductMapper productMapper;
    private final PointCodeMapper codeMapper;

    @Autowired
    public PointProductServiceImpl(PointProductMapper productMapper, PointCodeMapper codeMapper) {
        this.productMapper = productMapper;
        this.codeMapper = codeMapper;
    }

    @Override
    public PointProduct findOrFail(Integer id) throws NotFoundException {
        PointProduct product = productMapper.selectById(id);
        if (product == null) {
            throw new NotFoundException("兑换商品不存在");
        }
        return product;
    }

    @Override
    @Transactional
    public PointProduct create(String name, Integer pointsPrice) {
        return create(name, pointsPrice, PointProductStatus.ON_SALE);
    }

    @Override
    @Transactional
    public PointProduct create(String name, Integer pointsPrice, PointProductStatus status) {
        String normalizedName = validateName(name);
        validatePrice(pointsPrice);
        PointProductStatus normalizedStatus = status == null ? PointProductStatus.ON_SALE : status;

        Date now = new Date();
        PointProduct product = new PointProduct();
        product.setName(normalizedName);
        product.setPointsPrice(pointsPrice);
        product.setStatus(normalizedStatus);
        product.setCreatedAt(now);
        product.setUpdatedAt(now);
        if (productMapper.insert(product) != 1) {
            throw new ServiceException("兑换商品创建失败");
        }
        return product;
    }

    @Override
    @Transactional
    public PointProduct update(Integer id, String name, Integer pointsPrice)
            throws NotFoundException {
        PointProduct current = findForUpdate(id);
        String normalizedName = validateName(name);
        validatePrice(pointsPrice);

        if (!Objects.equals(current.getPointsPrice(), pointsPrice) && hasDeliveredCodes(id)) {
            throw new ServiceException("商品已有已发放兑换码，积分价格不可修改");
        }

        PointProduct update = new PointProduct();
        update.setId(id);
        update.setName(normalizedName);
        update.setPointsPrice(pointsPrice);
        update.setUpdatedAt(new Date());
        if (productMapper.updateById(update) != 1) {
            throw new ServiceException("兑换商品更新失败");
        }
        PointProduct updated = productMapper.selectById(id);
        return updated == null ? update : updated;
    }

    @Override
    @Transactional
    public PointProduct changeStatus(Integer id, PointProductStatus status)
            throws NotFoundException {
        findForUpdate(id);
        if (status == null) {
            throw new ServiceException("兑换商品状态不能为空");
        }

        PointProduct update = new PointProduct();
        update.setId(id);
        update.setStatus(status);
        update.setUpdatedAt(new Date());
        if (productMapper.updateById(update) != 1) {
            throw new ServiceException("兑换商品状态更新失败");
        }
        PointProduct updated = productMapper.selectById(id);
        return updated == null ? update : updated;
    }

    @Override
    @Transactional
    public PointProduct offSale(Integer id) throws NotFoundException {
        return changeStatus(id, PointProductStatus.OFF_SALE);
    }

    @Override
    public long availableCount(Integer id) {
        return codeMapper.countAvailableByProductId(id);
    }

    @Override
    public boolean hasDeliveredCodes(Integer id) {
        return codeMapper.countDeliveredByProductId(id) > 0;
    }

    @Override
    @Transactional
    public void deleteById(Integer id) throws NotFoundException {
        findForUpdate(id);
        if (hasDeliveredCodes(id)) {
            throw new ServiceException("商品已有已发放兑换码，只能下架");
        }
        codeMapper.deleteAvailableByProductId(id);
        if (productMapper.deleteById(id) != 1) {
            throw new ServiceException("兑换商品删除失败");
        }
    }

    @Override
    @Transactional
    public PointProduct findForUpdate(Integer id) throws NotFoundException {
        PointProduct product = productMapper.selectByIdForUpdate(id);
        if (product == null) {
            throw new NotFoundException("兑换商品不存在");
        }
        return product;
    }

    private String validateName(String name) {
        if (name == null || name.strip().isEmpty()) {
            throw new ServiceException("兑换商品名称不能为空");
        }
        return name.strip();
    }

    private void validatePrice(Integer pointsPrice) {
        if (pointsPrice == null || pointsPrice <= 0) {
            throw new ServiceException("兑换商品积分价格必须为正整数");
        }
    }
}
