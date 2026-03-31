package io.argus.core.lifecycle;

/**
 * Lifecycle contract for runtime-managed components.
 *
 * <p>
 * Implementations expose a stable lifecycle phase and are expected to make
 * {@link #start()} idempotent for repeated bootstrap attempts.
 *
 * <p>
 * 生命周期契约。
 *
 * <p>
 * 实现类需要暴露当前生命周期阶段，并保证 {@link #start()} 在重复调用时
 * 具备幂等性。
 *
 * @author TK.ENDO
 * @since 2026-02-10 周二 14:04
 */
public interface Lifecycle {

    LifecyclePhase phase();

    Lifecycle start();

    default boolean isRunning() {
        return phase() == LifecyclePhase.RUNNING;
    }

    default boolean isStarted() {
        LifecyclePhase phase = phase();
        return phase == LifecyclePhase.STARTING
                || phase == LifecyclePhase.RUNNING
                || phase == LifecyclePhase.STOPPING;
    }

} // Class end.
