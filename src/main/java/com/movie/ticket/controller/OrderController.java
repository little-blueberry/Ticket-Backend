// controller/OrderController.java
package com.movie.ticket.controller;

import com.movie.ticket.dto.Result;
import com.movie.ticket.entity.Order;
import com.movie.ticket.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/order")
public class OrderController {

    @Autowired
    private OrderService orderService;

    // 创建订单（选座下单）
    @PostMapping("/create")
    public Result<Order> createOrder(@RequestBody Map<String, Object> params, HttpSession session) {
        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null) {
            return Result.error(401, "请先登录");
        }

        Integer scheduleId = (Integer) params.get("scheduleId");
        String seatInfo = (String) params.get("seatInfo");
        Integer quantity = (Integer) params.get("quantity");

        // 数据合法性检查
        if (scheduleId == null || seatInfo == null || seatInfo.trim().isEmpty()) {
            return Result.error("请选择座位");
        }

        Order order = orderService.createOrder(userId, scheduleId, seatInfo, quantity);
        if (order != null) {
            return Result.success(order);
        }
        return Result.error("座位已被选，请重新选择");
    }

    // 支付订单（模拟支付）
    @PostMapping("/pay/{orderId}")
    public Result<String> payOrder(@PathVariable Integer orderId, HttpSession session) {
        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null) {
            return Result.error(401, "请先登录");
        }

        boolean success = orderService.payOrder(orderId, userId);
        if (success) {
            return Result.success("支付成功");
        }
        return Result.error("支付失败，请检查余额或订单状态");
    }

    // 取消订单
    @PostMapping("/cancel/{orderId}")
    public Result<String> cancelOrder(@PathVariable Integer orderId, HttpSession session) {
        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null) {
            return Result.error(401, "请先登录");
        }

        boolean success = orderService.cancelOrder(orderId, userId);
        return success ? Result.success("取消成功") : Result.error("取消失败，订单可能已支付或已过期");
    }

    // 获取用户订单列表
    @GetMapping("/myOrders")
    public Result<List<Map<String, Object>>> getMyOrders(HttpSession session) {
        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null) {
            return Result.error(401, "请先登录");
        }
        List<Map<String, Object>> orders = orderService.getUserOrders(userId);
        return Result.success(orders);
    }

    // 获取所有订单（管理员）
    @GetMapping("/admin/all")
    public Result<List<Map<String, Object>>> getAllOrders(HttpSession session) {
        String role = (String) session.getAttribute("userRole");
        if (!"admin".equals(role)) {
            return Result.error(403, "无权限操作");
        }
        List<Map<String, Object>> orders = orderService.getAllOrders();
        return Result.success(orders);
    }
}