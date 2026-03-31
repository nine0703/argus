package io.argus.core.lifecycle;

/**
 * Canonical lifecycle phases for long-running runtime components.
 *
 * <p>
 * The phase model is intentionally compact so it can be reused by both
 * framework-managed containers and plain Java embeddings.
 *
 * <p>
 * 生命周期阶段定义。
 *
 * <p>
 * 该枚举用于描述运行时组件从创建到停止的标准状态机，
 * 既适用于 Spring 管理场景，也适用于纯 Java 嵌入场景。
 *
 * @author TK.ENDO
 * @since 2026-02-10 周二 14:04
 */
public enum LifecyclePhase {

    CREATED,
    STARTING,
    RUNNING,
    STOPPING,
    STOPPED,
    FAILED;

    public boolean isActive() {
        return this == STARTING || this == RUNNING || this == STOPPING;
    }

    public boolean isTerminal() {
        return this == STOPPED || this == FAILED;
    }

} // Class end.
