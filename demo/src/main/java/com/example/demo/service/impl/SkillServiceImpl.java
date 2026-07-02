package com.example.demo.service.impl;

import com.example.demo.mapper.ResumeMapper;
import com.example.demo.mapper.SkillMapper;
import com.example.demo.pojo.Resume;
import com.example.demo.pojo.Skill;
import com.example.demo.service.SkillService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class SkillServiceImpl implements SkillService {
    private final SkillMapper skillMapper;
    private final ResumeMapper resumeMapper;

    @Override
    @Transactional
    public Boolean addSkill(Skill skill, Integer currentUserId) {
        // 参数校验
        if (skill == null || skill.getResumeId() == null || !StringUtils.hasText(skill.getSkillName())) {
            return false;
        }

        // 简历权限校验
        Resume resume = resumeMapper.selectResumeById(skill.getResumeId());
        if (resume == null || Boolean.TRUE.equals(resume.getIsDeleted())
                || !resume.getUserId().equals(currentUserId)) {
            return false;
        }

        // 默认排序（当前最大 sort_order + 1，若没有记录则从0开始）
        List<Integer> existIds = skillMapper.selectIdsByResumeId(skill.getResumeId());
        int maxSort = existIds.isEmpty() ? 0 : existIds.size(); // 简单用数量作为新排序，也可查询实际最大值
        skill.setSortOrder(maxSort);

        // 熟练度默认处理
        if (skill.getProficiencyLevel() == null) {
            skill.setProficiencyLevel("intermediate");
        }

        int rows = skillMapper.insertSkill(skill);
        return rows > 0;
    }

    @Override
    @Transactional
    public Boolean updateSkill(Skill skill, Integer currentUserId) {
        if (skill == null || skill.getId() == null || skill.getResumeId() == null) {
            return false;
        }

        // 简历权限校验
        Resume resume = resumeMapper.selectResumeById(skill.getResumeId());
        if (resume == null || Boolean.TRUE.equals(resume.getIsDeleted())
                || !resume.getUserId().equals(currentUserId)) {
            return false;
        }

        // 校验技能是否存在
        Skill exist = skillMapper.selectById(skill.getId(), skill.getResumeId());
        if (exist == null) {
            return false;
        }

        // 空更新判断（除了 id 和 resumeId 其他全为空）
        if (skill.getSkillName() == null && skill.getProficiencyLevel() == null
                && skill.getCategory() == null && skill.getSortOrder() == null) {
            return false;
        }

        int rows = skillMapper.updateSkill(skill);
        return rows > 0;
    }

    @Override
    @Transactional
    public Boolean deleteSkill(Integer id, Integer resumeId, Integer currentUserId) {
        if (id == null || resumeId == null) {
            return false;
        }

        Resume resume = resumeMapper.selectResumeById(resumeId);
        if (resume == null || Boolean.TRUE.equals(resume.getIsDeleted())
                || !resume.getUserId().equals(currentUserId)) {
            return false;
        }

        int rows = skillMapper.deleteById(id, resumeId);
        return rows > 0;
    }

    @Override
    @Transactional
    public Boolean deleteBatch(Integer resumeId, List<Integer> ids, Integer currentUserId) {
        if (CollectionUtils.isEmpty(ids) || ids.size() > 100) {
            return false;
        }

        Resume resume = resumeMapper.selectResumeById(resumeId);
        if (resume == null || Boolean.TRUE.equals(resume.getIsDeleted())
                || !resume.getUserId().equals(currentUserId)) {
            return false;
        }

        int deletedRows = skillMapper.SkillDeleteBatch(ids, resumeId, currentUserId);
        return deletedRows == ids.size();
    }

    @Override
    public List<Skill> getSkillsByResumeId(Integer resumeId, Integer currentUserId) {
        if (resumeId == null) {
            return null;
        }
        // 权限校验
        Resume resume = resumeMapper.selectResumeById(resumeId);
        if (resume == null || Boolean.TRUE.equals(resume.getIsDeleted())
                || !resume.getUserId().equals(currentUserId)) {
            return new ArrayList<>();
        }
        return skillMapper.selectByResumeId(resumeId);
    }

    @Override
    public List<Skill> getSkillsByResumeIdAndCategory(Integer resumeId, String category, Integer currentUserId) {
        if (resumeId == null) {
            return new ArrayList<>();
        }
        Resume resume = resumeMapper.selectResumeById(resumeId);
        if (resume == null || Boolean.TRUE.equals(resume.getIsDeleted())
                || !resume.getUserId().equals(currentUserId)) {
            return new ArrayList<>();
        }
        return skillMapper.selectByResumeIdAndCategory(resumeId, category);
    }

    @Override
    public List<String> getDistinctCategories(Integer resumeId, Integer currentUserId) {
        if (resumeId == null) {
            return new ArrayList<>();
        }
        Resume resume = resumeMapper.selectResumeById(resumeId);
        if (resume == null || Boolean.TRUE.equals(resume.getIsDeleted())
                || !resume.getUserId().equals(currentUserId)) {
            return new ArrayList<>();
        }
        return skillMapper.selectDistinctCategoriesByResumeId(resumeId);
    }

    @Override
    @Transactional
    public Boolean sortSkills(Integer resumeId, List<Integer> sortedIds, Integer currentUserId) {
        if (CollectionUtils.isEmpty(sortedIds)) {
            return false;
        }

        Resume resume = resumeMapper.selectResumeById(resumeId);
        if (resume == null || Boolean.TRUE.equals(resume.getIsDeleted())
                || !resume.getUserId().equals(currentUserId)) {
            return false;
        }

        List<Integer> existIds = skillMapper.selectIdsByResumeId(resumeId);
        if (existIds.size() != sortedIds.size()) {
            return false;
        }

        Set<Integer> sortedSet = new HashSet<>(sortedIds);
        Set<Integer> existSet = new HashSet<>(existIds);
        if (!sortedSet.equals(existSet)) {
            return false;
        }

        List<SkillMapper.SortItem> sortItems = new ArrayList<>();
        for (int i = 0; i < sortedIds.size(); i++) {
            sortItems.add(new SkillMapper.SortItem(sortedIds.get(i), i));
        }

        int rows = skillMapper.updateSkillSortOrderBatch(resumeId, sortItems);
        return rows == sortedIds.size();
    }
}
