// mapper/UserMapper.java
package com.movie.ticket.mapper;

import com.movie.ticket.entity.User;
import org.apache.ibatis.annotations.*;

import java.math.BigDecimal;
import java.util.List;

@Mapper
public interface UserMapper {

    @Select("SELECT * FROM user WHERE username = #{username} AND password = #{password} AND status = 1")
    User findByUsernameAndPassword(@Param("username") String username, @Param("password") String password);

    @Select("SELECT * FROM user WHERE username = #{username}")
    User findByUsername(@Param("username") String username);

    @Select("SELECT * FROM user WHERE id = #{id}")
    User findById(@Param("id") Integer id);

    @Insert("INSERT INTO user (username, password, real_name, phone, balance, points, role, status) " +
            "VALUES (#{username}, #{password}, #{realName}, #{phone}, #{balance}, #{points}, #{role}, #{status})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(User user);

    @Update("UPDATE user SET balance = #{balance} WHERE id = #{id}")
    int updateBalance(@Param("id") Integer id, @Param("balance") BigDecimal balance);








    // 在 UserMapper.java 中补充以下方法：（新添加）

    /**
     * 获取所有用户（管理员用）
     */
    @Select("SELECT * FROM user ORDER BY id")
    List<User> findAll();

    /**
     * 修改密码
     */
    @Update("UPDATE user SET password = #{newPassword} WHERE id = #{id} AND password = #{oldPassword}")
    int updatePassword(@Param("id") Integer id,
                       @Param("oldPassword") String oldPassword,
                       @Param("newPassword") String newPassword);

    /**
     * 更新用户状态（禁用/启用）
     */
    @Update("UPDATE user SET status = #{status} WHERE id = #{id}")
    int updateStatus(@Param("id") Integer id, @Param("status") Integer status);

    /**
     * 更新用户积分
     */
    @Update("UPDATE user SET points = #{points} WHERE id = #{id}")
    int updatePoints(@Param("id") Integer id, @Param("points") Integer points);


}
