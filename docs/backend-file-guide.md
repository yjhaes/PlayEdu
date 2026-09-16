# PlayEdu 后端文件职责指南

依据当前工作区文件整理。覆盖六个后端模块的源码、资源、测试与构建文件；不逐项解释 target 构建产物。每个文件均提供源码链接和职责；方法/字段列为源码提取的阅读线索，可能包含内部辅助方法，不是完整 API 清单。

阅读顺序：先看子目录作用汇总，再看各目录详细职责，最后按需查阅逐文件清单。下文的业务目录均相对于 `src/main/java/xyz/playedu/<模块名>/`；外层 `playedu-api/` 是后端父工程，内层 `playedu-api/playedu-api/` 是应用入口模块。

## 一、各模块子目录作用汇总

### 所有模块共有的工程目录

| 位置 | 作用 |
|---|---|
| `pom.xml` | 模块依赖及构建配置 |
| `src/main/java/` | 正式运行的 Java 代码 |
| `src/main/resources/` | 配置、Mapper XML 和静态资源 |
| `src/test/java/` | 单元测试与集成测试；以实际存在的文件为准 |
| `target/` | 编译产物、JAR 和测试报告 |

### playedu-api：接口入口与业务协调

| 子目录 | 作用 |
|---|---|
| `controller` | HTTP 请求接收与响应 |
| `request` | 请求参数与校验规则 |
| `interceptor` | 限流、身份检查及请求上下文管理 |
| `bus` | 登录业务编排 |
| `service`、`service/impl` | 跨模块业务接口与实现 |
| `event` | 登录、退出、创建和删除事件载体 |
| `listener` | 事件发生后的关联处理 |
| `schedule` | 定时任务 |
| `cache` | 当前为空，无源码文件 |

### playedu-common：基础业务与公共支撑

| 子目录 | 作用 |
|---|---|
| `domain` | 账号、组织、权限、配置及 LDAP 实体 |
| `service`、`service/impl` | 基础业务接口与实现 |
| `mapper` | 基础业务数据库访问 |
| `bus` | 权限聚合和 LDAP 同步编排 |
| `config` | 公共运行配置 |
| `context` | 当前请求的身份上下文 |
| `annotation` | 权限和日志注解 |
| `constant` | 公共业务常量 |
| `exception` | 通用业务异常 |
| `redis` | Redis 运行、锁、限流和登录保护 |
| `types` | 配置、分页和查询结果等数据对象 |
| `util` | 通用工具及外部系统访问工具 |

### playedu-system：初始化、迁移和横切处理

| 子目录 | 作用 |
|---|---|
| `checks` | 启动检查与初始化 |
| `migration` | 专项数据库迁移 |
| `domain` | 迁移记录实体 |
| `service`、`service/impl` | 迁移记录查询和保存 |
| `mapper` | 迁移记录数据库访问 |
| `aspectj` | 权限检查与操作日志切面 |

### playedu-course：课程内容、学习记录与统计

| 子目录 | 作用 |
|---|---|
| `domain` | 课程及学习数据结构 |
| `service` | 课程业务接口及学习流程服务 |
| `service/impl` | 课程维护、学习记录和排行榜实现 |
| `mapper` | 课程、学习记录及统计数据库访问 |
| `bus` | 学员课程访问判断 |
| `event` | 学习时长确认与排行榜更新 |
| `caches` | 当前为空，无源码文件 |

### playedu-resource：资源元数据与上传

| 子目录 | 作用 |
|---|---|
| `domain` | 文件、扩展信息和分类关联 |
| `service`、`service/impl` | 资源及上传业务接口与实现 |
| `mapper` | 资源数据库访问 |

### playedu-points：积分、商品与兑换

| 子目录 | 作用 |
|---|---|
| `domain` | 商品、兑换码、流水和兑换记录 |
| `service` | 业务接口及积分来源键辅助定义 |
| `service/impl` | 积分变动、库存维护和兑换实现 |
| `mapper` | 积分相关数据库访问接口及固定注解 SQL |
| `crypto` | 兑换码加密与指纹 |
| `types` | 积分变动输入、执行结果及兑换码批量导入结果 |
| `exception` | 积分业务异常 |
| `src/main/resources/mapper` | 四个实体 Mapper 的动态分页 SQL 及共享结果映射 |
| `migration` | 当前无 Java 源文件 |

## 二、子目录内部详细职责

### playedu-api

| 子目录 | 具体职责与主要文件 |
|---|---|
| `controller/backend` | 管理端接口：管理员与角色权限、学员与部门、课程章节课时附件、资源与上传、积分商品和兑换码、LDAP 同步记录、仪表盘 |
| `controller/frontend` | 学员端接口：登录、首页、课程详情、附件下载、课时播放、学习心跳、个人信息和积分兑换 |
| `controller/acceptance` | `AcceptanceProbeController` 提供双实例验收探针，验证实例标识、锁、学习租约及排行榜；默认关闭 |
| `controller` 根目录 | `ExceptionController` 将业务异常、参数错误和基础能力不可用等转换为接口响应 |
| `request/backend` | 管理端保存、批量导入、排序、父节点调整、分片合并、积分调整等请求参数及校验 |
| `request/frontend` | 登录、密码修改、学习心跳、停止学习、旧学习记录上报和兑换参数 |
| `interceptor` | `ApiInterceptor` 限流；`AdminInterceptor` 校验管理员并填充 BCtx；`FrontInterceptor` 校验学员并填充 FCtx；请求结束清理上下文；`WebMvcConfig` 注册拦截器并配置跨域 |
| `bus` | `LoginBus` 协调本地或 LDAP 登录后的身份处理、Token 生成和登录事件发布 |
| `service`、`service/impl` | `UserDeletionService` 及实现协调删除学员的部门关联、学习记录、时长、登录记录、积分流水及兑换记录，并协调排行榜清理 |
| `event` | 传递发生的操作和相关 ID，本身不执行数据清理或更新 |
| `listener` | 登录后更新信息；课程删除后清理关联；章节删除后移除章节课时；课时增删后重算数量；学习记录删除后清理明细或重算进度 |
| `schedule` | `LDAPSchedule` 定时触发 LDAP 同步 |
| `src/main/resources` | `application.yml` 管理运行参数；`static/images` 存放默认头像和示例课程图片 |

### playedu-common

`domain`、`service`、`service/impl` 和 `mapper` 按相同业务对象配套组织：实体表达数据，Service 定义操作，实现类处理规则和关联维护，Mapper 执行数据库访问。

| 基础业务组 | 具体职责 |
|---|---|
| 学员 | 账号维护、部门关联、登录记录和图片上传日志 |
| 组织与分类 | 部门树、分类树、父子关系、排序和关联查询 |
| 管理员与权限 | 管理员、角色、权限项以及管理员角色和角色权限关联 |
| 应用配置 | 配置读取与保存、S3 和 LDAP 配置获取等 |
| LDAP | 外部用户与部门的本地映射、同步任务和逐项变化明细 |
| 认证 | 公共认证接口与实现，以及管理端和学员端各自的 Token/JWT 处理 |

| 子目录 | 具体职责与主要文件 |
|---|---|
| `bus` | `BackendBus` 聚合权限、判断超级管理员及按权限脱敏；`LDAPBus` 协调同步、统计、明细记录及 S3 数据保存 |
| `config` | `AuthConfig` 读取认证配置；`SaTokenConfig` 配置 JWT 模式；`MybatisPlusConfig` 注册插件；`PlayEduConfig` 绑定项目参数；`UniqueNameGeneratorConfig` 避免同名 Bean 冲突 |
| `context` | `BCtx` 保存管理员与权限；`FCtx` 保存学员与 JWT 标识，通过 ThreadLocal 绑定当前请求 |
| `annotation` | `BackendPermission` 声明接口权限；`Log` 声明日志标题和操作类型；执行逻辑位于 system/aspectj |
| `constant` | 权限 slug、配置键、登录白名单、日志操作类型、版本及业务状态等 |
| `exception` | `ServiceException` 表达业务失败；`NotFoundException` 表达对象不存在 |
| `redis` | 配置 Redisson 与启动检查；统一键名；封装分布式锁、共享限流、登录失败计数和锁定，以及对应异常 |
| `types` 根目录 | 统一响应、验证码结果、上传信息、选择项和 LDAP 配置等数据容器 |
| `types/config` | S3 连接与访问配置对象 |
| `types/paginate` | 管理员、学员、课程、资源、学习记录和日志的查询条件及分页结果 |
| `types/mapper` | SQL 聚合查询结果，如部门学员数量和分类数量统计；这些类不是数据库访问接口 |
| `util` | 请求信息、IP 查询、隐私脱敏、字符串、Base64、通用辅助方法及 S3 文件操作 |
| `util/ldap` | LDAP 查询与登录，以及读取后的用户、部门转换对象 |
| `src/main/resources` | Mapper XML 和供 IpUtil 使用的 ip2region 地域数据库 |

### playedu-system

| 子目录 | 具体职责与主要文件 |
|---|---|
| `checks` | `MigrationCheck` 执行未完成迁移；`SystemDataCheck` 检查基础数据；`AppConfigCheck` 补齐配置；`AdminPermissionCheck` 补齐权限；`UpgradeCheck` 处理升级检查 |
| `migration` | `UserLearnDurationStatsMigration` 处理学习时长统计专项迁移与历史数据 |
| `domain` | `Migration` 表达已执行的迁移记录 |
| `service`、`service/impl` | 查询已执行迁移、保存新的迁移记录 |
| `mapper` | 迁移记录表的数据访问 |
| `aspectj` | `BackendPermissionAspect` 检查权限；`AdminLogAspect` 收集并保存日志 |
| `src/main/resources/mapper` | 迁移记录 Mapper 的 XML 配置 |

后台权限功能通常涉及 common 的权限常量、system 的权限初始化、Controller 的权限注解及 system 的权限切面，应沿这几个位置一起阅读。

### playedu-course

| 子目录及职责组 | 具体职责与主要文件 |
|---|---|
| `domain`：内容 | 课程、章节、课时、附件及附件下载日志 |
| `domain`：关联 | 课程分类、部门及学员可见范围关联 |
| `domain`：学习 | 课程与课时进度、时长明细、每日时长汇总、最近学习信息和排行榜条目 |
| `service`：业务接口 | 课程、章节、课时、附件、关联和学习记录操作接口 |
| `service`：有效学习 | `ActiveLearningLeaseService` 管理有效会话及心跳；`LearningFactPersistenceService` 事务性保存时长、进度及首次完成奖励 |
| `service`：辅助结果 | `CourseCompletionTransition` 判断首次完成；`LearningFactPersistenceResult` 表达持久化结果 |
| `service/impl`：内容维护 | 实现内容增删改查、排序、课时数量及可见范围关联维护 |
| `service/impl`：学习记录 | 学习进度、时长明细和每日汇总的保存、查询与清理 |
| `service/impl`：排行榜 | `DailyLearningRankingService` 查询、更新、重建 Redis 排行榜并移除学员 |
| `mapper` | 课程和学习数据 CRUD、分页及聚合统计 |
| `bus` | `UserBus` 判断学员是否能查看课程 |
| `event` | 提交后发布时长确认事件，异步更新排行榜，并在启动时恢复排行榜 |
| `src/main/resources/mapper` | 课程及学习数据 XML；`BackendPermission.xml` 实际映射学习时长明细 |

```text
HourController
    → ActiveLearningLeaseService：确认有效会话与时长
    → LearningFactPersistenceService：保存学习事实和奖励
    → 事务提交后发布时长事件
    → DailyLearningRankingEventListener：更新排行榜
```

### playedu-resource

| 子目录 | 具体职责与主要文件 |
|---|---|
| `domain` | `Resource` 表达文件基本信息；`ResourceExtra` 表达封面和时长等；`ResourceCategory` 表达资源与分类关联 |
| `service` | 定义资源查询维护、扩展信息、分类关联和上传操作 |
| `service/impl` | 实现分页、资源信息保存更新、访问地址与下载信息、扩展信息维护及上传处理 |
| `mapper` | 访问资源、扩展信息和分类关联数据 |
| `src/main/resources/mapper` | 结果映射及自定义 SQL；`ResourceVideoMapper.xml` 实际绑定 ResourceExtraMapper |

文件内容保存于 MinIO/S3，由 common/S3Util 操作；文件描述信息保存于 MySQL，由 resource 的 Service/Mapper 管理。

### playedu-points

| 子目录及职责组 | 具体职责与主要文件 |
|---|---|
| `domain`：积分 | `PointLedger` 记录变动金额、余额、来源和操作人；`PointLedgerType` 定义变动类型 |
| `domain`：商品 | `PointProduct` 表达商品；`PointProductStatus` 定义上下架状态 |
| `domain`：兑换码 | `PointCode` 保存加密码、指纹和库存状态；`PointCodeStatus` 定义状态 |
| `domain`：兑换 | `PointRedemption` 关联学员、商品、兑换码、扣分和请求标识 |
| `service`：接口 | 余额变动、流水查询、商品维护、兑换码库存和商品兑换 |
| `service`：来源键 | `PointSourceKeys` 构造课程奖励与人工调整来源键 |
| `types`：参数与结果 | `PointBalanceChange` 表达变动请求；`PointBalanceChangeResult` 表达执行结果 |
| `exception` | `PointBalanceException` 继承公共 ServiceException，表达积分变动失败 |
| `service/impl`：余额 | 更新 users.credit1 并写流水，处理校验、事务和幂等 |
| `service/impl`：商品与库存 | 商品维护、上下架约束、兑换码导入去重、库存查询及删除 |
| `service/impl`：兑换 | 编排商品检查、库存码分配、积分扣减和兑换记录保存，处理并发及重复请求 |
| `mapper` | 余额、流水、商品、兑换码和兑换记录访问；固定查询、锁和幂等写入 SQL 位于 Java 注解 |
| `src/main/resources/mapper` | PointProduct、PointCode、PointLedger、PointRedemption 的 paginate/paginateCount 共 8 条动态查询及 4 个共享 resultMap；固定注解查询复用同名映射 |
| `crypto` | 规范化兑换码，通过 AES-GCM 加解密和 HMAC-SHA256 生成指纹 |
| `types`：导入 | 批量导入汇总、单行结果及单行状态 |

课程奖励、人工调整和兑换扣分都经过积分余额服务；当前余额位于 users.credit1，没有单独的 PointBalance 实体。

## 三、caches 空目录的历史与扩展方向

### 为什么为空

`playedu-course/caches` 曾包含 `UserCanSeeCourseCache.java`（学员是否可查看课程的内存缓存）和 `UserLastLearnTimeCache.java`（学员最近学习时间戳的内存状态）。提交 `a9b5c2369da554f1ee7f8666557dc30ae9cd80a0`，标题为 `refactor(redis): remove legacy memory state`，于 2026-09-09 删除了这两个文件，同时删除 MemoryCacheUtil 和旧内存锁等代码。因此这是旧实现删除后留下的目录，并非尚未实现的缓存功能声明。Git 不跟踪空目录，磁盘目录可以在删除文件后继续存在。

项目的 [Redis 运行状态决策](C:/Users/86198/Desktop/play/docs/adr/0001-require-redis-for-shared-runtime-state.md) 明确要求 Redis 承载共享运行状态，避免进程内锁或计数器破坏多实例正确性。当前学习会话由 ActiveLearningLeaseService 管理，排行榜由 DailyLearningRankingService 维护；这些能力在 service 中，不要求放入 caches。课程访问判断目前通过 UserBus.canSeeCourse 查询课程部门范围、学员部门及父级部门后计算。

### 可以扩展什么

以下为设计建议，尚未实现；应先用接口耗时和查询次数确认收益。

| 扩展 | 缓存内容 | 失效条件与设计重点 |
|---|---|---|
| 课程内容快照 | 课程公共信息、章节、课时及附件结构 | 课程内容更新、课时/章节/附件增删排序后，在事务提交后失效；排除个人进度和播放签名地址 |
| 首页公开课程列表 | 公开课程 ID、分类筛选及展示信息 | 上下架、展示状态、分类和排序变化后失效；部门专属列表需区分可见范围 |
| 学员课程访问判断 | 学员与课程组合的访问结果，或作为判断输入的部门关联 | 课程范围、学员部门、部门父子关系变化均需失效；权限撤销不能只等待 TTL |
| 课程统计摘要 | 学员数、完成人数、完成率等聚合结果 | 明确允许的数据延迟，学习进度变化或定时重算后更新；适合统计展示 |

第一步建议选择课程内容快照，因为章节和课时结构可在多个学员之间复用，变化入口也较明确。可以增加 CourseContentCache 与不包含个人信息的快照对象，通过 RedisKeyspace 统一键名。读取未命中时查询数据库并缓存；修改在提交后使快照失效，同时设置合理 TTL。若要严格处理“旧读取在失效后回填旧值”的并发情况，应增加版本校验或刷新协调机制。

普通查询缓存可在 Redis 失败时回源数据库，但这条策略不适用于项目要求共享的学习租约、锁和限流状态。积分余额和兑换码分配继续以事务及数据库校验为准；带有效期的预签名播放地址不能混入长期公共快照。

缓存目录只是一种代码组织选择，不会自动启用缓存。没有实际缓存需求时可以保留空目录或删除；需要扩展时再放入明确职责的缓存组件，不必把现有业务状态服务机械迁入该目录。

## 四、积分模块与既有模块的结构对比

结论：积分模块沿用现有 Maven 模块与分层方式，能够纳入统一 API、依赖、响应、异常及迁移机制；具体编码组织并非完全一致。以下是结构检查结果，不代表已经验证完整积分业务的正确性。

| 对比项 | 积分模块与现有模块的关系 |
|---|---|
| Maven 与包名 | 一致：继承 playedu 父工程，包名为 xyz.playedu.points，仅依赖 common；父工程、api 和 course 已声明 points |
| 目录分层 | 一致：domain、service、service/impl、mapper、types、exception；crypto 为积分兑换码能力扩展 |
| HTTP 层 | 一致：PointAdminController、PointsController 和 Point*Request 位于 api 模块，领域模块不放 Controller |
| 实体 | 一致：持久化实体使用 @Data、@TableName、@TableId 和 Serializable；额外状态枚举表达积分规则 |
| Mapper | 大体一致：四个实体 Mapper 继承 BaseMapper；PointBalanceMapper 是专门更新 users.credit1 的普通 Mapper |
| SQL 放置 | 动态查询阅读位置一致：积分的 8 条分页/计数 SQL 位于 resources/mapper，与课程、资源相同；固定查询、锁和幂等写入保留 Mapper 注解 |
| Service 继承 | 混合：Ledger 和 Redemption 继承 IService/ServiceImpl；Balance、Product、Code 使用业务接口与普通实现类，不暴露通用 CRUD |
| 注入方式 | 不同：积分主要使用构造器注入及 final 依赖；既有 CRUD 服务多使用字段 @Autowired；课程新服务也有构造器注入 |
| 参数与结果 | 积分变动输入、结果使用 record 且位于 types，批量导入结果也位于 types；分页复用 common/PaginationResult，积分仍使用多个参数而非独立 PaginateFilter |
| 异常 | 接入与位置一致：PointBalanceException 继承公共 ServiceException，位于 points/exception |
| 权限与日志 | 复用现有 BCtx/BackendBus 和 @Log；积分管理主要显式检查超级管理员，旧后台多使用 @BackendPermission，表达方式不同 |
| 迁移 | 接入一致：积分表定义位于 system/MigrationCheck；points/migration 当前无 Java 源文件 |
| 测试 | 使用现有 src/test/java 目录及父工程测试依赖，积分具备单元和集成测试 |

2026-09-16 已完成类型归位和动态 SQL 迁移：PointBalanceException 位于 points/exception，PointBalanceChange 和 PointBalanceChangeResult 位于 points/types；四个实体 Mapper 配有动态查询和结果映射 XML。整理仅调整代码阅读位置，余额、事务、幂等、权限及兑换规则保持一致。测试及打包结果见 [结构整理验收记录](C:/Users/86198/Desktop/play/docs/points-structure-cleanup-validation.md)。

后续可选项仍未执行：分页筛选增长时增加 points/types/paginate，管理端接口增长时拆分 PointAdminController，以及明确需要时统一超级管理员权限表达。保留构造器注入及较窄的积分业务接口，不强制所有服务继承 IService；固定注解 SQL 合法且继续保留。权限表达整理须保留超级管理员限制。

## 五、逐文件清单

### 先理解文件如何配合

Controller 接收 HTTP 请求；Request 承接参数与校验；Service 定义或执行业务；ServiceImpl 是接口实现；Mapper 操作数据库；domain 通常映射表；types 承载参数或结果；XML 显式定义映射或 SQL。Event 传递通知，Listener 执行收到通知后的业务。Bus 是业务编排对象，不代表消息中间件。

典型路径：Controller → Service/Bus → Mapper → MySQL。积分 Mapper 有注解 SQL，不能只找 XML；types/mapper 内的类是统计结果对象；部分 service 下是直接标注 @Service 的具体类，没有 Impl 文件。

学习路径：HourController → ActiveLearningLeaseService → LearningFactPersistenceService → 学习记录/时长/积分服务；时长确认提交后发布事件，由 DailyLearningRankingEventListener 更新排行榜。删除路径也要追踪事件监听器，不能只看 Controller 的 delete 方法。

### 文件计数

| 范围 | 文件数 |
|---|---:|
| playedu-api | 116 |
| playedu-common | 161 |
| playedu-system | 18 |
| playedu-course | 75 |
| playedu-resource | 18 |
| playedu-points | 42 |

### 父工程与构建支撑

