<template>
  <div class="admin-page">
    <div class="container">
      <h1>🎬 电影管理</h1>
      
      <!-- 添加电影表单 -->
      <el-card class="add-card">
        <template #header>添加新电影</template>
        <el-form :model="newMovie" label-width="80px">
          <el-row :gutter="20">
            <el-col :span="8">
              <el-form-item label="电影名称">
                <el-input v-model="newMovie.title" placeholder="请输入电影名称"></el-input>
              </el-form-item>
            </el-col>
            <el-col :span="8">
              <el-form-item label="导演">
                <el-input v-model="newMovie.director" placeholder="请输入导演"></el-input>
              </el-form-item>
            </el-col>
            <el-col :span="8">
              <el-form-item label="类型">
                <el-input v-model="newMovie.type" placeholder="请输入类型"></el-input>
              </el-form-item>
            </el-col>
          </el-row>
          <el-row :gutter="20">
            <el-col :span="8">
              <el-form-item label="时长(分钟)">
                <el-input-number v-model="newMovie.duration" :min="1"></el-input-number>
              </el-form-item>
            </el-col>
            <el-col :span="8">
              <el-form-item label="评分">
                <el-input-number v-model="newMovie.rating" :min="0" :max="10" :step="0.1"></el-input-number>
              </el-form-item>
            </el-col>
            <el-col :span="8">
              <el-form-item label="上映日期">
                <el-date-picker v-model="newMovie.releaseDate" type="date" placeholder="选择日期" value-format="YYYY-MM-DD"></el-date-picker>
              </el-form-item>
            </el-col>
          </el-row>
          <el-form-item label="简介">
            <el-input v-model="newMovie.description" type="textarea" :rows="2"></el-input>
          </el-form-item>
          <el-button type="primary" @click="addMovie">添加电影</el-button>
        </el-form>
      </el-card>

      <!-- 电影列表 -->
      <el-table :data="movieList" border style="margin-top: 20px">
        <el-table-column prop="id" label="ID" width="60"></el-table-column>
        <el-table-column prop="title" label="电影名称" width="150"></el-table-column>
        <el-table-column prop="director" label="导演" width="100"></el-table-column>
        <el-table-column prop="type" label="类型" width="100"></el-table-column>
        <el-table-column prop="duration" label="时长(分)" width="80"></el-table-column>
        <el-table-column prop="rating" label="评分" width="80"></el-table-column>
        <el-table-column prop="status" label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'">
              {{ row.status === 1 ? '上映中' : '已下架' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="150">
          <template #default="{ row }">
            <el-button size="small" type="danger" @click="deleteMovie(row.id)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>
  </div>
</template>

<script>
import { movieApi } from '../../api/movie';
import { ElMessage, ElMessageBox } from 'element-plus';

export default {
  name: 'MovieManage',
  data() {
    return {
      movieList: [],
      newMovie: {
        title: '',
        director: '',
        type: '',
        duration: 120,
        rating: 0,
        releaseDate: '',
        description: '',
        status: 1
      }
    };
  },
  created() {
    this.loadMovies();
  },
  methods: {
    async loadMovies() {
      try {
        const res = await fetch('http://localhost:8080/movie/admin/list', {
          method: 'GET',
          credentials: 'include',
          headers: { 'Content-Type': 'application/json' }
        });
        const data = await res.json();
        console.log('电影列表响应:', data);
        
        if (data.code === 200) {
          this.movieList = data.data || [];
        } else {
          this.movieList = [];
          ElMessage.error(data.message || '加载失败');
        }
      } catch (error) {
        console.error('加载电影失败:', error);
        ElMessage.error('加载失败');
        this.movieList = [];
      }
    },
    async addMovie() {
      if (!this.newMovie.title) {
        ElMessage.warning('请输入电影名称');
        return;
      }
      if (!this.newMovie.releaseDate) {
        ElMessage.warning('请选择上映日期');
        return;
      }
      
      try {
        const res = await fetch('http://localhost:8080/movie/admin/add', {
          method: 'POST',
          headers: { 'Content-Type': 'application/json' },
          credentials: 'include',
          body: JSON.stringify(this.newMovie)
        });
        const data = await res.json();
        console.log('添加电影响应:', data);
        
        if (data.code === 200) {
          ElMessage.success('添加成功');
          this.newMovie = {
            title: '',
            director: '',
            type: '',
            duration: 120,
            rating: 0,
            releaseDate: '',
            description: '',
            status: 1
          };
          this.loadMovies();
        } else {
          ElMessage.error(data.message || '添加失败');
        }
      } catch (error) {
        console.error('添加失败:', error);
        ElMessage.error('添加失败');
      }
    },
    async deleteMovie(id) {
      ElMessageBox.confirm('确定删除该电影吗？', '提示', { type: 'warning' }).then(async () => {
        try {
          const res = await fetch(`http://localhost:8080/movie/admin/${id}`, {
            method: 'DELETE',
            credentials: 'include'
          });
          const data = await res.json();
          if (data.code === 200) {
            ElMessage.success('删除成功');
            this.loadMovies();
          } else {
            ElMessage.error(data.message || '删除失败');
          }
        } catch (error) {
          console.error('删除失败:', error);
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