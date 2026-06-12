package com.movie.ticket.mapper;

import com.movie.ticket.entity.Hall;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface HallMapper {

    /**
     * 获取所有影厅
     */
    @Select("SELECT * FROM hall ORDER BY id")
    List<Hall> findAll();

    /**
     * 根据ID获取影厅
     */
    @Select("SELECT * FROM hall WHERE id = #{id}")
    Hall findById(@Param("id") Integer id);

    /**
     * 添加影厅
     */
    @Insert("INSERT INTO hall (name, capacity, row_count, col_count) VALUES (#{name}, #{capacity}, #{rowCount}, #{colCount})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Hall hall);

    /**
     * 更新影厅
     */
    @Update("UPDATE hall SET name = #{name}, capacity = #{capacity}, row_count = #{rowCount}, col_count = #{colCount} WHERE id = #{id}")
    int update(Hall hall);

    /**
     * 删除影厅
     */
    @Delete("DELETE FROM hall WHERE id = #{id}")
    int deleteById(@Param("id") Integer id);
}