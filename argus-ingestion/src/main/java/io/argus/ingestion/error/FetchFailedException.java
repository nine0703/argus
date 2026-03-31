package io.argus.ingestion.error;

/**
 * @author TK.ENDO
 * @since 2026-02-10 周二 15:00
 */
public class FetchFailedException extends IngestionException {

    public FetchFailedException(String message) {
        super(message);
    }

    public FetchFailedException(String message, Throwable cause) {
        super(message, cause);
    }

    public FetchFailedException(Throwable cause) {
        super(cause);
    }

} // Class end.