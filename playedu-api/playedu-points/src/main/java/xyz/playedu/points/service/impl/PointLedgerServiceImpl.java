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
package xyz.playedu.points.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import java.util.List;
import org.springframework.stereotype.Service;
import xyz.playedu.common.types.paginate.PaginationResult;
import xyz.playedu.points.domain.PointLedger;
import xyz.playedu.points.domain.PointLedgerType;
import xyz.playedu.points.mapper.PointLedgerMapper;
import xyz.playedu.points.service.PointLedgerService;

/** Default persistence service for immutable points ledger entries. */
@Service
public class PointLedgerServiceImpl extends ServiceImpl<PointLedgerMapper, PointLedger>
        implements PointLedgerService {

    @Override
    public void removeByUserId(Integer userId) {
        if (userId == null) {
            return;
        }
        remove(query().getWrapper().eq("user_id", userId));
    }

    @Override
    public PaginationResult<PointLedger> paginate(
            int page,
            int size,
            Integer userId,
            PointLedgerType type,
            Integer operatorAdminId,
            String keyword,
            String startTime,
            String endTime) {
        int pageSize = normalizedPageSize(size);
        int offset = pageOffset(page, pageSize);
        List<PointLedger> ledgers =
                getBaseMapper()
                        .paginate(
                                userId,
                                type,
                                operatorAdminId,
                                keyword,
                                startTime,
                                endTime,
                                offset,
                                pageSize);

        PaginationResult<PointLedger> result = new PaginationResult<>();
        result.setData(ledgers == null ? List.of() : ledgers);
        result.setTotal(
                getBaseMapper()
                        .paginateCount(userId, type, operatorAdminId, keyword, startTime, endTime));
        return result;
    }

    private int normalizedPageSize(int size) {
        if (size <= 0) {
            return 10;
        }
        return Math.min(size, 100);
    }

    private int pageOffset(int page, int pageSize) {
        if (page <= 1) {
            return 0;
        }
        long offset = (long) (page - 1) * pageSize;
        return offset > Integer.MAX_VALUE ? Integer.MAX_VALUE : (int) offset;
    }
}
