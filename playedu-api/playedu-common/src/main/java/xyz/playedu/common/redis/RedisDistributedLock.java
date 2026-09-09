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

import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.util.DigestUtils;

/** Runs protected write operations under a Redis lock with one application-wide lock policy. */
@Component
@Slf4j
public class RedisDistributedLock {

    private static final long WAIT_SECONDS = 5;
    private static final String UNAVAILABLE_MESSAGE = "受保护操作暂不可用，请稍后重试";

    private final RedisKeyspace keyspace;
    private final RedissonClient redisson;

    public RedisDistributedLock(RedisKeyspace keyspace, RedissonClient redisson) {
        this.keyspace = keyspace;
        this.redisson = redisson;
    }

    public <T> T execute(String module, String subject, Supplier<T> operation) {
        return executeWithLock(module, subject, operation);
    }

    private <T> T executeWithLock(String module, String subject, Supplier<T> operation) {
        RLock lock = lock(module, subject);
        boolean acquired = acquire(lock, module);
        if (!acquired) {
            log.warn("Redis lock was unavailable for module={}", module);
            throw new RedisLockException(UNAVAILABLE_MESSAGE);
        }

        try {
            return operation.get();
        } finally {
            releaseAfterTransaction(lock, module);
        }
    }

    private boolean acquire(RLock lock, String module) {
        try {
            return lock.tryLock(WAIT_SECONDS, TimeUnit.SECONDS);
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            throw unavailable(module, exception);
        } catch (RuntimeException exception) {
            throw unavailable(module, exception);
        }
    }

    private RLock lock(String module, String subject) {
        try {
            String hashedSubject =
                    DigestUtils.md5DigestAsHex(subject.getBytes(StandardCharsets.UTF_8));
            return redisson.getLock(keyspace.lock(module, hashedSubject));
        } catch (RuntimeException exception) {
            throw unavailable(module, exception);
        }
    }

    private void releaseAfterTransaction(RLock lock, String module) {
        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            TransactionSynchronizationManager.registerSynchronization(
                    new TransactionSynchronization() {
                        @Override
                        public void afterCompletion(int status) {
                            release(lock, module);
                        }
                    });
            return;
        }
        release(lock, module);
    }

    private void release(RLock lock, String module) {
        try {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        } catch (RuntimeException exception) {
            throw unavailable(module, exception);
        }
    }

    private RedisLockException unavailable(String module, Exception exception) {
        log.error("Redis lock operation failed for module={}", module, exception);
        return new RedisLockException(UNAVAILABLE_MESSAGE, exception);
    }
}
