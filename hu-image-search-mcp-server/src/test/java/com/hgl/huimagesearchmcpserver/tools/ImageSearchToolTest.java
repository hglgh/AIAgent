package com.hgl.huimagesearchmcpserver.tools;

import jakarta.annotation.Resource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

/**
 * ClassName: ImageSearchToolTest
 * Package: com.hgl.huimagesearchmcpserver.tools
 * Description:
 *
 * @Author HGL
 * @Create: 2025/5/21 11:33
 */
@SpringBootTest
class ImageSearchToolTest {

    @Resource
    private ImageSearchTool imageSearchTool;
    @Test
    void searchImage() {
        String image = imageSearchTool.searchImage("cat");
        Assertions.assertNotNull(image);
        System.out.println(image);
    }
}