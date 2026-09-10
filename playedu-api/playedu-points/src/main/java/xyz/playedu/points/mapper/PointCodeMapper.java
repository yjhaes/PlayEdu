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
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.ResultMap;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;
import xyz.playedu.points.domain.PointCode;
import xyz.playedu.points.domain.PointCodeStatus;

/** Mapper for encrypted voucher-code inventory. */
public interface PointCodeMapper extends BaseMapper<PointCode> {

    @Select(
            """
            SELECT id, product_id, code_ciphertext, code_digest, status, delivered_at,
                   created_at, updated_at
            FROM point_codes
            WHERE code_digest = #{digest}
            """)
    @Results(
            id = "pointCodeResultMap",
            value = {
                @Result(column = "id", property = "id"),
                @Result(column = "product_id", property = "productId"),
                @Result(column = "code_ciphertext", property = "codeCiphertext"),
                @Result(column = "code_digest", property = "codeDigest"),
                @Result(column = "status", property = "status", javaType = PointCodeStatus.class),
                @Result(column = "delivered_at", property = "deliveredAt"),
                @Result(column = "created_at", property = "createdAt"),
                @Result(column = "updated_at", property = "updatedAt")
            })
    PointCode findByDigest(@Param("digest") String digest);

    @Select(
            """
            SELECT id, product_id, code_ciphertext, code_digest, status, delivered_at,
                   created_at, updated_at
            FROM point_codes
            WHERE product_id = #{productId} AND status = 'AVAILABLE'
            ORDER BY id
            LIMIT 1
            FOR UPDATE
            """)
    @ResultMap("pointCodeResultMap")
    PointCode findAvailableForUpdate(@Param("productId") Integer productId);

    @Select(
            "SELECT COUNT(*) FROM point_codes WHERE product_id = #{productId} AND status ="
                    + " 'AVAILABLE'")
    long countAvailableByProductId(@Param("productId") Integer productId);

    @Select(
            "SELECT COUNT(*) FROM point_codes WHERE product_id = #{productId} AND status ="
                    + " 'DELIVERED'")
    long countDeliveredByProductId(@Param("productId") Integer productId);

    @Insert(
            """
            INSERT INTO point_codes
                (product_id, code_ciphertext, code_digest, status, delivered_at, created_at, updated_at)
            VALUES
                (#{productId}, #{codeCiphertext}, #{codeDigest}, #{status}, #{deliveredAt},
                 #{createdAt}, #{updatedAt})
            ON DUPLICATE KEY UPDATE id = id
            """)
    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
    int insertIgnore(PointCode code);

    @Delete("DELETE FROM point_codes WHERE id = #{codeId} AND status = 'AVAILABLE'")
    int deleteAvailableById(@Param("codeId") Integer codeId);

    @Delete("DELETE FROM point_codes WHERE product_id = #{productId} AND status = 'AVAILABLE'")
    int deleteAvailableByProductId(@Param("productId") Integer productId);
}
