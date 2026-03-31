package io.argus.spring.boot.autoconfigure;

import io.argus.core.audit.AuditLog;
import io.argus.core.memory.Memory;
import io.argus.ingestion.audit.AuditingIngestionOrchestrator;
import io.argus.ingestion.audit.IngestionAuditPublisher;
import io.argus.ingestion.audit.fetch.FetchAuditPublisher;
import io.argus.ingestion.domain.chunk.ChunkStrategy;
import io.argus.ingestion.domain.chunk.FixedSizeChunkStrategy;
import io.argus.ingestion.domain.embedding.EmbeddingModel;
import io.argus.ingestion.domain.embedding.HashEmbeddingModel;
import io.argus.ingestion.domain.vector.InMemoryVectorStore;
import io.argus.ingestion.domain.vector.VectorStore;
import io.argus.ingestion.fetch.FetchExecutor;
import io.argus.ingestion.fetch.FetchExecutorRegistry;
import io.argus.ingestion.fetch.RegistryBackedFetchExecutor;
import io.argus.ingestion.orchestration.DefaultIngestionOrchestrator;
import io.argus.ingestion.orchestration.IngestionOrchestrator;
import io.argus.ingestion.parse.Parser;
import io.argus.ingestion.parse.SimpleDocumentParser;
import io.argus.runtime.ArgusRuntime;
import io.argus.runtime.ArgusRuntimeFactory;
import io.argus.runtime.AuditLogBackedIngestionAuditPublisher;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

/**
 * Spring Boot auto-configuration for the ARGUS runtime stack.
 *
 * <p>
 * This module is the Spring integration boundary for the framework-agnostic
 * runtime and ingestion layers. The goal is starter-style activation:
 * importing the starter should yield a coherent local runtime plus a usable
 * default ingestion pipeline without manual bean assembly.
 *
 * <p>
 * Every bean in this configuration backs off cleanly so applications can
 * replace individual infrastructure parts while still reusing the remaining
 * defaults.
 *
 * @author TK.ENDO
 * @since 2026-03-31 周二 17:08
 */
@AutoConfiguration
@ConditionalOnClass(ArgusRuntime.class)
@EnableConfigurationProperties(ArgusProperties.class)
@ConditionalOnProperty(prefix = "argus", name = "enabled", havingValue = "true", matchIfMissing = true)
public class ArgusAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public Memory argusMemory() {
        return ArgusRuntimeFactory.createDefaultMemory();
    }

    @Bean
    @ConditionalOnMissingBean
    public AuditLog argusAuditLog() {
        return ArgusRuntimeFactory.createDefaultAuditLog();
    }

    @Bean
    @ConditionalOnMissingBean
    public FetchAuditPublisher argusFetchAuditPublisher(AuditLog auditLog) {
        return ArgusRuntimeFactory.createDefaultFetchAuditPublisher(auditLog);
    }

    @Bean
    @ConditionalOnMissingBean
    public IngestionAuditPublisher argusIngestionAuditPublisher(AuditLog auditLog) {
        return new AuditLogBackedIngestionAuditPublisher(auditLog);
    }

    @Bean
    @ConditionalOnMissingBean
    public FetchExecutorRegistry argusFetchExecutorRegistry(FetchAuditPublisher publisher) {
        return ArgusRuntimeFactory.createDefaultFetchExecutorRegistry(publisher);
    }

    @Bean
    @ConditionalOnMissingBean
    public FetchExecutor argusFetchExecutor(FetchExecutorRegistry registry) {
        return new RegistryBackedFetchExecutor(registry);
    }

    @Bean
    @ConditionalOnMissingBean
    public Parser argusParser() {
        return new SimpleDocumentParser();
    }

    @Bean
    @ConditionalOnMissingBean
    public ChunkStrategy argusChunkStrategy(ArgusProperties properties) {
        return new FixedSizeChunkStrategy(properties.getIngestion().getChunkSize());
    }

    @Bean
    @ConditionalOnMissingBean
    public EmbeddingModel argusEmbeddingModel(ArgusProperties properties) {
        return new HashEmbeddingModel(properties.getIngestion().getEmbeddingDimension());
    }

    @Bean
    @ConditionalOnMissingBean
    public VectorStore argusVectorStore(ArgusProperties properties) {
        return new InMemoryVectorStore(properties.getIngestion().getVectorNamespace());
    }

    @Bean
    @ConditionalOnMissingBean
    public IngestionOrchestrator argusIngestionOrchestrator(
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

    @Bean
    @ConditionalOnMissingBean
    public ArgusRuntime argusRuntime(
            Memory memory,
            AuditLog auditLog,
            FetchAuditPublisher fetchAuditPublisher,
            FetchExecutorRegistry fetchExecutorRegistry
    ) {
        return ArgusRuntimeFactory.createRuntime(
                memory,
                auditLog,
                fetchAuditPublisher,
                fetchExecutorRegistry
        );
    }

} // Class end.