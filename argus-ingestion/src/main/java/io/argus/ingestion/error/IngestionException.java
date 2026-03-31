package io.argus.ingestion.error;

import io.argus.core.error.ArgusException;

/**
 * @author TK.ENDO
 * @since 2026-02-10 周二 15:00
 */
public class IngestionException extends ArgusException {

    public IngestionException(String message) {
        super(message);
    }

    public IngestionException(String message, Throwable cause) {
        super(message, cause);
    }

    public IngestionException(Throwable cause) {
        super(cause);
    }

} // Class end.