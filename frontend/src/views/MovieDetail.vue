<!-- 
<template>
  <div class="movie-detail">
    <div class="container">
      <div v-if="movie" class="detail-header">
        <div class="poster">
          <img :src="movie.poster || 'https://via.placeholder.com/200x280?text=No+Poster'" :alt="movie.title">
        </div>
        <div class="info">
          <h1>{{ movie.title }}</h1>
          <p>评分：{{ movie.rating || '暂无' }}分</p>
          <p>导演：{{ movie.director || '未知' }}</p>
          <p>主演：{{ movie.actors || '未知' }}</p>
          <p>类型：{{ movie.type || '未知' }}</p>
          <p>时长：{{ movie.duration || 0 }}分钟</p>
          <p>上映日期：{{ movie.releaseDate || '未知' }}</p>
          <p>简介：{{ movie.description || '暂无简介' }}</p>
        </div>
      </div>
      <div v-else class="loading">
        <el-skeleton :rows="10" animated />
      </div>

      <div class="schedules" v-if="schedules.length > 0">
        <h2>选择场次</h2>
        <div class="schedule-list">
          <div class="schedule-item" v-for="schedule in schedules" :key="schedule.id" @click="selectSchedule(schedule)">
            <div class="time">{{ formatDateTime(schedule.startTime) }}</div>
            <div class="price">¥{{ schedule.price }}</div>
            <div class="remain">余票{{ schedule.remainSeats }}</div>
          </div>
        </div>
      </div>
      <div v-else-if="!loading" class="no-schedules">
        <p>暂无场次</p>
      </div>

      <div class="order-info" v-if="selectedSchedule">
        <p>已选场次：{{ formatDateTime(selectedSchedule.startTime) }}</p>
        <p>票价：¥{{ selectedSchedule.price }}</p>
        <el-button type="primary" @click="createOrder">立即购票</el-button>
      </div>
    </div>
  </div>
</template>

<script>
import { movieApi } from '../api/movie';
import { orderApi } from '../api/order';
import { ElMessage, ElMessageBox } from 'element-plus';

export default {
  name: 'MovieDetail',
  data() {
    return {
      loading: false,
      movie: null,
      schedules: [],
      selectedSchedule: null
    };
  },
  created() {
    this.loadMovieDetail();
    this.loadSchedules();
  },
  methods: {
    formatDateTime(datetime) {
      if (!datetime) return '待定';
      return datetime.replace('T', ' ');
    },
    async loadMovieDetail() {
      const id = this.$route.params.id;
      console.log('加载电影详情，ID:', id);
      
      if (!id || id === 'undefined') {
        ElMessage.error('无效的电影ID');
        this.$router.push('/movies');
        return;
      }
      
      this.loading = true;
      try {
        const res = await movieApi.getDetail(id);
        console.log('电影详情响应:', res);
        if (res.code === 200) {
          this.movie = res.data;
        } else {
          ElMessage.error(res.message || '加载失败');
        }
      } catch (error) {
        console.error('加载电影详情失败:', error);
        ElMessage.error('加载失败');
      } finally {
        this.loading = false;
      }
    },
    async loadSchedules() {
      const id = this.$route.params.id;
      if (!id || id === 'undefined') return;
      
      try {
        const res = await movieApi.getSchedules(id);
        console.log('场次响应:', res);
        if (res.code === 200) {
          this.schedules = res.data || [];
        }
      } catch (error) {
        console.error('加载场次失败:', error);
      }
    },
    selectSchedule(schedule) {
      this.selectedSchedule = schedule;
      ElMessage.success(`已选择 ${this.formatDateTime(schedule.startTime)} 场次`);
    },
    async createOrder() {
      if (!this.selectedSchedule) {
        ElMessage.warning('请先选择场次');
        return;
      }
      
      try {
        const res = await orderApi.createOrder({
          scheduleId: this.selectedSchedule.id,
          seatInfo: '1排1座',
          quantity: 1
        });
        
        console.log('创建订单响应:', res);
        
        if (res.code === 200) {
          ElMessageBox.confirm(`订单创建成功，总计 ¥${res.data.totalAmount}，是否立即支付？`, '提示', {
            confirmButtonText: '去支付',
            cancelButtonText: '稍后支付'
          }).then(async () => {
            const payRes = await orderApi.payOrder(res.data.id);
            if (payRes.code === 200) {
              ElMessage.success('支付成功！');
              this.$router.push('/myorders');
            } else {
              ElMessage.error(payRes.message);
            }
          }).catch(() => {
            this.$router.push('/myorders');
          });
        } else {
          ElMessage.error(res.message);
        }
      } catch (error) {
        console.error('创建订单失败:', error);
        ElMessage.error('创建订单失败');
      }
    }
  }
};
</script>

