package com.example.demo.pojo;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 用户信息实体
 * 对应数据库表 users（已移除 avatar_url 和 is_premium 字段）
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {

    /**
     * 用户ID，主键自增
     */
    private Integer id;

    /**
     * 用户名
     */
    private String username;

    /**
     * 电子邮箱
     */
    private String email;

    /**
     * 加密后的密码哈希值
     */
    private String passwordHash;

    /**
     * 用户角色: user-普通用户, admin-管理员
     */
    private String role;

    /**
     * 账号创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 信息最后更新时间
     */
    private LocalDateTime updatedAt;
}