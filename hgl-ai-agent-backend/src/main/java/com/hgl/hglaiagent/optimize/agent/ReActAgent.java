package com.hgl.hglaiagent.optimize.agent;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;

/**
 * ReAct (Reasoning and Acting) 模式代理抽象类
 * 
 * <p>实现了"思考-行动"循环模式：
 * <ol>
 *   <li>Think: 分析当前状态，决定是否需要行动</li>
 *   <li>Act: 如果需要，执行相应的行动</li>
 * </ol>
 * 
 * <p>这种模式模拟了人类解决问题的思维过程：
 * 先分析情况，再采取行动，循环往复直到目标达成。
 * 
 * @author HGL
 * @since 2025/5/22
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Slf4j
public abstract class ReActAgent extends BaseAgent {

    /**
     * 思考：分析当前状态并决定下一步行动
     * 
     * <p>子类应在此方法中：
     * <ul>
     *   <li>分析当前任务状态</li>
     *   <li>决定是否需要执行工具或操作</li>
     *   <li>返回决策结果</li>
     * </ul>
     *
     * @return true表示需要执行行动，false表示无需行动
     */
    public abstract boolean think();

    /**
     * 行动：执行决定的行动
     * 
     * <p>子类应在此方法中：
     * <ul>
     *   <li>执行具体的工具调用或操作</li>
     *   <li>更新内部状态</li>
     *   <li>返回执行结果</li>
     * </ul>
     *
     * @return 行动执行结果
     */
    public abstract String act();

    /**
     * 执行单个步骤：先思考，再行动
     *
     * @return 步骤执行结果
     */
    @Override
    public String step() {
        try {
            // 记录思考开始
            log.debug("Agent [{}] starting think phase", getName());
            
            // 思考阶段
            boolean shouldAct = think();
            
            if (!shouldAct) {
                log.info("Agent [{}] think phase completed - no action needed", getName());
                return "思考完成 - 无需行动";
            }
            
            // 记录行动开始
            log.debug("Agent [{}] starting act phase", getName());
            
            // 行动阶段
            String result = act();
            
            log.info("Agent [{}] act phase completed with result: {}", 
                getName(), truncateResult(result));
            
            return result;
            
        } catch (Exception e) {
            log.error("Agent [{}] step execution failed", getName(), e);
            return "步骤执行失败: " + e.getMessage();
        }
    }

    /**
     * 截断结果用于日志显示
     */
    private String truncateResult(String result) {
        if (result == null) {
            return "null";
        }
        return result.length() > 200 ? result.substring(0, 200) + "..." : result;
    }
}