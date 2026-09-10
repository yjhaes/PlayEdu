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

import java.util.List;
import xyz.playedu.common.exception.NotFoundException;
import xyz.playedu.common.types.paginate.PaginationResult;
import xyz.playedu.points.domain.PointCode;
import xyz.playedu.points.domain.PointCodeStatus;
import xyz.playedu.points.types.PointCodeImportResult;

/** Base service for encrypted voucher-code inventory. */
public interface PointCodeService {

    PaginationResult<PointCode> paginate(
            int page, int size, Integer productId, PointCodeStatus status, String code);

    PointCodeImportResult importCodes(Integer productId, String multilineCodes)
            throws NotFoundException;

    PointCodeImportResult importCodes(Integer productId, List<String> codeLines)
            throws NotFoundException;

    long availableCount(Integer productId);

    boolean hasDeliveredCodes(Integer productId);

    void deleteAvailable(Integer codeId) throws NotFoundException;

    PointCode findOrFail(Integer codeId) throws NotFoundException;

    String reveal(Integer codeId) throws NotFoundException;

    int deleteAvailableByProductId(Integer productId);
}
