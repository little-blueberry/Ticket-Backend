// service/UserService.java
package com.movie.ticket.service;

import com.movie.ticket.entity.User;
import com.movie.ticket.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class UserService {

    @Autowired
    private UserMapper userMapper;

    public User login(String username, String password) {
        return userMapper.findByUsernameAndPassword(username, password);
    }

    public boolean register(User user) {
        // 检查用户名是否已存在
        User existUser = userMapper.findByUsername(user.getUsername());
        if (existUser != null) {
            return false;
        }
        // 初始化新用户数据
        user.setBalance(BigDecimal.valueOf(100));  // 赠送100元
        user.setPoints(0);
        user.setRole("user");
        user.setStatus(1);
        return userMapper.insert(user) > 0;
    }

    public User getById(Integer id) {
        return userMapper.findById(id);
    }
}