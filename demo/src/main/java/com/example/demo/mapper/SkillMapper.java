package com.example.demo.mapper;

import com.example.demo.pojo.Skill;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface SkillMapper {
    Integer insertSkill(Skill skill);
    Integer updateSkill(Skill skill);
    Integer deleteById(@Param("id") Integer id, @Param("resumeId") Integer resumeId);
    Integer SkillDeleteBatch(@Param("ids") List<Integer> ids,
                    @Param("resumeId") Integer resumeId,
                    @Param("userId") Integer userId);
    Skill selectById(@Param("id") Integer id, @Param("resumeId") Integer resumeId);
    List<Skill> selectByResumeId(@Param("resumeId") Integer resumeId);
    List<Skill> selectByResumeIdAndCategory(@Param("resumeId") Integer resumeId,
                                            @Param("category") String category);
    /**
     * 获取某简历下所有不重复的技能分类（用于筛选下拉框）
     * @param resumeId 简历ID
     * @return 分类名称列表
     */
    List<String> selectDistinctCategoriesByResumeId(@Param("resumeId") Integer resumeId);
    List<Integer> selectIdsByResumeId(@Param("resumeId") Integer resumeId);
    Integer updateSkillSortOrderBatch(@Param("resumeId") Integer resumeId,
                             @Param("list") List<SortItem> sortItems);



    @Data
    @AllArgsConstructor
    class SortItem {
        private Integer id;
        private Integer sortOrder;
    }

}
