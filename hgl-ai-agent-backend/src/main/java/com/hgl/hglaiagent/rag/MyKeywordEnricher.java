package com.hgl.hglaiagent.rag;

import jakarta.annotation.Resource;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.document.Document;
import org.springframework.ai.transformer.KeywordMetadataEnricher;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * ClassName: MyKeywordEnricher
 * Package: com.hgl.hglaiagent.rag
 * Description:
 *
 * @Author HGL
 * @Create: 2025/5/12 15:50
 * @Desription: 基于 AI 的文档元数据增强器(为文档补充元数据)
 */
@Component
public class MyKeywordEnricher {

    @Resource
    private ChatModel dashscopeChatModel;

    public List<Document> enrichDoucment(List<Document> documents){
        KeywordMetadataEnricher keywordMetadataEnricher = new KeywordMetadataEnricher(dashscopeChatModel, 5);
        return keywordMetadataEnricher.apply(documents);
    }
}
