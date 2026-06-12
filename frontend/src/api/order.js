import request from './request';

export const orderApi = {
    createOrder(data) {
        return request.post('/order/create', data);
    },
    payOrder(orderId) {
        return request.post(`/order/pay/${orderId}`);
    },
    cancelOrder(orderId) {
        return request.post(`/order/cancel/${orderId}`);
    },
    getMyOrders() {
        return request.get('/order/myOrders');
    },
    // 管理员：获取所有订单
    getAllOrders() {
        return request.get('/order/admin/all');
    }
};