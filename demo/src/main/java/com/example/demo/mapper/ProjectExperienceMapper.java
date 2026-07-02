package com.example.demo.mapper;

import com.example.demo.pojo.ProjectExperience;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ProjectExperienceMapper {
    Integer insertProject(ProjectExperience record);
    Integer deleteProject(@Param("id") Integer id, @Param("resumeId") Integer resumeId);
    Integer updateProject(ProjectExperience record);
    ProjectExperience selectByProjectId(@Param("id") Integer id, @Param("resumeId") Integer resumeId);
    List<ProjectExperience> selectByProjectResumeId(@Param("resumeId") Integer resumeId);
    List<Integer> selectIdsByResumeId(@Param("resumeId") Integer resumeId);
    Integer ProjectDeleteBatch(@Param("ids") List<Integer> ids,
                       @Param("resumeId") Integer resumeId,
                       @Param("userId") Integer userId);
    Integer updateProjectSortOrderBatch(@Param("resumeId") Integer resumeId,
                                @Param("list") List<SortItem> sortItems);

    @Data
    @AllArgsConstructor
    class SortItem {
        private Integer id;
        private Integer sortOrder;
    }
}
