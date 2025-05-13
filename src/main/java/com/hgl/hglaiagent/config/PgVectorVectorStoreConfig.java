package com.hgl.hglaiagent.config;

import com.hgl.hglaiagent.rag.LoveMasterAppDocumentLoader;
import jakarta.annotation.Resource;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.rag.preretrieval.query.transformation.RewriteQueryTransformer;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.pgvector.PgVectorStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;

import static org.springframework.ai.vectorstore.pgvector.PgVectorStore.PgDistanceType.COSINE_DISTANCE;
import static org.springframework.ai.vectorstore.pgvector.PgVectorStore.PgIndexType.HNSW;

/**
 * ClassName: PgVectorVectorStoreConfig
 * Package: com.hgl.hglaiagent.config
 * Description:
 *
 * @Author HGL
 * @Create: 2025/5/9 14:20
 * @Desription: 通过配置pgVector将文档向量化存储在数据库中
 */
//@Configuration
public class PgVectorVectorStoreConfig {

    @Resource
    LoveMasterAppDocumentLoader loveMasterAppDocumentLoader;

    @Bean
    public VectorStore pgVectorVectorStore(JdbcTemplate jdbcTemplate, EmbeddingModel dashscopeEmbeddingModel) {
        /*
        Optional: defaults to model dimensions or 1536
        Optional: defaults to COSINE_DISTANCE
        Optional: defaults to HNSW
        Optional: defaults to false
        Optional: defaults to "public"
        Optional: defaults to "vector_store"
        Optional: defaults to 10000
        */
        VectorStore pgVectorStore = PgVectorStore.builder(jdbcTemplate, dashscopeEmbeddingModel)
                .dimensions(1536)
                .distanceType(COSINE_DISTANCE)
                .indexType(HNSW)
                .initializeSchema(true)
                .schemaName("public")
                .vectorTableName("vector_store")
                .maxDocumentBatchSize(10000)
                .build();
        List<Document> documents = loveMasterAppDocumentLoader.loadMarkdownDocuments();
        //DashScope Embedding API 的限制：一次最多只能处理 25 条文本输入。
        // 按每 25 条文档一批，进行分批次插入
        int batchSize = 25;
        for (int i = 0; i < documents.size(); i += batchSize) {
            int end = Math.min(i + batchSize, documents.size());
            List<Document> subList = documents.subList(i, end);
            // 分批插入
            pgVectorStore.add(subList);
        }
        return pgVectorStore;
    }
}
