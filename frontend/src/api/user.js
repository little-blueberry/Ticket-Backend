// import request from './request';

// export const userApi = {
//     login(data) {
//         return request({
//             method: 'post',
//             url: '/user/login',
//             data: data
//         });
//     },
//     register(data) {
//         return request({
//             method: 'post',
//             url: '/user/register',
//             data: data
//         });
//     },
//     getInfo() {
//         return request({
//             method: 'get',
//             url: '/user/info'
//         });
//     },
//     logout() {
//         return request({
//             method: 'post',
//             url: '/user/logout'
//         });
//     }
// };

import request from './request';

export const userApi = {
    login(data) {
        return request.post('/user/login', data);
    },
    register(data) {
        return request.post('/user/register', data);
    },
    getInfo() {
        return request.get('/user/info');
    },
    logout() {
        return request.post('/user/logout');
    }
};