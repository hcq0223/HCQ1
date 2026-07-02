package com.example.demo.pojo;

import lombok.Data;

import java.util.List;
// 简历详情视图对象，用于前端展示
@Data
public class ResumeDetailVO {
    // 简历基本信息
    private Integer resumeId;
    private String title;
    private String fullName;
    private String email;
    private String phone;
    private String location;
    private String linkedinUrl;
    private String githubUrl;
    private String personalWebsite;
    private String avatarUrl;
    private String professionalSummary;
    private Integer templateId;
    private String templateCategory;
    private String status;
    private String createdAt;
    private String updatedAt;

    // 各个模块列表（按排序返回）
    private List<WorkExperience> workExperiences;
    private List<Education> educations;
    private List<Skill> skills;
    private List<ProjectExperience> projects;
    private List<Certification> certifications;
    private List<Language> languages;
    private List<CustomSection> customSections;
}