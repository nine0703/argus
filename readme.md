# ARGUS

> The All-Seeing Runtime for Web Knowledge Ingestion and AI Agents

ARGUS 是一个生产级的 Java 运行时，专为基于代理的系统中**可审计、可控制、可复现**的网络数据获取而设计。

## 模块结构

| 模块 | 说明 |
|------|------|
| `argus-core` | 核心基础能力（Action、Agent、Memory、Observation） |
| `argus-ingestion` | 网络知识获取（Fetch、Parse、Policy） |
| `argus-agent` | AI 代理最小执行层与 loop 驱动模型 |
| `argus-runtime` | 运行时核心实现与默认装配能力 |
| `argus-spring-boot-autoconfigure` | Spring Boot 自动装配模块 |
| `argus-spring-boot-starter` | Spring Boot Starter 聚合入口 |

## Spring Boot 集成

ARGUS 的 Spring Boot 接入采用分层设计：

- `argus-runtime` 提供不绑定 Spring 的运行时核心能力
- `argus-agent` 提供 loop-driven agent 的默认执行层
- `argus-spring-boot-autoconfigure` 提供自动装配
- `argus-spring-boot-starter` 作为业务方导入坐标

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
- `FetchExecutor`
- `Parser`（`SimpleDocumentParser`）
- `ChunkStrategy`（`FixedSizeChunkStrategy`）
- `EmbeddingModel`（`HashEmbeddingModel`）
- `VectorStore`（`InMemoryVectorStore`）
- `IngestionOrchestrator`
- `AgentRunner`

`ArgusRuntime` 会聚合上述关键能力，因此业务侧既可以按 Bean 单点注入，也可以直接以运行时容器为统一入口。

所有 Bean 都采用 `@ConditionalOnMissingBean`，业务侧可以按单个能力点覆盖，而不需要整体替换 starter。

## 配置项

| 配置项 | 默认值 | 说明 |
|------|------|------|
| `argus.enabled` | `true` | 是否启用 ARGUS 自动装配 |
| `argus.ingestion.chunk-size` | `1000` | 默认分块策略的固定块大小 |
| `argus.ingestion.embedding-dimension` | `16` | 默认哈希向量模型的维度 |
| `argus.ingestion.vector-namespace` | `default` | 默认内存向量库命名空间 |

## 快速开始

```bash
# 编译打包
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