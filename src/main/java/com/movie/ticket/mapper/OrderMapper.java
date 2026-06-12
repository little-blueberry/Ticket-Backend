package com.movie.ticket.mapper;

import com.movie.ticket.entity.Order;
import org.apache.ibatis.annotations.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Mapper
public interface OrderMapper {

    @Insert("INSERT INTO orders (order_no, user_id, schedule_id, seat_info, quantity, total_amount, status, expire_time, create_time) " +
            "VALUES (#{orderNo}, #{userId}, #{scheduleId}, #{seatInfo}, #{quantity}, #{totalAmount}, #{status}, #{expireTime}, #{createTime})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Order order);

    @Select("SELECT * FROM orders WHERE id = #{id}")
    @Results(id = "orderMap", value = {
            @Result(property = "id", column = "id"),
            @Result(property = "orderNo", column = "order_no"),
            @Result(property = "userId", column = "user_id"),
            @Result(property = "scheduleId", column = "schedule_id"),
            @Result(property = "seatInfo", column = "seat_info"),
            @Result(property = "quantity", column = "quantity"),
            @Result(property = "totalAmount", column = "total_amount"),
            @Result(property = "status", column = "status"),
            @Result(property = "payTime", column = "pay_time"),
            @Result(property = "cancelTime", column = "cancel_time"),
            @Result(property = "expireTime", column = "expire_time"),
            @Result(property = "createTime", column = "create_time")
    })
    Order findById(@Param("id") Integer id);

    @Select("SELECT * FROM orders WHERE schedule_id = #{scheduleId} AND seat_info = #{seatInfo} AND status = 'pending'")
    Order findByScheduleAndSeat(@Param("scheduleId") Integer scheduleId, @Param("seatInfo") String seatInfo);

    @Update("UPDATE orders SET status = 'paid', pay_time = #{payTime} WHERE id = #{id} AND status = 'pending'")
    int updateToPaid(@Param("id") Integer id, @Param("payTime") LocalDateTime payTime);

    @Update("UPDATE orders SET status = 'cancelled', cancel_time = #{cancelTime} WHERE id = #{id} AND status = 'pending'")
    int updateToCancelled(@Param("id") Integer id, @Param("cancelTime") LocalDateTime cancelTime);

    @Update("UPDATE orders SET status = 'expired' WHERE id = #{id} AND status = 'pending'")
    int updateToExpired(@Param("id") Integer id);

    @Update("UPDATE orders SET status = 'expired' WHERE status = 'pending' AND expire_time < NOW()")
    int batchExpireOrders();

    /**
     * 查询用户的所有订单
     */
    @Select("SELECT " +
            "o.id, o.order_no, o.seat_info, o.quantity, o.total_amount, o.status, o.create_time, " +
            "m.title AS movie_title, m.poster, " +
            "s.start_time, " +
            "h.name AS hall_name " +
            "FROM orders o " +
            "LEFT JOIN schedule s ON o.schedule_id = s.id " +
            "LEFT JOIN movie m ON s.movie_id = m.id " +
            "LEFT JOIN hall h ON s.hall_id = h.id " +
            "WHERE o.user_id = #{userId} " +
            "ORDER BY o.create_time DESC")
    List<Map<String, Object>> findByUserIdWithDetail(@Param("userId") Integer userId);

    /**
     * 查询所有订单详情（管理员用）
     */
    @Select("SELECT " +
            "o.id, " +
            "o.order_no, " +
            "o.seat_info, " +
            "o.quantity, " +
            "o.total_amount, " +
            "o.status, " +
            "o.create_time, " +
            "u.username, " +
            "COALESCE(m.title, '未知电影') AS movie_title, " +
            "COALESCE(s.start_time, NULL) AS start_time, " +
            "COALESCE(h.name, '未知影厅') AS hall_name " +
            "FROM orders o " +
            "LEFT JOIN user u ON o.user_id = u.id " +
            "LEFT JOIN schedule s ON o.schedule_id = s.id " +
            "LEFT JOIN movie m ON s.movie_id = m.id " +
            "LEFT JOIN hall h ON s.hall_id = h.id " +
            "ORDER BY o.create_time DESC")
    List<Map<String, Object>> findAllWithDetail();

    @Select("SELECT COUNT(*) FROM orders WHERE user_id = #{userId}")
    int countByUserId(@Param("userId") Integer userId);

    @Select("SELECT SUM(o.total_amount) FROM orders o " +
            "JOIN schedule s ON o.schedule_id = s.id " +
            "WHERE s.movie_id = #{movieId} AND o.status = 'paid'")
    BigDecimal sumBoxOfficeByMovie(@Param("movieId") Integer movieId);

    @Select("SELECT seat_info FROM orders WHERE schedule_id = #{scheduleId} AND status IN ('pending', 'paid')")
    List<String> findSeatsByScheduleId(@Param("scheduleId") Integer scheduleId);

    @Select("SELECT * FROM orders WHERE status = 'pending' AND expire_time < NOW()")
    List<Order> findExpiredOrders();
}