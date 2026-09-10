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
package xyz.playedu.points.crypto;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;
import xyz.playedu.common.exception.ServiceException;

class PointCodeCryptoServiceTest {

    private static final String KEY = "test-code-encryption-key";

    @Test
    void encryptsAndDecryptsWithoutStoringThePlaintext() {
        PointCodeCryptoService crypto = new PointCodeCryptoService(KEY);
        String plaintext = "权益码-001/AbC";

        String ciphertext = crypto.encrypt(plaintext);

        assertThat(ciphertext).isNotEqualTo(plaintext).doesNotContain(plaintext);
        assertThat(crypto.decrypt(ciphertext)).isEqualTo(plaintext);
        assertThat(crypto.encrypt(plaintext)).isNotEqualTo(ciphertext);
    }

    @Test
    void calculatesTheSameKeyedDigestForNormalizedInput() {
        PointCodeCryptoService crypto = new PointCodeCryptoService(KEY);

        assertThat(crypto.digest("  CODE-001  ")).isEqualTo(crypto.digest("CODE-001"));
        assertThat(crypto.digest("CODE-001")).isNotEqualTo(crypto.digest("CODE-002"));
        assertThat(crypto.digest("CODE-001")).matches("[0-9a-f]{64}");
        assertThat(crypto.digest("CODE-001"))
                .isNotEqualTo(new PointCodeCryptoService("another-code-key").digest("CODE-001"));
    }

    @Test
    void rejectsMissingKeysAndInvalidCiphertextsWithoutLeakingTheCode() {
        PointCodeCryptoService crypto = new PointCodeCryptoService("");

        assertThatThrownBy(() -> crypto.encrypt("CODE-001"))
                .isInstanceOf(ServiceException.class)
                .hasMessage("兑换码加密密钥未配置");
        assertThatThrownBy(() -> new PointCodeCryptoService(KEY).decrypt("not-a-ciphertext"))
                .isInstanceOf(ServiceException.class)
                .hasMessage("兑换码解密失败");
    }
}
