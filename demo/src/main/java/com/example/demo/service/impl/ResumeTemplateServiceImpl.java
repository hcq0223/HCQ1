package com.example.demo.service.impl;

import com.example.demo.mapper.ResumeTemplateMapper;
import com.example.demo.pojo.ResumeTemplate;
import com.example.demo.service.ResumeTemplateService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ResumeTemplateServiceImpl implements ResumeTemplateService {
    private final ResumeTemplateMapper resumeTemplateMapper;

    // ==================== 管理端实现 ====================

    @Override
    @Transactional
    public Boolean addTemplate(ResumeTemplate template) {
        if (template == null || !StringUtils.hasText(template.getName())) {
            return false;
        }
        // 设置默认值
        if (template.getIsActive() == null) {
            template.setIsActive(true);
        }
        if (template.getIsPremium() == null) {
            template.setIsPremium(false);
        }
        if (!StringUtils.hasText(template.getCategory())) {
            template.setCategory("professional");
        }

        int rows = resumeTemplateMapper.insertResumeTemplate(template);
        return rows > 0;
    }

    @Override
    @Transactional
    public Boolean updateTemplate(ResumeTemplate template) {
        if (template == null || template.getId() == null) {
            return false;
        }
        // 判断是否有实际更新字段
        if (template.getName() == null && template.getDescription() == null
                && template.getPreviewImageUrl() == null && template.getCategory() == null
                && template.getIsPremium() == null && template.getIsActive() == null) {
            return false;
        }
        int rows = resumeTemplateMapper.updateResumeTemplate(template);
        return rows > 0;
    }

    @Override
    @Transactional
    public Boolean deleteTemplate(Integer id) {
        if (id == null) {
            return false;
        }
        int rows = resumeTemplateMapper.deleteById(id);
        return rows > 0;
    }

    @Override
    @Transactional
    public Boolean toggleActiveStatus(Integer id, Boolean isActive) {
        if (id == null || isActive == null) {
            return false;
        }
        int rows = resumeTemplateMapper.updateActiveStatus(id, isActive);
        return rows > 0;
    }

    @Override
    public ResumeTemplate getTemplateById(Integer id) {
        if (id == null) {
            return null;
        }
        return resumeTemplateMapper.selectById(id);
    }

    @Override
    public List<ResumeTemplate> getTemplateList(String category, Boolean isPremium, Boolean isActive, String keyword) {
        return resumeTemplateMapper.selectList(category, isPremium, isActive, keyword);
    }

    // ==================== 用户端实现 ====================

    @Override
    public List<ResumeTemplate> getAvailableTemplates(String category, Boolean isPremium) {
        // 用户端只查询启用的模板
        return resumeTemplateMapper.selectList(category, isPremium, true, null);
    }
}
