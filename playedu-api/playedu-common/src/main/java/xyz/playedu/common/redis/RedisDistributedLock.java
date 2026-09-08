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
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;
import org.springframework.util.DigestUtils;

/** Acquires locks from the required Redis runtime without exposing raw key names to callers. */
@Component
public class RedisDistributedLock {

    private final RedisKeyspace keyspace;
    private final RedissonClient redisson;

    public RedisDistributedLock(RedisKeyspace keyspace, RedissonClient redisson) {
        this.keyspace = keyspace;
        this.redisson = redisson;
    }

    public boolean tryLock(
            String module, String subject, long waitTime, long leaseTime, TimeUnit timeUnit) {
        try {
            return lock(module, subject).tryLock(waitTime, leaseTime, timeUnit);
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Interrupted while acquiring Redis lock", exception);
        }
    }

    public void release(String module, String subject) {
        RLock lock = lock(module, subject);
        if (lock.isHeldByCurrentThread()) {
            lock.unlock();
        }
    }

    private RLock lock(String module, String subject) {
        String hashedSubject = DigestUtils.md5DigestAsHex(subject.getBytes(StandardCharsets.UTF_8));
        return redisson.getLock(keyspace.lock(module, hashedSubject));
    }
}
