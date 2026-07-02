package com.example.demo.service.impl;

import com.example.demo.mapper.ResumeMapper;
import com.example.demo.pojo.Resume;
import com.example.demo.service.Avatar.StorageService;
import com.example.demo.service.ResumeService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
public class ResumeServiceImpl implements ResumeService {
    private final ResumeMapper resumeMapper;
    private final StorageService storageService;

    public ResumeServiceImpl(ResumeMapper resumeMapper, StorageService storageService) {
        this.resumeMapper = resumeMapper;
        this.storageService = storageService;
    }

    @Override
    @Transactional
    public boolean insertResume(Resume resume, Integer userId) {
        if (resume == null || userId == null) {
            return false;
        }
        if ((!StringUtils.hasText(resume.getTitle())) || (!StringUtils.hasText(resume.getFullName()))) {
            return false;
        }
        resume.setUserId(userId);
        resume.setStatus("draft");
        resume.setIsDeleted(false);
        Integer i = resumeMapper.insertResume(resume);
        return i != null && i == 1;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateResume(Resume resume) {
        if (resume == null || resume.getId() == null || resume.getUserId() == null) {
            return false;
        }
        int rows = resumeMapper.updateResume(resume);
        return rows > 0;
    }

    @Override
    @Transactional
    public boolean updateResumeWithAvatar(Resume resume, MultipartFile avatarFile) {
        if (resume == null || resume.getId() == null || resume.getUserId() == null) {
            return false;
        }
        if (avatarFile != null && !avatarFile.isEmpty()) {
            String avatarUrl = storageService.upload(avatarFile);
            resume.setAvatarUrl(avatarUrl);
        }
        return resumeMapper.updateResume(resume) > 0;
    }

    @Override
    @Transactional
    public boolean isDeleteResume(Integer id, Integer userId) {
        if (resumeMapper.selectResumeById(id) == null || resumeMapper.selectResumeById(id).getIsDeleted()){
            return false;
        }
        Integer deleteResume = resumeMapper.isDeleteResume(id, userId);
        return deleteResume == 1;
    }

    @Override
    @Transactional
    public boolean noIsDeleteResume(Integer id, Integer userId) {
        Resume r = resumeMapper.selectResumeByIdHf(id);
        if (r == null || !r.getIsDeleted()){
            return false;
        }
        Integer deleteResume = resumeMapper.noIsDeleteResume(id, userId);
        return deleteResume == 1;
    }

    @Override
    @Transactional
    public boolean updateStatusDraftResume(Integer id, Integer userId) {
        Resume r = resumeMapper.selectResumeById(id);
        if (r == null || "draft".equals(r.getStatus())){
            return false;
        }
        Integer deleteResume = resumeMapper.updateStatusDraftResume(id, userId);
        return deleteResume == 1;
    }

    @Override
    @Transactional
    public boolean updateStatusPublishedResume(Integer id, Integer userId) {
        Resume r = resumeMapper.selectResumeById(id);
        if (r == null || "published".equals(r.getStatus())){
            return false;
        }
        Integer deleteResume = resumeMapper.updateStatusPublishedResume(id, userId);
        return deleteResume == 1;
    }

    @Override
    @Transactional
    public boolean deleteResume(Integer id, Integer userId) {
        if(id == null){
            return false;
        }
        Resume resume = resumeMapper.selectResumeByIdHf(id);
        if (resume == null || !resume.getIsDeleted()){
            return false;
        }
        Integer deleteResume = resumeMapper.deleteResume(id, userId);
        return deleteResume == 1;
    }

    @Override
    public List<Resume> selectResumeByUserId(Integer userId) {
        return resumeMapper.selectResumeByUserId(userId);
    }

    @Override
    public List<Resume> selectResumeByTemplateId(Integer userId, Integer templateId) {
        return resumeMapper.selectResumeByTemplateId(userId, templateId);
    }

    @Override
    public List<Resume> selectResumeByStatus(Integer userId, String status) {
        return resumeMapper.selectResumeByStatus(userId, status);
    }

    @Override
    public List<Resume> selectTrashResume(Integer userId) {
        return resumeMapper.selectTrashResume(userId);
    }

    @Override
    public List<Resume> selectAllResumes() {
        return resumeMapper.selectAllResumes();
    }

    @Override
    public Resume selectResumeById(Integer id) {
        return resumeMapper.selectResumeById(id);
    }
}