# 积分模块结构整理验收

日期：2026-09-16。验收对象为当前工作区；本次文档提交不包含工作区原有业务修改和历史迁移文件删除。

## 完成项

| 阶段 | 结果 |
|---|---|
| 01 类型归位 | PointBalanceException 移至 exception；PointBalanceChange、PointBalanceChangeResult 移至 types；内容和行为不变，相关源码引用同步更新。提交：7b4a475。 |
| 02 动态 SQL | 四个实体 Mapper 的 8 条 paginate/paginateCount SQL 和 4 个共享结果映射迁入 resources/mapper。固定查询、锁和幂等写入 SQL 保留注解。提交：85de6a4。 |
| 格式修复 | 统一后端换行、排版和 import；全库 Spotless 检查恢复通过。修复保留在工作区。 |
| 03 文档验收 | 更新后端指南的目录职责、类型位置、XML 和测试清单、结构对比与积分文件数，并核对本地文件链接。 |

积分模块当前有 30 个正式 Java 文件、4 个资源 XML、7 个测试 Java 文件及 1 个 pom.xml，共 42 个文件。余额仍存于 users.credit1。

未执行的后续可选项：提取 types/paginate、拆分 PointAdminController、统一超级管理员权限表达、增加缓存或调整 Service 继承方式。

## 检查结果

本轮仅修改文档，按 issue 03 的“没有新改动时复用已完成的有效检查”复用以下结果：

| 检查 | 结果与有效性 |
|---|---|
| 迁移前后查询测试 | 同一组 4 个真实 MySQL 查询测试在迁移前后均通过，无跳过。 |
| 完整 Maven 测试 | 22:10 完成 mvnw.cmd clean test：96 个测试，0 失败、0 错误、0 跳过，涵盖余额事务和幂等、课程奖励、并发兑换、权限、兑换码加密导入及删除学员积分数据。 |
| Spotless 和打包 | 格式修复后于 22:16 完成 mvnw.cmd spotless:check package -DskipTests：全库检查和打包通过。此命令跳过测试运行，完成源码及测试源码编译；与上述完整测试结果分别记录。 |
| XML 打包 | playedu-points-1.0.jar 包含四个 Mapper XML。 |

完整测试后仅进行了 Spotless 格式修复和本轮文档更新，业务实现及 SQL 未再次改动，因此复用已有完整测试。此前的 Spotless 失败已由格式修复解决，不属于当前未通过项。

测试环境：Java 17.0.17、Docker Desktop 29.7.2、Testcontainers 1.21.4，实际启动 MySQL 8.0 和 Redis 7.4 容器。没有因环境缺失而跳过的集成测试；本轮未新增前端源码修改，未运行前端构建。此验收覆盖现有自动化用例，不包含生产部署或人工端到端验收。

原始运行日志保留在本地 .scratch/points-structure-cleanup/ 下：baseline-02.log、queries-02.log、final-tests-02.log、spotless-verify.log。前两阶段详细记录为 validation-01.md、validation-02.md。

复现命令（在 playedu-api 父工程目录通过 pwsh 执行，顺序运行）：

```powershell
.\mvnw.cmd test
.\mvnw.cmd spotless:check package -DskipTests
```

## 范围核对

类型迁移仅更改 package/import；动态 SQL 的结构比对确认条件、参数、排序、LIMIT 和时间包含边界一致，固定 SQL 保持原样。未因结构整理更改路由、JSON、数据库、权限、事务、来源键、密文格式或业务规则。

本次提交仅包含指南和验收文档。已有工作区修改与删除保持原状态，格式修复未混入文档提交。

指南的 444 个本地文件链接全部有效；积分逐文件清单与实际 42 个文件逐项一致，无缺失或多余项。

## Standards

零发现。目录职责和清单符合仓库结构规范，文件链接有效，验证结果与未覆盖范围分别说明。

## Spec

零发现。文档对应实际类型位置、SQL 和文件清单；日志支持所记录的测试、格式检查和打包结果，复用依据及未执行可选项明确。

两路审查各 0 项发现，无遗留问题。
