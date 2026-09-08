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
package xyz.playedu.common.redis;

import java.util.StringJoiner;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/** Builds Redis keys inside PlayEdu's single application namespace. */
@Component
@ConfigurationProperties(prefix = "playedu.redis")
public class RedisKeyspace {

    private String keyPrefix = "playedu";

    public String getKeyPrefix() {
        return keyPrefix;
    }

    public void setKeyPrefix(String keyPrefix) {
        if (!StringUtils.hasText(keyPrefix) || !keyPrefix.startsWith("playedu")) {
            throw new IllegalArgumentException("Redis key prefix must start with playedu");
        }
        this.keyPrefix = keyPrefix;
    }

    public String key(String module, String... parts) {
        StringJoiner key = new StringJoiner(":");
        key.add(keyPrefix);
        key.add(segment(module));
        for (String part : parts) {
            key.add(segment(part));
        }
        return key.toString();
    }

    public String lock(String module, String subject) {
        return key("lock", module, subject);
    }

    public String rateLimiter(String subject) {
        return key("rate-limit", subject);
    }

    private String segment(String value) {
        if (!StringUtils.hasText(value) || value.contains(":")) {
            throw new IllegalArgumentException(
                    "Redis key segments must be non-empty and colon-free");
        }
        return value;
    }
}