| 文件 | 作用 | 阅读线索 |
|---|---|---|
| [.gitignore](C:/Users/86198/Desktop/play/playedu-api/.gitignore) | 该目录 Git 忽略规则。 | — |
| [.mvn/maven.config](C:/Users/86198/Desktop/play/playedu-api/.mvn/maven.config) | Maven Wrapper 调用时附加的项目参数。 | — |
| [.mvn/settings.xml](C:/Users/86198/Desktop/play/playedu-api/.mvn/settings.xml) | 项目 Maven 设置文件，例如仓库和镜像配置。 | — |
| [.mvn/wrapper/maven-wrapper.jar](C:/Users/86198/Desktop/play/playedu-api/.mvn/wrapper/maven-wrapper.jar) | Maven Wrapper 引导程序，用于下载/启动配置的 Maven。 | — |
| [.mvn/wrapper/maven-wrapper.properties](C:/Users/86198/Desktop/play/playedu-api/.mvn/wrapper/maven-wrapper.properties) | Maven Wrapper 的 Maven 分发版本与下载地址配置。 | — |
| [Dockerfile](C:/Users/86198/Desktop/play/playedu-api/Dockerfile) | 后端容器镜像构建定义，具体构建与运行阶段见文件。 | — |
| [Dockerfile.local](C:/Users/86198/Desktop/play/playedu-api/Dockerfile.local) | 本地后端容器构建定义，具体使用的构建产物见文件。 | — |
| [docs/ldap-sync-record.md](C:/Users/86198/Desktop/play/playedu-api/docs/ldap-sync-record.md) | LDAP 同步记录功能说明，包括数据结构及接口等。 | — |
| [header.txt](C:/Users/86198/Desktop/play/playedu-api/header.txt) | Spotless 添加或校验的 Java 版权许可证头模板。 | — |
| [mvnw](C:/Users/86198/Desktop/play/playedu-api/mvnw) | Unix 系统 Maven Wrapper 启动脚本。 | — |
| [mvnw.cmd](C:/Users/86198/Desktop/play/playedu-api/mvnw.cmd) | Windows Maven Wrapper 启动脚本。 | — |
| [pom.xml](C:/Users/86198/Desktop/play/playedu-api/pom.xml) | 后端 Maven 聚合父工程：声明六个子模块、Java 17、Spring Boot 3.3.4、公共依赖、测试依赖与 Spotless/编译插件。 | — |

### playedu-api

应用入口与 HTTP 层。backend 面向管理员，frontend 面向学员；request 存放参数对象；interceptor 做请求身份处理；listener 协调删除和登录后的关联更新；service 处理跨模块业务。

#### 入口与构建

| 文件 | 作用 | 方法、字段或 SQL 线索 |
|---|---|---|
| [pom.xml](C:/Users/86198/Desktop/play/playedu-api/playedu-api/pom.xml) | 该模块的 Maven 构建定义：继承父工程、声明模块依赖与构建插件。 | — |
| [PlayeduApiApplication.java](C:/Users/86198/Desktop/play/playedu-api/playedu-api/src/main/java/xyz/playedu/api/PlayeduApiApplication.java) | 整个后端的 Spring Boot 启动入口；扫描所有模块的组件和 Mapper，启用事务、异步任务及定时任务。 | 方法：main |

#### 资源与配置

| 文件 | 作用 | 方法、字段或 SQL 线索 |
|---|---|---|
| [src/main/resources/application.yml](C:/Users/86198/Desktop/play/playedu-api/playedu-api/src/main/resources/application.yml) | 运行配置：端口、MySQL、Redis、线程任务、MyBatis、Sa-Token 和 playedu 业务配置，支持环境变量覆盖。 | — |
| [src/main/resources/static/images/courses/thumb1.png](C:/Users/86198/Desktop/play/playedu-api/playedu-api/src/main/resources/static/images/courses/thumb1.png) | 示例课程封面图片 1。 | — |
| [src/main/resources/static/images/courses/thumb2.png](C:/Users/86198/Desktop/play/playedu-api/playedu-api/src/main/resources/static/images/courses/thumb2.png) | 示例课程封面图片 2。 | — |
| [src/main/resources/static/images/courses/thumb3.png](C:/Users/86198/Desktop/play/playedu-api/playedu-api/src/main/resources/static/images/courses/thumb3.png) | 示例课程封面图片 3。 | — |
| [src/main/resources/static/images/default_avatar.png](C:/Users/86198/Desktop/play/playedu-api/playedu-api/src/main/resources/static/images/default_avatar.png) | 系统默认学员头像静态图片。 | — |

#### bus

| 文件 | 作用 | 方法、字段或 SQL 线索 |
|---|---|---|
| [bus/LoginBus.java](C:/Users/86198/Desktop/play/playedu-api/playedu-api/src/main/java/xyz/playedu/api/bus/LoginBus.java) | 学员登录业务编排：本地账号或 LDAP 用户完成身份处理后生成 Token，并发布登录事件。 | 方法：tokenByUser、tokenByLdapTransformUser |

#### controller

| 文件 | 作用 | 方法、字段或 SQL 线索 |
|---|---|---|
| [controller/ExceptionController.java](C:/Users/86198/Desktop/play/playedu-api/playedu-api/src/main/java/xyz/playedu/api/controller/ExceptionController.java) | 全局异常处理：将业务异常、参数校验错误、资源不存在及 Redis 能力不可用等转换为统一响应。 | 方法：exceptionHandler、serviceExceptionHandler、apiRateLimitUnavailableHandler、loginFailureTrackingUnavailableHandler、learningLeaseUnavailableHandler |

#### controller/acceptance

| 文件 | 作用 | 方法、字段或 SQL 线索 |
|---|---|---|
| [controller/acceptance/AcceptanceProbeController.java](C:/Users/86198/Desktop/play/playedu-api/playedu-api/src/main/java/xyz/playedu/api/controller/acceptance/AcceptanceProbeController.java) | 双实例验收用 HTTP 探针，验证实例标识、分布式锁、学习租约和排行榜；仅 playedu.acceptance.enabled=true 时启用，默认关闭。 | 方法：instance、lock、leaseHeartbeat、leaseStop、ranking、rebuildRanking、heartbeatData、sleep、LockRequest、LeaseHeartbeatRequest、isValid、LeaseStopRequest |

#### controller/backend

| 文件 | 作用 | 方法、字段或 SQL 线索 |
|---|---|---|
| [controller/backend/AdminLogController.java](C:/Users/86198/Desktop/play/playedu-api/playedu-api/src/main/java/xyz/playedu/api/controller/backend/AdminLogController.java) | 后台操作日志列表与详情。 | 方法：index、detail |
| [controller/backend/AdminRoleController.java](C:/Users/86198/Desktop/play/playedu-api/playedu-api/src/main/java/xyz/playedu/api/controller/backend/AdminRoleController.java) | 管理员角色增删改查及角色权限配置。 | 方法：index、create、store、edit、update、destroy |
| [controller/backend/AdminUserController.java](C:/Users/86198/Desktop/play/playedu-api/playedu-api/src/main/java/xyz/playedu/api/controller/backend/AdminUserController.java) | 管理员账号增删改查及角色分配。 | 方法：Index、create、store、edit、update、destroy |
| [controller/backend/AppConfigController.java](C:/Users/86198/Desktop/play/playedu-api/playedu-api/src/main/java/xyz/playedu/api/controller/backend/AppConfigController.java) | 读取和保存应用配置，对私密配置返回掩码。 | 方法：index、save |
| [controller/backend/CourseAttachmentController.java](C:/Users/86198/Desktop/play/playedu-api/playedu-api/src/main/java/xyz/playedu/api/controller/backend/CourseAttachmentController.java) | 课程附件增删改查、批量创建及排序。 | 方法：store、storeMulti、edit、update、destroy、updateSort |
| [controller/backend/CourseAttachmentDownloadLogController.java](C:/Users/86198/Desktop/play/playedu-api/playedu-api/src/main/java/xyz/playedu/api/controller/backend/CourseAttachmentDownloadLogController.java) | 查询课程附件下载日志。 | 方法：index |
| [controller/backend/CourseChapterController.java](C:/Users/86198/Desktop/play/playedu-api/playedu-api/src/main/java/xyz/playedu/api/controller/backend/CourseChapterController.java) | 课程章节创建、更新、删除及排序。 | 方法：store、edit、update、destroy、updateSort |
| [controller/backend/CourseController.java](C:/Users/86198/Desktop/play/playedu-api/playedu-api/src/main/java/xyz/playedu/api/controller/backend/CourseController.java) | 课程分页查询、创建、更新、删除，并组合分类、部门、课时等信息。 | 方法：index、create、store、edit、update、destroy |
| [controller/backend/CourseHourController.java](C:/Users/86198/Desktop/play/playedu-api/playedu-api/src/main/java/xyz/playedu/api/controller/backend/CourseHourController.java) | 课时创建、批量创建、更新、删除及排序，并发布课时变化事件。 | 方法：create、store、storeMulti、edit、update、destroy、updateSort |
| [controller/backend/CourseUserController.java](C:/Users/86198/Desktop/play/playedu-api/playedu-api/src/main/java/xyz/playedu/api/controller/backend/CourseUserController.java) | 查询课程学员学习情况，删除相关学习记录。 | 方法：index、destroy |
| [controller/backend/DashboardController.java](C:/Users/86198/Desktop/play/playedu-api/playedu-api/src/main/java/xyz/playedu/api/controller/backend/DashboardController.java) | 后台仪表盘统计与每日学习排行榜，提供排行榜重建入口。 | 方法：index、rebuildLearningRanking、top10Users |
| [controller/backend/DepartmentController.java](C:/Users/86198/Desktop/play/playedu-api/playedu-api/src/main/java/xyz/playedu/api/controller/backend/DepartmentController.java) | 部门树维护、排序、调整父节点、部门学员查询及手动 LDAP 同步。 | 方法：index、create、store、edit、update、preDestroy、destroy、resort、updateParent、users、ldapSync |
| [controller/backend/LdapController.java](C:/Users/86198/Desktop/play/playedu-api/playedu-api/src/main/java/xyz/playedu/api/controller/backend/LdapController.java) | LDAP 同步任务列表、任务详情及同步数据下载。 | 方法：syncRecords、syncRecordDetail、syncRecordDownload |
| [controller/backend/LdapSyncDetailController.java](C:/Users/86198/Desktop/play/playedu-api/playedu-api/src/main/java/xyz/playedu/api/controller/backend/LdapSyncDetailController.java) | LDAP 同步的部门/学员逐项明细查询。 | 方法：getDetails |
| [controller/backend/LoginController.java](C:/Users/86198/Desktop/play/playedu-api/playedu-api/src/main/java/xyz/playedu/api/controller/backend/LoginController.java) | 管理员登录、退出、当前账号信息及修改密码。 | 方法：login、logout、detail、changePassword |
| [controller/backend/PointAdminController.java](C:/Users/86198/Desktop/play/playedu-api/playedu-api/src/main/java/xyz/playedu/api/controller/backend/PointAdminController.java) | 积分商品维护、兑换码库存与导入、人工调整积分、流水和兑换记录查询。 | 方法：products、product、createProduct、updateProduct、updateProductStatus、offSaleProduct、onSaleProduct、deleteProduct、importCodes、codes、revealCode、deleteCode等 |
| [controller/backend/ResourceCategoryController.java](C:/Users/86198/Desktop/play/playedu-api/playedu-api/src/main/java/xyz/playedu/api/controller/backend/ResourceCategoryController.java) | 资源分类树维护、排序、调整父节点及删除前检查。 | 方法：index、create、store、edit、update、preDestroy、destroy、resort、updateParent |
| [controller/backend/ResourceController.java](C:/Users/86198/Desktop/play/playedu-api/playedu-api/src/main/java/xyz/playedu/api/controller/backend/ResourceController.java) | 资源列表与信息更新、单个或批量删除。 | 方法：index、destroy、multiDestroy、edit、update |
| [controller/backend/SystemController.java](C:/Users/86198/Desktop/play/playedu-api/playedu-api/src/main/java/xyz/playedu/api/controller/backend/SystemController.java) | 读取管理端所需系统配置。 | 方法：config |
| [controller/backend/UploadController.java](C:/Users/86198/Desktop/play/playedu-api/playedu-api/src/main/java/xyz/playedu/api/controller/backend/UploadController.java) | 对接 MinIO/S3 的文件、分片上传：初始化上传、预签名 URL、分片列表、清理和合并，保存资源信息。 | 方法：uploadMinio、minioUploadId、minioPreSignUrl、minioListParts、purgeIncompleteSegments、minioMergeFile、doSaveResourceExtra |
| [controller/backend/UserController.java](C:/Users/86198/Desktop/play/playedu-api/playedu-api/src/main/java/xyz/playedu/api/controller/backend/UserController.java) | 学员维护、批量导入、学习记录和课程进度查询，以及学员/学习记录删除。 | 方法：index、create、store、edit、update、destroy、batchStore、learnHours、latestLearnCourses、allCourses、learnCourseDetail、learn等 |

#### controller/frontend

| 文件 | 作用 | 方法、字段或 SQL 线索 |
|---|---|---|
| [controller/frontend/CategoryController.java](C:/Users/86198/Desktop/play/playedu-api/playedu-api/src/main/java/xyz/playedu/api/controller/frontend/CategoryController.java) | 返回学员端可用分类。 | 方法：all |
| [controller/frontend/CourseController.java](C:/Users/86198/Desktop/play/playedu-api/playedu-api/src/main/java/xyz/playedu/api/controller/frontend/CourseController.java) | 课程详情和附件下载，并记录下载日志。 | 方法：detail、attachmentDownload |
| [controller/frontend/DepartmentController.java](C:/Users/86198/Desktop/play/playedu-api/playedu-api/src/main/java/xyz/playedu/api/controller/frontend/DepartmentController.java) | 返回学员端所需部门列表。 | 方法：index |
| [controller/frontend/HourController.java](C:/Users/86198/Desktop/play/playedu-api/playedu-api/src/main/java/xyz/playedu/api/controller/frontend/HourController.java) | 课时详情、播放资源及学习心跳/停止；旧客户端时长上报入口包含拒绝处理。 | 方法：detail、play、record、ping、stopPing、rejectClientDurationReport、checkCourseAccess、activeLearningData |
| [controller/frontend/IndexController.java](C:/Users/86198/Desktop/play/playedu-api/playedu-api/src/main/java/xyz/playedu/api/controller/frontend/IndexController.java) | 首页课程信息。 | 方法：index |
| [controller/frontend/LoginController.java](C:/Users/86198/Desktop/play/playedu-api/playedu-api/src/main/java/xyz/playedu/api/controller/frontend/LoginController.java) | 学员账号密码登录、LDAP 登录及退出。 | 方法：password、ldap、logout、ldapLogin |
| [controller/frontend/PointsController.java](C:/Users/86198/Desktop/play/playedu-api/playedu-api/src/main/java/xyz/playedu/api/controller/frontend/PointsController.java) | 积分概览、流水、商品、兑换、兑换记录及积分规则查询。 | 方法：summary、ledgers、products、product、redeem、redemptions、redemption、rules、deliveredRedemptionData、currentLearnerId、ensureUnlocked、pageData等 |
| [controller/frontend/SystemController.java](C:/Users/86198/Desktop/play/playedu-api/playedu-api/src/main/java/xyz/playedu/api/controller/frontend/SystemController.java) | 读取学员端系统配置。 | 方法：config |
| [controller/frontend/UserController.java](C:/Users/86198/Desktop/play/playedu-api/playedu-api/src/main/java/xyz/playedu/api/controller/frontend/UserController.java) | 个人信息、头像和密码修改、课程列表及最近学习记录。 | 方法：detail、changeAvatar、changePassword、courses、latestLearn |

#### event

| 文件 | 作用 | 方法、字段或 SQL 线索 |
|---|---|---|
| [event/AdminUserLoginEvent.java](C:/Users/86198/Desktop/play/playedu-api/playedu-api/src/main/java/xyz/playedu/api/event/AdminUserLoginEvent.java) | 事件载体：传递管理员账号登录通知及关联 ID；事件本身不执行业务处理。 | 数据/常量定义；详见源码 |
| [event/CourseCategoryDestroyEvent.java](C:/Users/86198/Desktop/play/playedu-api/playedu-api/src/main/java/xyz/playedu/api/event/CourseCategoryDestroyEvent.java) | 事件载体：传递课程与分类关联删除通知及关联 ID；事件本身不执行业务处理。 | 数据/常量定义；详见源码 |
| [event/CourseChapterDestroyEvent.java](C:/Users/86198/Desktop/play/playedu-api/playedu-api/src/main/java/xyz/playedu/api/event/CourseChapterDestroyEvent.java) | 事件载体：传递课程章节删除通知及关联 ID；事件本身不执行业务处理。 | 数据/常量定义；详见源码 |
| [event/CourseDestroyEvent.java](C:/Users/86198/Desktop/play/playedu-api/playedu-api/src/main/java/xyz/playedu/api/event/CourseDestroyEvent.java) | 事件载体：传递课程删除通知及关联 ID；事件本身不执行业务处理。 | 数据/常量定义；详见源码 |
| [event/CourseHourCreatedEvent.java](C:/Users/86198/Desktop/play/playedu-api/playedu-api/src/main/java/xyz/playedu/api/event/CourseHourCreatedEvent.java) | 事件载体：传递课程课时创建通知及关联 ID；事件本身不执行业务处理。 | 数据/常量定义；详见源码 |
| [event/CourseHourDestroyEvent.java](C:/Users/86198/Desktop/play/playedu-api/playedu-api/src/main/java/xyz/playedu/api/event/CourseHourDestroyEvent.java) | 事件载体：传递课程课时删除通知及关联 ID；事件本身不执行业务处理。 | 数据/常量定义；详见源码 |
| [event/DepartmentDestroyEvent.java](C:/Users/86198/Desktop/play/playedu-api/playedu-api/src/main/java/xyz/playedu/api/event/DepartmentDestroyEvent.java) | 事件载体：传递部门组织树删除通知及关联 ID；事件本身不执行业务处理。 | 数据/常量定义；详见源码 |
| [event/ResourceCategoryDestroyEvent.java](C:/Users/86198/Desktop/play/playedu-api/playedu-api/src/main/java/xyz/playedu/api/event/ResourceCategoryDestroyEvent.java) | 事件载体：传递资源与分类关联删除通知及关联 ID；事件本身不执行业务处理。 | 数据/常量定义；详见源码 |
| [event/UserCourseHourRecordDestroyEvent.java](C:/Users/86198/Desktop/play/playedu-api/playedu-api/src/main/java/xyz/playedu/api/event/UserCourseHourRecordDestroyEvent.java) | 事件载体：传递学员课时学习进度删除通知及关联 ID；事件本身不执行业务处理。 | 数据/常量定义；详见源码 |
| [event/UserCourseRecordDestroyEvent.java](C:/Users/86198/Desktop/play/playedu-api/playedu-api/src/main/java/xyz/playedu/api/event/UserCourseRecordDestroyEvent.java) | 事件载体：传递学员课程学习进度删除通知及关联 ID；事件本身不执行业务处理。 | 数据/常量定义；详见源码 |
| [event/UserLoginEvent.java](C:/Users/86198/Desktop/play/playedu-api/playedu-api/src/main/java/xyz/playedu/api/event/UserLoginEvent.java) | 事件载体：传递学员账号登录通知及关联 ID；事件本身不执行业务处理。 | 数据/常量定义；详见源码 |
| [event/UserLogoutEvent.java](C:/Users/86198/Desktop/play/playedu-api/playedu-api/src/main/java/xyz/playedu/api/event/UserLogoutEvent.java) | 事件载体：传递学员账号退出通知及关联 ID；事件本身不执行业务处理。 | 数据/常量定义；详见源码 |

#### interceptor

| 文件 | 作用 | 方法、字段或 SQL 线索 |
|---|---|---|
| [interceptor/AdminInterceptor.java](C:/Users/86198/Desktop/play/playedu-api/playedu-api/src/main/java/xyz/playedu/api/interceptor/AdminInterceptor.java) | 校验管理端身份和登录白名单，填充 BCtx 中的管理员与权限信息，请求结束后清理上下文。 | 方法：preHandle、responseTransform、afterCompletion |
| [interceptor/ApiInterceptor.java](C:/Users/86198/Desktop/play/playedu-api/playedu-api/src/main/java/xyz/playedu/api/interceptor/ApiInterceptor.java) | 公共请求拦截器，接入 Redis 请求限流；健康检查等路径在注册处排除。 | 方法：preHandle |
| [interceptor/FrontInterceptor.java](C:/Users/86198/Desktop/play/playedu-api/playedu-api/src/main/java/xyz/playedu/api/interceptor/FrontInterceptor.java) | 校验学员端身份和登录白名单，填充 FCtx，请求结束后清理上下文。 | 方法：preHandle、responseTransform、afterCompletion |
| [interceptor/WebMvcConfig.java](C:/Users/86198/Desktop/play/playedu-api/playedu-api/src/main/java/xyz/playedu/api/interceptor/WebMvcConfig.java) | 注册公共、管理端、学员端拦截器，并配置跨域规则。 | 方法：addInterceptors、addCorsMappings |

#### listener

