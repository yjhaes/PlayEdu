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
package xyz.playedu.api.request.backend;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.io.Serializable;
import lombok.Data;

/** Request to create one auditable manual points adjustment. */
@Data
public class PointAdjustmentRequest implements Serializable {

    @NotNull(message = "请提供学员ID")
    @JsonProperty("user_id")
    private Integer userId;

    @NotNull(message = "请提供积分调整值")
    private Integer delta;

    @NotBlank(message = "请填写积分调整原因")
    private String reason;

    /** Optional client key used to make a retried adjustment return the original ledger. */
    @Size(max = 140, message = "积分调整幂等键长度不能超过140个字符")
    @JsonProperty("request_key")
    private String requestKey;
}
