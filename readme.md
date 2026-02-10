# ARGUS

> The All-Seeing Runtime for Web Knowledge Ingestion and AI Agents

ARGUS 是一个生产级的 Java 运行时，专为基于代理的系统中**可审计、可控制、可复现**的网络数据获取而设计。

## 模块结构

| 模块 | 说明 |
|------|------|
| `argus-core` | 核心基础能力（Action、Agent、Memory、Observation） |
| `argus-ingestion` | 网络知识获取（Fetch、Parse、Policy） |
| `argus-agent` | AI 代理集成支持 |
| `argus-runtime` | 生产级运行时容器 |

## 快速开始

```bash
# 编译打包
mvn clean package
```

## 设计原则

- **Auditable** - 运行过程透明可追溯
- **Controllable** - 行为确定可控
- **Reproducible** - 结果一致可复现
