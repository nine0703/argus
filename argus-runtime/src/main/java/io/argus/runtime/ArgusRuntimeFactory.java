package io.argus.runtime;

import io.argus.agent.AgentRunner;
import io.argus.agent.DefaultAgentRunner;
import io.argus.core.audit.AuditLog;
import io.argus.core.audit.InMemoryAuditLog;
import io.argus.core.memory.InMemoryMemory;
import io.argus.core.memory.Memory;
import io.argus.ingestion.audit.AuditingIngestionOrchestrator;
import io.argus.ingestion.audit.IngestionAuditPublisher;
import io.argus.ingestion.audit.fetch.AuditingFetchExecutor;
import io.argus.ingestion.audit.fetch.FetchAuditPublisher;
import io.argus.ingestion.domain.chunk.ChunkStrategy;
import io.argus.ingestion.domain.chunk.FixedSizeChunkStrategy;
import io.argus.ingestion.domain.embedding.EmbeddingModel;
import io.argus.ingestion.domain.embedding.HashEmbeddingModel;
import io.argus.ingestion.domain.vector.InMemoryVectorStore;
import io.argus.ingestion.domain.vector.VectorStore;
import io.argus.ingestion.fetch.DefaultFetchExecutorRegistry;
import io.argus.ingestion.fetch.FetchExecutor;
import io.argus.ingestion.fetch.FetchExecutorRegistry;
import io.argus.ingestion.fetch.RegistryBackedFetchExecutor;
import io.argus.ingestion.fetch.protocol.ftp.FtpFetchExecutor;
import io.argus.ingestion.fetch.protocol.http.HttpFetchExecutor;
import io.argus.ingestion.orchestration.DefaultIngestionOrchestrator;
import io.argus.ingestion.orchestration.IngestionOrchestrator;
import io.argus.ingestion.parse.Parser;
import io.argus.ingestion.parse.SimpleDocumentParser;

/**
 * Creates default runtime infrastructure for ARGUS.
 *
 * <p>
 * This factory defines the out-of-the-box local runtime composition:
 * <ul>
 *   <li>in-memory {@link Memory}</li>
 *   <li>in-memory {@link AuditLog}</li>
 *   <li>audit-backed fetch publication</li>
 *   <li>audit-backed ingestion publication</li>
 *   <li>HTTP and FTP fetch executor registration</li>
 *   <li>default deterministic parser/chunk/embed/vector implementations</li>
 *   <li>default in-process {@link AgentRunner}</li>
 * </ul>
 *
 * <p>
 * The produced runtime is intentionally minimal and deterministic,
 * suitable for starter auto-configuration and local embedding.
 *
 * @author TK.ENDO
 * @since 2026-03-31 周二 17:06
 */
public final class ArgusRuntimeFactory {

    private static final int DEFAULT_CHUNK_SIZE = 1000;
    private static final int DEFAULT_EMBEDDING_DIMENSION = 16;
    private static final String DEFAULT_VECTOR_NAMESPACE = "default";

    private ArgusRuntimeFactory() {
    }

    public static ArgusRuntime createDefaultRuntime() {

        AuditLog auditLog = createDefaultAuditLog();
        Memory memory = createDefaultMemory();
        FetchAuditPublisher fetchAuditPublisher = createDefaultFetchAuditPublisher(auditLog);
        IngestionAuditPublisher ingestionAuditPublisher = createDefaultIngestionAuditPublisher(auditLog);
        FetchExecutorRegistry fetchExecutorRegistry = createDefaultFetchExecutorRegistry(fetchAuditPublisher);
        FetchExecutor fetchExecutor = createDefaultFetchExecutor(fetchExecutorRegistry);
        Parser parser = createDefaultParser();
        ChunkStrategy chunkStrategy = createDefaultChunkStrategy();
        EmbeddingModel embeddingModel = createDefaultEmbeddingModel();
        VectorStore vectorStore = createDefaultVectorStore();
        IngestionOrchestrator ingestionOrchestrator = createDefaultIngestionOrchestrator(
                fetchExecutor,
                parser,
                chunkStrategy,
                embeddingModel,
                vectorStore,
                ingestionAuditPublisher
        );
        AgentRunner agentRunner = createDefaultAgentRunner(memory, auditLog);

        return createRuntime(
                memory,
                auditLog,
                fetchAuditPublisher,
                ingestionAuditPublisher,
                fetchExecutorRegistry,
                fetchExecutor,
                ingestionOrchestrator,
                agentRunner
        );
    }

    public static Memory createDefaultMemory() {
        return new InMemoryMemory();
    }

    public static AuditLog createDefaultAuditLog() {
        return new InMemoryAuditLog();
    }

    public static FetchAuditPublisher createDefaultFetchAuditPublisher(AuditLog auditLog) {
        return new AuditLogBackedFetchAuditPublisher(auditLog);
    }

    public static IngestionAuditPublisher createDefaultIngestionAuditPublisher(AuditLog auditLog) {
        return new AuditLogBackedIngestionAuditPublisher(auditLog);
    }

    public static FetchExecutorRegistry createDefaultFetchExecutorRegistry(
            FetchAuditPublisher publisher
    ) {

        DefaultFetchExecutorRegistry registry = new DefaultFetchExecutorRegistry();
        registry.register(new AuditingFetchExecutor(new HttpFetchExecutor(), publisher));
        registry.register(new AuditingFetchExecutor(new FtpFetchExecutor(), publisher));
        return registry;
    }

    public static FetchExecutor createDefaultFetchExecutor(FetchExecutorRegistry registry) {
        return new RegistryBackedFetchExecutor(registry);
    }

    public static Parser createDefaultParser() {
        return new SimpleDocumentParser();
    }

    public static ChunkStrategy createDefaultChunkStrategy() {
        return new FixedSizeChunkStrategy(DEFAULT_CHUNK_SIZE);
    }

    public static EmbeddingModel createDefaultEmbeddingModel() {
        return new HashEmbeddingModel(DEFAULT_EMBEDDING_DIMENSION);
    }

    public static VectorStore createDefaultVectorStore() {
        return new InMemoryVectorStore(DEFAULT_VECTOR_NAMESPACE);
    }

    public static IngestionOrchestrator createDefaultIngestionOrchestrator(
            FetchExecutor fetchExecutor,
            Parser parser,
            ChunkStrategy chunkStrategy,
            EmbeddingModel embeddingModel,
            VectorStore vectorStore,
            IngestionAuditPublisher ingestionAuditPublisher
    ) {
        return new AuditingIngestionOrchestrator(
                new DefaultIngestionOrchestrator(
                        fetchExecutor,
                        parser,
                        chunkStrategy,
                        embeddingModel,
                        vectorStore
                ),
                ingestionAuditPublisher
        );
    }

    public static AgentRunner createDefaultAgentRunner(Memory memory, AuditLog auditLog) {
        return new DefaultAgentRunner(memory, auditLog);
    }

    public static ArgusRuntime createRuntime(
            Memory memory,
            AuditLog auditLog,
            FetchAuditPublisher fetchAuditPublisher,
            IngestionAuditPublisher ingestionAuditPublisher,
            FetchExecutorRegistry fetchExecutorRegistry,
            FetchExecutor fetchExecutor,
            IngestionOrchestrator ingestionOrchestrator,
            AgentRunner agentRunner
    ) {
        return new ArgusRuntime(
                memory,
                auditLog,
                fetchAuditPublisher,
                ingestionAuditPublisher,
                fetchExecutorRegistry,
                fetchExecutor,
                ingestionOrchestrator,
                agentRunner
        );
    }

} // Class end.