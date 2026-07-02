package com.example.demo.controller;

import com.example.demo.pojo.WorkExperience;
import com.example.demo.pojo.User;
import com.example.demo.service.WorkExperienceService;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/hcq/WorkExperience")
public class WorkExperienceController {
    private final WorkExperienceService service;

    public WorkExperienceController(WorkExperienceService s) {
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

    @GetMapping("/selectWorkExperienceByResumeId")
    public ResponseEntity<Map<String, Object>> selectByResumeId(@RequestParam("resumeId") Integer rid, HttpSession session) {
        User u = (User) session.getAttribute("user");
        if (u == null) return err("unauth");
        List<WorkExperience> list = service.selectWorkExperienceByResumeId(rid);
        Map<String, Object> r = new HashMap<>();
        r.put("success", true);
        r.put("workExperiences", list);
        return ResponseEntity.ok(r);
    }

    @PostMapping("/insertWorkExperience")
    public ResponseEntity<Map<String, Object>> insert(@RequestBody WorkExperience w, @RequestParam("resumeId") Integer rid, HttpSession session) {
        User u = (User) session.getAttribute("user");
        if (u == null) return err("unauth");
        return service.insertWorkExperience(w, rid) ? ResponseEntity.ok(ok()) : err("add failed");
    }

    @PutMapping("/updateWorkExperience")
    public ResponseEntity<Map<String, Object>> update(@RequestBody WorkExperience w, HttpSession session) {
        User u = (User) session.getAttribute("user");
        if (u == null) return err("unauth");
        return service.updateWorkExperience(w, u.getId()) ? ResponseEntity.ok(ok()) : err("update failed");
    }

    @DeleteMapping("/deleteWorkExperience")
    public ResponseEntity<Map<String, Object>> delete(@RequestParam("id") Integer id, @RequestParam("resumeId") Integer rid, HttpSession session) {
        User u = (User) session.getAttribute("user");
        if (u == null) return err("unauth");
        return service.deleteWorkExperience(id, rid) ? ResponseEntity.ok(ok()) : err("delete failed");
    }

    @DeleteMapping("/{resumeId}/workExperienceBatch")
    public ResponseEntity<Map<String, Object>> batchDelete(@PathVariable Integer resumeId, @RequestBody Map<String, List<Integer>> body, HttpSession session) {
        User u = (User) session.getAttribute("user");
        if (u == null) return err("unauth");
        return service.WorkExperienceDeleteBatch(resumeId, body.get("ids"), u.getId()) ? ResponseEntity.ok(ok()) : err("batch delete failed");
    }

    @PutMapping("/{resumeId}/sortExperiences")
    public ResponseEntity<Map<String, Object>> sort(@PathVariable Integer resumeId, @RequestBody Map<String, List<Integer>> body, HttpSession session) {
        User u = (User) session.getAttribute("user");
        if (u == null) return err("unauth");
        return service.updateWorkExperienceSortOrderBatch(resumeId, body.get("sortedIds"), u.getId()) ? ResponseEntity.ok(ok()) : err("sort failed");
    }
}