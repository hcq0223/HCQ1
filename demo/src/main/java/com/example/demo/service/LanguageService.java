package com.example.demo.service;

import com.example.demo.pojo.Language;
import java.util.List;

public interface LanguageService {

    Boolean addLanguage(Language language, Integer currentUserId);

    Boolean updateLanguage(Language language, Integer currentUserId);

    Boolean deleteLanguage(Integer id, Integer resumeId, Integer currentUserId);

    Boolean deleteBatch(Integer resumeId, List<Integer> ids, Integer currentUserId);

    Boolean sortLanguages(Integer resumeId, List<Integer> sortedIds, Integer currentUserId);

    List<Language> getLanguagesByResumeId(Integer resumeId, Integer currentUserId);

    Language getLanguageById(Integer id, Integer resumeId, Integer currentUserId);
}
