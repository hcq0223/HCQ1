package com.example.demo.enums;

/**
 * 语言熟练程度枚举
 */
public enum LanguageProficiencyLevel {
    BASIC("basic", "基础"),
    CONVERSATIONAL("conversational", "会话"),
    PROFESSIONAL("professional", "专业"),
    NATIVE("native", "母语");

    private final String code;
    private final String desc;

    LanguageProficiencyLevel(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public String getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

    public static LanguageProficiencyLevel fromCode(String code) {
        for (LanguageProficiencyLevel level : LanguageProficiencyLevel.values()) {
            if (level.code.equalsIgnoreCase(code)) {
                return level;
            }
        }
        throw new IllegalArgumentException("无效的语言熟练度: " + code);
    }
}
