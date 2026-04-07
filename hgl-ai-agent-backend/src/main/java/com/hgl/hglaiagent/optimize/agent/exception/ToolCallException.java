package com.hgl.hglaiagent.optimize.agent.exception;

import lombok.Getter;

/**
 * 工具调用异常
 */
@Getter
public class ToolCallException extends AgentException {
    
    private final String toolName;
    
    public ToolCallException(String toolName, String message) {
        super(String.format("Tool '%s' execution failed: %s", toolName, message));
        this.toolName = toolName;
    }
    
    public ToolCallException(String toolName, String message, Throwable cause) {
        super(String.format("Tool '%s' execution failed: %s", toolName, message), cause);
        this.toolName = toolName;
    }

}