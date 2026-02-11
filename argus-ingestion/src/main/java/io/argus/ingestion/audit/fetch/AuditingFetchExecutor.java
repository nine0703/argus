package io.argus.ingestion.audit.fetch;

import io.argus.ingestion.fetch.FetchExecutor;
import io.argus.ingestion.fetch.FetchProtocol;
import io.argus.ingestion.fetch.FetchRequest;
import io.argus.ingestion.fetch.FetchResult;

import java.time.Instant;

/**
 * Decorator that emits audit events around fetch execution.
 * @author TK.ENDO
 * @since 2026-02-11 周三 14:56
 */
public class AuditingFetchExecutor implements FetchExecutor {

    private final FetchExecutor delegate;
    private final FetchAuditPublisher publisher;

    public AuditingFetchExecutor(
            FetchExecutor delegate,
            FetchAuditPublisher publisher
    ) {
        this.delegate = delegate;
        this.publisher = publisher;
    }

    @Override
    public FetchProtocol protocol() {
        return delegate.protocol();
    }

    @Override
    public FetchResult execute(FetchRequest request) {

        publisher.publish(
                new FetchAuditEvent(
                        FetchAuditType.FETCH_STARTED,
                        request.protocol(),
                        request.resource().toString(),
                        Instant.now(),
                        null
                )
        );

        try {

            FetchResult result = delegate.execute(request);

            publisher.publish(
                    new FetchAuditEvent(
                            FetchAuditType.FETCH_SUCCEEDED,
                            request.protocol(),
                            request.resource().toString(),
                            Instant.now(),
                            "success=" + result.success()
                    )
            );

            return result;

        } catch (Exception e) {

            publisher.publish(
                    new FetchAuditEvent(
                            FetchAuditType.FETCH_FAILED,
                            request.protocol(),
                            request.resource().toString(),
                            Instant.now(),
                            e.getMessage()
                    )
            );

            throw e;
        }
    }

}   // Class end.