<template>
  <div class="admin-page">
    <div class="container">
      <h1>📋 订单管理</h1>
      
      <el-table :data="orderList" border style="margin-top: 20px; width: 100%">
        <el-table-column prop="id" label="订单ID" width="60"></el-table-column>
        <el-table-column prop="order_no" label="订单号" width="200"></el-table-column>
        <el-table-column prop="username" label="用户名" width="100"></el-table-column>
        <el-table-column prop="movie_title" label="电影名称" width="150"></el-table-column>
        <el-table-column prop="seat_info" label="座位" width="100"></el-table-column>
        <el-table-column prop="quantity" label="数量" width="60"></el-table-column>
        <el-table-column prop="total_amount" label="金额" width="80">
          <template #default="{ row }">¥{{ row.total_amount }}</template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">{{ getStatusText(row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="create_time" label="创建时间" width="170"></el-table-column>
      </el-table>
      
      <div v-if="orderList.length === 0 && !loading" class="empty">
        <el-empty description="暂无订单数据"></el-empty>
      </div>
    </div>
  </div>
</template>

<script>
import { orderApi } from '../../api/order';
import { ElMessage } from 'element-plus';

export default {
  name: 'OrderManage',
  data() {
    return {
      loading: false,
      orderList: []
    };
  },
  created() {
    this.loadOrders();
  },
  methods: {
    async loadOrders() {
      this.loading = true;
      try {
        const res = await orderApi.getAllOrders();
        console.log('订单列表响应:', res);
        if (res.code === 200) {
          this.orderList = res.data || [];
          console.log('解析后订单列表:', this.orderList);
        } else {
          this.orderList = [];
          ElMessage.error(res.message || '加载失败');
        }
      } catch (error) {
        console.error('加载订单失败:', error);
        ElMessage.error('加载订单失败');
        this.orderList = [];
      } finally {
        this.loading = false;
      }
    },
    getStatusText(status) {
      const map = { pending: '待支付', paid: '已支付', cancelled: '已取消', expired: '已过期' };
      return map[status] || status;
    },
    getStatusType(status) {
      const map = { pending: 'warning', paid: 'success', cancelled: 'info', expired: 'danger' };
      return map[status] || 'info';
    }
  }
};
</script>

<style scoped>
.admin-page { padding: 30px 0; }
.container { max-width: 1200px; margin: 0 auto; padding: 0 20px; }
.empty { text-align: center; padding: 60px 0; }
</style>