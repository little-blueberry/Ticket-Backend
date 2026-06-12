import { createRouter, createWebHistory } from 'vue-router'

const routes = [
    // 公共页面
    { path: '/', name: 'Home', component: () => import('../views/Home.vue') },
    { path: '/movies', name: 'NowShowing', component: () => import('../views/NowShowing.vue') },
    { path: '/movie/:id', name: 'MovieDetail', component: () => import('../views/MovieDetail.vue') },
    { path: '/myorders', name: 'MyOrders', component: () => import('../views/MyOrders.vue') },
    
    // 管理员页面
    { path: '/admin/movies', name: 'MovieManage', component: () => import('../views/admin/MovieManage.vue') },
    { path: '/admin/schedules', name: 'ScheduleManage', component: () => import('../views/admin/ScheduleManage.vue') },
    { path: '/admin/orders', name: 'OrderManage', component: () => import('../views/admin/OrderManage.vue') }
]

const router = createRouter({
    history: createWebHistory(),
    routes
})

export default router