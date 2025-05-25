package com.hgl.hglaiagent.rag;

import jakarta.annotation.Resource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.ai.rag.Query;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * ClassName: MyMultiQueryExpanderTest
 * Package: com.hgl.hglaiagent.rag
 * Description:
 *
 * @Author HGL
 * @Create: 2025/5/12 16:53
 */
@SpringBootTest
class MyMultiQueryExpanderTest {

    @Resource
    private MyMultiQueryExpander myMultiQueryExpander;
    @Test
    void expand() {
        List<Query> expand = myMultiQueryExpander.expand("谁是程序员鱼皮啊？");
        Assertions.assertNotNull(expand);
    }
}