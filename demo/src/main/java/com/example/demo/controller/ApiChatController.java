package com.example.demo.controller;

import com.example.demo.pojo.*;
import com.example.demo.service.*;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.content.Media;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * ai接口
 */


@RestController
@RequestMapping("/hcq/chat")
@RequiredArgsConstructor
public class ApiChatController {
    private final ChatService chatService;
    private final ResumeService resumeBasicInfoService;
    private final WorkExperienceService workExperienceService;
    private final EducationService educationService;
    private final SkillService skillService;
    private final CertificationService certificationService;
    private final ProjectExperienceService projectExperienceService;
    private final LanguageService languageService;
    /**
     * 多轮对话接口（带记忆）
     *
     */
    @PostMapping("/chatWithMemory")
    public Map<String, Object> chatWithMemory(@RequestParam("content") String content,
                                              @RequestParam(required = false) String model,
                                              @RequestParam(required = false) String conversationId,
                                              @RequestParam(required = false) Integer userId,
                                              @RequestParam(required = false) MultipartFile[] files) {
        Map<String, Object> result = new HashMap<>();
        try {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            HttpSession session = attributes.getRequest().getSession(false);
            User user = (User) session.getAttribute("user");

            if (user == null && userId == null) {
                result.put("success", false);
                result.put("message", "未登录");
                return result;
            }
            if (userId != null) {
                user.setId(userId);
            }
            // 若未传入 conversationId，使用 sessionId 作为默认记忆标识
            if (conversationId == null || conversationId.isEmpty()) {
                conversationId = session.getId();
            }

            // 拼接用户背景信息
            String userInfo = getUserInfo(content, user);

            // ★ 处理上传的文件，转换为 Media 列表
            List<Media> mediaList = null;
            if (files != null && files.length > 0) {
                mediaList = new ArrayList<>();
                for (MultipartFile file : files) {
                    // 判断文件是否为空
                    if (!file.isEmpty()) {
                        // 解析 MIME 类型（如 image/png）
                        MediaType mediaType = MediaType.parseMediaType(file.getContentType());
                        // 创建 Media 对象（使用 MultipartFileResource 包装文件）
                        Media media = new Media(mediaType, new ByteArrayResource(file.getBytes()));
                        mediaList.add(media);
                    }
                }
                // 若所有文件均为空，则置 null
                if (mediaList.isEmpty()) {
                    mediaList = null;
                }
            }
            // 调用带记忆的服务方法
            String reply = chatService.chatWithMemory(conversationId, userInfo, model,mediaList, user.getId());

            result.put("success", true);
            result.put("result", reply);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", e.getMessage());
        }
        return result;
    }


    public String getUserInfo(String content, User user) {
        StringBuilder sb = new StringBuilder();
        //基本信息为空时
        List<Resume> resumes = resumeBasicInfoService.selectResumeByUserId(user.getId());
        if (resumes == null) {
            return "请先完善个人信息";
        }

        //添加初始消息
        sb.append(content == null ? "" : content.trim()).append("\n");
        sb.append("【系统补充信息】该学生的个人信息情况如下：\n");
        sb.append("用户的userId:").append(user.getId()).append("\n");
        sb.append("姓名: ").append(resumes.get(0).getFullName()).append("\n");
        for (int hhh = 0; hhh < resumes.size(); hhh++){

        sb.append("简历ID: ").append(resumes.get(hhh).getId()).append("\n");
        sb.append("个人简介/求职意向: ").append(resumes.get(hhh).getProfessionalSummary()).append("\n");

        List<WorkExperience> workList = workExperienceService.selectWorkExperienceByResumeId(resumes.get(hhh).getId());
        if (workList != null && !workList.isEmpty()) {
            sb.append("工作经历:\n");
            for (int i = 0; i < workList.size(); i++) {
                WorkExperience exp = workList.get(i);
                sb.append(String.format("  %d. %s \n",
                        i + 1,
                        exp.getCompanyName()));

            }
        }

        List<Education> educationList = educationService.selectEducationByResumeId(resumes.get(hhh).getId());
        if (educationList != null && !educationList.isEmpty()) {
            sb.append("教育背景:\n");
            for (Education edu : educationList) {
                sb.append(String.format("  - %s | %s |\n",
                        edu.getSchoolName(),
                        edu.getMajor(),
                        edu.getDegree()));
                if (edu.getAchievements() != null) {
                    sb.append("     学术成就: ").append(edu.getAchievements()).append("\n");
                }
            }
        }

        List<Skill> skills = skillService.getSkillsByResumeId(resumes.get(hhh).getId(), user.getId());
        if (skills != null && !skills.isEmpty()) {
            sb.append("技能: ");
            sb.append(skills.stream()
                    .map(s -> s.getSkillName() + "(" + s.getProficiencyLevel() + ")")
                    .collect(Collectors.joining("、")));
            sb.append("\n");
        }

        List<ProjectExperience> projectExperienceList = projectExperienceService.getProjectsByResumeId(resumes.get(hhh).getId(), user.getId());
        if (projectExperienceList != null && !projectExperienceList.isEmpty()) {
            sb.append("项目经历:\n");
            for (ProjectExperience project : projectExperienceList) {
                sb.append(String.format("  - %s \n",
                        project.getProjectName()));
                if (project.getDescription() != null) {
                    sb.append("     项目描述: ").append(project.getDescription()).append("\n");
                }
                if (project.getAchievements() != null) {
                    sb.append("     '项目成果/亮点: ").append(project.getAchievements()).append("\n");
                }
            }
        }

        List<Certification> certifications = certificationService.getCertificationsByResumeId(resumes.get(hhh).getId(), user.getId());
        if (certifications != null && !certifications.isEmpty()) {
            sb.append("证书与奖励: ");
            sb.append(certifications.stream()
                    .map(c -> c.getName() + "(" + c.getIssuingOrganization() + ")")
                    .collect(Collectors.joining("、")));
            sb.append("\n");
        }

        List<Language> languages = languageService.getLanguagesByResumeId(resumes.get(hhh).getId(), user.getId());
        if (languages != null && !languages.isEmpty()) {
            sb.append("语言能力: ");
            sb.append(languages.stream()
                    .map(l -> l.getLanguageName() + "(" + l.getProficiencyLevel() + ")")
                    .collect(Collectors.joining("、")));
            sb.append("\n");
        }
        }
        return sb.toString();
    }
}

