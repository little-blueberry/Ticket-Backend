// controller/UserController.java
package com.movie.ticket.controller;

import com.movie.ticket.dto.Result;
import com.movie.ticket.entity.User;
import com.movie.ticket.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    // 用户登录
    @PostMapping("/login")
    public Result<Map<String, Object>> login(@RequestBody User user, HttpSession session) {
        User loginUser = userService.login(user.getUsername(), user.getPassword());
        if (loginUser != null) {
            session.setAttribute("userId", loginUser.getId());
            session.setAttribute("userRole", loginUser.getRole());
            Map<String, Object> data = new HashMap<>();
            data.put("user", loginUser);
            data.put("role", loginUser.getRole());
            return Result.success(data);
        }
        return Result.error("用户名或密码错误");
    }

    // 用户注册
    @PostMapping("/register")
    public Result<String> register(@RequestBody User user) {
        // 数据合法性检查
        if (user.getUsername() == null || user.getUsername().trim().isEmpty()) {
            return Result.error("用户名不能为空");
        }
        if (user.getPassword() == null || user.getPassword().length() < 6) {
            return Result.error("密码长度不能少于6位");
        }
        if (user.getPhone() != null && !user.getPhone().matches("^1[3-9]\\d{9}$")) {
            return Result.error("手机号格式不正确");
        }
        boolean success = userService.register(user);
        if (success) {
            return Result.success("注册成功");
        }
        return Result.error("用户名已存在");
    }

    // 获取当前登录用户信息
    @GetMapping("/info")
    public Result<User> getInfo(HttpSession session) {
        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null) {
            return Result.error(401, "未登录");
        }
        User user = userService.getById(userId);
        return Result.success(user);
    }

    // 退出登录
    @PostMapping("/logout")
    public Result<String> logout(HttpSession session) {
        session.invalidate();
        return Result.success("退出成功");
    }
}