<template>
  <div class="announcements">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>公告通知</span>
        </div>
      </template>

      <!-- 公告列表 -->
      <el-timeline>
        <el-timeline-item
          v-for="announcement in tableData"
          :key="announcement.id"
          :timestamp="announcement.publishTime"
          placement="top"
        >
          <el-card>
            <h4>{{ announcement.title }}</h4>
            <p class="announcement-content">{{ announcement.content }}</p>
          </el-card>
        </el-timeline-item>
      </el-timeline>

      <!-- 空状态 -->
      <el-empty v-if="!loading && tableData.length === 0" description="暂无公告" />

      <!-- 分页 -->
      <el-pagination
        v-if="tableData.length > 0"
        v-model:current-page="pagination.current"
        v-model:page-size="pagination.size"
        :total="pagination.total"
        :page-sizes="[5, 10, 20]"
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
import { ElMessage } from 'element-plus'
import request from '@/utils/request'

const loading = ref(false)
const tableData = ref([])

const pagination = reactive({
  current: 1,
  size: 10,
  total: 0
})

const loadData = async () => {
  loading.value = true
  try {
    const res = await request({
      url: '/announcement/page',
      method: 'get',
      params: {
        current: pagination.current,
        size: pagination.size,
        status: 1  // 只显示已发布的公告
      }
    })
    tableData.value = res.data.records || []
    pagination.total = res.data.total || 0
  } catch (error) {
    console.error('加载公告失败:', error)
    ElMessage.error('加载公告失败')
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  loadData()
})
</script>

<style scoped>
.announcements {
  padding: 0;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.announcement-content {
  margin-top: 10px;
  color: #606266;
  line-height: 1.6;
  white-space: pre-wrap;
}

:deep(.el-timeline-item__timestamp) {
  color: #909399;
  font-size: 13px;
}
</style>
