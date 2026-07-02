package com.example.demo.mapper;

import com.example.demo.pojo.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface UserMapper {
    User selectByIds (Integer id);
    User selectByUsername (String username);
    User selectByEmail (String email);
    User selectByUsernameAndRole(@Param("username") String username, @Param("role") String role);
    List<User> selectAllUsers();
    int insertUser (User user);
    int updateUser (User user);
    int forgetUser (User user);
    int deleteUser (Integer id);
}