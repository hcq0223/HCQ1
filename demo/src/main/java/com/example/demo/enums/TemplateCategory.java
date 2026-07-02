package com.example.demo.enums;

/**
 * 简历模板分类枚举
 */
public enum TemplateCategory {
    PROFESSIONAL("professional", "专业"),
    CREATIVE("creative", "创意"),
    MODERN("modern", "现代"),
    MINIMALIST("minimalist", "极简"),
    ACADEMIC("academic", "学术");

    private final String code;
    private final String desc;

    TemplateCategory(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public String getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

    public static TemplateCategory fromCode(String code) {
        for (TemplateCategory category : TemplateCategory.values()) {
            if (category.code.equalsIgnoreCase(code)) {
                return category;
            }
        }
        throw new IllegalArgumentException("无效的模板分类: " + code);
    }
}