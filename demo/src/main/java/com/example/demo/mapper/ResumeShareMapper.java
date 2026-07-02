package com.example.demo.mapper;

import com.example.demo.pojo.ResumeShare;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface ResumeShareMapper {
    Integer insert(ResumeShare record);
    Integer deleteByPrimary(@Param("id") Integer id, @Param("resumeId") Integer resumeId);
    List<ResumeShare> selectByResumeId(@Param("resumeId") Integer resumeId);
    /**
     * 根据分享令牌查询分享记录（用于公开访问）
     */
    ResumeShare selectByToken(@Param("shareToken") String shareToken);
    /**
     * 根据 ID 查询分享记录（管理端查看详情）
     */
    ResumeShare selectById(@Param("id") Integer id, @Param("resumeId") Integer resumeId);
    /**
     * 更新分享记录的有效状态（停用/启用）
     */
    Integer updateActiveStatus(@Param("id") Integer id, @Param("isActive") Boolean isActive);
    /**
     * 更新过期时间
     */
    Integer updateExpiresAt(@Param("id") Integer id, @Param("expiresAt") LocalDateTime expiresAt);
    /**
     * 增加浏览次数（原子操作，每次访问 +1）
     */
    Integer incrementViewCount(@Param("id") Integer id);
    /**
     * 根据用户ID查询其所有简历的分享记录（用于用户维度统计，可选）
     * @param userId 用户ID
     * @return 分享记录列表
     */
    List<ResumeShare> selectByResumeShareUserId(@Param("userId") Integer userId);

}
