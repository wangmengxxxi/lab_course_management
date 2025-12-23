<template>
  <div class="dashboard">
    <el-row :gutter="20">
      <el-col :span="6">
        <el-card shadow="hover">
          <el-statistic title="用户总数" :value="stats.totalUsers" />
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover">
          <el-statistic title="课程总数" :value="stats.totalCourses" />
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover">
          <el-statistic title="实验室总数" :value="stats.totalLabs" />
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover">
          <el-statistic title="公告总数" :value="stats.totalAnnouncements" />
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" style="margin-top: 20px">
      <el-col :span="6">
        <el-card shadow="hover">
          <el-statistic title="学生总数" :value="stats.totalStudents" />
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover">
          <el-statistic title="教师总数" :value="stats.totalTeachers" />
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover">
          <el-statistic title="已发布课程" :value="stats.publishedCourses" />
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover">
          <el-statistic title="待审批课程" :value="stats.pendingCourses" />
        </el-card>
      </el-col>
    </el-row>

    <el-card style="margin-top: 20px">
      <template #header>
        <span>快捷操作</span>
      </template>
      <el-space wrap>
        <el-button type="primary" @click="$router.push('/admin/users')">用户管理</el-button>
        <el-button type="success" @click="$router.push('/admin/labs')">实验室管理</el-button>
        <el-button type="warning" @click="$router.push('/admin/courses')">课程管理</el-button>
        <el-button type="info" @click="$router.push('/admin/announcements')">公告管理</el-button>
      </el-space>
    </el-card>
  </div>
</template>

<script setup>
import { reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import request from '@/utils/request'

const stats = reactive({
  totalUsers: 0,
  totalCourses: 0,
  totalLabs: 0,
  totalAnnouncements: 0,
  totalStudents: 0,
  totalTeachers: 0,
  publishedCourses: 0,
  pendingCourses: 0
})

const loadStats = async () => {
  try {
    const res = await request({
      url: '/api/statistics/overview',
      method: 'get'
    })
    Object.assign(stats, res.data)
  } catch (error) {
    console.error('加载统计数据失败:', error)
    ElMessage.error('加载统计数据失败')
  }
}

onMounted(() => {
  loadStats()
})
</script>

<style scoped>
.dashboard {
  padding: 0;
}
</style>
