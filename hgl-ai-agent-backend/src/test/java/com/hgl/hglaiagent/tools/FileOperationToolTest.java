package com.hgl.hglaiagent.tools;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * ClassName: FileOperationToolTest
 * Package: com.hgl.hglaiagent.tools
 * Description:
 *
 * @Author HGL
 * @Create: 2025/5/13 16:17
 */
class FileOperationToolTest {

    @Test
    void readFile() {
        FileOperationTool fileOperationTool = new FileOperationTool();
        String result = fileOperationTool.readFile("test.txt");
        System.out.println(result);
    }

    @Test
    void writeFile() {
        FileOperationTool fileOperationTool = new FileOperationTool();
        String result = fileOperationTool.writeFile("test.txt", "Hello, World!");
        System.out.println(result);
    }
}