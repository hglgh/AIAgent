package com.hgl.hglaiagent.chatmemory;

import cn.hutool.core.util.ObjUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.hgl.hglaiagent.converter.MessageConverter;
import com.hgl.hglaiagent.model.entity.ChatMessage;
import com.hgl.hglaiagent.service.ChatMessageService;
import com.hgl.hglaiagent.service.impl.ChatMessageServiceImpl;
import jakarta.annotation.Resource;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.messages.Message;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

/**
 * ClassName: DatabaseChatMemory
 * Package: com.hgl.hglaiagent.chatmemory
 * Description:
 *
 * @Author HGL
 * @Create: 2025/5/3 22:26
 */
@Component
public class DatabaseChatMemory implements ChatMemory {

    private final ChatMessageService chatMessageService;

    public DatabaseChatMemory(ChatMessageService chatMessageService) {
        this.chatMessageService = chatMessageService;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void add(String conversationId, List<Message> messages) {
        if (ObjUtil.isNull(messages)) {
            return;
        }
        List<ChatMessage> chatMessageList = messages.stream().map(message -> MessageConverter.toChatMessage(message, conversationId)).toList();
        chatMessageService.saveOrUpdateBatch(chatMessageList);
    }

    @Override
    public List<Message> get(String conversationId, int lastN) {
        List<ChatMessage> chatMessages = chatMessageService.list(
                new LambdaQueryWrapper<ChatMessage>()
                        .eq(ChatMessage::getConversationId, conversationId)
                        .orderByDesc(ChatMessage::getCreateTime)
                        .last(lastN > 0, "limit " + lastN)
        );
        // 按照时间顺序返回
        if (!chatMessages.isEmpty()) {
            Collections.reverse(chatMessages);
        }
        return chatMessages.stream().map(MessageConverter::toMessage).toList();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void clear(String conversationId) {
        chatMessageService.remove(
                new LambdaQueryWrapper<ChatMessage>()
                        .eq(ChatMessage::getConversationId, conversationId)
        );
    }
}