| 文件 | 作用 | 方法、字段或 SQL 线索 |
|---|---|---|
| [listener/AdminUserLoginListener.java](C:/Users/86198/Desktop/play/playedu-api/playedu-api/src/main/java/xyz/playedu/api/listener/AdminUserLoginListener.java) | 接收管理员登录事件，更新管理员登录信息。 | 方法：updateLoginInfo |
| [listener/CourseCategoryDestroyListener.java](C:/Users/86198/Desktop/play/playedu-api/playedu-api/src/main/java/xyz/playedu/api/listener/CourseCategoryDestroyListener.java) | 接收课程分类删除事件，清理课程与该分类的关联。 | 方法：resetRelateCourseCategoryId |
| [listener/CourseChapterDestroyListener.java](C:/Users/86198/Desktop/play/playedu-api/playedu-api/src/main/java/xyz/playedu/api/listener/CourseChapterDestroyListener.java) | 接收章节删除事件，调用课时服务移除对应章节的课时。 | 方法：resetCourseHourChapterId |
| [listener/CourseDestroyListener.java](C:/Users/86198/Desktop/play/playedu-api/playedu-api/src/main/java/xyz/playedu/api/listener/CourseDestroyListener.java) | 接收课程删除事件，清理部门、分类、附件及课程/课时学习记录等关联。 | 方法：departmentRelateRemove、categoryRelateRemove、attachmentRelateRemove、removeUserRecords |
| [listener/CourseHourCreatedListener.java](C:/Users/86198/Desktop/play/playedu-api/playedu-api/src/main/java/xyz/playedu/api/listener/CourseHourCreatedListener.java) | 接收课时创建事件，重新统计课程课时数量。 | 方法：courseClassHourUpdate |
| [listener/CourseHourDestroyListener.java](C:/Users/86198/Desktop/play/playedu-api/playedu-api/src/main/java/xyz/playedu/api/listener/CourseHourDestroyListener.java) | 接收课时删除事件，重新统计课程课时数量。 | 方法：courseClassHourUpdate |
| [listener/DepartmentDestroyListener.java](C:/Users/86198/Desktop/play/playedu-api/playedu-api/src/main/java/xyz/playedu/api/listener/DepartmentDestroyListener.java) | 接收部门删除事件，清理学员与部门等关联。 | 方法：updateLoginInfo |
| [listener/UserCourseHourRecordDestroyListener.java](C:/Users/86198/Desktop/play/playedu-api/playedu-api/src/main/java/xyz/playedu/api/listener/UserCourseHourRecordDestroyListener.java) | 接收课时学习记录删除事件，重新计算课程学习进度。 | 方法：updateUserCourseRecord |
| [listener/UserCourseRecordDestroyListener.java](C:/Users/86198/Desktop/play/playedu-api/playedu-api/src/main/java/xyz/playedu/api/listener/UserCourseRecordDestroyListener.java) | 接收课程学习记录删除事件，清理对应课时学习记录。 | 方法：emptyUserCourseHourRecords |
| [listener/UserLoginListener.java](C:/Users/86198/Desktop/play/playedu-api/playedu-api/src/main/java/xyz/playedu/api/listener/UserLoginListener.java) | 异步接收学员登录事件，更新登录信息并保存登录记录。 | 方法：updateLoginInfo |
| [listener/UserLogoutListener.java](C:/Users/86198/Desktop/play/playedu-api/playedu-api/src/main/java/xyz/playedu/api/listener/UserLogoutListener.java) | 异步接收学员退出事件，更新登录记录。 | 方法：updateLoginRecord |

#### request/backend

| 文件 | 作用 | 方法、字段或 SQL 线索 |
|---|---|---|
| [request/backend/AdminRoleRequest.java](C:/Users/86198/Desktop/play/playedu-api/playedu-api/src/main/java/xyz/playedu/api/request/backend/AdminRoleRequest.java) | 管理员角色的 HTTP 请求参数对象；承接 JSON 字段及参数校验约束，不直接操作数据库。 | 字段：name、permissionIds |
| [request/backend/AdminUserRequest.java](C:/Users/86198/Desktop/play/playedu-api/playedu-api/src/main/java/xyz/playedu/api/request/backend/AdminUserRequest.java) | 管理员账号的 HTTP 请求参数对象；承接 JSON 字段及参数校验约束，不直接操作数据库。 | 字段：name、email、password、isBanLogin、roleIds |
| [request/backend/AppConfigRequest.java](C:/Users/86198/Desktop/play/playedu-api/playedu-api/src/main/java/xyz/playedu/api/request/backend/AppConfigRequest.java) | 应用配置的 HTTP 请求参数对象；承接 JSON 字段及参数校验约束，不直接操作数据库。 | 字段：data |
| [request/backend/CourseAttachmentMultiRequest.java](C:/Users/86198/Desktop/play/playedu-api/playedu-api/src/main/java/xyz/playedu/api/request/backend/CourseAttachmentMultiRequest.java) | 批量创建附件的 HTTP 请求参数对象；承接 JSON 字段及参数校验约束，不直接操作数据库。 | 字段：title、sort、type、rid、attachments |
| [request/backend/CourseAttachmentRequest.java](C:/Users/86198/Desktop/play/playedu-api/playedu-api/src/main/java/xyz/playedu/api/request/backend/CourseAttachmentRequest.java) | 课程附件的 HTTP 请求参数对象；承接 JSON 字段及参数校验约束，不直接操作数据库。 | 字段：title、sort、type、rid |
| [request/backend/CourseAttachmentSortRequest.java](C:/Users/86198/Desktop/play/playedu-api/playedu-api/src/main/java/xyz/playedu/api/request/backend/CourseAttachmentSortRequest.java) | 附件排序的 HTTP 请求参数对象；承接 JSON 字段及参数校验约束，不直接操作数据库。 | 字段：ids |
| [request/backend/CourseCategoryRequest.java](C:/Users/86198/Desktop/play/playedu-api/playedu-api/src/main/java/xyz/playedu/api/request/backend/CourseCategoryRequest.java) | 课程与分类关联的 HTTP 请求参数对象；承接 JSON 字段及参数校验约束，不直接操作数据库。 | 字段：name、parentId、sort |
| [request/backend/CourseChapterRequest.java](C:/Users/86198/Desktop/play/playedu-api/playedu-api/src/main/java/xyz/playedu/api/request/backend/CourseChapterRequest.java) | 课程章节的 HTTP 请求参数对象；承接 JSON 字段及参数校验约束，不直接操作数据库。 | 字段：name、sort |
| [request/backend/CourseChapterSortRequest.java](C:/Users/86198/Desktop/play/playedu-api/playedu-api/src/main/java/xyz/playedu/api/request/backend/CourseChapterSortRequest.java) | 章节排序的 HTTP 请求参数对象；承接 JSON 字段及参数校验约束，不直接操作数据库。 | 字段：ids |
| [request/backend/CourseHourMultiRequest.java](C:/Users/86198/Desktop/play/playedu-api/playedu-api/src/main/java/xyz/playedu/api/request/backend/CourseHourMultiRequest.java) | 批量创建课时的 HTTP 请求参数对象；承接 JSON 字段及参数校验约束，不直接操作数据库。 | 字段：chapterId、title、duration、sort、type、rid、hours |
| [request/backend/CourseHourRequest.java](C:/Users/86198/Desktop/play/playedu-api/playedu-api/src/main/java/xyz/playedu/api/request/backend/CourseHourRequest.java) | 课程课时的 HTTP 请求参数对象；承接 JSON 字段及参数校验约束，不直接操作数据库。 | 字段：chapterId、title、duration、sort、type、rid |
| [request/backend/CourseHourSortRequest.java](C:/Users/86198/Desktop/play/playedu-api/playedu-api/src/main/java/xyz/playedu/api/request/backend/CourseHourSortRequest.java) | 课时排序的 HTTP 请求参数对象；承接 JSON 字段及参数校验约束，不直接操作数据库。 | 字段：ids |
| [request/backend/CourseRequest.java](C:/Users/86198/Desktop/play/playedu-api/playedu-api/src/main/java/xyz/playedu/api/request/backend/CourseRequest.java) | 课程的 HTTP 请求参数对象；承接 JSON 字段及参数校验约束，不直接操作数据库。 | 字段：title、thumb、shortDesc、isShow、isRequired、depIds、categoryIds、sortAt、name、type、duration、rid、hours、chapters、attachments |
| [request/backend/CourseUserDestroyRequest.java](C:/Users/86198/Desktop/play/playedu-api/playedu-api/src/main/java/xyz/playedu/api/request/backend/CourseUserDestroyRequest.java) | 删除课程学习记录的 HTTP 请求参数对象；承接 JSON 字段及参数校验约束，不直接操作数据库。 | 字段：ids |
| [request/backend/DepartmentParentRequest.java](C:/Users/86198/Desktop/play/playedu-api/playedu-api/src/main/java/xyz/playedu/api/request/backend/DepartmentParentRequest.java) | 调整部门父节点的 HTTP 请求参数对象；承接 JSON 字段及参数校验约束，不直接操作数据库。 | 字段：ids、id、parentId |
| [request/backend/DepartmentRequest.java](C:/Users/86198/Desktop/play/playedu-api/playedu-api/src/main/java/xyz/playedu/api/request/backend/DepartmentRequest.java) | 部门组织树的 HTTP 请求参数对象；承接 JSON 字段及参数校验约束，不直接操作数据库。 | 字段：name、parentId、sort |
| [request/backend/DepartmentSortRequest.java](C:/Users/86198/Desktop/play/playedu-api/playedu-api/src/main/java/xyz/playedu/api/request/backend/DepartmentSortRequest.java) | 部门排序的 HTTP 请求参数对象；承接 JSON 字段及参数校验约束，不直接操作数据库。 | 字段：ids |
| [request/backend/LoginRequest.java](C:/Users/86198/Desktop/play/playedu-api/playedu-api/src/main/java/xyz/playedu/api/request/backend/LoginRequest.java) | 登录的 HTTP 请求参数对象；承接 JSON 字段及参数校验约束，不直接操作数据库。 | 数据/常量定义；详见源码 |
| [request/backend/PasswordChangeRequest.java](C:/Users/86198/Desktop/play/playedu-api/playedu-api/src/main/java/xyz/playedu/api/request/backend/PasswordChangeRequest.java) | 修改管理员密码的 HTTP 请求参数对象；承接 JSON 字段及参数校验约束，不直接操作数据库。 | 字段：oldPassword、newPassword |
| [request/backend/PointAdjustmentRequest.java](C:/Users/86198/Desktop/play/playedu-api/playedu-api/src/main/java/xyz/playedu/api/request/backend/PointAdjustmentRequest.java) | 人工调整积分的 HTTP 请求参数对象；承接 JSON 字段及参数校验约束，不直接操作数据库。 | 字段：userId、delta、reason、requestKey |
| [request/backend/PointCodeImportRequest.java](C:/Users/86198/Desktop/play/playedu-api/playedu-api/src/main/java/xyz/playedu/api/request/backend/PointCodeImportRequest.java) | 批量导入兑换码的 HTTP 请求参数对象；承接 JSON 字段及参数校验约束，不直接操作数据库。 | 字段：codes、multilineCodes |
| [request/backend/PointProductRequest.java](C:/Users/86198/Desktop/play/playedu-api/playedu-api/src/main/java/xyz/playedu/api/request/backend/PointProductRequest.java) | 积分商品的 HTTP 请求参数对象；承接 JSON 字段及参数校验约束，不直接操作数据库。 | 字段：name、pointsPrice |
| [request/backend/ResourceCategoryChangeRequest.java](C:/Users/86198/Desktop/play/playedu-api/playedu-api/src/main/java/xyz/playedu/api/request/backend/ResourceCategoryChangeRequest.java) | 变更资源分类的 HTTP 请求参数对象；承接 JSON 字段及参数校验约束，不直接操作数据库。 | 字段：ids、categoryId |
| [request/backend/ResourceCategoryParentRequest.java](C:/Users/86198/Desktop/play/playedu-api/playedu-api/src/main/java/xyz/playedu/api/request/backend/ResourceCategoryParentRequest.java) | 调整资源分类父节点的 HTTP 请求参数对象；承接 JSON 字段及参数校验约束，不直接操作数据库。 | 字段：ids、id、parentId |
| [request/backend/ResourceCategoryRequest.java](C:/Users/86198/Desktop/play/playedu-api/playedu-api/src/main/java/xyz/playedu/api/request/backend/ResourceCategoryRequest.java) | 资源与分类关联的 HTTP 请求参数对象；承接 JSON 字段及参数校验约束，不直接操作数据库。 | 字段：name、parentId、sort |
| [request/backend/ResourceCategorySortRequest.java](C:/Users/86198/Desktop/play/playedu-api/playedu-api/src/main/java/xyz/playedu/api/request/backend/ResourceCategorySortRequest.java) | 资源分类排序的 HTTP 请求参数对象；承接 JSON 字段及参数校验约束，不直接操作数据库。 | 字段：ids |
| [request/backend/ResourceDestroyMultiRequest.java](C:/Users/86198/Desktop/play/playedu-api/playedu-api/src/main/java/xyz/playedu/api/request/backend/ResourceDestroyMultiRequest.java) | 批量删除资源的 HTTP 请求参数对象；承接 JSON 字段及参数校验约束，不直接操作数据库。 | 字段：ids |
| [request/backend/ResourceRequest.java](C:/Users/86198/Desktop/play/playedu-api/playedu-api/src/main/java/xyz/playedu/api/request/backend/ResourceRequest.java) | 资源文件的 HTTP 请求参数对象；承接 JSON 字段及参数校验约束，不直接操作数据库。 | 字段：categoryId、name、extension、size、disk、fileId、path、url、duration、poster、parentId |
| [request/backend/ResourceUpdateRequest.java](C:/Users/86198/Desktop/play/playedu-api/playedu-api/src/main/java/xyz/playedu/api/request/backend/ResourceUpdateRequest.java) | 更新资源的 HTTP 请求参数对象；承接 JSON 字段及参数校验约束，不直接操作数据库。 | 字段：name、categoryId |
| [request/backend/UploadFileMergeRequest.java](C:/Users/86198/Desktop/play/playedu-api/playedu-api/src/main/java/xyz/playedu/api/request/backend/UploadFileMergeRequest.java) | 合并上传分片的 HTTP 请求参数对象；承接 JSON 字段及参数校验约束，不直接操作数据库。 | 字段：filename、uploadId、originalFilename、size、duration、extension、categoryIds、poster |
| [request/backend/UserImportRequest.java](C:/Users/86198/Desktop/play/playedu-api/playedu-api/src/main/java/xyz/playedu/api/request/backend/UserImportRequest.java) | 批量导入学员的 HTTP 请求参数对象；承接 JSON 字段及参数校验约束，不直接操作数据库。 | 字段：deps、email、name、password、idCard、users、startLine |
| [request/backend/UserRequest.java](C:/Users/86198/Desktop/play/playedu-api/playedu-api/src/main/java/xyz/playedu/api/request/backend/UserRequest.java) | 学员账号的 HTTP 请求参数对象；承接 JSON 字段及参数校验约束，不直接操作数据库。 | 字段：email、name、avatar、password、idCard、depIds |

#### request/frontend

| 文件 | 作用 | 方法、字段或 SQL 线索 |
|---|---|---|
| [request/frontend/ChangePasswordRequest.java](C:/Users/86198/Desktop/play/playedu-api/playedu-api/src/main/java/xyz/playedu/api/request/frontend/ChangePasswordRequest.java) | 修改学员密码的 HTTP 请求参数对象；承接 JSON 字段及参数校验约束，不直接操作数据库。 | 字段：oldPassword、newPassword |
| [request/frontend/CourseHourRecordRequest.java](C:/Users/86198/Desktop/play/playedu-api/playedu-api/src/main/java/xyz/playedu/api/request/frontend/CourseHourRecordRequest.java) | 客户端提交课时学习记录的 HTTP 请求参数对象；承接 JSON 字段及参数校验约束，不直接操作数据库。 | 字段：duration |
| [request/frontend/LearningHeartbeatRequest.java](C:/Users/86198/Desktop/play/playedu-api/playedu-api/src/main/java/xyz/playedu/api/request/frontend/LearningHeartbeatRequest.java) | 学习心跳的 HTTP 请求参数对象；承接 JSON 字段及参数校验约束，不直接操作数据库。 | 字段：sessionId |
| [request/frontend/LearningStopRequest.java](C:/Users/86198/Desktop/play/playedu-api/playedu-api/src/main/java/xyz/playedu/api/request/frontend/LearningStopRequest.java) | 停止学习心跳的 HTTP 请求参数对象；承接 JSON 字段及参数校验约束，不直接操作数据库。 | 字段：sessionId |
| [request/frontend/LoginLdapRequest.java](C:/Users/86198/Desktop/play/playedu-api/playedu-api/src/main/java/xyz/playedu/api/request/frontend/LoginLdapRequest.java) | LDAP 登录的 HTTP 请求参数对象；承接 JSON 字段及参数校验约束，不直接操作数据库。 | 字段：username、password |
| [request/frontend/LoginPasswordRequest.java](C:/Users/86198/Desktop/play/playedu-api/playedu-api/src/main/java/xyz/playedu/api/request/frontend/LoginPasswordRequest.java) | 账号密码登录的 HTTP 请求参数对象；承接 JSON 字段及参数校验约束，不直接操作数据库。 | 字段：email、password |
| [request/frontend/PointRedemptionRequest.java](C:/Users/86198/Desktop/play/playedu-api/playedu-api/src/main/java/xyz/playedu/api/request/frontend/PointRedemptionRequest.java) | 积分兑换记录的 HTTP 请求参数对象；承接 JSON 字段及参数校验约束，不直接操作数据库。 | 字段：productId、requestKey |

#### schedule

| 文件 | 作用 | 方法、字段或 SQL 线索 |
|---|---|---|
| [schedule/LDAPSchedule.java](C:/Users/86198/Desktop/play/playedu-api/playedu-api/src/main/java/xyz/playedu/api/schedule/LDAPSchedule.java) | 定时触发 LDAP 同步。 | 方法：sync |

#### service

| 文件 | 作用 | 方法、字段或 SQL 线索 |
|---|---|---|
| [service/UserDeletionService.java](C:/Users/86198/Desktop/play/playedu-api/playedu-api/src/main/java/xyz/playedu/api/service/UserDeletionService.java) | 跨模块删除学员的业务接口。 | 方法：destroy |

#### service/impl

| 文件 | 作用 | 方法、字段或 SQL 线索 |
|---|---|---|
| [service/impl/UserDeletionServiceImpl.java](C:/Users/86198/Desktop/play/playedu-api/playedu-api/src/main/java/xyz/playedu/api/service/impl/UserDeletionServiceImpl.java) | 在事务中协调删除学员、部门关联、学习记录、登录记录、积分流水与兑换记录，并协调排行榜清理。 | 方法：destroy |

#### 测试文件

| 文件 | 作用 | 方法、字段或 SQL 线索 |
|---|---|---|
| [test/controller/acceptance/AcceptanceProbeControllerTest.java](C:/Users/86198/Desktop/play/playedu-api/playedu-api/src/test/java/xyz/playedu/api/controller/acceptance/AcceptanceProbeControllerTest.java) | 验证 AcceptanceProbeController 的指定功能及异常分支；具体用例见右侧方法。 | 数据/常量定义；详见源码 |
| [test/controller/backend/PointAdminControllerTest.java](C:/Users/86198/Desktop/play/playedu-api/playedu-api/src/test/java/xyz/playedu/api/controller/backend/PointAdminControllerTest.java) | 验证 PointAdminController 的指定功能及异常分支；具体用例见右侧方法。 | 数据/常量定义；详见源码 |
| [test/controller/ExceptionControllerTest.java](C:/Users/86198/Desktop/play/playedu-api/playedu-api/src/test/java/xyz/playedu/api/controller/ExceptionControllerTest.java) | 验证 ExceptionController 的指定功能及异常分支；具体用例见右侧方法。 | 数据/常量定义；详见源码 |
| [test/controller/frontend/HourControllerLearningLeaseRedisIntegrationTest.java](C:/Users/86198/Desktop/play/playedu-api/playedu-api/src/test/java/xyz/playedu/api/controller/frontend/HourControllerLearningLeaseRedisIntegrationTest.java) | 验证 HourControllerLearningLeaseRedis 的集成行为（实际依赖与事务/并发等）；具体用例见右侧方法。 | 方法：postHeartbeat、sessionId、hour、getZone、withZone、instant |
| [test/controller/frontend/HourControllerLearningLeaseTest.java](C:/Users/86198/Desktop/play/playedu-api/playedu-api/src/test/java/xyz/playedu/api/controller/frontend/HourControllerLearningLeaseTest.java) | 验证 HourControllerLearningLease 的指定功能及异常分支；具体用例见右侧方法。 | 数据/常量定义；详见源码 |
| [test/controller/frontend/PointsControllerTest.java](C:/Users/86198/Desktop/play/playedu-api/playedu-api/src/test/java/xyz/playedu/api/controller/frontend/PointsControllerTest.java) | 验证 PointsController 的指定功能及异常分支；具体用例见右侧方法。 | 方法：product |
| [test/service/UserDeletionServiceIntegrationTest.java](C:/Users/86198/Desktop/play/playedu-api/playedu-api/src/test/java/xyz/playedu/api/service/UserDeletionServiceIntegrationTest.java) | 验证 UserDeletionService 的集成行为（实际依赖与事务/并发等）；具体用例见右侧方法。 | 方法：count、statusOfCode |


### playedu-common

公共基础业务与基础设施。domain/service/mapper 覆盖账号、组织、权限、配置和 LDAP；config/context/redis/util 为其他模块提供运行支撑。

#### 入口与构建

| 文件 | 作用 | 方法、字段或 SQL 线索 |
|---|---|---|
| [pom.xml](C:/Users/86198/Desktop/play/playedu-api/playedu-common/pom.xml) | 该模块的 Maven 构建定义：继承父工程、声明模块依赖与构建插件。 | — |

#### 资源与配置

