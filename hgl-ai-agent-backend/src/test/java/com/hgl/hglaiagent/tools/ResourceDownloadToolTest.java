package com.hgl.hglaiagent.tools;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * ClassName: ResourceDownloadToolTest
 * Package: com.hgl.hglaiagent.tools
 * Description:
 *
 * @Author HGL
 * @Create: 2025/5/14 15:00
 */
class ResourceDownloadToolTest {

    @Test
    void downloadResource() {
        ResourceDownloadTool tool = new ResourceDownloadTool();
        String url = "https://www.codefather.cn/logo.png";
        String fileName = "logo.png";
        String result = tool.downloadResource(url, fileName);
        assertNotNull(result);
    }
}