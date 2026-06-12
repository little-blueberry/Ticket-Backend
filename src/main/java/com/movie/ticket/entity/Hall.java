package com.movie.ticket.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Hall {
    private Integer id;
    private String name;
    private Integer capacity;
    private Integer rowCount;
    private Integer colCount;
    private LocalDateTime createTime;
}