| 文件 | 作用 | 方法、字段或 SQL 线索 |
|---|---|---|
| [src/main/resources/ip2region/ip2region.xdb](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/resources/ip2region/ip2region.xdb) | IP 地域查询数据库，供 IpUtil 使用。 | — |
| [src/main/resources/mapper/AdminLogMapper.xml](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/resources/mapper/AdminLogMapper.xml) | 管理员操作日志的数据访问接口；通过 MyBatis-Plus 基础方法、注解 SQL 或 XML 执行数据库操作。 | 绑定 `xyz.playedu.common.mapper.AdminLogMapper`；SQL：paginate、paginateCount |
| [src/main/resources/mapper/AdminPermissionMapper.xml](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/resources/mapper/AdminPermissionMapper.xml) | 后台权限项的数据访问接口；通过 MyBatis-Plus 基础方法、注解 SQL 或 XML 执行数据库操作。 | 绑定 `xyz.playedu.common.mapper.AdminPermissionMapper`；仅结果映射/SQL 片段，无独立增删改查语句 |
| [src/main/resources/mapper/AdminRoleMapper.xml](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/resources/mapper/AdminRoleMapper.xml) | 管理员角色的数据访问接口；通过 MyBatis-Plus 基础方法、注解 SQL 或 XML 执行数据库操作。 | 绑定 `xyz.playedu.common.mapper.AdminRoleMapper`；仅结果映射/SQL 片段，无独立增删改查语句 |
| [src/main/resources/mapper/AdminRolePermissionMapper.xml](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/resources/mapper/AdminRolePermissionMapper.xml) | 角色与权限关联的数据访问接口；通过 MyBatis-Plus 基础方法、注解 SQL 或 XML 执行数据库操作。 | 绑定 `xyz.playedu.common.mapper.AdminRolePermissionMapper`；仅结果映射/SQL 片段，无独立增删改查语句 |
| [src/main/resources/mapper/AdminUserMapper.xml](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/resources/mapper/AdminUserMapper.xml) | 管理员账号的数据访问接口；通过 MyBatis-Plus 基础方法、注解 SQL 或 XML 执行数据库操作。 | 绑定 `xyz.playedu.common.mapper.AdminUserMapper`；仅结果映射/SQL 片段，无独立增删改查语句 |
| [src/main/resources/mapper/AdminUserRoleMapper.xml](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/resources/mapper/AdminUserRoleMapper.xml) | 管理员与角色关联的数据访问接口；通过 MyBatis-Plus 基础方法、注解 SQL 或 XML 执行数据库操作。 | 绑定 `xyz.playedu.common.mapper.AdminUserRoleMapper`；仅结果映射/SQL 片段，无独立增删改查语句 |
| [src/main/resources/mapper/AppConfigMapper.xml](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/resources/mapper/AppConfigMapper.xml) | 应用配置的数据访问接口；通过 MyBatis-Plus 基础方法、注解 SQL 或 XML 执行数据库操作。 | 绑定 `xyz.playedu.common.mapper.AppConfigMapper`；仅结果映射/SQL 片段，无独立增删改查语句 |
| [src/main/resources/mapper/CategoryMapper.xml](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/resources/mapper/CategoryMapper.xml) | 分类树的数据访问接口；通过 MyBatis-Plus 基础方法、注解 SQL 或 XML 执行数据库操作。 | 绑定 `xyz.playedu.common.mapper.CategoryMapper`；仅结果映射/SQL 片段，无独立增删改查语句 |
| [src/main/resources/mapper/DepartmentMapper.xml](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/resources/mapper/DepartmentMapper.xml) | 部门组织树的数据访问接口；通过 MyBatis-Plus 基础方法、注解 SQL 或 XML 执行数据库操作。 | 绑定 `xyz.playedu.common.mapper.DepartmentMapper`；SQL：getDepartmentsUserCount |
| [src/main/resources/mapper/LdapDepartmentMapper.xml](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/resources/mapper/LdapDepartmentMapper.xml) | LDAP 部门与本地部门映射的数据访问接口；通过 MyBatis-Plus 基础方法、注解 SQL 或 XML 执行数据库操作。 | 绑定 `xyz.playedu.common.mapper.LdapDepartmentMapper`；仅结果映射/SQL 片段，无独立增删改查语句 |
| [src/main/resources/mapper/LdapSyncDepartmentDetailMapper.xml](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/resources/mapper/LdapSyncDepartmentDetailMapper.xml) | LDAP 部门同步明细的数据访问接口；通过 MyBatis-Plus 基础方法、注解 SQL 或 XML 执行数据库操作。 | 绑定 `xyz.playedu.common.mapper.LdapSyncDepartmentDetailMapper`；仅结果映射/SQL 片段，无独立增删改查语句 |
| [src/main/resources/mapper/LdapSyncRecordMapper.xml](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/resources/mapper/LdapSyncRecordMapper.xml) | LDAP 同步任务记录的数据访问接口；通过 MyBatis-Plus 基础方法、注解 SQL 或 XML 执行数据库操作。 | 绑定 `xyz.playedu.common.mapper.LdapSyncRecordMapper`；仅结果映射/SQL 片段，无独立增删改查语句 |
| [src/main/resources/mapper/LdapSyncUserDetailMapper.xml](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/resources/mapper/LdapSyncUserDetailMapper.xml) | LDAP 用户同步明细的数据访问接口；通过 MyBatis-Plus 基础方法、注解 SQL 或 XML 执行数据库操作。 | 绑定 `xyz.playedu.common.mapper.LdapSyncUserDetailMapper`；仅结果映射/SQL 片段，无独立增删改查语句 |
| [src/main/resources/mapper/LdapUserMapper.xml](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/resources/mapper/LdapUserMapper.xml) | LDAP 用户与本地学员映射的数据访问接口；通过 MyBatis-Plus 基础方法、注解 SQL 或 XML 执行数据库操作。 | 绑定 `xyz.playedu.common.mapper.LdapUserMapper`；仅结果映射/SQL 片段，无独立增删改查语句 |
| [src/main/resources/mapper/UserDepartmentMapper.xml](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/resources/mapper/UserDepartmentMapper.xml) | 学员与部门关联的数据访问接口；通过 MyBatis-Plus 基础方法、注解 SQL 或 XML 执行数据库操作。 | 绑定 `xyz.playedu.common.mapper.UserDepartmentMapper`；仅结果映射/SQL 片段，无独立增删改查语句 |
| [src/main/resources/mapper/UserLoginRecordMapper.xml](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/resources/mapper/UserLoginRecordMapper.xml) | 学员登录记录的数据访问接口；通过 MyBatis-Plus 基础方法、注解 SQL 或 XML 执行数据库操作。 | 绑定 `xyz.playedu.common.mapper.UserLoginRecordMapper`；仅结果映射/SQL 片段，无独立增删改查语句 |
| [src/main/resources/mapper/UserMapper.xml](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/resources/mapper/UserMapper.xml) | 学员账号的数据访问接口；通过 MyBatis-Plus 基础方法、注解 SQL 或 XML 执行数据库操作。 | 绑定 `xyz.playedu.common.mapper.UserMapper`；SQL：paginateCount、paginate |
| [src/main/resources/mapper/UserUploadImageLogMapper.xml](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/resources/mapper/UserUploadImageLogMapper.xml) | 学员图片上传日志的数据访问接口；通过 MyBatis-Plus 基础方法、注解 SQL 或 XML 执行数据库操作。 | 绑定 `xyz.playedu.common.mapper.UserUploadImageLogMapper`；仅结果映射/SQL 片段，无独立增删改查语句 |

#### annotation

| 文件 | 作用 | 方法、字段或 SQL 线索 |
|---|---|---|
| [annotation/BackendPermission.java](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/annotation/BackendPermission.java) | 后台权限注解：用 slug 声明接口所需权限，由 BackendPermissionAspect 执行检查。 | 方法：slug |
| [annotation/Log.java](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/annotation/Log.java) | 操作日志注解：声明业务标题和操作类型，由 AdminLogAspect 记录日志。 | 方法：title、businessType |

#### bus

| 文件 | 作用 | 方法、字段或 SQL 线索 |
|---|---|---|
| [bus/BackendBus.java](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/bus/BackendBus.java) | 聚合管理员角色权限，判断超级管理员、匹配免登录路径，并依据字段权限进行隐私脱敏。 | 方法：inUnAuthWhitelist、adminUserPermissions、valueHidden、isSuperAdmin |
| [bus/LDAPBus.java](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/bus/LDAPBus.java) | 编排 LDAP 部门与学员同步，记录任务及逐项明细、统计变化，并将同步数据保存到 S3。 | 方法：enabledLDAP、hasSyncInProgress、syncAndRecord、collectDepartmentSyncDetails、collectUserSyncDetails、collectSyncStatistics、saveDataToS3、departmentSync、userSync、singleUserSync |

#### config

| 文件 | 作用 | 方法、字段或 SQL 线索 |
|---|---|---|
| [config/AuthConfig.java](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/config/AuthConfig.java) | 认证相关配置对象，保存过期时间等配置。 | 数据/常量定义；详见源码 |
| [config/MybatisPlusConfig.java](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/config/MybatisPlusConfig.java) | 注册 MyBatis-Plus 插件，包括分页处理。 | 方法：mybatisPlusInterceptor |
| [config/PlayEduConfig.java](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/config/PlayEduConfig.java) | 绑定 playedu 配置，提供环境、测试开关、实例标识及相关业务配置。 | 数据/常量定义；详见源码 |
| [config/SaTokenConfig.java](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/config/SaTokenConfig.java) | 将 Sa-Token 的 StpLogic 配置为 JWT Simple 模式。 | 方法：getStpLogicJwt |
| [config/UniqueNameGeneratorConfig.java](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/config/UniqueNameGeneratorConfig.java) | 以完整类名作为 Spring Bean 名，避免管理端和学员端同名 Controller 的 Bean 名冲突。 | 方法：generateBeanName |

#### constant

| 文件 | 作用 | 方法、字段或 SQL 线索 |
|---|---|---|
| [constant/BackendConstant.java](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/constant/BackendConstant.java) | 管理端免登录白名单、超级管理员角色标识及隐私字段类型等常量。 | 数据/常量定义；详见源码 |
| [constant/BPermissionConstant.java](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/constant/BPermissionConstant.java) | 后台权限 slug 及权限类型常量，是注解、权限初始化与界面权限判断的共同词汇。 | 数据/常量定义；详见源码 |
| [constant/BusinessTypeConstant.java](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/constant/BusinessTypeConstant.java) | 操作日志的业务类型枚举，如新增、修改、删除、查询。 | 数据/常量定义；详见源码 |
| [constant/CommonConstant.java](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/constant/CommonConstant.java) | 公共数字、业务分类及状态等常量。 | 数据/常量定义；详见源码 |
| [constant/ConfigConstant.java](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/constant/ConfigConstant.java) | 数据库应用配置项的键名，例如站点、S3、LDAP 设置。 | 数据/常量定义；详见源码 |
| [constant/FrontendConstant.java](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/constant/FrontendConstant.java) | 学员端免登录白名单等常量。 | 数据/常量定义；详见源码 |
| [constant/SystemConstant.java](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/constant/SystemConstant.java) | 系统版本、环境、JWT 身份区分标识及其他系统常量。 | 数据/常量定义；详见源码 |

#### context

| 文件 | 作用 | 方法、字段或 SQL 线索 |
|---|---|---|
| [context/BCtx.java](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/context/BCtx.java) | 通过 ThreadLocal 保存当前请求的管理员、权限及相关上下文；请求结束时必须清理。 | 方法：put、get、isNull、remove、getId、setId、getAdminUser、setAdminUser、setAdminPer、getAdminPer、setConfig、getConfig |
| [context/FCtx.java](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/context/FCtx.java) | 通过 ThreadLocal 保存当前请求的学员及 JWT 标识；请求结束时清理。 | 方法：put、get、remove、setId、getId、setUser、getUser、setJWtJti、getJwtJti |

#### domain

| 文件 | 作用 | 方法、字段或 SQL 线索 |
|---|---|---|
| [domain/AdminLog.java](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/domain/AdminLog.java) | 管理员操作日志的持久化实体，映射数据库字段。 | 字段：id、adminId、adminName、module、title、opt、method、requestMethod、url、param、result、ip、ipArea、errorMsg、createdAt |
| [domain/AdminPermission.java](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/domain/AdminPermission.java) | 后台权限项的持久化实体，映射数据库字段。 | 字段：id、type、groupName、sort、name、slug、createdAt |
| [domain/AdminRole.java](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/domain/AdminRole.java) | 管理员角色的持久化实体，映射数据库字段。 | 字段：id、name、slug、createdAt、updatedAt |
| [domain/AdminRolePermission.java](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/domain/AdminRolePermission.java) | 角色与权限关联的持久化实体，映射数据库字段。 | 字段：roleId、permId |
| [domain/AdminUser.java](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/domain/AdminUser.java) | 管理员账号的持久化实体，映射数据库字段。 | 字段：id、name、email、loginIp、loginAt、isBanLogin、loginTimes、createdAt、updatedAt |
| [domain/AdminUserRole.java](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/domain/AdminUserRole.java) | 管理员与角色关联的持久化实体，映射数据库字段。 | 字段：adminId、roleId |
| [domain/AppConfig.java](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/domain/AppConfig.java) | 应用配置的持久化实体，映射数据库字段。 | 字段：id、groupName、name、sort、fieldType、keyName、keyValue、optionValue、isPrivate、help |
| [domain/Category.java](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/domain/Category.java) | 分类树的持久化实体，映射数据库字段。 | 字段：id、parentId、parentChain、name、sort |
| [domain/Department.java](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/domain/Department.java) | 部门组织树的持久化实体，映射数据库字段。 | 字段：id、name、parentId、parentChain、sort、createdAt、updatedAt |
| [domain/LdapDepartment.java](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/domain/LdapDepartment.java) | LDAP 部门与本地部门映射的持久化实体，映射数据库字段。 | 字段：id、uuid、departmentId、dn、createdAt、updatedAt |
| [domain/LdapSyncDepartmentDetail.java](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/domain/LdapSyncDepartmentDetail.java) | LDAP 部门同步明细的持久化实体，映射数据库字段。 | 字段：id、recordId、departmentId、uuid、dn、name、action、createdAt |
| [domain/LdapSyncRecord.java](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/domain/LdapSyncRecord.java) | LDAP 同步任务记录的持久化实体，映射数据库字段。 | 字段：id、adminId、status、s3FilePath、totalDepartmentCount、createdDepartmentCount、updatedDepartmentCount、deletedDepartmentCount、totalUserCount、createdUserCount、updatedUserCount、deletedUserCount、bannedUserCount、errorMessage、createdAt、updatedAt |
| [domain/LdapSyncUserDetail.java](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/domain/LdapSyncUserDetail.java) | LDAP 用户同步明细的持久化实体，映射数据库字段。 | 字段：id、recordId、userId、uuid、dn、cn、uid、email、ou、action、createdAt |
| [domain/LdapUser.java](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/domain/LdapUser.java) | LDAP 用户与本地学员映射的持久化实体，映射数据库字段。 | 字段：id、uuid、userId、cn、dn、ou、uid、email、createdAt、updatedAt |
| [domain/User.java](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/domain/User.java) | 学员账号的持久化实体，映射数据库字段。 | 字段：id、email、name、avatar、idCard、credit1、createIp、createCity、isActive、isLock、isVerify、verifyAt、isSetPassword、loginAt、createdAt、updatedAt |
| [domain/UserDepartment.java](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/domain/UserDepartment.java) | 学员与部门关联的持久化实体，映射数据库字段。 | 字段：userId、depId |
| [domain/UserLoginRecord.java](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/domain/UserLoginRecord.java) | 学员登录记录的持久化实体，映射数据库字段。 | 字段：id、userId、jti、ip、ipArea、browser、browserVersion、os、expired、isLogout、createdAt |
| [domain/UserUploadImageLog.java](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/domain/UserUploadImageLog.java) | 学员图片上传日志的持久化实体，映射数据库字段。 | 字段：id、userId、typed、scene、driver、path、url、size、name、createdAt |

#### exception

| 文件 | 作用 | 方法、字段或 SQL 线索 |
|---|---|---|
| [exception/NotFoundException.java](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/exception/NotFoundException.java) | 查找对象不存在时抛出的异常，由统一异常处理器转换为业务响应。 | 数据/常量定义；详见源码 |
| [exception/ServiceException.java](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/exception/ServiceException.java) | 可携带业务提示的运行时异常，供各模块报告业务失败。 | 数据/常量定义；详见源码 |

#### mapper

| 文件 | 作用 | 方法、字段或 SQL 线索 |
|---|---|---|
| [mapper/AdminLogMapper.java](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/mapper/AdminLogMapper.java) | 管理员操作日志的数据访问接口；通过 MyBatis-Plus 基础方法、注解 SQL 或 XML 执行数据库操作。 | 方法：paginate、paginateCount |
| [mapper/AdminPermissionMapper.java](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/mapper/AdminPermissionMapper.java) | 后台权限项的数据访问接口；通过 MyBatis-Plus 基础方法、注解 SQL 或 XML 执行数据库操作。 | 继承 `BaseMapper<AdminPermission>`，基础 CRUD 来自 MyBatis-Plus |
| [mapper/AdminRoleMapper.java](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/mapper/AdminRoleMapper.java) | 管理员角色的数据访问接口；通过 MyBatis-Plus 基础方法、注解 SQL 或 XML 执行数据库操作。 | 继承 `BaseMapper<AdminRole>`，基础 CRUD 来自 MyBatis-Plus |
| [mapper/AdminRolePermissionMapper.java](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/mapper/AdminRolePermissionMapper.java) | 角色与权限关联的数据访问接口；通过 MyBatis-Plus 基础方法、注解 SQL 或 XML 执行数据库操作。 | 继承 `BaseMapper<AdminRolePermission>`，基础 CRUD 来自 MyBatis-Plus |
| [mapper/AdminUserMapper.java](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/mapper/AdminUserMapper.java) | 管理员账号的数据访问接口；通过 MyBatis-Plus 基础方法、注解 SQL 或 XML 执行数据库操作。 | 继承 `BaseMapper<AdminUser>`，基础 CRUD 来自 MyBatis-Plus |
| [mapper/AdminUserRoleMapper.java](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/mapper/AdminUserRoleMapper.java) | 管理员与角色关联的数据访问接口；通过 MyBatis-Plus 基础方法、注解 SQL 或 XML 执行数据库操作。 | 继承 `BaseMapper<AdminUserRole>`，基础 CRUD 来自 MyBatis-Plus |
| [mapper/AppConfigMapper.java](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/mapper/AppConfigMapper.java) | 应用配置的数据访问接口；通过 MyBatis-Plus 基础方法、注解 SQL 或 XML 执行数据库操作。 | 继承 `BaseMapper<AppConfig>`，基础 CRUD 来自 MyBatis-Plus |
| [mapper/CategoryMapper.java](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/mapper/CategoryMapper.java) | 分类树的数据访问接口；通过 MyBatis-Plus 基础方法、注解 SQL 或 XML 执行数据库操作。 | 继承 `BaseMapper<Category>`，基础 CRUD 来自 MyBatis-Plus |
| [mapper/DepartmentMapper.java](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/mapper/DepartmentMapper.java) | 部门组织树的数据访问接口；通过 MyBatis-Plus 基础方法、注解 SQL 或 XML 执行数据库操作。 | 方法：getDepartmentsUserCount |
| [mapper/LdapDepartmentMapper.java](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/mapper/LdapDepartmentMapper.java) | LDAP 部门与本地部门映射的数据访问接口；通过 MyBatis-Plus 基础方法、注解 SQL 或 XML 执行数据库操作。 | 继承 `BaseMapper<LdapDepartment>`，基础 CRUD 来自 MyBatis-Plus |
| [mapper/LdapSyncDepartmentDetailMapper.java](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/mapper/LdapSyncDepartmentDetailMapper.java) | LDAP 部门同步明细的数据访问接口；通过 MyBatis-Plus 基础方法、注解 SQL 或 XML 执行数据库操作。 | 继承 `BaseMapper<LdapSyncDepartmentDetail>`，基础 CRUD 来自 MyBatis-Plus |
| [mapper/LdapSyncRecordMapper.java](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/mapper/LdapSyncRecordMapper.java) | LDAP 同步任务记录的数据访问接口；通过 MyBatis-Plus 基础方法、注解 SQL 或 XML 执行数据库操作。 | 继承 `BaseMapper<LdapSyncRecord>`，基础 CRUD 来自 MyBatis-Plus |
| [mapper/LdapSyncUserDetailMapper.java](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/mapper/LdapSyncUserDetailMapper.java) | LDAP 用户同步明细的数据访问接口；通过 MyBatis-Plus 基础方法、注解 SQL 或 XML 执行数据库操作。 | 继承 `BaseMapper<LdapSyncUserDetail>`，基础 CRUD 来自 MyBatis-Plus |
| [mapper/LdapUserMapper.java](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/mapper/LdapUserMapper.java) | LDAP 用户与本地学员映射的数据访问接口；通过 MyBatis-Plus 基础方法、注解 SQL 或 XML 执行数据库操作。 | 继承 `BaseMapper<LdapUser>`，基础 CRUD 来自 MyBatis-Plus |
| [mapper/UserDepartmentMapper.java](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/mapper/UserDepartmentMapper.java) | 学员与部门关联的数据访问接口；通过 MyBatis-Plus 基础方法、注解 SQL 或 XML 执行数据库操作。 | 继承 `BaseMapper<UserDepartment>`，基础 CRUD 来自 MyBatis-Plus |
| [mapper/UserLoginRecordMapper.java](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/mapper/UserLoginRecordMapper.java) | 学员登录记录的数据访问接口；通过 MyBatis-Plus 基础方法、注解 SQL 或 XML 执行数据库操作。 | 继承 `BaseMapper<UserLoginRecord>`，基础 CRUD 来自 MyBatis-Plus |
| [mapper/UserMapper.java](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/mapper/UserMapper.java) | 学员账号的数据访问接口；通过 MyBatis-Plus 基础方法、注解 SQL 或 XML 执行数据库操作。 | 方法：lockIdForUpdate、paginate、paginateCount |
| [mapper/UserUploadImageLogMapper.java](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/mapper/UserUploadImageLogMapper.java) | 学员图片上传日志的数据访问接口；通过 MyBatis-Plus 基础方法、注解 SQL 或 XML 执行数据库操作。 | 继承 `BaseMapper<UserUploadImageLog>`，基础 CRUD 来自 MyBatis-Plus |

#### redis

