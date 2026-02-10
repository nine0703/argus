package io.argus.core.action;

import io.argus.core.model.Metadata;

/**
 * Represents an intention expressed by an Agent during execution.
 *
 * <p>
 * An {@code Action} describes <strong>what the agent intends to do</strong>,
 * not how the action is executed.
 * It is a declarative representation of intent, interpreted by the runtime.
 *
 * <p>
 * Every action MUST be classified by an {@link ActionType},
 * which defines its high-level semantic category.
 * Implementations MUST NOT encode execution logic or technology-specific
 * details in the action itself.
 *
 * <p>
 * Additional or domain-specific information SHOULD be provided via
 * {@link Metadata}, rather than by extending {@link ActionType}.
 *
 * <p>
 * Agent 行为（意图）模型。
 *
 * <p>
 * {@code Action} 用于描述代理“想要做什么”，
 * 而不是描述具体如何执行该行为。
 * 它是一种声明式的意图表达，由运行时进行解释和执行。
 *
 * <p>
 * 每个 Action 必须通过 {@code ActionType} 进行一级语义分类，
 * 不应在 Action 本身中包含执行逻辑或技术细节。
 * @author TK.ENDO
 * @since 2026-02-10 周二 14:03
 */
public interface Action {

    ActionType getType();

    Metadata getMetadata();

} // Class end.