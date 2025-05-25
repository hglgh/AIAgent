package com.hgl.hglaiagent.tools;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * ClassName: PDFGenerationToolTest
 * Package: com.hgl.hglaiagent.tools
 * Description:
 *
 * @Author HGL
 * @Create: 2025/5/14 15:26
 */
class PDFGenerationToolTest {

    @Test
    void generatePDF() {
        PDFGenerationTool tool = new PDFGenerationTool();
        String fileName = "test.pdf";
        String content = "编程导航原创项目 https://www.codefather.cn";
        String result = tool.generatePDF(fileName, content);
        assertNotNull(result);
    }
}