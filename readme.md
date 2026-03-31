# ARGUS

> The All-Seeing Runtime for Web Knowledge Ingestion and AI Agents

ARGUS 是一个生产级 Java 运行时，专为代理式系统中的 **可审计、可控制、可复现** Web 数据摄取而设计。

## 模块结构

| 模块 | 说明 |
|------|------|
| `argus-core` | 核心基础能力：Action、Agent、Memory、Observation、Lifecycle |
| `argus-ingestion` | 网络知识摄取：Fetch、Parse、Policy、Replay |
| `argus-agent` | AI 代理的最小执行层与 loop 驱动模型 |
| `argus-runtime` | 运行时核心实现与默认装配能力 |
| `argus-spring-boot-autoconfigure` | Spring Boot 自动装配模块 |
| `argus-spring-boot-starter` | Spring Boot Starter 聚合入口 |

## Spring Boot 集成

ARGUS 的 Spring Boot 接入采用分层设计：

- `argus-runtime` 提供不绑定 Spring 的运行时核心能力
- `argus-agent` 提供 loop-driven agent 的默认执行层
- `argus-spring-boot-autoconfigure` 提供自动装配
- `argus-spring-boot-starter` 作为业务侧导入坐标

目标使用方式如下：

```xml
<dependency>
    <groupId>io.argus</groupId>
    <artifactId>argus-spring-boot-starter</artifactId>
    <version>1.0.0</version>
</dependency>
```

## Starter 默认装配

引入 `argus-spring-boot-starter` 后，默认会装配以下本地实现：

- `ArgusRuntime`
- `Memory`（`InMemoryMemory`）
- `AuditLog`（`InMemoryAuditLog`）
- `FetchAuditPublisher`
- `IngestionAuditPublisher`
- `FetchExecutorRegistry`
- `FetchRecordStore`（默认 `InMemoryFetchRecordStore`）
- `FetchPolicy`
- `FetchExecutor`
- `RobotsTxtService`
- `Parser`（`SimpleDocumentParser`）
- `ChunkStrategy`（`FixedSizeChunkStrategy`）
- `EmbeddingModel`（`HashEmbeddingModel`）
- `VectorStore`（`InMemoryVectorStore`）
- `IngestionOrchestrator`
- `IngestionSource`
- `AgentRunner`

`ArgusRuntime` 会聚合上述关键能力，因此业务侧既可以按 Bean 单点注入，也可以直接以运行时容器作为统一入口。

所有 Bean 都采用 `@ConditionalOnMissingBean`，业务侧可以按单个能力点覆盖，而不需要整体替换 starter。

## 生命周期语义

`ArgusRuntime` 现在具备显式生命周期：

- 纯 Java 场景下，`ArgusRuntimeFactory.createDefaultRuntime()` 返回已启动的运行时
- Spring Boot 场景下，自动装配 Bean 会在容器初始化时调用 `start()`，在容器关闭时调用 `stop()`
- 生命周期阶段包括 `CREATED`、`STARTING`、`RUNNING`、`STOPPING`、`STOPPED`、`FAILED`

这保证了 starter 导入后的运行时状态是明确的，而不是“Bean 在，但是否可用不确定”。

## 抓取与回放默认行为

- 默认抓取回放模式为 `LIVE`
- 默认回放记录存储为内存实现；如需跨进程复现，可切换到 `FILE`
- `robots.txt` 校验默认关闭；仅在显式开启后，才会在抓取 HTTP/HTTPS 资源前抓取并解析对应站点的 `robots.txt`
- 当回放模式为 `REPLAY_ONLY` 或 `HYBRID` 且开启 `robots.txt` 校验时，`robots.txt` 本身也会通过同一套 replay 机制获取

## 配置项

| 配置项 | 默认值 | 说明 |
|------|------|------|
| `argus.enabled` | `true` | 是否启用 ARGUS 自动装配 |
| `argus.ingestion.chunk-size` | `1000` | 默认分块策略的固定块大小，必须大于 0 |
| `argus.ingestion.embedding-dimension` | `16` | 默认哈希向量模型的维度，必须大于 0 |
| `argus.ingestion.vector-namespace` | `default` | 默认内存向量库命名空间，不能为空白 |
| `argus.fetch.replay-mode` | `LIVE` | 默认抓取执行模式，可选 `LIVE`、`HYBRID`、`REPLAY_ONLY` |
| `argus.fetch.record-store.type` | `MEMORY` | 回放记录存储类型，可选 `MEMORY`、`FILE` |
| `argus.fetch.record-store.file-path` | 空 | 当记录存储类型为 `FILE` 时使用的持久化文件路径；留空时使用运行时默认路径 |
| `argus.fetch.policy.allowed-protocols` | 空集合 | 允许的抓取协议名集合；为空时表示不限制协议 |
| `argus.fetch.policy.rate-limit` | `PT0S` | 同一资源两次 LIVE 抓取之间的最小时间间隔 |
| `argus.fetch.policy.obey-robots-txt` | `false` | 是否在访问 HTTP/HTTPS 资源前抓取并执行 `robots.txt` 规则 |
| `argus.fetch.policy.user-agent` | `argus` | 执行 `robots.txt` 规则匹配时使用的 User-Agent |
| `argus.fetch.policy.robots-cache-ttl` | `PT10M` | `robots.txt` 解析结果缓存时长 |

## 配置示例

```yaml
argus:
  fetch:
    replay-mode: HYBRID
    record-store:
      type: FILE
      file-path: /var/lib/argus/fetch-record-store.bin
    policy:
      allowed-protocols:
        - http
        - https
      rate-limit: 1s
      obey-robots-txt: true
      user-agent: argus-bot
      robots-cache-ttl: 5m
```

## 快速开始

```bash
mvn clean package
```

```java
@Configuration
public class DemoConfiguration {

    private final ArgusRuntime runtime;

    public DemoConfiguration(ArgusRuntime runtime) {
        this.runtime = runtime;
    }

}
```

## 设计原则

- **Auditable** - 运行过程透明可追溯
- **Controllable** - 行为确定可控
- **Reproducible** - 结果一致可复现