| 文件 | 作用 | 方法、字段或 SQL 线索 |
|---|---|---|
| [redis/ApiRateLimitUnavailableException.java](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/redis/ApiRateLimitUnavailableException.java) | 限流依赖不可用时抛出的异常。 | 数据/常量定义；详见源码 |
| [redis/ApiRequestRateLimiter.java](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/redis/ApiRequestRateLimiter.java) | 以 Redis/Redisson 实现跨实例共享的 API 请求限流，返回是否允许及相关决策。 | 方法：acquire、subject、RateLimitDecision |
| [redis/LearningLeaseUnavailableException.java](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/redis/LearningLeaseUnavailableException.java) | 学习租约所依赖的 Redis 不可用时的异常。 | 数据/常量定义；详见源码 |
| [redis/LoginFailureLimitException.java](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/redis/LoginFailureLimitException.java) | 登录失败达到限制或处于锁定状态时的异常。 | 数据/常量定义；详见源码 |
| [redis/LoginFailureTracker.java](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/redis/LoginFailureTracker.java) | 用 Redis 记录登录失败、判断是否锁定、成功后重置状态。 | 方法：assertNotLocked、recordFailure、reset、status、assertUnlocked、execute、key、parseStatus、remainingSeconds、waitingTime、keySegment、lockWindowSeconds等 |
| [redis/LoginFailureTrackingUnavailableException.java](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/redis/LoginFailureTrackingUnavailableException.java) | 登录失败保护依赖不可用时的异常。 | 数据/常量定义；详见源码 |
| [redis/RedisDistributedLock.java](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/redis/RedisDistributedLock.java) | 封装 Redisson 分布式锁及加锁执行流程，处理竞争、不可用情况和事务完成后的释放。 | 方法：execute、executeWithLock、acquire、lock、releaseAfterTransaction、afterCompletion、release、unavailable |
| [redis/RedisKeyspace.java](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/redis/RedisKeyspace.java) | 统一拼接业务键、锁键、限流键及前缀，管理 Redis 命名空间。 | 方法：getKeyPrefix、setKeyPrefix、key、lock、rateLimiter、segment |
| [redis/RedisLockException.java](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/redis/RedisLockException.java) | 分布式锁获取或执行失败的业务异常。 | 数据/常量定义；详见源码 |
| [redis/RedisRuntimeConfiguration.java](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/redis/RedisRuntimeConfiguration.java) | 创建 Redisson 客户端及 Redis 启动检查，集中配置 Redis 运行依赖。 | 方法：redissonClient、redisRequiredStartupCheck、toMilliseconds |

#### service

| 文件 | 作用 | 方法、字段或 SQL 线索 |
|---|---|---|
| [service/AdminLogService.java](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/service/AdminLogService.java) | 定义管理员操作日志的业务操作接口，基础 CRUD 可能继承自 IService，具体扩展能力见方法列表。 | 方法：paginate、find |
| [service/AdminPermissionService.java](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/service/AdminPermissionService.java) | 定义后台权限项的业务操作接口，基础 CRUD 可能继承自 IService，具体扩展能力见方法列表。 | 方法：allSlugs、listOrderBySortAsc、getSlugsByIds、allIds、chunks |
| [service/AdminRolePermissionService.java](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/service/AdminRolePermissionService.java) | 定义角色与权限关联的业务操作接口，基础 CRUD 可能继承自 IService，具体扩展能力见方法列表。 | 继承 `IService<AdminRolePermission>`，基础 CRUD 来自 MyBatis-Plus |
| [service/AdminRoleService.java](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/service/AdminRoleService.java) | 定义管理员角色的业务操作接口，基础 CRUD 可能继承自 IService，具体扩展能力见方法列表。 | 方法：getBySlug、initSuperAdminRole、createWithPermissionIds、relatePermissions、resetRelatePermissions、updateWithPermissionIds、findOrFail、removeWithPermissions、getPermissionIdsByRoleId、getPermissionIdsByRoleIds、removeRelatePermissionByRoleId |
| [service/AdminUserRoleService.java](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/service/AdminUserRoleService.java) | 定义管理员与角色关联的业务操作接口，基础 CRUD 可能继承自 IService，具体扩展能力见方法列表。 | 方法：getAdminUserIds |
| [service/AdminUserService.java](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/service/AdminUserService.java) | 定义管理员账号的业务操作接口，基础 CRUD 可能继承自 IService，具体扩展能力见方法列表。 | 方法：paginate、findByEmail、emailExists、findById、findOrFail、createWithRoleIds、relateRoles、resetRelateRoles、getRoleIdsByUserId、updateWithRoleIds、removeWithRoleIds、removeRelateRolesByUserId等 |
| [service/AppConfigService.java](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/service/AppConfigService.java) | 定义应用配置的业务操作接口，基础 CRUD 可能继承自 IService，具体扩展能力见方法列表。 | 方法：allKeys、allShow、saveFromMap、keyValues、getS3Config、getAllImageValue、enabledLdapLogin、defaultAvatar、ldapConfig |
| [service/AuthService.java](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/service/AuthService.java) | 认证基础接口，定义登录、身份检查、用户 ID、退出、JWT 解析与过期处理。 | 方法：loginUsingId、check、userId、logout、jti、expired、parse |
| [service/BackendAuthService.java](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/service/BackendAuthService.java) | 管理端认证接口。 | 方法：loginUsingId、check、userId、logout、jti、parse |
| [service/CategoryService.java](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/service/CategoryService.java) | 定义分类树的业务操作接口，基础 CRUD 可能继承自 IService，具体扩展能力见方法列表。 | 方法：listByParentId、all、findOrFail、deleteById、update、create、childrenParentChain、compParentChain、resetSort、changeParent、groupByParent、id2name等 |
| [service/DepartmentService.java](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/service/DepartmentService.java) | 定义部门组织树的业务操作接口，基础 CRUD 可能继承自 IService，具体扩展能力见方法列表。 | 方法：listByParentId、all、findOrFail、destroy、update、compParentChain、childrenParentChain、create、remoteRelateUsersByDepId、getUserIdsByDepId、changeParent、resetSort等 |
| [service/FrontendAuthService.java](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/service/FrontendAuthService.java) | 学员端认证接口。 | 方法：loginUsingId、check、userId、logout、jti、parse |
| [service/LdapDepartmentService.java](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/service/LdapDepartmentService.java) | 定义LDAP 部门与本地部门映射的业务操作接口，基础 CRUD 可能继承自 IService，具体扩展能力见方法列表。 | 方法：all、notChunkByUUIDList、destroy、create、updateDnById |
| [service/LdapSyncDepartmentDetailService.java](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/service/LdapSyncDepartmentDetailService.java) | 定义LDAP 部门同步明细的业务操作接口，基础 CRUD 可能继承自 IService，具体扩展能力见方法列表。 | 方法：batchCreate、getByRecordIdAndAction |
| [service/LdapSyncRecordService.java](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/service/LdapSyncRecordService.java) | 定义LDAP 同步任务记录的业务操作接口，基础 CRUD 可能继承自 IService，具体扩展能力见方法列表。 | 方法：create、updateSyncResult、updateSyncFailed、paginate、hasSyncInProgress、getLatestRecord |
| [service/LdapSyncUserDetailService.java](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/service/LdapSyncUserDetailService.java) | 定义LDAP 用户同步明细的业务操作接口，基础 CRUD 可能继承自 IService，具体扩展能力见方法列表。 | 方法：batchCreate、getByRecordIdAndAction |
| [service/LdapUserService.java](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/service/LdapUserService.java) | 定义LDAP 用户与本地学员映射的业务操作接口，基础 CRUD 可能继承自 IService，具体扩展能力见方法列表。 | 方法：findByUUID、store、updateUserId、updateCN、updateOU、updateEmail、updateUid、updateDN |
| [service/UserDepartmentService.java](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/service/UserDepartmentService.java) | 定义学员与部门关联的业务操作接口，基础 CRUD 可能继承自 IService，具体扩展能力见方法列表。 | 方法：getUserIdsByDepIds、storeDepIds、resetStoreDepIds |
| [service/UserLoginRecordService.java](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/service/UserLoginRecordService.java) | 定义学员登录记录的业务操作接口，基础 CRUD 可能继承自 IService，具体扩展能力见方法列表。 | 方法：store、logout、remove |
| [service/UserService.java](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/service/UserService.java) | 定义学员账号的业务操作接口，基础 CRUD 可能继承自 IService，具体扩展能力见方法列表。 | 方法：emailIsExists、paginate、existsEmailsByEmails、removeRelateDepartmentsByUserId、findOrFail、ensureExistsForUpdate、find、createWithDepIds、updateWithDepIds、getDepIdsByUserId、passwordChange、chunks等 |
| [service/UserUploadImageLogService.java](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/service/UserUploadImageLogService.java) | 定义学员图片上传日志的业务操作接口，基础 CRUD 可能继承自 IService，具体扩展能力见方法列表。 | 继承 `IService<UserUploadImageLog>`，基础 CRUD 来自 MyBatis-Plus |

#### service/impl

| 文件 | 作用 | 方法、字段或 SQL 线索 |
|---|---|---|
| [service/impl/AdminLogServiceImpl.java](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/service/impl/AdminLogServiceImpl.java) | 实现管理员操作日志业务接口；负责该对象的查询、维护及关联处理，具体能力见方法列表。 | 方法：paginate、find |
| [service/impl/AdminPermissionServiceImpl.java](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/service/impl/AdminPermissionServiceImpl.java) | 实现后台权限项业务接口；负责该对象的查询、维护及关联处理，具体能力见方法列表。 | 方法：allSlugs、listOrderBySortAsc、getSlugsByIds、allIds、chunks |
| [service/impl/AdminRolePermissionServiceImpl.java](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/service/impl/AdminRolePermissionServiceImpl.java) | 实现角色与权限关联业务接口；负责该对象的查询、维护及关联处理，具体能力见方法列表。 | 数据/常量定义；详见源码 |
| [service/impl/AdminRoleServiceImpl.java](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/service/impl/AdminRoleServiceImpl.java) | 实现管理员角色业务接口；负责该对象的查询、维护及关联处理，具体能力见方法列表。 | 方法：getBySlug、initSuperAdminRole、createWithPermissionIds、relatePermissions、resetRelatePermissions、updateWithPermissionIds、findOrFail、removeWithPermissions、getPermissionIdsByRoleId、getPermissionIdsByRoleIds、removeRelatePermissionByRoleId |
| [service/impl/AdminUserRoleServiceImpl.java](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/service/impl/AdminUserRoleServiceImpl.java) | 实现管理员与角色关联业务接口；负责该对象的查询、维护及关联处理，具体能力见方法列表。 | 方法：getAdminUserIds |
| [service/impl/AdminUserServiceImpl.java](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/service/impl/AdminUserServiceImpl.java) | 实现管理员账号业务接口；负责该对象的查询、维护及关联处理，具体能力见方法列表。 | 方法：paginate、findByEmail、findById、findOrFail、emailExists、createWithRoleIds、relateRoles、resetRelateRoles、getRoleIdsByUserId、updateWithRoleIds、removeWithRoleIds、removeRelateRolesByUserId等 |
| [service/impl/AppConfigServiceImpl.java](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/service/impl/AppConfigServiceImpl.java) | 实现应用配置业务接口；负责该对象的查询、维护及关联处理，具体能力见方法列表。 | 方法：allKeys、allShow、saveFromMap、keyValues、getS3Config、getAllImageValue、enabledLdapLogin、defaultAvatar、ldapConfig |
| [service/impl/AuthServiceImpl.java](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/service/impl/AuthServiceImpl.java) | 认证基础实现，封装 Token/JWT 的公共处理。 | 方法：loginUsingId、check、userId、logout、jti、expired、parse |
| [service/impl/BackendAuthServiceImpl.java](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/service/impl/BackendAuthServiceImpl.java) | 管理端认证实现，处理管理员身份对应的 Token/JWT。 | 方法：loginUsingId、check、userId、logout、jti、parse |
| [service/impl/CategoryServiceImpl.java](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/service/impl/CategoryServiceImpl.java) | 实现分类树业务接口；负责该对象的查询、维护及关联处理，具体能力见方法列表。 | 方法：listByParentId、all、findOrFail、deleteById、update、updateParentChain、create、childrenParentChain、compParentChain、resetSort、changeParent、groupByParent等 |
| [service/impl/DepartmentServiceImpl.java](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/service/impl/DepartmentServiceImpl.java) | 实现部门组织树业务接口；负责该对象的查询、维护及关联处理，具体能力见方法列表。 | 方法：listByParentId、all、findOrFail、destroy、update、updateParentChain、compParentChain、childrenParentChain、create、remoteRelateUsersByDepId、getUserIdsByDepId、changeParent等 |
| [service/impl/FrontendAuthServiceImpl.java](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/service/impl/FrontendAuthServiceImpl.java) | 学员端认证实现，处理学员身份对应的 Token/JWT。 | 方法：loginUsingId、check、userId、logout、jti、parse |
| [service/impl/LdapDepartmentServiceImpl.java](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/service/impl/LdapDepartmentServiceImpl.java) | 实现LDAP 部门与本地部门映射业务接口；负责该对象的查询、维护及关联处理，具体能力见方法列表。 | 方法：all、notChunkByUUIDList、destroy、create、updateDnById |
| [service/impl/LdapSyncDepartmentDetailServiceImpl.java](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/service/impl/LdapSyncDepartmentDetailServiceImpl.java) | 实现LDAP 部门同步明细业务接口；负责该对象的查询、维护及关联处理，具体能力见方法列表。 | 方法：batchCreate、getByRecordIdAndAction |
| [service/impl/LdapSyncRecordServiceImpl.java](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/service/impl/LdapSyncRecordServiceImpl.java) | 实现LDAP 同步任务记录业务接口；负责该对象的查询、维护及关联处理，具体能力见方法列表。 | 方法：create、updateSyncResult、updateSyncFailed、paginate、hasSyncInProgress、getLatestRecord |
| [service/impl/LdapSyncUserDetailServiceImpl.java](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/service/impl/LdapSyncUserDetailServiceImpl.java) | 实现LDAP 用户同步明细业务接口；负责该对象的查询、维护及关联处理，具体能力见方法列表。 | 方法：batchCreate、getByRecordIdAndAction |
| [service/impl/LdapUserServiceImpl.java](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/service/impl/LdapUserServiceImpl.java) | 实现LDAP 用户与本地学员映射业务接口；负责该对象的查询、维护及关联处理，具体能力见方法列表。 | 方法：findByUUID、store、updateUserId、updateCN、updateOU、updateEmail、updateUid、updateDN |
| [service/impl/UserDepartmentServiceImpl.java](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/service/impl/UserDepartmentServiceImpl.java) | 实现学员与部门关联业务接口；负责该对象的查询、维护及关联处理，具体能力见方法列表。 | 方法：getUserIdsByDepIds、storeDepIds、resetStoreDepIds |
| [service/impl/UserLoginRecordServiceImpl.java](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/service/impl/UserLoginRecordServiceImpl.java) | 实现学员登录记录业务接口；负责该对象的查询、维护及关联处理，具体能力见方法列表。 | 方法：store、logout、remove |
| [service/impl/UserServiceImpl.java](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/service/impl/UserServiceImpl.java) | 实现学员账号业务接口；负责该对象的查询、维护及关联处理，具体能力见方法列表。 | 方法：emailIsExists、paginate、existsEmailsByEmails、removeRelateDepartmentsByUserId、findOrFail、ensureExistsForUpdate、createWithDepIds、updateWithDepIds、getDepIdsByUserId、find、passwordChange、chunks等 |
| [service/impl/UserUploadImageLogServiceImpl.java](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/service/impl/UserUploadImageLogServiceImpl.java) | 实现学员图片上传日志业务接口；负责该对象的查询、维护及关联处理，具体能力见方法列表。 | 数据/常量定义；详见源码 |

#### types

| 文件 | 作用 | 方法、字段或 SQL 线索 |
|---|---|---|
| [types/ImageCaptchaResult.java](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/types/ImageCaptchaResult.java) | 图片验证码生成结果的数据对象。 | 数据/常量定义；详见源码 |
| [types/JsonResponse.java](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/types/JsonResponse.java) | 统一接口返回类型及 success、data、error 工厂方法。 | 字段：code、msg、data |
| [types/LdapConfig.java](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/types/LdapConfig.java) | LDAP 连接、查询及登录相关配置的数据对象。 | 字段：enabled、url、adminUser、adminPass、baseDN |
| [types/SelectOption.java](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/types/SelectOption.java) | 下拉选择项的数据对象。 | 字段：key、value |
| [types/UploadFileInfo.java](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/types/UploadFileInfo.java) | 上传文件信息的数据对象。 | 字段：originalName、extension、size、saveName、resourceType、savePath、disk |

#### types/config

| 文件 | 作用 | 方法、字段或 SQL 线索 |
|---|---|---|
| [types/config/S3Config.java](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/types/config/S3Config.java) | S3 兼容存储的连接与访问配置对象。 | 字段：accessKey、secretKey、bucket、region、endpoint |

#### types/mapper

| 文件 | 作用 | 方法、字段或 SQL 线索 |
|---|---|---|
| [types/mapper/CourseCategoryCountMapper.java](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/types/mapper/CourseCategoryCountMapper.java) | 课程分类数量统计的 SQL 查询结果对象；虽然以 Mapper 命名，但不是数据库访问接口。 | 字段：cid、total |
| [types/mapper/DepartmentsUserCountMapRes.java](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/types/mapper/DepartmentsUserCountMapRes.java) | 部门学员数量统计的 SQL 查询结果对象；虽然以 Mapper 命名，但不是数据库访问接口。 | 字段：depId、total |
| [types/mapper/ResourceCategoryCountMapper.java](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/types/mapper/ResourceCategoryCountMapper.java) | 资源分类数量统计的 SQL 查询结果对象；虽然以 Mapper 命名，但不是数据库访问接口。 | 字段：cid、total |
| [types/mapper/UserCourseHourRecordCourseCountMapper.java](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/types/mapper/UserCourseHourRecordCourseCountMapper.java) | 按课程汇总课时记录数量的 SQL 查询结果对象；虽然以 Mapper 命名，但不是数据库访问接口。 | 字段：courseId、total |
| [types/mapper/UserCourseHourRecordUserCountMapper.java](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/types/mapper/UserCourseHourRecordUserCountMapper.java) | 按学员汇总课时记录数量的 SQL 查询结果对象；虽然以 Mapper 命名，但不是数据库访问接口。 | 字段：userId、total |
| [types/mapper/UserCourseHourRecordUserFirstCreatedAtMapper.java](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/types/mapper/UserCourseHourRecordUserFirstCreatedAtMapper.java) | 按学员统计首次课时记录时间的 SQL 查询结果对象；虽然以 Mapper 命名，但不是数据库访问接口。 | 字段：userId、createdAt |

#### types/paginate

| 文件 | 作用 | 方法、字段或 SQL 线索 |
|---|---|---|
| [types/paginate/AdminLogPaginateFiler.java](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/types/paginate/AdminLogPaginateFiler.java) | 操作日志筛选条件对象，承接分页查询的过滤与排序参数。 | 字段：adminId、adminName、module、title、opt、startTime、endTime、sortField、sortAlgo、pageStart、pageSize |
| [types/paginate/AdminUserPaginateFilter.java](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/types/paginate/AdminUserPaginateFilter.java) | 管理员列表筛选条件对象，承接分页查询的过滤与排序参数。 | 字段：name、roleId |
| [types/paginate/CourseAttachmentDownloadLogPaginateFiler.java](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/types/paginate/CourseAttachmentDownloadLogPaginateFiler.java) | 附件下载日志筛选条件对象，承接分页查询的过滤与排序参数。 | 字段：userId、courseId、title、courserAttachmentId、rid、sortField、sortAlgo、pageStart、pageSize |
| [types/paginate/CoursePaginateFiler.java](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/types/paginate/CoursePaginateFiler.java) | 课程列表筛选条件对象，承接分页查询的过滤与排序参数。 | 字段：title、depIds、categoryIds、isRequired、sortField、sortAlgo、isShow、pageStart、pageSize、adminId |
| [types/paginate/PaginationResult.java](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/types/paginate/PaginationResult.java) | 分页结果容器，承载记录列表、总数、页起点和页大小等信息。 | 字段：data、total |
| [types/paginate/ResourcePaginateFilter.java](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/types/paginate/ResourcePaginateFilter.java) | 资源列表筛选条件对象，承接分页查询的过滤与排序参数。 | 字段：name、extension、disk、sortField、sortAlgo、categoryIds、type、adminId、pageStart、pageSize |
| [types/paginate/UserCourseHourRecordPaginateFilter.java](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/types/paginate/UserCourseHourRecordPaginateFilter.java) | 课时学习记录筛选条件对象，承接分页查询的过滤与排序参数。 | 字段：userId、pageStart、pageSize、sortField、sortAlgo、isFinished |
| [types/paginate/UserCourseRecordPaginateFilter.java](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/types/paginate/UserCourseRecordPaginateFilter.java) | 课程学习记录筛选条件对象，承接分页查询的过滤与排序参数。 | 字段：courseId、email、name、idCard、sortField、sortAlgo、pageStart、pageSize、userId、isFinished |
| [types/paginate/UserPaginateFilter.java](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/types/paginate/UserPaginateFilter.java) | 学员列表筛选条件对象，承接分页查询的过滤与排序参数。 | 字段：name、email、idCard、isActive、isLock、isVerify、isSetPassword、createdAt、depIds、sortField、sortAlgo、pageStart、pageSize |

#### util

| 文件 | 作用 | 方法、字段或 SQL 线索 |
|---|---|---|
| [util/Base64Util.java](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/util/Base64Util.java) | Base64 编码、解码工具。 | 方法：encode、decode、isWhiteSpace、isPad、isData、removeWhiteSpace |
| [util/HelperUtil.java](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/util/HelperUtil.java) | 通用辅助方法：UUID、随机文本、MD5、文件大小格式化、JSON、文件扩展名等。 | 方法：zeroIntegerList、MD5、uuid、randomString、randomInt、storageUnit、download、mergeMapByObj、mergeMapByStr、toJsonStr、fileExt |
| [util/IpUtil.java](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/util/IpUtil.java) | 提取客户端 IP、处理代理链、判断内网地址并查询 IP 地域。 | 方法：getSearcher、getIpAddress、getRealAddressByIP、cleanField、internalIp、textToNumericFormatV4、getHostIp、getMultistageReverseProxyIp、isUnknown |
| [util/PrivacyUtil.java](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/util/PrivacyUtil.java) | 手机号、邮箱、姓名、身份证等敏感字段脱敏工具。 | 方法：hidePhone、hideEmail、hideIDCard、hideChineseName、desValue |
| [util/RequestUtil.java](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/util/RequestUtil.java) | 读取当前 HTTP 请求、Token、User-Agent、URI、域名及协议等信息。 | 方法：handler、ua、token、url、uri、uriWithProtocol、pathname、port、domain、protocol |
| [util/S3Util.java](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/util/S3Util.java) | 封装 S3 客户端：文件上传、分片上传及合并、预签名地址、删除和读取。 | 方法：getS3Config、setConfig、getClient、saveFile、saveBytes、uploadId、uploadPart、listParts、purgeSegments、generatePartUploadPreSignUrl、merge、removeByPath等 |
| [util/StringUtil.java](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/util/StringUtil.java) | 字符串及集合判空、转换、分隔、命名格式和模式匹配等工具。 | 方法：nvl、isEmpty、isNotEmpty、isNull、isNotNull、isArray、trim、substring、str2Set、str2List、containsAnyIgnoreCase、toUnderScoreCase等 |

