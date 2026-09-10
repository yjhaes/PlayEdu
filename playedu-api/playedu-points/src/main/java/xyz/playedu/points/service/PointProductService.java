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

import xyz.playedu.common.exception.NotFoundException;
import xyz.playedu.common.types.paginate.PaginationResult;
import xyz.playedu.points.domain.PointProduct;
import xyz.playedu.points.domain.PointProductStatus;

/** Base service for redeemable points products. */
public interface PointProductService {

    PaginationResult<PointProduct> paginate(
            int page, int size, String name, PointProductStatus status);

    PointProduct findOrFail(Integer id) throws NotFoundException;

    PointProduct findForUpdate(Integer id) throws NotFoundException;

    PointProduct create(String name, Integer pointsPrice);

    PointProduct create(String name, Integer pointsPrice, PointProductStatus status);

    PointProduct update(Integer id, String name, Integer pointsPrice) throws NotFoundException;

    PointProduct changeStatus(Integer id, PointProductStatus status) throws NotFoundException;

    PointProduct offSale(Integer id) throws NotFoundException;

    long availableCount(Integer id);

    boolean hasDeliveredCodes(Integer id);

    void deleteById(Integer id) throws NotFoundException;
}
