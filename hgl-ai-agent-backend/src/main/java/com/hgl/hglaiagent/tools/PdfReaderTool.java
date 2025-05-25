package com.hgl.hglaiagent.tools;

import com.hgl.hglaiagent.constant.FileConstant;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;

import java.io.File;
import java.io.IOException;

/**
 * ClassName: PdfReaderTool
 * Package: com.hgl.hglaiagent.tools
 * Description: PDF 文件读取工具类，用于提取 PDF 中的文本内容
 *
 * @Author HGL
 * @Create: 2025/5/14 14:46
 */
public class PdfReaderTool {

    @Tool(description = "从指定路径的 PDF 文件中提取文本内容")
    public String extractPdfText(@ToolParam(description = "PDF 文件的名称") String fileName) {
        String fileDir = FileConstant.FILE_SAVE_DIR + "/pdf";
        String filePath = fileDir + "/" + fileName;
        File file = new File(filePath);

        if (!file.exists()) {
            return "错误：文件不存在！路径：" + filePath;
        }

        try (PDDocument document = Loader.loadPDF(file)) {
            PDFTextStripper stripper = new PDFTextStripper();
            String text = stripper.getText(document);
            return "提取的文本内容如下：\n" + text;
        } catch (IOException e) {
            return "读取 PDF 出错：" + e.getMessage();
        }
    }
}
