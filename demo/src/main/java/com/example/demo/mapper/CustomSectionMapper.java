package com.example.demo.mapper;

import com.example.demo.pojo.CustomSection;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface CustomSectionMapper {

    Integer insertCustomSection(CustomSection customSection);

    Integer deleteById(@Param("id") Integer id, @Param("resumeId") Integer resumeId);

    Integer updateCustomSection(CustomSection customSection);

    CustomSection selectById(@Param("id") Integer id, @Param("resumeId") Integer resumeId);

    List<CustomSection> selectByResumeId(@Param("resumeId") Integer resumeId);


    Integer deleteCustomSectionBatch(@Param("ids") List<Integer> ids,
                    @Param("resumeId") Integer resumeId,
                    @Param("userId") Integer userId);

    Integer updateCustomSectionSortOrderBatch(@Param("resumeId") Integer resumeId,
                             @Param("list") List<SortItem> sortItems);

    List<Integer> selectIdsByResumeId(@Param("resumeId") Integer resumeId);


    @lombok.Data
    @lombok.AllArgsConstructor
    class SortItem {
        private Integer id;
        private Integer sortOrder;
    }
}
