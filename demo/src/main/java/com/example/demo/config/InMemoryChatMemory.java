package com.example.demo.config;

import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.messages.Message;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryChatMemory implements ChatMemory {
    private final ConcurrentHashMap<String, List<Message>> store = new ConcurrentHashMap<>();
    @Override
    public void add(String conversationId, List<Message> messages) {
        store.computeIfAbsent(conversationId, k -> new ArrayList<>()).addAll(messages);
    }

    @Override
    public List<Message> get(String conversationId) {
        return store.getOrDefault(conversationId, new ArrayList<>());
    }

    @Override
    public void clear(String conversationId) {
        store.remove(conversationId);
    }
}
