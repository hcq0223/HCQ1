package com.example.demo.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 自定义模块实体
 * 对应数据库表 custom_sections
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomSection {

    /**
     * 自定义模块ID，主键自增
     */
    private Integer id;

    /**
     * 所属简历ID
     */
    private Integer resumeId;

    /**
     * 自定义模块标题
     */
    private String sectionTitle;

    /**
     * 自定义模块内容（支持富文本或纯文本）
     */
    private String content;

    /**
     * 排序序号
     */
    private Integer sortOrder;

    /**
     * 记录创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 记录最后修改时间
     */
    private LocalDateTime updatedAt;
}