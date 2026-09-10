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
import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/** An immutable change to a learner's points balance. */
@Data
@TableName("point_ledgers")
public class PointLedger implements Serializable {

    @TableId(type = IdType.AUTO)
    private Integer id;

    @JsonProperty("user_id")
    private Integer userId;

    private Integer delta;

    @JsonProperty("balance_after")
    private Integer balanceAfter;

    private PointLedgerType type;

    @JsonProperty("source_key")
    private String sourceKey;

    private String reason;

    @JsonProperty("operator_admin_id")
    private Integer operatorAdminId;

    @JsonProperty("created_at")
    private Date createdAt;

    private static final long serialVersionUID = 1L;
}