#### util/ldap

| 文件 | 作用 | 方法、字段或 SQL 线索 |
|---|---|---|
| [util/ldap/LdapTransformDepartment.java](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/util/ldap/LdapTransformDepartment.java) | 承载从 LDAP 读取并转换后的部门属性，供同步使用。 | 数据/常量定义；详见源码 |
| [util/ldap/LdapTransformUser.java](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/util/ldap/LdapTransformUser.java) | 承载从 LDAP 读取并转换后的用户属性，供登录和同步使用。 | 数据/常量定义；详见源码 |
| [util/ldap/LdapUtil.java](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/util/ldap/LdapUtil.java) | LDAP 连接、用户与部门查询以及邮箱/UID 登录等底层操作。 | 方法：initContext、users、parseCookie、departments、loginByMailOrUid、parseTransformUser、getAttribute、baseDNOuScope、closeContext |

#### 测试文件

| 文件 | 作用 | 方法、字段或 SQL 线索 |
|---|---|---|
| [test/redis/ApiRequestRateLimiterTest.java](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/test/java/xyz/playedu/common/redis/ApiRequestRateLimiterTest.java) | 验证 ApiRequestRateLimiter 的指定功能及异常分支；具体用例见右侧方法。 | 数据/常量定义；详见源码 |
| [test/redis/RedisRuntimeIntegrationTest.java](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/test/java/xyz/playedu/common/redis/RedisRuntimeIntegrationTest.java) | 验证 RedisRuntime 的集成行为（实际依赖与事务/并发等）；具体用例见右侧方法。 | 方法：executeProtectedOperation、waitForExpiry、createSecondRedissonClient |


### playedu-system

启动初始化和系统横切能力。checks 在启动时执行迁移及初始化；aspectj 提供权限校验和日志；migration/service/mapper 处理迁移与迁移记录。

#### 入口与构建

| 文件 | 作用 | 方法、字段或 SQL 线索 |
|---|---|---|
| [pom.xml](C:/Users/86198/Desktop/play/playedu-api/playedu-system/pom.xml) | 该模块的 Maven 构建定义：继承父工程、声明模块依赖与构建插件。 | — |

#### 资源与配置

| 文件 | 作用 | 方法、字段或 SQL 线索 |
|---|---|---|
| [src/main/resources/mapper/MigrationMapper.xml](C:/Users/86198/Desktop/play/playedu-api/playedu-system/src/main/resources/mapper/MigrationMapper.xml) | 数据库迁移记录的数据访问接口；通过 MyBatis-Plus 基础方法、注解 SQL 或 XML 执行数据库操作。 | 绑定 `xyz.playedu.system.mapper.MigrationMapper`；仅结果映射/SQL 片段，无独立增删改查语句 |

#### aspectj

| 文件 | 作用 | 方法、字段或 SQL 线索 |
|---|---|---|
| [aspectj/AdminLogAspect.java](C:/Users/86198/Desktop/play/playedu-api/playedu-system/src/main/java/xyz/playedu/system/aspectj/AdminLogAspect.java) | 拦截带 Log 的方法，收集并保存后台操作日志。 | 方法：logPointCut、doAfterReturning、doAfterThrowing、handleLog、getAnnotationLog、excludeProperties |
| [aspectj/BackendPermissionAspect.java](C:/Users/86198/Desktop/play/playedu-api/playedu-system/src/main/java/xyz/playedu/system/aspectj/BackendPermissionAspect.java) | 拦截带 BackendPermission 的方法，检查当前管理员是否拥有所需权限。 | 方法：doPointcut、doAround |

#### checks

| 文件 | 作用 | 方法、字段或 SQL 线索 |
|---|---|---|
| [checks/AdminPermissionCheck.java](C:/Users/86198/Desktop/play/playedu-api/playedu-system/src/main/java/xyz/playedu/system/checks/AdminPermissionCheck.java) | 启动时初始化、补齐后台权限项。 | 方法：run |
| [checks/AppConfigCheck.java](C:/Users/86198/Desktop/play/playedu-api/playedu-system/src/main/java/xyz/playedu/system/checks/AppConfigCheck.java) | 启动时补齐应用配置项及默认值。 | 方法：run |
| [checks/MigrationCheck.java](C:/Users/86198/Desktop/play/playedu-api/playedu-system/src/main/java/xyz/playedu/system/checks/MigrationCheck.java) | 启动时检查并执行未完成的数据库结构迁移，同时记录 migrations；包含积分等新增业务的建表定义。 | 方法：run |
| [checks/SystemDataCheck.java](C:/Users/86198/Desktop/play/playedu-api/playedu-system/src/main/java/xyz/playedu/system/checks/SystemDataCheck.java) | 启动时初始化或检查系统基础数据。 | 方法：run、adminInit |
| [checks/UpgradeCheck.java](C:/Users/86198/Desktop/play/playedu-api/playedu-system/src/main/java/xyz/playedu/system/checks/UpgradeCheck.java) | 启动时执行版本升级相关检查与处理。 | 方法：run、upgrade_1_4、upgrade_1_beta7、upgrade_1_6 |

#### domain

| 文件 | 作用 | 方法、字段或 SQL 线索 |
|---|---|---|
| [domain/Migration.java](C:/Users/86198/Desktop/play/playedu-api/playedu-system/src/main/java/xyz/playedu/system/domain/Migration.java) | 数据库迁移记录的持久化实体，映射数据库字段。 | 字段：id、migration |

#### mapper

| 文件 | 作用 | 方法、字段或 SQL 线索 |
|---|---|---|
| [mapper/MigrationMapper.java](C:/Users/86198/Desktop/play/playedu-api/playedu-system/src/main/java/xyz/playedu/system/mapper/MigrationMapper.java) | 数据库迁移记录的数据访问接口；通过 MyBatis-Plus 基础方法、注解 SQL 或 XML 执行数据库操作。 | 继承 `BaseMapper<Migration>`，基础 CRUD 来自 MyBatis-Plus |

#### migration

| 文件 | 作用 | 方法、字段或 SQL 线索 |
|---|---|---|
| [migration/UserLearnDurationStatsMigration.java](C:/Users/86198/Desktop/play/playedu-api/playedu-system/src/main/java/xyz/playedu/system/migration/UserLearnDurationStatsMigration.java) | 学习时长统计的专项迁移与历史数据处理。 | 方法：migrate、hasUniqueIndex |

#### service

| 文件 | 作用 | 方法、字段或 SQL 线索 |
|---|---|---|
| [service/MigrationService.java](C:/Users/86198/Desktop/play/playedu-api/playedu-system/src/main/java/xyz/playedu/system/service/MigrationService.java) | 定义数据库迁移记录的业务操作接口，基础 CRUD 可能继承自 IService，具体扩展能力见方法列表。 | 方法：all、store |

#### service/impl

| 文件 | 作用 | 方法、字段或 SQL 线索 |
|---|---|---|
| [service/impl/MigrationServiceImpl.java](C:/Users/86198/Desktop/play/playedu-api/playedu-system/src/main/java/xyz/playedu/system/service/impl/MigrationServiceImpl.java) | 实现数据库迁移记录业务接口；负责该对象的查询、维护及关联处理，具体能力见方法列表。 | 方法：all、store |

#### 测试文件

| 文件 | 作用 | 方法、字段或 SQL 线索 |
|---|---|---|
| [test/aspectj/AdminLogAspectTest.java](C:/Users/86198/Desktop/play/playedu-api/playedu-system/src/test/java/xyz/playedu/system/aspectj/AdminLogAspectTest.java) | 验证 AdminLogAspect 的指定功能及异常分支；具体用例见右侧方法。 | 数据/常量定义；详见源码 |
| [test/checks/PointsSchemaMigrationDefinitionTest.java](C:/Users/86198/Desktop/play/playedu-api/playedu-system/src/test/java/xyz/playedu/system/checks/PointsSchemaMigrationDefinitionTest.java) | 验证 PointsSchemaMigrationDefinition 的指定功能及异常分支；具体用例见右侧方法。 | 方法：sqlFor |
| [test/checks/PointsSchemaMigrationIntegrationTest.java](C:/Users/86198/Desktop/play/playedu-api/playedu-system/src/test/java/xyz/playedu/system/checks/PointsSchemaMigrationIntegrationTest.java) | 验证 PointsSchemaMigration 的集成行为（实际依赖与事务/并发等）；具体用例见右侧方法。 | 方法：applyUnappliedMigrations、pointMigrations、tableNames、indexColumns、columnNames |
| [test/migration/UserLearnDurationStatsMigrationIntegrationTest.java](C:/Users/86198/Desktop/play/playedu-api/playedu-system/src/test/java/xyz/playedu/system/migration/UserLearnDurationStatsMigrationIntegrationTest.java) | 验证 UserLearnDurationStatsMigration 的集成行为（实际依赖与事务/并发等）；具体用例见右侧方法。 | 数据/常量定义；详见源码 |


### playedu-course

课程与学习领域。课程结构、可见范围及附件是一组；学习进度、时长明细及每日统计是一组；有效学习租约、积分奖励持久化与排行榜事件链是另一组。

#### 入口与构建

| 文件 | 作用 | 方法、字段或 SQL 线索 |
|---|---|---|
| [pom.xml](C:/Users/86198/Desktop/play/playedu-api/playedu-course/pom.xml) | 该模块的 Maven 构建定义：继承父工程、声明模块依赖与构建插件。 | — |

#### 资源与配置

| 文件 | 作用 | 方法、字段或 SQL 线索 |
|---|---|---|
| [src/main/resources/mapper/BackendPermission.xml](C:/Users/86198/Desktop/play/playedu-api/playedu-course/src/main/resources/mapper/BackendPermission.xml) | 实际绑定 UserLearnDurationRecordMapper，映射学习时长明细；文件名不表示权限逻辑。 | 绑定 `xyz.playedu.course.mapper.UserLearnDurationRecordMapper`；仅结果映射/SQL 片段，无独立增删改查语句 |
| [src/main/resources/mapper/CourseAttachmentDownloadLogMapper.xml](C:/Users/86198/Desktop/play/playedu-api/playedu-course/src/main/resources/mapper/CourseAttachmentDownloadLogMapper.xml) | 附件下载日志的数据访问接口；通过 MyBatis-Plus 基础方法、注解 SQL 或 XML 执行数据库操作。 | 绑定 `xyz.playedu.course.mapper.CourseAttachmentDownloadLogMapper`；SQL：paginate、paginateCount |
| [src/main/resources/mapper/CourseAttachmentMapper.xml](C:/Users/86198/Desktop/play/playedu-api/playedu-course/src/main/resources/mapper/CourseAttachmentMapper.xml) | 课程附件的数据访问接口；通过 MyBatis-Plus 基础方法、注解 SQL 或 XML 执行数据库操作。 | 绑定 `xyz.playedu.course.mapper.CourseAttachmentMapper`；仅结果映射/SQL 片段，无独立增删改查语句 |
| [src/main/resources/mapper/CourseCategoryMapper.xml](C:/Users/86198/Desktop/play/playedu-api/playedu-course/src/main/resources/mapper/CourseCategoryMapper.xml) | 课程与分类关联的数据访问接口；通过 MyBatis-Plus 基础方法、注解 SQL 或 XML 执行数据库操作。 | 绑定 `xyz.playedu.course.mapper.CourseCategoryMapper`；仅结果映射/SQL 片段，无独立增删改查语句 |
| [src/main/resources/mapper/CourseChapterMapper.xml](C:/Users/86198/Desktop/play/playedu-api/playedu-course/src/main/resources/mapper/CourseChapterMapper.xml) | 课程章节的数据访问接口；通过 MyBatis-Plus 基础方法、注解 SQL 或 XML 执行数据库操作。 | 绑定 `xyz.playedu.course.mapper.CourseChapterMapper`；仅结果映射/SQL 片段，无独立增删改查语句 |
| [src/main/resources/mapper/CourseDepartmentMapper.xml](C:/Users/86198/Desktop/play/playedu-api/playedu-course/src/main/resources/mapper/CourseDepartmentMapper.xml) | MyBatis Mapper 配置：定义字段结果映射、公共 SQL 片段及该文件实际声明的自定义 SQL；以 namespace 为准。 | 绑定 `xyz.playedu.course.mapper.CourseDepartmentUserMapper`；仅结果映射/SQL 片段，无独立增删改查语句 |
| [src/main/resources/mapper/CourseHourMapper.xml](C:/Users/86198/Desktop/play/playedu-api/playedu-course/src/main/resources/mapper/CourseHourMapper.xml) | 课程课时的数据访问接口；通过 MyBatis-Plus 基础方法、注解 SQL 或 XML 执行数据库操作。 | 绑定 `xyz.playedu.course.mapper.CourseHourMapper`；仅结果映射/SQL 片段，无独立增删改查语句 |
| [src/main/resources/mapper/CourseMapper.xml](C:/Users/86198/Desktop/play/playedu-api/playedu-course/src/main/resources/mapper/CourseMapper.xml) | 课程的数据访问接口；通过 MyBatis-Plus 基础方法、注解 SQL 或 XML 执行数据库操作。 | 绑定 `xyz.playedu.course.mapper.CourseMapper`；SQL：paginate、paginateCount、openCoursesAndShow |
| [src/main/resources/mapper/UserCourseHourRecordMapper.xml](C:/Users/86198/Desktop/play/playedu-api/playedu-course/src/main/resources/mapper/UserCourseHourRecordMapper.xml) | 学员课时学习进度的数据访问接口；通过 MyBatis-Plus 基础方法、注解 SQL 或 XML 执行数据库操作。 | 绑定 `xyz.playedu.course.mapper.UserCourseHourRecordMapper`；SQL：getUserLatestRecords、getUserCourseHourCount、paginate、paginateCount、getUserCourseHourUserCount、getUserCourseHourUserFirstCreatedAt、getUserPerCourseEarliestRecord、getCoursePerUserEarliestRecord |
| [src/main/resources/mapper/UserCourseRecordMapper.xml](C:/Users/86198/Desktop/play/playedu-api/playedu-course/src/main/resources/mapper/UserCourseRecordMapper.xml) | 学员课程学习进度的数据访问接口；通过 MyBatis-Plus 基础方法、注解 SQL 或 XML 执行数据库操作。 | 绑定 `xyz.playedu.course.mapper.UserCourseRecordMapper`；SQL：paginateTotal、paginate |
| [src/main/resources/mapper/UserLearnDurationStatsMapper.xml](C:/Users/86198/Desktop/play/playedu-api/playedu-course/src/main/resources/mapper/UserLearnDurationStatsMapper.xml) | 按日期汇总的学习时长的数据访问接口；通过 MyBatis-Plus 基础方法、注解 SQL 或 XML 执行数据库操作。 | 绑定 `xyz.playedu.course.mapper.UserLearnDurationStatsMapper`；SQL：getUserDuration、totalByDate、rankingByDate、durationByUserAndDate、increment |

#### bus

| 文件 | 作用 | 方法、字段或 SQL 线索 |
|---|---|---|
| [bus/UserBus.java](C:/Users/86198/Desktop/play/playedu-api/playedu-course/src/main/java/xyz/playedu/course/bus/UserBus.java) | 聚合课程与学员关系，判断学员能否查看课程。 | 方法：canSeeCourse |

#### domain

| 文件 | 作用 | 方法、字段或 SQL 线索 |
|---|---|---|
| [domain/Course.java](C:/Users/86198/Desktop/play/playedu-api/playedu-course/src/main/java/xyz/playedu/course/domain/Course.java) | 课程的持久化实体，映射数据库字段。 | 字段：id、title、thumb、charge、shortDesc、isRequired、classHour、isShow、createdAt、sortAt、extra、adminId |
| [domain/CourseAttachment.java](C:/Users/86198/Desktop/play/playedu-api/playedu-course/src/main/java/xyz/playedu/course/domain/CourseAttachment.java) | 课程附件的持久化实体，映射数据库字段。 | 字段：id、courseId、sort、title、type、rid、url、ext |
| [domain/CourseAttachmentDownloadLog.java](C:/Users/86198/Desktop/play/playedu-api/playedu-course/src/main/java/xyz/playedu/course/domain/CourseAttachmentDownloadLog.java) | 附件下载日志的持久化实体，映射数据库字段。 | 字段：id、userId、courseId、title、courserAttachmentId、rid、ip、createdAt |
| [domain/CourseCategory.java](C:/Users/86198/Desktop/play/playedu-api/playedu-course/src/main/java/xyz/playedu/course/domain/CourseCategory.java) | 课程与分类关联的持久化实体，映射数据库字段。 | 字段：courseId、categoryId |
| [domain/CourseChapter.java](C:/Users/86198/Desktop/play/playedu-api/playedu-course/src/main/java/xyz/playedu/course/domain/CourseChapter.java) | 课程章节的持久化实体，映射数据库字段。 | 字段：id、courseId、name、sort、createdAt、updatedAt |
| [domain/CourseDepartmentUser.java](C:/Users/86198/Desktop/play/playedu-api/playedu-course/src/main/java/xyz/playedu/course/domain/CourseDepartmentUser.java) | 课程与部门、学员可见范围关联的持久化实体，映射数据库字段。 | 字段：courseId、rangeId、type |
| [domain/CourseHour.java](C:/Users/86198/Desktop/play/playedu-api/playedu-course/src/main/java/xyz/playedu/course/domain/CourseHour.java) | 课程课时的持久化实体，映射数据库字段。 | 字段：id、courseId、chapterId、sort、title、type、rid、duration、deleted |
| [domain/DailyLearningRankingEntry.java](C:/Users/86198/Desktop/play/playedu-api/playedu-course/src/main/java/xyz/playedu/course/domain/DailyLearningRankingEntry.java) | 每日学习排行榜条目的数据对象，承载信息供业务或接口使用。 | 字段：userId、duration、createdDate |
| [domain/UserCourseHourRecord.java](C:/Users/86198/Desktop/play/playedu-api/playedu-course/src/main/java/xyz/playedu/course/domain/UserCourseHourRecord.java) | 学员课时学习进度的持久化实体，映射数据库字段。 | 字段：id、userId、courseId、hourId、totalDuration、finishedDuration、realDuration、isFinished、finishedAt、createdAt、updatedAt |
| [domain/UserCourseRecord.java](C:/Users/86198/Desktop/play/playedu-api/playedu-course/src/main/java/xyz/playedu/course/domain/UserCourseRecord.java) | 学员课程学习进度的持久化实体，映射数据库字段。 | 字段：id、userId、courseId、hourCount、finishedCount、progress、isFinished、finishedAt、createdAt、updatedAt |
| [domain/UserLatestLearn.java](C:/Users/86198/Desktop/play/playedu-api/playedu-course/src/main/java/xyz/playedu/course/domain/UserLatestLearn.java) | 最近学习信息的数据对象，承载信息供业务或接口使用。 | 字段：course、userCourseRecord、lastLearnHour、hourRecord |
| [domain/UserLearnDurationRecord.java](C:/Users/86198/Desktop/play/playedu-api/playedu-course/src/main/java/xyz/playedu/course/domain/UserLearnDurationRecord.java) | 学习时长明细的持久化实体，映射数据库字段。 | 字段：id、userId、createdDate、duration、startAt、endAt、fromId、fromScene |
| [domain/UserLearnDurationStats.java](C:/Users/86198/Desktop/play/playedu-api/playedu-course/src/main/java/xyz/playedu/course/domain/UserLearnDurationStats.java) | 按日期汇总的学习时长的持久化实体，映射数据库字段。 | 字段：id、userId、duration、createdDate |

#### event

| 文件 | 作用 | 方法、字段或 SQL 线索 |
|---|---|---|
| [event/DailyLearningDurationConfirmedEvent.java](C:/Users/86198/Desktop/play/playedu-api/playedu-course/src/main/java/xyz/playedu/course/event/DailyLearningDurationConfirmedEvent.java) | 有效学习时长确认事件，携带排行榜更新所需的数据。 | 数据/常量定义；详见源码 |
| [event/DailyLearningDurationEventPublisher.java](C:/Users/86198/Desktop/play/playedu-api/playedu-course/src/main/java/xyz/playedu/course/event/DailyLearningDurationEventPublisher.java) | 在数据库事务提交后发布时长确认事件，避免回滚事务更新排行榜。 | 方法：publishAfterCommit、afterCommit |
| [event/DailyLearningDurationIncrement.java](C:/Users/86198/Desktop/play/playedu-api/playedu-course/src/main/java/xyz/playedu/course/event/DailyLearningDurationIncrement.java) | 单个学员在某一天新增学习时长的数据对象。 | 方法：DailyLearningDurationIncrement |
| [event/DailyLearningRankingEventListener.java](C:/Users/86198/Desktop/play/playedu-api/playedu-course/src/main/java/xyz/playedu/course/event/DailyLearningRankingEventListener.java) | 接收时长确认事件，异步更新每日学习排行榜。 | 方法：project |
| [event/DailyLearningRankingStartupRebuilder.java](C:/Users/86198/Desktop/play/playedu-api/playedu-course/src/main/java/xyz/playedu/course/event/DailyLearningRankingStartupRebuilder.java) | 应用启动时重建或恢复每日学习排行榜。 | 方法：run |

#### mapper

