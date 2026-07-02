package com.example.demo.controller;

import com.example.demo.dto.DeleteBatchRequest;
import com.example.demo.pojo.Certification;
import com.example.demo.pojo.User;
import com.example.demo.service.CertificationService;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/hcq/Certification")
public class CertificationController {

    private final CertificationService certificationService;

    public CertificationController(CertificationService certificationService) {
        this.certificationService = certificationService;
    }

    private static Map<String, Object> success() {
        Map<String, Object> map = new HashMap<>(2);
        map.put("success", true);
        return map;
    }

    private static ResponseEntity<Map<String, Object>> unSuccess(String message) {
        Map<String, Object> map = new HashMap<>(4);
        map.put("success", false);
        map.put("message", message);
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(map);
    }

    @PostMapping("/insertCertification")
    public ResponseEntity<Map<String, Object>> insertCertification(@RequestBody Certification certification,
                                                                   @RequestParam("resumeId") Integer resumeId,
                                                                   HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) return unSuccess("unauthorized");
        certification.setResumeId(resumeId);
        if (certificationService.addCertification(certification, user.getId())) {
            return ResponseEntity.ok(success());
        } else {
            return unSuccess("add failed");
        }
    }

    @PutMapping("/updateCertification")
    public ResponseEntity<Map<String, Object>> updateCertification(@RequestBody Certification certification,
                                                                   HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) return unSuccess("unauthorized");
        if (certificationService.updateCertification(certification, user.getId())) {
            return ResponseEntity.ok(success());
        } else {
            return unSuccess("update failed");
        }
    }

    @DeleteMapping("/deleteCertification")
    public ResponseEntity<Map<String, Object>> deleteCertification(@RequestParam("id") Integer id,
                                                                   @RequestParam("resumeId") Integer resumeId,
                                                                   HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) return unSuccess("unauthorized");
        if (certificationService.deleteCertification(id, resumeId, user.getId())) {
            return ResponseEntity.ok(success());
        } else {
            return unSuccess("delete failed");
        }
    }

    @GetMapping("/selectCertificationsByResumeId")
    public ResponseEntity<Map<String, Object>> selectCertificationsByResumeId(@RequestParam("resumeId") Integer resumeId,
                                                                              HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) return unSuccess("unauthorized");
        List<Certification> certifications = certificationService.getCertificationsByResumeId(resumeId, user.getId());
        Map<String, Object> result = new HashMap<>(2);
        result.put("success", true);
        result.put("certifications", certifications);
        return ResponseEntity.ok(result);
    }

    @DeleteMapping("/{resumeId}/certificationBatch")
    public ResponseEntity<Map<String, Object>> certificationBatchDelete(@PathVariable Integer resumeId,
                                                                        @RequestBody DeleteBatchRequest request,
                                                                        HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) return unSuccess("unauthorized");
        if (CollectionUtils.isEmpty(request.getIds())) return unSuccess("no ids");
        if (certificationService.deleteBatch(resumeId, request.getIds(), user.getId())) {
            return ResponseEntity.ok(success());
        } else {
            return unSuccess("batch delete failed");
        }
    }

    @PutMapping("/{resumeId}/certificationSort")
    public ResponseEntity<Map<String, Object>> certificationSort(@PathVariable Integer resumeId,
                                                                 @RequestBody List<Integer> sortedIds,
                                                                 HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) return unSuccess("unauthorized");
        if (CollectionUtils.isEmpty(sortedIds)) return unSuccess("empty sort list");
        if (certificationService.sortCertifications(resumeId, sortedIds, user.getId())) {
            return ResponseEntity.ok(success());
        } else {
            return unSuccess("sort failed");
        }
    }
}