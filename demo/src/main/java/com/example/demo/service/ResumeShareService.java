package com.example.demo.service;

import com.example.demo.pojo.ResumeShare;
import java.time.LocalDateTime;
import java.util.List;

public interface ResumeShareService {

    /**
     * 创建分享链接
     * @param resumeId 简历ID
     * @param expiresAt 过期时间（可为null）
     * @param currentUserId 当前用户ID
     * @return 新创建的分享记录（含token）
     */
    ResumeShare createShare(Integer resumeId, LocalDateTime expiresAt, Integer currentUserId);

    /**
     * 获取某简历的所有分享记录（按创建时间倒序）
     */
    List<ResumeShare> getSharesByResumeId(Integer resumeId, Integer currentUserId);

    /**
     * 获取单条分享详情（管理端）
     */
    ResumeShare getShareById(Integer id, Integer resumeId, Integer currentUserId);

    /**
     * 停用分享
     */
    Boolean disableShare(Integer id, Integer resumeId, Integer currentUserId);

    /**
     * 启用分享
     */
    Boolean enableShare(Integer id, Integer resumeId, Integer currentUserId);

    /**
     * 更新过期时间
     */
    Boolean updateExpiresAt(Integer id, Integer resumeId, LocalDateTime expiresAt, Integer currentUserId);

    /**
     * 删除分享链接
     */
    Boolean deleteShare(Integer id, Integer resumeId, Integer currentUserId);

    /**
     * 公开访问分享（校验token并增加浏览次数）
     * @return 有效的分享记录，若无效或过期则返回null
     */
    ResumeShare validateAndIncrementView(String token);

    /**
     * 获取用户所有简历的分享记录（跨简历聚合）
     */
    List<ResumeShare> getAllSharesByUserId(Integer userId);
}
