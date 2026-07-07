package com.example.demo.config;

import com.example.demo.pojo.StepInfo;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * 拦截 ToolService 中所有 @Tool 注解的方法，
 * 自动记录工具调用步骤信息返回给前端展示。
 */
@Aspect
@Component
public class ToolCallTrackingAspect {

    private static final ThreadLocal<List<StepInfo>> stepsHolder =
            ThreadLocal.withInitial(ArrayList::new);

    @Around("@annotation(tool)")
    public Object trackToolCall(ProceedingJoinPoint pjp, Tool tool) throws Throwable {
        String toolName = pjp.getSignature().getName();
        String description = tool.description();

        long start = System.nanoTime();

        Object result;
        try {
            result = pjp.proceed();
            long durationMs = (System.nanoTime() - start) / 1_000_000;

            stepsHolder.get().add(new StepInfo(toolName, description, "completed",
                    result != null ? result.toString() : "", durationMs));
            return result;
        } catch (Throwable t) {
            long durationMs = (System.nanoTime() - start) / 1_000_000;

            stepsHolder.get().add(new StepInfo(toolName, description, "failed",
                    t.getMessage() != null ? t.getMessage() : "未知异常", durationMs));
            throw t;
        }
    }

    /** 获取当前线程记录的步骤列表，并清空 */
    public static List<StepInfo> drainSteps() {
        List<StepInfo> steps = stepsHolder.get();
        stepsHolder.remove();
        return steps;
    }

    /** 清空当前线程的步骤记录 */
    public static void clearSteps() {
        stepsHolder.remove();
    }
}
