package com.example.demo.mapper;

import com.example.demo.pojo.WorkExperience;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface WorkExperienceMapper {
    Integer insert(WorkExperience record);
    Integer deleteByPrimary(Integer id,Integer resumeId);
    Integer updateByPrimary(WorkExperience record);
    WorkExperience selectByPrimaryId(Integer id,Integer resumeId);
    List<WorkExperience> selectByPrimaryResumeId(Integer resumeId);
    // 批量删除
    Integer WorkExperienceDeleteBatch(@Param("ids") List<Integer> ids,
                    @Param("resumeId") Integer resumeId,
                    @Param("userId") Integer userId);

    // 批量更新排序序号
    Integer updateWorkExperienceSortOrderBatch(@Param("resumeId") Integer resumeId,
                             @Param("list") List<SortItem> sortItems);


    /**
     * 内部类：排序项
     */
    @Data
    @AllArgsConstructor
    class SortItem {
        private Integer id;
        private Integer sortOrder;
    }

}
