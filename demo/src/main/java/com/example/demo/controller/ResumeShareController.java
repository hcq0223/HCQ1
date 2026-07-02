package com.example.demo.controller;

import com.example.demo.pojo.ResumeDetailVO;
import com.example.demo.pojo.ResumeShare;
import com.example.demo.pojo.User;
import com.example.demo.service.ResumeDetailService;
import com.example.demo.service.ResumeShareService;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/hcq/ResumeShare")
public class ResumeShareController {
    private final ResumeShareService shareService;
    private final ResumeDetailService detailService;

    public ResumeShareController(ResumeShareService a, ResumeDetailService b) {
        this.shareService = a;
        this.detailService = b;
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

    @PostMapping("/createShare")
    public ResponseEntity<Map<String, Object>> createShare(@RequestParam("resumeId") Integer rid, @RequestParam(value = "expiresAt", required = false) String expiresAtStr, HttpSession session) {
        User u = (User) session.getAttribute("user");
        if (u == null) return err("unauth");
        LocalDateTime expiresAt = (expiresAtStr != null && !expiresAtStr.isEmpty()) ? LocalDateTime.parse(expiresAtStr) : null;
        ResumeShare rs = shareService.createShare(rid, expiresAt, u.getId());
        if (rs != null) {
            Map<String, Object> r = new HashMap<>();
            r.put("success", true);
            r.put("share", rs);
            return ResponseEntity.ok(r);
        }
        return err("create failed");
    }

    @GetMapping("/getSharesByResumeId")
    public ResponseEntity<Map<String, Object>> getSharesByResumeId(@RequestParam("resumeId") Integer rid, HttpSession session) {
        User u = (User) session.getAttribute("user");
        if (u == null) return err("unauth");
        List<ResumeShare> list = shareService.getSharesByResumeId(rid, u.getId());
        Map<String, Object> r = new HashMap<>();
        r.put("success", true);
        r.put("shares", list);
        return ResponseEntity.ok(r);
    }

    @PutMapping("/disableShare")
    public ResponseEntity<Map<String, Object>> disableShare(@RequestParam("id") Integer id, @RequestParam("resumeId") Integer rid, HttpSession session) {
        User u = (User) session.getAttribute("user");
        if (u == null) return err("unauth");
        return shareService.disableShare(id, rid, u.getId()) ? ResponseEntity.ok(ok()) : err("disable failed");
    }

    @PutMapping("/enableShare")
    public ResponseEntity<Map<String, Object>> enableShare(@RequestParam("id") Integer id, @RequestParam("resumeId") Integer rid, HttpSession session) {
        User u = (User) session.getAttribute("user");
        if (u == null) return err("unauth");
        return shareService.enableShare(id, rid, u.getId()) ? ResponseEntity.ok(ok()) : err("enable failed");
    }

    @PutMapping("/updateExpiresAt")
    public ResponseEntity<Map<String, Object>> updateExpiresAt(@RequestParam("id") Integer id, @RequestParam("resumeId") Integer rid, @RequestParam("expiresAt") String expiresAtStr, HttpSession session) {
        User u = (User) session.getAttribute("user");
        if (u == null) return err("unauth");
        LocalDateTime expiresAt = (expiresAtStr != null && !expiresAtStr.isEmpty()) ? LocalDateTime.parse(expiresAtStr) : null;
        return shareService.updateExpiresAt(id, rid, expiresAt, u.getId()) ? ResponseEntity.ok(ok()) : err("update failed");
    }

    @DeleteMapping("/deleteShare")
    public ResponseEntity<Map<String, Object>> deleteShare(@RequestParam("id") Integer id, @RequestParam("resumeId") Integer rid, HttpSession session) {
        User u = (User) session.getAttribute("user");
        if (u == null) return err("unauth");
        return shareService.deleteShare(id, rid, u.getId()) ? ResponseEntity.ok(ok()) : err("delete failed");
    }

    @GetMapping("/public/view")
    public ResponseEntity<Map<String, Object>> viewSharedResume(@RequestParam("token") String token) {
        ResumeShare rs = shareService.validateAndIncrementView(token);
        if (rs != null) {
            Map<String, Object> r = new HashMap<>();
            r.put("success", true);
            r.put("share", rs);
            return ResponseEntity.ok(r);
        }
        return err("invalid token");
    }

    @GetMapping("/public/get")
    public ResponseEntity<Map<String, Object>> getPublicResume(@RequestParam("resumeId") Integer rid) {
        ResumeDetailVO vo = detailService.getPublicResume(rid);
        if (vo != null) {
            Map<String, Object> r = new HashMap<>();
            r.put("success", true);
            r.put("resume", vo);
            return ResponseEntity.ok(r);
        }
        return err("resume not found or not published");
    }
}