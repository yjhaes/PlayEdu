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
package xyz.playedu.points.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.util.List;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.ResultMap;
import org.apache.ibatis.annotations.Select;
import xyz.playedu.points.domain.PointLedger;
import xyz.playedu.points.domain.PointLedgerType;

/** Mapper for immutable points ledger entries. */
public interface PointLedgerMapper extends BaseMapper<PointLedger> {

    List<PointLedger> paginate(
            @Param("userId") Integer userId,
            @Param("type") PointLedgerType type,
            @Param("operatorAdminId") Integer operatorAdminId,
            @Param("keyword") String keyword,
            @Param("startTime") String startTime,
            @Param("endTime") String endTime,
            @Param("offset") int offset,
            @Param("limit") int limit);

    long paginateCount(
            @Param("userId") Integer userId,
            @Param("type") PointLedgerType type,
            @Param("operatorAdminId") Integer operatorAdminId,
            @Param("keyword") String keyword,
            @Param("startTime") String startTime,
            @Param("endTime") String endTime);

    @Select(
            """
            SELECT id, user_id, delta, balance_after, type, source_key, reason,
                   operator_admin_id, created_at
            FROM point_ledgers
            WHERE source_key = #{sourceKey}
            """)
    @ResultMap("pointLedgerResultMap")
    PointLedger findBySourceKey(@Param("sourceKey") String sourceKey);

    @Select(
            """
            SELECT id, user_id, delta, balance_after, type, source_key, reason,
                   operator_admin_id, created_at
            FROM point_ledgers
            WHERE source_key = #{sourceKey}
            FOR UPDATE
            """)
    @ResultMap("pointLedgerResultMap")
    PointLedger findBySourceKeyForUpdate(@Param("sourceKey") String sourceKey);

    @Insert(
            """
            INSERT INTO point_ledgers
                (user_id, delta, balance_after, type, source_key, reason,
                 operator_admin_id, created_at)
            VALUES
                (#{userId}, #{delta}, #{balanceAfter}, #{type}, #{sourceKey}, #{reason},
                 #{operatorAdminId}, #{createdAt})
            ON DUPLICATE KEY UPDATE id = id
            """)
    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
    int insertIfAbsent(PointLedger ledger);
}
