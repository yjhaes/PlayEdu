# 02 较长动态 SQL 整理评估

Status: resolved
Priority: P2

## 范围

本任务为可选阶段。评估 PointProductMapper、PointCodeMapper、PointLedgerMapper 和 PointRedemptionMapper 的长动态查询是否值得迁至 resources/mapper；注解 SQL 本身合法。

## 实施步骤

1. 列出候选语句、结果映射和引用关系，确认迁移带来的阅读收益。
2. 若执行迁移，逐方法迁移，避免注解和 XML 重复定义 statement。
3. 保持筛选、排序、分页、枚举映射、锁和幂等 SQL 语义。
4. 运行受影响的查询及并发/事务集成测试。

## 验收

- namespace、ID、参数和映射正确，查询结果和锁行为保持一致。
- 简单固定 SQL 无需机械迁移。
- 未执行时记录原因，不阻碍类型归位与文档交付。

## Comments

本任务待评估，尚未执行。

2026-09-16：评估后执行迁移，四个 Mapper 的 8 条分页/计数动态 SQL 和 4 个共享结果映射已迁至 resources/mapper。固定 SQL、锁及幂等写入保留，语义结构比对通过。新增 4 个 MySQL 查询测试在迁移前后均通过；完整 clean test 共 96 项通过，无跳过；本轮文件格式检查通过，全库仍有既有 common 格式问题。Standards/Spec 审查均零发现。见 [评估及验收记录](../validation-02.md)。
