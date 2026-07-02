package com.example.demo.service;
import com.example.demo.pojo.ResumeTemplate;

import java.util.List;

public interface ResumeTemplateService {

    // ==================== 管理端 ====================
    Boolean addTemplate(ResumeTemplate template);

    Boolean updateTemplate(ResumeTemplate template);

    Boolean deleteTemplate(Integer id);

    Boolean toggleActiveStatus(Integer id, Boolean isActive);

    ResumeTemplate getTemplateById(Integer id);

    List<ResumeTemplate> getTemplateList(String category, Boolean isPremium, Boolean isActive, String keyword);

    // ==================== 用户端 ====================
    List<ResumeTemplate> getAvailableTemplates(String category, Boolean isPremium);
}