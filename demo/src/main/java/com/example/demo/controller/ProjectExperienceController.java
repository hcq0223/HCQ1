package com.example.demo.controller;

import com.example.demo.pojo.ProjectExperience;
import com.example.demo.pojo.User;
import com.example.demo.service.ProjectExperienceService;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/hcq/ProjectExperience")
public class ProjectExperienceController {
    private final ProjectExperienceService service;

    public ProjectExperienceController(ProjectExperienceService s) {
        this.service = s;
    }

    private static Map<String, Object> ok() {
        Map<String, Object> m = new HashMap<>(2);
        m.put("success", true);
        return m;
    }

    private static ResponseEntity<Map<String, Object>> err(String msg) {
        Map<String, Object> m = new HashMap<>(4);
        m.put("success", false);
        m.put("message", msg);
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(m);
    }

    @GetMapping("/selectProjectsByResumeId")
    public ResponseEntity<Map<String, Object>> selectProjectsByResumeId(@RequestParam("resumeId") Integer rid, HttpSession session) {
        User u = (User) session.getAttribute("user");
        if (u == null) return err("unauth");
        List<ProjectExperience> list = service.getProjectsByResumeId(rid, u.getId());
        Map<String, Object> r = new HashMap<>();
        r.put("success", true);
        r.put("projects", list);
        return ResponseEntity.ok(r);
    }

    @PostMapping("/insertProject")
    public ResponseEntity<Map<String, Object>> insertProject(@RequestBody ProjectExperience p, @RequestParam("resumeId") Integer rid, HttpSession session) {
        User u = (User) session.getAttribute("user");
        if (u == null) return err("unauth");
        p.setResumeId(rid);
        return service.addProject(p, u.getId()) ? ResponseEntity.ok(ok()) : err("add failed");
    }

    @PutMapping("/updateProject")
    public ResponseEntity<Map<String, Object>> updateProject(@RequestBody ProjectExperience p, HttpSession session) {
        User u = (User) session.getAttribute("user");
        if (u == null) return err("unauth");
        return service.updateProject(p, u.getId()) ? ResponseEntity.ok(ok()) : err("update failed");
    }

    @DeleteMapping("/deleteProject")
    public ResponseEntity<Map<String, Object>> deleteProject(@RequestParam("id") Integer id, @RequestParam("resumeId") Integer rid, HttpSession session) {
        User u = (User) session.getAttribute("user");
        if (u == null) return err("unauth");
        return service.deleteProject(id, rid, u.getId()) ? ResponseEntity.ok(ok()) : err("delete failed");
    }

    @DeleteMapping("/{resumeId}/projectBatch")
    public ResponseEntity<Map<String, Object>> batchDelete(@PathVariable Integer resumeId, @RequestBody Map<String, List<Integer>> body, HttpSession session) {
        User u = (User) session.getAttribute("user");
        if (u == null) return err("unauth");
        return service.deleteBatch(resumeId, body.get("ids"), u.getId()) ? ResponseEntity.ok(ok()) : err("batch delete failed");
    }

    @PutMapping("/{resumeId}/projectSort")
    public ResponseEntity<Map<String, Object>> sort(@PathVariable Integer resumeId, @RequestBody List<Integer> sortedIds, HttpSession session) {
        User u = (User) session.getAttribute("user");
        if (u == null) return err("unauth");
        return service.sortProjects(resumeId, sortedIds, u.getId()) ? ResponseEntity.ok(ok()) : err("sort failed");
    }
}