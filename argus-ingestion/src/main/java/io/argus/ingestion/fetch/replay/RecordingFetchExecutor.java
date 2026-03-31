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

/**
 * A FetchExecutor decorator that records every execution
 * into a FetchRecordStore for replay or auditing.
 * <p>
 * This executor:
 * - does NOT change execution semantics
 * - does NOT suppress exceptions
 * - always records success and failure
 * @author TK.ENDO
 * @since 2026-02-11 周三 14:51
 */
public class RecordingFetchExecutor implements FetchExecutor {

    private final FetchExecutor delegate;
    private final FetchRecordStore recordStore;
    private final FetchAuditPublisher publisher;

    public RecordingFetchExecutor(
            FetchExecutor delegate,
            FetchRecordStore recordStore) {
        this(delegate, recordStore, null);
    }

    public RecordingFetchExecutor(
            FetchExecutor delegate,
            FetchRecordStore recordStore,
            FetchAuditPublisher publisher
    ) {
        this.delegate = Objects.requireNonNull(delegate);
        this.recordStore = Objects.requireNonNull(recordStore);
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

        long start = System.currentTimeMillis();

        try {
            FetchResult result = delegate.execute(request);

            long end = System.currentTimeMillis();

            recordStore.save(
                    FetchExecutionRecord.success(
                            request,
                            result,
                            Instant.ofEpochMilli(start),
                            Instant.ofEpochMilli(end)
                    )
            );
            publishRecorded(request, "success=true");

            return result;

        } catch (RuntimeException ex) {

            long end = System.currentTimeMillis();

            recordStore.save(
                    FetchExecutionRecord.failure(
                            request,
                            ex,
                            Instant.ofEpochMilli(start),
                            Instant.ofEpochMilli(end)
                    )
            );
            publishRecorded(request, "success=false");

            throw ex;
        }
    }

    private void publishRecorded(FetchRequest request, String message) {

        if (publisher == null) {
            return;
        }

        publisher.publish(
                new FetchAuditEvent(
                        FetchAuditType.FETCH_RECORDED,
                        request.protocol(),
                        request.resource().toString(),
                        Instant.now(),
                        message
                )
        );
    }

}   // Class end.
