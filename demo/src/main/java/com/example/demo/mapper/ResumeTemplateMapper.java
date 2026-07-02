package com.example.demo.mapper;

import com.example.demo.pojo.ResumeTemplate;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ResumeTemplateMapper {
    /**
     * 新增模板
     */
    Integer insertResumeTemplate(ResumeTemplate template);

    /**
     * 物理删除模板（根据 ID）
     */
    Integer deleteById(@Param("id") Integer id);

    /**
     * 更新模板信息（动态更新非空字段）
     */
    Integer updateResumeTemplate(ResumeTemplate template);

    /**
     * 更新模板启用/禁用状态
     */
    Integer updateActiveStatus(@Param("id") Integer id, @Param("isActive") Boolean isActive);

    /**
     * 根据 ID 查询模板
     */
    ResumeTemplate selectById(@Param("id") Integer id);

    /**
     * 多条件查询模板列表（管理端）
     * @param category  分类（可选）
     * @param isPremium 是否付费（可选）
     * @param isActive  是否启用（可选）
     * @param keyword   名称关键字（可选，模糊搜索）
     * @return 模板列表
     */
    List<ResumeTemplate> selectList(@Param("category") String category,
                                    @Param("isPremium") Boolean isPremium,
                                    @Param("isActive") Boolean isActive,
                                    @Param("keyword") String keyword);
}
