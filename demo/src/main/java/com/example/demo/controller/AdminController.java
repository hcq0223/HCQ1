package com.example.demo.controller;

import com.example.demo.mapper.UserMapper;
import com.example.demo.pojo.Resume;
import com.example.demo.pojo.User;
import com.example.demo.service.ResumeService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/hcq/admin")
@RequiredArgsConstructor
public class AdminController {

    private final UserMapper userMapper;
    private final ResumeService resumeService;

    @GetMapping("/users")
    public ResponseEntity<Map<String, Object>> getAllUsers(HttpSession session) {
        User admin = (User) session.getAttribute("user");
        if (admin == null || !"admin".equals(admin.getRole())) {
            return unauthorized("无权访问");
        }
        List<User> users = userMapper.selectAllUsers();
        for (User u : users) {
            u.setPasswordHash(null);
        }
        Map<String, Object> data = new HashMap<>();
        data.put("users", users);
        return ResponseEntity.ok(data);
    }

    @GetMapping("/resumes")
    public ResponseEntity<Map<String, Object>> getAllResumes(HttpSession session) {
        User admin = (User) session.getAttribute("user");
        if (admin == null || !"admin".equals(admin.getRole())) {
            return unauthorized("无权访问");
        }
        List<Resume> resumes = resumeService.selectAllResumes();
        Map<String, Object> data = new HashMap<>();
        data.put("resumes", resumes);
        return ResponseEntity.ok(data);
    }

    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getStats(HttpSession session) {
        User admin = (User) session.getAttribute("user");
        if (admin == null || !"admin".equals(admin.getRole())) {
            return unauthorized("无权访问");
        }
        List<User> users = userMapper.selectAllUsers();
        List<Resume> resumes = resumeService.selectAllResumes();

        long totalUsers = users.size();
        long totalResumes = resumes.size();
        long publishedResumes = resumes.stream().filter(r -> "published".equals(r.getStatus())).count();
        long draftResumes = resumes.stream().filter(r -> "draft".equals(r.getStatus())).count();

        Map<String, Object> stats = new HashMap<>();
        stats.put("totalUsers", totalUsers);
        stats.put("totalResumes", totalResumes);
        stats.put("publishedResumes", publishedResumes);
        stats.put("draftResumes", draftResumes);

        Map<String, Object> data = new HashMap<>();
        data.put("stats", stats);
        return ResponseEntity.ok(data);
    }

    private ResponseEntity<Map<String, Object>> unauthorized(String message) {
        Map<String, Object> resp = new HashMap<>();
        resp.put("success", false);
        resp.put("message", message);
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(resp);
    }
}