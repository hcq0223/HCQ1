import sys

fp = "D:/JL/demo/src/main/java/com/example/demo/service/tool/ToolService.java"
with open(fp, "rb") as f:
    raw = f.read()
if raw[:3] == b"\xef\xbb\xbf":
    raw = raw[3:]
content = raw.decode("utf-8")

# Remove the closing brace of the class
# The file ends with "}\n" (class close)
# Find the last } which is the class close
last_brace = content.rfind("}")
content = content[:last_brace]

new_methods = """

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
            StringBuilder sb = new StringBuilder("你的简历列表：\n");
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

            return "简历详情：\n"
                + "  ID: " + resume.getId() + "\n"
                + "  标题: " + resume.getTitle() + "\n"
                + "  姓名: " + resume.getFullName() + "\n"
                + "  邮箱: " + (resume.getEmail() != null ? resume.getEmail() : "未设置") + "\n"
                + "  电话: " + (resume.getPhone() != null ? resume.getPhone() : "未设置") + "\n"
                + "  所在地: " + (resume.getLocation() != null ? resume.getLocation() : "未设置") + "\n"
                + "  个人简介: " + (resume.getProfessionalSummary() != null ? resume.getProfessionalSummary() : "未设置") + "\n"
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
            @ToolParam(description = "熟练度：beginner(入门)、intermediate(中级)、advanced(高级)、expert(专家)") String proficiencyLevel,
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
            StringBuilder sb = new StringBuilder("回收站中的简历：\n");
            for (Resume r : trash) {
                sb.append("  ID: ").append(r.getId())
                  .append(" | 标题: ").append(r.getTitle())
                  .append(" | 姓名: ").append(r.getFullName())
                  .append("\n");
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

"""

content += new_methods + "\n}"

with open(fp, "w", encoding="utf-8") as f:
    f.write(content)

print("OK - new tools added")