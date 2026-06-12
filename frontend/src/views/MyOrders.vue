<template>
  <div class="my-orders">
    <div class="container">
      <h1>我的订单</h1>
      
      <div v-if="loading" class="loading-container">
        <el-skeleton :rows="5" animated />
      </div>
      
      <div v-else-if="orderList.length === 0" class="empty">
        <el-empty description="暂无订单">
          <el-button type="primary" @click="$router.push('/movies')">去购票</el-button>
        </el-empty>
      </div>
      
      <div v-else class="order-list">
        <div class="order-card" v-for="order in orderList" :key="order.id">
          <div class="order-header">
            <span class="order-no">订单号：{{ order.orderNo || '未知' }}</span>
            <span :class="'status-' + order.status">
              {{ getStatusText(order.status) }}
            </span>
          </div>
          <div class="order-body">
            <div class="order-poster">
              <img 
                :src="order.poster || 'https://picsum.photos/80/120?random=' + order.id" 
                :alt="order.movie_title"
                @error="handleImageError"
              >
            </div>
            <div class="order-info">
              <h3>{{ order.movie_title || '未知电影' }}</h3>
              <p>场次：{{ formatDateTime(order.start_time) }}</p>
              <p>影厅：{{ order.hall_name || '未知' }}</p>
              <p>座位：{{ order.seat_info || '未知' }}</p>
              <p>数量：{{ order.quantity || 1 }}张</p>
              <p class="price">总价：¥{{ order.total_amount || 0 }}</p>
            </div>
          </div>
          <div class="order-footer" v-if="order.status === 'pending'">
            <el-button size="small" type="danger" @click="cancelOrder(order.id)">取消订单</el-button>
            <el-button size="small" type="primary" @click="payOrder(order.id)">去支付</el-button>
          </div>
          <div class="order-footer" v-else-if="order.status === 'paid'">
            <el-tag type="success">已支付，请准时观影</el-tag>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { orderApi } from '../api/order';
import { ElMessage, ElMessageBox } from 'element-plus';

export default {
  name: 'MyOrders',
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
    handleImageError(event) {
      event.target.src = `https://picsum.photos/80/120?random=${Date.now()}`;
    },
    formatDateTime(datetime) {
      if (!datetime) return '待定';
      return datetime.replace('T', ' ');
    },
    getStatusText(status) {
      const map = {
        pending: '待支付',
        paid: '已支付',
        cancelled: '已取消',
        expired: '已过期'
      };
      return map[status] || status;
    },
    async loadOrders() {
      this.loading = true;
      try {
        const res = await orderApi.getMyOrders();
        console.log('订单列表响应:', res);
        if (res.code === 200) {
          this.orderList = res.data || [];
          console.log('解析后的订单:', this.orderList);
        } else {
          this.orderList = [];
        }
      } catch (error) {
        console.error('加载订单失败:', error);
        ElMessage.error('加载订单失败');
        this.orderList = [];
      } finally {
        this.loading = false;
      }
    },
    async payOrder(orderId) {
      try {
        const res = await orderApi.payOrder(orderId);
        if (res.code === 200) {
          ElMessage.success('支付成功');
          this.loadOrders();
        } else {
          ElMessage.error(res.message);
        }
      } catch (error) {
        console.error('支付失败:', error);
        ElMessage.error('支付失败');
      }
    },
    async cancelOrder(orderId) {
      ElMessageBox.confirm('确定取消该订单吗？', '提示', { type: 'warning' }).then(async () => {
        try {
          const res = await orderApi.cancelOrder(orderId);
          if (res.code === 200) {
            ElMessage.success('已取消');
            this.loadOrders();
          } else {
            ElMessage.error(res.message);
          }
        } catch (error) {
          console.error('取消失败:', error);
          ElMessage.error('取消失败');
        }
      });
    }
  }
};
</script>

<style scoped>
.my-orders { padding: 30px 0; min-height: calc(100vh - 70px); }
.container { max-width: 1000px; margin: 0 auto; padding: 0 20px; }
h1 { margin-bottom: 30px; color: #333; }
.order-list { display: flex; flex-direction: column; gap: 20px; }
.order-card { background: white; border-radius: 10px; overflow: hidden; box-shadow: 0 2px 8px rgba(0,0,0,0.1); }
.order-header { display: flex; justify-content: space-between; padding: 15px 20px; background: #f8f9fa; border-bottom: 1px solid #eee; }
.order-no { color: #666; font-size: 14px; }
.order-body { display: flex; gap: 20px; padding: 20px; }
.order-poster img { width: 80px; height: 120px; object-fit: cover; border-radius: 6px; }
.order-info { flex: 1; }
.order-info h3 { margin-bottom: 10px; }
.order-info p { margin: 5px 0; color: #666; font-size: 14px; }
.price { color: #ff6b6b; font-size: 18px; font-weight: bold; }
.order-footer { padding: 15px 20px; border-top: 1px solid #eee; display: flex; justify-content: flex-end; gap: 15px; }
.status-pending { color: #e6a23c; }
.status-paid { color: #67c23a; }
.status-cancelled { color: #909399; }
.status-expired { color: #f56c6c; }
.loading-container { padding: 40px; }
.empty { text-align: center; padding: 60px 0; }
</style>