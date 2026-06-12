package com.movie.ticket.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/test")
public class TestController {

    @GetMapping("/hello")
    public Map<String, String> hello() {
        Map<String, String> result = new HashMap<>();
        result.put("message", "后端服务运行正常！");
        result.put("status", "success");
        return result;
    }
}