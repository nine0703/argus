/**
 * Observations produced during agent execution.
 *
 * <p>
 * An Observation represents a fact perceived by an Agent,
 * either from internal state changes or from the external environment.
 *
 * <p>
 * Observations are immutable and are classified by
 * {@link io.argus.core.observation.ObservationType}.
 *
 * <p>
 * Agent 观测模型。
 *
 * <p>
 * Observation 表示代理在执行过程中感知到的事实，
 * 这些事实可能来自内部状态变化，也可能来自外部世界。
 *
 * <p>
 * 所有观测应当是不可变的，并通过 {@code ObservationType} 进行语义分类。
 */
package io.argus.core.observation;