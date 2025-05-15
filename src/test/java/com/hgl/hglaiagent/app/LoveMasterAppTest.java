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
        message = "我忘记了我是谁，你能提醒一下我吗？";
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

    @Test
    void doChatWithRag() {
        String chatId = UUID.randomUUID().toString();
        String message = "我已经结婚了，但是婚后关系不太亲密，如何改善？";
        String answer =  loveMasterApp.doChatWithRag(message, chatId);
        Assertions.assertNotNull(answer);
    }

    @Test
    void doChatWithTools() {
        // 测试联网搜索问题的答案
        testMessage("周末想带女朋友去上海约会，推荐几个适合情侣的小众打卡地？");

        // 测试网页抓取：恋爱案例分析
        testMessage("最近和对象吵架了，看看编程导航网站（codefather.cn）的其他情侣是怎么解决矛盾的？");

        // 测试资源下载：图片下载
        testMessage("直接下载一张适合做手机壁纸的星空情侣图片为文件");

        // 测试终端操作：执行代码
        testMessage("执行 Python3 脚本来生成数据分析报告");

        // 测试文件操作：保存用户档案
        testMessage("保存我的恋爱档案为文件");

        // 测试 PDF 生成
        testMessage("生成一份‘七夕约会计划’PDF，包含餐厅预订、活动流程和礼物清单");
    }
    private void testMessage(String message) {
        String chatId = UUID.randomUUID().toString();
        String answer = loveMasterApp.doChatWithTools(message, chatId);
        Assertions.assertNotNull(answer);
    }
}