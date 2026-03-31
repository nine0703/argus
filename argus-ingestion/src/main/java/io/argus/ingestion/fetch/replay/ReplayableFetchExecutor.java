package io.argus.ingestion.fetch.replay;

import io.argus.ingestion.audit.fetch.FetchAuditEvent;
import io.argus.ingestion.audit.fetch.FetchAuditPublisher;
import io.argus.ingestion.audit.fetch.FetchAuditType;
import io.argus.ingestion.fetch.FetchExecutor;
import io.argus.ingestion.fetch.FetchProtocol;
import io.argus.ingestion.fetch.FetchRequest;
import io.argus.ingestion.fetch.FetchResult;

import java.time.Instant;
import java.util.Objects;
import java.util.Optional;

/**
 * Decorator that enables replay capability.
 * @author TK.ENDO
 * @since 2026-02-11 周三 14:50
 */
public class ReplayableFetchExecutor implements FetchExecutor {

    private final FetchExecutor delegate;
    private final FetchRecordStore store;
    private final FetchReplayMode mode;
    private final FetchAuditPublisher publisher;

    public ReplayableFetchExecutor(
            FetchExecutor delegate,
            FetchRecordStore store,
            FetchReplayMode mode
    ) {
        this(delegate, store, mode, null);
    }

    public ReplayableFetchExecutor(
            FetchExecutor delegate,
            FetchRecordStore store,
            FetchReplayMode mode,
            FetchAuditPublisher publisher
    ) {
        this.delegate = Objects.requireNonNull(delegate, "delegate");
        this.store = Objects.requireNonNull(store, "store");
        this.mode = Objects.requireNonNull(mode, "mode");
        this.publisher = publisher;
    }

    @Override
    public FetchProtocol protocol() {
        return delegate.protocol();
    }

    @Override
    public boolean supports(FetchProtocol protocol) {
        return delegate.supports(protocol);
    }

    @Override
    public FetchResult execute(FetchRequest request) {

        Optional<FetchExecutionRecord> existing = store.find(request);

        switch (mode) {

            case REPLAY_ONLY:
                return replay(request, existing.orElseThrow(() ->
                        new IllegalStateException(
                                "No recorded execution found for request: " + request.resource()
                        )
                ));

            case HYBRID:
                if (existing.isPresent()) {
                    return replay(request, existing.get());
                }
                break;

            case LIVE:
            default:
                break;
        }

        return delegate.execute(request);
    }


    private FetchResult replay(FetchRequest request, FetchExecutionRecord record) {

        if (record.isFailure()) {
            throw new IllegalStateException(
                    "Recorded execution was failure for "
                            + request.resource()
                            + ": "
                            + record.failure().type()
                            + " - "
                            + record.failure().message()
            );
        }

        publishReplayed(request);
        return record.result();
    }

    private void publishReplayed(FetchRequest request) {

        if (publisher == null) {
            return;
        }

        publisher.publish(
                new FetchAuditEvent(
                        FetchAuditType.FETCH_REPLAYED,
                        request.protocol(),
                        request.resource().toString(),
                        Instant.now(),
                        "mode=" + mode.name()
                )
        );
    }

}   // Class end.
