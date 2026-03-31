# ARGUS Agent Guide

> Working agreement for contributors and coding agents in this repository.
>
> 本文档定义 ARGUS 仓库内人工与代理协作时的最小工作约束。

## Project Intent

- ARGUS is a production-grade Java runtime for auditable, controllable, and reproducible ingestion and agent execution.
- ARGUS 是一个面向可审计、可控制、可复现执行语义的 Java 工程，不接受“先跑起来再说”的随意实现。

## Module Boundaries

- `argus-core`
  - Core abstractions only.
  - Place shared semantics here: action, agent, audit, memory, lifecycle, model, observation, error.
- `argus-ingestion`
  - Deterministic ingestion pipeline and fetch-related integrations.
  - Fetch / parse / chunk / embed / vector responsibilities belong here.
- `argus-agent`
  - Agent-specific orchestration built on `argus-core`.
  - Do not duplicate core contracts here.
- `argus-runtime`
  - Runtime container and execution wiring.
  - Keep framework or deployment concerns isolated here.

## Code Style

- Keep Java source in UTF-8 without BOM.
- Prefer small, explicit, framework-agnostic classes.
- Follow the existing style:
  - English-first API naming.
  - Javadoc in the current project tone, with Chinese supplemental explanation where the surrounding file already uses it.
  - End-of-class marker comments such as `// Class end.` when the file already follows that pattern.
- Prefer immutable state and defensive copying for exposed collections.
- New behavior must preserve the project principles:
  - Auditable
  - Controllable
  - Reproducible

## Change Rules

- Patch existing files instead of replacing whole files.
- Do not remove large existing comment blocks just to simplify an edit.
- Complete the already-declared contract before inventing new abstractions.
- When a module only contains scaffolding, prefer filling missing core behavior over introducing speculative architecture.

## Testing Rules

- Add focused tests for every newly completed execution path.
- Prefer deterministic local tests.
- Avoid network-dependent integration tests unless the behavior being added is explicitly network-facing and can be exercised with a local fixture or embedded server.
- Maven command for this repository is `C:/dev/apache-maven-3.6.3/bin/mvn.cmd`.
- Maven settings files for this repository are located under `C:/dev/maven-env/conf`.
- Default Maven settings file for this repository is `C:/dev/maven-env/conf/settings-public.xml`.

## Documentation Rules

- Keep docs concrete.
- If a class is still intentionally incomplete, state the boundary explicitly in code or docs.
- If an implementation throws by design, explain why it is intentionally unsupported instead of leaving a silent placeholder.
