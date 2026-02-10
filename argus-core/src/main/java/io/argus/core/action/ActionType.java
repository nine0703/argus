package io.argus.core.action;

/**
 * High-level semantic categories of actions expressed by an Agent.
 *
 * <p>
 * An {@code ActionType} represents the intention of an Agent,
 * not the concrete execution mechanism.
 * It defines <strong>what the agent wants to do</strong>,
 * not <strong>how it is done</strong>.
 *
 * <p>
 * Implementations MUST interpret action types according to their semantic meaning,
 * and MUST NOT extend this enum to encode protocol- or technology-specific details.
 *
 * <p>
 * 二级及更具体的语义应通过 {@code Metadata} 表达，
 * 而不是通过扩展本枚举。
 * @author TK.ENDO
 * @since 2026-02-10 周二 14:03
 */
public enum ActionType {
    /**
     * Internal decision-making without direct side effects.
     *
     * <p>
     * Used for planning, reasoning, or state transitions that do not
     * directly interact with the external environment.
     *
     * <p>
     * Typical use cases:
     * <ul>
     *   <li>Strategy selection</li>
     *   <li>Planning next steps</li>
     *   <li>Internal reasoning or inference</li>
     * </ul>
     *
     * <p>
     * 内部决策行为，不直接作用于外部世界。
     */
    DECIDE,

    /**
     * Request for an external capability or service.
     *
     * <p>
     * Represents an abstract request for computation or assistance,
     * without binding to a specific protocol or provider.
     *
     * <p>
     * Typical use cases:
     * <ul>
     *   <li>Requesting language model inference</li>
     *   <li>Invoking a plugin or tool</li>
     *   <li>Delegating a task to an external system</li>
     * </ul>
     *
     * <p>
     * 请求外部能力或服务的抽象行为。
     */
    REQUEST,

    /**
     * Fetch data from an external or internal source.
     *
     * <p>
     * Used when the primary intention is to retrieve information or content.
     * The source, protocol, and access method are implementation details.
     *
     * <p>
     * Typical use cases:
     * <ul>
     *   <li>Fetching web content</li>
     *   <li>Reading files or databases</li>
     *   <li>Loading resources</li>
     * </ul>
     *
     * <p>
     * 获取数据的行为。
     */
    FETCH,

    /**
     * Pure data transformation without external side effects.
     *
     * <p>
     * Represents deterministic or computational transformations
     * applied to existing data.
     *
     * <p>
     * Typical use cases:
     * <ul>
     *   <li>Parsing or cleaning data</li>
     *   <li>Format conversion</li>
     *   <li>Feature extraction or embedding computation</li>
     * </ul>
     *
     * <p>
     * 纯数据变换行为，不应产生外部副作用。
     */
    TRANSFORM,

    /**
     * Persist or commit data to memory or storage.
     *
     * <p>
     * Represents actions that produce durable side effects,
     * such as storing agent memory or updating persistent state.
     *
     * <p>
     * Typical use cases:
     * <ul>
     *   <li>Writing to agent memory</li>
     *   <li>Persisting results</li>
     *   <li>Updating long-term state</li>
     * </ul>
     *
     * <p>
     * 数据持久化或状态提交行为。
     */
    STORE,

    /**
     * Emit information or signals to the external environment.
     *
     * <p>
     * Used when the agent intends to communicate results or events
     * outside of its internal execution context.
     *
     * <p>
     * Typical use cases:
     * <ul>
     *   <li>Returning final output</li>
     *   <li>Publishing events or notifications</li>
     *   <li>Triggering callbacks or webhooks</li>
     * </ul>
     *
     * <p>
     * 对外输出或通知行为。
     */
    EMIT

} // Class end.