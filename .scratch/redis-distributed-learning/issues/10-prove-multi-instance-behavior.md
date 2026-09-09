# 10: 证明多实例行为并完成交付文档

**What to build:** 在实际负载均衡的双 API 实例拓扑中，完成分布式锁、共享限流、学习租约和实时学习榜的端到端验收，并交付可复现的测试结果与运维文档，使维护者能够证明系统在跨实例并发和 Redis 数据丢失场景下仍符合既定行为。

**Blocked by:** 07: 让 PC 与 H5 使用学习租约; 08: 交付可重建的实时学习榜; 09: 移除遗留内存状态实现

**Status:** ready-for-agent

- [x] 建立包含两个 API 实例、Redis、MySQL 和负载均衡入口的验收拓扑，并记录启动、健康检查和停止命令。
- [x] JMeter 测试计划覆盖分布式锁、共享限流和学习租约，并能由仓库中的复现命令非交互运行。
- [x] 测试证据表明请求确实由负载均衡入口分发到两个 API 实例，而不是只命中单一实例。
- [x] 锁场景证明同一业务锁跨实例最多只有一个执行者进入受保护区，并记录竞争失败的可观察结果。
- [x] 限流场景证明两个实例共享同一总配额，请求容量不会因实例数量增加而翻倍。
- [x] 学习租约场景证明同一用户跨实例最多只有一个活跃课时，并覆盖冲突、续租和租约过期后的行为。
- [x] 负载测试报告记录每个核心场景的吞吐量、错误率、P95 和 P99，并记录排行榜从权威数据提交到可查询结果的延迟。
- [x] 清空或重启 Redis 后，实时学习榜能够从 MySQL 权威数据自动或手动重建；重建前后的榜单一致性和完成时间有可复现证据。
- [x] README 包含系统架构图、Redis 与 MySQL 的职责边界、关键故障策略、排行榜重建流程，以及完整的本地复现命令。
- [x] 后端完整 Maven 测试通过，管理端、PC 端和 H5 端生产构建全部通过，执行命令与结果摘要记录在交付文档中。

## Comments

- 2026-09-09：交付双 API（9702/9703）+ Redis + MySQL + Nginx（9701）验收拓扑，复现入口为 pwsh -NoProfile -File scripts/acceptance/run.ps1 -Action full，停止命令为 ... -Action stop。
- HTTP 探针实测路由实例为 api-1, api-2；跨实例分布式锁为 1 次进入、1 次 42301 竞争失败；学习租约覆盖 CONFLICT、CONTINUED、CREATED、STOPPED。
- JMeter 非 GUI 计划共 40 个样本：共享限流 24 次中 12 次预期 429（总配额未按实例翻倍）；报告记录吞吐量、错误率、P95/P99。HTTP 探针实测权威学习提交到榜单可查询为 59 ms，Redis 清空后的榜单重建为 20 ms，重建前后一致。
- 后端 .\mvnw.cmd -B spotless:check package、admin/PC/H5 生产构建均通过。详细架构、故障策略、重建流程和命令见 docs/acceptance/multi-instance-learning.md；原始 probe.json、JTL 和报告写入本地 .scratch/redis-distributed-learning/evidence/。
