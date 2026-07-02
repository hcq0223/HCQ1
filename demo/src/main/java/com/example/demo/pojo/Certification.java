package com.example.demo.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 证书与奖项实体
 * 对应数据库表 certifications
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Certification {

    /**
     * 证书/奖项ID，主键自增
     */
    private Integer id;

    /**
     * 所属简历ID
     */
    private Integer resumeId;

    /**
     * 证书或奖项名称
     */
    private String name;

    /**
     * 颁发机构/主办单位
     */
    private String issuingOrganization;

    /**
     * 获得日期
     */
    private LocalDate issueDate;

    /**
     * 有效期截止日期（为空表示永久有效）
     */
    private LocalDate expiryDate;

    /**
     * 证书编号/认证ID
     */
    private String credentialId;

    /**
     * 证书验证链接
     */
    private String credentialUrl;

    /**
     * 排序序号
     */
    private Integer sortOrder;

    /**
     * 记录创建时间
     */
    private LocalDateTime createdAt;
}
