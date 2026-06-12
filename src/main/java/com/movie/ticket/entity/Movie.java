package com.movie.ticket.entity;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class Movie {
    private Integer id;
    private String title;
    private String poster;
    private String director;
    private String actors;
    private String type;
    private Integer duration;
    private String description;
    private BigDecimal rating;
    private LocalDate releaseDate;
    private Integer status;
    private LocalDateTime createTime;
}