package com.hgl.hglaiagent.app;

import cn.hutool.core.lang.UUID;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.messages.Message;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.HashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * ClassName: LoveMasterAppTest
 * Package: com.hgl.hglaiagent.app
 * Description:
 *
 * @Author HGL
 * @Create: 2025/4/28 15:26
 */
@SpringBootTest
class LoveMasterAppTest {

    @Resource
    private LoveMasterApp loveMasterApp;

    @Test
    void doChat() {
        String chatId = UUID.randomUUID().toString();
        // 第一轮
        String message = "你好，我是扈官龙";
        String answer = loveMasterApp.doChat(message, chatId);
        Assertions.assertNotNull(answer);
        // 第二轮
        message = "我目前单身";
        answer = loveMasterApp.doChat(message, chatId);
        Assertions.assertNotNull(answer);
        // 第三轮
        message = "我是谁?";
        answer = loveMasterApp.doChat(message, chatId);
        Assertions.assertNotNull(answer);
    }

    @Test
    void doChatWithReport() {
        String chatId = UUID.randomUUID().toString();
        String message = "你好，我是扈官龙,如何寻找另一半";
        LoveMasterApp.LoveReport loveReport = loveMasterApp.doChatWithReport(message, chatId);
        Assertions.assertNotNull(loveReport);
    }
}