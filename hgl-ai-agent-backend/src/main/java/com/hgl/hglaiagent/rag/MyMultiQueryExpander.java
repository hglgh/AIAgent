package com.hgl.hglaiagent.rag;

import jakarta.annotation.Resource;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.rag.Query;
import org.springframework.ai.rag.preretrieval.query.expansion.MultiQueryExpander;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * ClassName: MyMultiQueryExpander
 * Package: com.hgl.hglaiagent.rag
 * Description:
 *
 * @Author HGL
 * @Create: 2025/5/12 16:38
 * @Desription: 多查询扩展器
 */
@Component
public class MyMultiQueryExpander {

    private final ChatClient.Builder chatClientBuilder;

    public MyMultiQueryExpander(ChatModel dashscopeChatModel) {
        this.chatClientBuilder = ChatClient.builder(dashscopeChatModel);
    }

    public List<Query> expand(String query) {
        MultiQueryExpander queryExpander = MultiQueryExpander.builder()
                .chatClientBuilder(chatClientBuilder)
                .numberOfQueries(3)
                .build();

        return queryExpander.expand(new Query(query));
    }
}
