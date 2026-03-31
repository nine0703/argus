package io.argus.runtime;

import io.argus.agent.AgentRunner;
import io.argus.core.audit.AuditLog;
import io.argus.core.lifecycle.LifecyclePhase;
import io.argus.core.memory.Memory;
import io.argus.ingestion.audit.IngestionAuditPublisher;
import io.argus.ingestion.audit.fetch.FetchAuditPublisher;
import io.argus.ingestion.domain.chunk.ChunkStrategy;
import io.argus.ingestion.domain.embedding.EmbeddingModel;
import io.argus.ingestion.domain.vector.VectorStore;
import io.argus.ingestion.fetch.FetchExecutor;
import io.argus.ingestion.fetch.FetchExecutorRegistry;
import io.argus.ingestion.orchestration.IngestionOrchestrator;
import io.argus.ingestion.parse.Parser;
import junit.framework.TestCase;

/**
 * @author TK.ENDO
 * @since 2026-03-31 周二 23:11
 */
public class ArgusRuntimeTest extends TestCase {

    public void testShouldTransitionLifecyclePhases() {

        Memory memory = ArgusRuntimeFactory.createDefaultMemory();
        AuditLog auditLog = ArgusRuntimeFactory.createDefaultAuditLog();
        FetchAuditPublisher fetchAuditPublisher = ArgusRuntimeFactory.createDefaultFetchAuditPublisher(auditLog);
        IngestionAuditPublisher ingestionAuditPublisher =
                ArgusRuntimeFactory.createDefaultIngestionAuditPublisher(auditLog);
        FetchExecutorRegistry fetchExecutorRegistry =
                ArgusRuntimeFactory.createDefaultFetchExecutorRegistry(fetchAuditPublisher);
        FetchExecutor fetchExecutor = ArgusRuntimeFactory.createDefaultFetchExecutor(fetchExecutorRegistry);
        Parser parser = ArgusRuntimeFactory.createDefaultParser();
        ChunkStrategy chunkStrategy = ArgusRuntimeFactory.createDefaultChunkStrategy();
        EmbeddingModel embeddingModel = ArgusRuntimeFactory.createDefaultEmbeddingModel();
        VectorStore vectorStore = ArgusRuntimeFactory.createDefaultVectorStore();
        IngestionOrchestrator ingestionOrchestrator = ArgusRuntimeFactory.createDefaultIngestionOrchestrator(
                fetchExecutor,
                parser,
                chunkStrategy,
                embeddingModel,
                vectorStore,
                ingestionAuditPublisher
        );
        AgentRunner agentRunner = ArgusRuntimeFactory.createDefaultAgentRunner(memory, auditLog);

        ArgusRuntime runtime = ArgusRuntimeFactory.createRuntime(
                memory,
                auditLog,
                fetchAuditPublisher,
                ingestionAuditPublisher,
                fetchExecutorRegistry,
                fetchExecutor,
                ingestionOrchestrator,
                agentRunner
        );

        assertEquals(LifecyclePhase.CREATED, runtime.phase());
        runtime.start();
        assertEquals(LifecyclePhase.RUNNING, runtime.phase());
        runtime.stop();
        assertEquals(LifecyclePhase.STOPPED, runtime.phase());
        runtime.start();
        assertEquals(LifecyclePhase.RUNNING, runtime.phase());
    }

} // Class end.