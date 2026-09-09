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
package xyz.playedu.api.controller;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import xyz.playedu.common.redis.ApiRateLimitUnavailableException;
import xyz.playedu.common.types.JsonResponse;

class ExceptionControllerTest {

    @Test
    void returnsServiceUnavailableWhenRedisBackedRateLimitingIsUnavailable() {
        ExceptionController controller = new ExceptionController();

        ResponseEntity<JsonResponse> response =
                controller.apiRateLimitUnavailableHandler(
                        new ApiRateLimitUnavailableException(
                                "Redis is unavailable", new IllegalStateException()));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.SERVICE_UNAVAILABLE);
        assertThat(response.getBody()).isNotNull();
    }
}
