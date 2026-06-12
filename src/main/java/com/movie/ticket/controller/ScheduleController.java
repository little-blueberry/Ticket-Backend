package com.movie.ticket.controller;

import com.movie.ticket.dto.Result;
import com.movie.ticket.entity.Schedule;
import com.movie.ticket.service.ScheduleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.List;

@RestController
@RequestMapping("/schedule")
public class ScheduleController {

    @Autowired
    private ScheduleService scheduleService;

    /**
     * 根据电影ID获取场次（用户端）
     */
    @GetMapping("/movie/{movieId}")
    public Result<List<Schedule>> getSchedulesByMovie(@PathVariable Integer movieId) {
        List<Schedule> schedules = scheduleService.getSchedulesByMovieId(movieId);
        return Result.success(schedules);
    }

    /**
     * 获取所有场次（管理员）
     */
    @GetMapping("/admin/list")
    public Result<List<Schedule>> getAllSchedules(HttpSession session) {
        String role = (String) session.getAttribute("userRole");
        if (!"admin".equals(role)) {
            return Result.error(403, "无权限操作");
        }
        List<Schedule> schedules = scheduleService.getAllSchedules();
        return Result.success(schedules);
    }

    /**
     * 获取场次详情
     */
    @GetMapping("/{id}")
    public Result<Schedule> getScheduleById(@PathVariable Integer id) {
        Schedule schedule = scheduleService.getScheduleById(id);
        if (schedule == null) {
            return Result.error("场次不存在");
        }
        return Result.success(schedule);
    }

    /**
     * 添加场次（管理员）
     */
    @PostMapping("/admin/add")
    public Result<String> addSchedule(@RequestBody Schedule schedule, HttpSession session) {
        String role = (String) session.getAttribute("userRole");
        if (!"admin".equals(role)) {
            return Result.error(403, "无权限操作");
        }
        boolean success = scheduleService.addSchedule(schedule);
        return success ? Result.success("添加成功") : Result.error("添加失败");
    }

    /**
     * 更新场次（管理员）
     */
    @PutMapping("/admin/update")
    public Result<String> updateSchedule(@RequestBody Schedule schedule, HttpSession session) {
        String role = (String) session.getAttribute("userRole");
        if (!"admin".equals(role)) {
            return Result.error(403, "无权限操作");
        }
        boolean success = scheduleService.updateSchedule(schedule);
        return success ? Result.success("更新成功") : Result.error("更新失败");
    }

    /**
     * 删除场次（管理员）
     */
    @DeleteMapping("/admin/{id}")
    public Result<String> deleteSchedule(@PathVariable Integer id, HttpSession session) {
        String role = (String) session.getAttribute("userRole");
        if (!"admin".equals(role)) {
            return Result.error(403, "无权限操作");
        }
        boolean success = scheduleService.deleteSchedule(id);
        return success ? Result.success("删除成功") : Result.error("删除失败");
    }
}