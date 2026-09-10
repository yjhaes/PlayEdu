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
import xyz.playedu.points.domain.PointProduct;
import xyz.playedu.points.domain.PointProductStatus;

/** Mapper for redeemable points products. */
public interface PointProductMapper extends BaseMapper<PointProduct> {

    @Select(
            """
            <script>
            SELECT id, name, points_price, status, created_at, updated_at
            FROM point_products
            <where>
              <if test="name != null and name != ''">
                AND name LIKE CONCAT('%', #{name}, '%')
              </if>
              <if test="status != null">
                AND status = #{status}
              </if>
            </where>
            ORDER BY id DESC
            LIMIT #{offset}, #{limit}
            </script>
            """)
    @ResultMap("pointProductResultMap")
    List<PointProduct> paginate(
            @Param("name") String name,
            @Param("status") PointProductStatus status,
            @Param("offset") int offset,
            @Param("limit") int limit);

    @Select(
            """
            <script>
            SELECT COUNT(*)
            FROM point_products
            <where>
              <if test="name != null and name != ''">
                AND name LIKE CONCAT('%', #{name}, '%')
              </if>
              <if test="status != null">
                AND status = #{status}
              </if>
            </where>
            </script>
            """)
    long paginateCount(
            @Param("name") String name, @Param("status") PointProductStatus status);

    @Select(
            """
            SELECT id, name, points_price, status, created_at, updated_at
            FROM point_products
            WHERE id = #{id}
            FOR UPDATE
            """)
    @Results(
            id = "pointProductResultMap",
            value = {
                @Result(column = "id", property = "id"),
                @Result(column = "name", property = "name"),
                @Result(column = "points_price", property = "pointsPrice"),
                @Result(column = "status", property = "status"),
                @Result(column = "created_at", property = "createdAt"),
                @Result(column = "updated_at", property = "updatedAt")
            })
    PointProduct selectByIdForUpdate(@Param("id") Integer id);
}
