package com.example.demo.controller;

import com.example.demo.pojo.Resume;
import com.example.demo.pojo.ResumeDetailVO;
import com.example.demo.pojo.User;
import com.example.demo.service.ResumeDetailService;
import com.example.demo.service.ResumeService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/hcq/Resume")
public class ResumeServiceController {
    private final ResumeService resumeService;
    private final ResumeDetailService resumeDetailService;
    private final ObjectMapper objectMapper;

    public ResumeServiceController(ResumeService a, ResumeDetailService b, ObjectMapper c) {
        this.resumeService = a;
        this.resumeDetailService = b;
        this.objectMapper = c;
    }

    private static Map<String, Object> s() {
        Map<String, Object> m = new HashMap<>(2);
        m.put("success", true);
        return m;
    }

    private static ResponseEntity<Map<String, Object>> e(String msg) {
        Map<String, Object> m = new HashMap<>(4);
        m.put("success", false);
        m.put("message", msg);
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(m);
    }

    @PostMapping("/insertResume")
    public ResponseEntity<Map<String, Object>> insertResume(@RequestBody Resume resume, HttpSession session) {
        User u = (User) session.getAttribute("user");
        if (u == null) return e("unauthorized");
        return resumeService.insertResume(resume, u.getId()) ? ResponseEntity.ok(s()) : e("insert failed");
    }

    @PutMapping("/updateResume")
    public ResponseEntity<Map<String, Object>> updateResume(@RequestBody Resume resume, HttpSession session) {
        User u = (User) session.getAttribute("user");
        if (u == null) return e("unauthorized");
        resume.setUserId(u.getId());
        if (resume.getId() == null) return e("missing id");
        return resumeService.updateResume(resume) ? ResponseEntity.ok(s()) : e("update failed");
    }

    @PutMapping(value = "/updateResumeWithAvatar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Map<String, Object>> updateResumeWithAvatar(@RequestPart("resume") String resumeJson, @RequestPart(value = "avatar", required = false) MultipartFile avatarFile, HttpSession session) {
        User u = (User) session.getAttribute("user");
        if (u == null) return e("unauthorized");
        Resume resume;
        try {
            resume = objectMapper.readValue(resumeJson, Resume.class);
        } catch (Exception ex) {
            return e("parse error");
        }
        resume.setUserId(u.getId());
        if (resume.getId() == null) return e("missing id");
        return resumeService.updateResumeWithAvatar(resume, avatarFile) ? ResponseEntity.ok(s()) : e("update failed");
    }

    @PutMapping("/isDeleteResume")
    public ResponseEntity<Map<String, Object>> isDeleteResume(@RequestParam("id") Integer id, HttpSession session) {
        User u = (User) session.getAttribute("user");
        if (u == null) return e("unauthorized");
        return resumeService.isDeleteResume(id, u.getId()) ? ResponseEntity.ok(s()) : e("delete failed");
    }

    @PutMapping("/noIsDeleteResume")
    public ResponseEntity<Map<String, Object>> noIsDeleteResume(@RequestParam("id") Integer id, HttpSession session) {
        User u = (User) session.getAttribute("user");
        if (u == null) return e("unauthorized");
        return resumeService.noIsDeleteResume(id, u.getId()) ? ResponseEntity.ok(s()) : e("restore failed");
    }

    @GetMapping("/selectResumeByUserId")
    public ResponseEntity<Map<String, Object>> selectResumeByUserId(HttpSession session) {
        User u = (User) session.getAttribute("user");
        if (u == null) return e("unauthorized");
        Map<String, Object> r = new HashMap<>();
        r.put("resumes", resumeService.selectResumeByUserId(u.getId()));
        return ResponseEntity.ok(r);
    }

    @GetMapping("/selectResumeByTemplateId")
    public ResponseEntity<Map<String, Object>> selectResumeByTemplateId(@RequestParam("templateId") Integer templateId, HttpSession session) {
        User u = (User) session.getAttribute("user");
        if (u == null) return e("unauthorized");
        Map<String, Object> r = new HashMap<>();
        r.put("resumes", resumeService.selectResumeByTemplateId(u.getId(), templateId));
        return ResponseEntity.ok(r);
    }

    @GetMapping("/selectResumeByStatus")
    public ResponseEntity<Map<String, Object>> selectResumeByStatus(@RequestParam("status") String status, HttpSession session) {
        User u = (User) session.getAttribute("user");
        if (u == null) return e("unauthorized");
        Map<String, Object> r = new HashMap<>();
        r.put("resumes", resumeService.selectResumeByStatus(u.getId(), status));
        return ResponseEntity.ok(r);
    }

    @PutMapping("/updateStatusResume")
    public ResponseEntity<Map<String, Object>> updateStatusResume(@RequestParam("id") Integer id, @RequestParam("status") String status, HttpSession session) {
        User u = (User) session.getAttribute("user");
        if (u == null) return e("unauthorized");
        if ("draft".equals(status) && resumeService.updateStatusDraftResume(id, u.getId()))
            return ResponseEntity.ok(s());
        if ("published".equals(status) && resumeService.updateStatusPublishedResume(id, u.getId()))
            return ResponseEntity.ok(s());
        return e("update status failed");
    }

    @GetMapping("/selectTrashResume")
    public ResponseEntity<Map<String, Object>> selectTrashResume(HttpSession session) {
        User u = (User) session.getAttribute("user");
        if (u == null) return e("unauthorized");
        Map<String, Object> r = new HashMap<>();
        r.put("resumes", resumeService.selectTrashResume(u.getId()));
        return ResponseEntity.ok(r);
    }

    @DeleteMapping("/deleteResume")
    public ResponseEntity<Map<String, Object>> deleteResume(@RequestParam("id") Integer id, HttpSession session) {
        User u = (User) session.getAttribute("user");
        if (u == null) return e("unauthorized");
        return resumeService.deleteResume(id, u.getId()) ? ResponseEntity.ok(s()) : e("delete failed");
    }

    @GetMapping("/getFullResume")
    public ResponseEntity<Map<String, Object>> getFullResume(@RequestParam("resumeId") Integer resumeId, HttpSession session) {
        User u = (User) session.getAttribute("user");
        if (u == null) return e("unauthorized");
        ResumeDetailVO vo = resumeDetailService.getOwnResume(resumeId, u.getId());
        if (vo != null) {
            Map<String, Object> r = new HashMap<>();
            r.put("success", true);
            r.put("resume", vo);
            return ResponseEntity.ok(r);
        }
        return e("resume not found");
    }
}