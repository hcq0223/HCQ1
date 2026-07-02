package com.example.demo.mapper;

import com.example.demo.pojo.Education;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface EducationMapper {
    Integer insertEducation(Education education);
    Integer deleteEducation(Integer id,Integer resumeId);
    Integer updateEducation(Education education);
    Education selectEducationById(Integer id,Integer resumeId);
    List<Education> selectEducationByResumeId(Integer resumeId);
    Integer EducationDeleteBatch(@Param("ids") List<Integer> ids,
                        @Param("resumeId") Integer resumeId,
                        @Param("userId") Integer userId);


}
