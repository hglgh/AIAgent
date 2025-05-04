package com.hgl.hglaiagent.chatmemory;

import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson2.JSON;
import com.hgl.hglaiagent.converter.MessageConverter;
import com.hgl.hglaiagent.model.entity.ChatMessage;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.MessageType;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * ClassName: RedisBasedChatMemory
 * Package: com.hgl.hglaiagent.chatmemory
 * Description:
 *
 * @Author HGL
 * @Create: 2025/4/29 16:46
 */
public class RedisBasedChatMemory implements ChatMemory {

    private final RedisTemplate<String, String> redisTemplate;

    public RedisBasedChatMemory(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public void add(String conversationId, List<Message> messages) {
        HashOperations<String, String, String> hashOperations = redisTemplate.opsForHash();
        final int[] count = {0};
        messages.stream().map(message -> MessageConverter.toChatMessage(message, conversationId))
                .forEach(chatMessage -> {
                    count[0]++;
                    String jsonStr = JSONUtil.toJsonStr(chatMessage);
                    hashOperations.put(conversationId,String.valueOf(count[0]), jsonStr);
                });
        redisTemplate.expire(conversationId, 5, TimeUnit.MINUTES);
    }

    @Override
    public List<Message> get(String conversationId, int lastN) {
        HashOperations<String, String, String> hashOperations = redisTemplate.opsForHash();
        List<String> values = hashOperations.values(conversationId);
        if (!values.isEmpty()) {
            return values
                    .stream()
                    .map(str ->JSONUtil.toBean(str, ChatMessage.class)).map(MessageConverter::toMessage)
                    .skip(Math.max(0, values.size() - lastN))
                    .toList();
        }
        return List.of();
    }

    @Override
    public void clear(String conversationId) {
        HashOperations<String, String, String> hashOperations = redisTemplate.opsForHash();
        hashOperations.delete(conversationId);
    }
}
