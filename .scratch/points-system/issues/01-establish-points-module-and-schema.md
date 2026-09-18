# 01: 建立积分模块与数据结构

**What to build:** 新建独立 `playedu-points` Maven 模块，并建立积分流水、兑换商品、兑换码和兑换记录的数据结构、索引与约束；不新建积分账户表。

**Blocked by:** None (can start immediately)

**Status:** ready-for-agent

- [ ] 父 Maven 项目声明 `playedu-points`，运行模块依赖它，模块依赖方向不形成循环。
- [ ] 创建 `point_ledgers`、`point_products`、`point_codes`、`point_redemptions` 表及领域对象、Mapper 和基础 Service。
- [ ] `point_ledgers.source_key` 具备全局唯一约束，并建立学员流水分页索引。
- [ ] `point_codes.code_digest` 全局唯一，`point_redemptions.code_id` 唯一。
- [ ] 兑换码状态仅允许 `AVAILABLE`、`DELIVERED`；商品状态仅允许 `ON_SALE`、`OFF_SALE`。
- [ ] 兑换码表不包含 `user_id`、批次、有效期或撤销字段。
- [ ] 迁移沿用仓库迁移登记机制，可重复启动且不会重复建表或约束。
- [ ] Maven 格式检查和测试通过。

