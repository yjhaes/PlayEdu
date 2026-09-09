# 01: 建立 Redis 运行基线

**What to build:** 让开发者和运维人员能够以 Redis 作为强制依赖启动 PlayEdu，并获得可复用的真实 Redis 集成测试环境。应用必须清楚暴露 Redis 连接状态，且不提供进程内降级。

**Blocked by:** None (can start immediately)

**Status:** ready-for-agent

- [ ] 应用能够通过外部配置连接独立运行的 Redis；Docker Compose 单节点 Redis 作为可选演示方式，而不是强制依赖。
- [ ] Redis 主机、端口、认证和数据库选择均可由环境配置提供，不在源码中保存凭据。
- [ ] 应用使用 Spring Data Redis 处理字符串、Lua 与 ZSet，使用 Redisson 处理锁与令牌桶。
- [ ] Redis 在启动阶段不可用时，应用不能进入可服务状态，健康检查能够反映原因。
- [ ] 所有 Redis key 具备统一 PlayEdu 命名空间，且命名细节不暴露给 Controller 或 Interceptor。
- [ ] 建立 Testcontainers Redis 测试基线，并以真实 Redis 完成连接、读写和过期 smoke test。
- [ ] 本地 Redis 不依赖持久化保证业务正确性，重启行为与 ADR 保持一致。
- [ ] Maven 测试通过，容器启动说明可被另一位开发者复现。
