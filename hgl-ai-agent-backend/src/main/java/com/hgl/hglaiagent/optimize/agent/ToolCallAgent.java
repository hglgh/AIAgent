package com.hgl.hglaiagent.optimize.agent;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatOptions;
import com.hgl.hglaiagent.optimize.agent.constant.AgentConstants;
import com.hgl.hglaiagent.optimize.agent.exception.ToolCallException;
import com.hgl.hglaiagent.optimize.agent.model.enums.AgentStateEnum;
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
 * 工具调用代理
 * 
 * <p>基于ReAct模式，增加了工具调用能力的智能体。
 * 
 * <p>核心特性：
 * <ul>
 *   <li>自动调用LLM决策是否需要使用工具</li>
 *   <li>支持多工具并行调用</li>
 *   <li>防止工具死循环（相同工具连续调用限制）</li>
 *   <li>支持主动终止机制</li>
 * </ul>
 *
 * @author HGL
 * @since 2025/5/22
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Slf4j
public class ToolCallAgent extends ReActAgent {

    // ==================== 核心属性 ====================
    
    /**
     * 可用工具列表
     */
    private final ToolCallback[] availableTools;
    
    /**
     * 工具调用管理器
     */
    private final ToolCallingManager toolCallingManager;
    
    /**
     * 聊天选项（禁用内置工具调用机制）
     */
    private final ChatOptions chatOptions;
    
    /**
     * 保存了工具调用信息的响应
     */
    private ChatResponse toolCallChatResponse;

    // ==================== 防死循环机制 ====================
    
    /**
     * 上次调用的工具名称
     */
    private String lastToolName;
    
    /**
     * 连续调用相同工具的次数
     */
    private int sameToolCallCount = 0;
    
    /**
     * 最大连续调用相同工具次数
     */
    private final int maxSameToolCallCount;

    // ==================== 构造方法 ====================

    /**
     * 构造方法
     *
     * @param availableTools 可用工具列表
     * @param maxSameToolCallCount 最大相同工具连续调用次数
     */
    public ToolCallAgent(ToolCallback[] availableTools, int maxSameToolCallCount) {
        super();
        this.availableTools = availableTools;
        this.toolCallingManager = ToolCallingManager.builder().build();
        this.maxSameToolCallCount = maxSameToolCallCount;
        
        // 禁用 Spring AI 内置的工具调用机制，自己维护消息上下文
        this.chatOptions = DashScopeChatOptions.builder()
                .withProxyToolCalls(true)
                .build();
    }

    /**
     * 构造方法（使用默认最大相同工具调用次数）
     *
     * @param availableTools 可用工具列表
     */
    public ToolCallAgent(ToolCallback[] availableTools) {
        this(availableTools, AgentConstants.DEFAULT_MAX_SAME_TOOL_CALL_COUNT);
    }

    // ==================== 核心方法 ====================

    /**
     * 思考：调用LLM分析是否需要使用工具
     *
     * @return true表示需要调用工具，false表示无需行动
     */
    @Override
    public boolean think() {
        // 添加下一步提示
        addNextStepPrompt();
        
        // 构建Prompt
        Prompt prompt = buildPrompt();
        
        try {
            // 调用LLM
            ChatResponse chatResponse = callLLM(prompt);
            
            // 记录响应
            this.toolCallChatResponse = chatResponse;
            
            // 解析响应
            AssistantMessage assistantMessage = chatResponse.getResult().getOutput();
            List<AssistantMessage.ToolCall> toolCalls = assistantMessage.getToolCalls();
            
            // 记录思考日志
            logThinkResult(assistantMessage.getText(), toolCalls);
            
            // 判断是否需要行动
            if (toolCalls.isEmpty()) {
                // 无工具调用，记录助手消息
                getMessageList().add(assistantMessage);
                return false;
            } else {
                // 有工具调用，不记录助手消息（工具调用后会记录）
                return true;
            }
            
        } catch (Exception e) {
            handleThinkError(e);
            return false;
        }
    }

    /**
     * 行动：执行工具调用
     *
     * @return 工具执行结果
     */
    @Override
    public String act() {
        // 验证是否有工具调用
        if (!hasToolCalls()) {
            return "没有工具调用";
        }
        
        try {
            // 执行工具调用
            ToolExecutionResult result = executeToolCalls();
            
            // 检查死循环
            String loopCheckResult = checkInfiniteLoop(result);
            if (loopCheckResult != null) {
                return loopCheckResult;
            }
            
            // 更新消息历史
            updateMessageHistory(result);
            
            // 构建返回结果
            String toolResult = buildToolResult(result);
            
            // 检查终止信号
            checkTermination(result);
            
            return toolResult;
            
        } catch (Exception e) {
            throw new ToolCallException("unknown", "Tool execution failed", e);
        }
    }

    @Override
    protected void cleanup() {
        super.cleanup();
        this.lastToolName = null;
        this.sameToolCallCount = 0;
        this.toolCallChatResponse = null;
    }

    // ==================== 私有方法 ====================

