package com.example.demo.service;

import com.example.demo.mapper.*;
import com.example.demo.pojo.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;



@Service
@RequiredArgsConstructor
public class ResumeDetailService {

    private final ResumeMapper resumeMapper;
    private final WorkExperienceMapper workExperienceMapper;
    private final EducationMapper educationMapper;
    private final SkillMapper skillMapper;
    private final ProjectExperienceMapper projectExperienceMapper;
    private final CertificationMapper certificationMapper;
    private final LanguageMapper languageMapper;
    private final CustomSectionMapper customSectionMapper;
    private final ResumeTemplateMapper resumeTemplateMapper;

    /**
     * 简历所有者查看自己的简历（完整字段，无需脱敏）
     */
    public ResumeDetailVO getOwnResume(Integer resumeId, Integer userId) {
        Resume resume = resumeMapper.selectResumeById(resumeId);
        if (resume == null || Boolean.TRUE.equals(resume.getIsDeleted())) {
            return null;
        }
        if (!resume.getUserId().equals(userId)) {
            return null; // 无权查看
        }

        ResumeDetailVO vo = buildBaseVO(resume);
        fillModuleLists(vo, resumeId);
        return vo;
    }

    /**
     * 公开访问（通过分享链接）查看已发布简历（脱敏处理）
     */
    public ResumeDetailVO getPublicResume(Integer resumeId) {
        Resume resume = resumeMapper.selectResumeById(resumeId);
        if (resume == null || Boolean.TRUE.equals(resume.getIsDeleted())) {
            return null;
        }
        if (!"published".equals(resume.getStatus())) {
            return null;
        }

        ResumeDetailVO vo = buildBaseVO(resume);
        // 脱敏：隐藏邮箱和电话的部分内容
        vo.setEmail(maskEmail(resume.getEmail()));
        vo.setPhone(maskPhone(resume.getPhone()));
        // 其他敏感字段根据需要处理，如 linkedin_url 等
        fillModuleLists(vo, resumeId);
        return vo;
    }

    private ResumeDetailVO buildBaseVO(Resume resume) {
        ResumeDetailVO vo = new ResumeDetailVO();
        BeanUtils.copyProperties(resume, vo);
        vo.setResumeId(resume.getId());
        if (resume.getTemplateId() != null) {
            ResumeTemplate tpl = resumeTemplateMapper.selectById(resume.getTemplateId());
            if (tpl != null) vo.setTemplateCategory(tpl.getCategory());
        }
        return vo;
    }

    private void fillModuleLists(ResumeDetailVO vo, Integer resumeId) {
        vo.setWorkExperiences(workExperienceMapper.selectByPrimaryResumeId(resumeId));
        vo.setEducations(educationMapper.selectEducationByResumeId(resumeId));
        vo.setSkills(skillMapper.selectByResumeId(resumeId));
        vo.setProjects(projectExperienceMapper.selectByProjectResumeId(resumeId));
        vo.setCertifications(certificationMapper.selectByResumeId(resumeId));
        vo.setLanguages(languageMapper.selectByLanguageResumeId(resumeId));
        vo.setCustomSections(customSectionMapper.selectByResumeId(resumeId));
    }

    // 简单脱敏示例
    private String maskEmail(String email) {
        if (email == null || !email.contains("@")) return email;
        String[] parts = email.split("@");
        String name = parts[0];
        if (name.length() <= 3) return name + "***@" + parts[1];
        return name.substring(0, 3) + "***@" + parts[1];
    }

    private String maskPhone(String phone) {
        if (phone == null || phone.length() < 7) return phone;
        return phone.substring(0, 3) + "****" + phone.substring(phone.length() - 4);
    }
}
