package com.movie.ticket.service;

import com.movie.ticket.entity.Order;
import com.movie.ticket.entity.Schedule;
import com.movie.ticket.entity.User;
import com.movie.ticket.mapper.OrderMapper;
import com.movie.ticket.mapper.ScheduleMapper;
import com.movie.ticket.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class OrderService {

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private ScheduleMapper scheduleMapper;

    @Autowired
    private UserMapper userMapper;

    // 生成唯一订单号
    private String generateOrderNo() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"))
                + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    @Transactional
    public Order createOrder(Integer userId, Integer scheduleId, String seatInfo, Integer quantity) {
        // 检查座位是否已被占用
        Order existOrder = orderMapper.findByScheduleAndSeat(scheduleId, seatInfo);
        if (existOrder != null && "pending".equals(existOrder.getStatus())) {
            return null;
        }

        Schedule schedule = scheduleMapper.findById(scheduleId);
        if (schedule == null || schedule.getRemainSeats() < quantity) {
            return null;
        }

        Order order = new Order();
        order.setOrderNo(generateOrderNo());
        order.setUserId(userId);
        order.setScheduleId(scheduleId);
        order.setSeatInfo(seatInfo);
        order.setQuantity(quantity);
        order.setTotalAmount(schedule.getPrice().multiply(BigDecimal.valueOf(quantity)));
        order.setStatus("pending");
        order.setExpireTime(LocalDateTime.now().plusMinutes(15));
        order.setCreateTime(LocalDateTime.now());

        int result = orderMapper.insert(order);
        if (result > 0) {
            return order;
        }
        return null;
    }

    @Transactional
    public boolean payOrder(Integer orderId, Integer userId) {
        Order order = orderMapper.findById(orderId);
        if (order == null || !order.getUserId().equals(userId)) {
            return false;
        }
        if (!"pending".equals(order.getStatus())) {
            return false;
        }
        if (order.getExpireTime().isBefore(LocalDateTime.now())) {
            return false;
        }

        User user = userMapper.findById(userId);
        if (user.getBalance().compareTo(order.getTotalAmount()) < 0) {
            return false;
        }

        // 扣减余额
        userMapper.updateBalance(userId, user.getBalance().subtract(order.getTotalAmount()));

        // 【修改点】改用已有的 updateToPaid 方法
        int result = orderMapper.updateToPaid(orderId, LocalDateTime.now());
        return result > 0;
    }

    @Transactional
    public boolean cancelOrder(Integer orderId, Integer userId) {
        Order order = orderMapper.findById(orderId);
        if (order == null || !order.getUserId().equals(userId)) {
            return false;
        }
        if (!"pending".equals(order.getStatus())) {
            return false;
        }

        // 【修改点】改用已有的 updateToCancelled 方法
        int result = orderMapper.updateToCancelled(orderId, LocalDateTime.now());
        return result > 0;
    }

    // 新增：批量过期订单方法（使用 Mapper 中已有的 batchExpireOrders）
    @Transactional
    public int expirePendingOrders() {
        return orderMapper.batchExpireOrders();
    }

    public List<Map<String, Object>> getUserOrders(Integer userId) {
        return orderMapper.findByUserIdWithDetail(userId);
    }

    public List<Map<String, Object>> getAllOrders() {
        return orderMapper.findAllWithDetail();
    }

    // 新增：根据ID获取订单
    public Order getOrderById(Integer orderId) {
        return orderMapper.findById(orderId);
    }

    // 新增：获取场次已占座位
    public List<String> getOccupiedSeats(Integer scheduleId) {
        return orderMapper.findSeatsByScheduleId(scheduleId);
    }



}