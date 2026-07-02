package com.example.demo.service;

import com.example.demo.pojo.Education;

import java.util.List;

public interface EducationService {
    Boolean insertEducation(Education education, Integer resumeId);
    Boolean updateEducation(Education education,Integer currentUserId);
    List<Education> selectEducationByResumeId(Integer resumeId);
    Education selectEducationById(Integer id,Integer resumeId);
    Boolean deleteEducation(Integer id,Integer resumeId);
    Boolean EducationDeleteBatch(Integer resumeId, List<Integer> ids, Integer currentUserId);
}
