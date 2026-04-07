package com.hgl.hglaiagent.optimize.agent.exception;

import com.hgl.hglaiagent.optimize.agent.model.enums.AgentStateEnum;
import lombok.Getter;

/**
 * Agent状态异常
 */
@Getter
public class AgentStateException extends AgentException {
    
    private final AgentStateEnum currentState;
    private final AgentStateEnum expectedState;
    
    public AgentStateException(AgentStateEnum currentState, AgentStateEnum expectedState) {
        super(String.format("Invalid agent state. Current: %s, Expected: %s", 
              currentState, expectedState));
        this.currentState = currentState;
        this.expectedState = expectedState;
    }

}