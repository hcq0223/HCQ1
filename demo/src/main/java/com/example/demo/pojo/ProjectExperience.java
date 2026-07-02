package com.example.demo.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 项目经历实体
 * 对应数据库表 projects
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProjectExperience {

    /**
     * 主键ID
     */
    private Integer id;

    /**
     * 所属简历ID
     */
    private Integer resumeId;

    /**
     * 项目名称
     */
    private String projectName;

    /**
     * 担任角色
     */
    private String role;

    /**
     * 项目链接
     */
    private String projectUrl;

    /**
     * 开始日期
     */
    private LocalDate startDate;

    /**
     * 结束日期（为空表示至今）
     */
    private LocalDate endDate;

    /**
     * 项目描述
     */
    private String description;

    /**
     * 使用技术栈
     */
    private String technologiesUsed;

    /**
     * 项目成果/亮点
     */
    private String achievements;

    /**
     * 排序序号
     */
    private Integer sortOrder;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;
}