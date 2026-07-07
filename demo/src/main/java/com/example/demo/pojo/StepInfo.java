package com.example.demo.pojo;

public class StepInfo {

    /** 工具方法名，如 addWorkExperience */
    private String toolName;

    /** 工具的 description 字段 */
    private String description;

    /** completed / failed / running */
    private String status;

    /** 工具返回的结果文本 */
    private String result;

    /** 执行耗时（毫秒） */
    private long durationMs;

    public StepInfo() {}

    public StepInfo(String toolName, String description, String status, String result, long durationMs) {
        this.toolName = toolName;
        this.description = description;
        this.status = status;
        this.result = result;
        this.durationMs = durationMs;
    }

    public String getToolName() { return toolName; }
    public void setToolName(String toolName) { this.toolName = toolName; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getResult() { return result; }
    public void setResult(String result) { this.result = result; }

    public long getDurationMs() { return durationMs; }
    public void setDurationMs(long durationMs) { this.durationMs = durationMs; }
}
