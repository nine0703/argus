package io.argus.ingestion.orchestration;

import io.argus.ingestion.fetch.FetchRequest;

/**
 * @author TK.ENDO
 * @since 2026-02-11 周三 15:31
 */
public final class IngestionCommand {

    private final String id;
    private final FetchRequest fetchRequest;
    private final IngestionOptions options;

    public IngestionCommand(
            String id,
            FetchRequest fetchRequest,
            IngestionOptions options
    ) {
        this.id = id;
        this.fetchRequest = fetchRequest;
        this.options = options;
    }

    public String id() {
        return id;
    }

    public FetchRequest fetchRequest() {
        return fetchRequest;
    }

    public IngestionOptions options() {
        return options;
    }

}   // Class end.