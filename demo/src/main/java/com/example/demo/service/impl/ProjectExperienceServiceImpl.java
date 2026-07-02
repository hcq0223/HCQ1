package com.example.demo.service.impl;
import com.example.demo.mapper.ProjectExperienceMapper;
import com.example.demo.mapper.ProjectExperienceMapper.SortItem;
import com.example.demo.mapper.ResumeMapper;
import com.example.demo.pojo.ProjectExperience;
import com.example.demo.pojo.Resume;
import com.example.demo.service.ProjectExperienceService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import java.util.ArrayList;
import java.util.List;
@Service
public class ProjectExperienceServiceImpl implements ProjectExperienceService {
    private final ProjectExperienceMapper projectMapper;
    private final ResumeMapper resumeMapper;
    public ProjectExperienceServiceImpl(ProjectExperienceMapper a, ResumeMapper b) { this.projectMapper = a; this.resumeMapper = b; }
    @Override @Transactional
    public Boolean addProject(ProjectExperience project, Integer currentUserId) {
        if (project == null || project.getResumeId() == null || !StringUtils.hasText(project.getProjectName())) return false;
        Resume resume = resumeMapper.selectResumeById(project.getResumeId());
        if (resume == null || Boolean.TRUE.equals(resume.getIsDeleted()) || !resume.getUserId().equals(currentUserId)) return false;
        if (project.getStartDate() != null && project.getEndDate() != null && project.getEndDate().isBefore(project.getStartDate())) return false;
        List<Integer> existIds = projectMapper.selectIdsByResumeId(project.getResumeId());
        project.setSortOrder(existIds.size());
        return projectMapper.insertProject(project) > 0;
    }
    @Override @Transactional
    public Boolean updateProject(ProjectExperience project, Integer currentUserId) {
        if (project == null || project.getId() == null || project.getResumeId() == null) return false;
        Resume resume = resumeMapper.selectResumeById(project.getResumeId());
        if (resume == null || Boolean.TRUE.equals(resume.getIsDeleted()) || !resume.getUserId().equals(currentUserId)) return false;
        ProjectExperience exist = projectMapper.selectByProjectId(project.getId(), project.getResumeId());
        if (exist == null) return false;
        if (project.getStartDate() != null && project.getEndDate() != null && project.getEndDate().isBefore(project.getStartDate())) return false;
        if (project.getProjectName() == null && project.getRole() == null && project.getProjectUrl() == null && project.getStartDate() == null && project.getEndDate() == null && project.getDescription() == null && project.getTechnologiesUsed() == null && project.getAchievements() == null && project.getSortOrder() == null) return false;
        return projectMapper.updateProject(project) > 0;
    }
    @Override @Transactional
    public Boolean deleteProject(Integer id, Integer resumeId, Integer currentUserId) {
        if (id == null || resumeId == null) return false;
        Resume resume = resumeMapper.selectResumeById(resumeId);
        if (resume == null || Boolean.TRUE.equals(resume.getIsDeleted()) || !resume.getUserId().equals(currentUserId)) return false;
        return projectMapper.deleteProject(id, resumeId) > 0;
    }
    @Override @Transactional
    public Boolean deleteBatch(Integer resumeId, List<Integer> ids, Integer currentUserId) {
        Resume resume = resumeMapper.selectResumeById(resumeId);
        if (resume == null || Boolean.TRUE.equals(resume.getIsDeleted()) || !resume.getUserId().equals(currentUserId)) return false;
        return projectMapper.ProjectDeleteBatch(ids, resumeId, currentUserId) == ids.size();
    }
    @Override @Transactional
    public Boolean sortProjects(Integer resumeId, List<Integer> sortedIds, Integer currentUserId) {
        Resume resume = resumeMapper.selectResumeById(resumeId);
        if (resume == null || Boolean.TRUE.equals(resume.getIsDeleted()) || !resume.getUserId().equals(currentUserId)) return false;
        List<SortItem> items = new ArrayList<>();
        for (int i = 0; i < sortedIds.size(); i++) items.add(new SortItem(sortedIds.get(i), i));
        return projectMapper.updateProjectSortOrderBatch(resumeId, items) == sortedIds.size();
    }
    @Override
    public List<ProjectExperience> getProjectsByResumeId(Integer resumeId, Integer currentUserId) {
        Resume resume = resumeMapper.selectResumeById(resumeId);
        if (resume == null || Boolean.TRUE.equals(resume.getIsDeleted()) || !resume.getUserId().equals(currentUserId)) return java.util.Collections.emptyList();
        return projectMapper.selectByProjectResumeId(resumeId);
    }
    @Override
    public ProjectExperience getProjectById(Integer id, Integer resumeId, Integer currentUserId) {
        Resume resume = resumeMapper.selectResumeById(resumeId);
        if (resume == null || Boolean.TRUE.equals(resume.getIsDeleted()) || !resume.getUserId().equals(currentUserId)) return null;
        return projectMapper.selectByProjectId(id, resumeId);
    }
}