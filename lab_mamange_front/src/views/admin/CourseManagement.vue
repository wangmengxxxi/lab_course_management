<template>
  <div class="course-management">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>课程管理</span>
        </div>
      </template>

      <!-- 状态筛选 -->
      <el-radio-group v-model="statusFilter" @change="loadData" style="margin-bottom: 20px">
        <el-radio-button label="">全部</el-radio-button>
        <el-radio-button :label="0">待审批</el-radio-button>
        <el-radio-button :label="1">已通过</el-radio-button>
        <el-radio-button :label="2">已拒绝</el-radio-button>
      </el-radio-group>

      <!-- 数据表格 -->
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
        <el-table-column prop="maxStudents" label="最大人数" width="100" />
        <el-table-column prop="description" label="描述" min-width="200" show-overflow-tooltip />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag v-if="row.status === 0" type="warning">待审批</el-tag>
            <el-tag v-else-if="row.status === 1" type="success">已通过</el-tag>
            <el-tag v-else type="danger">已拒绝</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" fixed="right" width="220">
          <template #default="{ row }">
            <el-button
              v-if="row.status === 0"
              type="success"
              size="small"
              @click="handleApprove(row)"
            >
              审批排课
            </el-button>
            <el-button
              v-if="row.status === 0"
              type="warning"
              size="small"
              @click="handleReject(row)"
            >
              拒绝
            </el-button>
            <el-button type="danger" size="small" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
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

    <!-- 审批排课对话框 -->
    <el-dialog
      v-model="dialogVisible"
      title="审批排课"
      width="600px"
      @close="handleDialogClose"
    >
      <el-form
        ref="formRef"
        :model="formData"
        :rules="formRules"
        label-width="100px"
      >
        <el-form-item label="课程名称">
          <el-input v-model="currentCourse.courseName" disabled />
        </el-form-item>
        <el-form-item label="实验室" prop="labId">
          <el-select v-model="formData.labId" placeholder="请选择实验室" style="width: 100%" filterable>
            <el-option
              v-for="lab in labList"
              :key="lab.labId"
              :label="`${lab.labName} (容量:${lab.capacity})`"
              :value="lab.labId"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="星期" prop="dayOfWeek">
          <el-select v-model="formData.dayOfWeek" placeholder="请选择星期" style="width: 100%">
            <el-option label="星期一" :value="1" />
            <el-option label="星期二" :value="2" />
            <el-option label="星期三" :value="3" />
            <el-option label="星期四" :value="4" />
            <el-option label="星期五" :value="5" />
            <el-option label="星期六" :value="6" />
            <el-option label="星期日" :value="7" />
          </el-select>
        </el-form-item>
        <el-form-item label="节次" prop="slotId">
          <el-select v-model="formData.slotId" placeholder="请选择节次" style="width: 100%">
            <el-option
              v-for="slot in timeSlots"
              :key="slot.slotId"
              :label="slot.slotName"
              :value="slot.slotId"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="起始周" prop="startWeek">
          <el-input-number v-model="formData.startWeek" :min="1" :max="20" />
        </el-form-item>
        <el-form-item label="结束周" prop="endWeek">
          <el-input-number v-model="formData.endWeek" :min="1" :max="20" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit" :loading="submitLoading">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import request from '@/utils/request'

const loading = ref(false)
const submitLoading = ref(false)
const tableData = ref([])
const dialogVisible = ref(false)
const formRef = ref(null)
const statusFilter = ref('')
const labList = ref([])
const timeSlots = ref([])
const currentCourse = ref({})

const pagination = reactive({
  pageNum: 1,
  pageSize: 10,
  total: 0
})

const formData = reactive({
  labId: null,
  dayOfWeek: null,
  slotId: null,
  startWeek: 1,
  endWeek: 16,
  teacherId: null
})

const formRules = {
  labId: [{ required: true, message: '请选择实验室', trigger: 'change' }],
  dayOfWeek: [{ required: true, message: '请选择星期', trigger: 'change' }],
  slotId: [{ required: true, message: '请选择节次', trigger: 'change' }],
  startWeek: [{ required: true, message: '请输入起始周', trigger: 'blur' }],
  endWeek: [{ required: true, message: '请输入结束周', trigger: 'blur' }]
}

const loadData = async () => {
  loading.value = true
  try {
    const res = await request({
      url: '/courses',
      method: 'get',
      params: {
        pageNum: pagination.pageNum,
        pageSize: pagination.pageSize,
        status: statusFilter.value
      }
    })
    tableData.value = res.data.records || []
    pagination.total = res.data.total || 0
  } catch (error) {
    console.error('加载数据失败:', error)
  } finally {
    loading.value = false
  }
}

const loadLabList = async () => {
  try {
    const res = await request({
      url: '/labs',
      method: 'get',
      params: { pageNum: 1, pageSize: 100 }
    })
    labList.value = res.data.records || []
  } catch (error) {
    console.error('加载实验室列表失败:', error)
  }
}

const loadTimeSlots = async () => {
  try {
    const res = await request({
      url: '/api/class-time-slots/list',
      method: 'get'
    })
    timeSlots.value = res.data || []
  } catch (error) {
    console.error('加载时间段列表失败:', error)
  }
}

const handleApprove = (row) => {
  currentCourse.value = row
  formData.teacherId = row.teacherId
  dialogVisible.value = true
}

const handleReject = (row) => {
  ElMessageBox.confirm('确定要拒绝该课程吗?', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await request({
        url: `/courses/${row.courseId}/reject`,
        method: 'put'
      })
      ElMessage.success('已拒绝')
      loadData()
    } catch (error) {
      console.error('拒绝失败:', error)
      ElMessage.error(error.response?.data?.message || '拒绝失败')
    }
  })
}

const handleDelete = (row) => {
  ElMessageBox.confirm('确定要删除该课程吗?', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await request({
        url: `/courses/${row.courseId}`,
        method: 'delete'
      })
      ElMessage.success('删除成功')
      loadData()
    } catch (error) {
      console.error('删除失败:', error)
      ElMessage.error(error.response?.data?.message || '删除失败')
    }
  })
}

const handleSubmit = async () => {
  if (!formRef.value) return
  
  await formRef.value.validate(async (valid) => {
    if (!valid) return
    
    if (formData.startWeek > formData.endWeek) {
      ElMessage.error('起始周不能大于结束周')
      return
    }
    
    submitLoading.value = true
    try {
      await request({
        url: `/courses/${currentCourse.value.courseId}/approve`,
        method: 'put',
        data: formData
      })
      
      ElMessage.success('审批排课成功')
      dialogVisible.value = false
      loadData()
    } catch (error) {
      console.error('提交失败:', error)
      if (error.response?.data?.message) {
        ElMessage.error(error.response.data.message)
      }
    } finally {
      submitLoading.value = false
    }
  })
}

const handleDialogClose = () => {
  formRef.value?.resetFields()
  Object.assign(formData, {
    labId: null,
    dayOfWeek: null,
    slotId: null,
    startWeek: 1,
    endWeek: 16,
    teacherId: null
  })
}

onMounted(() => {
  loadData()
  loadLabList()
  loadTimeSlots()
})
</script>

<style scoped>
.course-management {
  padding: 0;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>
