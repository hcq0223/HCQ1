package com.example.demo.mapper;

import com.example.demo.pojo.Certification;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface CertificationMapper {
    Integer insertCertification(Certification certification);

    Integer deleteById(@Param("id") Integer id, @Param("resumeId") Integer resumeId);

    Integer updateCertification(Certification certification);

    Certification selectById(@Param("id") Integer id, @Param("resumeId") Integer resumeId);

    List<Certification> selectByResumeId(@Param("resumeId") Integer resumeId);


    Integer deleteCertificationBatch(@Param("ids") List<Integer> ids,
                    @Param("resumeId") Integer resumeId,
                    @Param("userId") Integer userId);

    Integer updateCertificationSortOrderBatch(@Param("resumeId") Integer resumeId,
                             @Param("list") List<SortItem> sortItems);

    List<Integer> selectIdsByResumeId(@Param("resumeId") Integer resumeId);

    @Data
    @AllArgsConstructor
    class SortItem {
        private Integer id;
        private Integer sortOrder;
    }
}
