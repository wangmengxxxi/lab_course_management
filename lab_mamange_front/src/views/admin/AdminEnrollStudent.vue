<template>
  <div class="admin-enroll-page">
    <!-- 页面标题 -->
    <el-card class="page-header">
      <div class="header-content">
        <h2>管理员代选课</h2>
        <p>选择学生后，可以查看其已选课程，并帮助其选择新课程或退课</p>
      </div>
    </el-card>

    <div class="main-content">
      <!-- 左侧面板：学生选择 + 已选课程 -->
      <div class="left-panel">
        <!-- 学生选择卡片 -->
        <el-card class="student-card">
          <template #header>
            <div class="card-header">
              <el-icon><User /></el-icon>
              <span>选择学生</span>
            </div>
          </template>
          
          <el-select
            v-model="selectedStudentId"
            placeholder="请选择或搜索学生"
            filterable
            clearable
            style="width: 100%"
            @change="onStudentChange"
          >
            <el-option
              v-for="student in students"
              :key="student.userId"
              :label="`${student.realName} (${student.account})`"
              :value="student.userId"
            />
          </el-select>

          <!-- 学生信息展示 -->
          <div v-if="selectedStudent" class="student-info">
            <el-descriptions :column="1" size="small" border>
              <el-descriptions-item label="姓名">{{ selectedStudent.realName }}</el-descriptions-item>
              <el-descriptions-item label="账号">{{ selectedStudent.account }}</el-descriptions-item>
              <el-descriptions-item label="已选课程">{{ studentCourses.length }} 门</el-descriptions-item>
            </el-descriptions>
          </div>
        </el-card>

        <!-- 已选课程列表 -->
        <el-card class="enrolled-courses-card" v-if="selectedStudentId">
          <template #header>
            <div class="card-header">
              <el-icon><Collection /></el-icon>
              <span>已选课程 ({{ studentCourses.length }})</span>
            </div>
          </template>

          <div v-if="loadingCourses" class="loading-container">
            <el-icon class="is-loading"><Loading /></el-icon>
            <span>加载中...</span>
          </div>

          <div v-else-if="studentCourses.length === 0" class="empty-tip">
            <el-empty description="该学生暂未选课" :image-size="80" />
          </div>

          <div v-else class="course-list">
            <div v-for="course in studentCourses" :key="course.enrollmentId" class="course-item">
              <div class="course-info">
                <div class="course-name">{{ course.course?.courseName }}</div>
                <div class="course-meta">
                  <el-tag size="small" type="info">{{ course.course?.credit }} 学分</el-tag>
                  <span class="teacher-name">{{ course.course?.teacherName }}</span>
                </div>
                <div class="schedule-info" v-if="course.scheduleInfo">
                  <el-icon><Clock /></el-icon>
                  <span>{{ formatSchedule(course.scheduleInfo) }}</span>
                </div>
              </div>
              <el-popconfirm
                title="确定要帮该学生退掉这门课吗?"
                @confirm="handleDropCourse(course.enrollmentId)"
              >
                <template #reference>
                  <el-button type="danger" size="small" link>
                    <el-icon><Delete /></el-icon>
                    退课
                  </el-button>
                </template>
              </el-popconfirm>
            </div>
          </div>
        </el-card>
      </div>

      <!-- 右侧面板：可选课程列表 -->
      <div class="right-panel">
        <el-card class="available-courses-card">
          <template #header>
            <div class="card-header">
              <el-icon><Reading /></el-icon>
              <span>可选课程列表</span>
            </div>
          </template>

          <!-- 搜索和筛选 -->
          <div class="filter-section">
            <el-input
              v-model="searchKeyword"
              placeholder="搜索课程名称..."
              clearable
              style="width: 200px"
              @input="filterCourses"
            >
              <template #prefix>
                <el-icon><Search /></el-icon>
              </template>
            </el-input>

            <el-select
              v-model="filterSemester"
              placeholder="筛选学期"
              clearable
              style="width: 150px"
              @change="filterCourses"
            >
              <el-option label="2025春季学期" value="2025春季学期" />
              <el-option label="2025秋季学期" value="2025秋季学期" />
              <el-option label="2026春季学期" value="2026春季学期" />
            </el-select>
          </div>

          <!-- 提示信息 -->
          <el-alert
            v-if="!selectedStudentId"
            title="请先在左侧选择一名学生"
            type="info"
            :closable="false"
            show-icon
            style="margin-bottom: 15px"
          />

          <!-- 课程表格 -->
          <el-table
            :data="filteredCourses"
            v-loading="loadingAllCourses"
            border
            stripe
            max-height="500"
          >
            <el-table-column prop="courseName" label="课程名称" min-width="150" />
            <el-table-column prop="teacherName" label="授课教师" width="100" />
            <el-table-column prop="credit" label="学分" width="70" align="center" />
            <el-table-column prop="semester" label="学期" width="120" />
            <el-table-column label="上课时间" width="160">
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
            <el-table-column label="容量" width="100" align="center">
              <template #default="{ row }">
                <el-tag :type="getCapacityTagType(row)">
                  {{ row.enrolledStudents || 0 }}/{{ row.maxStudents }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column label="状态" width="100" align="center">
              <template #default="{ row }">
                <el-tag v-if="isAlreadySelected(row.courseId)" type="success">已选</el-tag>
                <el-tag v-else-if="row.status !== 1" type="info">未开放</el-tag>
                <el-tag v-else type="primary">可选</el-tag>
              </template>
            </el-table-column>
            <el-table-column label="操作" width="120" fixed="right" align="center">
              <template #default="{ row }">
                <el-button
                  v-if="!isAlreadySelected(row.courseId) && row.status === 1"
                  type="primary"
                  size="small"
                  :disabled="!selectedStudentId"
                  @click="handleEnroll(row)"
                >
                  选课
                </el-button>
                <span v-else-if="isAlreadySelected(row.courseId)" style="color: #67c23a">
                  已选择
                </span>
                <span v-else style="color: #909399">不可选</span>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </div>
    </div>

    <!-- 选课确认对话框 -->
    <el-dialog
      v-model="enrollDialogVisible"
      title="确认选课"
      width="500px"
    >
      <div v-if="selectedCourse" class="enroll-confirm">
        <el-descriptions :column="1" border>
          <el-descriptions-item label="课程名称">{{ selectedCourse.courseName }}</el-descriptions-item>
          <el-descriptions-item label="授课教师">{{ selectedCourse.teacherName }}</el-descriptions-item>
          <el-descriptions-item label="学分">{{ selectedCourse.credit }}</el-descriptions-item>
          <el-descriptions-item label="学期">{{ selectedCourse.semester }}</el-descriptions-item>
        </el-descriptions>

        <div class="conflict-check" style="margin-top: 15px">
          <div v-if="checkingConflict" class="checking">
            <el-icon class="is-loading"><Loading /></el-icon>
            <span>正在检测时间冲突...</span>
          </div>
          <el-alert
            v-else-if="conflictResult !== null"
            :title="conflictResult ? '存在时间冲突，无法选课' : '无时间冲突，可以选课'"
            :type="conflictResult ? 'error' : 'success'"
            :closable="false"
            show-icon
          />
        </div>
      </div>

      <template #footer>
        <el-button @click="enrollDialogVisible = false">取消</el-button>
        <el-button
          type="primary"
          :loading="enrolling"
          :disabled="conflictResult === true || checkingConflict"
          @click="confirmEnroll"
        >
          确认选课
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted, watch } from 'vue'
import { ElMessage } from 'element-plus'
import { 
  User, Collection, Reading, Search, Delete, Clock, Loading 
} from '@element-plus/icons-vue'
import request from '@/utils/request'

