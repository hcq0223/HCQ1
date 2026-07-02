package com.example.demo.service;

import com.example.demo.pojo.CustomSection;

import java.util.List;

public interface CustomSectionService {
    Boolean addCustomSection(CustomSection customSection, Integer currentUserId);

    Boolean updateCustomSection(CustomSection customSection, Integer currentUserId);

    Boolean deleteCustomSection(Integer id, Integer resumeId, Integer currentUserId);

    Boolean deleteBatch(Integer resumeId, List<Integer> ids, Integer currentUserId);

    Boolean sortCustomSections(Integer resumeId, List<Integer> sortedIds, Integer currentUserId);

    List<CustomSection> getCustomSectionsByResumeId(Integer resumeId, Integer currentUserId);

    CustomSection getCustomSectionById(Integer id, Integer resumeId, Integer currentUserId);
}
