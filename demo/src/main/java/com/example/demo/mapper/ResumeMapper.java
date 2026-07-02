package com.example.demo.mapper;

import com.example.demo.pojo.Resume;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ResumeMapper {
    Integer insertResume(Resume resume);
    Resume selectResumeById(Integer id);
    Resume selectResumeByIdHf(Integer id);
    Integer updateResume(Resume resume);
    Integer isDeleteResume(Integer id,Integer userId);
    Integer noIsDeleteResume(Integer id,Integer userId);
    Integer updateStatusDraftResume(Integer id,Integer userId);
    Integer updateStatusPublishedResume(Integer id,Integer userId);
    Integer deleteResume(Integer id,Integer userId);
    List<Resume> selectResumeByUserId(Integer userId);
    List<Resume> selectResumeByTemplateId(Integer userId,Integer templateId);
    List<Resume> selectResumeByStatus(Integer userId,String status);
    List<Resume> selectTrashResume(Integer userId);
    List<Resume> selectAllResumes();
}