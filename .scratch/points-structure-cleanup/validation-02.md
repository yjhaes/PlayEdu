# 02 动态 SQL 迁移评估与验收

日期：2026-09-16

## 评估结论

执行迁移。四个 Mapper 的分页和计数方法包含多项条件，集中到 `resources/mapper` 可直接对照列表、计数的筛选规则和结果映射，并与已有课程模块的阅读位置保持一致。

| Mapper | 候选方法 | 结果映射及共享固定查询 |
|---|---|---|
| PointProductMapper | paginate、paginateCount | pointProductResultMap；selectByIdForUpdate |
| PointCodeMapper | paginate、paginateCount | pointCodeResultMap；findByDigest、findAvailableForUpdate |
| PointLedgerMapper | paginate、paginateCount | pointLedgerResultMap；findBySourceKey、findBySourceKeyForUpdate |
| PointRedemptionMapper | paginate、paginateCount | pointRedemptionResultMap；findByIdAndUserId、findByUserIdAndRequestKey、findByUserIdAndRequestKeyForUpdate |

8 条动态查询和 4 个结果映射迁入对应 Mapper XML。分页方法移除 SQL 注解；固定查询保留 SQL 注解并引用同名 XML resultMap。计数使用 long 映射；code status 和 ledger type 的显式枚举类型保留。namespace、方法 ID 和 @Param 名对应不变。

固定查询、FOR UPDATE、条件发放、幂等写入及所有其他写入 SQL 保持原样。SQL 结构逐项比对通过，包含 if 条件、WHERE、排序、LIMIT 和包含等号的时间边界。未改配置、API、数据库或前端；已有用户修改及删除状态保留。

## 验证

- 新增 PointPaginationMapperIntegrationTest，使用实际 MySQL 8.0：空/null 筛选、单条件与组合条件、列表/计数、精确 digest、keyword 两条匹配路径、时间包含边界、分页及同时间 id 排序、枚举和字段映射。
- 同一组 4 个查询测试在迁移前及迁移后均通过，无跳过。
- 本轮 4 个 Mapper 和新增测试的 Spotless 检查通过。
- 全库 Spotless 仍被既有 common 格式问题阻断（UserMapper、UserServiceImpl、UserService），未扩展清理范围。
- Standards 与 Spec 两路审查均为零发现。

- 最终 `mvnw.cmd clean test` 成功：96 个测试，0 失败、0 错误、0 跳过，包含新增查询、并发兑换、事务幂等、课程奖励及 API 集成测试。Java 17.0.17、Docker Desktop 29.7.2、Testcontainers 1.21.4，实际使用 MySQL 8.0 和 Redis 7.4 容器。
- 测试完成后顺序执行 `mvnw.cmd package -DskipTests` 成功，积分 JAR 包含四个 Mapper XML。

## Standards

零发现。XML 及测试位于 AGENTS.md 规定的模块目录；未新增多余抽象或可行动异味。

## Spec

零发现。筛选、排序、分页、时间边界及结果映射保持一致，未发现重复 statement 或 ID 冲突；固定 SQL、锁、幂等和事务逻辑未变。

两路审查各 0 项发现，无遗留问题。
