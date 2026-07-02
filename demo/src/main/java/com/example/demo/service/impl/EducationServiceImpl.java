package com.example.demo.service.impl;

import com.example.demo.mapper.EducationMapper;
import com.example.demo.mapper.ResumeMapper;
import com.example.demo.pojo.Education;
import com.example.demo.pojo.Resume;
import com.example.demo.service.EducationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EducationServiceImpl implements EducationService {
    private final EducationMapper educationMapper;
    private final ResumeMapper resumeMapper;
    @Override
    @Transactional
    public Boolean insertEducation(Education education, Integer resumeId) {
        if (education == null || resumeId == null) {
            return false;
        }
        education.setResumeId(resumeId);
        Integer i = educationMapper.insertEducation(education);
        return i==1;
    }

    @Override
    @Transactional
    public Boolean updateEducation(Education education , Integer currentUserId) {
        // 1. 参数完整性校验
        if (education == null || education.getId() == null || education.getResumeId() == null) {
            return false;
        }

        // 2. 简历权限校验（核心！）
        Resume resume = resumeMapper.selectResumeById(education.getResumeId());
        if (resume == null || Boolean.TRUE.equals(resume.getIsDeleted())
                || !resume.getUserId().equals(currentUserId)) {
            return false;
        }

        // 3. 执行更新
        Integer rows = educationMapper.updateEducation(education);
        return rows != null && rows > 0;
    }

    @Override
    public List<Education> selectEducationByResumeId(Integer resumeId) {
        return educationMapper.selectEducationByResumeId(resumeId);
    }

    @Override
    public Education selectEducationById(Integer id, Integer resumeId) {
        return educationMapper.selectEducationById(id, resumeId);
    }

    @Override
    @Transactional
    public Boolean deleteEducation(Integer id, Integer resumeId) {
        if (educationMapper.selectEducationById(id, resumeId) == null) {
            return false;
        }
        Integer deleteResume = educationMapper.deleteEducation(id, resumeId);
        return deleteResume == 1;
    }

    @Override
    @Transactional
    public Boolean EducationDeleteBatch(Integer resumeId, List<Integer> ids, Integer currentUserId) {
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
        int deletedRows = educationMapper.EducationDeleteBatch(ids, resumeId, currentUserId);

        // 4. 结果校验
        return deletedRows == ids.size();
    }
}
