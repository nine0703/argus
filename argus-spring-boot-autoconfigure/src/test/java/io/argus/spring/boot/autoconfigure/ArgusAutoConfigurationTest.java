package io.argus.spring.boot.autoconfigure;

import io.argus.agent.AgentRunner;
import io.argus.core.audit.AuditLog;
import io.argus.core.lifecycle.LifecyclePhase;
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
import io.argus.ingestion.fetch.replay.FetchRecordStore;
import io.argus.ingestion.fetch.replay.FileFetchRecordStore;
import io.argus.ingestion.orchestration.IngestionOrchestrator;
import io.argus.ingestion.parse.Parser;
import io.argus.ingestion.policy.FetchPolicy;
import io.argus.ingestion.policy.RobotsTxtService;
import io.argus.ingestion.source.IngestionSource;
import io.argus.runtime.ArgusRuntime;
import junit.framework.TestCase;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

/**
 * @author TK.ENDO
 * @since 2026-03-31
 */
public class ArgusAutoConfigurationTest extends TestCase {

    private final ApplicationContextRunner contextRunner =
            new ApplicationContextRunner()
                    .withConfiguration(AutoConfigurations.of(ArgusAutoConfiguration.class));

    public void testShouldAutoConfigureArgusRuntimeAndIngestionBeans() {

        contextRunner.run(context -> {
            ArgusRuntime runtime = context.getBean(ArgusRuntime.class);
            assertNotNull(runtime);
            assertNotNull(context.getBean(Memory.class));
            assertNotNull(context.getBean(AuditLog.class));
            assertNotNull(context.getBean(FetchAuditPublisher.class));
            assertNotNull(context.getBean(IngestionAuditPublisher.class));
            assertNotNull(context.getBean(FetchExecutorRegistry.class));
            assertNotNull(context.getBean(FetchRecordStore.class));
            assertNotNull(context.getBean(FetchPolicy.class));
            assertNotNull(context.getBean(FetchExecutor.class));
            assertNotNull(context.getBean(Parser.class));
            assertNotNull(context.getBean(ChunkStrategy.class));
            assertNotNull(context.getBean(EmbeddingModel.class));
            assertNotNull(context.getBean(VectorStore.class));
            assertNotNull(context.getBean(IngestionOrchestrator.class));
            assertNotNull(context.getBean(RobotsTxtService.class));
            assertNotNull(context.getBean(IngestionSource.class));
            assertNotNull(context.getBean(AgentRunner.class));
            assertNotNull(runtime.fetchRecordStore());
            assertNotNull(runtime.fetchPolicy());
            assertNotNull(runtime.ingestionAuditPublisher());
            assertNotNull(runtime.fetchExecutor());
            assertNotNull(runtime.ingestionOrchestrator());
            assertNotNull(runtime.ingestionSource());
            assertNotNull(runtime.agentRunner());
            assertEquals(LifecyclePhase.RUNNING, runtime.phase());
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

        String filePath = "D:/@DevCode/21-labs-demo-java/argus/target/fetch-record-store-test.bin";

        contextRunner
                .withPropertyValues(
                        "argus.ingestion.chunk-size=8",
                        "argus.ingestion.embedding-dimension=6",
                        "argus.ingestion.vector-namespace=docs",
                        "argus.fetch.replay-mode=HYBRID",
                        "argus.fetch.record-store.type=FILE",
                        "argus.fetch.record-store.file-path=" + filePath,
                        "argus.fetch.policy.allowed-protocols[0]=http",
                        "argus.fetch.policy.rate-limit=2s",
                        "argus.fetch.policy.obey-robots-txt=true",
                        "argus.fetch.policy.user-agent=argus-docs",
                        "argus.fetch.policy.robots-cache-ttl=30s"
                )
                .run(context -> {
                    FixedSizeChunkStrategy chunkStrategy =
                            (FixedSizeChunkStrategy) context.getBean(ChunkStrategy.class);
                    HashEmbeddingModel embeddingModel =
                            (HashEmbeddingModel) context.getBean(EmbeddingModel.class);
                    VectorStore vectorStore = context.getBean(VectorStore.class);
                    FetchPolicy fetchPolicy = context.getBean(FetchPolicy.class);
                    FetchRecordStore fetchRecordStore = context.getBean(FetchRecordStore.class);

                    assertEquals(8, chunkStrategy.chunkSize());
                    assertEquals(6, embeddingModel.dimension());
                    assertEquals("docs", vectorStore.namespace());
                    assertTrue(fetchPolicy.allowedProtocols().contains("http"));
                    assertEquals(2000L, fetchPolicy.rateLimitPolicy().minInterval().toMillis());
                    assertTrue(fetchPolicy.robotPolicy().obeyRobotsTxt());
                    assertEquals("argus-docs", fetchPolicy.robotPolicy().userAgent());
                    assertTrue(fetchRecordStore instanceof FileFetchRecordStore);
                    assertEquals(
                            "D:\\@DevCode\\21-labs-demo-java\\argus\\target\\fetch-record-store-test.bin",
                            ((FileFetchRecordStore) fetchRecordStore).filePath().toString()
                    );
                });
    }

    public void testShouldRejectInvalidIngestionProperties() {

        contextRunner
                .withPropertyValues("argus.ingestion.chunk-size=0")
                .run(context -> assertNotNull(context.getStartupFailure()));
    }

} // Class end.