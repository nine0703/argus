package io.argus.ingestion.source;

import io.argus.ingestion.fetch.FetchRequest;
import io.argus.ingestion.orchestration.IngestionOptions;
import io.argus.ingestion.policy.FetchPolicy;

import java.util.Objects;

/**
 * Default immutable ingestion request snapshot.
 *
 * <p>
 * This request type binds the fetch snapshot, ingestion pipeline options,
 * and fetch policy declaration into a single transport object that can be
 * handed to an {@link IngestionSource}.
 *
 * @author TK.ENDO
 * @since 2026-03-31
 */
public final class DefaultIngestionRequest implements IngestionRequest {

    private final String id;
    private final FetchRequest fetchRequest;
    private final IngestionOptions options;
    private final FetchPolicy fetchPolicy;

    public DefaultIngestionRequest(
            String id,
            FetchRequest fetchRequest,
            IngestionOptions options,
            FetchPolicy fetchPolicy
    ) {
        this.id = Objects.requireNonNull(id, "id");
        this.fetchRequest = Objects.requireNonNull(fetchRequest, "fetchRequest");
        this.options = options == null ? IngestionOptions.defaultOptions() : options;
        this.fetchPolicy = fetchPolicy;
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

    public FetchPolicy fetchPolicy() {
        return fetchPolicy;
    }

} // Class end.
