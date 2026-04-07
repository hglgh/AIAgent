package com.hgl.hglaiagent.optimize.agent.builder;

import com.hgl.hglaiagent.agent.ToolCallAgent;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.api.Advisor;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.tool.ToolCallback;

/**
 * Agent建造者
 *
 * <p>使用建造者模式简化Agent的创建过程。
 *
 * @author HGL
 * @since 2025/5/22
 */
public class AgentBuilder {

    // 必需参数
    private final String name;
    private final ChatModel chatModel;
    private ToolCallback[] tools;

    // 可选参数
    private String systemPrompt;
    private String nextStepPrompt;
    private int maxSteps = 10;
    private int maxSameToolCallCount = 3;
    private Object[] advisors;

    private AgentBuilder(String name, ChatModel chatModel) {
        this.name = name;
        this.chatModel = chatModel;
    }

    /**
     * 创建建造者
     */
    public static AgentBuilder create(String name, ChatModel chatModel) {
        return new AgentBuilder(name, chatModel);
    }

    /**
     * 设置工具
     */
    public AgentBuilder tools(ToolCallback... tools) {
        this.tools = tools;
        return this;
    }

    /**
     * 设置系统提示词
     */
    public AgentBuilder systemPrompt(String systemPrompt) {
        this.systemPrompt = systemPrompt;
        return this;
    }

    /**
     * 设置下一步提示词
     */
    public AgentBuilder nextStepPrompt(String nextStepPrompt) {
        this.nextStepPrompt = nextStepPrompt;
        return this;
    }

    /**
     * 设置最大步数
     */
    public AgentBuilder maxSteps(int maxSteps) {
        this.maxSteps = maxSteps;
        return this;
    }

    /**
     * 设置最大相同工具调用次数
     */
    public AgentBuilder maxSameToolCallCount(int maxSameToolCallCount) {
        this.maxSameToolCallCount = maxSameToolCallCount;
        return this;
    }

    /**
     * 设置顾问
     */
    public AgentBuilder advisors(Object... advisors) {
        this.advisors = advisors;
        return this;
    }

    /**
     * 构建ToolCallAgent
     */
    public ToolCallAgent build() {
        // 创建Agent
        ToolCallAgent agent = new ToolCallAgent(tools, maxSameToolCallCount);

        // 设置基本属性
        agent.setName(name);
        if (systemPrompt != null) {
            agent.setSystemPrompt(systemPrompt);
        }
        if (nextStepPrompt != null) {
            agent.setNextStepPrompt(nextStepPrompt);
        }
        agent.setMaxSteps(maxSteps);

        // 创建ChatClient
        ChatClient.Builder builder = ChatClient.builder(chatModel);
        if (advisors != null && advisors.length > 0) {
            builder.defaultAdvisors((Advisor[]) advisors);
        }
        agent.setChatClient(builder.build());

        return agent;
    }
}