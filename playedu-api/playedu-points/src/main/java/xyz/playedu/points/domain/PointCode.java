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
package xyz.playedu.points.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;
import lombok.ToString;

/** An encrypted voucher code owned by a points product. */
@Data
@TableName("point_codes")
public class PointCode implements Serializable {

    @TableId(type = IdType.AUTO)
    private Integer id;

    @JsonProperty("product_id")
    private Integer productId;

    @JsonIgnore
    @ToString.Exclude
    @JsonProperty("code_ciphertext")
    private String codeCiphertext;

    @JsonIgnore
    @ToString.Exclude
    @JsonProperty("code_digest")
    private String codeDigest;

    private PointCodeStatus status;

    @JsonProperty("delivered_at")
    private Date deliveredAt;

    @JsonProperty("created_at")
    private Date createdAt;

    @JsonProperty("updated_at")
    private Date updatedAt;

    private static final long serialVersionUID = 1L;
}
