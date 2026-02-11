package io.argus.ingestion.orchestration;

import io.argus.ingestion.source.IngestionResult;

/**
 * @author TK.ENDO
 * @since 2026-02-11 周三 15:30
 */
public interface IngestionOrchestrator {

    IngestionResult ingest(IngestionCommand command);

}
