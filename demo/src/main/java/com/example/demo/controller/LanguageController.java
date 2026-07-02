package com.example.demo.controller;

import com.example.demo.pojo.Language;
import com.example.demo.pojo.User;
import com.example.demo.service.LanguageService;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/hcq/Language")
public class LanguageController {
    private final LanguageService service;

    public LanguageController(LanguageService s) {
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

    @GetMapping("/selectLanguageByResumeId")
    public ResponseEntity<Map<String, Object>> selectLanguageByResumeId(@RequestParam("resumeId") Integer rid, HttpSession session) {
        User u = (User) session.getAttribute("user");
        if (u == null) return err("unauth");
        List<Language> list = service.getLanguagesByResumeId(rid, u.getId());
        Map<String, Object> r = new HashMap<>();
        r.put("success", true);
        r.put("languages", list);
        return ResponseEntity.ok(r);
    }

    @PostMapping("/insertLanguage")
    public ResponseEntity<Map<String, Object>> insertLanguage(@RequestBody Language lang, HttpSession session) {
        User u = (User) session.getAttribute("user");
        if (u == null) return err("unauth");
        if (lang.getResumeId() == null) return err("no resumeId");
        return service.addLanguage(lang, u.getId()) ? ResponseEntity.ok(ok()) : err("add failed");
    }

    @PutMapping("/updateLanguage")
    public ResponseEntity<Map<String, Object>> updateLanguage(@RequestBody Language lang, HttpSession session) {
        User u = (User) session.getAttribute("user");
        if (u == null) return err("unauth");
        return service.updateLanguage(lang, u.getId()) ? ResponseEntity.ok(ok()) : err("update failed");
    }

    @DeleteMapping("/deleteLanguage")
    public ResponseEntity<Map<String, Object>> deleteLanguage(@RequestParam("id") Integer id, @RequestParam("resumeId") Integer rid, HttpSession session) {
        User u = (User) session.getAttribute("user");
        if (u == null) return err("unauth");
        return service.deleteLanguage(id, rid, u.getId()) ? ResponseEntity.ok(ok()) : err("delete failed");
    }

    @DeleteMapping("/{resumeId}/languageBatch")
    public ResponseEntity<Map<String, Object>> batchDelete(@PathVariable Integer resumeId, @RequestBody Map<String, List<Integer>> body, HttpSession session) {
        User u = (User) session.getAttribute("user");
        if (u == null) return err("unauth");
        return service.deleteBatch(resumeId, body.get("ids"), u.getId()) ? ResponseEntity.ok(ok()) : err("batch delete failed");
    }

    @PutMapping("/{resumeId}/languageSort")
    public ResponseEntity<Map<String, Object>> sort(@PathVariable Integer resumeId, @RequestBody List<Integer> sortedIds, HttpSession session) {
        User u = (User) session.getAttribute("user");
        if (u == null) return err("unauth");
        return service.sortLanguages(resumeId, sortedIds, u.getId()) ? ResponseEntity.ok(ok()) : err("sort failed");
    }
}