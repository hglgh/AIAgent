package com.hgl.hglaiagent.converter;

import cn.hutool.json.JSONUtil;
import com.hgl.hglaiagent.model.entity.ChatMessage;
import org.springframework.ai.chat.messages.*;

import java.util.List;
import java.util.Map;

/**
 * @author 请别把我整破防
 * @Desription: ChatMessage <=> Message 转换
 */
public class MessageConverter {

    /**
     * 将 Message 转换为 ChatMessage
     */
    public static ChatMessage toChatMessage(Message message, String conversationId) {
        return ChatMessage.builder()
                .conversationId(conversationId)
                .messageType(message.getMessageType())
                .content(message.getText())
                .metadata(JSONUtil.toJsonStr(message.getMetadata()))
                .build();
    }

    /**
     * 将 ChatMessage 转换为 Message
     */
    public static Message toMessage(ChatMessage chatMessage) {
        MessageType messageType = chatMessage.getMessageType();
        String text = chatMessage.getContent();
        Map<String, Object> metadata = JSONUtil.toBean(chatMessage.getMetadata(), Map.class);
        return switch (messageType) {
            case USER -> new UserMessage(text);
            case ASSISTANT -> new AssistantMessage(text, metadata);
            case SYSTEM -> new SystemMessage(text);
            case TOOL -> new ToolResponseMessage(List.of(), metadata);
        };
    }

}
