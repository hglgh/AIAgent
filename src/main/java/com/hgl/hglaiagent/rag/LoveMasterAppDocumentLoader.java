package com.hgl.hglaiagent.rag;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.markdown.MarkdownDocumentReader;
import org.springframework.ai.reader.markdown.config.MarkdownDocumentReaderConfig;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * ClassName: LoveMasterAppDocumentLoader
 * Package: com.hgl.hglaiagent.rag
 * Description:
 *
 * @Author HGL
 * @Create: 2025/4/30 14:47
 * @Desription: 该类用于从classpath:documents/路径下加载所有.md（Markdown）格式的文档文件，<h3>并将它们转换为Document对象列表返回。</h3> 主要逻辑如下：<br/>
 * <ol>
 *     <li>使用Spring的ResourcePatternResolver查找所有匹配的Markdown资源文件。</li>
 *     <li>遍历每个资源文件，配置MarkdownDocumentReaderConfig以控制解析行为。</li>
 *     <li>使用MarkdownDocumentReader读取文档内容并添加到结果列表中。</li>
 *     <li>若读取过程中发生异常，则记录错误日志。</li>
 * </ol>
 */
@Slf4j
@Component
public class LoveMasterAppDocumentLoader {
    private final ResourcePatternResolver resourcePatternResolver;

    public LoveMasterAppDocumentLoader(ResourcePatternResolver resourcePatternResolver) {
        this.resourcePatternResolver = resourcePatternResolver;
    }

    public List<Document> loadMarkdownDocuments() {
        List<Document> allDocuments = new ArrayList<>();
        try {
            Resource[] resources = resourcePatternResolver.getResources("classpath:documents/*.md");
            for (Resource resource : resources) {
                String fileName = resource.getFilename();
                assert fileName != null;
                String status = fileName.substring(fileName.length() - 6, fileName.length() - 4);
                MarkdownDocumentReaderConfig markdownDocumentReaderConfig = MarkdownDocumentReaderConfig.builder()
                        .withHorizontalRuleCreateDocument(true)
                        .withIncludeCodeBlock(false)
                        .withIncludeBlockquote(false)
                        .withAdditionalMetadata("fileName", fileName)
                        .withAdditionalMetadata("status",status)
                        .build();
                MarkdownDocumentReader markdownDocumentReader = new MarkdownDocumentReader(resource, markdownDocumentReaderConfig);
                allDocuments.addAll(markdownDocumentReader.get());
            }
        } catch (IOException e) {
            log.error("Markdown 文档加载失败: {}", e.getMessage());
        }
        return allDocuments;
    }
}
