package com.hgl.hglaiagent.optimize.agent.exception;

import lombok.Getter;

/**
 * Agent执行异常
 */
@Getter
public class AgentExecutionException extends AgentException {
    
    private final int stepNumber;
    
    public AgentExecutionException(String message, int stepNumber) {
        super(message);
        this.stepNumber = stepNumber;
    }
    
    public AgentExecutionException(String message, int stepNumber, Throwable cause) {
        super(message, cause);
        this.stepNumber = stepNumber;
    }

}