# 01 类型归位验收记录

日期：2026-09-16

- PointBalanceException 已迁至 exception，PointBalanceChange、PointBalanceChangeResult 已迁至 types。逐字比对确认除 package 外内容不变。
- points、course、API 及测试引用同步更新。工作区 Java 源码和 Git 暂存树均无旧包引用。
- 工作区已有业务修改、前端修改及历史迁移文件删除状态保留。提交中两个已删除历史文件只更新 import，以保证提交树引用完整。
- 基线 `./mvnw.cmd test` 成功，无跳过。测试环境为 Java 17.0.17、Docker Desktop 29.7.2、Testcontainers 1.21.4，实际启动 MySQL 8.0 和 Redis 容器。
- 独立 `./mvnw.cmd package -DskipTests` 成功。
- 基线和最终 `spotless:check` 均被 common 原有格式问题阻断：UserMapper.java、UserServiceImpl.java、UserService.java。
- 限定迁移文件的 Spotless 检查：points、course 通过；API 仅 PointAdminControllerTest 原有未使用的 Mockito.never import 失败，已通过迁移前快照确认。未扩大范围清理。
- Standards 和 Spec 两路审查最终均为零发现。审查发现的两个历史文件暂存树旧 import 已修复并复查。
- 首次最终测试与打包并行导致运行中课程类缺失，该结果作废；改为 `./mvnw.cmd clean test` 顺序重跑。
- 最终 `./mvnw.cmd clean test` 成功：92 个测试，0 失败、0 错误、0 跳过，包含积分、课程奖励、API 和删除学员集成测试。
