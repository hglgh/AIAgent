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
 * @Desription:
    该方法用于创建并配置一个向量存储（VectorStore），流程如下：
    <ol>
    <li>创建一个基于嵌入模型的简单向量存储对象。</li>
    <li>使用文档加载器加载Markdown格式的文档列表。</li>
    <li>将加载到的文档添加到向量存储中。</li>
    <li>返回配置好的向量存储对象。</li>
    </ol>
 */
@Configuration
public class LoveMasterAppVectorStoreConfig {

    @Resource
    private LoveMasterAppDocumentLoader loveMasterAppDocumentLoader;

    @Resource
    private MyTokenTextSplitter  myTokenTextSplitter;

    @Resource
    private MyKeywordEnricher myKeywordEnricher;

    @Bean
    public VectorStore loveMasterAppVectorStore(EmbeddingModel dashscopeEmbeddingModel) {
        SimpleVectorStore vectorStore = SimpleVectorStore.builder(dashscopeEmbeddingModel).build();
        // 加载文档
        List<Document> documents = loveMasterAppDocumentLoader.loadMarkdownDocuments();
        //自主分割
//        List<Document> splitCustomizedList = myTokenTextSplitter.splitCustomized(documents);
        // 关键词提取
        List<Document> enrichedDoucments = myKeywordEnricher.enrichDoucment(documents);
        vectorStore.add(enrichedDoucments);
        return vectorStore;
    }
}
