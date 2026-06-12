package com.movie.ticket.mapper;

import com.movie.ticket.entity.Schedule;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface ScheduleMapper {

    /**
     * 根据电影ID获取场次列表（包含影厅名称、电影名称）
     */
    @Select("SELECT s.*, m.title as movie_title, h.name as hall_name " +
            "FROM schedule s " +
            "JOIN movie m ON s.movie_id = m.id " +
            "JOIN hall h ON s.hall_id = h.id " +
            "WHERE s.movie_id = #{movieId} AND s.status = 1 AND s.start_time > NOW() " +
            "ORDER BY s.start_time")
    List<Schedule> findByMovieId(@Param("movieId") Integer movieId);

    /**
     * 根据ID获取场次详情（包含影厅名称、电影名称）
     */
    @Select("SELECT s.*, m.title as movie_title, h.name as hall_name, h.capacity " +
            "FROM schedule s " +
            "JOIN movie m ON s.movie_id = m.id " +
            "JOIN hall h ON s.hall_id = h.id " +
            "WHERE s.id = #{id}")
    Schedule findById(@Param("id") Integer id);

    /**
     * 获取所有场次（管理员用，包含关联信息）
     */
    @Select("SELECT s.*, m.title as movie_title, h.name as hall_name " +
            "FROM schedule s " +
            "JOIN movie m ON s.movie_id = m.id " +
            "JOIN hall h ON s.hall_id = h.id " +
            "ORDER BY s.start_time DESC")
    List<Schedule> findAll();

    /**
     * 添加场次
     */
    @Insert("INSERT INTO schedule (movie_id, hall_id, start_time, end_time, price, remain_seats, status, create_time) " +
            "VALUES (#{movieId}, #{hallId}, #{startTime}, #{endTime}, #{price}, #{remainSeats}, #{status}, #{createTime})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Schedule schedule);

    /**
     * 更新场次信息
     */
    @Update("UPDATE schedule SET movie_id = #{movieId}, hall_id = #{hallId}, " +
            "start_time = #{startTime}, end_time = #{endTime}, price = #{price}, status = #{status} " +
            "WHERE id = #{id}")
    int update(Schedule schedule);

    /**
     * 删除场次（物理删除）
     */
    @Delete("DELETE FROM schedule WHERE id = #{id}")
    int deleteById(@Param("id") Integer id);

    /**
     * 减少剩余座位数（支付成功后调用）
     */
    @Update("UPDATE schedule SET remain_seats = remain_seats - #{quantity} " +
            "WHERE id = #{id} AND remain_seats >= #{quantity}")
    int decreaseRemainSeats(@Param("id") Integer id, @Param("quantity") Integer quantity);

    /**
     * 恢复剩余座位数（取消订单时调用）
     */
    @Update("UPDATE schedule SET remain_seats = remain_seats + #{quantity} WHERE id = #{id}")
    int increaseRemainSeats(@Param("id") Integer id, @Param("quantity") Integer quantity);
}