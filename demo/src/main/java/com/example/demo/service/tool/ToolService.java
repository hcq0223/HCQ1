package com.example.demo.service.tool;

import com.example.demo.pojo.*;
import com.example.demo.service.*;
import com.example.demo.service.impl.ResumeServiceImpl;
import jakarta.servlet.http.HttpSession;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.List;

@Service
public class ToolService {
    @Autowired
    private ResumeServiceImpl resumeService;

    @Autowired
    private WorkExperienceService workExperienceService;

    @Autowired
    private EducationService educationService;

    @Autowired
    private SkillService skillService;

    @Autowired
    private ProjectExperienceService projectExperienceService;

    @Autowired
    private CertificationService certificationService;

    @Autowired
    private LanguageService languageService;

    @Tool(description = "根据简历标题软删除（放入回收站）当前用户的所有同名简历，返回删除数量")
    public String deleteResume(@ToolParam(description = "简历的标题") String title) {
        try {
            // 从当前请求中获取用户
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            HttpSession session = attributes.getRequest().getSession(false);
            User user = (User) session.getAttribute("user");

            List<Resume> resumes = resumeService.selectResumeTitle(user.getId(), title);
            if (resumes.isEmpty()) {
                return "未找到名为【" + title + "】的简历";
            }

            int count = 0;
            for (Resume r : resumes) {
                resumeService.isDeleteResume(r.getId(), user.getId());
                count++;
            }
            return "已将 " + count + " 份名为【" + title + "】的简历放入回收站";
        } catch (Exception e) {
            return "删除失败：" + e.getMessage();
        }
    }


    @Tool(description = "将回收站里的数据全部清空，注意此操作后数据是真正意义上删除的，需要得到用户的允许才可执行")
    public String emptyRecycleBin() {
        try {
            // 从当前请求中获取用户
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            HttpSession session = attributes.getRequest().getSession(false);
            User user = (User) session.getAttribute("user");
            int i = resumeService.countRecycledResumes(user.getId());
            if (i == 0) {
                return "回收站为空";
            }
            resumeService.deleteResumes(user.getId());
            return "已清空回收站数据";
        } catch (Exception e) {
            return "删除失败：" + e.getMessage();
        }
    }