| 文件 | 作用 | 方法、字段或 SQL 线索 |
|---|---|---|
| [mapper/CourseAttachmentDownloadLogMapper.java](C:/Users/86198/Desktop/play/playedu-api/playedu-course/src/main/java/xyz/playedu/course/mapper/CourseAttachmentDownloadLogMapper.java) | 附件下载日志的数据访问接口；通过 MyBatis-Plus 基础方法、注解 SQL 或 XML 执行数据库操作。 | 方法：paginate、paginateCount |
| [mapper/CourseAttachmentMapper.java](C:/Users/86198/Desktop/play/playedu-api/playedu-course/src/main/java/xyz/playedu/course/mapper/CourseAttachmentMapper.java) | 课程附件的数据访问接口；通过 MyBatis-Plus 基础方法、注解 SQL 或 XML 执行数据库操作。 | 继承 `BaseMapper<CourseAttachment>`，基础 CRUD 来自 MyBatis-Plus |
| [mapper/CourseCategoryMapper.java](C:/Users/86198/Desktop/play/playedu-api/playedu-course/src/main/java/xyz/playedu/course/mapper/CourseCategoryMapper.java) | 课程与分类关联的数据访问接口；通过 MyBatis-Plus 基础方法、注解 SQL 或 XML 执行数据库操作。 | 继承 `BaseMapper<CourseCategory>`，基础 CRUD 来自 MyBatis-Plus |
| [mapper/CourseChapterMapper.java](C:/Users/86198/Desktop/play/playedu-api/playedu-course/src/main/java/xyz/playedu/course/mapper/CourseChapterMapper.java) | 课程章节的数据访问接口；通过 MyBatis-Plus 基础方法、注解 SQL 或 XML 执行数据库操作。 | 继承 `BaseMapper<CourseChapter>`，基础 CRUD 来自 MyBatis-Plus |
| [mapper/CourseDepartmentUserMapper.java](C:/Users/86198/Desktop/play/playedu-api/playedu-course/src/main/java/xyz/playedu/course/mapper/CourseDepartmentUserMapper.java) | 课程与部门、学员可见范围关联的数据访问接口；通过 MyBatis-Plus 基础方法、注解 SQL 或 XML 执行数据库操作。 | 继承 `BaseMapper<CourseDepartmentUser>`，基础 CRUD 来自 MyBatis-Plus |
| [mapper/CourseHourMapper.java](C:/Users/86198/Desktop/play/playedu-api/playedu-course/src/main/java/xyz/playedu/course/mapper/CourseHourMapper.java) | 课程课时的数据访问接口；通过 MyBatis-Plus 基础方法、注解 SQL 或 XML 执行数据库操作。 | 继承 `BaseMapper<CourseHour>`，基础 CRUD 来自 MyBatis-Plus |
| [mapper/CourseMapper.java](C:/Users/86198/Desktop/play/playedu-api/playedu-course/src/main/java/xyz/playedu/course/mapper/CourseMapper.java) | 课程的数据访问接口；通过 MyBatis-Plus 基础方法、注解 SQL 或 XML 执行数据库操作。 | 方法：paginate、paginateCount、openCoursesAndShow |
| [mapper/UserCourseHourRecordMapper.java](C:/Users/86198/Desktop/play/playedu-api/playedu-course/src/main/java/xyz/playedu/course/mapper/UserCourseHourRecordMapper.java) | 学员课时学习进度的数据访问接口；通过 MyBatis-Plus 基础方法、注解 SQL 或 XML 执行数据库操作。 | 方法：getUserLatestRecords、getUserCourseHourCount、getUserCourseHourUserCount、getUserCourseHourUserFirstCreatedAt、paginate、paginateCount、getUserPerCourseEarliestRecord、getCoursePerUserEarliestRecord |
| [mapper/UserCourseRecordMapper.java](C:/Users/86198/Desktop/play/playedu-api/playedu-course/src/main/java/xyz/playedu/course/mapper/UserCourseRecordMapper.java) | 学员课程学习进度的数据访问接口；通过 MyBatis-Plus 基础方法、注解 SQL 或 XML 执行数据库操作。 | 方法：paginate、paginateTotal |
| [mapper/UserLearnDurationRecordMapper.java](C:/Users/86198/Desktop/play/playedu-api/playedu-course/src/main/java/xyz/playedu/course/mapper/UserLearnDurationRecordMapper.java) | 学习时长明细的数据访问接口；通过 MyBatis-Plus 基础方法、注解 SQL 或 XML 执行数据库操作。 | 继承 `BaseMapper<UserLearnDurationRecord>`，基础 CRUD 来自 MyBatis-Plus |
| [mapper/UserLearnDurationStatsMapper.java](C:/Users/86198/Desktop/play/playedu-api/playedu-course/src/main/java/xyz/playedu/course/mapper/UserLearnDurationStatsMapper.java) | 按日期汇总的学习时长的数据访问接口；通过 MyBatis-Plus 基础方法、注解 SQL 或 XML 执行数据库操作。 | 方法：getUserDuration、totalByDate、rankingByDate、durationByUserAndDate、increment |

#### service

| 文件 | 作用 | 方法、字段或 SQL 线索 |
|---|---|---|
| [service/ActiveLearningLeaseService.java](C:/Users/86198/Desktop/play/playedu-api/playedu-course/src/main/java/xyz/playedu/course/service/ActiveLearningLeaseService.java) | 用 Redis Lua 协调一个学员唯一的有效学习课时与会话，校验心跳间隔、累计有效时长并调用学习事实持久化。 | 方法：heartbeat、stop、execute、leaseKey、HeartbeatResult、withEarnedPoints、from |
| [service/CourseAttachmentDownloadLogService.java](C:/Users/86198/Desktop/play/playedu-api/playedu-course/src/main/java/xyz/playedu/course/service/CourseAttachmentDownloadLogService.java) | 定义附件下载日志的业务操作接口，基础 CRUD 可能继承自 IService，具体扩展能力见方法列表。 | 方法：paginate |
| [service/CourseAttachmentService.java](C:/Users/86198/Desktop/play/playedu-api/playedu-course/src/main/java/xyz/playedu/course/service/CourseAttachmentService.java) | 定义课程附件的业务操作接口，基础 CRUD 可能继承自 IService，具体扩展能力见方法列表。 | 方法：findOrFail、update、getAttachmentsByCourseId、create、getCountByCourseId、remove、updateSort、getRidsByCourseId、chunk |
| [service/CourseCategoryService.java](C:/Users/86198/Desktop/play/playedu-api/playedu-course/src/main/java/xyz/playedu/course/service/CourseCategoryService.java) | 定义课程与分类关联的业务操作接口，基础 CRUD 可能继承自 IService，具体扩展能力见方法列表。 | 方法：getCourseIdsByCategoryIds、removeByCourseId、removeByCategoryId、getCategoryIdsByCourseId、getCourseIdsByCategoryId |
| [service/CourseChapterService.java](C:/Users/86198/Desktop/play/playedu-api/playedu-course/src/main/java/xyz/playedu/course/service/CourseChapterService.java) | 定义课程章节的业务操作接口，基础 CRUD 可能继承自 IService，具体扩展能力见方法列表。 | 方法：getChaptersByCourseId、create、update、findOrFail、updateSort |
| [service/CourseCompletionTransition.java](C:/Users/86198/Desktop/play/playedu-api/playedu-course/src/main/java/xyz/playedu/course/service/CourseCompletionTransition.java) | 表示课程完成状态变化，提供是否首次完成的判断。 | 方法：isFirstCompletion |
| [service/CourseDepartmentUserService.java](C:/Users/86198/Desktop/play/playedu-api/playedu-course/src/main/java/xyz/playedu/course/service/CourseDepartmentUserService.java) | 定义课程与部门、学员可见范围关联的业务操作接口，基础 CRUD 可能继承自 IService，具体扩展能力见方法列表。 | 方法：getCourseIdsByDepIds、getDepIdsByCourseId、removeByCourseId、getCourseIdsByDepId |
| [service/CourseHourService.java](C:/Users/86198/Desktop/play/playedu-api/playedu-course/src/main/java/xyz/playedu/course/service/CourseHourService.java) | 定义课程课时的业务操作接口，基础 CRUD 可能继承自 IService，具体扩展能力见方法列表。 | 方法：findOrFail、update、getHoursByCourseId、create、getCountByCourseId、getCountByChapterId、remove、updateSort、getRidsByCourseId、chunk |
| [service/CourseService.java](C:/Users/86198/Desktop/play/playedu-api/playedu-course/src/main/java/xyz/playedu/course/service/CourseService.java) | 定义课程的业务操作接口，基础 CRUD 可能继承自 IService，具体扩展能力见方法列表。 | 方法：paginate、createWithCategoryIdsAndDepIds、updateWithCategoryIdsAndDepIds、relateDepartments、resetRelateDepartments、relateCategories、resetRelateCategories、findOrFail、getDepIdsByCourseId、getCategoryIdsByCourseId、updateClassHour、removeCategoryIdRelate等 |
| [service/LearningFactPersistenceResult.java](C:/Users/86198/Desktop/play/playedu-api/playedu-course/src/main/java/xyz/playedu/course/service/LearningFactPersistenceResult.java) | 学习事实持久化返回对象，携带本次获得的积分。 | 方法：LearningFactPersistenceResult |
| [service/LearningFactPersistenceService.java](C:/Users/86198/Desktop/play/playedu-api/playedu-course/src/main/java/xyz/playedu/course/service/LearningFactPersistenceService.java) | 在事务中持久化有效学习时长与进度，处理课程首次完成积分奖励，并发布每日时长确认信息。 | 方法：record、recordIncrement、persistLearningFacts、awardCourseCompletion |
| [service/UserCourseHourRecordService.java](C:/Users/86198/Desktop/play/playedu-api/playedu-course/src/main/java/xyz/playedu/course/service/UserCourseHourRecordService.java) | 定义学员课时学习进度的业务操作接口，基础 CRUD 可能继承自 IService，具体扩展能力见方法列表。 | 方法：find、storeOrUpdate、getFinishedHourCount、getRecords、getLatestCourseIds、removeByCourseId、remove、getUserCourseHourCount、getUserCourseHourUserCount、getUserCourseHourUserFirstCreatedAt、paginate、getUserPerCourseEarliestRecord等 |
| [service/UserCourseRecordService.java](C:/Users/86198/Desktop/play/playedu-api/playedu-course/src/main/java/xyz/playedu/course/service/UserCourseRecordService.java) | 定义学员课程学习进度的业务操作接口，基础 CRUD 可能继承自 IService，具体扩展能力见方法列表。 | 方法：find、storeOrUpdate、chunk、paginate、destroy、removeByCourseId、chunks、updateUserCourseLearnProgress |
| [service/UserLearnDurationRecordService.java](C:/Users/86198/Desktop/play/playedu-api/playedu-course/src/main/java/xyz/playedu/course/service/UserLearnDurationRecordService.java) | 定义学习时长明细的业务操作接口，基础 CRUD 可能继承自 IService，具体扩展能力见方法列表。 | 方法：store、remove |
| [service/UserLearnDurationStatsService.java](C:/Users/86198/Desktop/play/playedu-api/playedu-course/src/main/java/xyz/playedu/course/service/UserLearnDurationStatsService.java) | 定义按日期汇总的学习时长的业务操作接口，基础 CRUD 可能继承自 IService，具体扩展能力见方法列表。 | 方法：storeOrUpdate、todayTotal、yesterdayTotal、top10、todayUserDuration、userDuration、dateBetween、remove |

#### service/impl

| 文件 | 作用 | 方法、字段或 SQL 线索 |
|---|---|---|
| [service/impl/CourseAttachmentDownloadLogServiceImpl.java](C:/Users/86198/Desktop/play/playedu-api/playedu-course/src/main/java/xyz/playedu/course/service/impl/CourseAttachmentDownloadLogServiceImpl.java) | 实现附件下载日志业务接口；负责该对象的查询、维护及关联处理，具体能力见方法列表。 | 方法：paginate |
| [service/impl/CourseAttachmentServiceImpl.java](C:/Users/86198/Desktop/play/playedu-api/playedu-course/src/main/java/xyz/playedu/course/service/impl/CourseAttachmentServiceImpl.java) | 实现课程附件业务接口；负责该对象的查询、维护及关联处理，具体能力见方法列表。 | 方法：findOrFail、update、getAttachmentsByCourseId、create、getCountByCourseId、remove、updateSort、getRidsByCourseId、chunk |
| [service/impl/CourseCategoryServiceImpl.java](C:/Users/86198/Desktop/play/playedu-api/playedu-course/src/main/java/xyz/playedu/course/service/impl/CourseCategoryServiceImpl.java) | 实现课程与分类关联业务接口；负责该对象的查询、维护及关联处理，具体能力见方法列表。 | 方法：getCourseIdsByCategoryIds、removeByCourseId、removeByCategoryId、getCategoryIdsByCourseId、getCourseIdsByCategoryId |
| [service/impl/CourseChapterServiceImpl.java](C:/Users/86198/Desktop/play/playedu-api/playedu-course/src/main/java/xyz/playedu/course/service/impl/CourseChapterServiceImpl.java) | 实现课程章节业务接口；负责该对象的查询、维护及关联处理，具体能力见方法列表。 | 方法：create、update、findOrFail、getChaptersByCourseId、updateSort |
| [service/impl/CourseDepartmentUserServiceImpl.java](C:/Users/86198/Desktop/play/playedu-api/playedu-course/src/main/java/xyz/playedu/course/service/impl/CourseDepartmentUserServiceImpl.java) | 实现课程与部门、学员可见范围关联业务接口；负责该对象的查询、维护及关联处理，具体能力见方法列表。 | 方法：getCourseIdsByDepIds、getDepIdsByCourseId、removeByCourseId、getCourseIdsByDepId |
| [service/impl/CourseHourServiceImpl.java](C:/Users/86198/Desktop/play/playedu-api/playedu-course/src/main/java/xyz/playedu/course/service/impl/CourseHourServiceImpl.java) | 实现课程课时业务接口；负责该对象的查询、维护及关联处理，具体能力见方法列表。 | 方法：findOrFail、update、getHoursByCourseId、create、getCountByCourseId、getCountByChapterId、remove、updateSort、getRidsByCourseId、chunk |
| [service/impl/CourseServiceImpl.java](C:/Users/86198/Desktop/play/playedu-api/playedu-course/src/main/java/xyz/playedu/course/service/impl/CourseServiceImpl.java) | 实现课程业务接口；负责该对象的查询、维护及关联处理，具体能力见方法列表。 | 方法：paginate、createWithCategoryIdsAndDepIds、relateDepartments、resetRelateDepartments、relateCategories、resetRelateCategories、updateWithCategoryIdsAndDepIds、findOrFail、getDepIdsByCourseId、getCategoryIdsByCourseId、updateClassHour、removeCategoryIdRelate等 |
| [service/impl/DailyLearningRankingService.java](C:/Users/86198/Desktop/play/playedu-api/playedu-course/src/main/java/xyz/playedu/course/service/impl/DailyLearningRankingService.java) | 维护每日学习排行榜 Redis 投影，提供前十名、增量处理、数据库重建和用户移除等能力。 | 方法：todayTop10、yesterdayTop10、project、rebuildTodayAndYesterday、rebuildMissing、top10For、rebuildIfMissing、removeUser、projectionReady、removeUserFromDate、rebuildForDate、replaceFromAuthority等 |
| [service/impl/UserCourseHourRecordServiceImpl.java](C:/Users/86198/Desktop/play/playedu-api/playedu-course/src/main/java/xyz/playedu/course/service/impl/UserCourseHourRecordServiceImpl.java) | 实现学员课时学习进度业务接口；负责该对象的查询、维护及关联处理，具体能力见方法列表。 | 方法：find、storeOrUpdate、getFinishedHourCount、getRecords、getLatestCourseIds、removeByCourseId、getUserCourseHourCount、getUserCourseHourUserCount、remove、paginate、getUserCourseHourUserFirstCreatedAt、getUserPerCourseEarliestRecord等 |
| [service/impl/UserCourseRecordServiceImpl.java](C:/Users/86198/Desktop/play/playedu-api/playedu-course/src/main/java/xyz/playedu/course/service/impl/UserCourseRecordServiceImpl.java) | 实现学员课程学习进度业务接口；负责该对象的查询、维护及关联处理，具体能力见方法列表。 | 方法：find、storeOrUpdate、chunk、paginate、destroy、removeByCourseId、chunks、updateUserCourseLearnProgress |
| [service/impl/UserLearnDurationRecordServiceImpl.java](C:/Users/86198/Desktop/play/playedu-api/playedu-course/src/main/java/xyz/playedu/course/service/impl/UserLearnDurationRecordServiceImpl.java) | 实现学习时长明细业务接口；负责该对象的查询、维护及关联处理，具体能力见方法列表。 | 方法：store、remove |
| [service/impl/UserLearnDurationStatsServiceImpl.java](C:/Users/86198/Desktop/play/playedu-api/playedu-course/src/main/java/xyz/playedu/course/service/impl/UserLearnDurationStatsServiceImpl.java) | 实现按日期汇总的学习时长业务接口；负责该对象的查询、维护及关联处理，具体能力见方法列表。 | 方法：storeOrUpdate、todayTotal、yesterdayTotal、top10、todayUserDuration、userDuration、dateBetween、remove |

#### 测试文件

| 文件 | 作用 | 方法、字段或 SQL 线索 |
|---|---|---|
| [test/event/DailyLearningRankingAsyncEventListenerTest.java](C:/Users/86198/Desktop/play/playedu-api/playedu-course/src/test/java/xyz/playedu/course/event/DailyLearningRankingAsyncEventListenerTest.java) | 验证 DailyLearningRankingAsyncEventListener 的指定功能及异常分支；具体用例见右侧方法。 | 方法：doGetTransaction、doBegin、doCommit、doRollback |
| [test/event/DailyLearningRankingEventListenerTest.java](C:/Users/86198/Desktop/play/playedu-api/playedu-course/src/test/java/xyz/playedu/course/event/DailyLearningRankingEventListenerTest.java) | 验证 DailyLearningRankingEventListener 的指定功能及异常分支；具体用例见右侧方法。 | 数据/常量定义；详见源码 |
| [test/service/ActiveLearningLeaseServiceIntegrationTest.java](C:/Users/86198/Desktop/play/playedu-api/playedu-course/src/test/java/xyz/playedu/course/service/ActiveLearningLeaseServiceIntegrationTest.java) | 验证 ActiveLearningLeaseService 的集成行为（实际依赖与事务/并发等）；具体用例见右侧方法。 | 方法：getZone、withZone、instant |
| [test/service/DailyLearningRankingServiceIntegrationTest.java](C:/Users/86198/Desktop/play/playedu-api/playedu-course/src/test/java/xyz/playedu/course/service/DailyLearningRankingServiceIntegrationTest.java) | 验证 DailyLearningRankingService 的集成行为（实际依赖与事务/并发等）；具体用例见右侧方法。 | 方法：insertAuthoritativeDuration |
| [test/service/LearningFactPersistenceServiceIntegrationTest.java](C:/Users/86198/Desktop/play/playedu-api/playedu-course/src/test/java/xyz/playedu/course/service/LearningFactPersistenceServiceIntegrationTest.java) | 验证 LearningFactPersistenceService 的集成行为（实际依赖与事务/并发等）；具体用例见右侧方法。 | 方法：tryAcquireLearningLockFromAnotherThread、queryCredit1、pointLedgerCount、getResult、collect |
| [test/service/UserLearnDurationStatsServiceIntegrationTest.java](C:/Users/86198/Desktop/play/playedu-api/playedu-course/src/test/java/xyz/playedu/course/service/UserLearnDurationStatsServiceIntegrationTest.java) | 验证 UserLearnDurationStatsService 的集成行为（实际依赖与事务/并发等）；具体用例见右侧方法。 | 数据/常量定义；详见源码 |


### playedu-resource

资源管理和上传领域。Resource 表示文件，ResourceExtra 表示封面/时长等扩展信息，ResourceCategory 表示文件与分类的关联。实际对象存储操作复用 common/S3Util。

#### 入口与构建

| 文件 | 作用 | 方法、字段或 SQL 线索 |
|---|---|---|
| [pom.xml](C:/Users/86198/Desktop/play/playedu-api/playedu-resource/pom.xml) | 该模块的 Maven 构建定义：继承父工程、声明模块依赖与构建插件。 | — |

#### 资源与配置

| 文件 | 作用 | 方法、字段或 SQL 线索 |
|---|---|---|
| [src/main/resources/mapper/ResourceCategoryMapper.xml](C:/Users/86198/Desktop/play/playedu-api/playedu-resource/src/main/resources/mapper/ResourceCategoryMapper.xml) | 资源与分类关联的数据访问接口；通过 MyBatis-Plus 基础方法、注解 SQL 或 XML 执行数据库操作。 | 绑定 `xyz.playedu.resource.mapper.ResourceCategoryMapper`；仅结果映射/SQL 片段，无独立增删改查语句 |
| [src/main/resources/mapper/ResourceMapper.xml](C:/Users/86198/Desktop/play/playedu-api/playedu-resource/src/main/resources/mapper/ResourceMapper.xml) | 资源文件的数据访问接口；通过 MyBatis-Plus 基础方法、注解 SQL 或 XML 执行数据库操作。 | 绑定 `xyz.playedu.resource.mapper.ResourceMapper`；SQL：paginate、paginateCount、paginateType |
| [src/main/resources/mapper/ResourceVideoMapper.xml](C:/Users/86198/Desktop/play/playedu-api/playedu-resource/src/main/resources/mapper/ResourceVideoMapper.xml) | 实际绑定 ResourceExtraMapper，映射资源封面、时长等扩展信息。 | 绑定 `xyz.playedu.resource.mapper.ResourceExtraMapper`；仅结果映射/SQL 片段，无独立增删改查语句 |

#### domain

| 文件 | 作用 | 方法、字段或 SQL 线索 |
|---|---|---|
| [domain/Resource.java](C:/Users/86198/Desktop/play/playedu-api/playedu-resource/src/main/java/xyz/playedu/resource/domain/Resource.java) | 资源文件的持久化实体，映射数据库字段。 | 字段：id、adminId、type、name、extension、size、disk、path、createdAt、parentId |
| [domain/ResourceCategory.java](C:/Users/86198/Desktop/play/playedu-api/playedu-resource/src/main/java/xyz/playedu/resource/domain/ResourceCategory.java) | 资源与分类关联的持久化实体，映射数据库字段。 | 字段：cid、rid |
| [domain/ResourceExtra.java](C:/Users/86198/Desktop/play/playedu-api/playedu-resource/src/main/java/xyz/playedu/resource/domain/ResourceExtra.java) | 资源扩展信息（封面、时长等）的持久化实体，映射数据库字段。 | 字段：id、rid、poster、duration、createdAt |

