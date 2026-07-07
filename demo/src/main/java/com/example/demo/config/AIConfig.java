package com.example.demo.config;


import jakarta.annotation.PostConstruct;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * DashScope & AI 相关配置
 */
@Configuration
public class AIConfig {

    @PostConstruct
    public void init() {
        // 增加 DashScope SDK 超时时间，防止 AI 响应慢导致 timeout
        System.setProperty("dashscope.sdk.http.connectTimeout", "30000");
        System.setProperty("dashscope.sdk.http.readTimeout", "180000");
    }

    @Bean
    public ChatMemory chatMemory() {
        return new InMemoryChatMemory();
    }

}