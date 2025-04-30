package com.hgl.hglaiagent.rag;

import jakarta.annotation.Resource;
import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * ClassName: LoveMasterAppVectorStoreConfig
 * Package: com.hgl.hglaiagent.rag
 * Description:
 *
 * @Author HGL
 * @Create: 2025/4/30 15:07
 */
@Configuration
public class LoveMasterAppVectorStoreConfig {

    @Resource
    private LoveMasterAppDocumentLoader loveMasterAppDocumentLoader;

    @Bean
    public VectorStore loveMasterAppVectorStore(EmbeddingModel dashscopeEmbeddingModel) {
        SimpleVectorStore vectorStore = SimpleVectorStore.builder(dashscopeEmbeddingModel).build();
        // 加载文档
        List<Document> documents = loveMasterAppDocumentLoader.loadMarkdownDocuments();
        vectorStore.add(documents);
        return vectorStore;
    }
}
