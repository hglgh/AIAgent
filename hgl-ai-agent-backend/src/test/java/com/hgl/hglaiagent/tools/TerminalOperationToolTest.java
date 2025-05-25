package com.hgl.hglaiagent.tools;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * ClassName: TerminalOperationToolTest
 * Package: com.hgl.hglaiagent.tools
 * Description:
 *
 * @Author HGL
 * @Create: 2025/5/14 14:37
 */
class TerminalOperationToolTest {

    @Test
    void executeTerminalCommand() {
        TerminalOperationTool tool = new TerminalOperationTool();
        String command = "dir";
        String result = tool.executeTerminalCommand(command);
        assertNotNull(result);
        System.out.println(result);
    }
}