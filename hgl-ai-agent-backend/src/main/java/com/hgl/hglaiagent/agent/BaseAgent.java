package com.hgl.hglaiagent.agent;

import cn.hutool.core.util.StrUtil;
import com.hgl.hglaiagent.agent.model.enums.AgentStateEnum;
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

/**
 * ClassName: BaseAgent
 * Package: com.hgl.hglaiagent.agent
 * Description:
 *
 * @Author HGL
 * @Create: 2025/5/22 15:01
 * @Desription: 智能体基类，定义基本信息和多步骤执行流程<p>
 * 抽象基础代理类，用于管理代理状态和执行流程。
 * <p>
 * 提供状态转换、内存管理和基于步骤的执行循环的基础功能。
 * 子类必须实现step方法。
 */
@Data
@Slf4j
public abstract class BaseAgent {

    // 核心属性
    private String name;

    // 提示
    private String systemPrompt;
    private String nextStepPrompt;

    // 执行控制
    private int maxSteps = 10;
    private int currentStep = 0;

    // 状态
    private AgentStateEnum state = AgentStateEnum.IDLE;

    //LLM
    private ChatClient chatClient;

    //Memory(需要自主维护会话的上下文)
    private List<Message> messageList = new ArrayList<>();

    /**
     * 运行代理
     *
     * @param userPrompt 用户提示词
     * @return 执行结果
     */
    public String run(String userPrompt) {
        if (this.state != AgentStateEnum.IDLE) {
            throw new RuntimeException("Cannot run agent from state:" + this.state);
        }
        if (StrUtil.isBlank(userPrompt)) {
            throw new RuntimeException("Cannot run agent with empty user prompt");
        }
        //更改状态
        this.state = AgentStateEnum.RUNNING;
        //记录消息上下文
        this.messageList.add(new UserMessage(userPrompt));
        //保存结果列表
        List<String> resultList = new ArrayList<>();
        try {
            while (this.currentStep < this.maxSteps && this.state != AgentStateEnum.FINISHED) {
                currentStep += 1;
                log.info("Executing step:{}/{}", currentStep, maxSteps);
                String stepResult = this.step();
                String result = "Step " + currentStep + ": " + stepResult;
                resultList.add(result);
            }
            if (currentStep >= maxSteps) {
                this.state = AgentStateEnum.FINISHED;
                resultList.add("Terminated: Reached max steps (" + maxSteps + ")");
            }
            return StrUtil.join("\n", resultList);
        } catch (Exception e) {
            this.state = AgentStateEnum.ERROR;
            log.error("Error executing agent", e);
            return "执行错误" + e.getMessage();
        } finally {
            // 清理资源
            this.cleanup();
        }
    }

    /**
     * 运行代理(流式输出)
     *
     * @param userPrompt 用户提示词
     * @return 执行结果
     */
    public SseEmitter runStream(String userPrompt) {
        SseEmitter emitter = new SseEmitter(300000L);
        //使用线程异步处理，避免阻塞主线程
        CompletableFuture.runAsync(() -> {
            try {
                if (this.state != AgentStateEnum.IDLE) {
                    emitter.send("错误,无法从状态运行代理：" + this.state);
                    emitter.complete();
                    return;
                }
                if (StrUtil.isBlank(userPrompt)) {
                    emitter.send("错误,不能使用空提示词运行代理!");
                    emitter.complete();
                    return;
                }
            } catch (IOException e) {
                emitter.completeWithError(e);
            }
            //更改状态
            this.state = AgentStateEnum.RUNNING;
            //记录消息上下文
            this.messageList.add(new UserMessage(userPrompt));
            try {
                while (this.currentStep < this.maxSteps && this.state != AgentStateEnum.FINISHED) {
                    currentStep += 1;
                    log.info("Executing step:{}/{}", currentStep, maxSteps);
                    String stepResult = this.step();
                    String result = "Step " + currentStep + ": " + stepResult;
                    emitter.send(result);
                }
                if (currentStep >= maxSteps) {
                    this.state = AgentStateEnum.FINISHED;
                    emitter.send("执行结束，达到最大步数 (" + maxSteps + ")");
                }
                emitter.complete();
            } catch (Exception e) {
                this.state = AgentStateEnum.ERROR;
                log.error("Error executing agent", e);
                try {
                    emitter.send("执行出错: " + e.getMessage());
                    emitter.complete();
                } catch (IOException ex) {
                    emitter.completeWithError(ex);
                }
            } finally {
                // 清理资源
                this.cleanup();
            }
        });
        //设置超时回调
        emitter.onTimeout(() -> {
            this.state = AgentStateEnum.ERROR;
            this.cleanup();
            log.warn("SSE连接超时，取消执行");
        });
        //设置完成回调
        emitter.onCompletion(() -> {
            if (this.state == AgentStateEnum.RUNNING) {
                this.state = AgentStateEnum.FINISHED;
            }
            this.cleanup();
            log.info("SSE连接完成，执行完毕");
        });
        return emitter;
    }

    /**
     * 执行单个步骤
     *
     * @return 步骤执行结果
     */
    public abstract String step();

    /**
     * 清理资源
     */
    protected void cleanup() {
        // 子类可以重写此方法来清理资源
    }
}
