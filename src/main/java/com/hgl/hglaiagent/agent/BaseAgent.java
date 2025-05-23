package com.hgl.hglaiagent.agent;

import cn.hutool.core.util.StrUtil;
import com.hgl.hglaiagent.agent.model.enums.AgentStateEnum;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;

import java.util.ArrayList;
import java.util.List;

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
