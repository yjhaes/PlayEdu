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
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.Duration;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.redisson.Redisson;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
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

    private static final int TEST_REDIS_DATABASE = 5;

    @Container
    static final GenericContainer<?> REDIS =
            new GenericContainer<>(DockerImageName.parse("redis:7.4-alpine"))
                    .withExposedPorts(6379);

    @Autowired private StringRedisTemplate redisTemplate;

    @Autowired private RedissonClient redisson;

    @Autowired private RedisKeyspace keyspace;

    @Autowired private ApiRequestRateLimiter apiRequestRateLimiter;

    @Autowired private RedisDistributedLock distributedLock;

    @Autowired private LoginFailureTracker loginFailureTracker;

    @DynamicPropertySource
    static void redisProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.redis.host", REDIS::getHost);
        registry.add("spring.data.redis.port", () -> REDIS.getMappedPort(6379));
        registry.add("spring.data.redis.database", () -> TEST_REDIS_DATABASE);
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
    }

    @Test
    void refillsTokensAfterTheConfiguredWindowAndAllowsABoundedBurst() throws Exception {
        String clientIp = "198.51.100.43";

        assertThat(apiRequestRateLimiter.acquire(clientIp, 3, 1).allowed()).isTrue();
        assertThat(apiRequestRateLimiter.acquire(clientIp, 3, 1).allowed()).isTrue();
        assertThat(apiRequestRateLimiter.acquire(clientIp, 3, 1).allowed()).isTrue();

        ApiRequestRateLimiter.RateLimitDecision exhausted =
                apiRequestRateLimiter.acquire(clientIp, 3, 1);
        assertThat(exhausted.allowed()).isFalse();
        assertThat(exhausted.remaining()).isZero();

        ApiRequestRateLimiter.RateLimitDecision recovered =
                waitForRateLimiterRecovery(clientIp, 3, 1);
        assertThat(recovered.allowed()).isTrue();
        assertThat(recovered.remaining()).isBetween(0L, 2L);
    }

    @Test
    void keepsQuotasSeparateForDifferentClientIps() {
        assertThat(apiRequestRateLimiter.acquire("198.51.100.44", 1, 5).allowed()).isTrue();
        assertThat(apiRequestRateLimiter.acquire("198.51.100.45", 1, 5).allowed()).isTrue();
        assertThat(apiRequestRateLimiter.acquire("198.51.100.44", 1, 5).allowed()).isFalse();
        assertThat(apiRequestRateLimiter.acquire("198.51.100.45", 1, 5).allowed()).isFalse();
    }

    @Test
    void sharesOneClientQuotaBetweenSeparateRedissonClients() {
        RedissonClient secondRedisson = createSecondRedissonClient();
        ApiRequestRateLimiter secondInstance = new ApiRequestRateLimiter(keyspace, secondRedisson);
        String clientIp = "198.51.100.46";

        try {
            assertThat(apiRequestRateLimiter.acquire(clientIp, 3, 5).allowed()).isTrue();
            assertThat(secondInstance.acquire(clientIp, 3, 5).allowed()).isTrue();
            assertThat(apiRequestRateLimiter.acquire(clientIp, 3, 5).allowed()).isTrue();

            ApiRequestRateLimiter.RateLimitDecision exhausted =
                    secondInstance.acquire(clientIp, 3, 5);
            assertThat(exhausted.allowed()).isFalse();
            assertThat(exhausted.remaining()).isZero();
        } finally {
            secondRedisson.shutdown();
        }
    }

    @Test
    void atomicallySharesLearnerFailuresAndTheOriginalLockWindowAcrossInstances() throws Exception {
        LoginFailureTracker secondInstance = new LoginFailureTracker(redisTemplate, keyspace);
        String learner = "learner-" + UUID.randomUUID();

        LoginFailureTracker.LoginFailureStatus firstFailure =
                loginFailureTracker.recordFailure(LoginFailureTracker.LoginType.LEARNER, learner);
        Thread.sleep(1_100);
        LoginFailureTracker.LoginFailureStatus secondFailure =
                secondInstance.recordFailure(LoginFailureTracker.LoginType.LEARNER, learner);

        assertThat(secondFailure.failureCount()).isEqualTo(2);
        assertThat(secondFailure.remainingSeconds()).isLessThan(firstFailure.remainingSeconds());

        ExecutorService executor = Executors.newFixedThreadPool(8);
        try {
            List<Future<LoginFailureTracker.LoginFailureStatus>> failures =
                    java.util.stream.IntStream.range(0, 20)
                            .mapToObj(
                                    attempt ->
                                            executor.submit(
                                                    () ->
                                                            (attempt % 2 == 0
                                                                            ? loginFailureTracker
                                                                            : secondInstance)
                                                                    .recordFailure(
                                                                            LoginFailureTracker
                                                                                    .LoginType
                                                                                    .LEARNER,
                                                                            learner)))
                            .toList();
            for (Future<LoginFailureTracker.LoginFailureStatus> failure : failures) {
                failure.get();
            }
        } finally {
            executor.shutdownNow();
        }

        LoginFailureTracker.LoginFailureStatus sharedStatus =
                secondInstance.status(LoginFailureTracker.LoginType.LEARNER, learner);
        assertThat(sharedStatus.failureCount()).isEqualTo(LoginFailureTracker.MAX_FAILURES);
        assertThat(sharedStatus.remainingSeconds()).isPositive();
        assertThatThrownBy(
                        () ->
                                secondInstance.assertNotLocked(
                                        LoginFailureTracker.LoginType.LEARNER, learner))
                .isInstanceOf(LoginFailureLimitException.class)
                .hasMessageContaining("请");
        assertThatThrownBy(
                        () ->
                                loginFailureTracker.reset(
                                        LoginFailureTracker.LoginType.LEARNER, learner))
                .isInstanceOf(LoginFailureLimitException.class);

        String successfulLearner = "successful-learner-" + UUID.randomUUID();
        loginFailureTracker.recordFailure(LoginFailureTracker.LoginType.LEARNER, successfulLearner);
        secondInstance.reset(LoginFailureTracker.LoginType.LEARNER, successfulLearner);
        assertThat(
                        loginFailureTracker.status(
                                LoginFailureTracker.LoginType.LEARNER, successfulLearner))
                .isEqualTo(new LoginFailureTracker.LoginFailureStatus(0, 0));
    }

    @Test
    void locksAdministratorsForOneHourAndClearsUnlockedFailuresAfterSuccess() {
        String administrator = "administrator-" + UUID.randomUUID();
        String successfulAdministrator = "successful-administrator-" + UUID.randomUUID();

        LoginFailureTracker.LoginFailureStatus firstFailure =
                loginFailureTracker.recordFailure(
                        LoginFailureTracker.LoginType.ADMINISTRATOR, administrator);
        assertThat(firstFailure.remainingSeconds()).isBetween(3_590L, 3_600L);
        for (int attempt = 1; attempt < LoginFailureTracker.MAX_FAILURES; attempt++) {
            loginFailureTracker.recordFailure(
                    LoginFailureTracker.LoginType.ADMINISTRATOR, administrator);
        }

        assertThatThrownBy(
                        () ->
                                loginFailureTracker.assertNotLocked(
                                        LoginFailureTracker.LoginType.ADMINISTRATOR, administrator))
                .isInstanceOf(LoginFailureLimitException.class);

        loginFailureTracker.recordFailure(
                LoginFailureTracker.LoginType.ADMINISTRATOR, successfulAdministrator);
        loginFailureTracker.reset(
                LoginFailureTracker.LoginType.ADMINISTRATOR, successfulAdministrator);
        assertThat(
                        loginFailureTracker.status(
                                LoginFailureTracker.LoginType.ADMINISTRATOR,
                                successfulAdministrator))
                .isEqualTo(new LoginFailureTracker.LoginFailureStatus(0, 0));
    }

    @Test
    void allowsAtMostOneConcurrentExecutorIntoTheSameBusinessLock() throws Exception {
        ExecutorService executor = Executors.newFixedThreadPool(2);
        CountDownLatch ready = new CountDownLatch(2);
        CountDownLatch start = new CountDownLatch(1);
        AtomicInteger inCriticalSection = new AtomicInteger();
        AtomicInteger highestConcurrentExecutors = new AtomicInteger();

        try {
            List<Future<?>> operations =
                    List.of(
                            executor.submit(
                                    () ->
                                            executeProtectedOperation(
                                                    ready,
                                                    start,
                                                    inCriticalSection,
                                                    highestConcurrentExecutors)),
                            executor.submit(
                                    () ->
                                            executeProtectedOperation(
                                                    ready,
                                                    start,
                                                    inCriticalSection,
                                                    highestConcurrentExecutors)));

            assertThat(ready.await(2, TimeUnit.SECONDS)).isTrue();
            start.countDown();
            for (Future<?> operation : operations) {
                operation.get();
            }
        } finally {
            executor.shutdownNow();
        }

        assertThat(highestConcurrentExecutors.get()).isOne();
    }

    private void executeProtectedOperation(
            CountDownLatch ready,
            CountDownLatch start,
            AtomicInteger inCriticalSection,
            AtomicInteger highestConcurrentExecutors) {
        ready.countDown();
        try {
            start.await();
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException(
                    "Interrupted while preparing concurrent lock test", exception);
        }
        distributedLock.execute(
                "test-write",
                "learner-42",
                () -> {
                    int currentExecutors = inCriticalSection.incrementAndGet();
                    highestConcurrentExecutors.accumulateAndGet(currentExecutors, Math::max);
                    try {
                        Thread.sleep(150);
                    } catch (InterruptedException exception) {
                        Thread.currentThread().interrupt();
                        throw new IllegalStateException(
                                "Interrupted while executing concurrent lock test", exception);
                    } finally {
                        inCriticalSection.decrementAndGet();
                    }
                    return null;
                });
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

    private ApiRequestRateLimiter.RateLimitDecision waitForRateLimiterRecovery(
            String clientIp, long permitsPerWindow, long windowSeconds)
            throws InterruptedException {
        long deadline = System.nanoTime() + TimeUnit.SECONDS.toNanos(2);
        ApiRequestRateLimiter.RateLimitDecision decision;
        do {
            decision = apiRequestRateLimiter.acquire(clientIp, permitsPerWindow, windowSeconds);
            if (decision.allowed()) {
                return decision;
            }
            Thread.sleep(20);
        } while (System.nanoTime() < deadline);
        return decision;
    }

    private RedissonClient createSecondRedissonClient() {
        Config config = new Config();
        config.useSingleServer()
                .setAddress("redis://" + REDIS.getHost() + ":" + REDIS.getMappedPort(6379))
                .setDatabase(TEST_REDIS_DATABASE);
        return Redisson.create(config);
    }

    @SpringBootConfiguration
    @EnableAutoConfiguration(exclude = DataSourceAutoConfiguration.class)
    @Import({
        RedisRuntimeConfiguration.class,
        ApiRequestRateLimiter.class,
        RedisDistributedLock.class,
        LoginFailureTracker.class,
        RedisKeyspace.class
    })
    static class TestApplication {}
}
