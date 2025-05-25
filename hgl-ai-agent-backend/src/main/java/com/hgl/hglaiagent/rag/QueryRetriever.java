package com.hgl.hglaiagent.rag;

import jakarta.annotation.Resource;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.document.Document;
import org.springframework.ai.rag.Query;
import org.springframework.ai.rag.preretrieval.query.transformation.QueryTransformer;
import org.springframework.ai.rag.preretrieval.query.transformation.RewriteQueryTransformer;
import org.springframework.ai.rag.retrieval.search.VectorStoreDocumentRetriever;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.filter.FilterExpressionBuilder;
import org.springframework.ai.vectorstore.pgvector.PgVectorStore;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * ClassName: QueryRetriever
 * Package: com.hgl.hglaiagent.rag
 * Description:
 *
 * @Author HGL
 * @Create: 2025/5/12 17:13
 * @Desription: 查询重写器
 */
@Component
public class QueryRetriever {

    private final QueryTransformer queryTransformer;

    public QueryRetriever(ChatModel dashscopeChatModel){
        ChatClient.Builder builder = ChatClient.builder(dashscopeChatModel);
        //创建查询重写转换器
        this.queryTransformer = RewriteQueryTransformer.builder()
                .chatClientBuilder(builder)
                .build();
    }

    /**
     * 执行查询重写
     * @param prompt 原始查询
     * @return 重写后的查询
     */
    public String doQueryRewrite(String prompt) {
        Query query = new Query(prompt);
        //执行查询重写
        Query transformedQuery = queryTransformer.transform(query);
        //返回重写后的查询
        return transformedQuery.text();
    }
}
