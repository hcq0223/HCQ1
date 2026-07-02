package com.example.demo.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 简历基本信息实体
 * 对应数据库表 resumes
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Resume {

    /**
     * 简历ID，主键自增
     */
    private Integer id;

    /**
     * 所属用户ID
     */
    private Integer userId;

    /**
     * 简历标题
     */
    private String title;

    /**
     * 求职者全名
     */
    private String fullName;

    /**
     * 联系邮箱
     */
    private String email;

    /**
     * 联系电话
     */
    private String phone;

    /**
     * 所在地区
     */
    private String location;

    /**
     * LinkedIn 链接
     */
    private String linkedinUrl;

    /**
     * GitHub 链接
     */
    private String githubUrl;

    /**
     * 个人网站
     */
    private String personalWebsite;

    /**
     * 简历头像 URL
     */
    private String avatarUrl;

    /**
     * 个人简介/求职意向
     */
    private String professionalSummary;

    /**
     * 使用的简历模板 ID
     */
    private Integer templateId;

    /**
     * 简历状态 (draft / published)
     * 数据库为 ENUM 字符串，建议在 MyBatis 中映射为 String 或使用自定义 TypeHandler
     */
    private String status;

    /**
     * 软删除标记
     */
    private Boolean isDeleted;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;


}