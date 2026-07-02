package com.example.demo.service;

import com.example.demo.pojo.Skill;
import org.springframework.stereotype.Service;

import java.util.List;

public interface SkillService {
    /**
     * 新增技能
     */
    Boolean addSkill(Skill skill, Integer currentUserId);

    /**
     * 更新技能
     */
    Boolean updateSkill(Skill skill, Integer currentUserId);

    /**
     * 删除单条技能
     */
    Boolean deleteSkill(Integer id, Integer resumeId, Integer currentUserId);

    /**
     * 批量删除技能
     */
    Boolean deleteBatch(Integer resumeId, List<Integer> ids, Integer currentUserId);

    /**
     * 获取某简历下所有技能（按排序）
     */
    List<Skill> getSkillsByResumeId(Integer resumeId, Integer currentUserId);

    /**
     * 按分类查询技能
     */
    List<Skill> getSkillsByResumeIdAndCategory(Integer resumeId, String category, Integer currentUserId);

    /**
     * 获取某简历下所有不重复的技能分类
     */
    List<String> getDistinctCategories(Integer resumeId, Integer currentUserId);

    /**
     * 批量更新排序
     */
    Boolean sortSkills(Integer resumeId, List<Integer> sortedIds, Integer currentUserId);
}
