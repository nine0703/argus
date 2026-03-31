package io.argus.core.error;

/**
 * @author TK.ENDO
 * @since 2026-02-10 周二 14:05
 */
public class ArgusException extends RuntimeException {

    public ArgusException(String message) {
        super(message);
    }

    public ArgusException(String message, Throwable cause) {
        super(message, cause);
    }

    public ArgusException(Throwable cause) {
        super(cause);
    }

} // Class end.