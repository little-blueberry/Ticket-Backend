<template>
  <div id="app">
    <nav class="navbar">
      <div class="nav-container">
        <div class="logo" @click="goHome">🎬 在线电影订票系统</div>
        <div class="nav-links">
          <router-link to="/">首页</router-link>
          <router-link to="/movies">正在热映</router-link>
          <router-link to="/myorders" v-if="isLogin && !isAdmin">我的订单</router-link>
          <!-- 管理员专属菜单 -->
          <template v-if="isLogin && isAdmin">
            <router-link to="/admin/movies">🎬 电影管理</router-link>
            <router-link to="/admin/schedules">📅 场次管理</router-link>
            <router-link to="/admin/orders">📋 订单管理</router-link>
          </template>
        </div>
        <div class="nav-user">
          <div v-if="isLogin" class="user-info">
            <span>欢迎，{{ username }}</span>
            <span class="balance" v-if="!isAdmin">余额：¥{{ balance }}</span>
            <span class="role-badge" v-if="isAdmin">👑 管理员</span>
            <el-button type="danger" size="small" @click="handleLogout">退出</el-button>
          </div>
          <div v-else>
            <el-button type="primary" size="small" @click="showLogin = true">登录</el-button>
            <el-button size="small" @click="showRegister = true">注册</el-button>
          </div>
        </div>
      </div>
    </nav>

    <router-view @loginSuccess="handleLoginSuccess"/>

    <!-- 登录弹窗 -->
    <el-dialog v-model="showLogin" title="登录" width="400px">
      <el-form :model="loginForm" :rules="loginRules" ref="loginFormRef">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="loginForm.username" placeholder="请输入用户名"></el-input>
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input type="password" v-model="loginForm.password" placeholder="请输入密码"></el-input>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showLogin = false">取消</el-button>
        <el-button type="primary" @click="handleLogin">登录</el-button>
      </template>
    </el-dialog>

    <!-- 注册弹窗 -->
    <el-dialog v-model="showRegister" title="注册" width="400px">
      <el-form :model="registerForm" :rules="registerRules" ref="registerFormRef">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="registerForm.username" placeholder="请输入用户名"></el-input>
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input type="password" v-model="registerForm.password" placeholder="至少6位"></el-input>
        </el-form-item>
        <el-form-item label="确认密码" prop="confirmPassword">
          <el-input type="password" v-model="registerForm.confirmPassword" placeholder="请再次输入密码"></el-input>
        </el-form-item>
        <el-form-item label="手机号" prop="phone">
          <el-input v-model="registerForm.phone" placeholder="选填"></el-input>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showRegister = false">取消</el-button>
        <el-button type="primary" @click="handleRegister">注册</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import { userApi } from './api/user';
import { ElMessage } from 'element-plus';

export default {
  name: 'App',
  data() {
    const validateConfirm = (rule, value, callback) => {
      if (value !== this.registerForm.password) {
        callback(new Error('两次输入密码不一致'));
      } else {
        callback();
      }
    };
    return {
      isLogin: false,
      isAdmin: false,
      username: '',
      balance: 0,
      showLogin: false,
      showRegister: false,
      loginForm: { username: '', password: '' },
      loginRules: {
        username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
        password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
      },
      registerForm: { username: '', password: '', confirmPassword: '', phone: '' },
      registerRules: {
        username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
        password: [{ required: true, min: 6, message: '密码至少6位', trigger: 'blur' }],
        confirmPassword: [{ required: true, validator: validateConfirm, trigger: 'blur' }]
      }
    };
  },
  created() {
    this.checkLogin();
  },
  methods: {
    async checkLogin() {
      try {
        console.log('检查登录状态...');
        const res = await userApi.getInfo();
        console.log('用户信息响应:', res);
        if (res.code === 200 && res.data) {
          this.isLogin = true;
          this.username = res.data.username;
          this.balance = res.data.balance || 0;
          this.isAdmin = res.data.role === 'admin';
        } else {
          this.isLogin = false;
          this.isAdmin = false;
        }
      } catch (error) {
        console.error('检查登录失败:', error);
        this.isLogin = false;
        this.isAdmin = false;
      }
    },
    async handleLogin() {
      this.$refs.loginFormRef.validate(async (valid) => {
        if (valid) {
          try {
            console.log('开始登录请求...');
            const res = await userApi.login({
              username: this.loginForm.username,
              password: this.loginForm.password
            });
            console.log('登录响应:', res);
            
            if (res.code === 200) {
              ElMessage.success('登录成功');
              this.showLogin = false;
              this.isLogin = true;
              this.username = res.data.user.username;
              this.balance = res.data.user.balance;
              this.isAdmin = res.data.user.role === 'admin';
              this.$router.push('/');
            } else {
              ElMessage.error(res.message);
            }
          } catch (error) {
            console.error('登录请求失败:', error);
          }
        }
      });
    },
    async handleRegister() {
      this.$refs.registerFormRef.validate(async (valid) => {
        if (valid) {
          try {
            const res = await userApi.register(this.registerForm);
            if (res.code === 200) {
              ElMessage.success('注册成功，请登录');
              this.showRegister = false;
            } else {
              ElMessage.error(res.message);
            }
          } catch (error) {
            console.error('注册请求失败:', error);
            ElMessage.error('注册失败，请检查网络');
          }
        }
      });
    },
    async handleLogout() {
      await userApi.logout();
      this.isLogin = false;
      this.isAdmin = false;
      this.username = '';
      this.balance = 0;
      ElMessage.success('已退出登录');
      this.$router.push('/');
    },
    handleLoginSuccess() {
      this.checkLogin();
    },
    goHome() {
      this.$router.push('/');
    }
  }
};
</script>

<style>
* { margin: 0; padding: 0; box-sizing: border-box; }
body { font-family: 'Microsoft YaHei', sans-serif; background: #f5f7fa; }
.navbar { background: #1a1a2e; color: white; padding: 15px 0; position: sticky; top: 0; z-index: 100; }
.nav-container { max-width: 1200px; margin: 0 auto; display: flex; justify-content: space-between; align-items: center; padding: 0 20px; }
.logo { font-size: 24px; font-weight: bold; cursor: pointer; }
.nav-links a { color: white; text-decoration: none; margin: 0 15px; transition: opacity 0.3s; }
.nav-links a:hover { opacity: 0.8; }
.nav-links .router-link-active { color: #ff6b6b; }
.nav-user { display: flex; align-items: center; gap: 15px; }
.user-info { display: flex; align-items: center; gap: 15px; }
.balance { color: #ffd700; }
.role-badge { background: #ff6b6b; padding: 2px 8px; border-radius: 12px; font-size: 12px; }
</style>