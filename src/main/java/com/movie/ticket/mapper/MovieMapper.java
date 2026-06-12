package com.movie.ticket.mapper;

import com.movie.ticket.entity.Movie;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface MovieMapper {

    /**
     * 分页查询全部电影
     */
    @Select("<script>" +
            "SELECT * FROM movie WHERE status = 1 " +
            "<if test='keyword != null and keyword != \"\"'>" +
            "AND title LIKE CONCAT('%', #{keyword}, '%') " +
            "</if>" +
            "<if test='type != null and type != \"\"'>" +
            "AND type = #{type} " +
            "</if>" +
            "ORDER BY release_date DESC " +
            "LIMIT #{offset}, #{pageSize}" +
            "</script>")
    List<Movie> findAllMovies(@Param("keyword") String keyword,
                              @Param("type") String type,
                              @Param("offset") Integer offset,
                              @Param("pageSize") Integer pageSize);

    /**
     * 统计全部电影总数
     */
    @Select("<script>" +
            "SELECT COUNT(*) FROM movie WHERE status = 1 " +
            "<if test='keyword != null and keyword != \"\"'>" +
            "AND title LIKE CONCAT('%', #{keyword}, '%') " +
            "</if>" +
            "<if test='type != null and type != \"\"'>" +
            "AND type = #{type} " +
            "</if>" +
            "</script>")
    Integer countAllMovies(@Param("keyword") String keyword,
                           @Param("type") String type);

    /**
     * 分页查询近一年上映的电影（正在热映）
     */
    @Select("<script>" +
            "SELECT * FROM movie WHERE status = 1 " +
            "AND release_date >= DATE_SUB(CURDATE(), INTERVAL 1 YEAR) " +
            "<if test='keyword != null and keyword != \"\"'>" +
            "AND title LIKE CONCAT('%', #{keyword}, '%') " +
            "</if>" +
            "<if test='type != null and type != \"\"'>" +
            "AND type = #{type} " +
            "</if>" +
            "ORDER BY release_date DESC " +
            "LIMIT #{offset}, #{pageSize}" +
            "</script>")
    List<Movie> findNowShowingMovies(@Param("keyword") String keyword,
                                     @Param("type") String type,
                                     @Param("offset") Integer offset,
                                     @Param("pageSize") Integer pageSize);

    /**
     * 统计近一年上映电影总数
     */
    @Select("<script>" +
            "SELECT COUNT(*) FROM movie WHERE status = 1 " +
            "AND release_date >= DATE_SUB(CURDATE(), INTERVAL 1 YEAR) " +
            "<if test='keyword != null and keyword != \"\"'>" +
            "AND title LIKE CONCAT('%', #{keyword}, '%') " +
            "</if>" +
            "<if test='type != null and type != \"\"'>" +
            "AND type = #{type} " +
            "</if>" +
            "</script>")
    Integer countNowShowingMovies(@Param("keyword") String keyword,
                                  @Param("type") String type);

    /**
     * 根据ID获取电影详情
     */
    @Select("SELECT * FROM movie WHERE id = #{id}")
    Movie findById(@Param("id") Integer id);

    /**
     * 添加电影
     */
    @Insert("INSERT INTO movie (title, poster, director, actors, type, duration, description, rating, release_date, status) " +
            "VALUES (#{title}, #{poster}, #{director}, #{actors}, #{type}, #{duration}, #{description}, #{rating}, #{releaseDate}, #{status})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Movie movie);

    /**
     * 更新电影信息
     */
    @Update("UPDATE movie SET title = #{title}, poster = #{poster}, director = #{director}, " +
            "actors = #{actors}, type = #{type}, duration = #{duration}, description = #{description}, " +
            "rating = #{rating}, release_date = #{releaseDate}, status = #{status} WHERE id = #{id}")
    int update(Movie movie);

    /**
     * 删除电影（软删除）
     */
    @Update("UPDATE movie SET status = 0 WHERE id = #{id}")
    int deleteById(@Param("id") Integer id);

    /**
     * 管理员获取所有电影列表（不分页，包括已下架的）
     */
    @Select("SELECT * FROM movie ORDER BY id DESC")
    @Results(id = "movieResultMap", value = {
            @Result(property = "id", column = "id"),
            @Result(property = "title", column = "title"),
            @Result(property = "poster", column = "poster"),
            @Result(property = "director", column = "director"),
            @Result(property = "actors", column = "actors"),
            @Result(property = "type", column = "type"),
            @Result(property = "duration", column = "duration"),
            @Result(property = "description", column = "description"),
            @Result(property = "rating", column = "rating"),
            @Result(property = "releaseDate", column = "release_date"),
            @Result(property = "status", column = "status"),
            @Result(property = "createTime", column = "create_time"),
            @Result(property = "updateTime", column = "update_time")
    })
    List<Movie> findAllMoviesForAdmin();
}