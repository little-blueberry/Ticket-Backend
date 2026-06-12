package com.movie.ticket.mapper;

import com.movie.ticket.entity.Comment;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Map;

@Mapper
public interface CommentMapper {

    /**
     * 添加评论
     */
    @Insert("INSERT INTO comment (user_id, movie_id, content, rating) VALUES (#{userId}, #{movieId}, #{content}, #{rating})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Comment comment);

    /**
     * 获取某部电影的评论列表
     */
    @Select("SELECT c.*, u.username, u.real_name " +
            "FROM comment c " +
            "JOIN user u ON c.user_id = u.id " +
            "WHERE c.movie_id = #{movieId} " +
            "ORDER BY c.create_time DESC " +
            "LIMIT #{offset}, #{pageSize}")
    List<Map<String, Object>> findByMovieId(@Param("movieId") Integer movieId,
                                            @Param("offset") Integer offset,
                                            @Param("pageSize") Integer pageSize);

    /**
     * 获取电影评论总数
     */
    @Select("SELECT COUNT(*) FROM comment WHERE movie_id = #{movieId}")
    Integer countByMovieId(@Param("movieId") Integer movieId);

    /**
     * 调用存储函数获取电影平均评分
     */
    @Select("SELECT get_movie_avg_rating(#{movieId})")
    Double getMovieAvgRating(@Param("movieId") Integer movieId);

    /**
     * 删除评论
     */
    @Delete("DELETE FROM comment WHERE id = #{id}")
    int deleteById(@Param("id") Integer id);
}