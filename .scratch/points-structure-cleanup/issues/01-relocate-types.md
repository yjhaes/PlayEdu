# 01 积分输入、结果及异常归位

Status: resolved
Priority: P1

## 范围

按 [整理计划](C:/Users/86198/Desktop/play/.scratch/points-structure-cleanup/spec.md) 第一阶段，将 PointBalanceException 从 service 移至 exception，将 PointBalanceChange 和 PointBalanceChangeResult 从 service 移至 types。

## 实施步骤

1. 检查工作区与测试基线。
2. 移动三个源码文件，修改 package，保留类型内容和行为。
3. 更新 points、course、api 及测试中的 import 和全限定名。
4. 检查旧引用并运行 Maven 测试、格式检查。

## 验收

- 新目录与包名一致，旧包引用无残留。
- 现有积分、课程奖励和 API 测试通过；记录集成测试环境与跳过项。
- 无 API、数据库、业务规则或前端变化。

## Comments

本任务已规划，尚未执行。

2026-09-16：第一阶段已完成，三个类型归位且源码及暂存树旧引用清零。`mvnw.cmd clean test` 92 个测试全部通过、无跳过；独立打包成功。Spotless 遇到原有 common 换行格式及 API 测试未使用 import 问题，未扩大整理范围。两路审查均无遗留发现。详见 [验收记录](../validation-01.md)。
