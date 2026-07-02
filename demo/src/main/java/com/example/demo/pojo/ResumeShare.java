package com.example.demo.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 简历分享记录实体
 * 对应数据库表 resume_shares
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResumeShare {

    /**
     * 分享记录ID，主键自增
     */
    private Integer id;

    /**
     * 被分享的简历ID
     */
    private Integer resumeId;

    /**
     * 分享令牌（随机生成的唯一字符串）
     */
    private String shareToken;

    /**
     * 分享过期时间（为空表示永久有效）
     */
    private LocalDateTime expiresAt;

    /**
     * 累计浏览次数
     */
    private Integer viewCount;

    /**
     * 分享状态标记（true-有效，false-已手动关闭或过期失效）
     */
    private Boolean isActive;

    /**
     * 分享创建时间
     */
    private LocalDateTime createdAt;
}
