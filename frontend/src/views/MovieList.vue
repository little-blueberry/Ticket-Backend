<template>
  <div class="movie-list">
    <div class="container">
      <!-- 搜索栏 -->
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

      <!-- 加载状态 -->
      <div v-if="loading" class="loading-container">
        <el-skeleton :rows="5" animated />
      </div>

      <!-- 无数据 -->
      <div v-else-if="movieList.length === 0" class="empty">
        <el-empty description="暂无电影数据">
          <el-button type="primary" @click="loadMovies">刷新</el-button>
        </el-empty>
      </div>

      <!-- 电影列表 -->
      <div v-else class="movie-grid">
        <div 
          class="movie-card" 
          v-for="movie in movieList" 
          :key="movie.id" 
          @click="goToDetail(movie.id)"
        >
          <div class="movie-poster">
            <img 
              :src="movie.poster || `https://picsum.photos/220/300?random=${movie.id}`" 
              :alt="movie.title"
              @error="handleImageError"
            >
            <div class="movie-rating">{{ movie.rating || '暂无' }}分</div>
          </div>
          <div class="movie-info">
            <h3>{{ movie.title }}</h3>
            <p class="movie-type">{{ movie.type || '未知' }} | {{ movie.duration || 0 }}分钟</p>
            <p class="movie-desc">{{ (movie.description || '暂无简介').slice(0, 50) }}...</p>
          </div>
        </div>
      </div>

      <!-- 分页 -->
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
  name: 'MovieList',
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
    console.log('页面加载，开始获取电影列表');
    this.loadMovies();
  },
  methods: {
    handleImageError(event) {
      event.target.src = `https://picsum.photos/220/300?random=${Date.now()}`;
    },
    
    async loadMovies() {
      this.loading = true;
      try {
        const params = {
          pageNum: this.pageNum,
          pageSize: this.pageSize
        };
        if (this.keyword && this.keyword.trim()) {
          params.keyword = this.keyword.trim();
        }
        
        console.log('请求参数:', params);
        const res = await movieApi.getList(params);
        console.log('响应数据:', res);
        
        if (res && res.code === 200 && res.data) {
          this.movieList = res.data.list || [];
          this.total = res.data.total || 0;
          console.log('✅ 成功加载', this.movieList.length, '部电影');
        } else {
          console.warn('⚠️ 响应异常:', res);
          this.movieList = [];
          this.total = 0;
        }
      } catch (error) {
        console.error('❌ 加载失败:', error);
        ElMessage.error('加载失败: ' + (error.message || '未知错误'));
        this.movieList = [];
      } finally {
        this.loading = false;
      }
    },
    
    handleSearch() {
      console.log('🔍 搜索关键词:', this.keyword);
      this.pageNum = 1;
      this.loadMovies();
    },
    
    handlePageChange(page) {
      console.log('📄 切换页码:', page);
      this.pageNum = page;
      this.loadMovies();
    },
    
    goToDetail(id) {
      console.log('🎬 点击电影, ID:', id);
      if (id && id !== 'undefined' && id !== null) {
        this.$router.push(`/movie/${id}`);
      } else {
        ElMessage.error('电影ID无效');
      }
    }
  }
};
</script>

<style scoped>
.movie-list { min-height: calc(100vh - 70px); padding: 30px 0; }
.container { max-width: 1200px; margin: 0 auto; padding: 0 20px; }
.search-bar { display: flex; gap: 15px; margin-bottom: 30px; justify-content: center; }
.movie-grid { display: grid; grid-template-columns: repeat(auto-fill, minmax(220px, 1fr)); gap: 25px; }
.movie-card { background: white; border-radius: 10px; overflow: hidden; box-shadow: 0 2px 10px rgba(0,0,0,0.1); cursor: pointer; transition: transform 0.3s; }
.movie-card:hover { transform: translateY(-5px); box-shadow: 0 5px 20px rgba(0,0,0,0.15); }
.movie-poster { position: relative; height: 300px; overflow: hidden; }
.movie-poster img { width: 100%; height: 100%; object-fit: cover; }
.movie-rating { position: absolute; top: 10px; right: 10px; background: rgba(0,0,0,0.7); color: #ffd700; padding: 4px 8px; border-radius: 4px; font-size: 12px; }
.movie-info { padding: 15px; }
.movie-info h3 { font-size: 16px; margin-bottom: 8px; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; }
.movie-type { color: #666; font-size: 12px; margin-bottom: 8px; }
.movie-desc { color: #999; font-size: 12px; line-height: 1.4; display: -webkit-box; -webkit-line-clamp: 2; -webkit-box-orient: vertical; overflow: hidden; }
.pagination { display: flex; justify-content: center; margin-top: 40px; }
.loading-container { padding: 40px; }
.empty { text-align: center; padding: 60px 0; }
</style>