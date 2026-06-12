package com.movie.ticket.controller;

import com.movie.ticket.dto.Result;
import com.movie.ticket.entity.Movie;
import com.movie.ticket.entity.Schedule;
import com.movie.ticket.service.MovieService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/movie")
public class MovieController {

    @Autowired
    private MovieService movieService;

    /**
     * 获取全部电影列表（首页用）
     */
    @GetMapping("/all")
    public Result<Map<String, Object>> getAllMovies(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String type,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {

        Map<String, Object> movies = movieService.getAllMovies(keyword, type, pageNum, pageSize);
        return Result.success(movies);
    }

    /**
     * 获取正在热映的电影列表（近一年上映）
     */
    @GetMapping("/nowShowing")
    public Result<Map<String, Object>> getNowShowingMovies(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String type,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {

        Map<String, Object> movies = movieService.getNowShowingMovies(keyword, type, pageNum, pageSize);
        return Result.success(movies);
    }

    /**
     * 管理员：获取所有电影列表（不分页，用于管理）
     */
    @GetMapping("/admin/list")
    public Result<List<Movie>> getAdminMovieList(HttpSession session) {
        String role = (String) session.getAttribute("userRole");
        if (!"admin".equals(role)) {
            return Result.error(403, "无权限操作");
        }
        List<Movie> movies = movieService.getAllMoviesForAdmin();
        return Result.success(movies);
    }

    /**
     * 获取电影详情
     */
    @GetMapping("/{id}")
    public Result<Movie> getMovieDetail(@PathVariable Integer id) {
        Movie movie = movieService.getById(id);
        if (movie == null) {
            return Result.error("电影不存在");
        }
        return Result.success(movie);
    }

    /**
     * 获取某部电影的场次
     */
    @GetMapping("/{movieId}/schedules")
    public Result<List<Schedule>> getSchedules(@PathVariable Integer movieId) {
        List<Schedule> schedules = movieService.getSchedulesByMovie(movieId);
        return Result.success(schedules);
    }

    /**
     * 管理员：添加电影
     */
    @PostMapping("/admin/add")
    public Result<String> addMovie(@RequestBody Movie movie, HttpSession session) {
        String role = (String) session.getAttribute("userRole");
        if (!"admin".equals(role)) {
            return Result.error(403, "无权限操作");
        }
        boolean success = movieService.addMovie(movie);
        return success ? Result.success("添加成功") : Result.error("添加失败");
    }

    /**
     * 管理员：更新电影
     */
    @PutMapping("/admin/update")
    public Result<String> updateMovie(@RequestBody Movie movie, HttpSession session) {
        String role = (String) session.getAttribute("userRole");
        if (!"admin".equals(role)) {
            return Result.error(403, "无权限操作");
        }
        boolean success = movieService.updateMovie(movie);
        return success ? Result.success("更新成功") : Result.error("更新失败");
    }

    /**
     * 管理员：删除电影
     */
    @DeleteMapping("/admin/{id}")
    public Result<String> deleteMovie(@PathVariable Integer id, HttpSession session) {
        String role = (String) session.getAttribute("userRole");
        if (!"admin".equals(role)) {
            return Result.error(403, "无权限操作");
        }
        boolean success = movieService.deleteMovie(id);
        return success ? Result.success("删除成功") : Result.error("删除失败");
    }
}