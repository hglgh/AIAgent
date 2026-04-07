package com.hgl.hglaiagent.optimize.agent;

import cn.hutool.core.util.StrUtil;
import com.hgl.hglaiagent.optimize.agent.constant.AgentConstants;
import com.hgl.hglaiagent.optimize.agent.exception.AgentExecutionException;
import com.hgl.hglaiagent.optimize.agent.exception.AgentStateException;
import com.hgl.hglaiagent.optimize.agent.model.enums.AgentStateEnum;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

/**
 * 智能体基类
 *
 * <p>定义智能体的基本信息和多步骤执行流程，提供：
 * <ul>
 *   <li>状态管理：IDLE → RUNNING → FINISHED/ERROR</li>
 *   <li>内存管理：维护对话上下文</li>
 *   <li>执行控制：同步和异步执行模式</li>
 *   <li>错误处理：完善的异常处理机制</li>
 * </ul>
 *
 * @author HGL
 * @since 2025/5/22
 */
@Data
@Slf4j
public abstract class BaseAgent {

    // ==================== 核心属性 ====================

    /**
     * Agent名称
     */
    private String name;

    /**
     * 系统提示词
     */
    private String systemPrompt = AgentConstants.DEFAULT_SYSTEM_PROMPT;

    /**
     * 下一步提示词
     */
    private String nextStepPrompt = AgentConstants.DEFAULT_NEXT_STEP_PROMPT;

    // ==================== 执行控制 ====================

    /**
     * 最大执行步数
     */
    private int maxSteps = AgentConstants.DEFAULT_MAX_STEPS;

    /**
     * 当前执行步数（线程安全）
     */
    private final AtomicInteger currentStep = new AtomicInteger(0);

    // ==================== 状态管理 ====================

    /**
     * 当前状态（线程安全）
     */
    private final AtomicReference<AgentStateEnum> state =
            new AtomicReference<>(AgentStateEnum.IDLE);

    // ==================== LLM相关 ====================

    /**
     * 聊天客户端
     */
    private ChatClient chatClient;

    // ==================== 内存管理 ====================

    /**
     * 消息列表（对话历史）
     */
    private List<Message> messageList = new ArrayList<>();

    // ==================== 公共方法 ====================

    /**
     * 获取当前状态
     */
    public AgentStateEnum getState() {
        return state.get();
    }

    /**
     * 设置状态
     */
    public void setState(AgentStateEnum newState) {
        AgentStateEnum oldState = state.getAndSet(newState);
        log.debug("Agent [{}] state changed: {} -> {}", name, oldState, newState);
    }

    /**
     * 获取当前步数
     */
    public int getCurrentStep() {
        return currentStep.get();
    }

    /**
     * 运行代理（同步模式）
     *
     * @param userPrompt 用户提示词
     * @return 执行结果
     * @throws AgentStateException     如果状态不允许运行
     * @throws AgentExecutionException 如果执行过程中发生错误
     */
    public String run(String userPrompt) {
        // 验证前置条件
        validatePreconditions(userPrompt);

        // 初始化执行环境
        initializeExecution(userPrompt);

        // 执行主循环
        List<String> results = executeMainLoop();

        // 处理完成状态
        return handleCompletion(results);
    }

    /**
     * 运行代理（流式输出模式）
     *
     * @param userPrompt 用户提示词
     * @return SSE发射器
     */
    public SseEmitter runStream(String userPrompt) {
        SseEmitter emitter = createSseEmitter();

        // 异步执行代理逻辑
        CompletableFuture.runAsync(() -> executeAgentLogicAsync(emitter, userPrompt));

        // 设置回调处理
        setupEmitterCallbacks(emitter);

        return emitter;
    }

    // ==================== 抽象方法 ====================

    /**
     * 执行单个步骤
     *
     * @return 步骤执行结果
     * @throws AgentExecutionException 如果步骤执行失败
     */
    public abstract String step();

    /**
     * 清理资源
     */
    protected void cleanup() {
        // 子类可重写此方法来清理资源
        log.debug("Agent [{}] cleanup completed", name);
    }

    /**
     * 重置Agent状态
     */
    public void reset() {
        this.state.set(AgentStateEnum.IDLE);
        this.currentStep.set(0);
        this.messageList.clear();
        log.info("Agent [{}] has been reset", name);
    }

    // ==================== 私有方法 ====================

    /**
     * 验证前置条件
     */
    private void validatePreconditions(String userPrompt) {
        // 验证状态
        if (state.get().isNotIdle()) {
            throw new AgentStateException(state.get(), AgentStateEnum.IDLE);
        }

        // 验证提示词
        if (StrUtil.isBlank(userPrompt)) {
            throw new IllegalArgumentException(AgentConstants.ERROR_EMPTY_PROMPT);
        }
    }

    /**
     * 初始化执行环境
     */
    private void initializeExecution(String userPrompt) {
        state.set(AgentStateEnum.RUNNING);
        currentStep.set(0);
        messageList.clear();
        messageList.add(new UserMessage(userPrompt));

        log.info("Agent [{}] started with prompt: {}", name,
                StrUtil.maxLength(userPrompt, 100));
    }

