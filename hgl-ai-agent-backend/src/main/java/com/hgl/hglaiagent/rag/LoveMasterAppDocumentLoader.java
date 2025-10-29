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

    /**
     * 加载Markdown文档并转换为Document对象列表
     * <p>
     * 该方法会读取classpath下documents目录中的所有.md文件，解析每个文件的元数据，
     * 并使用MarkdownDocumentReader将文件内容转换为Document对象。
     *
     * @return 包含所有解析后文档的列表
     */
    public List<Document> loadMarkdownDocuments() {
        List<Document> allDocuments = new ArrayList<>();
        try {
            Resource[] resources = resourcePatternResolver.getResources("classpath:documents/*.md");
            for (Resource resource : resources) {
                String fileName = resource.getFilename();
                assert fileName != null;
                // 从文件名中提取状态信息（文件名倒数第4-5位字符）
                String status = fileName.substring(fileName.length() - 6, fileName.length() - 4);
                // 配置Markdown文档读取器，设置解析选项和元数据
                MarkdownDocumentReaderConfig markdownDocumentReaderConfig = MarkdownDocumentReaderConfig.builder()
                        // 设置遇到水平分割线时创建新文档
                        .withHorizontalRuleCreateDocument(true)
                        // 不包含代码块内容
                        .withIncludeCodeBlock(false)
                        // 不包含引用块内容
                        .withIncludeBlockquote(false)
                        // 添加文件名作为元数据
                        .withAdditionalMetadata("fileName", fileName)
                        // 添加状态信息作为元数据
                        .withAdditionalMetadata("status", status)
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
