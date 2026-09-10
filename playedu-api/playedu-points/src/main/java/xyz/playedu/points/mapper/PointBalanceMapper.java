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

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

/** SQL boundary for the current balance kept on {@code users.credit1}. */
@Mapper
public interface PointBalanceMapper {

    @Select("SELECT credit1 FROM users WHERE id = #{userId} FOR UPDATE")
    Integer lockCredit1(@Param("userId") Integer userId);

    /** Locks the learner row so redemption validates the same lock state it commits against. */
    @Select("SELECT is_lock FROM users WHERE id = #{userId} FOR UPDATE")
    Integer lockIsLock(@Param("userId") Integer userId);

    @Update(
            """
            UPDATE users
            SET credit1 = credit1 + #{delta}, updated_at = CURRENT_TIMESTAMP
            WHERE id = #{userId}
            """)
    int applyCredit1Delta(@Param("userId") Integer userId, @Param("delta") Integer delta);

    @Update(
            """
            UPDATE users
            SET credit1 = credit1 + #{delta}, updated_at = CURRENT_TIMESTAMP
            WHERE id = #{userId} AND credit1 + #{delta} >= 0
            """)
    int applyCredit1DeltaIfNonNegative(
            @Param("userId") Integer userId, @Param("delta") Integer delta);
}
