package com.hgl.hglaiagent.agent;

import cn.hutool.core.collection.CollUtil;
import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatOptions;
import com.hgl.hglaiagent.agent.model.enums.AgentStateEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.ToolResponseMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.model.tool.ToolCallingManager;
import org.springframework.ai.model.tool.ToolExecutionResult;
import org.springframework.ai.tool.ToolCallback;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * ClassName: ToolCallAgent
 * Package: com.hgl.hglaiagent.agent
 * Description:
 *
 * @Author HGL
 * @Create: 2025/5/22 15:01
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Slf4j
public class ToolCallAgent extends ReActAgent {

    //可用的工具
    private ToolCallback[] availableTools;

    // 保存了工具调用信息的响应
    private ChatResponse toolCallChatResponse;

    // 工具调用管理者
    private final ToolCallingManager toolCallingManager;

    // 禁用内置的工具调用机制，自己维护上下文
    private final ChatOptions chatOptions;

    /**
     * 上次调用的工具名称
     */
    private String lastToolName;

    /**
     * 连续调用相同工具的次数
     */
    private int sameToolCallCount = 0;

    /**
     * 用户设置的最大连续调用相同工具次数
     */
    private final int maxSameToolCallCount;


    public ToolCallAgent(ToolCallback[] availableTools, int maxSameToolCallCount) {
        super();
        this.availableTools = availableTools;
        this.toolCallingManager = ToolCallingManager.builder().build();
        this.maxSameToolCallCount = maxSameToolCallCount;
        // 禁用 Spring AI 内置的工具调用机制，自己维护选项和消息上下文
        this.chatOptions = DashScopeChatOptions.builder()
                .withProxyToolCalls(true)
                .build();
    }

    public ToolCallAgent(ToolCallback[] availableTools) {
        // 默认为3次
        this(availableTools, 3);
    }


    /**
     * 处理当前状态并决定下一步行动
     *
     * @return 是否需要执行行动
     */
    @Override
    public boolean think() {
        if (super.getNextStepPrompt() != null && !super.getNextStepPrompt().isEmpty()) {
            UserMessage userMessage = new UserMessage(super.getNextStepPrompt());
            super.getMessageList().add(userMessage);
        }
        List<Message> messageList = super.getMessageList();
        Prompt prompt = new Prompt(messageList, chatOptions);
        try {
            //获取带工具选项的响应
            ChatResponse chatResponse = super.getChatClient()
                    .prompt(prompt)
                    .system(super.getSystemPrompt())
                    .tools(availableTools)
                    .call()
                    .chatResponse();
            // 记录响应，用于 Act
            this.toolCallChatResponse = chatResponse;
            AssistantMessage assistantMessage = chatResponse.getResult().getOutput();
            //输出提示信息
            String result = assistantMessage.getText();
            List<AssistantMessage.ToolCall> toolCallList = assistantMessage.getToolCalls();
            log.info("{} 的思考：{}", super.getName(), result);
            log.info("{} 选择了 {} 个工具来使用", super.getName(), toolCallList.size());
            String toolCallInfo = toolCallList.stream()
                    .map(toolCall -> String.format("工具名称：%s，参数：%s", toolCall.name(), toolCall.arguments()))
                    .collect(Collectors.joining("\n"));
            log.info("{} 的工具调用信息：\n{}", super.getName(), toolCallInfo);
            if (toolCallList.isEmpty()) {
                // 只有不调用工具时，才记录助手消息
                super.getMessageList().add(assistantMessage);
                return false;
            } else {
                // 需要调用工具时，无需记录助手消息，因为调用工具时会自动记录
                return true;
            }
        } catch (Exception e) {
            log.error("{}的思考过程遇到了问题: {}", getName(), e.getMessage());
            getMessageList().add(
                    new AssistantMessage("处理时遇到错误: " + e.getMessage()));
            return false;
        }
    }

    /**
     * 执行工具调用并处理结果
     *
     * @return 执行结果
     */
    @Override
    public String act() {
        if (!toolCallChatResponse.hasToolCalls()) {
            return "没有工具调用";
        }
        //调用工具
        Prompt prompt = new Prompt(super.getSystemPrompt(), chatOptions);
        ToolExecutionResult toolExecutionResult = toolCallingManager.executeToolCalls(prompt, toolCallChatResponse);
        // 获取当前调用的所有工具
        List<String> currentToolNames = toolExecutionResult.conversationHistory().stream()
                .filter(message -> message instanceof ToolResponseMessage)
                .map(message -> ((ToolResponseMessage) message).getResponses().stream()
                        .map(ToolResponseMessage.ToolResponse::name)
                        .findFirst()
                        .orElse(null))
                .filter(Objects::nonNull)
                .toList();

        // 判断是否只调用了一个工具
        if (currentToolNames.size() == 1) {
            String currentToolName = currentToolNames.getFirst();

            if (currentToolName.equals(lastToolName)) {
                sameToolCallCount++;
            } else {
                lastToolName = currentToolName;
                sameToolCallCount = 1;
            }

            // 如果连续调用相同工具超过3次
            if (sameToolCallCount >= maxSameToolCallCount) {
                log.warn("{} 连续调用了 {} 次相同的工具：{}", super.getName(), sameToolCallCount, currentToolName);
                super.setState(AgentStateEnum.FINISHED);
                return "检测到连续调用相同工具超过" + maxSameToolCallCount + "次，已主动终止流程。";
            }
        } else {
            // 非单一工具调用，重置计数器
            lastToolName = null;
            sameToolCallCount = 0;
        }

        // 记录消息上下文，conversationHistory 已经包含了助手消息和工具调用返回的结果
        super.setMessageList(toolExecutionResult.conversationHistory());
        // 当前工具调用的结果
        ToolResponseMessage toolResponseMessage = (ToolResponseMessage) CollUtil.getLast(toolExecutionResult.conversationHistory());
        String results = toolResponseMessage.getResponses().stream()
                .map(toolResponse -> String.format("工具 %s 完成了它的任务！结果: %s", toolResponse.name(), toolResponse.responseData()))
                .collect(Collectors.joining("\n"));
        boolean terminateToolCalled = toolResponseMessage.getResponses().stream()
                .anyMatch(toolResponse -> "doTerminate".equals(toolResponse.name()));
        if (terminateToolCalled) {
            log.info("{} 停止了工具调用", super.getName());
            super.setState(AgentStateEnum.FINISHED);
        }
        log.info(results);
        return results;
    }

    @Override
    protected void cleanup() {
        super.cleanup();
    }
}
