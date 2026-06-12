package com.movie.ticket.entity;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class Order {
    private Integer id;
    private String orderNo;
    private Integer userId;
    private Integer scheduleId;
    private String seatInfo;
    private Integer quantity;
    private BigDecimal totalAmount;
    private String status;  // pending, paid, cancelled, expired
    private LocalDateTime payTime;
    private LocalDateTime cancelTime;
    private LocalDateTime expireTime;
    private LocalDateTime createTime;
}