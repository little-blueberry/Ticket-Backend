package com.movie.ticket.service;

import com.movie.ticket.entity.Movie;
import com.movie.ticket.entity.Schedule;
import com.movie.ticket.mapper.MovieMapper;
import com.movie.ticket.mapper.ScheduleMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class MovieService {

    @Autowired
    private MovieMapper movieMapper;

    @Autowired
    private ScheduleMapper scheduleMapper;

    /**
     * 获取全部电影列表（分页）
     */
    public Map<String, Object> getAllMovies(String keyword, String type, Integer pageNum, Integer pageSize) {
        int offset = (pageNum - 1) * pageSize;
        List<Movie> list = movieMapper.findAllMovies(keyword, type, offset, pageSize);
        Integer total = movieMapper.countAllMovies(keyword, type);

        Map<String, Object> result = new HashMap<>();
        result.put("list", list);
        result.put("total", total);
        result.put("pageNum", pageNum);
        result.put("pageSize", pageSize);
        return result;
    }

    /**
     * 获取近一年上映的电影列表（正在热映）
     */
    public Map<String, Object> getNowShowingMovies(String keyword, String type, Integer pageNum, Integer pageSize) {
        int offset = (pageNum - 1) * pageSize;
        List<Movie> list = movieMapper.findNowShowingMovies(keyword, type, offset, pageSize);
        Integer total = movieMapper.countNowShowingMovies(keyword, type);

        Map<String, Object> result = new HashMap<>();
        result.put("list", list);
        result.put("total", total);
        result.put("pageNum", pageNum);
        result.put("pageSize", pageSize);
        return result;
    }

    /**
     * 根据ID获取电影详情
     */
    public Movie getById(Integer id) {
        return movieMapper.findById(id);
    }

    /**
     * 获取某部电影的所有场次
     */
    public List<Schedule> getSchedulesByMovie(Integer movieId) {
        return scheduleMapper.findByMovieId(movieId);
    }

    /**
     * 添加电影
     */
    public boolean addMovie(Movie movie) {
        if (movie.getTitle() == null || movie.getTitle().trim().isEmpty()) {
            return false;
        }
        if (movie.getStatus() == null) {
            movie.setStatus(1);
        }
        if (movie.getRating() == null) {
            movie.setRating(BigDecimal.valueOf(0.0));
        }
        return movieMapper.insert(movie) > 0;
    }

    /**
     * 更新电影信息
     */
    public boolean updateMovie(Movie movie) {
        if (movie.getId() == null) {
            return false;
        }
        return movieMapper.update(movie) > 0;
    }

    /**
     * 删除电影
     */
    public boolean deleteMovie(Integer id) {
        return movieMapper.deleteById(id) > 0;
    }

    /**
     * 管理员获取所有电影列表（不分页）
     */
    public List<Movie> getAllMoviesForAdmin() {
        return movieMapper.findAllMoviesForAdmin();
    }
}