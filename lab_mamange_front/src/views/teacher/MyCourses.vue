<template>
  <div class="my-courses">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>我的课程</span>
        </div>
      </template>

      <!-- 课程列表 -->
      <el-table
        v-loading="loading"
        :data="tableData"
        border
        style="width: 100%"
      >
        <el-table-column prop="courseId" label="ID" width="80" />
        <el-table-column prop="courseName" label="课程名称" width="200" />
        <el-table-column prop="semester" label="学期" width="120" />
        <el-table-column prop="credit" label="学分" width="80" />
        <el-table-column label="选课情况" width="150">
          <template #default="{ row }">
            <span :class="{ 'text-danger': row.enrolledStudents >= row.maxStudents }">
              {{ row.enrolledStudents || 0 }}/{{ row.maxStudents }}
            </span>
          </template>
        </el-table-column>
        <el-table-column prop="description" label="课程描述" min-width="200" show-overflow-tooltip />
        <el-table-column label="操作" fixed="right" width="200">
          <template #default="{ row }">
            <el-button
              type="primary"
              size="small"
              @click="viewStudents(row)"
            >
              查看学生
            </el-button>
            <el-button
              type="success"
              size="small"
              @click="inputGrades(row)"
            >
              录入成绩
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <el-pagination
        v-model:current-page="pagination.pageNum"
        v-model:page-size="pagination.pageSize"
        :total="pagination.total"
        :page-sizes="[10, 20, 50]"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="loadData"
        @current-change="loadData"
        style="margin-top: 20px; justify-content: flex-end"
      />
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { useAuthStore } from '@/stores/auth'
import request from '@/utils/request'

const router = useRouter()
const authStore = useAuthStore()
const loading = ref(false)
const tableData = ref([])

const pagination = reactive({
  pageNum: 1,
  pageSize: 10,
  total: 0
})

const loadData = async () => {
  loading.value = true
  try {
    const res = await request({
      url: '/courses',
      method: 'get',
      params: {
        pageNum: pagination.pageNum,
        pageSize: pagination.pageSize,
        teacherId: authStore.userInfo.userId
      }
    })
    tableData.value = res.data.records || []
    pagination.total = res.data.total || 0
  } catch (error) {
    console.error('加载课程失败:', error)
    ElMessage.error('加载课程失败')
  } finally {
    loading.value = false
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
  loadData()
})
</script>

<style scoped>
.my-courses {
  padding: 0;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.text-danger {
  color: #f56c6c;
  font-weight: bold;
}
</style>
