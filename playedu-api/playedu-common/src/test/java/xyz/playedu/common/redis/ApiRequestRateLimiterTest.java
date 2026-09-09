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

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.redisson.api.RedissonClient;

class ApiRequestRateLimiterTest {

    @Test
    void failsClosedWhenRedisRateLimiterCannotBeReached() {
        RedissonClient unavailableRedisson = mock(RedissonClient.class);
        when(unavailableRedisson.getRateLimiter(anyString()))
                .thenThrow(new IllegalStateException("Redis is unavailable"));
        ApiRequestRateLimiter unavailableLimiter =
                new ApiRequestRateLimiter(new RedisKeyspace(), unavailableRedisson);

        assertThatThrownBy(() -> unavailableLimiter.acquire("198.51.100.47", 1, 1))
                .isInstanceOf(ApiRateLimitUnavailableException.class)
                .hasCauseInstanceOf(IllegalStateException.class);
    }
}
