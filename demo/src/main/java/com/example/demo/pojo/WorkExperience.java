package com.example.demo.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 工作经历实体
 * 对应数据库表 work_experiences
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class WorkExperience {

    /**
     * 主键ID
     */
    private Integer id;

    /**
     * 所属简历ID
     */
    private Integer resumeId;

    /**
     * 公司名称
     */
    private String companyName;

    /**
     * 职位名称
     */
    private String position;

    /**
     * 工作地点
     */
    private String location;

    /**
     * 入职日期
     */
    private LocalDate startDate;

    /**
     * 离职日期（为空表示至今）
     */
    private LocalDate endDate;

    /**
     * 工作描述
     */
    private String description;

    /**
     * 主要成就
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