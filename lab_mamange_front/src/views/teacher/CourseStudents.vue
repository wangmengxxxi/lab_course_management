<template>
  <div class="course-students">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>{{ courseName }} - 选课学生</span>
          <el-button @click="$router.back()">返回</el-button>
        </div>
      </template>

      <!-- 搜索 -->
      <el-form :inline="true" style="margin-bottom: 20px">
        <el-form-item label="学生姓名">
          <el-input
            v-model="searchName"
            placeholder="请输入学生姓名"
            clearable
            style="width: 200px"
            @clear="loadData"
          />
        </el-form-item>
        <el-form-item label="排序">
          <el-select v-model="sortBy" placeholder="排序字段" style="width: 150px" @change="loadData">
            <el-option label="总成绩" value="grade" />
            <el-option label="平时成绩" value="usual_grade" />
            <el-option label="期末成绩" value="final_grade" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-select v-model="sortOrder" placeholder="排序方式" style="width: 100px" @change="loadData">
            <el-option label="降序" value="desc" />
            <el-option label="升序" value="asc" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="loadData">
            <el-icon><Search /></el-icon>
            搜索
          </el-button>
        </el-form-item>
      </el-form>

      <!-- 学生列表 -->
      <el-table
        v-loading="loading"
        :data="tableData"
        border
        style="width: 100%"
      >
        <el-table-column prop="studentNumber" label="学号" width="150" />
        <el-table-column prop="studentName" label="姓名" width="120" />
        <el-table-column prop="usualGrade" label="平时成绩" width="120">
          <template #default="{ row }">
            {{ row.usualGrade || '-' }}
          </template>
        </el-table-column>
        <el-table-column prop="finalGrade" label="期末成绩" width="120">
          <template #default="{ row }">
            {{ row.finalGrade || '-' }}
          </template>
        </el-table-column>
        <el-table-column prop="grade" label="总成绩" width="120">
          <template #default="{ row }">
            <span :class="getGradeClass(row.grade)">
              {{ row.grade || '-' }}
            </span>
          </template>
        </el-table-column>
        <el-table-column prop="gradeLevel" label="等级" width="100" />
        <el-table-column prop="enrollmentTime" label="选课时间" width="180" />
      </el-table>

      <!-- 分页 -->
      <el-pagination
        v-model:current-page="pagination.pageNum"
        v-model:page-size="pagination.pageSize"
        :total="pagination.total"
        :page-sizes="[10, 20, 50, 100]"
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
import { useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Search } from '@element-plus/icons-vue'
import request from '@/utils/request'

const route = useRoute()
const loading = ref(false)
const tableData = ref([])
const searchName = ref('')
const sortBy = ref('grade')
const sortOrder = ref('desc')
const courseName = ref(route.query.courseName || '课程')
const courseId = ref(route.params.courseId)

const pagination = reactive({
  pageNum: 1,
  pageSize: 10,
  total: 0
})

const loadData = async () => {
  loading.value = true
  try {
    const res = await request({
      url: '/admin/student-courses/grades',
      method: 'get',
      params: {
        courseId: courseId.value,
        pageNum: pagination.pageNum,
        pageSize: pagination.pageSize,
        studentName: searchName.value || undefined,
        sortBy: sortBy.value,
        sortOrder: sortOrder.value
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

const getGradeClass = (grade) => {
  if (!grade) return ''
  if (grade >= 90) return 'grade-excellent'
  if (grade >= 80) return 'grade-good'
  if (grade >= 70) return 'grade-medium'
  if (grade >= 60) return 'grade-pass'
  return 'grade-fail'
}

onMounted(() => {
  loadData()
})
</script>

<style scoped>
.course-students {
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
</style>
