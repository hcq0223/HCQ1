package com.example.demo.pojo;

import com.example.demo.enums.ProficiencyLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


/**
 * 技能实体
 * 对应数据库表 skills
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Skill {

    /**
     * 技能ID，主键自增
     */
    private Integer id;

    /**
     * 所属简历ID
     */
    private Integer resumeId;

    /**
     * 技能名称
     */
    private String skillName;

    /**
     * 技能熟练程度 (数据库 ENUM，存储字符串值)
     */
    private String proficiencyLevel;

    /**
     * 技能分类
     */
    private String category;

    /**
     * 排序序号
     */
    private Integer sortOrder;

    /**
     * 记录创建时间
     */
    private LocalDateTime createdAt;

    // ========== 便利方法：枚举转换 ==========

    /**
     * 将数据库字符串转为枚举
     */
    public ProficiencyLevel getProficiencyLevelEnum() {
        return ProficiencyLevel.fromCode(this.proficiencyLevel);
    }

    /**
     * 将枚举值设置到数据库字段
     */
    public void setProficiencyLevelEnum(ProficiencyLevel level) {
        this.proficiencyLevel = level.getCode();
    }
}
