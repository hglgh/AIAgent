package com.hgl.hglaiagent.rag;

import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.rag.generation.augmentation.ContextualQueryAugmenter;

/**
 * ClassName: LoveMasterAppContextualQueryAugmenterFactory
 * Package: com.hgl.hglaiagent.rag
 * Description:
 *
 * @Author HGL
 * @Create: 2025/5/13 10:10
 * @Desription: 创建上下文查询增强器的工厂
 */
public class LoveMasterAppContextualQueryAugmenterFactory {
    public static ContextualQueryAugmenter createLoveMasterAppContextualQueryAugmenter() {
        PromptTemplate emptyContextPromptTemplate = new PromptTemplate("""
                你应该输出下面的内容：
                抱歉，我只能回答恋爱相关的问题，别的没办法帮到您哦，
                有问题可以联系编程导航客服 https://codefather.cn
                """);
        return ContextualQueryAugmenter.builder()
                //  不允许空上下文
                .allowEmptyContext(false)
                // 空上下文提示语
                .emptyContextPromptTemplate(emptyContextPromptTemplate)
                .build();
    }
}