#### mapper

| 文件 | 作用 | 方法、字段或 SQL 线索 |
|---|---|---|
| [mapper/ResourceCategoryMapper.java](C:/Users/86198/Desktop/play/playedu-api/playedu-resource/src/main/java/xyz/playedu/resource/mapper/ResourceCategoryMapper.java) | 资源与分类关联的数据访问接口；通过 MyBatis-Plus 基础方法、注解 SQL 或 XML 执行数据库操作。 | 继承 `BaseMapper<ResourceCategory>`，基础 CRUD 来自 MyBatis-Plus |
| [mapper/ResourceExtraMapper.java](C:/Users/86198/Desktop/play/playedu-api/playedu-resource/src/main/java/xyz/playedu/resource/mapper/ResourceExtraMapper.java) | 资源扩展信息（封面、时长等）的数据访问接口；通过 MyBatis-Plus 基础方法、注解 SQL 或 XML 执行数据库操作。 | 继承 `BaseMapper<ResourceExtra>`，基础 CRUD 来自 MyBatis-Plus |
| [mapper/ResourceMapper.java](C:/Users/86198/Desktop/play/playedu-api/playedu-resource/src/main/java/xyz/playedu/resource/mapper/ResourceMapper.java) | 资源文件的数据访问接口；通过 MyBatis-Plus 基础方法、注解 SQL 或 XML 执行数据库操作。 | 方法：paginate、paginateCount、paginateType |

#### service

| 文件 | 作用 | 方法、字段或 SQL 线索 |
|---|---|---|
| [service/ResourceCategoryService.java](C:/Users/86198/Desktop/play/playedu-api/playedu-resource/src/main/java/xyz/playedu/resource/service/ResourceCategoryService.java) | 定义资源与分类关联的业务操作接口，基础 CRUD 可能继承自 IService，具体扩展能力见方法列表。 | 方法：rebuild、getRidsByCategoryId |
| [service/ResourceExtraService.java](C:/Users/86198/Desktop/play/playedu-api/playedu-resource/src/main/java/xyz/playedu/resource/service/ResourceExtraService.java) | 定义资源扩展信息（封面、时长等）的业务操作接口，基础 CRUD 可能继承自 IService，具体扩展能力见方法列表。 | 方法：create、removeByRid、chunksByRids |
| [service/ResourceService.java](C:/Users/86198/Desktop/play/playedu-api/playedu-resource/src/main/java/xyz/playedu/resource/service/ResourceService.java) | 定义资源文件的业务操作接口，基础 CRUD 可能继承自 IService，具体扩展能力见方法列表。 | 方法：paginate、paginateType、create、update、findOrFail、chunks、total、duration、updateNameAndCategoryId、categoryIds、chunksPreSignUrlByIds、downloadResById |
| [service/UploadService.java](C:/Users/86198/Desktop/play/playedu-api/playedu-resource/src/main/java/xyz/playedu/resource/service/UploadService.java) | 文件上传业务接口。 | 方法：upload、storeBase64Image |

#### service/impl

| 文件 | 作用 | 方法、字段或 SQL 线索 |
|---|---|---|
| [service/impl/ResourceCategoryServiceImpl.java](C:/Users/86198/Desktop/play/playedu-api/playedu-resource/src/main/java/xyz/playedu/resource/service/impl/ResourceCategoryServiceImpl.java) | 实现资源与分类关联业务接口；负责该对象的查询、维护及关联处理，具体能力见方法列表。 | 方法：rebuild、getRidsByCategoryId |
| [service/impl/ResourceExtraServiceImpl.java](C:/Users/86198/Desktop/play/playedu-api/playedu-resource/src/main/java/xyz/playedu/resource/service/impl/ResourceExtraServiceImpl.java) | 实现资源扩展信息（封面、时长等）业务接口；负责该对象的查询、维护及关联处理，具体能力见方法列表。 | 方法：create、removeByRid、chunksByRids |
| [service/impl/ResourceServiceImpl.java](C:/Users/86198/Desktop/play/playedu-api/playedu-resource/src/main/java/xyz/playedu/resource/service/impl/ResourceServiceImpl.java) | 实现资源文件业务接口；负责该对象的查询、维护及关联处理，具体能力见方法列表。 | 方法：paginate、paginateType、create、update、findOrFail、chunks、total、duration、updateNameAndCategoryId、categoryIds、chunksPreSignUrlByIds、downloadResById |
| [service/impl/UploadServiceImpl.java](C:/Users/86198/Desktop/play/playedu-api/playedu-resource/src/main/java/xyz/playedu/resource/service/impl/UploadServiceImpl.java) | 处理业务层文件上传并调用存储工具。 | 方法：upload、storeBase64Image |


### playedu-points

积分闭环。余额存于 users.credit1，流水记录变动；商品关联兑换码库存，兑换服务协调扣积分与交付；crypto 负责兑换码保密。当前工作区没有 migration 目录中的 Java 源文件。

#### 入口与构建

| 文件 | 作用 | 方法、字段或 SQL 线索 |
|---|---|---|
| [pom.xml](C:/Users/86198/Desktop/play/playedu-api/playedu-points/pom.xml) | 该模块的 Maven 构建定义：继承父工程、声明模块依赖与构建插件。 | — |

#### 资源与配置

| 文件 | 作用 | 方法、字段或 SQL 线索 |
|---|---|---|
| [src/main/resources/mapper/PointCodeMapper.xml](C:/Users/86198/Desktop/play/playedu-api/playedu-points/src/main/resources/mapper/PointCodeMapper.xml) | 动态分页及计数查询、共享结果映射；固定注解查询复用此映射。 | namespace：xyz.playedu.points.mapper.PointCodeMapper；SQL：paginate、paginateCount |
| [src/main/resources/mapper/PointLedgerMapper.xml](C:/Users/86198/Desktop/play/playedu-api/playedu-points/src/main/resources/mapper/PointLedgerMapper.xml) | 动态分页及计数查询、共享结果映射；固定注解查询复用此映射。 | namespace：xyz.playedu.points.mapper.PointLedgerMapper；SQL：paginate、paginateCount |
| [src/main/resources/mapper/PointProductMapper.xml](C:/Users/86198/Desktop/play/playedu-api/playedu-points/src/main/resources/mapper/PointProductMapper.xml) | 动态分页及计数查询、共享结果映射；固定注解查询复用此映射。 | namespace：xyz.playedu.points.mapper.PointProductMapper；SQL：paginate、paginateCount |
| [src/main/resources/mapper/PointRedemptionMapper.xml](C:/Users/86198/Desktop/play/playedu-api/playedu-points/src/main/resources/mapper/PointRedemptionMapper.xml) | 动态分页及计数查询、共享结果映射；固定注解查询复用此映射。 | namespace：xyz.playedu.points.mapper.PointRedemptionMapper；SQL：paginate、paginateCount |

#### crypto

| 文件 | 作用 | 方法、字段或 SQL 线索 |
|---|---|---|
| [crypto/PointCodeCryptoService.java](C:/Users/86198/Desktop/play/playedu-api/playedu-points/src/main/java/xyz/playedu/points/crypto/PointCodeCryptoService.java) | 规范化兑换码，使用 AES-GCM 加解密、HMAC-SHA256 生成指纹，避免以明文保存兑换码。 | 方法：normalize、encrypt、decrypt、digest、requireConfiguredKey、configuredKeyBytes、deriveDigestKey、sha256 |

#### domain

| 文件 | 作用 | 方法、字段或 SQL 线索 |
|---|---|---|
| [domain/PointCode.java](C:/Users/86198/Desktop/play/playedu-api/playedu-points/src/main/java/xyz/playedu/points/domain/PointCode.java) | 商品兑换码的持久化实体，映射数据库字段。 | 字段：id、productId、codeCiphertext、codeDigest、status、deliveredAt、createdAt、updatedAt |
| [domain/PointCodeStatus.java](C:/Users/86198/Desktop/play/playedu-api/playedu-points/src/main/java/xyz/playedu/points/domain/PointCodeStatus.java) | 兑换码库存状态枚举/状态定义。 | 数据/常量定义；详见源码 |
| [domain/PointLedger.java](C:/Users/86198/Desktop/play/playedu-api/playedu-points/src/main/java/xyz/playedu/points/domain/PointLedger.java) | 积分变动流水的持久化实体，映射数据库字段。 | 字段：id、userId、delta、balanceAfter、type、sourceKey、reason、operatorAdminId、createdAt |
| [domain/PointLedgerType.java](C:/Users/86198/Desktop/play/playedu-api/playedu-points/src/main/java/xyz/playedu/points/domain/PointLedgerType.java) | 积分变动类型枚举/状态定义。 | 数据/常量定义；详见源码 |
| [domain/PointProduct.java](C:/Users/86198/Desktop/play/playedu-api/playedu-points/src/main/java/xyz/playedu/points/domain/PointProduct.java) | 积分商品的持久化实体，映射数据库字段。 | 字段：id、name、pointsPrice、status、availableCount、deliveredCount、createdAt、updatedAt |
| [domain/PointProductStatus.java](C:/Users/86198/Desktop/play/playedu-api/playedu-points/src/main/java/xyz/playedu/points/domain/PointProductStatus.java) | 积分商品上下架状态枚举/状态定义。 | 数据/常量定义；详见源码 |
| [domain/PointRedemption.java](C:/Users/86198/Desktop/play/playedu-api/playedu-points/src/main/java/xyz/playedu/points/domain/PointRedemption.java) | 积分兑换记录的持久化实体，映射数据库字段。 | 字段：id、userId、productId、codeId、requestKey、pointsCost、createdAt |

#### exception

| 文件 | 作用 | 方法、字段或 SQL 线索 |
|---|---|---|
| [exception/PointBalanceException.java](C:/Users/86198/Desktop/play/playedu-api/playedu-points/src/main/java/xyz/playedu/points/exception/PointBalanceException.java) | 积分变动失败的业务异常，例如余额不足或非法变动。 | 数据/常量定义；详见源码 |

#### mapper

| 文件 | 作用 | 方法、字段或 SQL 线索 |
|---|---|---|
| [mapper/PointBalanceMapper.java](C:/Users/86198/Desktop/play/playedu-api/playedu-points/src/main/java/xyz/playedu/points/mapper/PointBalanceMapper.java) | 学员积分余额（users.credit1）的数据访问接口；通过 MyBatis-Plus 基础方法、注解 SQL 或 XML 执行数据库操作。 | 方法：lockCredit1、lockIsLock、applyCredit1Delta、applyCredit1DeltaIfNonNegative |
| [mapper/PointCodeMapper.java](C:/Users/86198/Desktop/play/playedu-api/playedu-points/src/main/java/xyz/playedu/points/mapper/PointCodeMapper.java) | 数据库访问接口；动态分页/计数 SQL 位于对应 XML，固定 SQL 保留注解并共享 XML 结果映射。 | 方法：paginate、paginateCount、findByDigest、findAvailableForUpdate、markDelivered、countAvailableByProductId、countDeliveredByProductId、insertIgnore、deleteAvailableById、deleteAvailableByProductId |
| [mapper/PointLedgerMapper.java](C:/Users/86198/Desktop/play/playedu-api/playedu-points/src/main/java/xyz/playedu/points/mapper/PointLedgerMapper.java) | 数据库访问接口；动态分页/计数 SQL 位于对应 XML，固定 SQL 保留注解并共享 XML 结果映射。 | 方法：paginate、paginateCount、findBySourceKey、findBySourceKeyForUpdate、insertIfAbsent |
| [mapper/PointProductMapper.java](C:/Users/86198/Desktop/play/playedu-api/playedu-points/src/main/java/xyz/playedu/points/mapper/PointProductMapper.java) | 数据库访问接口；动态分页/计数 SQL 位于对应 XML，固定 SQL 保留注解并共享 XML 结果映射。 | 方法：paginate、paginateCount、selectByIdForUpdate |
| [mapper/PointRedemptionMapper.java](C:/Users/86198/Desktop/play/playedu-api/playedu-points/src/main/java/xyz/playedu/points/mapper/PointRedemptionMapper.java) | 数据库访问接口；动态分页/计数 SQL 位于对应 XML，固定 SQL 保留注解并共享 XML 结果映射。 | 方法：paginate、paginateCount、findByIdAndUserId、findByUserIdAndRequestKey、findByUserIdAndRequestKeyForUpdate |

#### service

| 文件 | 作用 | 方法、字段或 SQL 线索 |
|---|---|---|
| [service/PointBalanceService.java](C:/Users/86198/Desktop/play/playedu-api/playedu-points/src/main/java/xyz/playedu/points/service/PointBalanceService.java) | 统一积分余额变动接口，接收变动请求并返回应用结果。 | 方法：apply |
| [service/PointCodeService.java](C:/Users/86198/Desktop/play/playedu-api/playedu-points/src/main/java/xyz/playedu/points/service/PointCodeService.java) | 兑换码库存业务接口：批量导入、查询、库存计数、查看码及删除。 | 方法：paginate、importCodes、availableCount、hasDeliveredCodes、deleteAvailable、findOrFail、reveal、deleteAvailableByProductId |
| [service/PointLedgerService.java](C:/Users/86198/Desktop/play/playedu-api/playedu-points/src/main/java/xyz/playedu/points/service/PointLedgerService.java) | 积分流水分页查询与按学员清理的业务接口。 | 方法：removeByUserId、paginate |
| [service/PointProductService.java](C:/Users/86198/Desktop/play/playedu-api/playedu-points/src/main/java/xyz/playedu/points/service/PointProductService.java) | 积分商品的查询、创建、更新、上下架及删除业务接口。 | 方法：paginate、findOrFail、findForUpdate、create、update、changeStatus、offSale、availableCount、hasDeliveredCodes、deleteById |
| [service/PointRedemptionService.java](C:/Users/86198/Desktop/play/playedu-api/playedu-points/src/main/java/xyz/playedu/points/service/PointRedemptionService.java) | 积分商品兑换及兑换记录查询、清理接口。 | 方法：removeByUserId、findForUser、paginate、redeem |
| [service/PointSourceKeys.java](C:/Users/86198/Desktop/play/playedu-api/playedu-points/src/main/java/xyz/playedu/points/service/PointSourceKeys.java) | 为课程完成奖励、人工调整等积分来源构造来源键。 | 方法：courseCompletion、manualAdjustment |

#### service/impl

| 文件 | 作用 | 方法、字段或 SQL 线索 |
|---|---|---|
| [service/impl/PointBalanceServiceImpl.java](C:/Users/86198/Desktop/play/playedu-api/playedu-points/src/main/java/xyz/playedu/points/service/impl/PointBalanceServiceImpl.java) | 事务性更新 users.credit1 并写积分流水，校验变动方向、来源和幂等性。 | 方法：apply、newLedger、validateSourceKey、validate、requirePositiveDelta、requireNegativeDelta、validateManualAdjustment |
| [service/impl/PointCodeServiceImpl.java](C:/Users/86198/Desktop/play/playedu-api/playedu-points/src/main/java/xyz/playedu/points/service/impl/PointCodeServiceImpl.java) | 实现兑换码逐行规范化、加密与去重导入，管理可用库存和查看、删除规则。 | 方法：paginate、importCodes、availableCount、hasDeliveredCodes、deleteAvailable、findOrFail、reveal、deleteAvailableByProductId、emptyImportResult、normalizedPageSize、pageOffset |
| [service/impl/PointLedgerServiceImpl.java](C:/Users/86198/Desktop/play/playedu-api/playedu-points/src/main/java/xyz/playedu/points/service/impl/PointLedgerServiceImpl.java) | 实现积分流水分页筛选及学员删除时的流水清理。 | 方法：removeByUserId、paginate、normalizedPageSize、pageOffset |
| [service/impl/PointProductServiceImpl.java](C:/Users/86198/Desktop/play/playedu-api/playedu-points/src/main/java/xyz/playedu/points/service/impl/PointProductServiceImpl.java) | 实现积分商品维护及状态转换，结合兑换码库存处理商品操作约束。 | 方法：paginate、findOrFail、create、update、changeStatus、offSale、availableCount、hasDeliveredCodes、deleteById、findForUpdate、validateName、validatePrice等 |
| [service/impl/PointRedemptionServiceImpl.java](C:/Users/86198/Desktop/play/playedu-api/playedu-points/src/main/java/xyz/playedu/points/service/impl/PointRedemptionServiceImpl.java) | 在事务中编排商品兑换、库存码分配、积分扣减及兑换记录，处理并发与重复请求。 | 方法：removeByUserId、findForUser、paginate、redeem、validateRequest、validateProduct、validateLearner、sourceKey、normalizedPageSize、pageOffset |

#### types

| 文件 | 作用 | 方法、字段或 SQL 线索 |
|---|---|---|
| [types/PointBalanceChange.java](C:/Users/86198/Desktop/play/playedu-api/playedu-points/src/main/java/xyz/playedu/points/types/PointBalanceChange.java) | 一次积分变动的输入对象，包含学员、金额、类型及来源等信息。 | 方法：PointBalanceChange |
| [types/PointBalanceChangeResult.java](C:/Users/86198/Desktop/play/playedu-api/playedu-points/src/main/java/xyz/playedu/points/types/PointBalanceChangeResult.java) | 积分变动执行结果，包含应用情况及变动后的余额等信息。 | 方法：PointBalanceChangeResult、balanceAfter |
| [types/PointCodeImportLineResult.java](C:/Users/86198/Desktop/play/playedu-api/playedu-points/src/main/java/xyz/playedu/points/types/PointCodeImportLineResult.java) | 兑换码单行导入结果的数据对象，承载信息供业务或接口使用。 | 方法：getLineNumber、getStatus |
| [types/PointCodeImportLineStatus.java](C:/Users/86198/Desktop/play/playedu-api/playedu-points/src/main/java/xyz/playedu/points/types/PointCodeImportLineStatus.java) | 兑换码单行导入状态枚举/状态定义。 | 数据/常量定义；详见源码 |
| [types/PointCodeImportResult.java](C:/Users/86198/Desktop/play/playedu-api/playedu-points/src/main/java/xyz/playedu/points/types/PointCodeImportResult.java) | 兑换码批量导入汇总的数据对象，承载信息供业务或接口使用。 | 方法：getImportedCount、getDuplicateCount、getResults |

#### 测试文件

| 文件 | 作用 | 方法、字段或 SQL 线索 |
|---|---|---|
| [test/mapper/PointPaginationMapperIntegrationTest.java](C:/Users/86198/Desktop/play/playedu-api/playedu-points/src/test/java/xyz/playedu/points/mapper/PointPaginationMapperIntegrationTest.java) | 使用真实 MySQL 验证四个 Mapper 的筛选、列表/计数、分页排序、时间边界及枚举和字段映射。 | 测试：products、codes、ledgers、redemptions 的动态分页查询 |
| [test/crypto/PointCodeCryptoServiceTest.java](C:/Users/86198/Desktop/play/playedu-api/playedu-points/src/test/java/xyz/playedu/points/crypto/PointCodeCryptoServiceTest.java) | 验证 PointCodeCryptoService 的指定功能及异常分支；具体用例见右侧方法。 | 数据/常量定义；详见源码 |
| [test/service/PointBalanceServiceIntegrationTest.java](C:/Users/86198/Desktop/play/playedu-api/playedu-points/src/test/java/xyz/playedu/points/service/PointBalanceServiceIntegrationTest.java) | 验证 PointBalanceService 的集成行为（实际依赖与事务/并发等）；具体用例见右侧方法。 | 方法：getResult |
| [test/service/PointCodeInventoryIntegrationTest.java](C:/Users/86198/Desktop/play/playedu-api/playedu-points/src/test/java/xyz/playedu/points/service/PointCodeInventoryIntegrationTest.java) | 验证 PointCodeInventory 的集成行为（实际依赖与事务/并发等）；具体用例见右侧方法。 | 数据/常量定义；详见源码 |
| [test/service/PointCodeServiceTest.java](C:/Users/86198/Desktop/play/playedu-api/playedu-points/src/test/java/xyz/playedu/points/service/PointCodeServiceTest.java) | 验证 PointCodeService 的指定功能及异常分支；具体用例见右侧方法。 | 方法：stubProduct、code |
| [test/service/PointProductServiceTest.java](C:/Users/86198/Desktop/play/playedu-api/playedu-points/src/test/java/xyz/playedu/points/service/PointProductServiceTest.java) | 验证 PointProductService 的指定功能及异常分支；具体用例见右侧方法。 | 方法：product |
| [test/service/PointRedemptionServiceIntegrationTest.java](C:/Users/86198/Desktop/play/playedu-api/playedu-points/src/test/java/xyz/playedu/points/service/PointRedemptionServiceIntegrationTest.java) | 验证 PointRedemptionService 的集成行为（实际依赖与事务/并发等）；具体用例见右侧方法。 | 方法：assertNoDeliveryOrDebit、insertAvailableCodes、balanceOf、countByStatus、count |

### 容易误读的地方

- common 包含用户、部门、管理员、LDAP 等业务，并非只放工具。
- course 的 CourseCategory 与 resource 的 ResourceCategory 都是关联实体；分类树本身由 common/Category 管理。
- PointBalanceMapper 更新学员表的 credit1；积分余额不一定对应单独的 PointBalance 实体或表。
- BackendPermission.xml 和 ResourceVideoMapper.xml 的职责应以实际 namespace 判断。
- 删除章节会调用课时服务删除该章节课时；监听方法名 resetCourseHourChapterId 不能完整表达其行为。
- api/cache 和 course/caches 当前为空目录，没有可解释的源码文件。
- AcceptanceProbeController 是默认关闭的验收接口，不是普通产品功能入口。
- target 是编译产物，依赖、class、JAR 和测试报告会随构建变化，不是维护业务的入口。
