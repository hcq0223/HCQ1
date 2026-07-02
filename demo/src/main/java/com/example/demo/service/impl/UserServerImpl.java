package com.example.demo.service.impl;

import com.example.demo.mapper.UserMapper;
import com.example.demo.pojo.User;
import com.example.demo.service.UserService;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;

@Service
public class UserServerImpl implements UserService {

    private final UserMapper userMapper;

    public UserServerImpl(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    @Override
    public Boolean UserLogin(String username, String plainPassword) {
        User user = userMapper.selectByUsername(username);
        if (user == null) return false;
        return BCrypt.checkpw(plainPassword, user.getPasswordHash());
    }

    @Override
    public Boolean adminLogin(String username, String plainPassword) {
        User user = userMapper.selectByUsernameAndRole(username, "admin");
        if (user == null) return false;
        return BCrypt.checkpw(plainPassword, user.getPasswordHash());
    }

    @Override
    public Boolean UserRegister(String username, String plainPassword, String email) {
        if (userMapper.selectByUsername(username) != null || userMapper.selectByEmail(email) != null) {
            return false;
        }
        User user = new User();
        user.setUsername(username);
        String hashedPassword = BCrypt.hashpw(plainPassword, BCrypt.gensalt());
        user.setPasswordHash(hashedPassword);
        user.setEmail(email);
        user.setRole("user");
        int rows = userMapper.insertUser(user);
        return rows == 1;
    }

    @Override
    public Boolean UserUpdate(Integer id,String username, String plainPassword, String email) {
        if (userMapper.selectByIds(id) ==  null){
            return false;
        }
        User user = new User();
        user.setId(id);
        if (username != null && !username.trim().isEmpty()) {
            user.setUsername(username);
        }
        if (plainPassword != null && !plainPassword.trim().isEmpty()) {
            String hashedPassword = BCrypt.hashpw(plainPassword, BCrypt.gensalt());
            user.setPasswordHash(hashedPassword);
        }
        if (email != null && !email.trim().isEmpty()) {
            user.setEmail(email);
        }
        if (user.getUsername() == null && user.getPasswordHash() == null && user.getEmail() == null) {
            return true;
        }
        int rows = userMapper.updateUser(user);
        return rows == 1;
    }

    @Override
    public Boolean forgetUser(String email, String plainPassword) {
        if (userMapper.selectByEmail(email) == null){
            return false;
        }
        User user = new User();
        user.setEmail(email);
        String hashedPassword = BCrypt.hashpw(plainPassword, BCrypt.gensalt());
        user.setPasswordHash(hashedPassword);
        int rows = userMapper.forgetUser(user);
        return rows == 1;
    }

    @Override
    public User UserSelectById(Integer id) {
        return userMapper.selectByIds(id);
    }

    @Override
    public User UserSelectByUsername(String username) {
        return userMapper.selectByUsername(username);
    }

    @Override
    public User UserSelectByEmail(String email) {
        return userMapper.selectByEmail(email);
    }

    @Override
    public Boolean UserDelete(Integer id) {
        User user = userMapper.selectByIds(id);
        if (id != null && user != null){
            int rows = userMapper.deleteUser(id);
            if (rows > 0){
                return true;
            }
        }
        return false;
    }
}