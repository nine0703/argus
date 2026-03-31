package io.argus.spring.boot.autoconfigure;

import io.argus.agent.AgentRunner;
import io.argus.core.audit.AuditLog;
import io.argus.core.memory.InMemoryMemory;
import io.argus.core.memory.Memory;
import io.argus.ingestion.audit.IngestionAuditPublisher;
import io.argus.ingestion.audit.fetch.FetchAuditPublisher;
import io.argus.ingestion.domain.chunk.ChunkStrategy;
import io.argus.ingestion.domain.chunk.FixedSizeChunkStrategy;
import io.argus.ingestion.domain.embedding.EmbeddingModel;
import io.argus.ingestion.domain.embedding.HashEmbeddingModel;
import io.argus.ingestion.domain.vector.VectorStore;
import io.argus.ingestion.fetch.FetchExecutor;
import io.argus.ingestion.fetch.FetchExecutorRegistry;
import io.argus.ingestion.orchestration.IngestionOrchestrator;
import io.argus.ingestion.parse.Parser;
import io.argus.runtime.ArgusRuntime;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

import junit.framework.TestCase;

/**
 * @author TK.ENDO
 * @since 2026-03-31 周二 17:09
 */
public class ArgusAutoConfigurationTest extends TestCase {

    private final ApplicationContextRunner contextRunner =
            new ApplicationContextRunner()
                    .withConfiguration(AutoConfigurations.of(ArgusAutoConfiguration.class));

    public void testShouldAutoConfigureArgusRuntimeAndIngestionBeans() {

        contextRunner.run(context -> {
            assertNotNull(context.getBean(ArgusRuntime.class));
            assertNotNull(context.getBean(Memory.class));
            assertNotNull(context.getBean(AuditLog.class));
            assertNotNull(context.getBean(FetchAuditPublisher.class));
            assertNotNull(context.getBean(IngestionAuditPublisher.class));
            assertNotNull(context.getBean(FetchExecutorRegistry.class));
            assertNotNull(context.getBean(FetchExecutor.class));
            assertNotNull(context.getBean(Parser.class));
            assertNotNull(context.getBean(ChunkStrategy.class));
            assertNotNull(context.getBean(EmbeddingModel.class));
            assertNotNull(context.getBean(VectorStore.class));
            assertNotNull(context.getBean(IngestionOrchestrator.class));
            assertNotNull(context.getBean(AgentRunner.class));
            assertNotNull(context.getBean(ArgusRuntime.class).agentRunner());
        });
    }

    public void testShouldBackOffWhenDisabled() {

        contextRunner
                .withPropertyValues("argus.enabled=false")
                .run(context -> assertFalse(context.containsBean("argusRuntime")));
    }

    public void testShouldReuseUserProvidedMemoryBean() {

        InMemoryMemory memory = new InMemoryMemory();

        contextRunner
                .withBean(Memory.class, () -> memory)
                .run(context -> {
                    assertSame(memory, context.getBean(Memory.class));
                    assertSame(memory, context.getBean(ArgusRuntime.class).memory());
                });
    }

    public void testShouldBindIngestionProperties() {

        contextRunner
                .withPropertyValues(
                        "argus.ingestion.chunk-size=8",
                        "argus.ingestion.embedding-dimension=6",
                        "argus.ingestion.vector-namespace=docs"
                )
                .run(context -> {
                    FixedSizeChunkStrategy chunkStrategy =
                            (FixedSizeChunkStrategy) context.getBean(ChunkStrategy.class);
                    HashEmbeddingModel embeddingModel =
                            (HashEmbeddingModel) context.getBean(EmbeddingModel.class);
                    VectorStore vectorStore = context.getBean(VectorStore.class);

                    assertEquals(8, chunkStrategy.chunkSize());
                    assertEquals(6, embeddingModel.dimension());
                    assertEquals("docs", vectorStore.namespace());
                });
    }

} // Class end.