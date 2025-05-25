package com.hgl.hglaiagent.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.hgl.hglaiagent.mapper.ChatMessageMapper;
import com.hgl.hglaiagent.model.entity.ChatMessage;
import com.hgl.hglaiagent.service.ChatMessageService;
import org.springframework.stereotype.Service;

/**
* @author 请别把我整破防
* @description 针对表【chat_message(聊天消息表)】的数据库操作Service实现
* @createDate 2025-05-03 22:37:53
*/
@Service
public class ChatMessageServiceImpl extends ServiceImpl<ChatMessageMapper, ChatMessage>
    implements ChatMessageService{

}




