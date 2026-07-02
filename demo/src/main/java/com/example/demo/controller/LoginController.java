package com.example.demo.controller;

import com.example.demo.pojo.User;
import com.example.demo.service.UserService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/hcq")
@Validated
public class LoginController {

    private final UserService userService;

    public LoginController(UserService userService) {
        this.userService = userService;
    }

    private static Map<String, Object> success() {
        Map<String, Object> a = new HashMap<>(2);
        a.put("success", true);
        return a;
    }

    private static ResponseEntity<Map<String, Object>> unSuccess(String message) {
        Map<String, Object> a = new HashMap<>(4);
        a.put("success", false);
        a.put("message", message);
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(a);
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(
            @RequestParam @NotBlank(message = "用户名不能为空") String username,
            @RequestParam @NotBlank(message = "密码不能为空") String password,
            HttpSession session) {
        if (userService.UserLogin(username, password)) {
            User user = userService.UserSelectByUsername(username);
            user.setPasswordHash(null);
            session.setAttribute("user", user);
            Map<String, Object> r = new HashMap<>(4);
            r.put("success", true);
            r.put("user", user);
            return ResponseEntity.ok(r);
        } else {
            return unSuccess("用户名或密码错误！");
        }
    }

    @PostMapping("/admin/login")
    public ResponseEntity<Map<String, Object>> adminLogin(
            @RequestParam @NotBlank(message = "用户名不能为空") String username,
            @RequestParam @NotBlank(message = "密码不能为空") String password,
            HttpSession session) {
        if (userService.adminLogin(username, password)) {
            User user = userService.UserSelectByUsername(username);
            user.setPasswordHash(null);
            if (user.getRole() == null) user.setRole("admin");
            session.setAttribute("user", user);
            session.setAttribute("isAdmin", true);
            Map<String, Object> r = new HashMap<>(4);
            r.put("success", true);
            r.put("user", user);
            return ResponseEntity.ok(r);
        } else {
            return unSuccess("管理员账号或密码错误");
        }
    }

    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> register(
            @RequestParam @NotBlank(message = "用户名不能为空") String username,
            @RequestParam @NotBlank(message = "密码不能为空") String password,
            @RequestParam @Email(message = "邮箱格式不正确") @NotBlank(message = "邮箱不能为空") String email) {
        if (userService.UserRegister(username, password, email)) {
            return ResponseEntity.ok(success());
        } else {
            return unSuccess("邮箱或用户名已存在");
        }
    }

    @PutMapping("/update")
    public ResponseEntity<Map<String, Object>> update(
            @RequestBody User user,
            HttpSession session) {
        User user1 = (User) session.getAttribute("user");
        if (user1.getId() == null || user1.getId() <= 0) {
            return unSuccess("用户未登录或会话失效！");
        }
        User existingUser = userService.UserSelectByUsername(user.getUsername()); if (existingUser != null && !existingUser.getId().equals(user1.getId())) { return unSuccess("账号已存在！"); }
        User existingEmail = userService.UserSelectByEmail(user.getEmail()); if (existingEmail != null && !existingEmail.getId().equals(user1.getId())) { return unSuccess("邮箱已存在！"); }
        Boolean b = userService.UserUpdate(user1.getId(),
                user.getUsername(),
                user.getPasswordHash(),
                user.getEmail());
        if (!b) return unSuccess("更新失败！");
        if (user.getUsername() != null && !user.getUsername().isBlank()) user1.setUsername(user.getUsername());
        if (user.getEmail() != null && !user.getEmail().isBlank()) user1.setEmail(user.getEmail());
        session.setAttribute("user", user1);
        return ResponseEntity.ok(success());
    }

    @PostMapping("/forgetPassword")
    public ResponseEntity<Map<String, Object>> forgetPassword(
            @RequestParam @Email(message = "邮箱格式不正确") @NotBlank(message = "邮箱不能为空") String email,
            @RequestParam @NotBlank(message = "新密码不能为空") String plainPassword) {
        if (userService.forgetUser(email, plainPassword)) {
            return ResponseEntity.ok(success());
        } else {
            return unSuccess("邮箱不正确！");
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<Map<String, Object>> logout(HttpSession session) {
        session.invalidate();
        return ResponseEntity.ok(success());
    }
}