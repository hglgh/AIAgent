package com.hgl.hglaiagent.tools;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * ClassName: PdfReaderToolTest
 * Package: com.hgl.hglaiagent.tools
 * Description:
 *
 * @Author HGL
 * @Create: 2025/5/14 15:42
 */
class PdfReaderToolTest {

    @Test
    void extractPdfText() {
        PdfReaderTool pdfReaderTool = new PdfReaderTool();
        String result = pdfReaderTool.extractPdfText("test.pdf");
        System.out.println(result);
    }
}