package com.example.demo.service.impl;

import com.example.demo.mapper.CustomSectionMapper;
import com.example.demo.mapper.ResumeMapper;
import com.example.demo.pojo.CustomSection;
import com.example.demo.pojo.Resume;
import com.example.demo.service.CustomSectionService;
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
public class CustomSectionServiceImpl implements CustomSectionService {

    private final CustomSectionMapper customSectionMapper;
    private final ResumeMapper resumeMapper;

    @Override
    @Transactional
    public Boolean addCustomSection(CustomSection customSection, Integer currentUserId) {
        if (customSection == null || customSection.getResumeId() == null
                || !StringUtils.hasText(customSection.getSectionTitle())) {
            return false;
        }

        Resume resume = resumeMapper.selectResumeById(customSection.getResumeId());
        if (resume == null || Boolean.TRUE.equals(resume.getIsDeleted())
                || !resume.getUserId().equals(currentUserId)) {
            return false;
        }

        // 自动排序（追加到末尾）
        List<Integer> existIds = customSectionMapper.selectIdsByResumeId(customSection.getResumeId());
        customSection.setSortOrder(existIds.size());

        int rows = customSectionMapper.insertCustomSection(customSection);
        return rows > 0;
    }

    @Override
    @Transactional
    public Boolean updateCustomSection(CustomSection customSection, Integer currentUserId) {
        if (customSection == null || customSection.getId() == null || customSection.getResumeId() == null) {
            return false;
        }

        Resume resume = resumeMapper.selectResumeById(customSection.getResumeId());
        if (resume == null || Boolean.TRUE.equals(resume.getIsDeleted())
                || !resume.getUserId().equals(currentUserId)) {
            return false;
        }

        CustomSection exist = customSectionMapper.selectById(customSection.getId(), customSection.getResumeId());
        if (exist == null) {
            return false;
        }

        // 空更新防护
        if (customSection.getSectionTitle() == null && customSection.getContent() == null
                && customSection.getSortOrder() == null) {
            return false;
        }

        int rows = customSectionMapper.updateCustomSection(customSection);
        return rows > 0;
    }

    @Override
    @Transactional
    public Boolean deleteCustomSection(Integer id, Integer resumeId, Integer currentUserId) {
        if (id == null || resumeId == null) {
            return false;
        }
        Resume resume = resumeMapper.selectResumeById(resumeId);
        if (resume == null || Boolean.TRUE.equals(resume.getIsDeleted())
                || !resume.getUserId().equals(currentUserId)) {
            return false;
        }
        int rows = customSectionMapper.deleteById(id, resumeId);
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
        int deletedRows = customSectionMapper.deleteCustomSectionBatch(ids, resumeId, currentUserId);
        return deletedRows == ids.size();
    }

    @Override
    @Transactional
    public Boolean sortCustomSections(Integer resumeId, List<Integer> sortedIds, Integer currentUserId) {
        if (CollectionUtils.isEmpty(sortedIds)) {
            return false;
        }
        Resume resume = resumeMapper.selectResumeById(resumeId);
        if (resume == null || Boolean.TRUE.equals(resume.getIsDeleted())
                || !resume.getUserId().equals(currentUserId)) {
            return false;
        }

        List<Integer> existIds = customSectionMapper.selectIdsByResumeId(resumeId);
        if (existIds.size() != sortedIds.size()) {
            return false;
        }
        Set<Integer> sortedSet = new HashSet<>(sortedIds);
        Set<Integer> existSet = new HashSet<>(existIds);
        if (!sortedSet.equals(existSet)) {
            return false;
        }

        List<CustomSectionMapper.SortItem> sortItems = new ArrayList<>();
        for (int i = 0; i < sortedIds.size(); i++) {
            sortItems.add(new CustomSectionMapper.SortItem(sortedIds.get(i), i));
        }

        int rows = customSectionMapper.updateCustomSectionSortOrderBatch(resumeId, sortItems);
        return rows == sortedIds.size();
    }

    @Override
    public List<CustomSection> getCustomSectionsByResumeId(Integer resumeId, Integer currentUserId) {
        if (resumeId == null) {
            return new ArrayList<>();
        }
        Resume resume = resumeMapper.selectResumeById(resumeId);
        if (resume == null || Boolean.TRUE.equals(resume.getIsDeleted())
                || !resume.getUserId().equals(currentUserId)) {
            return new ArrayList<>();
        }
        return customSectionMapper.selectByResumeId(resumeId);
    }

    @Override
    public CustomSection getCustomSectionById(Integer id, Integer resumeId, Integer currentUserId) {
        if (id == null || resumeId == null) {
            return null;
        }
        Resume resume = resumeMapper.selectResumeById(resumeId);
        if (resume == null || Boolean.TRUE.equals(resume.getIsDeleted())
                || !resume.getUserId().equals(currentUserId)) {
            return null;
        }
        return customSectionMapper.selectById(id, resumeId);
    }
}