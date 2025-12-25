<template>
  <div class="browse-courses">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>浏览课程</span>
        </div>
      </template>

      <!-- 搜索和筛选 -->
      <el-form :inline="true" style="margin-bottom: 20px">
        <el-form-item label="课程名称">
          <el-input
            v-model="searchName"
            placeholder="请输入课程名称"
            clearable
            style="width: 200px"
            @clear="loadData"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="loadData">
            <el-icon><Search /></el-icon>
            搜索
          </el-button>
        </el-form-item>
      </el-form>

      <!-- 课程列表 -->
      <el-table
        v-loading="loading"
        :data="tableData"
        border
        style="width: 100%"
      >
        <el-table-column prop="courseId" label="ID" width="80" />
        <el-table-column prop="courseName" label="课程名称" width="200" />
        <el-table-column prop="teacherName" label="教师" width="120" />
        <el-table-column prop="credit" label="学分" width="80" />
        <el-table-column label="选课情况" width="150">
          <template #default="{ row }">
            <span :class="{ 'text-danger': row.enrolledStudents >= row.maxStudents }">
              {{ row.enrolledStudents || 0 }}/{{ row.maxStudents }}
            </span>
          </template>
        </el-table-column>
        <el-table-column prop="semester" label="学期" width="120" />
        <el-table-column label="上课时间" width="180">
          <template #default="{ row }">
            <div v-if="row.scheduleInfo">
              <div>{{ row.scheduleInfo.dayOfWeekText }} {{ row.scheduleInfo.timeSlotName }}</div>
              <div style="color: #909399; font-size: 12px">
                第{{ row.scheduleInfo.startWeek }}-{{ row.scheduleInfo.endWeek }}周
              </div>
            </div>
            <span v-else style="color: #909399">暂无排课</span>
          </template>
        </el-table-column>
        <el-table-column prop="description" label="课程描述" min-width="150" show-overflow-tooltip />
        <el-table-column label="操作" fixed="right" width="120">
          <template #default="{ row }">
            <el-button
              v-if="!isEnrolled(row.courseId)"
              type="primary"
              size="small"
              :disabled="isFull(row)"
              @click="handleEnroll(row)"
            >
              {{ isFull(row) ? '已满' : '选课' }}
            </el-button>
            <el-tag v-else type="success">已选</el-tag>
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
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search } from '@element-plus/icons-vue'
import request from '@/utils/request'

const loading = ref(false)
const tableData = ref([])
const searchName = ref('')
const enrolledCourseIds = ref(new Set())

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
        status: 1  // 只显示已通过的课程
      }
    })
    
    let courses = res.data.records || []
    
    // 前端搜索过滤
    if (searchName.value) {
      courses = courses.filter(course => 
        course.courseName && course.courseName.includes(searchName.value)
      )
    }
    
    tableData.value = courses
    pagination.total = res.data.total || 0
  } catch (error) {
    console.error('加载课程失败:', error)
    ElMessage.error('加载课程失败')
  } finally {
    loading.value = false
  }
}

const loadMyEnrollments = async () => {
  try {
    const res = await request({
      url: '/enrollments/my',
      method: 'get',
      params: {
        pageNum: 1,
        pageSize: 100
      }
    })
    
    const enrollments = res.data.records || []
    enrolledCourseIds.value = new Set(enrollments.map(e => e.courseId))
  } catch (error) {
    console.error('加载已选课程失败:', error)
  }
}

const isEnrolled = (courseId) => {
  return enrolledCourseIds.value.has(courseId)
}

const isFull = (course) => {
  return course.enrolledStudents >= course.maxStudents
}

const handleEnroll = async (course) => {
  // 检查是否已满
  if (isFull(course)) {
    ElMessage.warning('该课程已满')
    return
  }

  // 检查冲突
  try {
    const conflictRes = await request({
      url: `/enrollments/check-conflict/${course.courseId}`,
      method: 'get'
    })
    
    if (conflictRes.data) {
      ElMessage.error('选课失败:与已选课程时间冲突')
      return
    }
  } catch (error) {
    console.error('检查冲突失败:', error)
  }

  ElMessageBox.confirm(
    `确定要选择《${course.courseName}》吗?`,
    '确认选课',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'info'
    }
  ).then(async () => {
    try {
      await request({
        url: '/enrollments',
        method: 'post',
        data: {
          courseId: course.courseId
        }
      })
      
      ElMessage.success('选课成功')
      enrolledCourseIds.value.add(course.courseId)
      loadData()  // 刷新列表
    } catch (error) {
      console.error('选课失败:', error)
      ElMessage.error(error.response?.data?.message || '选课失败')
    }
  }).catch(() => {
    // 取消选课
  })
}

onMounted(() => {
  loadData()
  loadMyEnrollments()
})
</script>

<style scoped>
.browse-courses {
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