// 状态变量
const students = ref([])
const selectedStudentId = ref(null)
const studentCourses = ref([])
const allCourses = ref([])
const loadingCourses = ref(false)
const loadingAllCourses = ref(false)
const searchKeyword = ref('')
const filterSemester = ref('')
const enrollDialogVisible = ref(false)
const selectedCourse = ref(null)
const checkingConflict = ref(false)
const conflictResult = ref(null)
const enrolling = ref(false)

// 计算属性：当前选中的学生对象
const selectedStudent = computed(() => {
  return students.value.find(s => s.userId === selectedStudentId.value)
})

// 计算属性：筛选后的课程列表
const filteredCourses = computed(() => {
  let result = allCourses.value
  
  if (searchKeyword.value) {
    result = result.filter(c => 
      c.courseName?.toLowerCase().includes(searchKeyword.value.toLowerCase())
    )
  }
  
  if (filterSemester.value) {
    result = result.filter(c => c.semester === filterSemester.value)
  }
  
  return result
})

// 加载学生列表（仅学生角色）
const loadStudents = async () => {
  try {
    const res = await request({
      url: '/user',
      method: 'get',
      params: { pageNum: 1, pageSize: 100 }
    })
    const allUsers = res.data.records || []
    students.value = allUsers.filter(u => u.roles && u.roles.includes('student'))
  } catch (error) {
    console.error('加载学生列表失败:', error)
  }
}

// 加载指定学生的已选课程
const loadStudentCourses = async () => {
  if (!selectedStudentId.value) {
    studentCourses.value = []
    return
  }
  
  loadingCourses.value = true
  try {
    const res = await request({
      url: '/admin/student-courses/student',
      method: 'get',
      params: { studentId: selectedStudentId.value }
    })
    studentCourses.value = res.data || []
  } catch (error) {
    console.error('加载学生课程失败:', error)
    studentCourses.value = []
  } finally {
    loadingCourses.value = false
  }
}

