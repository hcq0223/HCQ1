package com.example.demo.controller;

import com.example.demo.pojo.CustomSection;
import com.example.demo.pojo.User;
import com.example.demo.service.CustomSectionService;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/hcq/CustomSection")
public class CustomSectionController {
    private final CustomSectionService service;

    public CustomSectionController(CustomSectionService s) {
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

    @GetMapping("/selectCustomSectionsByResumeId")
    public ResponseEntity<Map<String, Object>> selectByResumeId(@RequestParam("resumeId") Integer rid, HttpSession session) {
        User u = (User) session.getAttribute("user");
        if (u == null) return err("unauth");
        List<CustomSection> list = service.getCustomSectionsByResumeId(rid, u.getId());
        Map<String, Object> r = new HashMap<>();
        r.put("success", true);
        r.put("sections", list);
        return ResponseEntity.ok(r);
    }

    @PostMapping("/insertCustomSection")
    public ResponseEntity<Map<String, Object>> insert(@RequestBody CustomSection cs, @RequestParam("resumeId") Integer rid, HttpSession session) {
        User u = (User) session.getAttribute("user");
        if (u == null) return err("unauth");
        cs.setResumeId(rid);
        return service.addCustomSection(cs, u.getId()) ? ResponseEntity.ok(ok()) : err("add failed");
    }

    @PutMapping("/updateCustomSection")
    public ResponseEntity<Map<String, Object>> update(@RequestBody CustomSection cs, HttpSession session) {
        User u = (User) session.getAttribute("user");
        if (u == null) return err("unauth");
        return service.updateCustomSection(cs, u.getId()) ? ResponseEntity.ok(ok()) : err("update failed");
    }

    @DeleteMapping("/deleteCustomSection")
    public ResponseEntity<Map<String, Object>> delete(@RequestParam("id") Integer id, @RequestParam("resumeId") Integer rid, HttpSession session) {
        User u = (User) session.getAttribute("user");
        if (u == null) return err("unauth");
        return service.deleteCustomSection(id, rid, u.getId()) ? ResponseEntity.ok(ok()) : err("delete failed");
    }

    @PutMapping("/{resumeId}/customSectionSort")
    public ResponseEntity<Map<String, Object>> sort(@PathVariable Integer resumeId, @RequestBody List<Integer> sortedIds, HttpSession session) {
        User u = (User) session.getAttribute("user");
        if (u == null) return err("unauth");
        return service.sortCustomSections(resumeId, sortedIds, u.getId()) ? ResponseEntity.ok(ok()) : err("sort failed");
    }
}