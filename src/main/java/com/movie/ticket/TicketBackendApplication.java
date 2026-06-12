package com.movie.ticket;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@MapperScan("com.movie.ticket.mapper")  // 确保这个包路径正确
@ComponentScan(basePackages = {"com.movie.ticket"})
public class TicketBackendApplication {
    public static void main(String[] args) {
        SpringApplication.run(TicketBackendApplication.class, args);
        System.out.println("=========================================");
        System.out.println("   电影订票系统后端启动成功！");
        System.out.println("=========================================");
    }
}