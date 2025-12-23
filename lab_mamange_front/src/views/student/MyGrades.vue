<template>
  <div class="my-grades">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>我的成绩</span>
        </div>
      </template>

      <!-- 统计信息 -->
      <el-row :gutter="20" style="margin-bottom: 20px">
        <el-col :span="8">
          <el-statistic title="已修学分" :value="totalCredits" />
        </el-col>
        <el-col :span="8">
          <el-statistic title="平均成绩" :value="averageGrade" :precision="2" />
        </el-col>
        <el-col :span="8">
          <el-statistic title="已完成课程" :value="completedCourses" />
        </el-col>
      </el-row>

      <!-- 成绩列表 -->
      <el-table
        v-loading="loading"
        :data="tableData"
        border
        style="width: 100%"
      >
        <el-table-column prop="courseName" label="课程名称" width="200" />
        <el-table-column prop="teacherName" label="教师" width="120" />
        <el-table-column prop="credit" label="学分" width="80" />
        <el-table-column prop="usualGrade" label="平时成绩" width="100">
          <template #default="{ row }">
            {{ row.usualGrade || '-' }}
          </template>
        </el-table-column>
        <el-table-column prop="finalGrade" label="期末成绩" width="100">
          <template #default="{ row }">
            {{ row.finalGrade || '-' }}
          </template>
        </el-table-column>
        <el-table-column prop="grade" label="总成绩" width="100">
          <template #default="{ row }">
            <span :class="getGradeClass(row.grade)">
              {{ row.grade || '-' }}
            </span>
          </template>
        </el-table-column>
        <el-table-column prop="semester" label="学期" width="120" />
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag v-if="row.grade >= 60" type="success">通过</el-tag>
            <el-tag v-else-if="row.grade" type="danger">未通过</el-tag>
            <el-tag v-else type="info">未录入</el-tag>
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
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import request from '@/utils/request'

const loading = ref(false)
const tableData = ref([])

const pagination = reactive({
  pageNum: 1,
  pageSize: 10,
  total: 0
})

const totalCredits = computed(() => {
  return tableData.value
    .filter(course => course.grade && course.grade >= 60)
    .reduce((sum, course) => sum + (course.credit || 0), 0)
})

const averageGrade = computed(() => {
  const gradesWithScore = tableData.value.filter(course => course.grade)
  if (gradesWithScore.length === 0) return 0
  
  const sum = gradesWithScore.reduce((total, course) => total + course.grade, 0)
  return sum / gradesWithScore.length
})

const completedCourses = computed(() => {
  return tableData.value.filter(course => course.grade && course.grade >= 60).length
})

const loadData = async () => {
  loading.value = true
  try {
    const res = await request({
      url: '/enrollments/my',
      method: 'get',
      params: {
        pageNum: pagination.pageNum,
        pageSize: pagination.pageSize
      }
    })
    tableData.value = res.data.records || []
    pagination.total = res.data.total || 0
  } catch (error) {
    console.error('加载成绩失败:', error)
    ElMessage.error('加载成绩失败')
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
.my-grades {
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
