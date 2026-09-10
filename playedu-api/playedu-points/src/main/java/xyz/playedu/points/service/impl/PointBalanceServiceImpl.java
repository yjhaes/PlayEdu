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

import java.util.Date;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import xyz.playedu.points.domain.PointLedger;
import xyz.playedu.points.domain.PointLedgerType;
import xyz.playedu.points.mapper.PointBalanceMapper;
import xyz.playedu.points.mapper.PointLedgerMapper;
import xyz.playedu.points.service.PointBalanceChange;
import xyz.playedu.points.service.PointBalanceChangeResult;
import xyz.playedu.points.service.PointBalanceException;
import xyz.playedu.points.service.PointBalanceService;

/** Transactional engine for changes to {@code users.credit1}. */
@Service
public class PointBalanceServiceImpl implements PointBalanceService {

    private final PointBalanceMapper pointBalanceMapper;
    private final PointLedgerMapper pointLedgerMapper;

    public PointBalanceServiceImpl(
            PointBalanceMapper pointBalanceMapper, PointLedgerMapper pointLedgerMapper) {
        this.pointBalanceMapper = pointBalanceMapper;
        this.pointLedgerMapper = pointLedgerMapper;
    }

    @Override
    @Transactional
    public PointBalanceChangeResult apply(PointBalanceChange change) {
        validateSourceKey(change);

        PointLedger existing = pointLedgerMapper.findBySourceKey(change.sourceKey());
        if (existing != null) {
            return new PointBalanceChangeResult(existing, false);
        }

        validate(change);
        Integer currentBalance = pointBalanceMapper.lockCredit1(change.userId());
        if (currentBalance == null) {
            throw new PointBalanceException("学员不存在");
        }

        existing = pointLedgerMapper.findBySourceKeyForUpdate(change.sourceKey());
        if (existing != null) {
            return new PointBalanceChangeResult(existing, false);
        }

        int balanceAfter;
        try {
            balanceAfter = Math.addExact(currentBalance, change.delta());
        } catch (ArithmeticException exception) {
            throw new PointBalanceException("积分余额超出整数范围", exception);
        }
        if (change.type() == PointLedgerType.REDEMPTION && balanceAfter < 0) {
            throw new PointBalanceException("积分余额不足");
        }

        PointLedger ledger = newLedger(change, balanceAfter);
        if (pointLedgerMapper.insertIfAbsent(ledger) == 0) {
            existing = pointLedgerMapper.findBySourceKeyForUpdate(change.sourceKey());
            if (existing == null) {
                throw new PointBalanceException("积分流水幂等键冲突");
            }
            return new PointBalanceChangeResult(existing, false);
        }

        int updatedRows =
                change.type() == PointLedgerType.REDEMPTION
                        ? pointBalanceMapper.applyCredit1DeltaIfNonNegative(
                                change.userId(), change.delta())
                        : pointBalanceMapper.applyCredit1Delta(change.userId(), change.delta());
        if (updatedRows != 1) {
            throw new PointBalanceException("积分余额更新失败");
        }

        return new PointBalanceChangeResult(ledger, true);
    }

    private PointLedger newLedger(PointBalanceChange change, int balanceAfter) {
        PointLedger ledger = new PointLedger();
        ledger.setUserId(change.userId());
        ledger.setDelta(change.delta());
        ledger.setBalanceAfter(balanceAfter);
        ledger.setType(change.type());
        ledger.setSourceKey(change.sourceKey());
        ledger.setReason(change.reason());
        ledger.setOperatorAdminId(change.operatorAdminId());
        ledger.setCreatedAt(new Date());
        return ledger;
    }

    private void validateSourceKey(PointBalanceChange change) {
        if (change == null) {
            throw new PointBalanceException("积分变更不能为空");
        }
        if (change.sourceKey() == null || change.sourceKey().isBlank()) {
            throw new PointBalanceException("积分变更必须提供source_key");
        }
        if (change.sourceKey().length() > 191) {
            throw new PointBalanceException("积分变更source_key长度不能超过191个字符");
        }
    }

    private void validate(PointBalanceChange change) {
        if (change.userId() == null) {
            throw new PointBalanceException("积分变更必须提供学员ID");
        }
        if (change.delta() == null || change.delta() == 0) {
            throw new PointBalanceException("积分变更值不能为零");
        }
        if (change.type() == null) {
            throw new PointBalanceException("积分流水类型不能为空");
        }
        switch (change.type()) {
            case COURSE_COMPLETION, HISTORICAL_COURSE_COMPLETION -> requirePositiveDelta(change);
            case REDEMPTION -> requireNegativeDelta(change);
            case MANUAL_ADJUSTMENT -> validateManualAdjustment(change);
        }
    }

    private void requirePositiveDelta(PointBalanceChange change) {
        if (change.delta() <= 0) {
            throw new PointBalanceException("课程奖励必须为正积分");
        }
        if (change.reason() != null || change.operatorAdminId() != null) {
            throw new PointBalanceException("普通积分流水不能包含人工调整信息");
        }
    }

    private void requireNegativeDelta(PointBalanceChange change) {
        if (change.delta() >= 0) {
            throw new PointBalanceException("兑换必须为负积分");
        }
        if (change.reason() != null || change.operatorAdminId() != null) {
            throw new PointBalanceException("兑换流水不能包含人工调整信息");
        }
    }

    private void validateManualAdjustment(PointBalanceChange change) {
        if (change.reason() == null || change.reason().isBlank()) {
            throw new PointBalanceException("人工调整必须填写原因");
        }
        if (change.operatorAdminId() == null) {
            throw new PointBalanceException("人工调整必须保留操作者ID");
        }
        if (!change.operatorIsSuperAdmin()) {
            throw new PointBalanceException("只有积分超级管理员可以调整积分");
        }
    }
}