    /**
     * 执行主循环
     */
    private List<String> executeMainLoop() {
        List<String> results = new ArrayList<>();

        try {
            while (shouldContinue()) {
                int step = currentStep.incrementAndGet();
                logStep(step);

                String stepResult = executeStep(step);
                results.add(formatStepResult(step, stepResult));
            }

            checkMaxStepsReached();

        } catch (Exception e) {
            handleExecutionError(e);
            throw e;
        } finally {
            cleanup();
        }

        return results;
    }

    /**
     * 判断是否应该继续执行
     */
    private boolean shouldContinue() {
        return currentStep.get() < maxSteps &&
                !state.get().isTerminated();
    }

    /**
     * 执行单步
     */
    private String executeStep(int step) {
        try {
            return step();
        } catch (Exception e) {
            throw new AgentExecutionException(e.getMessage(), step, e);
        }
    }

    /**
     * 记录步骤日志
     */
    private void logStep(int step) {
        log.info("Agent [{}] executing step: {}/{}", name, step, maxSteps);
    }

    /**
     * 格式化步骤结果
     */
    private String formatStepResult(int step, String result) {
        return String.format("Step %d: %s", step, result);
    }

    /**
     * 检查是否达到最大步数
     */
    private void checkMaxStepsReached() {
        if (currentStep.get() >= maxSteps) {
            state.set(AgentStateEnum.FINISHED);
            log.warn("Agent [{}] reached max steps limit: {}", name, maxSteps);
        }
    }

    /**
     * 处理执行错误
     */
    private void handleExecutionError(Exception e) {
        state.set(AgentStateEnum.ERROR);
        log.error("Agent [{}] execution failed at step {}", name, currentStep.get(), e);
    }

    /**
     * 处理执行完成
     */
    private String handleCompletion(List<String> results) {
        if (currentStep.get() >= maxSteps) {
            results.add(String.format(AgentConstants.MESSAGE_MAX_STEPS_TEMPLATE, maxSteps));
        }
        return StrUtil.join("\n", results);
    }

    /**
     * 创建SSE发射器
     */
    private SseEmitter createSseEmitter() {
        return new SseEmitter(AgentConstants.DEFAULT_SSE_TIMEOUT);
    }

    /**
     * 异步执行代理逻辑
     */
    private void executeAgentLogicAsync(SseEmitter emitter, String userPrompt) {
        try {
            if (!validateStreamPreconditions(emitter, userPrompt)) {
                return;
            }

            initializeExecution(userPrompt);
            executeStreamMainLoop(emitter);
            handleStreamCompletion(emitter);

        } catch (Exception e) {
            handleStreamError(emitter, e);
        } finally {
            cleanup();
        }
    }

    /**
     * 验证流式执行前置条件
     */
    private boolean validateStreamPreconditions(SseEmitter emitter, String userPrompt)
            throws IOException {
        if (state.get().isNotIdle()) {
            sendAndComplete(emitter,
                    String.format(AgentConstants.ERROR_STATE_TEMPLATE, state.get()));
            return false;
        }

        if (StrUtil.isBlank(userPrompt)) {
            sendAndComplete(emitter, AgentConstants.ERROR_EMPTY_PROMPT);
            return false;
        }

        return true;
    }

    /**
     * 执行流式主循环
     */
    private void executeStreamMainLoop(SseEmitter emitter) throws IOException {
        while (shouldContinue()) {
            int step = currentStep.incrementAndGet();
            logStep(step);

            String stepResult = executeStep(step);
            emitter.send(formatStepResult(step, stepResult));
        }

        if (currentStep.get() >= maxSteps) {
            state.set(AgentStateEnum.FINISHED);
        }
    }

    /**
     * 处理流式执行完成
     */
    private void handleStreamCompletion(SseEmitter emitter) throws IOException {
        if (currentStep.get() >= maxSteps) {
            emitter.send(String.format(AgentConstants.MESSAGE_MAX_STEPS_TEMPLATE, maxSteps));
        }
        emitter.complete();
        log.info("Agent [{}] stream execution completed", name);
    }

    /**
     * 处理流式执行错误
     */
    private void handleStreamError(SseEmitter emitter, Exception e) {
        state.set(AgentStateEnum.ERROR);
        log.error("Agent [{}] stream execution failed", name, e);

        try {
            String errorMessage = String.format(AgentConstants.ERROR_EXECUTION_TEMPLATE,
                    currentStep.get(), e.getMessage());
            sendAndComplete(emitter, errorMessage);
        } catch (Exception ex) {
            emitter.completeWithError(ex);
        }
    }

    /**
     * 发送消息并完成
     */
    private void sendAndComplete(SseEmitter emitter, String message) throws IOException {
        emitter.send(message);
        emitter.complete();
    }

    /**
     * 设置SSE回调
     */
    private void setupEmitterCallbacks(SseEmitter emitter) {
        emitter.onTimeout(() -> {
            state.set(AgentStateEnum.ERROR);
            cleanup();
            log.warn("Agent [{}] SSE connection timeout", name);
        });

        emitter.onCompletion(() -> {
            if (state.get().isActive()) {
                state.set(AgentStateEnum.FINISHED);
            }
            cleanup();
            log.debug("Agent [{}] SSE connection closed", name);
        });

        emitter.onError(throwable -> {
            state.set(AgentStateEnum.ERROR);
            log.error("Agent [{}] SSE connection error", name, throwable);
        });
    }
}