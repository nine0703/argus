package io.argus.core.observation;

import io.argus.core.model.Metadata;

/**
 * Represents a factual perception observed by an Agent during execution.
 *
 * <p>
 * An {@code Observation} describes <strong>what has occurred</strong>,
 * either as a result of an action or as a change in the environment.
 * Observations are immutable facts and do not prescribe behavior.
 *
 * <p>
 * Every observation MUST be classified by an {@link ObservationType},
 * which defines its high-level semantic meaning.
 *
 * <p>
 * Additional context or domain-specific information SHOULD be conveyed
 * via {@link Metadata}, rather than by extending {@link ObservationType}.
 *
 * <p>
 * Agent 观测模型。
 *
 * <p>
 * {@code Observation} 用于表达代理在执行过程中“看到的事实”，
 * 这些事实可能来源于内部状态变化，也可能来自外部环境。
 * 观测本身是不可变的，不包含决策或行为指令。
 * @author TK.ENDO
 * @since 2026-02-10 周二 14:03
 */
public interface Observation {

    ObservationType getType();

    Metadata getMetadata();

} // Class end.