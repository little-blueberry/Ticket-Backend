import axios from 'axios';
import { ElMessage } from 'element-plus';

// 直接使用后端地址
const request = axios.create({
    baseURL: 'http://localhost:8080',
    timeout: 60000,
    withCredentials: true
});

// 请求拦截器
request.interceptors.request.use(
    config => {
        console.log('📤 发送请求:', config.method.toUpperCase(), config.url);
        return config;
    },
    error => {
        console.error('请求错误:', error);
        return Promise.reject(error);
    }
);

// 响应拦截器
request.interceptors.response.use(
    response => {
        console.log('📥 收到响应:', response.config.url, response.data);
        return response.data;
    },
    error => {
        console.error('❌ 响应错误:', error);
        
        if (error.response) {
            const status = error.response.status;
            if (status === 401) {
                ElMessage.error('请先登录');
            } else if (status === 404) {
                ElMessage.error('接口不存在: ' + error.config?.url);
            } else {
                ElMessage.error('服务器错误: ' + status);
            }
        } else if (error.code === 'ECONNABORTED') {
            ElMessage.error('请求超时');
        } else if (error.message === 'Network Error') {
            ElMessage.error('网络错误，请确保后端服务已启动');
        } else {
            ElMessage.error(error.message || '未知错误');
        }
        
        return Promise.reject(error);
    }
);

export default request;