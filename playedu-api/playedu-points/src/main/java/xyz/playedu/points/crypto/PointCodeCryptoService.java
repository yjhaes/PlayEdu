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

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.HexFormat;
import javax.crypto.Cipher;
import javax.crypto.Mac;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import xyz.playedu.common.exception.ServiceException;

/** Encrypts and fingerprints voucher codes without retaining their plaintext. */
@Service
public final class PointCodeCryptoService {

    private static final String AES_TRANSFORMATION = "AES/GCM/NoPadding";
    private static final String HMAC_ALGORITHM = "HmacSHA256";
    private static final int GCM_TAG_LENGTH_BITS = 128;
    private static final int GCM_IV_LENGTH_BYTES = 12;
    private static final int MINIMUM_KEY_LENGTH_BYTES = 16;
    private static final byte[] DIGEST_CONTEXT =
            "playedu.points.code-digest.v1".getBytes(StandardCharsets.UTF_8);

    private final byte[] encryptionKey;
    private final byte[] digestKey;
    private final SecureRandom secureRandom;

    @Autowired
    public PointCodeCryptoService(
            @Value("${playedu.points.code-encryption-key:}") String configuredKey) {
        this(configuredKey, new SecureRandom());
    }

    PointCodeCryptoService(String configuredKey, SecureRandom secureRandom) {
        if (secureRandom == null) {
            throw new IllegalArgumentException("随机数生成器不能为空");
        }
        this.secureRandom = secureRandom;

        byte[] configuredKeyBytes = configuredKeyBytes(configuredKey);
        this.encryptionKey =
                configuredKeyBytes == null ? null : sha256(configuredKeyBytes, "加密密钥派生失败");
        this.digestKey = encryptionKey == null ? null : deriveDigestKey(encryptionKey);
    }

    /** Normalizes only surrounding whitespace; code contents and case remain unchanged. */
    public String normalize(String code) {
        if (code == null) {
            throw new ServiceException("兑换码不能为空");
        }
        String normalized = code.strip();
        if (normalized.isEmpty()) {
            throw new ServiceException("兑换码不能为空");
        }
        return normalized;
    }

    public String encrypt(String code) {
        String normalized = normalize(code);
        requireConfiguredKey();

        byte[] iv = new byte[GCM_IV_LENGTH_BYTES];
        secureRandom.nextBytes(iv);
        try {
            Cipher cipher = Cipher.getInstance(AES_TRANSFORMATION);
            cipher.init(
                    Cipher.ENCRYPT_MODE,
                    new SecretKeySpec(encryptionKey, "AES"),
                    new GCMParameterSpec(GCM_TAG_LENGTH_BITS, iv));
            byte[] ciphertext = cipher.doFinal(normalized.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder()
                    .encodeToString(
                            ByteBuffer.allocate(iv.length + ciphertext.length)
                                    .put(iv)
                                    .put(ciphertext)
                                    .array());
        } catch (GeneralSecurityException exception) {
            throw new ServiceException("兑换码加密失败", exception);
        }
    }

    public String decrypt(String ciphertext) {
        requireConfiguredKey();
        if (ciphertext == null || ciphertext.isBlank()) {
            throw new ServiceException("兑换码解密失败");
        }

        try {
            byte[] encoded = Base64.getDecoder().decode(ciphertext);
            if (encoded.length <= GCM_IV_LENGTH_BYTES) {
                throw new GeneralSecurityException("密文长度无效");
            }
            byte[] iv = new byte[GCM_IV_LENGTH_BYTES];
            byte[] encrypted = new byte[encoded.length - GCM_IV_LENGTH_BYTES];
            System.arraycopy(encoded, 0, iv, 0, iv.length);
            System.arraycopy(encoded, iv.length, encrypted, 0, encrypted.length);

            Cipher cipher = Cipher.getInstance(AES_TRANSFORMATION);
            cipher.init(
                    Cipher.DECRYPT_MODE,
                    new SecretKeySpec(encryptionKey, "AES"),
                    new GCMParameterSpec(GCM_TAG_LENGTH_BITS, iv));
            return new String(cipher.doFinal(encrypted), StandardCharsets.UTF_8);
        } catch (IllegalArgumentException | GeneralSecurityException exception) {
            throw new ServiceException("兑换码解密失败", exception);
        }
    }

    public String digest(String code) {
        String normalized = normalize(code);
        requireConfiguredKey();
        try {
            Mac mac = Mac.getInstance(HMAC_ALGORITHM);
            mac.init(new SecretKeySpec(digestKey, HMAC_ALGORITHM));
            return HexFormat.of()
                    .formatHex(mac.doFinal(normalized.getBytes(StandardCharsets.UTF_8)));
        } catch (GeneralSecurityException exception) {
            throw new ServiceException("兑换码摘要计算失败", exception);
        }
    }

    private void requireConfiguredKey() {
        if (encryptionKey == null) {
            throw new ServiceException("兑换码加密密钥未配置");
        }
    }

    private static byte[] configuredKeyBytes(String configuredKey) {
        if (configuredKey == null || configuredKey.strip().isEmpty()) {
            return null;
        }
        String value = configuredKey.strip();
        byte[] keyBytes;
        if (value.startsWith("base64:")) {
            try {
                keyBytes = Base64.getDecoder().decode(value.substring("base64:".length()));
            } catch (IllegalArgumentException exception) {
                throw new IllegalArgumentException("兑换码加密密钥不是有效的Base64值", exception);
            }
        } else {
            keyBytes = value.getBytes(StandardCharsets.UTF_8);
        }
        if (keyBytes.length < MINIMUM_KEY_LENGTH_BYTES) {
            throw new IllegalArgumentException("兑换码加密密钥长度至少为16字节");
        }
        return keyBytes;
    }

    private static byte[] deriveDigestKey(byte[] encryptionKey) {
        try {
            Mac mac = Mac.getInstance(HMAC_ALGORITHM);
            mac.init(new SecretKeySpec(encryptionKey, HMAC_ALGORITHM));
            return mac.doFinal(DIGEST_CONTEXT);
        } catch (GeneralSecurityException exception) {
            throw new IllegalStateException("兑换码摘要密钥派生失败", exception);
        }
    }

    private static byte[] sha256(byte[] value, String message) {
        try {
            return MessageDigest.getInstance("SHA-256").digest(value);
        } catch (GeneralSecurityException exception) {
            throw new IllegalStateException(message, exception);
        }
    }
}
