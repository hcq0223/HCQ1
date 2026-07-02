package com.example.demo.service.impl;

import com.example.demo.mapper.ResumeMapper;
import com.example.demo.mapper.WorkExperienceMapper;
import com.example.demo.pojo.Resume;
import com.example.demo.pojo.WorkExperience;
import com.example.demo.service.WorkExperienceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class WorkExperienceServiceImpl implements WorkExperienceService {
    private final WorkExperienceMapper workExperienceMapper;
    private final ResumeMapper resumeMapper;
    @Override
    @Transactional
    public Boolean insertWorkExperience(WorkExperience workExperience, Integer resumeId) {
        if (workExperience == null || resumeId == null) {
            return false;
        }
        workExperience.setResumeId(resumeId);
        Integer i = workExperienceMapper.insert(workExperience);
        return i==1;
    }

    @Override
    @Transactional
    public Boolean updateWorkExperience(WorkExperience workExperience, Integer currentUserId) {
        // 1. 参数完整性校验
        if (workExperience == null || workExperience.getId() == null || workExperience.getResumeId() == null) {
            return false;
        }

        // 2. 简历权限校验（核心！）
        Resume resume = resumeMapper.selectResumeById(workExperience.getResumeId());
        if (resume == null || Boolean.TRUE.equals(resume.getIsDeleted())
                || !resume.getUserId().equals(currentUserId)) {
            return false;
        }

        // 3. 执行更新
        Integer rows = workExperienceMapper.updateByPrimary(workExperience);
        return rows != null && rows > 0;
    }

    @Override
    @Transactional
    public Boolean deleteWorkExperience(Integer id, Integer resumeId) {
        if (workExperienceMapper.selectByPrimaryId(id, resumeId) == null) {
            return false;
        }
        Integer deleteResume = workExperienceMapper.deleteByPrimary(id, resumeId);
        return deleteResume == 1;
    }

    @Override
    public List<WorkExperience> selectWorkExperienceByResumeId(Integer resumeId) {
        return workExperienceMapper.selectByPrimaryResumeId(resumeId);
    }

    @Override
    public WorkExperience selectWorkExperienceById(Integer id, Integer resumeId) {
        return workExperienceMapper.selectByPrimaryId(id, resumeId);
    }

    @Override
    @Transactional
    public Boolean updateWorkExperienceSortOrderBatch(Integer resumeId, List<Integer> sortedIds, Integer currentUserId) {
        // 参数校验
        if (CollectionUtils.isEmpty(sortedIds)) {
            return false;
        }

        // 简历权限校验
        Resume resume = resumeMapper.selectResumeById(resumeId);
        if (resume == null || resume.getIsDeleted() || !resume.getUserId().equals(currentUserId)) {
            return false;
        }

        // 获取该简历下所有工作经历ID（未删除的）
        List<WorkExperience> experiences = workExperienceMapper.selectByPrimaryResumeId(resumeId);
        List<Integer> existIds = experiences.stream()
                .map(WorkExperience::getId)
                .toList();

        if (existIds.size() != sortedIds.size()) {
            return false;
        }

        // 校验ID集合是否完全一致
        Set<Integer> sortedSet = new HashSet<>(sortedIds);
        Set<Integer> existSet = new HashSet<>(existIds);
        if (!sortedSet.equals(existSet)) {
            return false;
        }

        // 构建排序项列表，sortOrder按传入顺序从0开始递增
        List<WorkExperienceMapper.SortItem> sortItems = new ArrayList<>();
        for (int i = 0; i < sortedIds.size(); i++) {
            sortItems.add(new WorkExperienceMapper.SortItem(sortedIds.get(i), i));
        }

        // 执行批量更新排序
        int rows = workExperienceMapper.updateWorkExperienceSortOrderBatch(resumeId, sortItems);
        return rows == sortedIds.size();
    }
    @Override
    @Transactional
    public Boolean WorkExperienceDeleteBatch(Integer resumeId, List<Integer> ids, Integer currentUserId) {
        // 1. 基本参数校验
        if (CollectionUtils.isEmpty(ids) || ids.size() > 100) {
            return false;
        }

        // 2. 简历存在性及权限校验
        Resume resume = resumeMapper.selectResumeById(resumeId);
        if (resume == null || resume.getIsDeleted() || !resume.getUserId().equals(currentUserId)) {
            return false;
        }
        // 3. 执行批量删除
        int deletedRows = workExperienceMapper.WorkExperienceDeleteBatch(ids, resumeId, currentUserId);

        // 4. 结果校验
        return deletedRows == ids.size();
    }
}

