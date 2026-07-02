package com.example.demo.service;

import com.example.demo.pojo.WorkExperience;

import java.util.List;

public interface WorkExperienceService {
    Boolean insertWorkExperience(WorkExperience workExperience, Integer resumeId);
    Boolean updateWorkExperience(WorkExperience workExperience, Integer currentUserId);
    Boolean deleteWorkExperience(Integer id,Integer resumeId);
    List<WorkExperience> selectWorkExperienceByResumeId(Integer resumeId);
    WorkExperience selectWorkExperienceById(Integer id,Integer resumeId);
    Boolean updateWorkExperienceSortOrderBatch(Integer resumeId, List<Integer> sortedIds, Integer currentUserId);
    Boolean WorkExperienceDeleteBatch(Integer resumeId, List<Integer> ids, Integer currentUserId);

}
