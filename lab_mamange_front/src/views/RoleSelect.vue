<template>
  <div class="role-select-container">
    <div class="role-select-box">
      <div class="header">
        <h1>选择角色</h1>
        <p>您拥有多个角色,请选择要使用的角色</p>
      </div>
      
      <div class="role-cards">
        <div
          v-for="role in roleList"
          :key="role.value"
          class="role-card"
          @click="selectRole(role.value)"
        >
          <el-icon :size="48" class="role-icon">
            <component :is="role.icon" />
          </el-icon>
          <h3>{{ role.label }}</h3>
          <p>{{ role.description }}</p>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { UserFilled, Reading, User } from '@element-plus/icons-vue'
import { useAuthStore } from '@/stores/auth'

const router = useRouter()
const authStore = useAuthStore()

// 角色配置
const roleConfig = {
  admin: {
    value: 'admin',
    label: '管理员',
    description: '管理用户、实验室、课程和公告',
    icon: UserFilled
  },
  teacher: {
    value: 'teacher',
    label: '教师',
    description: '管理课程、录入成绩',
    icon: Reading
  },
  student: {
    value: 'student',
    label: '学生',
    description: '选课、查看课表和成绩',
    icon: User
  }
}

// 根据用户拥有的角色生成角色列表
const roleList = computed(() => {
  return authStore.roles.map(role => roleConfig[role]).filter(Boolean)
})

const selectRole = async (role) => {
  try {
    await authStore.switchRole(role)
    ElMessage.success(`已切换到${roleConfig[role].label}角色`)
  } catch (error) {
    console.error('切换角色失败:', error)
    ElMessage.error('切换角色失败')
  }
}

// 如果用户没有登录或只有一个角色,重定向
if (!authStore.isLoggedIn) {
  router.push('/login')
} else if (!authStore.hasMultipleRoles) {
  router.push(authStore.dashboardPath)
}
</script>

<style scoped>
.role-select-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.role-select-box {
  width: 800px;
  padding: 40px;
  background: white;
  border-radius: 10px;
  box-shadow: 0 10px 40px rgba(0, 0, 0, 0.2);
}

.header {
  text-align: center;
  margin-bottom: 40px;
}

.header h1 {
  font-size: 28px;
  color: #333;
  margin: 0 0 10px 0;
}

.header p {
  font-size: 14px;
  color: #999;
  margin: 0;
}

.role-cards {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 20px;
}

.role-card {
  padding: 30px 20px;
  text-align: center;
  border: 2px solid #e0e0e0;
  border-radius: 10px;
  cursor: pointer;
  transition: all 0.3s ease;
}

.role-card:hover {
  border-color: #667eea;
  box-shadow: 0 5px 20px rgba(102, 126, 234, 0.3);
  transform: translateY(-5px);
}

.role-icon {
  color: #667eea;
  margin-bottom: 15px;
}

.role-card h3 {
  font-size: 20px;
  color: #333;
  margin: 0 0 10px 0;
}

.role-card p {
  font-size: 14px;
  color: #666;
  margin: 0;
}
</style>
