<template>
  <div class="teacher-dashboard">
    <el-row :gutter="20">
      <!-- 统计卡片 -->
      <el-col :span="8">
        <el-card shadow="hover">
          <el-statistic title="我的课程" :value="stats.totalCourses">
            <template #suffix>
              <el-icon><Reading /></el-icon>
            </template>
          </el-statistic>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card shadow="hover">
          <el-statistic title="学生总数" :value="stats.totalStudents">
            <template #suffix>
              <el-icon><User /></el-icon>
            </template>
          </el-statistic>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card shadow="hover">
          <el-statistic title="待录入成绩" :value="stats.pendingGrades">
            <template #suffix>
              <el-icon><Edit /></el-icon>
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
          <el-button type="primary" @click="$router.push('/teacher/courses')" style="width: 100%">
            <el-icon><Reading /></el-icon>
            我的课程
          </el-button>
        </el-col>
        <el-col :span="6">
          <el-button type="success" @click="$router.push('/teacher/grades')" style="width: 100%">
            <el-icon><Edit /></el-icon>
            录入成绩
          </el-button>
        </el-col>
        <el-col :span="6">
          <el-button type="info" @click="$router.push('/teacher/schedule')" style="width: 100%">
            <el-icon><Calendar /></el-icon>
            我的课表
          </el-button>
        </el-col>
      </el-row>
    </el-card>

    <!-- 课程列表 -->
    <el-card style="margin-top: 20px">
      <template #header>
        <div class="card-header">
          <span>我的课程</span>
          <el-button text @click="$router.push('/teacher/courses')">查看更多</el-button>
        </div>
      </template>
      <el-table :data="courses" border style="width: 100%">
        <el-table-column prop="courseName" label="课程名称" width="200" />
        <el-table-column prop="semester" label="学期" width="120" />
        <el-table-column label="选课情况" width="150">
          <template #default="{ row }">
            {{ row.enrolledStudents || 0 }}/{{ row.maxStudents }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200">
          <template #default="{ row }">
            <el-button type="primary" size="small" @click="viewStudents(row)">
              查看学生
            </el-button>
            <el-button type="success" size="small" @click="inputGrades(row)">
              录入成绩
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { Reading, User, Edit, Calendar } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import { useAuthStore } from '@/stores/auth'
import request from '@/utils/request'

const router = useRouter()
const authStore = useAuthStore()

const stats = reactive({
  totalCourses: 0,
  totalStudents: 0,
  pendingGrades: 0
})

const courses = ref([])

const loadStats = async () => {
  try {
    // 加载教师课程
    const coursesRes = await request({
      url: '/courses',
      method: 'get',
      params: {
        pageNum: 1,
        pageSize: 100,
        teacherId: authStore.userInfo.userId
      }
    })
    
    const myCourses = coursesRes.data.records || []
    stats.totalCourses = myCourses.length
    courses.value = myCourses.slice(0, 5) // 只显示前5个

    // 计算学生总数和待录入成绩数
    let totalStudents = 0
    let pendingGrades = 0

    for (const course of myCourses) {
      try {
        const gradesRes = await request({
          url: '/admin/student-courses/grades',
          method: 'get',
          params: {
            courseId: course.courseId,
            pageNum: 1,
            pageSize: 1
          }
        })
        
        const total = gradesRes.data.total || 0
        totalStudents += total
        
        // 简化:假设没有成绩的就是待录入
        const studentsRes = await request({
          url: '/admin/student-courses/grades',
          method: 'get',
          params: {
            courseId: course.courseId,
            pageNum: 1,
            pageSize: 100
          }
        })
        
        const students = studentsRes.data.records || []
        const pending = students.filter(s => !s.grade).length
        pendingGrades += pending
      } catch (error) {
        console.error('获取课程统计失败:', error)
      }
    }

    stats.totalStudents = totalStudents
    stats.pendingGrades = pendingGrades

  } catch (error) {
    console.error('加载统计信息失败:', error)
  }
}

const viewStudents = (course) => {
  router.push({
    path: `/teacher/course/${course.courseId}/students`,
    query: { courseName: course.courseName }
  })
}

const inputGrades = (course) => {
  router.push({
    path: '/teacher/grades',
    query: { courseId: course.courseId, courseName: course.courseName }
  })
}

onMounted(() => {
  loadStats()
})
</script>

<style scoped>
.teacher-dashboard {
  padding: 0;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>
