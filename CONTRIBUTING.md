# 贡献指南

## 开发环境

- 后端：Java 17，使用 `playedu-api/` 中的 Maven Wrapper。
- 前端：Node.js 20，各前端独立安装依赖，使用支持 lockfile v9 的 pnpm。
- 运行与集成测试：Docker Compose、MySQL、Redis；部分测试使用 Testcontainers，需要可用的 Docker 引擎。

## 本地开发

1. 从 `.env.example` 复制 `.env`，按 README 配置运行环境。
2. 前端在对应目录执行 `pnpm install --frozen-lockfile`，复制该目录的 `.env.example` 为 `.env.local`，配置 `VITE_APP_URL` 后执行 `pnpm dev`。
3. 后端的六个 Maven 模块在 `playedu-api/pom.xml` 中统一管理；数据库、Redis 等配置参考应用的 `application.yml` 与根目录 `.env.example`。根目录 `.env` 由 Compose 加载，本机启动 Java 时需单独设置相应环境变量。

## 提交前验证

在受影响的前端目录运行：

```sh
pnpm build
```

在 `playedu-api/` 中运行：

```sh
./mvnw test
./mvnw spotless:check package
```

Windows PowerShell 7 使用 `./mvnw.cmd` 替代 `./mvnw`。涉及分布式学习行为时，执行[双实例验收](docs/acceptance/multi-instance-learning.md)。记录实际结果与环境限制，不将未执行的检查写为通过。

## 代码与 PR

- Java 遵循项目的 Spotless 配置；TypeScript 遵循现有格式与严格类型检查。
- 提交消息使用 Conventional Commits，例如 `fix(admin): correct course validation`。
- PR 说明问题、用户可见变化、配置或数据库影响，以及验证结果；界面变更附截图。
- 保留源码版权声明、页面版权信息及 LICENSE。
- 不提交密钥、真实数据、IDE 配置、构建产物或工具运行缓存。
- 功能规格和本地任务遵循 [issue tracker 约定](docs/agents/issue-tracker.md)，架构决策保留在 `docs/adr/`。

安全漏洞请按[安全报告说明](.github/SECURITY.md)私下反馈，不在公开 issue 中披露敏感信息。
