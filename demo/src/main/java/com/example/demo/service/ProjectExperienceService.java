package com.example.demo.service;

import com.example.demo.pojo.ProjectExperience;

import java.util.List;

public interface ProjectExperienceService {
    Boolean addProject(ProjectExperience project, Integer currentUserId);

    Boolean updateProject(ProjectExperience project, Integer currentUserId);

    Boolean deleteProject(Integer id, Integer resumeId, Integer currentUserId);

    Boolean deleteBatch(Integer resumeId, List<Integer> ids, Integer currentUserId);

    Boolean sortProjects(Integer resumeId, List<Integer> sortedIds, Integer currentUserId);

    List<ProjectExperience> getProjectsByResumeId(Integer resumeId, Integer currentUserId);

    ProjectExperience getProjectById(Integer id, Integer resumeId, Integer currentUserId);
}
