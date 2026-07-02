package com.example.demo.controller;

import com.example.demo.pojo.Skill;
import com.example.demo.pojo.User;
import com.example.demo.service.SkillService;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/hcq/Skill")
public class SkillController {
    private final SkillService service;

    public SkillController(SkillService s) {
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

    @GetMapping("/selectSkillsByResumeId")
    public ResponseEntity<Map<String, Object>> selectSkillsByResumeId(@RequestParam("resumeId") Integer rid, HttpSession session) {
        User u = (User) session.getAttribute("user");
        if (u == null) return err("unauth");
        List<Skill> list = service.getSkillsByResumeId(rid, u.getId());
        Map<String, Object> r = new HashMap<>();
        r.put("success", true);
        r.put("skills", list);
        return ResponseEntity.ok(r);
    }

    @GetMapping("/selectSkillsByCategory")
    public ResponseEntity<Map<String, Object>> selectSkillsByCategory(@RequestParam("resumeId") Integer rid, @RequestParam("category") String category, HttpSession session) {
        User u = (User) session.getAttribute("user");
        if (u == null) return err("unauth");
        List<Skill> list = service.getSkillsByResumeId(rid, u.getId());
        list = list.stream().filter(s -> category.equals(s.getCategory())).toList();
        Map<String, Object> r = new HashMap<>();
        r.put("success", true);
        r.put("skills", list);
        return ResponseEntity.ok(r);
    }

    @GetMapping("/getCategories")
    public ResponseEntity<Map<String, Object>> getCategories(@RequestParam("resumeId") Integer rid, HttpSession session) {
        User u = (User) session.getAttribute("user");
        if (u == null) return err("unauth");
        List<String> cats = service.getSkillsByResumeId(rid, u.getId()).stream().map(Skill::getCategory).filter(java.util.Objects::nonNull).distinct().toList();
        Map<String, Object> r = new HashMap<>();
        r.put("success", true);
        r.put("categories", cats);
        return ResponseEntity.ok(r);
    }

    @PostMapping("/insertSkill")
    public ResponseEntity<Map<String, Object>> insertSkill(@RequestBody Skill s, @RequestParam("resumeId") Integer rid, HttpSession session) {
        User u = (User) session.getAttribute("user");
        if (u == null) return err("unauth");
        s.setResumeId(rid);
        return service.addSkill(s, u.getId()) ? ResponseEntity.ok(ok()) : err("add failed");
    }

    @PutMapping("/updateSkill")
    public ResponseEntity<Map<String, Object>> updateSkill(@RequestBody Skill s, HttpSession session) {
        User u = (User) session.getAttribute("user");
        if (u == null) return err("unauth");
        return service.updateSkill(s, u.getId()) ? ResponseEntity.ok(ok()) : err("update failed");
    }

    @DeleteMapping("/deleteSkill")
    public ResponseEntity<Map<String, Object>> deleteSkill(@RequestParam("id") Integer id, @RequestParam("resumeId") Integer rid, HttpSession session) {
        User u = (User) session.getAttribute("user");
        if (u == null) return err("unauth");
        return service.deleteSkill(id, rid, u.getId()) ? ResponseEntity.ok(ok()) : err("delete failed");
    }

    @DeleteMapping("/{resumeId}/skillBatch")
    public ResponseEntity<Map<String, Object>> batchDelete(@PathVariable Integer resumeId, @RequestBody Map<String, List<Integer>> body, HttpSession session) {
        User u = (User) session.getAttribute("user");
        if (u == null) return err("unauth");
        return service.deleteBatch(resumeId, body.get("ids"), u.getId()) ? ResponseEntity.ok(ok()) : err("batch delete failed");
    }

    @PutMapping("/{resumeId}/skillSort")
    public ResponseEntity<Map<String, Object>> sort(@PathVariable Integer resumeId, @RequestBody List<Integer> sortedIds, HttpSession session) {
        User u = (User) session.getAttribute("user");
        if (u == null) return err("unauth");
        return service.sortSkills(resumeId, sortedIds, u.getId()) ? ResponseEntity.ok(ok()) : err("sort failed");
    }
}