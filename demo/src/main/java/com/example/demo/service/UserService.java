package com.example.demo.service;

import com.example.demo.pojo.User;
import jakarta.servlet.http.HttpSession;

public interface UserService {
    Boolean UserLogin(String username, String plainPassword);
    Boolean adminLogin(String username, String plainPassword);
    Boolean UserRegister(String username, String plainPassword, String email);
    Boolean UserUpdate(Integer id,String username, String plainPassword, String email);
    Boolean forgetUser(String email, String plainPassword);
    User UserSelectById(Integer id);
    User UserSelectByUsername(String username);
    User UserSelectByEmail(String email);
    Boolean UserDelete(Integer id);

}