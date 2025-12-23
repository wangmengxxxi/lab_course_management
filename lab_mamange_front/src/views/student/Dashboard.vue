<template>
  <div class="student-dashboard">
    <el-row :gutter="20">
      <!-- 统计卡片 -->
      <el-col :span="8">
        <el-card shadow="hover">
          <el-statistic title="已选课程" :value="stats.enrolledCourses">
            <template #suffix>
              <el-icon><Reading /></el-icon>
            </template>
          </el-statistic>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card shadow="hover">
          <el-statistic title="本周课程" :value="stats.weekCourses">
            <template #suffix>
              <el-icon><Calendar /></el-icon>
            </template>
          </el-statistic>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card shadow="hover">
          <el-statistic title="平均成绩" :value="stats.averageGrade" :precision="1">
            <template #suffix>
              <el-icon><TrophyBase /></el-icon>
            </template>
          </el-statistic>
        </el-card>
      </el-col>
    </el-row>

    <!-- 快捷操作 -->
    <el-card style="margin-top: 20px">
      <template #header>
        <span>快捷操作</span>
      </template>
      <el-row :gutter="20">
        <el-col :span="6">
          <el-button type="primary" @click="$router.push('/student/courses')" style="width: 100%">
            <el-icon><Search /></el-icon>
            浏览课程
          </el-button>
        </el-col>
        <el-col :span="6">
          <el-button type="success" @click="$router.push('/student/my-courses')" style="width: 100%">
            <el-icon><Reading /></el-icon>
            我的课程
          </el-button>
        </el-col>
        <el-col :span="6">
          <el-button type="info" @click="$router.push('/student/schedule')" style="width: 100%">
            <el-icon><Calendar /></el-icon>
            我的课表
          </el-button>
        </el-col>
        <el-col :span="6">
          <el-button type="warning" @click="$router.push('/student/grades')" style="width: 100%">
            <el-icon><TrophyBase /></el-icon>
            我的成绩
          </el-button>
        </el-col>
      </el-row>
    </el-card>

    <!-- 最新公告 -->
    <el-card style="margin-top: 20px">
      <template #header>
        <div class="card-header">
          <span>最新公告</span>
          <el-button text @click="$router.push('/student/announcements')">查看更多</el-button>
        </div>
      </template>
      <el-timeline v-if="announcements.length > 0">
        <el-timeline-item
          v-for="announcement in announcements.slice(0, 3)"
          :key="announcement.id"
          :timestamp="announcement.publishTime"
        >
          <h4>{{ announcement.title }}</h4>
          <p class="announcement-preview">{{ announcement.content }}</p>
        </el-timeline-item>
      </el-timeline>
      <el-empty v-else description="暂无公告" />
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { Reading, Calendar, TrophyBase, Search } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import request from '@/utils/request'

const stats = reactive({
  enrolledCourses: 0,
  weekCourses: 0,
  averageGrade: 0
})

const announcements = ref([])

const loadStats = async () => {
  try {
    // 加载已选课程
    const coursesRes = await request({
      url: '/enrollments/my',
      method: 'get',
      params: { pageNum: 1, pageSize: 100 }
    })
    
    const myCourses = coursesRes.data.records || []
    stats.enrolledCourses = myCourses.length

    // 计算平均成绩
    const gradesWithScore = myCourses.filter(c => c.grade)
    if (gradesWithScore.length > 0) {
      const sum = gradesWithScore.reduce((total, c) => total + c.grade, 0)
      stats.averageGrade = sum / gradesWithScore.length
    }

    // 计算本周课程数(简化版,实际应该根据当前周次)
    stats.weekCourses = myCourses.filter(c => c.scheduleInfo).length

  } catch (error) {
    console.error('加载统计信息失败:', error)
  }
}

const loadAnnouncements = async () => {
  try {
    const res = await request({
      url: '/announcement/page',
      method: 'get',
      params: {
        current: 1,
        size: 3,
        status: 1
      }
    })
    announcements.value = res.data.records || []
  } catch (error) {
    console.error('加载公告失败:', error)
  }
}

onMounted(() => {
  loadStats()
  loadAnnouncements()
})
</script>

<style scoped>
.student-dashboard {
  padding: 0;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.announcement-preview {
  color: #606266;
  font-size: 14px;
  margin-top: 5px;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
}
</style>
