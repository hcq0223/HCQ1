package com.example.demo.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@Component
public class SessionAuthInterceptor implements HandlerInterceptor {
    private final ObjectMapper objectMapper;

    public SessionAuthInterceptor(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    private boolean isPublicApi(String uri){
        return uri == null
                || uri.startsWith("/hcq/login")
                || uri.startsWith("/hcq/admin/login")
                || uri.startsWith("/hcq/register")
                || uri.startsWith("/hcq/forgetPassword")
                || uri.startsWith("/hcq/ResumeShare/public")
                || uri.startsWith("/hcq/Public/get");
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())){
            return true;
        }
        String uri = request.getRequestURI();
        if (uri == null || !uri.startsWith("/hcq")){
            return true;
        }
        if (isPublicApi(uri)){
            return true;
        }
        HttpSession session = request.getSession(false);
        if (session != null && session.getAttribute("user") != null){
            return true;
        }
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        Map<String, Object> resp = new HashMap<>();
        resp.put("success", false);
        resp.put("message", "未登录");
        response.getWriter().write(objectMapper.writeValueAsString(resp));
        return false;
    }
}