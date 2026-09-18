# 积分模块结构整理计划

日期：2026-09-16

本文件记录实施计划及完成状态。现有积分模块已经沿用项目的 Maven 与分层结构；整理重点是类型归位和阅读一致性，不需要重建模块。

2026-09-16：第一阶段类型归位、第二阶段动态 SQL 迁移和第三阶段文档验收均已完成。后端指南已更新；完整测试 96 项通过、无跳过，格式修复后的全库 Spotless 检查及打包通过。后续可选项未执行。详见 [最终验收记录](C:/Users/86198/Desktop/play/docs/points-structure-cleanup-validation.md)。下文保留各阶段的实施要求。

## 目标与范围

让开发者能够沿“接口 → 业务服务 → 数据访问”的路径快速定位积分逻辑，同时保留现有事务、幂等、权限及兑换规则。

主要涉及 `playedu-points`；调整类型包名时同步修改 `playedu-api`、`playedu-course` 和各模块测试中的引用。数据库迁移仍位于 `playedu-system`，本计划不改变表结构或迁移机制。

背景见 [后端文件职责指南](C:/Users/86198/Desktop/play/docs/backend-file-guide.md)。

## 整理后的职责边界

| 目录 | 职责 | 本轮处理 |
|---|---|---|
| `domain` | 持久化实体及业务状态枚举 | 保留 |
| `service` | 对外业务接口与来源键辅助定义 | 移出积分变动输入、结果和异常；保留 PointSourceKeys |
| `service/impl` | 校验、事务、幂等、库存及兑换编排 | 保留现有接口继承与构造器注入 |
| `mapper` | 数据库访问接口及必要的 SQL 映射 | 第一阶段保留注解 SQL；较长动态 SQL 单独评估 |
| `types` | 输入与结果对象，包括变动请求及导入结果 | 接收 PointBalanceChange 和 PointBalanceChangeResult |
| `exception` | 积分业务异常 | 新建，接收 PointBalanceException |
| `crypto` | 兑换码加解密、规范化和指纹 | 保留 |
| `migration` | 当前为空 | 清理工作区空目录即可，无需添加占位文件 |

第一阶段目标结构：

```text
xyz/playedu/points/
├── crypto/
├── domain/
├── exception/
│   └── PointBalanceException.java
├── mapper/
├── service/
│   ├── PointBalanceService.java
│   ├── PointCodeService.java
│   ├── PointLedgerService.java
│   ├── PointProductService.java
│   ├── PointRedemptionService.java
│   ├── PointSourceKeys.java
│   └── impl/
└── types/
    ├── PointBalanceChange.java
    ├── PointBalanceChangeResult.java
    └── PointCodeImport*.java
```

## 第一阶段：类型归位，优先执行

对应任务：[01 类型归位](C:/Users/86198/Desktop/play/.scratch/points-structure-cleanup/issues/01-relocate-types.md)。

1. 记录工作区已有修改，确认本轮涉及的类仍与计划一致。运行 Maven 测试和格式检查建立基线；已有失败单独记录。
2. 将 `service/PointBalanceException.java` 移至 `exception/`，修改 package，保留继承关系和异常语义。
3. 将 `service/PointBalanceChange.java`、`service/PointBalanceChangeResult.java` 移至 `types/`，保留 record 字段、辅助方法和构造语义。
4. 更新积分模块、课程奖励、API 人工调整与相关测试的 import、全限定名引用。
5. 检查旧包名残留，运行 Maven 测试及 Spotless 检查。仅因移动类型产生的引用修改纳入本轮。

验收：三个类归位，旧引用消除，编译与现有测试通过，接口 JSON、业务返回及异常处理行为不变。

## 第二阶段：SQL 阅读位置统一，按需执行

对应任务：[02 动态 SQL 整理评估](C:/Users/86198/Desktop/play/.scratch/points-structure-cleanup/issues/02-align-dynamic-sql.md)。

先统计 PointProductMapper、PointCodeMapper、PointLedgerMapper 和 PointRedemptionMapper 中的较长动态查询。若当前团队希望统一从 `resources/mapper` 阅读 SQL，再将分页筛选等较长查询迁至 XML；简单固定 SQL 和专用余额更新可以继续保留注解。

迁移时明确每个方法使用注解还是 XML，避免同一 statement 重复注册。同步处理 namespace、方法 ID、@Param 名、结果映射、枚举映射及跨语句 @ResultMap 引用，不只复制 SQL 文本。

保持 WHERE 条件、排序、LIMIT、FOR UPDATE、幂等写入和条件余额更新的语义。不得在纯结构整理中顺带“优化”事务 SQL。

验收：受影响的查询、枚举映射及并发兑换测试通过，结果和锁行为一致。本阶段可以独立跳过，不阻碍第一阶段交付。

## 第三阶段：文档和最终验收

对应任务：[03 文档与验收](C:/Users/86198/Desktop/play/.scratch/points-structure-cleanup/issues/03-document-and-validate.md)。

更新后端指南的目录说明、迁移后文件链接与结构对比结论。如果第二阶段未执行，明确注解 SQL 仍是当前实现。

在后端父工程目录使用 pwsh 执行：

```powershell
Set-Location C:/Users/86198/Desktop/play/playedu-api
.\mvnw.cmd test
.\mvnw.cmd spotless:check package -DskipTests
```

第二个命令跳过已运行的测试，只做格式检查和打包。检查测试报告中的跳过项；若 Redis/MySQL 等集成环境缺失，应列出未验证部分，不能将跳过等同于通过。没有前端源码变化时，无需增加前端构建。

重点确认现有测试覆盖的以下行为：

- 余额变动与流水写入的事务和幂等行为。
- 学习事实与首次完成奖励的提交、回滚和重复处理。
- 兑换扣分、兑换码分配及重复请求、并发请求。
- 普通管理员不能访问敏感积分操作。
- 兑换码加密及导入去重。
- 删除学员时的积分数据清理。

仅当实际改动暴露现有测试未覆盖的行为时补充测试，不为纯 package 移动添加重复测试。

## 后续可选整理，不纳入本轮

| 事项 | 启动条件 | 注意事项 |
|---|---|---|
| 提取 `types/paginate` | 分页筛选参数明显增长或出现重复传参 | 当前少量参数不必为了目录一致而包装 |
| 拆分 PointAdminController | 商品、兑换码及流水接口继续增长，维护边界不清 | 保留现有 URL、权限与日志，避免结构整理影响前端 |
| 统一超级管理员权限表达 | 明确需要统一权限入口或消除重复检查 | 普通角色可授权权限不等于超级管理员限制，不得直接替换规则 |

## 必须保留的规则

- 积分余额继续存于 users.credit1。
- 余额、流水、兑换记录和库存分配的事务及并发约束保持一致。
- 课程完成奖励来源键和兑换幂等键保持一致。
- 超级管理员限制、兑换余额校验和异常提示保持一致。
- AES-GCM 密文与 HMAC 指纹格式、字段以及密钥配置保持一致。
- 保留构造器注入，不强制所有 Service 继承 IService。
- 不增加缓存，不改 API 路由、JSON 格式、数据库结构或前端页面。

## 提交与回退

推荐分开提交：第一阶段 `refactor(points): relocate balance types and exception`；第二阶段如果执行，单独提交 `refactor(points): move dynamic queries to mapper xml`；文档整理使用 `docs(points): update module structure guide`。

回退按阶段进行，类型移动需连同所有引用一起回退。计划无数据库变更，因此不需要数据回滚。实施前应保留用户已有工作，提交或回退只包含本轮修改。
