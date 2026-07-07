package com.example.demo.service.impl;


import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatOptions;
import com.example.demo.service.ChatService;
import com.example.demo.service.tool.ToolService;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.messages.*;

import org.springframework.ai.content.Media;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 暂留
 */
@Service
@RequiredArgsConstructor
public class ChatServiceImpl implements ChatService {
    private final ChatClient chatClient;
    //private final DashScopeChatModel chatModel;
    private final ChatMemory chatMemory;   // 新增

    @Value("${spring.ai.dashscope.chat.options.model}")
    private String defaultModel;
    @Value("${spring.ai.dashscope.chat.options.temperature}")
    private Double temperature;

    @Value("${spring.ai.dashscope.chat.options.max-tokens}")
    private Integer maxTokens;

    @Autowired
    public ChatServiceImpl(ChatClient.Builder chatClientBuilder,
                           ChatMemory chatMemory,
                           ToolService toolService) {   // 构造注入
        this.chatClient = chatClientBuilder
                .defaultSystem("你是一个学生简历优化助手，专门帮助在校学生和应届毕业生完善求职简历。"
                        + "回答要具体、可执行，并提供针对性的修改建议。"
                        + "如果用户消息中包含“系统补充信息”里列出的学生背景信息（如专业、年级、技能、实习经历、项目经验等），"
                        + "请先用简短的中文总结出“该学生的核心优势和待提升方向”，"
                        + "然后再结合这些背景信息来回答用户的具体问题。"
                        +"在涉及增删改（除了查询）的业务前，必须等用户回复“确认”后再调用tool"
                        +"不能给用户返回json格式的回答，只需返回纯文本。"
                        + "建议格式：先给予鼓励，再指出问题，最后提供具体的修改示例。【注意】对于用户的增删改的需求，不需要用‘建议格式’，只需简短询问确认即可")
                .defaultAdvisors(new SimpleLoggerAdvisor())
                .defaultTools(toolService)
                .build();

        this.chatMemory = chatMemory;
    }

    @Override
    public String chatWithMemory(String conversationId, String content, String model,
                                 List<Media> medias, Integer userId) {
        String userContent = content == null ? "" : content.trim();
        if (userContent.isEmpty() && (medias == null || medias.isEmpty())) {
            return "我没有收到问题，请重新输入问题！";
        }

        String realKey = userId + "_" + conversationId;

        synchronized (realKey.intern()) {
            // 1. 获取历史，并截取最近10条
            List<Message> allHistory = chatMemory.get(realKey);
            List<Message> history;
            if (allHistory != null && allHistory.size() > 10) {
                history = new ArrayList<>(allHistory.subList(allHistory.size() - 10, allHistory.size()));
            } else {
                history = allHistory != null ? new ArrayList<>(allHistory) : new ArrayList<>();
            }

            // 2. 构建当前用户消息（支持图片等多媒体）
            UserMessage currentUserMessage;
            if (medias != null && !medias.isEmpty()) {
                UserMessage.Builder builder = UserMessage.builder().text(userContent);
                for (Media media : medias) {
                    builder.media(media);
                }
                currentUserMessage = builder.build();
            } else {
                currentUserMessage = new UserMessage(userContent);
            }

            // 3. 拼接 API 消息（历史 + 当前）
            List<Message> apiMessages = new ArrayList<>(history);
            apiMessages.add(currentUserMessage);

            // 4. 设置模型参数
            DashScopeChatOptions options = DashScopeChatOptions.builder()
                    .withModel(model != null ? model : defaultModel)
                    .withTemperature(temperature)
                    .withMaxToken(maxTokens)
                    .withMultiModel(true)
                    .build();

            // 5. 调用 AI
            String reply = chatClient.prompt()
                    .messages(apiMessages)
                    .options(options)
                    .call()
                    .content();

            // 6. 存储本轮对话（只存一次）
            chatMemory.add(realKey, currentUserMessage);
            chatMemory.add(realKey, new AssistantMessage(reply));

            return reply;
        }
    }
}

