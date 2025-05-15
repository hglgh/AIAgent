package com.hgl.hglaiagent.config;

import com.hgl.hglaiagent.tools.*;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.ToolCallbacks;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * ClassName: ToolRegistration
 * Package: com.hgl.hglaiagent.config
 * Description:
 *
 * @Author HGL
 * @Create: 2025/5/14 15:55
 */
@Configuration
public class ToolRegistration {

    @Value("${search-api.api-key}")
    private String apiKey;

    @Bean
    public ToolCallback[] allTools() {
        return ToolCallbacks.from(
                new WebSearchTool(apiKey),
                new WebScrapingTool(),
                new PDFGenerationTool(),
                new PdfReaderTool(),
                new FileOperationTool(),
                new ResourceDownloadTool(),
                new TerminalOperationTool(),
                new TimeUtilTool()
        );
    }
}
