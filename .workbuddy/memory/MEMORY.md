# PlayEdu 改造项目 - 长期备忘

## 项目定位（2026-09-06 确定）
- 工作区 `C:\Users\86198\Desktop\play`（PlayEdu 2.2 开源版，白书科技版权）。
- 简历角色：**"单机开源系统分布式化改造"叙事**，与毕设（独立全栈）、paicoding（源码深度）差异化。须保留版权头与 "Designed By PlayEdu"，简历表述为"基于开源项目二次开发与分布式改造"。
- 技术栈：Spring Boot 3.3.4 / Java 17 / MyBatis Plus 3.5.7 / Sa-Token 1.39(JWT) / Maven 多模块（common/system/course/resource/api）/ React18×3 前端 / Docker Compose。

## 最终改造路线（1.5~2 周量级，穿插在 paicoding 70% 主线下）
1. **P0 Redis 分布式化**（3-4 天）：Spring Data Redis + Redisson；新写 `RedisCacheUtil`/`RedisDistributedLock`/`RedisRateLimiterService`，配置开关双模切换（memory/redis），业务零侵入；顺手做缓存穿透/击穿/雪崩防御。
2. **P1 积分系统**（4-5 天）：`credit_rules`（规则表，事件驱动发放，监听现有 UserCourseHourFinishedEvent/UserLoginEvent）+ `user_credit_logs`（流水，余额复用 users.credit1，幂等靠 user_id+scene+biz_id 唯一索引）+ `credit_goods`/`credit_orders`（商城，条件 UPDATE 防超扣）+ 排行（Redis ZSet）。管理端复用 @Log/@BackendPermission/@Lock。只做虚拟权益兑换，不做实物物流。
3. **P2 压测验证**（1-2 天）：JMeter 前后对比 QPS/P99/DB 查询量，数据填进简历 bullet。

## 关键代码位置
- 内存缓存三件套：`playedu-common/util/MemoryCacheUtil.java`、`MemoryDistributedLock.java`、`common/service/impl/MemoryRateLimiterServiceImpl.java`
- 业务缓存：`playedu-course/caches/`（UserCanSeeCourseCache、UserLastLearnTimeCache）、`playedu-api/cache/`（LoginLimitCache、LoginLockCache）
- 建表迁移：`playedu-system/checks/MigrationCheck.java` 的 TABLE_SQL 列表
- 事件钩子：`playedu-api/event/` + `listener/`
- 管理端缓存查看接口：`CacheController`（/backend/v1/cache/list）
