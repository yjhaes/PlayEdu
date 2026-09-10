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
import jakarta.validation.constraints.Positive;
import java.io.Serializable;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

/** Minimal create/update request for a redeemable points product. */
@Data
public class PointProductRequest implements Serializable {

    @NotBlank(message = "请输入兑换商品名称")
    @Length(max = 191, message = "兑换商品名称不能超过191个字符")
    private String name;

    @NotNull(message = "请输入兑换商品积分价格")
    @Positive(message = "兑换商品积分价格必须为正整数")
    @JsonProperty("points_price")
    private Integer pointsPrice;
}
