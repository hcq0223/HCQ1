package com.example.demo.service.impl;

import com.example.demo.mapper.LanguageMapper;
import com.example.demo.mapper.ResumeMapper;
import com.example.demo.pojo.Language;
import com.example.demo.pojo.Resume;
import com.example.demo.service.LanguageService;
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
public class LanguageServiceImpl implements LanguageService {
    private final LanguageMapper languageMapper;
    private final ResumeMapper resumeMapper;

    @Override
    @Transactional
    public Boolean addLanguage(Language language, Integer currentUserId) {
        // 参数校验
        if (language == null || language.getResumeId() == null
                || !StringUtils.hasText(language.getLanguageName())) {
            return false;
        }

        // 简历权限校验
        Resume resume = resumeMapper.selectResumeById(language.getResumeId());
        if (resume == null || Boolean.TRUE.equals(resume.getIsDeleted())
                || !resume.getUserId().equals(currentUserId)) {
            return false;
        }

        // 熟练度默认值
        if (!StringUtils.hasText(language.getProficiencyLevel())) {
            language.setProficiencyLevel("professional");
        }

        // 自动设定排序号（追加到末尾）
        List<Integer> existIds = languageMapper.selectIdsByResumeId(language.getResumeId());
        language.setSortOrder(existIds.size());

        int rows = languageMapper.insertLanguage(language);
        return rows > 0;
    }

    @Override
    @Transactional
    public Boolean updateLanguage(Language language, Integer currentUserId) {
        if (language == null || language.getId() == null || language.getResumeId() == null) {
            return false;
        }

        // 简历权限校验
        Resume resume = resumeMapper.selectResumeById(language.getResumeId());
        if (resume == null || Boolean.TRUE.equals(resume.getIsDeleted())
                || !resume.getUserId().equals(currentUserId)) {
            return false;
        }

        // 校验语言记录是否存在
        Language exist = languageMapper.selectById(language.getId(), language.getResumeId());
        if (exist == null) {
            return false;
        }

        // 空更新防护
        if (language.getLanguageName() == null
                && language.getProficiencyLevel() == null
                && language.getSortOrder() == null) {
            return false;
        }

        int rows = languageMapper.updateLanguage(language);
        return rows > 0;
    }

    @Override
    @Transactional
    public Boolean deleteLanguage(Integer id, Integer resumeId, Integer currentUserId) {
        if (id == null || resumeId == null) {
            return false;
        }
        Resume resume = resumeMapper.selectResumeById(resumeId);
        if (resume == null || Boolean.TRUE.equals(resume.getIsDeleted())
                || !resume.getUserId().equals(currentUserId)) {
            return false;
        }
        int rows = languageMapper.deleteLanguage(id, resumeId);
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
        int deletedRows = languageMapper.deleteLanguageBatch(ids, resumeId, currentUserId);
        return deletedRows == ids.size();
    }

    @Override
    @Transactional
    public Boolean sortLanguages(Integer resumeId, List<Integer> sortedIds, Integer currentUserId) {
        if (CollectionUtils.isEmpty(sortedIds)) {
            return false;
        }
        Resume resume = resumeMapper.selectResumeById(resumeId);
        if (resume == null || Boolean.TRUE.equals(resume.getIsDeleted())
                || !resume.getUserId().equals(currentUserId)) {
            return false;
        }

        List<Integer> existIds = languageMapper.selectIdsByResumeId(resumeId);
        if (existIds.size() != sortedIds.size()) {
            return false;
        }
        Set<Integer> sortedSet = new HashSet<>(sortedIds);
        Set<Integer> existSet = new HashSet<>(existIds);
        if (!sortedSet.equals(existSet)) {
            return false;
        }

        List<LanguageMapper.SortItem> sortItems = new ArrayList<>();
        for (int i = 0; i < sortedIds.size(); i++) {
            sortItems.add(new LanguageMapper.SortItem(sortedIds.get(i), i));
        }

        int rows = languageMapper.updateLanguageSortOrderBatch(resumeId, sortItems);
        return rows == sortedIds.size();
    }

    @Override
    public List<Language> getLanguagesByResumeId(Integer resumeId, Integer currentUserId) {
        if (resumeId == null) {
            return new ArrayList<>();
        }
        Resume resume = resumeMapper.selectResumeById(resumeId);
        if (resume == null || Boolean.TRUE.equals(resume.getIsDeleted())
                || !resume.getUserId().equals(currentUserId)) {
            return new ArrayList<>();
        }
        return languageMapper.selectByLanguageResumeId(resumeId);
    }

    @Override
    public Language getLanguageById(Integer id, Integer resumeId, Integer currentUserId) {
        if (id == null || resumeId == null) {
            return null;
        }
        Resume resume = resumeMapper.selectResumeById(resumeId);
        if (resume == null || Boolean.TRUE.equals(resume.getIsDeleted())
                || !resume.getUserId().equals(currentUserId)) {
            return null;
        }
        return languageMapper.selectById(id, resumeId);
    }
}
