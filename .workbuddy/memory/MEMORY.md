# PlayEdu 改造项目 - 长期备忘

## 项目定位（2026-09-06 确定）
- 工作区 `C:\Users\86198\Desktop\play`（PlayEdu 2.2 开源版，白书科技版权）。
- 简历角色：**"单机开源系统分布式化改造"叙事**，与毕设（独立全栈）、paicoding（源码深度）差异化。须保留版权头与 "Designed By PlayEdu"，简历表述为"基于开源项目二次开发与分布式改造"。
- 技术栈：Spring Boot 3.3.4 / Java 17 / MyBatis Plus 3.5.7 / Sa-Token 1.39(JWT) / Redis 7.4 + Redisson / MySQL 8 / Maven 多模块（common/system/course/resource/points/api）/ React18×3 前端 / Docker Compose。

## 交付状态（2026-09-11 已验证完成，非计划）
两条主线均已落地，**用"已完成"口径描述，不要再按计划口径写**。

### A. Redis 分布式化（原 P0，已交付）
- `playedu-common/redis/`：`RedisRuntimeConfiguration`（RedissonClient + 启动 PING 强校验，不可用则不进入可服务状态）、`RedisDistributedLock`（Redisson，subject 经 MD5 归一化，**锁释放挂 TransactionSynchronization，事务提交后才解锁**）、`ApiRequestRateLimiter`（Redisson 令牌桶 OVERALL）、`LoginFailureTracker`、`RedisKeyspace`（key 前缀 `playedu`）。
- 学习租约：`playedu-course/service/ActiveLearningLeaseService`，30s TTL Hash + 10s 心跳 + Lua 状态机（CREATED/CONTINUED/TOO_EARLY/BASELINE_RESET/CONFLICT/INVALID_SESSION/STOPPED/NOT_ACTIVE），同一学员同一时刻仅一个课时计时。
- 每日学习榜：`playedu-course/service/impl/DailyLearningRankingService`，ZSet 查询投影 + Lua 重建/对账脚本 + ready 标记；MySQL 为事实源，事务提交后异步投影；`durationByUserAndDate` 用权威总分对齐而非累加增量（防重建竞态）。
- 权威学习事实：`LearningFactPersistenceService`（单事务原子 UPSERT，唯一索引防重复统计）。
- 已删除旧 JVM 内存态三件套（MemoryCacheUtil / MemoryDistributedLock / MemoryRateLimiterServiceImpl）→ 无降级双模，Redis 缺失即明确失败。

### B. 积分商城（原 P1，已交付，实际比计划更完整）
- 独立 Maven 模块 `playedu-points`，包 `xyz.playedu.points`。
- 模型：`point_ledgers` 不可变流水（delta / balance_after / type / **source_key 幂等键** / reason / operator_admin_id）+ `users.credit1` 作为当前余额。
- `PointBalanceServiceImpl`：幂等靠先查 source_key → `lockCredit1`(FOR UPDATE) → 唯一索引 `insertIfAbsent` 兜底 → 条件更新 `applyCredit1DeltaIfNonNegative`（影响 0 行即余额不足），防超扣。
- `PointRedemptionServiceImpl`：幂等键 (user_id, request_key)，商品行锁 + `findAvailableForUpdate` 码行锁 + 锁定学员校验；余额扣减/码发放/兑换记录同事务。
- 兑换码：`PointCodeCryptoService` 密文落库，独立密钥 `PLAYEDU_POINTS_CODE_ENCRYPTION_KEY`（禁止复用 JWT 密钥）；`AdminLogAspect` 对码值脱敏。
- 首发从零开始（2026-09-16）：历史积分清零、课程奖励补发、迁移状态闸门、迁移配置和学员提示均已删除；保留正常首次课程完成奖励及数据库结构初始化。
- 权限：积分人工调整仅超管，强制 reason + operator 留痕。
- 前端：`playedu-admin/src/pages/points/index.tsx`、PC/H5 积分中心。
- 决策记录：`docs/adr/0001~0022`（22 篇 ADR），上线清单 `docs/points-launch-checklist.md`。

### C. 验收与测试证据（写简历只能引用这些真实数字）
- 当前后端测试总数 **92**（2026-09-16）：完整 clean test 通过，失败、错误、跳过均为 0；Testcontainers 使用真实 MySQL/Redis。删除历史迁移前的 101 项数字仅属此前验收记录。
- 多实例验收：`docker/acceptance/compose.yml`（Nginx:9701 + api-1/api-2 + Redis + MySQL + justb4/jmeter）、JMeter 计划 `docker/acceptance/jmeter/multi-instance.jmx`、驱动器 `scripts/acceptance/run.ps1`、文档 `docs/acceptance/multi-instance-learning.md`。
- 实测（`evidence/multi-instance-report.md`，2026-09-09）：锁探针进入 1 / 竞争失败 1（业务码 42301）；共享限流 24 请求共享 12/60s 配额，命中 429 共 12；路由确实落 api-1 + api-2；榜单查询 P95 **10ms**；提交到榜单可见 59ms/20ms。
- ⚠️ 该报告里"吞吐量 421/s"只有 8 个样本、"锁 P95 7015ms"是 5s 等待超时导致，**都不是有意义的性能数据，不可写入简历**。
- 本地历史迁移专用表及登记已清理（2026-09-16）；已有 15 条正常积分流水、8 条兑换记录保留，余额与流水差异 0。旧迁移执行记录仅作历史证据，当前已无迁移闸门。

## 关键代码位置速查
- Redis 基建：`playedu-common/redis/`
- 积分模块：`playedu-api/playedu-points/src/main/java/xyz/playedu/points/`
- 建表迁移：`playedu-system/checks/MigrationCheck.java` 的 TABLE_SQL 列表
- 事件钩子：`playedu-course/event/` + `playedu-api/event/` + `listener/`
- 验收脚本：`scripts/acceptance/run.ps1`（-Action start|status|probe|load|report|full|stop）
- 需求与 issue 记录：`.scratch/redis-distributed-learning/`、`.scratch/points-system/`（含 evidence）

## 简历写法约束
- 必须写成"基于开源项目 PlayEdu 二次开发 + 分布式化改造"，不可声称独立开发整个系统。
- 不许编造压测数字；只引用上述确定性结论（锁互斥 1/1、限流 429 占比 12/24、榜单 P95 10ms）。