// 加载所有课程
const loadAllCourses = async () => {
  loadingAllCourses.value = true
  try {
    const res = await request({
      url: '/courses',
      method: 'get',
      params: { pageNum: 1, pageSize: 100 }
    })
    allCourses.value = res.data.records || []
  } catch (error) {
    console.error('加载课程列表失败:', error)
  } finally {
    loadingAllCourses.value = false
  }
}

// 学生选择变化
const onStudentChange = () => {
  loadStudentCourses()
}

// 判断课程是否已选
const isAlreadySelected = (courseId) => {
  return studentCourses.value.some(sc => sc.course?.courseId === courseId)
}

// 获取容量标签类型
const getCapacityTagType = (course) => {
  const current = course.currentStudents || 0
  const max = course.maxStudents || 1
  const ratio = current / max
  if (ratio >= 1) return 'danger'
  if (ratio >= 0.8) return 'warning'
  return 'success'
}

// 格式化排课信息
const formatSchedule = (scheduleInfo) => {
  if (!scheduleInfo) return '暂无排课'
  const dayMap = { 1: '周一', 2: '周二', 3: '周三', 4: '周四', 5: '周五', 6: '周六', 7: '周日' }
  return `${dayMap[scheduleInfo.dayOfWeek] || ''} ${scheduleInfo.timeSlotName || ''} (${scheduleInfo.startWeek}-${scheduleInfo.endWeek}周)`
}

// 点击选课按钮
const handleEnroll = async (course) => {
  if (!selectedStudentId.value) {
    ElMessage.warning('请先选择学生')
    return
  }
  
  selectedCourse.value = course
  enrollDialogVisible.value = true
  conflictResult.value = null
  
  // 检测冲突
  checkingConflict.value = true
  try {
    const res = await request({
      url: '/admin/student-courses/check-conflict',
      method: 'get',
      params: {
        courseId: course.courseId,
        studentId: selectedStudentId.value
      }
    })
    conflictResult.value = res.data
  } catch (error) {
    console.error('检测冲突失败:', error)
    conflictResult.value = null
  } finally {
    checkingConflict.value = false
  }
}

// 确认选课
const confirmEnroll = async () => {
  if (!selectedCourse.value || !selectedStudentId.value) return
  
  enrolling.value = true
  try {
    await request({
      url: '/admin/student-courses/select',
      method: 'post',
      data: {
        courseId: selectedCourse.value.courseId,
        studentId: selectedStudentId.value
      }
    })
    ElMessage.success('选课成功')
    enrollDialogVisible.value = false
    loadStudentCourses() // 刷新已选课程
  } catch (error) {
    console.error('选课失败:', error)
    ElMessage.error(error.response?.data?.message || '选课失败')
  } finally {
    enrolling.value = false
  }
}

// 退课
const handleDropCourse = async (enrollmentId) => {
  try {
    await request({
      url: `/admin/student-courses/drop/${enrollmentId}`,
      method: 'delete'
    })
    ElMessage.success('退课成功')
    loadStudentCourses()
  } catch (error) {
    console.error('退课失败:', error)
    ElMessage.error(error.response?.data?.message || '退课失败')
  }
}

// 筛选课程
const filterCourses = () => {
  // 由 computed 自动处理
}

onMounted(() => {
  loadStudents()
  loadAllCourses()
})
</script>

<style scoped>
.admin-enroll-page {
  padding: 0;
}

.page-header {
  margin-bottom: 20px;
}

.header-content h2 {
  margin: 0 0 8px 0;
  font-size: 20px;
  color: #303133;
}

.header-content p {
  margin: 0;
  color: #909399;
  font-size: 14px;
}

.main-content {
  display: flex;
  gap: 20px;
}

.left-panel {
  width: 350px;
  flex-shrink: 0;
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.right-panel {
  flex: 1;
  min-width: 0;
}

.card-header {
  display: flex;
  align-items: center;
  gap: 8px;
  font-weight: 500;
}

.student-info {
  margin-top: 15px;
}

.loading-container {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  padding: 20px;
  color: #909399;
}

.empty-tip {
  padding: 20px 0;
}

.course-list {
  max-height: 400px;
  overflow-y: auto;
}

.course-item {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  padding: 12px;
  border: 1px solid #ebeef5;
  border-radius: 4px;
  margin-bottom: 10px;
  transition: all 0.3s;
}

.course-item:hover {
  border-color: #409eff;
  box-shadow: 0 2px 8px rgba(64, 158, 255, 0.1);
}

.course-info {
  flex: 1;
}

.course-name {
  font-weight: 500;
  color: #303133;
  margin-bottom: 6px;
}

.course-meta {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 4px;
}

.teacher-name {
  color: #909399;
  font-size: 12px;
}

.schedule-info {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 12px;
  color: #606266;
}

.filter-section {
  display: flex;
  gap: 15px;
  margin-bottom: 15px;
}

.enroll-confirm {
  padding: 10px 0;
}

.checking {
  display: flex;
  align-items: center;
  gap: 8px;
  color: #909399;
}
</style>
