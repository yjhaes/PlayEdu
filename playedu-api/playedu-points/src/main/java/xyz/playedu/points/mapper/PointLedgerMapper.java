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
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.ResultMap;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;
import xyz.playedu.points.domain.PointLedger;
import xyz.playedu.points.domain.PointLedgerType;

/** Mapper for immutable points ledger entries. */
public interface PointLedgerMapper extends BaseMapper<PointLedger> {

    @Select(
            """
            <script>
            SELECT id, user_id, delta, balance_after, type, source_key, reason,
                   operator_admin_id, created_at
            FROM point_ledgers
            <where>
              <if test="userId != null">
                AND user_id = #{userId}
              </if>
              <if test="type != null">
                AND type = #{type}
              </if>
              <if test="operatorAdminId != null">
                AND operator_admin_id = #{operatorAdminId}
              </if>
              <if test="keyword != null and keyword != ''">
                AND (source_key LIKE CONCAT('%', #{keyword}, '%')
                     OR reason LIKE CONCAT('%', #{keyword}, '%'))
              </if>
              <if test="startTime != null and startTime != ''">
                AND created_at &gt;= #{startTime}
              </if>
              <if test="endTime != null and endTime != ''">
                AND created_at &lt;= #{endTime}
              </if>
            </where>
            ORDER BY created_at DESC, id DESC
            LIMIT #{offset}, #{limit}
            </script>
            """)
    @ResultMap("pointLedgerResultMap")
    List<PointLedger> paginate(
            @Param("userId") Integer userId,
            @Param("type") PointLedgerType type,
            @Param("operatorAdminId") Integer operatorAdminId,
            @Param("keyword") String keyword,
            @Param("startTime") String startTime,
            @Param("endTime") String endTime,
            @Param("offset") int offset,
            @Param("limit") int limit);

    @Select(
            """
            <script>
            SELECT COUNT(*)
            FROM point_ledgers
            <where>
              <if test="userId != null">
                AND user_id = #{userId}
              </if>
              <if test="type != null">
                AND type = #{type}
              </if>
              <if test="operatorAdminId != null">
                AND operator_admin_id = #{operatorAdminId}
              </if>
              <if test="keyword != null and keyword != ''">
                AND (source_key LIKE CONCAT('%', #{keyword}, '%')
                     OR reason LIKE CONCAT('%', #{keyword}, '%'))
              </if>
              <if test="startTime != null and startTime != ''">
                AND created_at &gt;= #{startTime}
              </if>
              <if test="endTime != null and endTime != ''">
                AND created_at &lt;= #{endTime}
              </if>
            </where>
            </script>
            """)
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
    @Results(
            id = "pointLedgerResultMap",
            value = {
                @Result(column = "id", property = "id"),
                @Result(column = "user_id", property = "userId"),
                @Result(column = "delta", property = "delta"),
                @Result(column = "balance_after", property = "balanceAfter"),
                @Result(column = "type", property = "type", javaType = PointLedgerType.class),
                @Result(column = "source_key", property = "sourceKey"),
                @Result(column = "reason", property = "reason"),
                @Result(column = "operator_admin_id", property = "operatorAdminId"),
                @Result(column = "created_at", property = "createdAt")
            })
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
