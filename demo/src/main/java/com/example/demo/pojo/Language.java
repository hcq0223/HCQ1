package com.example.demo.pojo;

import com.example.demo.enums.LanguageProficiencyLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 语言能力实体
 * 对应数据库表 languages
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Language {

    /**
     * 语言能力ID，主键自增
     */
    private Integer id;

    /**
     * 所属简历ID
     */
    private Integer resumeId;

    /**
     * 语言名称
     */
    private String languageName;

    /**
     * 语言掌握程度
     */
    private String proficiencyLevel;

    /**
     * 排序序号
     */
    private Integer sortOrder;

    /**
     * 记录创建时间
     */
    private LocalDateTime createdAt;

    // 便利方法：转为枚举
    public LanguageProficiencyLevel getProficiencyLevelEnum() {
        return LanguageProficiencyLevel.fromCode(this.proficiencyLevel);
    }

    public void setProficiencyLevelEnum(LanguageProficiencyLevel level) {
        this.proficiencyLevel = level.getCode();
    }

}
