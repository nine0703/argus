package io.argus.ingestion.fetch.replay;

import java.io.Serializable;

/**
 * @author TK.ENDO
 * @since 2026-02-11 周三 15:14
 */
public final class ExceptionSnapshot implements Serializable {

    private static final long serialVersionUID = 1L;

    private final String type;
    private final String message;

    private ExceptionSnapshot(String type, String message) {
        this.type = type;
        this.message = message;
    }

    public static ExceptionSnapshot from(Throwable t) {
        return new ExceptionSnapshot(
                t.getClass().getName(),
                t.getMessage()
        );
    }

    public String type() {
        return type;
    }

    public String message() {
        return message;
    }

}
