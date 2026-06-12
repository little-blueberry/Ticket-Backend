<template>
  <div class="admin-page">
    <div class="container">
      <h1>📅 场次管理</h1>
      
      <el-card class="add-card">
        <template #header>添加场次</template>
        <el-form :model="newSchedule" label-width="80px" inline>
          <el-form-item label="电影">
            <el-select v-model="newSchedule.movieId" placeholder="请选择电影" clearable style="width: 200px">
              <el-option 
                v-for="m in movies" 
                :key="m.id" 
                :label="m.title" 
                :value="m.id">
              </el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="影厅">
            <el-select v-model="newSchedule.hallId" placeholder="请选择影厅" clearable style="width: 120px">
              <el-option label="1号厅" :value="1"></el-option>
              <el-option label="2号厅" :value="2"></el-option>
              <el-option label="3号厅" :value="3"></el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="开始时间">
            <el-date-picker 
              v-model="newSchedule.startTime" 
              type="datetime" 
              placeholder="选择日期时间"
              value-format="YYYY-MM-DD HH:mm:ss"
              style="width: 200px">
            </el-date-picker>
          </el-form-item>
          <el-form-item label="票价">
            <el-input-number v-model="newSchedule.price" :min="0" :step="5"></el-input-number>
          </el-form-item>
          <el-form-item label="余票">
            <el-input-number v-model="newSchedule.remainSeats" :min="0" :step="10"></el-input-number>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="addSchedule">添加场次</el-button>
          </el-form-item>
        </el-form>
      </el-card>

      <el-table :data="scheduleList" border style="margin-top: 20px">
        <el-table-column prop="id" label="ID" width="60"></el-table-column>
        <el-table-column prop="movieTitle" label="电影名称" width="150"></el-table-column>
        <el-table-column prop="hallName" label="影厅" width="80"></el-table-column>
        <el-table-column prop="startTime" label="开始时间" width="180"></el-table-column>
        <el-table-column prop="price" label="票价" width="80">
          <template #default="{ row }">¥{{ row.price }}</template>
        </el-table-column>
        <el-table-column prop="remainSeats" label="余票" width="80"></el-table-column>
        <el-table-column label="操作" width="100">
          <template #default="{ row }">
            <el-button size="small" type="danger" @click="deleteSchedule(row.id)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>
  </div>
</template>

<script>
import { ElMessage, ElMessageBox } from 'element-plus';

export default {
  name: 'ScheduleManage',
  data() {
    return {
      movies: [],
      scheduleList: [],
      newSchedule: {
        movieId: null,
        hallId: null,
        startTime: '',
        price: 45,
        remainSeats: 80,
        status: 1
      }
    };
  },
  created() {
    this.loadMovies();
    this.loadSchedules();
  },
  methods: {
    async loadMovies() {
      try {
        // 使用 fetch 直接调用获取电影列表的接口
        const res = await fetch('http://localhost:8080/movie/all?pageNum=1&pageSize=100', {
          method: 'GET',
          credentials: 'include',
          headers: { 'Content-Type': 'application/json' }
        });
        const data = await res.json();
        console.log('电影列表响应:', data);
        
        if (data.code === 200 && data.data) {
          this.movies = data.data.list || [];
          console.log('加载到的电影数量:', this.movies.length);
        } else {
          this.movies = [];
        }
      } catch (error) {
        console.error('加载电影失败:', error);
        this.movies = [];
      }
    },
    async loadSchedules() {
      try {
        const res = await fetch('http://localhost:8080/schedule/admin/list', { 
          method: 'GET',
          credentials: 'include',
          headers: { 'Content-Type': 'application/json' }
        });
        const data = await res.json();
        console.log('场次列表响应:', data);
        
        if (data.code === 200) {
          this.scheduleList = data.data || [];
        } else {
          this.scheduleList = [];
        }
      } catch (error) {
        console.error('加载场次失败:', error);
        this.scheduleList = [];
      }
    },
    async addSchedule() {
      if (!this.newSchedule.movieId) {
        ElMessage.warning('请选择电影');
        return;
      }
      if (!this.newSchedule.hallId) {
        ElMessage.warning('请选择影厅');
        return;
      }
      if (!this.newSchedule.startTime) {
        ElMessage.warning('请选择开始时间');
        return;
      }
      
      console.log('添加场次参数:', this.newSchedule);
      
      try {
        const res = await fetch('http://localhost:8080/schedule/admin/add', {
          method: 'POST',
          headers: { 'Content-Type': 'application/json' },
          credentials: 'include',
          body: JSON.stringify(this.newSchedule)
        });
        const data = await res.json();
        console.log('添加场次响应:', data);
        
        if (data.code === 200) {
          ElMessage.success('添加成功');
          this.newSchedule = {
            movieId: null,
            hallId: null,
            startTime: '',
            price: 45,
            remainSeats: 80,
            status: 1
          };
          await this.loadSchedules();
        } else {
          ElMessage.error(data.message || '添加失败');
        }
      } catch (error) {
        console.error('添加场次失败:', error);
        ElMessage.error('添加失败');
      }
    },
    async deleteSchedule(id) {
      ElMessageBox.confirm('确定删除该场次吗？', '提示', { type: 'warning' }).then(async () => {
        try {
          const res = await fetch(`http://localhost:8080/schedule/admin/${id}`, {
            method: 'DELETE',
            credentials: 'include'
          });
          const data = await res.json();
          if (data.code === 200) {
            ElMessage.success('删除成功');
            await this.loadSchedules();
          } else {
            ElMessage.error(data.message || '删除失败');
          }
        } catch (error) {
          console.error('删除场次失败:', error);
          ElMessage.error('删除失败');
        }
      });
    }
  }
};
</script>

<style scoped>
.admin-page { padding: 30px 0; }
.container { max-width: 1200px; margin: 0 auto; padding: 0 20px; }
.add-card { margin-bottom: 20px; }
</style>