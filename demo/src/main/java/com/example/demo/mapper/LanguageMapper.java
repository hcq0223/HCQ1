package com.example.demo.mapper;

import com.example.demo.pojo.Language;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface LanguageMapper {
    Integer insertLanguage(Language language);
    Integer deleteLanguage(@Param("id") Integer id,@Param("resumeId") Integer resumeId);
    Language selectById(@Param("id") Integer id, @Param("resumeId") Integer resumeId);
    Integer updateLanguage(Language language);
    List<Language> selectByLanguageResumeId(@Param("resumeId") Integer resumeId);

    Integer deleteLanguageBatch(@Param("ids") List<Integer> ids,
                    @Param("resumeId") Integer resumeId,
                    @Param("userId") Integer userId);

    Integer updateLanguageSortOrderBatch(@Param("resumeId") Integer resumeId,
                             @Param("list") List<SortItem> sortItems);

    List<Integer> selectIdsByResumeId(@Param("resumeId") Integer resumeId);

    @Data
    @AllArgsConstructor
    class SortItem {
        private Integer id;
        private Integer sortOrder;
    }

}
