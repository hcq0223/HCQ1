package com.example.demo.pojo;

import com.example.demo.enums.TemplateCategory;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 简历模板实体
 * 对应数据库表 resume_templates
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResumeTemplate {

    /**
     * 主键ID，自增
     */
    private Integer id;

    /**
     * 模板名称
     */
    private String name;

    /**
     * 模板描述
     */
    private String description;

    /**
     * 预览图片URL
     */
    private String previewImageUrl;

    /**
     * 模板分类 (数据库 ENUM，存储字符串)
     */
    private String category;

    /**
     * 是否付费模板
     */
    private Boolean isPremium;

    /**
     * 是否启用
     */
    private Boolean isActive;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    // 便利方法：枚举转换
    public TemplateCategory getCategoryEnum() {
        return TemplateCategory.fromCode(this.category);
    }

    public void setCategoryEnum(TemplateCategory category) {
        this.category = category.getCode();
    }
}