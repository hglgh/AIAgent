package com.hgl.hglaiagent.app;

import com.hgl.hglaiagent.advisor.MyLoggerAdvisor;
import com.hgl.hglaiagent.advisor.ProhibitedWordsAdvisor;
import com.hgl.hglaiagent.chatmemory.FileBasedChatMemory;
import com.hgl.hglaiagent.chatmemory.RedisBasedChatMemory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.InMemoryChatMemory;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

import static org.springframework.ai.chat.client.advisor.AbstractChatMemoryAdvisor.CHAT_MEMORY_CONVERSATION_ID_KEY;
import static org.springframework.ai.chat.client.advisor.AbstractChatMemoryAdvisor.CHAT_MEMORY_RETRIEVE_SIZE_KEY;

/**
 * ClassName: LoveMasterApp
 * Package: com.hgl.hglaiagent.app
 * Description:
 *
 * @Author HGL
 * @Create: 2025/4/28 15:00
 */
@Slf4j
@Component
public class LoveMasterApp {

    private final ChatClient chatClient;

    private static final String SYSTEM_PROMPT = """
            扮演深耕恋爱心理领域的专家。开场向用户表明身份，告知用户可倾诉恋爱难题。围绕单身、恋爱、已婚三种状态提问：单身状态询问社交圈拓展及追求心仪对象的困扰；恋爱状态询问沟通、习惯差异引发的矛盾；已婚状态询问家庭责任与亲属关系处理的问题。引导用户详述事情经过、对方反应及自身想法，以便给出专属解决方案。
            """;


    /**
     * 初始化对话客户端
     *
     * @param dashscopeChatModel 模型
     */
    public LoveMasterApp(ChatModel dashscopeChatModel, RedisTemplate<String, String> redisTemplate) {
        //初始化基于内存的对话记忆
        InMemoryChatMemory chatMemory = new InMemoryChatMemory();
        //初始化基于文件持久化的对话记忆
//        String dir = System.getProperty("user.dir")+ "/temp/chat-memory";
//        FileBasedChatMemory fileBasedChatMemory = new FileBasedChatMemory(dir);
        //初始化基于Redis的持久化对话记忆
//        RedisBasedChatMemory redisBasedChatMemory = new RedisBasedChatMemory(redisTemplate);
        this.chatClient = ChatClient.builder(dashscopeChatModel)
                .defaultSystem(SYSTEM_PROMPT)
                .defaultAdvisors(
                        new MessageChatMemoryAdvisor(chatMemory),
                        // 自定义日志 Advisor，可按需开启
                        new MyLoggerAdvisor()
                        // 自定义推理增强 Advisor，可按需开启
//                        ,new ReReadingAdvisor()
                        // 自定义违禁词 Advisor，可按需开启
//                        ,new ProhibitedWordsAdvisor()
                ).build();
    }

    /**
     * AI 基础对话(支持多轮对话记忆)
     *
     * @param message 用户输入
     * @param chatId  会话ID
     * @return AI输出
     */
    public String doChat(String message, String chatId) {
        /*
                String content = chatClient.prompt()
                .user(message)
                .advisors(advisorSpec -> advisorSpec
                        .param(CHAT_MEMORY_CONVERSATION_ID_KEY, chatId)
                        .param(CHAT_MEMORY_RETRIEVE_SIZE_KEY, 10))
                .call().content();
         */
        ChatResponse chatResponse = chatClient.prompt()
                .user(message)
                .advisors(advisorSpec -> advisorSpec
                        .param(CHAT_MEMORY_CONVERSATION_ID_KEY, chatId)
                        .param(CHAT_MEMORY_RETRIEVE_SIZE_KEY, 10))
                .call().chatResponse();
        assert chatResponse != null;
        String content = chatResponse.getResult().getOutput().getText();
        log.info("AI: {}", content);
        return content;
    }

    record LoveReport(String title, List<String> suggestions) {
    }
    /**
     * AI 恋爱报告功能(实战结构化输出)
     *
     * @param message 用户输入
     * @param chatId  会话ID
     * @return AI输出
     */
    public LoveReport doChatWithReport(String message, String chatId){
        //初始化基于Redis持久化的对话记忆
        LoveReport loveReport = chatClient.prompt()
                .system(SYSTEM_PROMPT + "每次对话后都要生成恋爱结果，标题为{用户名}的恋爱报告，内容为建议列表")
                .user(message)
                .advisors(advisorSpec -> advisorSpec
                        .param(CHAT_MEMORY_CONVERSATION_ID_KEY, chatId)
                        .param(CHAT_MEMORY_RETRIEVE_SIZE_KEY, 10))
                .call()
                .entity(LoveReport.class);
        return loveReport;
    }
}
