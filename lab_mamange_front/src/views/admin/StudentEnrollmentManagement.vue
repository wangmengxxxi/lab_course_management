<template>
  <div class="student-enrollment-management">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>学生选课管理</span>
          <el-button type="primary" @click="showAddDialog">
            <el-icon><Plus /></el-icon>
            代学生选课
          </el-button>
        </div>
      </template>

      <!-- 搜索筛选区 -->
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="学生">
          <el-select
            v-model="searchForm.studentId"
            placeholder="请选择学生"
            clearable
            filterable
            style="width: 200px"
          >
            <el-option
              v-for="student in students"
              :key="student.userId"
              :label="`${student.realName} (${student.account})`"
              :value="student.userId"
            />
          </el-select>
        </el-form-item>

        <el-form-item label="课程">
          <el-select
            v-model="searchForm.courseId"
            placeholder="请选择课程"
            clearable
            filterable
            style="width: 200px"
          >
            <el-option
              v-for="course in courses"
              :key="course.courseId"
              :label="course.courseName"
              :value="course.courseId"
            />
          </el-select>
        </el-form-item>

        <el-form-item>
          <el-button type="primary" @click="loadEnrollments">查询</el-button>
          <el-button @click="resetSearch">重置</el-button>
        </el-form-item>
      </el-form>

      <!-- 选课记录表格 -->
      <el-table
        :data="enrollments"
        v-loading="loading"
        border
        stripe
      >
        <el-table-column prop="enrollmentId" label="选课ID" width="80" />
        <el-table-column label="学生信息" width="200">
          <template #default="{ row }">
            <div>{{ row.student?.realName }}</div>
            <div style="color: #909399; font-size: 12px">{{ row.student?.account }}</div>
          </template>
        </el-table-column>
        <el-table-column label="课程信息" min-width="200">
          <template #default="{ row }">
            <div>{{ row.course?.courseName }}</div>
            <div style="color: #909399; font-size: 12px">
              学分: {{ row.course?.credit }} | 教师: {{ row.course?.teacherName }}
            </div>
          </template>
        </el-table-column>
        <el-table-column label="成绩" width="200">
          <template #default="{ row }">
            <div v-if="row.grade !== null && row.grade !== undefined">
              <el-tag type="success">总评: {{ row.grade }}</el-tag>
            </div>
            <div v-else style="color: #909399">未录入</div>
            <div style="font-size: 12px; margin-top: 4px">
              <span v-if="row.usualGrade">平时: {{ row.usualGrade }}</span>
              <span v-if="row.finalGrade" style="margin-left: 8px">期末: {{ row.finalGrade }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="选课时间" width="180">
          <template #default="{ row }">
            {{ formatDate(row.createTime) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="120" fixed="right">
          <template #default="{ row }">
            <el-popconfirm
              title="确定要帮该学生退课吗?"
              @confirm="handleDropCourse(row.enrollmentId)"
            >
              <template #reference>
                <el-button type="danger" size="small" link>
                  <el-icon><Delete /></el-icon>
                  退课
                </el-button>
              </template>
            </el-popconfirm>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <el-pagination
        v-model:current-page="pagination.current"
        v-model:page-size="pagination.size"
        :total="pagination.total"
        :page-sizes="[10, 20, 50, 100]"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="loadEnrollments"
        @current-change="loadEnrollments"
        style="margin-top: 20px; justify-content: flex-end"
      />
    </el-card>

    <!-- 代学生选课对话框 -->
    <el-dialog
      v-model="addDialogVisible"
      title="代学生选课"
      width="500px"
    >
      <el-form
        ref="addFormRef"
        :model="addForm"
        :rules="addRules"
        label-width="100px"
      >
        <el-form-item label="学生" prop="studentId">
          <el-select
            v-model="addForm.studentId"
            placeholder="请选择学生"
            filterable
            style="width: 100%"
          >
            <el-option
              v-for="student in students"
              :key="student.userId"
              :label="`${student.realName} (${student.account})`"
              :value="student.userId"
            />
          </el-select>
        </el-form-item>

        <el-form-item label="课程" prop="courseId">
          <el-select
            v-model="addForm.courseId"
            placeholder="请选择课程"
            filterable
            style="width: 100%"
            @change="checkConflict"
          >
            <el-option
              v-for="course in availableCourses"
              :key="course.courseId"
              :label="`${course.courseName} (${course.teacherName})`"
              :value="course.courseId"
            />
          </el-select>
        </el-form-item>

        <el-alert
          v-if="conflictMessage"
          :title="conflictMessage"
          :type="conflictType"
          :closable="false"
          style="margin-bottom: 15px"
        />
      </el-form>

      <template #footer>
        <el-button @click="addDialogVisible = false">取消</el-button>
        <el-button
          type="primary"
          @click="handleAddEnrollment"
          :loading="submitting"
          :disabled="hasConflict"
        >
          确定选课
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { ElMessage } from 'element-plus'
import { Plus, Delete } from '@element-plus/icons-vue'
import request from '@/utils/request'

const loading = ref(false)
const submitting = ref(false)
const enrollments = ref([])
const students = ref([])
const courses = ref([])
const addDialogVisible = ref(false)
const addFormRef = ref(null)
const conflictMessage = ref('')
const conflictType = ref('info')
const hasConflict = ref(false)

const searchForm = reactive({
  studentId: null,
  courseId: null
})

const pagination = reactive({
  current: 1,
  size: 10,
  total: 0
})

const addForm = reactive({
  studentId: null,
  courseId: null
})

const addRules = {
  studentId: [{ required: true, message: '请选择学生', trigger: 'change' }],
  courseId: [{ required: true, message: '请选择课程', trigger: 'change' }]
}

// 可选课程(已发布的课程)
const availableCourses = computed(() => {
  return courses.value.filter(c => c.status === 1)
})

const loadEnrollments = async () => {
  loading.value = true
  try {
    const res = await request({
      url: '/admin/student-courses/page',
      method: 'get',
      params: {
        current: pagination.current,
        size: pagination.size,
        studentId: searchForm.studentId,
        courseId: searchForm.courseId
      }
    })
    enrollments.value = res.data.records || []
    pagination.total = res.data.total || 0
  } catch (error) {
    console.error('加载选课记录失败:', error)
    ElMessage.error('加载选课记录失败')
  } finally {
    loading.value = false
  }
}

const loadStudents = async () => {
  try {
    const res = await request({
      url: '/user',
      method: 'get',
      params: {
        pageNum: 1,
        pageSize: 100
      }
    })
    // 只保留拥有student角色的用户
    const allUsers = res.data.records || []
    students.value = allUsers.filter(user =>
      user.roles && user.roles.includes('student')
    )
  } catch (error) {
    console.error('加载学生列表失败:', error)
  }
}

const loadCourses = async () => {
  try {
    const res = await request({
      url: '/courses',
      method: 'get',
      params: {
        pageNum: 1,
        pageSize: 100
      }
    })
    courses.value = res.data.records || []
  } catch (error) {
    console.error('加载课程列表失败:', error)
  }
}

const showAddDialog = () => {
  addForm.studentId = null
  addForm.courseId = null
  conflictMessage.value = ''
  hasConflict.value = false
  addDialogVisible.value = true
}

const checkConflict = async () => {
  if (!addForm.studentId || !addForm.courseId) {
    conflictMessage.value = ''
    return
  }

  try {
    // 检查是否已选
    const selectedRes = await request({
      url: `/admin/student-courses/check-selected/${addForm.courseId}`,
      method: 'get',
      params: { studentId: addForm.studentId }
    })

    if (selectedRes.data) {
      conflictMessage.value = '该学生已选择此课程'
      conflictType.value = 'warning'
      hasConflict.value = true
      return
    }

    // 检查时间冲突
    const conflictRes = await request({
      url: '/admin/student-courses/check-conflict',
      method: 'get',
      params: {
        courseId: addForm.courseId,
        studentId: addForm.studentId
      }
    })

    if (conflictRes.data) {
      conflictMessage.value = '该课程与学生已选课程时间冲突'
      conflictType.value = 'error'
      hasConflict.value = true
    } else {
      conflictMessage.value = '可以选课,无冲突'
      conflictType.value = 'success'
      hasConflict.value = false
    }
  } catch (error) {
    console.error('检查冲突失败:', error)
  }
}

const handleAddEnrollment = async () => {
  if (!addFormRef.value) return

  await addFormRef.value.validate(async (valid) => {
    if (!valid) return

    submitting.value = true
    try {
      await request({
        url: '/admin/student-courses/select',
        method: 'post',
        data: addForm
      })
      ElMessage.success('选课成功')
      addDialogVisible.value = false
      loadEnrollments()
    } catch (error) {
      console.error('选课失败:', error)
      ElMessage.error(error.response?.data?.message || '选课失败')
    } finally {
      submitting.value = false
    }
  })
}

const handleDropCourse = async (enrollmentId) => {
  try {
    await request({
      url: `/admin/student-courses/drop/${enrollmentId}`,
      method: 'delete'
    })
    ElMessage.success('退课成功')
    loadEnrollments()
  } catch (error) {
    console.error('退课失败:', error)
    ElMessage.error(error.response?.data?.message || '退课失败')
  }
}

const resetSearch = () => {
  searchForm.studentId = null
  searchForm.courseId = null
  pagination.current = 1
  loadEnrollments()
}

const formatDate = (dateStr) => {
  if (!dateStr) return '-'
  return new Date(dateStr).toLocaleString('zh-CN')
}

onMounted(() => {
  loadEnrollments()
  loadStudents()
  loadCourses()
})
</script>

<style scoped>
.student-enrollment-management {
  padding: 0;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.search-form {
  margin-bottom: 20px;
}
</style>
