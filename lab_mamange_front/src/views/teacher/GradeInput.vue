<template>
  <div class="grade-input">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>成绩录入</span>
        </div>
      </template>

      <!-- 课程选择 -->
      <el-form :inline="true" style="margin-bottom: 20px">
        <el-form-item label="选择课程">
          <el-select
            v-model="selectedCourseId"
            placeholder="请选择课程"
            style="width: 300px"
            @change="handleCourseChange"
          >
            <el-option
              v-for="course in courses"
              :key="course.courseId"
              :label="`${course.courseName} (${course.semester})`"
              :value="course.courseId"
            />
          </el-select>
        </el-form-item>
      </el-form>

      <!-- 学生成绩列表 -->
      <el-table
        v-if="selectedCourseId"
        v-loading="loading"
        :data="tableData"
        border
        style="width: 100%"
      >
        <el-table-column prop="studentNumber" label="学号" width="150" />
        <el-table-column prop="studentName" label="姓名" width="120" />
        <el-table-column label="平时成绩" width="150">
          <template #default="{ row }">
            <el-input-number
              v-model="row.usualGrade"
              :min="0"
              :max="100"
              :precision="1"
              :controls="false"
              placeholder="0-100"
              @change="calculateGrade(row)"
            />
          </template>
        </el-table-column>
        <el-table-column label="期末成绩" width="150">
          <template #default="{ row }">
            <el-input-number
              v-model="row.finalGrade"
              :min="0"
              :max="100"
              :precision="1"
              :controls="false"
              placeholder="0-100"
              @change="calculateGrade(row)"
            />
          </template>
        </el-table-column>
        <el-table-column prop="grade" label="总成绩" width="120">
          <template #default="{ row }">
            <span :class="getGradeClass(row.grade)">
              {{ row.grade ? row.grade.toFixed(1) : '-' }}
            </span>
          </template>
        </el-table-column>
        <el-table-column prop="gradeLevel" label="等级" width="100" />
        <el-table-column label="操作" fixed="right" width="100">
          <template #default="{ row }">
            <el-button
              type="primary"
              size="small"
              :disabled="!row.usualGrade && !row.finalGrade"
              @click="saveGrade(row)"
            >
              保存
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 批量操作 -->
      <div v-if="selectedCourseId && tableData.length > 0" style="margin-top: 20px">
        <el-button type="success" @click="saveAllGrades">
          批量保存所有成绩
        </el-button>
      </div>

      <!-- 分页 -->
      <el-pagination
        v-if="selectedCourseId"
        v-model:current-page="pagination.pageNum"
        v-model:page-size="pagination.pageSize"
        :total="pagination.total"
        :page-sizes="[10, 20, 50, 100]"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="loadStudents"
        @current-change="loadStudents"
        style="margin-top: 20px; justify-content: flex-end"
      />
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useAuthStore } from '@/stores/auth'
import request from '@/utils/request'

const route = useRoute()
const authStore = useAuthStore()
const loading = ref(false)
const courses = ref([])
const selectedCourseId = ref(route.query.courseId ? Number(route.query.courseId) : null)
const tableData = ref([])

const pagination = reactive({
  pageNum: 1,
  pageSize: 10,
  total: 0
})

const loadCourses = async () => {
  try {
    const res = await request({
      url: '/courses',
      method: 'get',
      params: {
        pageNum: 1,
        pageSize: 100,
        teacherId: authStore.userInfo.userId
      }
    })
    courses.value = res.data.records || []
  } catch (error) {
    console.error('加载课程列表失败:', error)
    ElMessage.error('加载课程列表失败')
  }
}

const loadStudents = async () => {
  if (!selectedCourseId.value) return
  
  loading.value = true
  try {
    const res = await request({
      url: '/admin/student-courses/grades',
      method: 'get',
      params: {
        courseId: selectedCourseId.value,
        pageNum: pagination.pageNum,
        pageSize: pagination.pageSize,
        sortBy: 'grade',
        sortOrder: 'desc'
      }
    })
    tableData.value = res.data.records || []
    pagination.total = res.data.total || 0
  } catch (error) {
    console.error('加载学生列表失败:', error)
    ElMessage.error('加载学生列表失败')
  } finally {
    loading.value = false
  }
}

const handleCourseChange = () => {
  pagination.pageNum = 1
  loadStudents()
}

const calculateGrade = (row) => {
  if (row.usualGrade !== null && row.usualGrade !== undefined && 
      row.finalGrade !== null && row.finalGrade !== undefined) {
    // 平时成绩30% + 期末成绩70%
    row.grade = row.usualGrade * 0.3 + row.finalGrade * 0.7
  } else {
    row.grade = null
  }
}

const saveGrade = async (row) => {
  if (!row.usualGrade && !row.finalGrade) {
    ElMessage.warning('请至少输入一项成绩')
    return
  }

  try {
    await request({
      url: `/admin/student-courses/grade/${selectedCourseId.value}/${row.studentId}`,
      method: 'post',
      params: {
        usualGrade: row.usualGrade,
        finalGrade: row.finalGrade
      }
    })
    ElMessage.success('成绩保存成功')
    loadStudents() // 刷新列表
  } catch (error) {
    console.error('保存成绩失败:', error)
    ElMessage.error(error.response?.data?.message || '保存成绩失败')
  }
}

const saveAllGrades = async () => {
  const studentsWithGrades = tableData.value.filter(row => 
    (row.usualGrade !== null && row.usualGrade !== undefined) || 
    (row.finalGrade !== null && row.finalGrade !== undefined)
  )

  if (studentsWithGrades.length === 0) {
    ElMessage.warning('没有需要保存的成绩')
    return
  }

  ElMessageBox.confirm(
    `确定要保存 ${studentsWithGrades.length} 个学生的成绩吗?`,
    '批量保存',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(async () => {
    let successCount = 0
    let failCount = 0

    for (const row of studentsWithGrades) {
      try {
        await request({
          url: `/admin/student-courses/grade/${selectedCourseId.value}/${row.studentId}`,
          method: 'post',
          params: {
            usualGrade: row.usualGrade,
            finalGrade: row.finalGrade
          }
        })
        successCount++
      } catch (error) {
        console.error('保存成绩失败:', error)
        failCount++
      }
    }

    if (failCount === 0) {
      ElMessage.success(`成功保存 ${successCount} 个学生的成绩`)
    } else {
      ElMessage.warning(`成功 ${successCount} 个,失败 ${failCount} 个`)
    }
    
    loadStudents() // 刷新列表
  }).catch(() => {
    // 取消操作
  })
}

const getGradeClass = (grade) => {
  if (!grade) return ''
  if (grade >= 90) return 'grade-excellent'
  if (grade >= 80) return 'grade-good'
  if (grade >= 70) return 'grade-medium'
  if (grade >= 60) return 'grade-pass'
  return 'grade-fail'
}

onMounted(() => {
  loadCourses()
  if (selectedCourseId.value) {
    loadStudents()
  }
})
</script>

<style scoped>
.grade-input {
  padding: 0;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.grade-excellent {
  color: #67c23a;
  font-weight: bold;
}

.grade-good {
  color: #409eff;
  font-weight: bold;
}

.grade-medium {
  color: #e6a23c;
}

.grade-pass {
  color: #909399;
}

.grade-fail {
  color: #f56c6c;
  font-weight: bold;
}

:deep(.el-input-number) {
  width: 120px;
}

:deep(.el-input-number .el-input__inner) {
  text-align: left;
}
</style>
