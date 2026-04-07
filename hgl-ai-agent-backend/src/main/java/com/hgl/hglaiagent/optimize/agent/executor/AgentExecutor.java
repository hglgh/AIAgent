package com.hgl.hglaiagent.optimize.agent.executor;

import com.hgl.hglaiagent.optimize.agent.BaseAgent;


import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Agent执行器
 * 
 * <p>提供Agent的执行管理，包括：
 * <ul>
 *   <li>异步执行管理</li>
 *   <li>执行状态追踪</li>
 *   <li>执行结果缓存</li>
 * </ul>
 *
 * @author HGL
 * @since 2025/5/22
 */
@Slf4j
@Component
public class AgentExecutor {

    /**
     * 执行线程池
     */
    private final ExecutorService executorService = Executors.newFixedThreadPool(10);
    
    /**
     * 执行中的任务
     */
    private final Map<String, Future<String>> runningTasks = new ConcurrentHashMap<>();
    
    /**
     * 执行结果缓存
     */
    private final Map<String, String> resultCache = new ConcurrentHashMap<>();

    /**
     * 异步执行Agent
     *
     * @param agentId Agent唯一标识
     * @param agent Agent实例
     * @param prompt 用户提示词
     * @return 执行结果Future
     */
    public Future<String> executeAsync(String agentId, BaseAgent agent, String prompt) {
        // 检查Agent状态
        if (!agent.getState().canStart()) {
            throw new IllegalStateException("Agent is not in IDLE state");
        }
        
        // 提交任务
        Future<String> future = executorService.submit(() -> {
            try {
                String result = agent.run(prompt);
                resultCache.put(agentId, result);
                return result;
            } finally {
                runningTasks.remove(agentId);
            }
        });
        
        runningTasks.put(agentId, future);
        log.info("Agent [{}] task submitted", agentId);
        
        return future;
    }

    /**
     * 取消执行
     *
     * @param agentId Agent唯一标识
     * @return 是否取消成功
     */
    public boolean cancel(String agentId) {
        Future<String> future = runningTasks.get(agentId);
        if (future != null) {
            boolean cancelled = future.cancel(true);
            if (cancelled) {
                runningTasks.remove(agentId);
                log.info("Agent [{}] task cancelled", agentId);
            }
            return cancelled;
        }
        return false;
    }

    /**
     * 获取执行状态
     *
     * @param agentId Agent唯一标识
     * @return 执行状态
     */
    public ExecutionStatus getStatus(String agentId) {
        if (resultCache.containsKey(agentId)) {
            return ExecutionStatus.COMPLETED;
        }
        
        Future<String> future = runningTasks.get(agentId);
        if (future == null) {
            return ExecutionStatus.NOT_FOUND;
        }
        
        if (future.isCancelled()) {
            return ExecutionStatus.CANCELLED;
        }
        
        if (future.isDone()) {
            return ExecutionStatus.COMPLETED;
        }
        
        return ExecutionStatus.RUNNING;
    }

    /**
     * 获取执行结果
     *
     * @param agentId Agent唯一标识
     * @return 执行结果
     */
    public String getResult(String agentId) {
        return resultCache.get(agentId);
    }

    /**
     * 清除结果缓存
     *
     * @param agentId Agent唯一标识
     */
    public void clearCache(String agentId) {
        resultCache.remove(agentId);
    }

    /**
     * 执行状态枚举
     */
    public enum ExecutionStatus {
        NOT_FOUND,   // 未找到
        RUNNING,     // 运行中
        COMPLETED,   // 已完成
        CANCELLED    // 已取消
    }
}