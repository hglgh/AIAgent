package com.hgl.hglaiagent.optimize.agent.constant;

/**
 * Agent常量定义
 */
public final class AgentConstants {
    
    private AgentConstants() {
        // 工具类，禁止实例化
    }
    
    // ==================== 默认配置 ====================
    
    /**
     * 默认最大步数
     */
    public static final int DEFAULT_MAX_STEPS = 10;
    
    /**
     * 默认SSE超时时间（毫秒）
     */
    public static final long DEFAULT_SSE_TIMEOUT = 300_000L; // 5分钟
    
    /**
     * 默认最大相同工具调用次数
     */
    public static final int DEFAULT_MAX_SAME_TOOL_CALL_COUNT = 3;
    
    // ==================== 提示词模板 ====================
    
    /**
     * 默认系统提示词
     */
    public static final String DEFAULT_SYSTEM_PROMPT = "You are a helpful AI assistant.";
    
    /**
     * 默认下一步提示词
     */
    public static final String DEFAULT_NEXT_STEP_PROMPT = "Continue with your task.";
    
    // ==================== 状态消息 ====================
    
    /**
     * 空提示词错误消息
     */
    public static final String ERROR_EMPTY_PROMPT = "Cannot run agent with empty user prompt";
    
    /**
     * 状态错误消息模板
     */
    public static final String ERROR_STATE_TEMPLATE = "Cannot run agent from state: %s";
    
    /**
     * 达到最大步数消息模板
     */
    public static final String MESSAGE_MAX_STEPS_TEMPLATE = "Terminated: Reached max steps (%d)";
    
    /**
     * 执行错误消息模板
     */
    public static final String ERROR_EXECUTION_TEMPLATE = "Execution error at step %d: %s";
}