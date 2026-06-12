// import request from './request';

// export const movieApi = {
//     getList(params) {
//         return request.get('/movie/list', { params });
//     },
//     getDetail(id) {
//         return request.get(`/movie/${id}`);
//     },
//     getSchedules(movieId) {
//         return request.get(`/movie/${movieId}/schedules`);
//     }
// };

import request from './request';

export const movieApi = {
    // 获取全部电影（首页）
    getAllMovies(params) {
        return request.get('/movie/all', { params });
    },
    // 获取正在热映电影（近一年）
    getNowShowingMovies(params) {
        return request.get('/movie/nowShowing', { params });
    },
    getDetail(id) {
        return request.get(`/movie/${id}`);
    },
    getSchedules(movieId) {
        return request.get(`/movie/${movieId}/schedules`);
    }
};