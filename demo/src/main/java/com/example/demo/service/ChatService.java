package com.example.demo.service;

import org.springframework.ai.content.Media;

import java.util.List;

/**
 * 暂留
 */
public interface ChatService {
    /**
     * 多轮对话（带记忆）
     * @param conversationId 会话唯一标识（前端生成或使用 sessionId）
     * @param content 用户消息
     * @param model 模型名称（可选）
     */
    String chatWithMemory(String conversationId, String content, String model, List<Media> medias,Integer userId);
}