    /**
     * 添加下一步提示
     */
    private void addNextStepPrompt() {
        String nextPrompt = getNextStepPrompt();
        if (StrUtil.isNotBlank(nextPrompt)) {
            getMessageList().add(new UserMessage(nextPrompt));
        }
    }

    /**
     * 构建Prompt
     */
    private Prompt buildPrompt() {
        return new Prompt(getMessageList(), chatOptions);
    }

    /**
     * 调用LLM
     */
    private ChatResponse callLLM(Prompt prompt) {
        return getChatClient()
                .prompt(prompt)
                .system(getSystemPrompt())
                .tools(availableTools)
                .call()
                .chatResponse();
    }

    /**
     * 记录思考结果日志
     */
    private void logThinkResult(String text, List<AssistantMessage.ToolCall> toolCalls) {
        log.info("Agent [{}] 思考结果: {}", getName(), 
            StrUtil.maxLength(text, 100));
        log.info("Agent [{}] 选择 {} 个工具", getName(), toolCalls.size());
        
        if (!toolCalls.isEmpty()) {
            String toolCallInfo = toolCalls.stream()
                    .map(tc -> String.format("工具: %s, 参数: %s", tc.name(), tc.arguments()))
                    .collect(Collectors.joining("\n  "));
            log.info("Agent [{}] 工具调用信息:\n  {}", getName(), toolCallInfo);
        }
    }

    /**
     * 处理思考错误
     */
    private void handleThinkError(Exception e) {
        log.error("Agent [{}] 思考过程出错: {}", getName(), e.getMessage());
        getMessageList().add(new AssistantMessage("处理时遇到错误: " + e.getMessage()));
    }

    /**
     * 检查是否有工具调用
     */
    private boolean hasToolCalls() {
        return toolCallChatResponse != null && toolCallChatResponse.hasToolCalls();
    }

    /**
     * 执行工具调用
     */
    private ToolExecutionResult executeToolCalls() {
        Prompt prompt = new Prompt(getSystemPrompt(), chatOptions);
        return toolCallingManager.executeToolCalls(prompt, toolCallChatResponse);
    }

    /**
     * 检查死循环
     *
     * @return 如果检测到死循环返回错误消息，否则返回null
     */
    private String checkInfiniteLoop(ToolExecutionResult result) {
        // 获取当前调用的工具列表
        List<String> currentToolNames = extractToolNames(result);
        
        // 只检查单一工具调用的情况
        if (currentToolNames.size() != 1) {
            // 多工具调用，重置计数器
            resetToolCallCounter();
            return null;
        }
        
        String currentToolName = currentToolNames.getFirst();
        
        // 更新计数器
        if (currentToolName.equals(lastToolName)) {
            sameToolCallCount++;
        } else {
            lastToolName = currentToolName;
            sameToolCallCount = 1;
        }
        
        // 检查是否超过阈值
        if (sameToolCallCount >= maxSameToolCallCount) {
            String message = String.format(
                "检测到工具 [%s] 连续调用 %d 次，可能存在死循环，已主动终止",
                currentToolName, sameToolCallCount);
            log.warn("Agent [{}] {}", getName(), message);
            setState(AgentStateEnum.FINISHED);
            return message;
        }
        
        return null;
    }

    /**
     * 提取工具名称列表
     */
    private List<String> extractToolNames(ToolExecutionResult result) {
        return result.conversationHistory().stream()
                .filter(message -> message instanceof ToolResponseMessage)
                .map(message -> (ToolResponseMessage) message)
                .flatMap(trm -> trm.getResponses().stream())
                .map(ToolResponseMessage.ToolResponse::name)
                .filter(Objects::nonNull)
                .toList();
    }

    /**
     * 重置工具调用计数器
     */
    private void resetToolCallCounter() {
        this.lastToolName = null;
        this.sameToolCallCount = 0;
    }

    /**
     * 更新消息历史
     */
    private void updateMessageHistory(ToolExecutionResult result) {
        setMessageList(result.conversationHistory());
    }

    /**
     * 构建工具执行结果
     */
    private String buildToolResult(ToolExecutionResult result) {
        Message lastMessage = CollUtil.getLast(result.conversationHistory());
        
        if (lastMessage instanceof ToolResponseMessage trm) {
            return trm.getResponses().stream()
                    .map(tr -> String.format("工具 [%s] 执行完成，结果: %s", 
                        tr.name(), truncateResponse(tr.responseData())))
                    .collect(Collectors.joining("\n"));
        }
        
        return "工具执行完成";
    }

    /**
     * 截断响应内容
     */
    private String truncateResponse(String response) {
        if (response == null) {
            return "null";
        }
        return response.length() > 500 ? response.substring(0, 500) + "..." : response;
    }

    /**
     * 检查终止信号
     */
    private void checkTermination(ToolExecutionResult result) {
        Message lastMessage = CollUtil.getLast(result.conversationHistory());
        
        if (lastMessage instanceof ToolResponseMessage trm) {
            boolean hasTerminate = trm.getResponses().stream()
                    .anyMatch(tr -> "doTerminate".equals(tr.name()));
            
            if (hasTerminate) {
                log.info("Agent [{}] 收到终止信号", getName());
                setState(AgentStateEnum.FINISHED);
            }
        }
    }
}