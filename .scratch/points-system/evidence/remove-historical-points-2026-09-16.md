# 删除历史积分迁移验证（2026-09-16）

用户确认项目尚未上线，删除整套旧积分清零、历史课程奖励补发、干跑与备份确认、迁移状态闸门、专用表以及 PC/H5 历史提示，并授权必要的本地数据库操作。

## 实施范围

- 删除后端积分 migration 包、专用 SQL、历史流水类型、历史提示字段与确认接口及相关测试依赖。
- 移除数据库初始化中的两张专用表和历史流水类型约束选项；保留正常课程奖励、兑换和结构初始化机制。
- 移除 PC/H5 历史提示及 API、后台历史类型筛选和迁移环境配置。
- 同步现行规格、问题单、词汇和上线清单。ADR-0008 标为 superseded，ADR-0022 保留 credit1 余额决定并取消首发清零要求。
- 既往本地验证记录仅保留为历史证据，不代表现行上线步骤。

## 本地数据库

目标仅为已核验的 `127.0.0.1:3306/playedu`。

- 变更前：2 名学员、15 条正常流水（7 条人工调整、8 条兑换）、8 条兑换记录、0 条历史补发流水；迁移状态 1 条，历史汇总 0 条。
- 先生成两张专用表的本地备份 `local-historical-points-removal-backup.sql`，已加入此目录 .gitignore，不提交数据库内容。
- 删除 point_migration_state、point_historical_reward_summaries 及它们对应的两条 migrations 登记。
- point_ledgers 类型约束仅接受 COURSE_COMPLETION、REDEMPTION、MANUAL_ADJUSTMENT。
- 变更后：15 条流水、8 条兑换记录保留，余额与流水差异为 0，专用表和迁移登记剩余均为 0。未清零或删除学员数据。

## 验证

- Admin、PC、H5 的 TypeScript 检查及 Vite 生产构建均通过。
- 后端缓存 Maven 执行 `-o clean test` 成功，包含真实临时 MySQL/Redis 集成测试。
- 测试汇总：{'tests': 92, 'failures': 0, 'errors': 0, 'skipped': 0}
- 本次修改的 Java 文件已执行 Spotless apply/check 并通过。
- 全库 Spotless check 发现 playedu-common 的既有行尾格式差异；未修改那些无关文件。
- `git diff --check` 通过；运行源码无历史积分迁移引用。
- 后端 `-o -DskipTests package` 通过，已核验最终 JAR 中的积分模块无历史迁移类。