    @Tool(description = "创建一份新的简历草稿，需要用户提供简历标题和姓名，其他信息可选。创建后状态为草稿。")
    public String createResume(
            @ToolParam(description = "简历标题，必填，如 '我的Java开发简历'") String title,
            @ToolParam(description = "真实姓名，必填") String fullName,
            @ToolParam(description = "邮箱地址，可选") String email,
            @ToolParam(description = "联系电话，可选") String phone,
            @ToolParam(description = "所在城市/地区，可选") String location) {

        try {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes == null) {
                return "无法获取当前会话，请重新登录";
            }
            HttpSession session = attributes.getRequest().getSession(false);
            User user = (User) session.getAttribute("user");

            // 构建 Resume 对象（必填字段校验）
            Resume resume = new Resume();
            resume.setTitle(title);
            resume.setFullName(fullName);
            // 可选字段，传入空字符串时置为 null（根据你的业务逻辑决定）
            resume.setEmail(email != null && !email.isBlank() ? email : null);
            resume.setPhone(phone != null && !phone.isBlank() ? phone : null);
            resume.setLocation(location != null && !location.isBlank() ? location : null);

            boolean success = resumeService.insertResume(resume, user.getId());
            if (success) {
                return "简历【" + title + "】已创建成功，状态为草稿，可继续完善其他信息。";
            } else {
                return "创建简历失败，请检查输入信息是否完整（标题和姓名不能为空）。";
            }

        } catch (Exception e) {
            return "创建简历时发生异常：" + e.getMessage();
        }
    }


    @Tool(description = "更新当前用户的简历信息。可更新标题、姓名、邮箱、电话、所在地、个人简介、LinkedIn、GitHub、个人网站。不支持更新头像。需要用户确认后执行。")
    public String updateResume(
            @ToolParam(description = "简历ID（数字），如果提供则优先使用此ID定位简历") Integer resumeId,
            @ToolParam(description = "简历标题，用于查找要更新的简历（如果未提供ID）。如果存在多个同名简历会提示用户提供ID") String title,
            @ToolParam(description = "新的简历标题（可选）") String newTitle,
            @ToolParam(description = "新的姓名（可选）") String fullName,
            @ToolParam(description = "新的邮箱（可选）") String email,
            @ToolParam(description = "新的电话（可选）") String phone,
            @ToolParam(description = "新的所在地（可选）") String location,
            @ToolParam(description = "新的个人简介（可选）") String professionalSummary,
            @ToolParam(description = "新的LinkedIn链接（可选）") String linkedinUrl,
            @ToolParam(description = "新的GitHub链接（可选）") String githubUrl,
            @ToolParam(description = "新的个人网站链接（可选）") String personalWebsite) {

        try {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes == null) {
                return "无法获取当前会话，请重新登录";
            }
            HttpSession session = attributes.getRequest().getSession(false);
            User user = (User) session.getAttribute("user");

            Resume targetResume = null;

            // 定位要更新的简历
            if (resumeId != null) {
                // 通过ID精确查找（需在service层添加根据ID和用户ID查询的方法）
                targetResume = resumeService.getResumeByIdAndUserId(resumeId, user.getId());
                if (targetResume == null) {
                    return "未找到ID为 " + resumeId + " 的简历，请确认ID是否正确。";
                }
            } else if (title != null && !title.isBlank()) {
                // 通过标题查找
                List<Resume> resumes = resumeService.selectResumeTitle(user.getId(), title);
                if (resumes.isEmpty()) {
                    return "未找到标题为【" + title + "】的简历，请检查标题是否正确。";
                }
                if (resumes.size() > 1) {
                    return "找到多个标题为【" + title + "】的简历，请提供具体的简历ID（数字）以精确更新。";
                }
                targetResume = resumes.get(0);
            } else {
                return "请提供简历ID或标题以定位要更新的简历。";
            }

            // 构建更新对象（只设置非空字段）
            Resume updateData = new Resume();
            updateData.setId(targetResume.getId());
            updateData.setUserId(user.getId()); // 用于SQL的WHERE条件

            // 只有传入非空字符串才更新（注意：null值会被MyBatis的if标签忽略）
            if (newTitle != null && !newTitle.isBlank()) updateData.setTitle(newTitle);
            if (fullName != null && !fullName.isBlank()) updateData.setFullName(fullName);
            if (email != null && !email.isBlank()) updateData.setEmail(email);
            if (phone != null && !phone.isBlank()) updateData.setPhone(phone);
            if (location != null && !location.isBlank()) updateData.setLocation(location);
            if (professionalSummary != null && !professionalSummary.isBlank())
                updateData.setProfessionalSummary(professionalSummary);
            if (linkedinUrl != null && !linkedinUrl.isBlank()) updateData.setLinkedinUrl(linkedinUrl);
            if (githubUrl != null && !githubUrl.isBlank()) updateData.setGithubUrl(githubUrl);
            if (personalWebsite != null && !personalWebsite.isBlank()) updateData.setPersonalWebsite(personalWebsite);


            // 调用service更新
            boolean success = resumeService.updateResume(updateData);
            if (success) {
                return "简历【" + targetResume.getTitle() + "】更新成功。";
            } else {
                return "更新失败，请检查输入信息是否有效。";
            }

        } catch (Exception e) {
            return "更新简历时发生异常：" + e.getMessage();
        }
    }


    @Tool(description = "列出当前用户所有未被删除的简历，返回简历标题列表和基本信息")
    public String listResumes() {
        try {
            ServletRequestAttributes a = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            HttpSession session = a.getRequest().getSession(false);
            User user = (User) session.getAttribute("user");
            List<Resume> resumes = resumeService.selectResumeByUserId(user.getId());
            if (resumes.isEmpty()) {
                return "暂无简历，请先创建一份简历。";
            }
            StringBuilder sb = new StringBuilder("你的简历列表：");
            for (Resume r : resumes) {
                sb.append("  ID: ").append(r.getId())
                        .append(" | 标题: ").append(r.getTitle())
                        .append(" | 姓名: ").append(r.getFullName())
                        .append(" | 状态: ").append("published".equals(r.getStatus()) ? "已发布" : "草稿")
                        .append("\n");
            }
            return sb.toString();
        } catch (Exception e) {
            return "获取简历列表失败：" + e.getMessage();
        }
    }

    @Tool(description = "根据简历ID或标题查看简历的完整详细信息，包括个人基本信息")
    public String getResumeDetail(
            @ToolParam(description = "简历ID（数字），提供ID时优先使用ID查找") Integer resumeId,
            @ToolParam(description = "简历标题，当未提供ID时用标题查找") String title) {
        try {
            ServletRequestAttributes a = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            HttpSession session = a.getRequest().getSession(false);
            User user = (User) session.getAttribute("user");

            Resume resume = null;
            if (resumeId != null) {
                resume = resumeService.getResumeByIdAndUserId(resumeId, user.getId());
                if (resume == null) return "未找到ID为 " + resumeId + " 的简历。";
            } else if (title != null && !title.isBlank()) {
                List<Resume> list = resumeService.selectResumeTitle(user.getId(), title);
                if (list.isEmpty()) return "未找到标题为「" + title + "」的简历。";
                if (list.size() > 1) return "找到多份同名简历，请提供具体的简历ID。";
                resume = list.get(0);
            } else {
                return "请提供简历ID或标题。";
            }

            return "简历详情： \n"
                    + "  ID: " + resume.getId() + "\n "
                    + "  标题: " + resume.getTitle() + " \n "
                    + "  姓名: " + resume.getFullName() + " \n "
                    + "  邮箱: " + (resume.getEmail() != null ? resume.getEmail() : "未设置") + " \n"
                    + "  电话: " + (resume.getPhone() != null ? resume.getPhone() : "未设置") + " \n"
                    + "  所在地: " + (resume.getLocation() != null ? resume.getLocation() : "未设置") + " \n"
                    + "  个人简介: " + (resume.getProfessionalSummary() != null ? resume.getProfessionalSummary() : "未设置") + " \n"
                    + "  状态: " + ("published".equals(resume.getStatus()) ? "已发布" : "草稿");
        } catch (Exception e) {
            return "获取简历详情失败：" + e.getMessage();
        }
    }

    @Tool(description = "将回收站中的简历恢复到正常列表，需要用户确认后执行")
    public String restoreResume(
            @ToolParam(description = "简历的ID（数字）") Integer resumeId,
            @ToolParam(description = "简历标题，当未提供ID时用标题查找") String title) {
        try {
            ServletRequestAttributes a = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            HttpSession session = a.getRequest().getSession(false);
            User user = (User) session.getAttribute("user");

            List<Resume> toRestore = null;
            if (resumeId != null) {
                Resume r = resumeService.getResumeByIdAndUserId(resumeId, user.getId());
                if (r == null) return "未找到ID为 " + resumeId + " 的简历。";
                toRestore = List.of(r);
            } else if (title != null && !title.isBlank()) {
                toRestore = resumeService.selectResumeTitle(user.getId(), title);
                if (toRestore.isEmpty()) return "未找到标题为「" + title + "」的简历。";
            } else {
                return "请提供简历ID或标题。";
            }

            int count = 0;
            for (Resume r : toRestore) {
                if (resumeService.noIsDeleteResume(r.getId(), user.getId())) count++;
            }
            return "已恢复 " + count + " 份简历。";
        } catch (Exception e) {
            return "恢复简历失败：" + e.getMessage();
        }
    }

    @Tool(description = "将指定简历发布，发布后简历可以通过分享链接对外展示")
    public String publishResume(
            @ToolParam(description = "简历ID（数字）") Integer resumeId,
            @ToolParam(description = "简历标题，当未提供ID时用标题查找") String title) {
        try {
            ServletRequestAttributes a = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            HttpSession session = a.getRequest().getSession(false);
            User user = (User) session.getAttribute("user");

            Resume resume = null;
            if (resumeId != null) {
                resume = resumeService.getResumeByIdAndUserId(resumeId, user.getId());
            } else if (title != null && !title.isBlank()) {
                List<Resume> list = resumeService.selectResumeTitle(user.getId(), title);
                if (list.size() == 1) resume = list.get(0);
                else if (list.size() > 1) return "找到多份同名简历，请提供具体的简历ID。";
                else return "未找到标题为「" + title + "」的简历。";
            } else {
                return "请提供简历ID或标题。";
            }
            if (resume == null) return "未找到指定的简历。";

            if ("published".equals(resume.getStatus())) return "该简历已经是发布状态。";
            boolean ok = resumeService.updateStatusPublishedResume(resume.getId(), user.getId());
            return ok ? "简历「" + resume.getTitle() + "」已成功发布。" : "发布失败。";
        } catch (Exception e) {
            return "发布简历失败：" + e.getMessage();
        }
    }

    @Tool(description = "将已发布的简历改为草稿状态")
    public String setDraftResume(
            @ToolParam(description = "简历ID（数字）") Integer resumeId,
            @ToolParam(description = "简历标题，当未提供ID时用标题查找") String title) {
        try {
            ServletRequestAttributes a = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            HttpSession session = a.getRequest().getSession(false);
            User user = (User) session.getAttribute("user");

            Resume resume = null;
            if (resumeId != null) {
                resume = resumeService.getResumeByIdAndUserId(resumeId, user.getId());
            } else if (title != null && !title.isBlank()) {
                List<Resume> list = resumeService.selectResumeTitle(user.getId(), title);
                if (list.size() == 1) resume = list.get(0);
                else if (list.size() > 1) return "找到多份同名简历，请提供具体的简历ID。";
                else return "未找到标题为「" + title + "」的简历。";
            } else {
                return "请提供简历ID或标题。";
            }
            if (resume == null) return "未找到指定的简历。";

            if ("draft".equals(resume.getStatus())) return "该简历已经是草稿状态。";
            boolean ok = resumeService.updateStatusDraftResume(resume.getId(), user.getId());
            return ok ? "简历「" + resume.getTitle() + "」已改为草稿状态。" : "操作失败。";
        } catch (Exception e) {
            return "修改简历状态失败：" + e.getMessage();
        }
    }

    @Tool(description = "向指定简历添加一条工作经历，需要用户确认后执行")
    public String addWorkExperience(
            @ToolParam(description = "所属简历的ID") Integer resumeId,
            @ToolParam(description = "公司名称") String companyName,
            @ToolParam(description = "职位名称") String position,
            @ToolParam(description = "开始日期，格式 yyyy-MM-dd") String startDate,
            @ToolParam(description = "结束日期，格式 yyyy-MM-dd，为空表示至今") String endDate,
            @ToolParam(description = "工作描述") String description,
            @ToolParam(description = "主要成就") String achievements) {
        try {
            ServletRequestAttributes a = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            HttpSession session = a.getRequest().getSession(false);
            User user = (User) session.getAttribute("user");

            WorkExperience exp = new WorkExperience();
            exp.setResumeId(resumeId);
            exp.setCompanyName(companyName);
            exp.setPosition(position);
            exp.setStartDate(java.time.LocalDate.parse(startDate));
            if (endDate != null && !endDate.isBlank()) exp.setEndDate(java.time.LocalDate.parse(endDate));
            exp.setDescription(description);
            exp.setAchievements(achievements);

            boolean ok = workExperienceService.insertWorkExperience(exp, resumeId);
            return ok ? "工作经历已添加到简历。" : "添加失败，请检查简历ID是否正确。";
        } catch (Exception e) {
            return "添加工作经历失败：" + e.getMessage();
        }
    }

    @Tool(description = "向指定简历添加一条教育经历")
    public String addEducation(
            @ToolParam(description = "所属简历的ID") Integer resumeId,
            @ToolParam(description = "学校名称") String schoolName,
            @ToolParam(description = "学位，如 本科、硕士、博士") String degree,
            @ToolParam(description = "专业名称") String major,
            @ToolParam(description = "开始日期，格式 yyyy-MM-dd") String startDate,
            @ToolParam(description = "结束日期，格式 yyyy-MM-dd，为空表示至今在读") String endDate,
            @ToolParam(description = "教育描述") String description,
            @ToolParam(description = "学术成就") String achievements) {
        try {
            ServletRequestAttributes a = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            HttpSession session = a.getRequest().getSession(false);
            User user = (User) session.getAttribute("user");

            Education edu = new Education();
            edu.setResumeId(resumeId);
            edu.setSchoolName(schoolName);
            edu.setDegree(degree);
            edu.setMajor(major);
            edu.setStartDate(java.time.LocalDate.parse(startDate));
            if (endDate != null && !endDate.isBlank()) edu.setEndDate(java.time.LocalDate.parse(endDate));
            edu.setDescription(description);
            edu.setAchievements(achievements);

            boolean ok = educationService.insertEducation(edu, resumeId);
            return ok ? "教育经历已添加到简历。" : "添加失败。";
        } catch (Exception e) {
            return "添加教育经历失败：" + e.getMessage();
        }
    }

    @Tool(description = "向指定简历添加一项技能，需要提供技能名称和熟练度")
    public String addSkill(
            @ToolParam(description = "所属简历的ID") Integer resumeId,
            @ToolParam(description = "技能名称，如 Java、Spring Boot、Python") String skillName,
            @ToolParam(description = "熟练度：beginner(入门)、intermediate(中级)、advanced(高级)、expert(专家)，只能用这四个，且传入时要用英文") String proficiencyLevel,
            @ToolParam(description = "技能分类，如 后端开发、前端开发、数据库等，可选") String category) {
        try {
            ServletRequestAttributes a = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            HttpSession session = a.getRequest().getSession(false);
            User user = (User) session.getAttribute("user");

            Skill skill = new Skill();
            skill.setResumeId(resumeId);
            skill.setSkillName(skillName);
            skill.setProficiencyLevel(proficiencyLevel);
            if (category != null && !category.isBlank()) skill.setCategory(category);

            boolean ok = skillService.addSkill(skill, user.getId());
            return ok ? "技能「" + skillName + "」已添加到简历。" : "添加失败。";
        } catch (Exception e) {
            return "添加技能失败：" + e.getMessage();
        }
    }

    @Tool(description = "向指定简历添加一条项目经历")
    public String addProject(
            @ToolParam(description = "所属简历的ID") Integer resumeId,
            @ToolParam(description = "项目名称") String projectName,
            @ToolParam(description = "担任角色") String role,
            @ToolParam(description = "项目链接，可选") String projectUrl,
            @ToolParam(description = "开始日期，格式 yyyy-MM-dd") String startDate,
            @ToolParam(description = "结束日期，格式 yyyy-MM-dd，为空表示至今") String endDate,
            @ToolParam(description = "项目描述") String description,
            @ToolParam(description = "使用的技术，逗号分隔") String technologiesUsed,
            @ToolParam(description = "项目成果/亮点") String achievements) {
        try {
            ServletRequestAttributes a = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            HttpSession session = a.getRequest().getSession(false);
            User user = (User) session.getAttribute("user");

            ProjectExperience proj = new ProjectExperience();
            proj.setResumeId(resumeId);
            proj.setProjectName(projectName);
            proj.setRole(role);
            if (projectUrl != null && !projectUrl.isBlank()) proj.setProjectUrl(projectUrl);
            proj.setStartDate(java.time.LocalDate.parse(startDate));
            if (endDate != null && !endDate.isBlank()) proj.setEndDate(java.time.LocalDate.parse(endDate));
            proj.setDescription(description);
            proj.setTechnologiesUsed(technologiesUsed);
            proj.setAchievements(achievements);

            boolean ok = projectExperienceService.addProject(proj, user.getId());
            return ok ? "项目经历已添加到简历。" : "添加失败。";
        } catch (Exception e) {
            return "添加项目经历失败：" + e.getMessage();
        }
    }

    @Tool(description = "向指定简历添加一条证书或奖项")
    public String addCertification(
            @ToolParam(description = "所属简历的ID") Integer resumeId,
            @ToolParam(description = "证书/奖项名称") String name,
            @ToolParam(description = "颁发机构") String issuingOrganization,
            @ToolParam(description = "获得日期，格式 yyyy-MM-dd") String issueDate,
            @ToolParam(description = "证书链接，可选") String credentialUrl) {
        try {
            ServletRequestAttributes a = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            HttpSession session = a.getRequest().getSession(false);
            User user = (User) session.getAttribute("user");

            Certification cert = new Certification();
            cert.setResumeId(resumeId);
            cert.setName(name);
            cert.setIssuingOrganization(issuingOrganization);
            cert.setIssueDate(java.time.LocalDate.parse(issueDate));
            if (credentialUrl != null && !credentialUrl.isBlank()) cert.setCredentialUrl(credentialUrl);

            boolean ok = certificationService.addCertification(cert, user.getId());
            return ok ? "证书/奖项已添加到简历。" : "添加失败。";
        } catch (Exception e) {
            return "添加证书失败：" + e.getMessage();
        }
    }

    @Tool(description = "向指定简历添加一门语言能力")
    public String addLanguage(
            @ToolParam(description = "所属简历的ID") Integer resumeId,
            @ToolParam(description = "语言名称，如 英语、日语、法语") String languageName,
            @ToolParam(description = "掌握程度：basic(基础)、conversational(会话)、professional(专业)、native(母语)") String proficiencyLevel) {
        try {
            ServletRequestAttributes a = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            HttpSession session = a.getRequest().getSession(false);
            User user = (User) session.getAttribute("user");

            Language lang = new Language();
            lang.setResumeId(resumeId);
            lang.setLanguageName(languageName);
            lang.setProficiencyLevel(proficiencyLevel);

            boolean ok = languageService.addLanguage(lang, user.getId());
            return ok ? "语言能力「" + languageName + "」已添加到简历。" : "添加失败。";
        } catch (Exception e) {
            return "添加语言能力失败：" + e.getMessage();
        }
    }

    @Tool(description = "恢复回收站中所有已删除的简历")
    public String restoreAllResumes() {
        try {
            ServletRequestAttributes a = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            HttpSession session = a.getRequest().getSession(false);
            User user = (User) session.getAttribute("user");

            List<Resume> trash = resumeService.selectTrashResume(user.getId());
            if (trash.isEmpty()) return "回收站为空，没有可恢复的简历。";

            int count = 0;
            for (Resume r : trash) {
                if (resumeService.noIsDeleteResume(r.getId(), user.getId())) count++;
            }
            return "已恢复 " + count + " 份简历。";
        } catch (Exception e) {
            return "恢复失败：" + e.getMessage();
        }
    }

    @Tool(description = "查看回收站中的简历列表")
    public String listTrashResumes() {
        try {
            ServletRequestAttributes a = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            HttpSession session = a.getRequest().getSession(false);
            User user = (User) session.getAttribute("user");

            List<Resume> trash = resumeService.selectTrashResume(user.getId());
            if (trash.isEmpty()) return "回收站为空。";
            StringBuilder sb = new StringBuilder("回收站中的简历：\n ");
            for (Resume r : trash) {
                sb.append("  ID: ").append(r.getId())
                        .append(" | 标题: ").append(r.getTitle())
                        .append(" | 姓名: ").append(r.getFullName())
                        .append("\n ");
            }
            return sb.toString();
        } catch (Exception e) {
            return "获取回收站列表失败：" + e.getMessage();
        }
    }

    @Tool(description = "删除指定简历中的一条工作经历，需要用户确认后执行")
    public String deleteWorkExperience(
            @ToolParam(description = "工作经历ID") Integer id,
            @ToolParam(description = "所属简历ID") Integer resumeId) {
        try {
            boolean ok = workExperienceService.deleteWorkExperience(id, resumeId);
            return ok ? "工作经历已删除。" : "删除失败，请检查ID是否正确。";
        } catch (Exception e) {
            return "删除工作经历失败：" + e.getMessage();
        }
    }

    @Tool(description = "删除指定简历中的一条教育经历")
    public String deleteEducation(
            @ToolParam(description = "教育经历ID") Integer id,
            @ToolParam(description = "所属简历ID") Integer resumeId) {
        try {
            boolean ok = educationService.deleteEducation(id, resumeId);
            return ok ? "教育经历已删除。" : "删除失败。";
        } catch (Exception e) {
            return "删除教育经历失败：" + e.getMessage();
        }
    }

    @Tool(description = "删除指定简历中的一项技能")
    public String deleteSkill(
            @ToolParam(description = "技能ID") Integer id,
            @ToolParam(description = "所属简历ID") Integer resumeId) {
        try {
            ServletRequestAttributes a = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            HttpSession session = a.getRequest().getSession(false);
            User user = (User) session.getAttribute("user");
            boolean ok = skillService.deleteSkill(id, resumeId, user.getId());
            return ok ? "技能已删除。" : "删除失败。";
        } catch (Exception e) {
            return "删除技能失败：" + e.getMessage();
        }
    }

    @Tool(description = "删除指定简历中的一条项目经历")
    public String deleteProject(
            @ToolParam(description = "项目经历ID") Integer id,
            @ToolParam(description = "所属简历ID") Integer resumeId) {
        try {
            ServletRequestAttributes a = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            HttpSession session = a.getRequest().getSession(false);
            User user = (User) session.getAttribute("user");
            boolean ok = projectExperienceService.deleteProject(id, resumeId, user.getId());
            return ok ? "项目经历已删除。" : "删除失败。";
        } catch (Exception e) {
            return "删除项目经历失败：" + e.getMessage();
        }
    }

    @Tool(description = "删除指定简历中的一条证书/奖项")
    public String deleteCertification(
            @ToolParam(description = "证书ID") Integer id,
            @ToolParam(description = "所属简历ID") Integer resumeId) {
        try {
            ServletRequestAttributes a = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            HttpSession session = a.getRequest().getSession(false);
            User user = (User) session.getAttribute("user");
            boolean ok = certificationService.deleteCertification(id, resumeId, user.getId());
            return ok ? "证书已删除。" : "删除失败。";
        } catch (Exception e) {
            return "删除证书失败：" + e.getMessage();
        }
    }

    @Tool(description = "删除指定简历中的一条语言能力")
    public String deleteLanguage(
            @ToolParam(description = "语言ID") Integer id,
            @ToolParam(description = "所属简历ID") Integer resumeId) {
        try {
            ServletRequestAttributes a = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            HttpSession session = a.getRequest().getSession(false);
            User user = (User) session.getAttribute("user");
            boolean ok = languageService.deleteLanguage(id, resumeId, user.getId());
            return ok ? "语言能力已删除。" : "删除失败。";
        } catch (Exception e) {
            return "删除语言能力失败：" + e.getMessage();
        }
    }


}