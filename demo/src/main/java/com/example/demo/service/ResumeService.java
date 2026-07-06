package com.example.demo.service;

import com.example.demo.pojo.Resume;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ResumeService {
    boolean insertResume(Resume resume, Integer userId);
    boolean updateResume(Resume resume);
    boolean updateResumeWithAvatar(Resume resume, MultipartFile avatarFile);
    boolean isDeleteResume(Integer id, Integer userId);
    boolean noIsDeleteResume(Integer id, Integer userId);
    boolean updateStatusDraftResume(Integer id, Integer userId);
    boolean updateStatusPublishedResume(Integer id, Integer userId);
    boolean deleteResume(Integer id, Integer userId);
    List<Resume> selectResumeByUserId(Integer userId);
    List<Resume> selectResumeByTemplateId(Integer userId, Integer templateId);
    List<Resume> selectResumeByStatus(Integer userId, String status);
    List<Resume> selectTrashResume(Integer userId);
    List<Resume> selectAllResumes();
    Resume selectResumeById(Integer id);
    List<Resume> selectResumeTitle(Integer userId, String title);
    int countRecycledResumes(Integer userId);
    Resume getResumeByIdAndUserId(Integer id, Integer userId);
    boolean deleteResumes(Integer userId);
}