package io.argus.ingestion.audit;

import io.argus.ingestion.orchestration.IngestionCommand;
import io.argus.ingestion.orchestration.IngestionOrchestrator;
import io.argus.ingestion.source.IngestionResult;

import java.time.Instant;
import java.util.Objects;

/**
 * Decorator that emits audit events around ingestion orchestration.
 *
 * <p>
 * The decorator does not change orchestration semantics. It only records
 * authoritative lifecycle facts for a single ingestion command.
 *
 * <p>
 * 该装饰器为 ingestion 编排补充开始、成功、失败三个审计事实，
 * 但不改变原始 orchestrator 的执行语义。
 * @author TK.ENDO
 * @since 2026-03-31 周二 17:25
 */
public class AuditingIngestionOrchestrator implements IngestionOrchestrator {

    private final IngestionOrchestrator delegate;
    private final IngestionAuditPublisher publisher;

    public AuditingIngestionOrchestrator(
            IngestionOrchestrator delegate,
            IngestionAuditPublisher publisher
    ) {
        this.delegate = Objects.requireNonNull(delegate, "delegate");
        this.publisher = Objects.requireNonNull(publisher, "publisher");
    }

    @Override
    public IngestionResult ingest(IngestionCommand command) {

        publisher.publish(
                new IngestionAuditEvent(
                        IngestionAuditType.INGESTION_STARTED,
                        command.id(),
                        Instant.now(),
                        null
                )
        );

        try {
            IngestionResult result = delegate.ingest(command);
            publisher.publish(
                    new IngestionAuditEvent(
                            IngestionAuditType.INGESTION_SUCCEEDED,
                            command.id(),
                            Instant.now(),
                            "success=true"
                    )
            );
            return result;
        } catch (RuntimeException e) {
            publisher.publish(
                    new IngestionAuditEvent(
                            IngestionAuditType.INGESTION_FAILED,
                            command.id(),
                            Instant.now(),
                            e.getMessage()
                    )
            );
            throw e;
        }
    }

} // Class end.
