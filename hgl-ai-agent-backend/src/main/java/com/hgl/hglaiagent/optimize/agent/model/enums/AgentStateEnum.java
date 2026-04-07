package com.hgl.hglaiagent.optimize.agent.model.enums;

import lombok.Getter;

/**
 * Agent状态枚举
 */
@Getter
public enum AgentStateEnum {
    
    IDLE("空闲", "Agent处于空闲状态，可以接受新任务"),
    RUNNING("运行中", "Agent正在执行任务"),
    FINISHED("已完成", "Agent已完成任务"),
    ERROR("错误", "Agent执行过程中发生错误");
    
    private final String displayName;
    private final String description;
    
    AgentStateEnum(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }
    
    /**
     * 判断是否可以启动新任务
     */
    public boolean canStart() {
        return this == IDLE;
    }
    
    /**
     * 判断是否处于活动状态
     */
    public boolean isActive() {
        return this == RUNNING;
    }
    
    /**
     * 判断是否已终止
     */
    public boolean isTerminated() {
        return this == FINISHED || this == ERROR;
    }

    /**
     * 判断是否为非空闲状态（用于验证前置条件）
     *
     * @return 如果不是 IDLE 状态返回 true
     */
    public boolean isNotIdle() {
        return this != IDLE;
    }
}