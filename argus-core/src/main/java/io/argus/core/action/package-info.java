/**
 * Agent actions.
 *
 * <p>
 * An Action represents an intention expressed by an Agent.
 * It does not perform execution by itself.
 *
 * <p>
 * Actions are classified by {@link io.argus.core.action.ActionType},
 * which defines high-level semantic categories rather than concrete implementations.
 *
 * <p>
 * Agent 行为（意图）模型。
 *
 * <p>
 * Action 表示代理表达的“意图”，
 * 本身不包含任何执行逻辑。
 *
 * <p>
 * 行为通过 {@code ActionType} 进行一级语义分类，
 * 具体实现由运行时模块解释。
 */
package io.argus.core.action;