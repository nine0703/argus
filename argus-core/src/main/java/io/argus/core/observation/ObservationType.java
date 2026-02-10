package io.argus.core.observation;

/**
 * High-level semantic categories of observations perceived by an Agent.
 *
 * <p>
 * An {@code ObservationType} represents a factual perception
 * produced during agent execution.
 * Observations describe <strong>what happened</strong>,
 * not <strong>how the agent should react</strong>.
 *
 * <p>
 * Observations are immutable facts and may originate from
 * internal state changes or external systems.
 * @author TK.ENDO
 * @since 2026-02-10 周二 14:04
 */
public enum ObservationType {
    /**
     * Observation of internal state changes.
     *
     * <p>
     * Represents updates to the agent's internal state or lifecycle phase.
     *
     * <p>
     * Typical use cases:
     * <ul>
     *   <li>Agent state transitions</li>
     *   <li>Lifecycle phase changes</li>
     * </ul>
     *
     * <p>
     * 内部状态变化的观测。
     */
    STATE,

    /**
     * Observation of raw or structured data.
     *
     * <p>
     * Represents factual data retrieved or produced during execution.
     * The data format and schema are implementation-specific.
     *
     * <p>
     * Typical use cases:
     * <ul>
     *   <li>Fetched web content</li>
     *   <li>Parsed documents</li>
     *   <li>Extracted datasets</li>
     * </ul>
     *
     * <p>
     * 原始或结构化数据的观测。
     */
    DATA,

    /**
     * Observation of responses to previously issued actions.
     *
     * <p>
     * Represents feedback or results returned by external systems
     * in response to an {@code Action}.
     *
     * <p>
     * Typical use cases:
     * <ul>
     *   <li>Language model responses</li>
     *   <li>API call results</li>
     *   <li>Tool invocation outputs</li>
     * </ul>
     *
     * <p>
     * 对 Action 的响应结果观测。
     */
    RESPONSE,

    /**
     * Observation of errors or failures.
     *
     * <p>
     * Represents observable failure conditions encountered during execution.
     * Errors are treated as facts, not exceptions.
     *
     * <p>
     * Typical use cases:
     * <ul>
     *   <li>Timeouts</li>
     *   <li>Access failures</li>
     *   <li>Parsing or processing errors</li>
     * </ul>
     *
     * <p>
     * 错误或失败状态的观测。
     */
    ERROR,

    /**
     * Observation of external or asynchronous events.
     *
     * <p>
     * Represents events originating outside the agent's immediate control,
     * such as user input or scheduled triggers.
     *
     * <p>
     * Typical use cases:
     * <ul>
     *   <li>User interactions</li>
     *   <li>Webhook events</li>
     *   <li>Scheduled or timer-based triggers</li>
     * </ul>
     *
     * <p>
     * 外部或异步事件的观测。
     */
    EVENT

} // Class end.