# Issue 12 执行记录（2026-09-11）

本记录只证明当前工作区可复核的自动化验收和上线准备。没有向未知的生产数据库执行清零、历史补发、真实兑换码导入或其他破坏性操作。

## 自动化验收

在 `playedu-api` 执行：

```text
.\mvnw.cmd test
```

结果：Maven Reactor 全部成功，测试失败数和错误数均为 0。主要结果如下：

- `playedu-common`：8 项
- `playedu-system`：5 项
- `playedu-points`：38 项
- `playedu-course`：20 项
- `playedu-api`：30 项

测试使用了真实 MySQL 8.0 和 Redis 7.4 Testcontainers。与本 issue 直接相关的覆盖包括：

- `HistoricalPointsBackfillServiceIntegrationTest`：干跑聚合、备份确认、清零/补发幂等、失败回滚、迁移与在线奖励竞态。
- `LearningFactPersistenceServiceIntegrationTest`：同一学员课程并发完成只产生一次奖励，以及事实写入事务回滚。
- `PointBalanceServiceIntegrationTest`：并发流水、人工负余额、后续奖励抵扣、禁止透支和失败回滚。
- `PointRedemptionServiceIntegrationTest`：重复请求返回原结果、并发抢库存/扣余额、锁定学员、库存/余额不足，以及新增的兑换记录写入失败全事务回滚。
- `PointCodeInventoryIntegrationTest`、`PointCodeServiceTest`、`PointProductServiceTest`：库存、导入重复、敏感字段和商品状态/删除限制。
- `PointAdminControllerTest`、`PointsControllerTest`：管理员权限、积分入口、售罄商品、兑换结果和普通响应字段。
- `UserDeletionServiceIntegrationTest`：学员积分关联删除、已发放码状态保留、无法反查学员和删除失败回滚。
- `AdminLogAspectTest`：兑换码字段在管理员操作日志参数中脱敏。

新增测试单独运行结果：`PointRedemptionServiceIntegrationTest` 6 项全部通过。

## 前端构建

三个前端均使用本项目现有依赖中的 `tsc` 和 `vite` 直接完成 `tsc && vite build`：

- Admin：通过，4003 个模块转换完成。
- PC：通过，3312 个模块转换完成。
- H5：通过，1123 个模块转换完成。

直接执行 `pnpm build` 时 Corepack 因 `...\\corepack\\lastKnownGood.json` 的 EPERM 权限错误提前退出；改用各项目 `node_modules/.bin` 中已安装的本地命令后构建成功。构建仍报告既有的 Sass/API、Ant Design `use client` 和大 chunk 警告，但没有构建错误。

## 上线准备改动

- `.env.example` 增加迁移模式和备份确认变量，默认均关闭。
- `compose.yml` 将上述两个变量显式传入应用容器，避免发布清单中的模式只存在于宿主机而未进入容器。
- `docs/points-launch-checklist.md` 增加余额与流水差异、兑换码库存和异常处置的只读对账步骤。

## 当前本地环境受控迁移（2026-09-11）

按用户确认，仅针对已验证的本地 `127.0.0.1:3306/playedu` 执行，未操作远程或未知数据库：

- 迁移前备份已生成：`.scratch/points-system/evidence/local-playedu-pre-points-migration-20260911.sql`，大小 1,026,407 bytes，SHA-256 为 `650D1EB6F4E6D2A8425A6434B590BE4B63EE76EB1D8328FDEA985403C13DBF24`。备份含数据库内容，不应提交到版本库。
- Runner 干跑通过：旧 `credit1` 非零用户 0、旧积分总额 0、历史学员 0、课程完成记录 0、预计补发总额 0。
- Runner 正式执行完成，迁移键为 `20260910_00_00_05_reset_credit1_and_backfill_historical_completions`。
- 迁移后只读核验：`point_migration_state` 记录 1 条、`point_ledgers` 记录 0 条、非零 `credit1` 用户 0、余额不一致 0，积分闸门为 `OPEN`。
- 本地 API 已恢复监听 9700，健康检查为 API/DB/Redis 全部 `UP`。

以上仅证明本地开发数据库已完成受控迁移；不替代真实发布环境的备份恢复演练、生产干跑、真实兑换码导入、人工验收和上线后监控。

## 尚需真实发布环境完成

以下项目依赖真实数据库、备份平台、真实兑换码和发布操作人，本次没有伪造完成：

1. 完成可恢复数据库备份并验证隔离恢复；运行生产数据只读干跑，保存旧 `credit1` 非零人数、旧值总额、历史学员/课程数和预计补发总额。
2. 在积分入口关闭状态执行一次性清零与历史补发，核对余额、流水、迁移状态和一次性提示后再开放入口。
3. 由超级管理员创建 50 积分首个商品，导入 20 个真实兑换码并完成受控兑换 smoke test。
4. 使用真实账号完成人工 H5/PC 关键流程验收，并在开放后按清单持续监控余额/流水差异、重复导入、兑换失败和库存变化。

在上述外部发布项完成前，issue 保持 `ready-for-human`，不能据此记录为生产首发已完成。
