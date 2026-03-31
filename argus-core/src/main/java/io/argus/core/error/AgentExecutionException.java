package io.argus.core.error;

/**
 * @author TK.ENDO
 * @since 2026-02-10 周二 14:05
 */
public class AgentExecutionException extends ArgusException {

    public AgentExecutionException(String message) {
        super(message);
    }

    public AgentExecutionException(String message, Throwable cause) {
        super(message, cause);
    }

    public AgentExecutionException(Throwable cause) {
        super(cause);
    }

} // Class end.