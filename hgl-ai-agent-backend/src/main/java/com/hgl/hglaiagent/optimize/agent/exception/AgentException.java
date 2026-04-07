package com.hgl.hglaiagent.optimize.agent.exception;

/**
 * Agent异常基类
 */
public class AgentException extends RuntimeException {
    
    public AgentException(String message) {
        super(message);
    }
    
    public AgentException(String message, Throwable cause) {
        super(message, cause);
    }
}