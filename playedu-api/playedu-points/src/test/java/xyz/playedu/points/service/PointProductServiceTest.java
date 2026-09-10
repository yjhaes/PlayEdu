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
package xyz.playedu.points.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import xyz.playedu.common.exception.ServiceException;
import xyz.playedu.points.domain.PointProduct;
import xyz.playedu.points.domain.PointProductStatus;
import xyz.playedu.points.mapper.PointCodeMapper;
import xyz.playedu.points.mapper.PointProductMapper;
import xyz.playedu.points.service.impl.PointProductServiceImpl;

@ExtendWith(MockitoExtension.class)
class PointProductServiceTest {

    @Mock private PointProductMapper productMapper;

    @Mock private PointCodeMapper codeMapper;

    private PointProductServiceImpl productService;

    @BeforeEach
    void setUp() {
        productService = new PointProductServiceImpl(productMapper, codeMapper);
    }

    @Test
    void createsOnlyProductsWithANonBlankNameAndPositivePrice() {
        when(productMapper.insert(any(PointProduct.class)))
                .thenAnswer(
                        invocation -> {
                            PointProduct product = invocation.getArgument(0);
                            product.setId(7);
                            return 1;
                        });

        PointProduct created = productService.create("  试运行商品  ", 50);

        assertThat(created.getId()).isEqualTo(7);
        assertThat(created.getName()).isEqualTo("试运行商品");
        assertThat(created.getPointsPrice()).isEqualTo(50);
        assertThat(created.getStatus()).isEqualTo(PointProductStatus.ON_SALE);
        assertThatThrownBy(() -> productService.create(" ", 50))
                .isInstanceOf(ServiceException.class)
                .hasMessage("兑换商品名称不能为空");
        assertThatThrownBy(() -> productService.create("商品", 0))
                .isInstanceOf(ServiceException.class)
                .hasMessage("兑换商品积分价格必须为正整数");
    }

    @Test
    void refusesToChangePriceAfterAnyCodeHasBeenDelivered() {
        PointProduct existing = product(7, "商品", 50, PointProductStatus.ON_SALE);
        when(productMapper.selectByIdForUpdate(7)).thenReturn(existing);
        when(codeMapper.countDeliveredByProductId(7)).thenReturn(1L);

        assertThatThrownBy(() -> productService.update(7, "新名称", 60))
                .isInstanceOf(ServiceException.class)
                .hasMessage("商品已有已发放兑换码，积分价格不可修改");
        verify(productMapper, never()).updateById(any(PointProduct.class));
    }

    @Test
    void canUpdateNameAndTakeProductsOffSale() throws Exception {
        PointProduct existing = product(7, "商品", 50, PointProductStatus.ON_SALE);
        when(productMapper.selectByIdForUpdate(7)).thenReturn(existing);
        when(productMapper.updateById(any(PointProduct.class))).thenReturn(1);
        when(productMapper.selectById(7))
                .thenReturn(product(7, "新名称", 50, PointProductStatus.ON_SALE));

        PointProduct updated = productService.update(7, "新名称", 50);

        assertThat(updated.getName()).isEqualTo("新名称");
        productService.changeStatus(7, PointProductStatus.OFF_SALE);
        verify(productMapper, org.mockito.Mockito.times(2)).updateById(any(PointProduct.class));
    }

    @Test
    void deletesUnissuedInventoryTogetherWithTheProduct() throws Exception {
        PointProduct existing = product(7, "商品", 50, PointProductStatus.OFF_SALE);
        when(productMapper.selectByIdForUpdate(7)).thenReturn(existing);
        when(codeMapper.countDeliveredByProductId(7)).thenReturn(0L);
        when(codeMapper.deleteAvailableByProductId(7)).thenReturn(3);
        when(productMapper.deleteById(7)).thenReturn(1);

        productService.deleteById(7);

        verify(codeMapper).deleteAvailableByProductId(eq(7));
        verify(productMapper).deleteById(7);
    }

    @Test
    void keepsProductsWithDeliveredCodesAndTheirHistory() throws Exception {
        PointProduct existing = product(7, "商品", 50, PointProductStatus.ON_SALE);
        when(productMapper.selectByIdForUpdate(7)).thenReturn(existing);
        when(codeMapper.countDeliveredByProductId(7)).thenReturn(1L);

        assertThatThrownBy(() -> productService.deleteById(7))
                .isInstanceOf(ServiceException.class)
                .hasMessage("商品已有已发放兑换码，只能下架");
        verify(codeMapper, never()).deleteAvailableByProductId(7);
        verify(productMapper, never()).deleteById(7);
    }

    private PointProduct product(int id, String name, int price, PointProductStatus status) {
        PointProduct product = new PointProduct();
        product.setId(id);
        product.setName(name);
        product.setPointsPrice(price);
        product.setStatus(status);
        return product;
    }
}
