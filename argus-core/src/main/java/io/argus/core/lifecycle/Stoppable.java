package io.argus.core.lifecycle;

/**
 * Stop contract for lifecycle-managed components.
 *
 * <p>
 * The interface also bridges into {@link AutoCloseable} so callers can use
 * try-with-resources when appropriate.
 *
 * <p>
 * 停止契约。
 *
 * <p>
 * 该接口同时对齐 {@link AutoCloseable}，便于在需要时使用统一关闭语义。
 *
 * @author TK.ENDO
 * @since 2026-02-10 周二 14:05
 */
public interface Stoppable extends AutoCloseable {

    void stop();

    @Override
    default void close() {
        stop();
    }

} // Class end.
