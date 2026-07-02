package com.example.demo.controller;

import com.example.demo.pojo.ResumeTemplate;
import com.example.demo.pojo.User;
import com.example.demo.service.Avatar.TemplateImageService;
import com.example.demo.service.ResumeTemplateService;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/hcq/ResumeTemplate")
public class ResumeTemplateController {
    private final ResumeTemplateService resumeTemplateService;
    private final TemplateImageService templateImageService;

    public ResumeTemplateController(ResumeTemplateService s, TemplateImageService ti) {
        this.resumeTemplateService = s;
        this.templateImageService = ti;
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
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(m);
    }

    private static boolean isAdmin(HttpSession session) {
        User u = (User) session.getAttribute("user");
        return u != null && "admin".equals(u.getRole());
    }

    @PostMapping("/insertTemplate")
    public ResponseEntity<Map<String, Object>> insertTemplate(@RequestBody ResumeTemplate t, HttpSession session) {
        if (!isAdmin(session)) return err("admin only");
        return resumeTemplateService.addTemplate(t) ? ResponseEntity.ok(ok()) : err("add failed");
    }

    @PutMapping("/updateTemplate")
    public ResponseEntity<Map<String, Object>> updateTemplate(@RequestBody ResumeTemplate t, HttpSession session) {
        if (!isAdmin(session)) return err("admin only");
        return resumeTemplateService.updateTemplate(t) ? ResponseEntity.ok(ok()) : err("update failed");
    }

    @DeleteMapping("/deleteTemplate")
    public ResponseEntity<Map<String, Object>> deleteTemplate(@RequestParam("id") Integer id, HttpSession session) {
        if (!isAdmin(session)) return err("admin only");
        return resumeTemplateService.deleteTemplate(id) ? ResponseEntity.ok(ok()) : err("delete failed");
    }

    @PutMapping("/toggleActiveStatus")
    public ResponseEntity<Map<String, Object>> toggleActiveStatus(@RequestParam("id") Integer id, @RequestParam("isActive") Boolean isActive, HttpSession session) {
        if (!isAdmin(session)) return err("admin only");
        return resumeTemplateService.toggleActiveStatus(id, isActive) ? ResponseEntity.ok(ok()) : err("toggle failed");
    }

    @GetMapping("/getTemplateList")
    public ResponseEntity<Map<String, Object>> getTemplateList(@RequestParam(value = "category", required = false) String cat, @RequestParam(value = "isPremium", required = false) Boolean prem, @RequestParam(value = "isActive", required = false) Boolean act, @RequestParam(value = "keyword", required = false) String kw, HttpSession session) {
        if (!isAdmin(session)) return err("admin only");
        List<ResumeTemplate> l = resumeTemplateService.getTemplateList(cat, prem, act, kw);
        Map<String, Object> r = new HashMap<>(2);
        r.put("success", true);
        r.put("templates", l);
        return ResponseEntity.ok(r);
    }

    @GetMapping("/getTemplateById")
    public ResponseEntity<Map<String, Object>> getTemplateById(@RequestParam("id") Integer id, HttpSession session) {
        User u = (User) session.getAttribute("user");
        if (u == null) return err("unauthorized");
        ResumeTemplate t = resumeTemplateService.getTemplateById(id);
        Map<String, Object> r = new HashMap<>(2);
        r.put("success", true);
        r.put("template", t);
        return ResponseEntity.ok(r);
    }

    @GetMapping("/getAvailableTemplates")
    public ResponseEntity<Map<String, Object>> getAvailableTemplates(@RequestParam(value = "category", required = false) String cat, @RequestParam(value = "isPremium", required = false) Boolean prem, HttpSession session) {
        User u = (User) session.getAttribute("user");
        if (u == null) return err("unauthorized");
        List<ResumeTemplate> l = resumeTemplateService.getAvailableTemplates(cat, prem);
        Map<String, Object> r = new HashMap<>(2);
        r.put("success", true);
        r.put("templates", l);
        return ResponseEntity.ok(r);
    }

    @PostMapping("/uploadPreviewImage")
    public ResponseEntity<Map<String, Object>> uploadPreviewImage(@RequestParam("file") MultipartFile file, HttpSession session) {
        if (!isAdmin(session)) {
            return err("not admin: " + ((User) session.getAttribute("user")).getRole());
        }
        if (file == null || file.isEmpty()) {
            return err("file is empty or missing");
        }
        try {
            String url = templateImageService.upload(file);
            Map<String, Object> r = new HashMap<>(2);
            r.put("success", true);
            r.put("url", url);
            System.out.println("Template image uploaded: " + url);
            return ResponseEntity.ok(r);
        } catch (Exception ex) {
            System.err.println("Upload error: " + ex.getMessage());
            return err("upload failed: " + ex.getMessage());
        }
    }
}