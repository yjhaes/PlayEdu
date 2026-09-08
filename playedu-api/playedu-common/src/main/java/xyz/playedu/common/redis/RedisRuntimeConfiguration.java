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

import java.time.Duration;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.redisson.config.SingleServerConfig;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.util.StringUtils;

/** Configures the required Redis clients used by distributed runtime modules. */
@Configuration
public class RedisRuntimeConfiguration {

    @Bean(destroyMethod = "shutdown")
    public RedissonClient redissonClient(RedisProperties properties) {
        Config config = new Config();
        SingleServerConfig server =
                config.useSingleServer()
                        .setAddress("redis://" + properties.getHost() + ":" + properties.getPort())
                        .setDatabase(properties.getDatabase());
        if (StringUtils.hasText(properties.getPassword())) {
            server.setPassword(properties.getPassword());
        }
        if (properties.getConnectTimeout() != null) {
            server.setConnectTimeout(toMilliseconds(properties.getConnectTimeout()));
        }
        if (properties.getTimeout() != null) {
            server.setTimeout(toMilliseconds(properties.getTimeout()));
        }
        return Redisson.create(config);
    }

    @Bean
    public SmartInitializingSingleton redisRequiredStartupCheck(StringRedisTemplate redisTemplate) {
        return () -> {
            try (RedisConnection connection =
                    redisTemplate.getConnectionFactory().getConnection()) {
                String response = connection.ping();
                if (!"PONG".equals(response)) {
                    throw new IllegalStateException("Redis did not respond to PING");
                }
            } catch (DataAccessException exception) {
                throw new IllegalStateException("Redis is required but unavailable", exception);
            }
        };
    }

    private int toMilliseconds(Duration duration) {
        return Math.toIntExact(duration.toMillis());
    }
}
