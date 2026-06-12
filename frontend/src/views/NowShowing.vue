<template>
  <div class="movie-list">
    <div class="container">
      <h1 class="page-title">🔥 正在热映（近一年上映）</h1>
      
      <div class="search-bar">
        <el-input 
          v-model="keyword" 
          placeholder="搜索电影名称" 
          style="width: 300px"
          @keyup.enter="handleSearch"
          clearable
        >
          <template #append>
            <el-button @click="handleSearch">搜索</el-button>
          </template>
        </el-input>
      </div>

      <div v-if="loading" class="loading-container">
        <el-skeleton :rows="5" animated />
      </div>

      <div v-else-if="movieList.length === 0" class="empty">
        <el-empty description="暂无热映电影">
          <el-button type="primary" @click="loadMovies">刷新</el-button>
        </el-empty>
      </div>

      <div v-else class="movie-grid">
        <div class="movie-card" v-for="movie in movieList" :key="movie.id" @click="goToDetail(movie.id)">
          <div class="movie-poster">
            <img :src="movie.poster || `https://picsum.photos/220/300?random=${movie.id}`" :alt="movie.title">
            <div class="movie-rating">{{ movie.rating || '暂无' }}分</div>
          </div>
          <div class="movie-info">
            <h3>{{ movie.title }}</h3>
            <p class="movie-type">{{ movie.type }} | {{ movie.duration }}分钟</p>
            <p class="movie-desc">{{ (movie.description || '暂无简介').slice(0, 50) }}...</p>
          </div>
        </div>
      </div>

      <div class="pagination" v-if="total > pageSize">
        <el-pagination 
          v-model:current-page="pageNum"
          :page-size="pageSize"
          :total="total"
          @current-change="handlePageChange"
          layout="prev, pager, next"
        />
      </div>
    </div>
  </div>
</template>

<script>
import { movieApi } from '../api/movie';
import { ElMessage } from 'element-plus';

export default {
  name: 'NowShowing',
  data() {
    return {
      loading: false,
      keyword: '',
      pageNum: 1,
      pageSize: 12,
      total: 0,
      movieList: []
    };
  },
  mounted() {
    this.loadMovies();
  },
  methods: {
    async loadMovies() {
      this.loading = true;
      try {
        const params = {
          pageNum: this.pageNum,
          pageSize: this.pageSize
        };
        if (this.keyword) params.keyword = this.keyword;
        
        const res = await movieApi.getNowShowingMovies(params);
        console.log('正在热映响应:', res);
        
        if (res.code === 200 && res.data) {
          this.movieList = res.data.list || [];
          this.total = res.data.total || 0;
        }
      } catch (error) {
        console.error('加载失败:', error);
        ElMessage.error('加载失败');
      } finally {
        this.loading = false;
      }
    },
    handleSearch() {
      this.pageNum = 1;
      this.loadMovies();
    },
    handlePageChange(page) {
      this.pageNum = page;
      this.loadMovies();
    },
    goToDetail(id) {
      this.$router.push(`/movie/${id}`);
    }
  }
};
</script>

<style scoped>
.movie-list { min-height: calc(100vh - 70px); padding: 30px 0; }
.container { max-width: 1200px; margin: 0 auto; padding: 0 20px; }
.page-title { margin-bottom: 20px; color: #333; }
.search-bar { display: flex; gap: 15px; margin-bottom: 30px; justify-content: center; }
.movie-grid { display: grid; grid-template-columns: repeat(auto-fill, minmax(220px, 1fr)); gap: 25px; }
.movie-card { background: white; border-radius: 10px; overflow: hidden; box-shadow: 0 2px 10px rgba(0,0,0,0.1); cursor: pointer; transition: transform 0.3s; }
.movie-card:hover { transform: translateY(-5px); }
.movie-poster { position: relative; height: 300px; overflow: hidden; }
.movie-poster img { width: 100%; height: 100%; object-fit: cover; }
.movie-rating { position: absolute; top: 10px; right: 10px; background: rgba(0,0,0,0.7); color: #ffd700; padding: 4px 8px; border-radius: 4px; }
.movie-info { padding: 15px; }
.movie-info h3 { font-size: 16px; margin-bottom: 8px; }
.movie-type { color: #666; font-size: 12px; margin-bottom: 8px; }
.movie-desc { color: #999; font-size: 12px; }
.pagination { display: flex; justify-content: center; margin-top: 40px; }
.loading-container { padding: 40px; }
.empty { text-align: center; padding: 60px 0; }
</style>