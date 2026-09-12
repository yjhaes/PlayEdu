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
package xyz.playedu.api.request.frontend;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;
import lombok.Data;

/** Client request for one idempotent, single-code points redemption. */
@Data
public class PointRedemptionRequest implements Serializable {

    /** Used by the route without a product path variable. */
    @JsonProperty("product_id")
    private Integer productId;

    @JsonProperty("request_key")
    private String requestKey;

    private static final long serialVersionUID = 1L;
}
