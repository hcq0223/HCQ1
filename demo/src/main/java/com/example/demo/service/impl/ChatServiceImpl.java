package com.example.demo.service.impl;


import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatModel;
import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatOptions;
import com.example.demo.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.messages.*;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.content.Media;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 暂留
 */
@Service
@RequiredArgsConstructor
public class ChatServiceImpl implements ChatService {
    private final ChatClient chatClient;
    private final DashScopeChatModel chatModel;
    private final ChatMemory chatMemory;   // 新增

    @Value("${spring.ai.dashscope.chat.options.model:qwen-max}")
    private String defaultModel;
    @Value("${spring.ai.dashscope.chat.options.temperature:0.7}")
    private Double temperature;

    @Value("${spring.ai.dashscope.chat.options.max-tokens:8000}")
    private Integer maxTokens;

    @Autowired
    public ChatServiceImpl(ChatClient.Builder chatClientBuilder,
                           DashScopeChatModel chatModel,
                           ChatMemory chatMemory) {   // 构造注入
        this.chatClient = chatClientBuilder
                .defaultSystem("你是一个学生简历优化助手，专门帮助在校学生和应届毕业生完善求职简历。"
                        + "回答要具体、可执行，并提供针对性的修改建议。"
                        + "如果用户消息中包含“系统补充信息”里列出的学生背景信息（如专业、年级、技能、实习经历、项目经验等），"
                        + "请先用简短的中文总结出“该学生的核心优势和待提升方向”，"
                        + "然后再结合这些背景信息来回答用户的具体问题。"
                        + "建议格式：先给予鼓励，再指出问题，最后提供具体的修改示例。")  // 原有系统提示保持不变
                .build();
        this.chatModel = chatModel;
        this.chatMemory = chatMemory;
    }

    // 新增多轮对话方法
    @Override
    public String chatWithMemory(String conversationId, String content, String model,
                                 List<Media> medias) {
        String userContent = content == null ? "" : content.trim();
        if (userContent.isEmpty() && (medias == null || medias.isEmpty())) {
            return "我没有收到问题，请重新输入问题！";
        }

        // 1. 获取历史消息
        // 1. 获取该会话的全部历史消息
        List<Message> allHistory = chatMemory.get(conversationId);
        if (allHistory == null) {
            allHistory = new ArrayList<>();
        }

        // 2. 限制历史条数（避免超出 token 限制）
        int maxHistory = 20;
        List<Message> history = allHistory.size() > maxHistory
                ? new ArrayList<>(allHistory.subList(allHistory.size() - maxHistory, allHistory.size()))
                : new ArrayList<>(allHistory);

        // 2. 构建系统提示（与 ChatClient 的 defaultSystem 保持一致）
        String systemText = "你是一个学生简历优化助手，专门帮助在校学生和应届毕业生完善求职简历。"
                + "回答要具体、可执行，并提供针对性的修改建议。"
                + "如果用户消息中包含“系统补充信息”里列出的学生背景信息（如专业、年级、技能、实习经历、项目经验等），"
                + "请先用简短的中文总结出“该学生的核心优势和待提升方向”，"
                + "然后再结合这些背景信息来回答用户的具体问题。"
                + "建议格式：先给予鼓励，再指出问题，最后提供具体的修改示例。";

        // 3. 拼接完整消息列表：系统消息 + 历史 + 当前用户消息
        UserMessage currentUserMessage;
        if (medias != null && !medias.isEmpty()) {
            UserMessage.Builder builder = UserMessage.builder().text(userContent);
            // 逐个添加媒体（而不是用 .medias(list)）
            for (Media media : medias) {
                builder.media(media);
            }
            currentUserMessage = builder.build();
        } else {
            // 纯文本情况保持原有构造方式（也可用 builder）
            currentUserMessage = new UserMessage(userContent);
        }

        List<Message> fullMessages = new ArrayList<>();
        fullMessages.add(new SystemMessage(systemText));
        fullMessages.addAll(history);
        fullMessages.add(currentUserMessage);

        // 4. 设置模型参数
        DashScopeChatOptions options = DashScopeChatOptions.builder()
                .withModel(model != null ? model : defaultModel)
                .withTemperature(temperature)
                .withMaxToken(maxTokens)
                .withMultiModel(true)
                .build();

        Prompt prompt = new Prompt(fullMessages, options);

        try {
            String reply = chatModel.call(prompt).getResult().getOutput().getText();

            // 5. 将本轮对话存入记忆
            chatMemory.add(conversationId, new UserMessage(userContent));
            chatMemory.add(conversationId, new AssistantMessage(reply));

            return reply;
        } catch (Exception e) {
            throw new RuntimeException("千问多轮对话失败：" + e.getMessage(), e);
        }
    }
}

