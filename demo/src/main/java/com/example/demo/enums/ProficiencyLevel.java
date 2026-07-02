package com.example.demo.enums;

/**
 * 技能熟练程度枚举
 */
public enum ProficiencyLevel {
    BEGINNER("beginner", "入门"),
    INTERMEDIATE("intermediate", "中级"),
    ADVANCED("advanced", "高级"),
    EXPERT("expert", "专家");

    private final String code;
    private final String desc;

    ProficiencyLevel(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public String getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

    public static ProficiencyLevel fromCode(String code) {
        for (ProficiencyLevel level : ProficiencyLevel.values()) {
            if (level.code.equalsIgnoreCase(code)) {
                return level;
            }
        }
        throw new IllegalArgumentException("无效的熟练度: " + code);
    }
}