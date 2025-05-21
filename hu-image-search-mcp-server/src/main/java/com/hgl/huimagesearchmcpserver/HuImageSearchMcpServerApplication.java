package com.hgl.huimagesearchmcpserver;

import com.hgl.huimagesearchmcpserver.tools.ImageSearchTool;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.tool.method.MethodToolCallbackProvider;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

/**
 * @author 请别把我整破防
 */
@SpringBootApplication
public class HuImageSearchMcpServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(HuImageSearchMcpServerApplication.class, args);
    }

    @Bean
    public ToolCallbackProvider imageSearchTools(ImageSearchTool imageSearchTool) {
        return MethodToolCallbackProvider
                .builder()
                .toolObjects(imageSearchTool)
                .build();
    }
}
