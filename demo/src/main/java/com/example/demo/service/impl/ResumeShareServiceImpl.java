package com.example.demo.service.impl;

import com.example.demo.mapper.ResumeMapper;
import com.example.demo.mapper.ResumeShareMapper;
import com.example.demo.pojo.Resume;
import com.example.demo.pojo.ResumeShare;
import com.example.demo.service.ResumeShareService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ResumeShareServiceImpl implements ResumeShareService {

    private final ResumeShareMapper resumeShareMapper;
    private final ResumeMapper resumeMapper;

    @Override
    @Transactional
    public ResumeShare createShare(Integer resumeId, LocalDateTime expiresAt, Integer currentUserId) {
        // 简历校验
        Resume resume = resumeMapper.selectResumeById(resumeId);
        if (resume == null || Boolean.TRUE.equals(resume.getIsDeleted())) {
            return null;
        }
        if (!resume.getUserId().equals(currentUserId)) {
            return null; // 无权操作
        }
        // 只有已发布的简历才能创建分享
        if (!"published".equals(resume.getStatus())) {
            return null;
        }

        // 生成唯一令牌
        String token = UUID.randomUUID().toString().replace("-", "");

        ResumeShare share = new ResumeShare();
        share.setResumeId(resumeId);
        share.setShareToken(token);
        share.setExpiresAt(expiresAt);
        share.setViewCount(0);
        share.setIsActive(true);

        int rows = resumeShareMapper.insert(share);
        if (rows > 0) {
            return share; // 插入后主键已回填
        }
        return null;
    }

    @Override
    public List<ResumeShare> getSharesByResumeId(Integer resumeId, Integer currentUserId) {
        if (resumeId == null) return new ArrayList<>();
        Resume resume = resumeMapper.selectResumeById(resumeId);
        if (resume == null || Boolean.TRUE.equals(resume.getIsDeleted())
                || !resume.getUserId().equals(currentUserId)) {
            return new ArrayList<>();
        }
        // 假设 Mapper 中有 selectByResumeId 方法，需要补充
        return resumeShareMapper.selectByResumeId(resumeId);
    }

    @Override
    public ResumeShare getShareById(Integer id, Integer resumeId, Integer currentUserId) {
        if (id == null || resumeId == null) return null;
        Resume resume = resumeMapper.selectResumeById(resumeId);
        if (resume == null || Boolean.TRUE.equals(resume.getIsDeleted())
                || !resume.getUserId().equals(currentUserId)) {
            return null;
        }
        return resumeShareMapper.selectById(id, resumeId);
    }

    @Override
    @Transactional
    public Boolean disableShare(Integer id, Integer resumeId, Integer currentUserId) {
        if (id == null || resumeId == null) return false;
        Resume resume = resumeMapper.selectResumeById(resumeId);
        if (resume == null || Boolean.TRUE.equals(resume.getIsDeleted())
                || !resume.getUserId().equals(currentUserId)) {
            return false;
        }
        int rows = resumeShareMapper.updateActiveStatus(id, false);
        return rows > 0;
    }

    @Override
    @Transactional
    public Boolean enableShare(Integer id, Integer resumeId, Integer currentUserId) {
        if (id == null || resumeId == null) return false;
        Resume resume = resumeMapper.selectResumeById(resumeId);
        if (resume == null || Boolean.TRUE.equals(resume.getIsDeleted())
                || !resume.getUserId().equals(currentUserId)) {
            return false;
        }
        int rows = resumeShareMapper.updateActiveStatus(id, true);
        return rows > 0;
    }

    @Override
    @Transactional
    public Boolean updateExpiresAt(Integer id, Integer resumeId, LocalDateTime expiresAt, Integer currentUserId) {
        if (id == null || resumeId == null) return false;
        Resume resume = resumeMapper.selectResumeById(resumeId);
        if (resume == null || Boolean.TRUE.equals(resume.getIsDeleted())
                || !resume.getUserId().equals(currentUserId)) {
            return false;
        }
        int rows = resumeShareMapper.updateExpiresAt(id, expiresAt);
        return rows > 0;
    }

    @Override
    @Transactional
    public Boolean deleteShare(Integer id, Integer resumeId, Integer currentUserId) {
        if (id == null || resumeId == null) return false;
        Resume resume = resumeMapper.selectResumeById(resumeId);
        if (resume == null || Boolean.TRUE.equals(resume.getIsDeleted())
                || !resume.getUserId().equals(currentUserId)) {
            return false;
        }
        int rows = resumeShareMapper.deleteByPrimary(id, resumeId); // 注意你Mapper中方法名为deleteByPrimary
        return rows > 0;
    }

    @Override
    @Transactional
    public ResumeShare validateAndIncrementView(String token) {
        if (token == null || token.isEmpty()) return null;
        ResumeShare share = resumeShareMapper.selectByToken(token);
        if (share == null || !Boolean.TRUE.equals(share.getIsActive())) {
            return null;
        }
        // 检查过期时间
        if (share.getExpiresAt() != null && share.getExpiresAt().isBefore(LocalDateTime.now())) {
            return null;
        }
        // 增加浏览次数
        resumeShareMapper.incrementViewCount(share.getId());
        // 为了返回最新viewCount，可以重新查询，但没必要，前端可从分享记录中取原值+1
        share.setViewCount(share.getViewCount() + 1);
        return share;
    }

    @Override
    public List<ResumeShare> getAllSharesByUserId(Integer userId) {
        if (userId == null) return new ArrayList<>();
        return resumeShareMapper.selectByResumeShareUserId(userId);
    }
}