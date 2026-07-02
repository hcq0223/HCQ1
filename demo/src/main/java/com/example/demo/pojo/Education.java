package com.example.demo.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 教育经历实体
 * 对应数据库表 education
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Education {

    /**
     * 主键ID
     */
    private Integer id;

    /**
     * 所属简历ID
     */
    private Integer resumeId;

    /**
     * 学校名称
     */
    private String schoolName;

    /**
     * 学位
     */
    private String degree;

    /**
     * 专业
     */
    private String major;

    /**
     * GPA绩点 (DECIMAL(3,2))
     */
    private BigDecimal gpa;

    /**
     * 入学日期
     */
    private LocalDate startDate;

    /**
     * 毕业日期（为空表示至今在读）
     */
    private LocalDate endDate;

    /**
     * 教育描述
     */
    private String description;

    /**
     * 学术成就
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
