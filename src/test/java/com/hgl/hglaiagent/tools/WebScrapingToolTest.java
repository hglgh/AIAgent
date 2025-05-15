package com.hgl.hglaiagent.tools;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * ClassName: WebScrapingToolTest
 * Package: com.hgl.hglaiagent.tools
 * Description:
 *
 * @Author HGL
 * @Create: 2025/5/14 14:17
 */
class WebScrapingToolTest {

    @Test
    void scrapeWeb() {
        WebScrapingTool webScrapingTool = new WebScrapingTool();
        String scrapedWeb = webScrapingTool.scrapeWeb("https://www.baidu.com");
        System.out.println(scrapedWeb);

    }
}