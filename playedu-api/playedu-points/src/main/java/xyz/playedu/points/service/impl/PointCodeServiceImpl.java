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

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import xyz.playedu.common.exception.NotFoundException;
import xyz.playedu.common.exception.ServiceException;
import xyz.playedu.common.types.paginate.PaginationResult;
import xyz.playedu.points.crypto.PointCodeCryptoService;
import xyz.playedu.points.domain.PointCode;
import xyz.playedu.points.domain.PointCodeStatus;
import xyz.playedu.points.mapper.PointCodeMapper;
import xyz.playedu.points.service.PointCodeService;
import xyz.playedu.points.service.PointProductService;
import xyz.playedu.points.types.PointCodeImportLineResult;
import xyz.playedu.points.types.PointCodeImportLineStatus;
import xyz.playedu.points.types.PointCodeImportResult;

/** Default persistence service for encrypted voucher-code inventory. */
@Service
public class PointCodeServiceImpl implements PointCodeService {

    private final PointCodeMapper codeMapper;
    private final PointProductService productService;
    private final PointCodeCryptoService cryptoService;

    @Autowired
    public PointCodeServiceImpl(
            PointCodeMapper codeMapper,
            PointProductService productService,
            PointCodeCryptoService cryptoService) {
        this.codeMapper = codeMapper;
        this.productService = productService;
        this.cryptoService = cryptoService;
    }

    @Override
    public PaginationResult<PointCode> paginate(
            int page, int size, Integer productId, PointCodeStatus status, String code) {
        int pageSize = normalizedPageSize(size);
        int offset = pageOffset(page, pageSize);
        String codeDigest = code == null || code.isBlank() ? null : cryptoService.digest(code);
        List<PointCode> codes =
                codeMapper.paginate(productId, status, codeDigest, offset, pageSize);
        if (codes == null) {
            codes = List.of();
        }

        PaginationResult<PointCode> result = new PaginationResult<>();
        result.setData(codes);
        result.setTotal(codeMapper.paginateCount(productId, status, codeDigest));
        return result;
    }

    @Override
    @Transactional
    public PointCodeImportResult importCodes(Integer productId, String multilineCodes)
            throws NotFoundException {
        if (multilineCodes == null || multilineCodes.isEmpty()) {
            return importCodes(productId, List.of());
        }
        return importCodes(productId, List.of(multilineCodes.split("\\R", -1)));
    }

    @Override
    @Transactional
    public PointCodeImportResult importCodes(Integer productId, List<String> codeLines)
            throws NotFoundException {
        productService.findForUpdate(productId);
        if (codeLines == null || codeLines.isEmpty()) {
            return emptyImportResult();
        }

        Set<String> seenDigests = new HashSet<>();
        List<PointCodeImportLineResult> results = new ArrayList<>();
        int importedCount = 0;
        int duplicateCount = 0;

        for (int index = 0; index < codeLines.size(); index++) {
            String input = codeLines.get(index);
            if (input == null || input.isBlank()) {
                continue;
            }

            String normalized = cryptoService.normalize(input);
            String digest = cryptoService.digest(normalized);
            boolean duplicate = !seenDigests.add(digest) || codeMapper.findByDigest(digest) != null;
            if (duplicate) {
                duplicateCount++;
                results.add(
                        new PointCodeImportLineResult(
                                index + 1, PointCodeImportLineStatus.DUPLICATE));
                continue;
            }

            PointCode code = new PointCode();
            code.setProductId(productId);
            code.setCodeCiphertext(cryptoService.encrypt(normalized));
            code.setCodeDigest(digest);
            code.setStatus(PointCodeStatus.AVAILABLE);
            Date now = new Date();
            code.setCreatedAt(now);
            code.setUpdatedAt(now);

            if (codeMapper.insertIgnore(code) == 0) {
                duplicateCount++;
                results.add(
                        new PointCodeImportLineResult(
                                index + 1, PointCodeImportLineStatus.DUPLICATE));
            } else {
                importedCount++;
                results.add(
                        new PointCodeImportLineResult(
                                index + 1, PointCodeImportLineStatus.IMPORTED));
            }
        }

        return new PointCodeImportResult(importedCount, duplicateCount, results);
    }

    @Override
    public long availableCount(Integer productId) {
        return codeMapper.countAvailableByProductId(productId);
    }

    @Override
    public boolean hasDeliveredCodes(Integer productId) {
        return codeMapper.countDeliveredByProductId(productId) > 0;
    }

    @Override
    @Transactional
    public void deleteAvailable(Integer codeId) throws NotFoundException {
        PointCode code = codeMapper.selectById(codeId);
        if (code == null) {
            throw new NotFoundException("兑换码不存在");
        }
        if (code.getStatus() != PointCodeStatus.AVAILABLE) {
            throw new ServiceException("已发放兑换码不可删除");
        }
        if (codeMapper.deleteAvailableById(codeId) != 1) {
            PointCode current = codeMapper.selectById(codeId);
            if (current != null && current.getStatus() == PointCodeStatus.DELIVERED) {
                throw new ServiceException("已发放兑换码不可删除");
            }
            throw new ServiceException("兑换码删除失败");
        }
    }

    @Override
    public PointCode findOrFail(Integer codeId) throws NotFoundException {
        PointCode code = codeMapper.selectById(codeId);
        if (code == null) {
            throw new NotFoundException("兑换码不存在");
        }
        return code;
    }

    @Override
    public String reveal(Integer codeId) throws NotFoundException {
        return cryptoService.decrypt(findOrFail(codeId).getCodeCiphertext());
    }

    @Override
    @Transactional
    public int deleteAvailableByProductId(Integer productId) {
        return codeMapper.deleteAvailableByProductId(productId);
    }

    private PointCodeImportResult emptyImportResult() {
        return new PointCodeImportResult(0, 0, List.of());
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