<style scoped>
.movie-detail { padding: 30px 0; min-height: calc(100vh - 70px); }
.container { max-width: 1200px; margin: 0 auto; padding: 0 20px; }
.detail-header { display: flex; gap: 40px; background: white; padding: 30px; border-radius: 10px; margin-bottom: 20px; }
.poster img { width: 200px; height: 280px; object-fit: cover; border-radius: 8px; }
.info { flex: 1; }
.info h1 { margin-bottom: 15px; }
.info p { margin: 8px 0; color: #666; }
.schedules { background: white; padding: 20px; border-radius: 10px; }
.schedule-list { display: flex; flex-wrap: wrap; gap: 15px; margin-top: 15px; }
.schedule-item { border: 1px solid #ddd; padding: 10px 20px; border-radius: 8px; cursor: pointer; transition: all 0.3s; }
.schedule-item:hover { border-color: #ff6b6b; background: #fff0f0; }
.price { color: #ff6b6b; font-weight: bold; }
.order-info { background: white; padding: 20px; margin-top: 20px; border-radius: 10px; display: flex; justify-content: space-between; align-items: center; }
.loading, .no-schedules { text-align: center; padding: 40px; color: #999; }
</style> -->


<template>
  <div class="movie-detail">
    <div class="container">
      <div v-if="movie" class="detail-header">
        <div class="poster">
          <img 
            :src="getPosterUrl(movie.poster)" 
            :alt="movie.title"
            @error="handleImageError"
          >
        </div>
        <div class="info">
          <h1>{{ movie.title }}</h1>
          <p>评分：{{ movie.rating || '暂无' }}分</p>
          <p>导演：{{ movie.director || '未知' }}</p>
          <p>主演：{{ movie.actors || '未知' }}</p>
          <p>类型：{{ movie.type || '未知' }}</p>
          <p>时长：{{ movie.duration || 0 }}分钟</p>
          <p>上映日期：{{ movie.releaseDate || '未知' }}</p>
          <p>简介：{{ movie.description || '暂无简介' }}</p>
        </div>
      </div>
      <div v-else class="loading">
        <el-skeleton :rows="10" animated />
      </div>

      <div class="schedules" v-if="schedules.length > 0">
        <h2>选择场次</h2>
        <div class="schedule-list">
          <div class="schedule-item" v-for="schedule in schedules" :key="schedule.id" @click="selectSchedule(schedule)">
            <div class="time">{{ formatDateTime(schedule.startTime) }}</div>
            <div class="hall">{{ schedule.hallName || '未知影厅' }}</div>
            <div class="price">¥{{ schedule.price }}</div>
            <div class="remain">余票{{ schedule.remainSeats }}</div>
          </div>
        </div>
      </div>
      <div v-else-if="!loading" class="no-schedules">
        <p>暂无场次</p>
      </div>

      <div class="order-info" v-if="selectedSchedule">
        <div class="selected-info">
          <p>已选场次：{{ formatDateTime(selectedSchedule.startTime) }}</p>
          <p>票价：¥{{ selectedSchedule.price }}</p>
        </div>
        <el-button type="primary" @click="createOrder">立即购票</el-button>
      </div>
    </div>
  </div>
</template>

<script>
import { movieApi } from '../api/movie';
import { orderApi } from '../api/order';
import { ElMessage, ElMessageBox } from 'element-plus';

export default {
  name: 'MovieDetail',
  data() {
    return {
      loading: false,
      movie: null,
      schedules: [],
      selectedSchedule: null
    };
  },
  created() {
    this.loadMovieDetail();
    this.loadSchedules();
  },
  methods: {
    // 获取图片URL
    getPosterUrl(poster) {
      if (!poster) {
        return `https://picsum.photos/200/280?random=${Date.now()}`;
      }
      if (poster.startsWith('http')) {
        return poster;
      }
      // 使用在线随机图片
      return `https://picsum.photos/200/280?random=${Math.random()}`;
    },
    handleImageError(event) {
      event.target.src = `https://picsum.photos/200/280?random=${Date.now()}`;
    },
    formatDateTime(datetime) {
      if (!datetime) return '待定';
      return datetime.replace('T', ' ');
    },
    async loadMovieDetail() {
      const id = this.$route.params.id;
      console.log('加载电影详情，ID:', id);
      
      if (!id || id === 'undefined') {
        ElMessage.error('无效的电影ID');
        this.$router.push('/movies');
        return;
      }
      
      this.loading = true;
      try {
        const res = await movieApi.getDetail(id);
        console.log('电影详情响应:', res);
        if (res.code === 200) {
          this.movie = res.data;
        } else {
          ElMessage.error(res.message || '加载失败');
        }
      } catch (error) {
        console.error('加载电影详情失败:', error);
        ElMessage.error('加载失败');
      } finally {
        this.loading = false;
      }
    },
    async loadSchedules() {
      const id = this.$route.params.id;
      if (!id || id === 'undefined') return;
      
      try {
        const res = await movieApi.getSchedules(id);
        console.log('场次响应:', res);
        if (res.code === 200) {
          this.schedules = res.data || [];
        }
      } catch (error) {
        console.error('加载场次失败:', error);
      }
    },
    selectSchedule(schedule) {
      this.selectedSchedule = schedule;
      ElMessage.success(`已选择 ${this.formatDateTime(schedule.startTime)} 场次`);
    },
    async createOrder() {
      if (!this.selectedSchedule) {
        ElMessage.warning('请先选择场次');
        return;
      }
      
      try {
        const res = await orderApi.createOrder({
          scheduleId: this.selectedSchedule.id,
          seatInfo: '1排1座',
          quantity: 1
        });
        
        console.log('创建订单响应:', res);
        
        if (res.code === 200) {
          ElMessageBox.confirm(`订单创建成功，总计 ¥${res.data.totalAmount}，是否立即支付？`, '提示', {
            confirmButtonText: '去支付',
            cancelButtonText: '稍后支付'
          }).then(async () => {
            const payRes = await orderApi.payOrder(res.data.id);
            if (payRes.code === 200) {
              ElMessage.success('支付成功！');
              this.$router.push('/myorders');
            } else {
              ElMessage.error(payRes.message);
            }
          }).catch(() => {
            this.$router.push('/myorders');
          });
        } else {
          ElMessage.error(res.message);
        }
      } catch (error) {
        console.error('创建订单失败:', error);
        ElMessage.error('创建订单失败');
      }
    }
  }
};
</script>

<style scoped>
.movie-detail { padding: 30px 0; min-height: calc(100vh - 70px); }
.container { max-width: 1200px; margin: 0 auto; padding: 0 20px; }
.detail-header { display: flex; gap: 40px; background: white; padding: 30px; border-radius: 10px; margin-bottom: 20px; }
.poster img { width: 200px; height: 280px; object-fit: cover; border-radius: 8px; }
.info { flex: 1; }
.info h1 { margin-bottom: 15px; }
.info p { margin: 8px 0; color: #666; }
.schedules { background: white; padding: 20px; border-radius: 10px; }
.schedule-list { display: flex; flex-wrap: wrap; gap: 15px; margin-top: 15px; }
.schedule-item { border: 1px solid #ddd; padding: 10px 20px; border-radius: 8px; cursor: pointer; transition: all 0.3s; }
.schedule-item:hover { border-color: #ff6b6b; background: #fff0f0; }
.price { color: #ff6b6b; font-weight: bold; }
.order-info { background: white; padding: 20px; margin-top: 20px; border-radius: 10px; display: flex; justify-content: space-between; align-items: center; }
.loading, .no-schedules { text-align: center; padding: 40px; color: #999; }
</style>