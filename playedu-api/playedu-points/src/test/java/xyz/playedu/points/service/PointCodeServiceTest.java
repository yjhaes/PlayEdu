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
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import xyz.playedu.common.exception.ServiceException;
import xyz.playedu.points.crypto.PointCodeCryptoService;
import xyz.playedu.points.domain.PointCode;
import xyz.playedu.points.domain.PointCodeStatus;
import xyz.playedu.points.domain.PointProduct;
import xyz.playedu.points.mapper.PointCodeMapper;
import xyz.playedu.points.service.impl.PointCodeServiceImpl;
import xyz.playedu.points.types.PointCodeImportLineStatus;
import xyz.playedu.points.types.PointCodeImportResult;

@ExtendWith(MockitoExtension.class)
class PointCodeServiceTest {

    private static final String KEY = "test-code-encryption-key";

    @Mock private PointCodeMapper codeMapper;

    @Mock private PointProductService productService;

    private PointCodeServiceImpl codeService;

    @BeforeEach
    void setUp() {
        codeService =
                new PointCodeServiceImpl(
                        codeMapper, productService, new PointCodeCryptoService(KEY));
    }

    private void stubProduct() throws Exception {
        PointProduct product = new PointProduct();
        product.setId(7);
        product.setName("商品");
        product.setPointsPrice(50);
        when(productService.findForUpdate(7)).thenReturn(product);
    }

    @Test
    void importsNonBlankNormalizedLinesAndReportsDuplicatesWithoutPlaintext() throws Exception {
        stubProduct();
        PointCodeCryptoService crypto = new PointCodeCryptoService(KEY);
        PointCode existing = new PointCode();
        existing.setId(12);
        existing.setCodeDigest(crypto.digest("EXISTING"));
        when(codeMapper.findByDigest(existing.getCodeDigest())).thenReturn(existing);
        when(codeMapper.findByDigest(crypto.digest("NEW-CODE"))).thenReturn(null);
        when(codeMapper.insertIgnore(any(PointCode.class)))
                .thenAnswer(
                        invocation -> {
                            PointCode code = invocation.getArgument(0);
                            code.setId(20);
                            return 1;
                        });

        PointCodeImportResult result =
                codeService.importCodes(7, List.of(" ", " EXISTING ", "NEW-CODE", "NEW-CODE"));

        assertThat(result.getImportedCount()).isEqualTo(1);
        assertThat(result.getImportedCount()).isEqualTo(1);
        assertThat(result.getDuplicateCount()).isEqualTo(2);
        assertThat(result.getResults())
                .extracting(item -> item.getLineNumber())
                .containsExactly(2, 3, 4);
        assertThat(result.getResults())
                .extracting(item -> item.getStatus())
                .containsExactly(
                        PointCodeImportLineStatus.DUPLICATE,
                        PointCodeImportLineStatus.IMPORTED,
                        PointCodeImportLineStatus.DUPLICATE);
        String report = new ObjectMapper().writeValueAsString(result);
        assertThat(report).doesNotContain("EXISTING", "NEW-CODE", KEY);
        verify(codeMapper).insertIgnore(any(PointCode.class));
    }

    @Test
    void skipsAConcurrentUniqueDigestConflictAsADuplicate() throws Exception {
        stubProduct();
        when(codeMapper.findByDigest(anyString())).thenReturn(null);
        when(codeMapper.insertIgnore(any(PointCode.class))).thenReturn(0);

        PointCodeImportResult result = codeService.importCodes(7, "RACE-CODE");

        assertThat(result.getImportedCount()).isZero();
        assertThat(result.getDuplicateCount()).isEqualTo(1);
        assertThat(result.getResults().get(0).getStatus())
                .isEqualTo(PointCodeImportLineStatus.DUPLICATE);
    }

    @Test
    void deletesOnlyCodesThatHaveNotBeenDelivered() throws Exception {
        PointCode available = code(4, PointCodeStatus.AVAILABLE);
        when(codeMapper.selectById(4)).thenReturn(available);
        when(codeMapper.deleteAvailableById(4)).thenReturn(1);

        codeService.deleteAvailable(4);

        verify(codeMapper).deleteAvailableById(4);

        PointCode delivered = code(5, PointCodeStatus.DELIVERED);
        when(codeMapper.selectById(5)).thenReturn(delivered);
        assertThatThrownBy(() -> codeService.deleteAvailable(5))
                .isInstanceOf(ServiceException.class)
                .hasMessage("已发放兑换码不可删除");
        verify(codeMapper, never()).deleteAvailableById(5);
    }

    @Test
    void exposesExactAvailableInventoryCount() {
        when(codeMapper.countAvailableByProductId(7)).thenReturn(19L);

        assertThat(codeService.availableCount(7)).isEqualTo(19L);
    }

    @Test
    void doesNotExposeCiphertextOrDigestInJsonOrToString() throws Exception {
        PointCode code = new PointCode();
        code.setCodeCiphertext("ciphertext");
        code.setCodeDigest("digest");

        String json = new ObjectMapper().writeValueAsString(code);

        assertThat(json).doesNotContain("ciphertext", "digest", "code_ciphertext", "code_digest");
        assertThat(code.toString()).doesNotContain("ciphertext", "digest");
    }

    private PointCode code(int id, PointCodeStatus status) {
        PointCode code = new PointCode();
        code.setId(id);
        code.setStatus(status);
        return code;
    }
}
