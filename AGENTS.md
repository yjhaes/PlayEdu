# Repository Guidelines

## Project Structure & Module Organization

PlayEdu is a monorepo with a Java 17/Spring Boot backend and three React 18 clients. `playedu-api/` is the Maven parent: `playedu-api/playedu-api` contains the runnable application, while `playedu-common`, `playedu-system`, `playedu-course`, and `playedu-resource` hold shared and domain-specific code. Java sources follow `src/main/java`; MyBatis mappers and configuration live under `src/main/resources`.

`playedu-admin/`, `playedu-pc/`, and `playedu-h5/` are independent Vite/TypeScript applications. Put application code in `src/`, static files in `public/`, and component styles beside components as `*.module.less` or `*.module.scss`. Root-level `compose.yml`, `Dockerfile`, and `docker/` define the packaged stack.

## Build, Test, and Development Commands

- `docker compose up -d --build` builds and starts MySQL, API, admin, PC, and H5 services.
- `cd playedu-api && ./mvnw test` runs Maven tests across backend modules. On PowerShell, use `.\mvnw.cmd test`.
- `cd playedu-api && ./mvnw spotless:check package` checks Java formatting and builds the backend JAR.
- In any frontend directory, run `pnpm install`, then `pnpm dev` for local development or `pnpm build` for type-checking and a production bundle.

## Coding Style & Naming Conventions

Java uses Spotless with Google Java Format's AOSP style and the copyright header in `playedu-api/header.txt`. Use four-space indentation, `PascalCase` classes, `camelCase` methods/fields, and packages below `xyz.playedu`.

TypeScript is strict. Match existing two-space indentation, double quotes, and semicolons. Use `PascalCase` for React components, `camelCase` for functions, and kebab-case feature directories. Prefer `index.tsx` plus a colocated CSS module for page and component entry points.

## Testing Guidelines

The repository currently has no committed automated test suite or frontend test script. Add backend tests under the relevant module's `src/test/java`. If adding frontend tests, colocate `*.test.ts` or `*.test.tsx` files and introduce the test runner configuration in the same change. At minimum, run the affected frontend build and Maven tests before submitting.

## Commit & Pull Request Guidelines

The visible history is shallow; the available commit uses `chore(version): sync to 2.2`. Follow that Conventional Commit shape (`feat(course): ...`, `fix(admin): ...`) with a concise imperative summary. Pull requests should explain affected modules, user-visible behavior, configuration or migration changes, linked issues, and validation commands. Include screenshots for UI changes.

## Security & Configuration

Copy `.env.example` for local settings; never commit credentials. Replace default database passwords and `PLAYEDU_JWT_KEY` in production. Report vulnerabilities privately using the contact in `.github/SECURITY.md`.

## Agent skills

### Issue tracker

Issues and specs live as local markdown files under `.scratch/<feature>/`. See `docs/agents/issue-tracker.md`.

### Triage labels

Default vocabulary: the five canonical triage roles use their own names as label strings. See `docs/agents/triage-labels.md`.

### Domain docs

Single-context: one `CONTEXT.md` and one `docs/adr/` at the repo root. See `docs/agents/domain.md`.
