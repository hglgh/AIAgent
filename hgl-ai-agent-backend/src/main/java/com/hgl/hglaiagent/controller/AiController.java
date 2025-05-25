package com.hgl.hglaiagent.controller;

import com.hgl.hglaiagent.agent.HuManus;
import com.hgl.hglaiagent.app.LoveMasterApp;
import jakarta.annotation.Resource;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import reactor.core.publisher.Flux;

import java.io.IOException;

/**
 * ClassName: AiController
 * Package: com.hgl.hglaiagent.controller
 * Description:
 *
 * @Author HGL
 * @Create: 2025/5/23 11:03
 */
@RestController
@RequestMapping("/ai")
public class AiController {

    @Resource
    private LoveMasterApp loveMasterApp;

    @Resource
    private ToolCallback[] allTools;

    @Resource
    private ChatModel dashscopeChatModel;

    /**
     * 同步调用AI 恋爱大师应用
     *
     * @param message 用户输入的
     * @param chatId  会话ID
     * @return AI的回答
     */
    @GetMapping("/love_master_app/chat/sync")
    public String doChatWithLoveMasterAppBySync(String message, String chatId) {
        return loveMasterApp.doChat(message, chatId);
    }

    /**
     * SSE流式调用AI 恋爱大师应用
     *
     * @param message 用户输入的
     * @param chatId  会话ID
     * @return AI的回答
     */
    @GetMapping(value = "/love_master_app/chat/sse", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> doChatWithLoveMasterAppBySse(String message, String chatId) {
        return loveMasterApp.doChatByStream(message, chatId);
    }

    @GetMapping("/love_master_app/chat/sse_responseEntity")
    public ResponseEntity<Flux<String>> doChatWithLoveMasterAppBySseResponseEntity(String message, String chatId) {
        return ResponseEntity.ok()
                .contentType(MediaType.TEXT_EVENT_STREAM)
                .body(loveMasterApp.doChatByStream(message, chatId));
    }

    /**
     * SSE流式调用AI 恋爱大师应用
     *
     * @param message 用户输入的
     * @param chatId  会话ID
     * @return AI的回答
     */
    @GetMapping(value = "/love_master_app/chat/server_sent_event")
    public Flux<ServerSentEvent<String>> doChatWithLoveMasterAppByServerSentEvent(String message, String chatId) {
        return loveMasterApp.doChatByStream(message, chatId)
                .map(chunk -> ServerSentEvent.<String>builder()
                        .data(chunk)
                        .build());
    }

    /**
     * SSE流式调用AI 恋爱大师应用
     *
     * @param message 用户输入的
     * @param chatId  会话ID
     * @return AI的回答
     */
    @GetMapping(value = "/love_master_app/chat/sse_emitter")
    public SseEmitter doChatWithLoveMasterAppBySseEmitter(String message, String chatId) {
        //创建一个3分钟超时的SseEmitter 对象
        SseEmitter sseEmitter = new SseEmitter(180000L);
        //获取Flux 响应式数据流并且通过订阅的方式将数据推送到客户端
        loveMasterApp.doChatByStream(message, chatId)
                .subscribe(chunk -> {
                    try {
                        sseEmitter.send(chunk);
                    } catch (IOException e) {
                        sseEmitter.completeWithError(e);
                    }
                }, sseEmitter::completeWithError, sseEmitter::complete);
        return sseEmitter;
    }

    /**
     * 流式获取HuManus超级智能体
     * @param message
     * @return
     */
    @GetMapping("/manus/chat")
    public SseEmitter doChatWithManus(String message) {
        HuManus huManus = new HuManus(allTools, dashscopeChatModel);
        return huManus.runStream(message);
    }
}
