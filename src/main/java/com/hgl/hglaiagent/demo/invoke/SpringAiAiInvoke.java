package com.hgl.hglaiagent.demo.invoke;

import jakarta.annotation.Resource;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * @author 请别把我整破防
 * @Desription: 使用springAI 调用 dashscope零积 模型
 */
// 取消注释即可在 SpringBoot 项目启动时执行
@Component
public class SpringAiAiInvoke implements CommandLineRunner {

    @Resource
    private ChatModel dashscopeChatModel;

    @Override
    public void run(String... args) throws Exception {
        AssistantMessage output = dashscopeChatModel.call(new Prompt("你好，我是扈官龙"))
                .getResult()
                .getOutput();
        System.out.println(output.getText());
    }
}
