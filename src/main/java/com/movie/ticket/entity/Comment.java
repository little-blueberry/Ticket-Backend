package com.movie.ticket.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Comment {
    private Integer id;
    private Integer userId;
    private Integer movieId;
    private String content;
    private Double rating;      // 评分 0-10
    private Integer likeCount;
    private LocalDateTime createTime;
}