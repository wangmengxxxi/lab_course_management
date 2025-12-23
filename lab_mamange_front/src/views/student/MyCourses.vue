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
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="courseName" label="课程名称" width="200" />
        <el-table-column prop="teacherName" label="教师" width="120" />
        <el-table-column prop="credit" label="学分" width="80" />
        <el-table-column label="上课时间" width="180">
          <template #default="{ row }">
            <div v-if="row.scheduleInfo">
              {{ row.scheduleInfo.dayOfWeekText }} {{ row.scheduleInfo.timeSlotName }}
            </div>
            <span v-else>-</span>
          </template>
        </el-table-column>
        <el-table-column label="上课地点" width="150">
          <template #default="{ row }">
            {{ row.scheduleInfo?.labName || '-' }}
          </template>
        </el-table-column>
        <el-table-column label="周次" width="120">
          <template #default="{ row }">
            <span v-if="row.scheduleInfo">
              {{ row.scheduleInfo.startWeek }}-{{ row.scheduleInfo.endWeek }}周
            </span>
            <span v-else>-</span>
          </template>
        </el-table-column>
        <el-table-column prop="semester" label="学期" width="120" />
        <el-table-column label="操作" fixed="right" width="100">
          <template #default="{ row }">
            <el-button
              type="danger"
              size="small"
              @click="handleDrop(row)"
            >
              退课
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
import { ElMessage, ElMessageBox } from 'element-plus'
import request from '@/utils/request'

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
    console.error('加载我的课程失败:', error)
    ElMessage.error('加载课程失败')
  } finally {
    loading.value = false
  }
}

const handleDrop = (course) => {
  ElMessageBox.confirm(
    `确定要退选《${course.courseName}》吗?`,
    '确认退课',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(async () => {
    try {
      await request({
        url: `/enrollments/${course.id}`,
        method: 'delete'
      })
      
      ElMessage.success('退课成功')
      loadData()
    } catch (error) {
      console.error('退课失败:', error)
      ElMessage.error(error.response?.data?.message || '退课失败')
    }
  }).catch(() => {
    // 取消退课
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
</style>
