package com.movie.ticket.service;

import com.movie.ticket.entity.Schedule;
import com.movie.ticket.mapper.ScheduleMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ScheduleService {

    @Autowired
    private ScheduleMapper scheduleMapper;

    /**
     * 根据电影ID获取场次列表
     */
    public List<Schedule> getSchedulesByMovieId(Integer movieId) {
        return scheduleMapper.findByMovieId(movieId);
    }

    /**
     * 获取所有场次（管理员用）
     */
    public List<Schedule> getAllSchedules() {
        return scheduleMapper.findAll();
    }

    /**
     * 获取场次详情
     */
    public Schedule getScheduleById(Integer id) {
        return scheduleMapper.findById(id);
    }

    /**
     * 添加场次
     */
    @Transactional
    public boolean addSchedule(Schedule schedule) {
        try {
            if (schedule.getStatus() == null) {
                schedule.setStatus(1);
            }
            if (schedule.getRemainSeats() == null || schedule.getRemainSeats() == 0) {
                // 默认80个座位
                schedule.setRemainSeats(80);
            }
            // 计算结束时间（默认电影时长120分钟）
            if (schedule.getStartTime() != null && schedule.getEndTime() == null) {
                schedule.setEndTime(schedule.getStartTime().plusMinutes(120));
            }
            schedule.setCreateTime(LocalDateTime.now());

            int result = scheduleMapper.insert(schedule);
            System.out.println("添加场次结果: " + result);
            return result > 0;
        } catch (Exception e) {
            System.out.println("添加场次异常: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 更新场次
     */
    public boolean updateSchedule(Schedule schedule) {
        return scheduleMapper.update(schedule) > 0;
    }

    /**
     * 删除场次
     */
    public boolean deleteSchedule(Integer id) {
        return scheduleMapper.deleteById(id) > 0;
    }

    /**
     * 减少剩余座位
     */
    public boolean decreaseRemainSeats(Integer scheduleId, Integer quantity) {
        return scheduleMapper.decreaseRemainSeats(scheduleId, quantity) > 0;
    }

    /**
     * 恢复剩余座位
     */
    public boolean increaseRemainSeats(Integer scheduleId, Integer quantity) {
        return scheduleMapper.increaseRemainSeats(scheduleId, quantity) > 0;
    }
}