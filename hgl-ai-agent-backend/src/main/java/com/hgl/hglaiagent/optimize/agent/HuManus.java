package com.hgl.hglaiagent.optimize.agent;

import com.hgl.hglaiagent.advisor.MyLoggerAdvisor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.stereotype.Component;

/**
 * HuManus 智能助手
 * 
 * <p>一个具备工具调用能力的全能AI助手，能够解决用户提出的各种任务。
 * 
 * <p>特点：
 * <ul>
 *   <li>支持多种工具调用</li>
 *   <li>自动选择最合适的工具</li>
 *   <li>能够分解复杂任务</li>
 *   <li>支持主动终止交互</li>
 * </ul>
 *
 * @author HGL
 * @since 2025/5/22
 */
@Slf4j
@Component
public class HuManus extends ToolCallAgent {

    /**
     * 系统提示词
     */
    private static final String SYSTEM_PROMPT = """
            You are HuManus, an all-capable AI assistant, aimed at solving any task presented by the user.
            You have various tools at your disposal that you can call upon to efficiently complete complex requests.
            Always think step by step and explain your reasoning.
            Be proactive in using tools when necessary.
            If a task is complete, use the terminate tool to end the interaction.
            """;

    /**
     * 下一步提示词
     */
    private static final String NEXT_STEP_PROMPT = """
            Based on user needs, proactively select the most appropriate tool or combination of tools.
            For complex tasks, you can break down the problem and use different tools step by step to solve it.
            After using each tool, clearly explain the execution results and suggest the next steps.
            If you want to stop the interaction at any point, use the `terminate` tool/function call.
            """;

    /**
     * 最大执行步数
     */
    private static final int MAX_STEPS = 20;

    /**
     * 构造方法
     *
     * @param allTools 所有可用工具
     * @param dashscopeChatModel 聊天模型
     */
    public HuManus(ToolCallback[] allTools, ChatModel dashscopeChatModel) {
        super(allTools);
        initializeAgent(dashscopeChatModel);
    }

    /**
     * 初始化Agent配置
     */
    private void initializeAgent(ChatModel chatModel) {
        // 设置基本信息
        setName("HuManus");
        setSystemPrompt(SYSTEM_PROMPT);
        setNextStepPrompt(NEXT_STEP_PROMPT);
        setMaxSteps(MAX_STEPS);
        
        // 初始化聊天客户端
        ChatClient chatClient = ChatClient.builder(chatModel)
                .defaultAdvisors(new MyLoggerAdvisor())
                .build();
        setChatClient(chatClient);
        
        log.info("HuManus agent initialized with {} tools", 
            getAvailableTools() != null ? getAvailableTools().length : 0);
    }
}