package com.example.demo.controller;

import com.example.demo.pojo.Education;
import com.example.demo.pojo.User;
import com.example.demo.service.EducationService;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/hcq/Education")
public class EducationController {
    private final EducationService service;

    public EducationController(EducationService s) {
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

    @GetMapping("/selectEducationByResumeId")
    public ResponseEntity<Map<String, Object>> selectByResumeId(@RequestParam("resumeId") Integer rid, HttpSession session) {
        User u = (User) session.getAttribute("user");
        if (u == null) return err("unauth");
        List<Education> list = service.selectEducationByResumeId(rid);
        Map<String, Object> r = new HashMap<>();
        r.put("success", true);
        r.put("educations", list);
        return ResponseEntity.ok(r);
    }

    @PostMapping("/insertEducation")
    public ResponseEntity<Map<String, Object>> insert(@RequestBody Education e, @RequestParam("resumeId") Integer rid, HttpSession session) {
        User u = (User) session.getAttribute("user");
        if (u == null) return err("unauth");
        return service.insertEducation(e, rid) ? ResponseEntity.ok(ok()) : err("add failed");
    }

    @PutMapping("/updateEducation")
    public ResponseEntity<Map<String, Object>> update(@RequestBody Education e, HttpSession session) {
        User u = (User) session.getAttribute("user");
        if (u == null) return err("unauth");
        return service.updateEducation(e, u.getId()) ? ResponseEntity.ok(ok()) : err("update failed");
    }

    @DeleteMapping("/deleteEducation")
    public ResponseEntity<Map<String, Object>> delete(@RequestParam("id") Integer id, @RequestParam("resumeId") Integer rid, HttpSession session) {
        User u = (User) session.getAttribute("user");
        if (u == null) return err("unauth");
        return service.deleteEducation(id, rid) ? ResponseEntity.ok(ok()) : err("delete failed");
    }

    @DeleteMapping("/{resumeId}/educationBatch")
    public ResponseEntity<Map<String, Object>> batchDelete(@PathVariable Integer resumeId, @RequestBody Map<String, List<Integer>> body, HttpSession session) {
        User u = (User) session.getAttribute("user");
        if (u == null) return err("unauth");
        return service.EducationDeleteBatch(resumeId, body.get("ids"), u.getId()) ? ResponseEntity.ok(ok()) : err("batch delete failed");
    }
}