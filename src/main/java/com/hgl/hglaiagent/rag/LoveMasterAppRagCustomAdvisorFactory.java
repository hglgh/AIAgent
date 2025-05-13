package com.hgl.hglaiagent.rag;

import com.hgl.hglaiagent.advisor.MyLoggerAdvisor;
import org.springframework.ai.chat.client.advisor.RetrievalAugmentationAdvisor;
import org.springframework.ai.chat.client.advisor.api.Advisor;
import org.springframework.ai.rag.retrieval.search.DocumentRetriever;
import org.springframework.ai.rag.retrieval.search.VectorStoreDocumentRetriever;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.filter.FilterExpressionBuilder;

/**
 * ClassName: LoveMasterAppRagCustomAdvisorFactory
 * Package: com.hgl.hglaiagent.rag
 * Description:
 *
 * @Author HGL
 * @Create: 2025/5/12 17:51
 * @Desription: 创建自定义RAG的检索增强顾问的工厂
 */
public class LoveMasterAppRagCustomAdvisorFactory {

    public static Advisor createLoveAppRagCustomAdvisor(VectorStore vectorStore,String status) {
        //定义基于向量数据库的一个文档检索器
        DocumentRetriever vectorStoreDocumentRetriever = VectorStoreDocumentRetriever.builder()
                .vectorStore(vectorStore)
                //过滤条件(过滤特定状态的文档)
                .filterExpression(new FilterExpressionBuilder().eq("status", status).build())
                //相似度阈值
                .similarityThreshold(0.5)
                //返回数量
                .topK(3)
                .build();

        return RetrievalAugmentationAdvisor.builder()
                .documentRetriever(vectorStoreDocumentRetriever)
                .queryAugmenter(LoveMasterAppContextualQueryAugmenterFactory.createLoveMasterAppContextualQueryAugmenter())
                .build();
    }
}
