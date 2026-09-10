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
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.ResultMap;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;
import xyz.playedu.points.domain.PointRedemption;

/** Mapper for completed points redemptions. */
public interface PointRedemptionMapper extends BaseMapper<PointRedemption> {

    @Select(
            """
            <script>
            SELECT id, user_id, product_id, code_id, request_key, points_cost, created_at
            FROM point_redemptions
            <where>
              <if test="userId != null">
                AND user_id = #{userId}
              </if>
              <if test="productId != null">
                AND product_id = #{productId}
              </if>
              <if test="codeId != null">
                AND code_id = #{codeId}
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
    @ResultMap("pointRedemptionResultMap")
    List<PointRedemption> paginate(
            @Param("userId") Integer userId,
            @Param("productId") Integer productId,
            @Param("codeId") Integer codeId,
            @Param("startTime") String startTime,
            @Param("endTime") String endTime,
            @Param("offset") int offset,
            @Param("limit") int limit);

    @Select(
            """
            <script>
            SELECT COUNT(*)
            FROM point_redemptions
            <where>
              <if test="userId != null">
                AND user_id = #{userId}
              </if>
              <if test="productId != null">
                AND product_id = #{productId}
              </if>
              <if test="codeId != null">
                AND code_id = #{codeId}
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
            @Param("productId") Integer productId,
            @Param("codeId") Integer codeId,
            @Param("startTime") String startTime,
            @Param("endTime") String endTime);

    @Select(
            """
            SELECT id, user_id, product_id, code_id, request_key, points_cost, created_at
            FROM point_redemptions
            WHERE user_id = #{userId} AND request_key = #{requestKey}
            """)
    @Results(
            id = "pointRedemptionResultMap",
            value = {
                @Result(column = "id", property = "id"),
                @Result(column = "user_id", property = "userId"),
                @Result(column = "product_id", property = "productId"),
                @Result(column = "code_id", property = "codeId"),
                @Result(column = "request_key", property = "requestKey"),
                @Result(column = "points_cost", property = "pointsCost"),
                @Result(column = "created_at", property = "createdAt")
            })
    PointRedemption findByUserIdAndRequestKey(
            @Param("userId") Integer userId, @Param("requestKey") String requestKey);

    @Select(
            """
            SELECT id, user_id, product_id, code_id, request_key, points_cost, created_at
            FROM point_redemptions
            WHERE user_id = #{userId} AND request_key = #{requestKey}
            FOR UPDATE
            """)
    @ResultMap("pointRedemptionResultMap")
    PointRedemption findByUserIdAndRequestKeyForUpdate(
            @Param("userId") Integer userId, @Param("requestKey") String requestKey);
}
