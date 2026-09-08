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

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = RedisRuntimeIntegrationTest.TestApplication.class)
@Testcontainers(disabledWithoutDocker = true)
class RedisRuntimeIntegrationTest {

    @Container
    static final GenericContainer<?> REDIS =
            new GenericContainer<>(DockerImageName.parse("redis:7.4-alpine"))
                    .withExposedPorts(6379);

    @Autowired private StringRedisTemplate redisTemplate;

    @Autowired private RedissonClient redisson;

    @Autowired private RedisKeyspace keyspace;

    @Autowired private ApiRequestRateLimiter apiRequestRateLimiter;

    @DynamicPropertySource
    static void redisProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.redis.host", REDIS::getHost);
        registry.add("spring.data.redis.port", () -> REDIS.getMappedPort(6379));
        registry.add("spring.data.redis.database", () -> 5);
        registry.add("spring.data.redis.timeout", () -> "2s");
        registry.add("playedu.redis.key-prefix", () -> "playedu:test");
    }

    @Test
    void usesRealRedisForStringsLuaZSetsExpirationLocksAndRateLimits() throws Exception {
        String stringKey = keyspace.key("smoke", "string");
        String expiringKey = keyspace.key("smoke", "expiration");
        String zsetKey = keyspace.key("smoke", "ranking");

        redisTemplate.opsForValue().set(stringKey, "stored");
        String scriptedValue =
                redisTemplate.execute(
                        new DefaultRedisScript<>("return redis.call('GET', KEYS[1])", String.class),
                        List.of(stringKey));
        redisTemplate.opsForZSet().add(zsetKey, "learner-42", 42);
        redisTemplate.opsForValue().set(expiringKey, "short-lived", Duration.ofMillis(100));

        assertThat(scriptedValue).isEqualTo("stored");
        assertThat(redisTemplate.opsForZSet().score(zsetKey, "learner-42")).isEqualTo(42D);
        assertThat(waitForExpiry(expiringKey)).isTrue();

        RLock lock = redisson.getLock(keyspace.lock("smoke", "lock"));
        assertThat(lock.tryLock(0, 1, TimeUnit.SECONDS)).isTrue();
        try {
            assertThat(lock.isHeldByCurrentThread()).isTrue();
        } finally {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }

        ApiRequestRateLimiter.RateLimitDecision firstRequest =
                apiRequestRateLimiter.acquire("198.51.100.42", 1, 1);
        ApiRequestRateLimiter.RateLimitDecision rejectedRequest =
                apiRequestRateLimiter.acquire("198.51.100.42", 1, 1);
        assertThat(firstRequest.allowed()).isTrue();
        assertThat(rejectedRequest.allowed()).isFalse();
    }

    private boolean waitForExpiry(String key) throws InterruptedException {
        long deadline = System.nanoTime() + TimeUnit.SECONDS.toNanos(2);
        while (System.nanoTime() < deadline) {
            if (redisTemplate.opsForValue().get(key) == null) {
                return true;
            }
            Thread.sleep(20);
        }
        return false;
    }

    @SpringBootConfiguration
    @EnableAutoConfiguration
    @Import({RedisRuntimeConfiguration.class, ApiRequestRateLimiter.class})
    static class TestApplication {}
}
