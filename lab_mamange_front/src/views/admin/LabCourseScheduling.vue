<template>
  <div class="lab-course-scheduling">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>实验室排课管理</span>
          <el-button type="primary" @click="handleAdd">
            <el-icon><Plus /></el-icon>
            新增排课
          </el-button>
        </div>
      </template>

      <!-- 筛选条件 -->
      <el-form :inline="true" style="margin-bottom: 20px">
        <el-form-item label="实验室">
          <el-select 
            v-model="filterLabId" 
            placeholder="选择实验室" 
            clearable 
            filterable
            @change="loadData"
            style="width: 200px"
          >
            <el-option
              v-for="lab in labList"
              :key="lab.labId"
              :label="lab.labName"
              :value="lab.labId"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="课程">
          <el-select 
            v-model="filterCourseId" 
            placeholder="选择课程" 
            clearable 
            filterable
            @change="loadData"
            style="width: 200px"
          >
            <el-option
              v-for="course in courseList"
              :key="course.courseId"
              :label="course.courseName"
              :value="course.courseId"
            />
          </el-select>
        </el-form-item>
      </el-form>

      <!-- 数据表格 -->
      <el-table
        v-loading="loading"
        :data="tableData"
        border
        style="width: 100%"
      >
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column label="课程" width="200">
          <template #default="{ row }">
            {{ row.course?.courseName || '-' }}
          </template>
        </el-table-column>
        <el-table-column label="实验室" width="150">
          <template #default="{ row }">
            {{ row.laboratory?.labName || '-' }}
          </template>
        </el-table-column>
        <el-table-column label="教师" width="120">
          <template #default="{ row }">
            {{ row.course?.teacherName || '-' }}
          </template>
        </el-table-column>
        <el-table-column prop="dayOfWeek" label="星期" width="100">
          <template #default="{ row }">
            {{ row.dayOfWeekText || `星期${['一', '二', '三', '四', '五', '六', '日'][row.dayOfWeek - 1]}` }}
          </template>
        </el-table-column>
        <el-table-column label="节次" width="180">
          <template #default="{ row }">
            {{ row.timeSlot?.slotName || getSlotName(row.timeSlot?.slotId) }}
          </template>
        </el-table-column>
        <el-table-column label="周次" width="120">
          <template #default="{ row }">
            {{ row.startWeek }}-{{ row.endWeek }}周
          </template>
        </el-table-column>
        <el-table-column label="操作" fixed="right" width="180">
          <template #default="{ row }">
            <el-button type="primary" size="small" @click="handleEdit(row)">编辑</el-button>
            <el-button type="danger" size="small" @click="handleDelete(row)">删除</el-button>
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
        @size-change="loadData"
        @current-change="loadData"
        style="margin-top: 20px; justify-content: flex-end"
      />
    </el-card>

    <!-- 新增/编辑对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="600px"
      @close="handleDialogClose"
    >
      <el-form
        ref="formRef"
        :model="formData"
        :rules="formRules"
        label-width="100px"
      >
        <el-form-item label="课程" prop="courseId">
          <el-select v-model="formData.courseId" placeholder="请选择课程" style="width: 100%">
            <el-option
              v-for="course in courseList"
              :key="course.courseId"
              :label="course.courseName"
              :value="course.courseId"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="实验室" prop="labId">
          <el-select v-model="formData.labId" placeholder="请选择实验室" style="width: 100%">
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
            <el-option label="第1-2节 (08:00-09:40)" :value="1" />
            <el-option label="第3-4节 (10:00-11:40)" :value="2" />
            <el-option label="第5-6节 (14:00-15:40)" :value="3" />
            <el-option label="第7-8节 (16:00-17:40)" :value="4" />
            <el-option label="第9-10节 (19:00-20:40)" :value="5" />
          </el-select>
        </el-form-item>
        <el-form-item label="起始周" prop="startWeek">
          <el-input-number v-model="formData.startWeek" :min="1" :max="20" />
        </el-form-item>
        <el-form-item label="结束周" prop="endWeek">
          <el-input-number v-model="formData.endWeek" :min="1" :max="20" />
        </el-form-item>
        <el-form-item>
          <el-button @click="checkConflict" :loading="checkingConflict">检测冲突</el-button>
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
import { Plus } from '@element-plus/icons-vue'
import request from '@/utils/request'

const loading = ref(false)
const submitLoading = ref(false)
const checkingConflict = ref(false)
const tableData = ref([])
const dialogVisible = ref(false)
const isEdit = ref(false)
const formRef = ref(null)
const labList = ref([])
const courseList = ref([])
const filterLabId = ref(null)
const filterCourseId = ref(null)

const pagination = reactive({
  current: 1,
  size: 10,
  total: 0
})

const formData = reactive({
  courseId: null,
  labId: null,
  dayOfWeek: null,
  slotId: null,
  startWeek: 1,
  endWeek: 16
})

const formRules = {
  courseId: [{ required: true, message: '请选择课程', trigger: 'change' }],
  labId: [{ required: true, message: '请选择实验室', trigger: 'change' }],
  dayOfWeek: [{ required: true, message: '请选择星期', trigger: 'change' }],
  slotId: [{ required: true, message: '请选择节次', trigger: 'change' }],
  startWeek: [{ required: true, message: '请输入起始周', trigger: 'blur' }],
  endWeek: [{ required: true, message: '请输入结束周', trigger: 'blur' }]
}

const dialogTitle = ref('新增排课')
const teacherMap = ref({})

const getTeacherName = (teacherId) => {
  if (!teacherId) return '-'
  return teacherMap.value[teacherId] || '-'
}

const getSlotName = (slotId) => {
  const slots = {
    1: '第1-2节 (08:00-09:40)',
    2: '第3-4节 (10:00-11:40)',
    3: '第5-6节 (14:00-15:40)',
    4: '第7-8节 (16:00-17:40)',
    5: '第9-10节 (19:00-20:40)'
  }
  return slots[slotId] || ''
}

const loadData = async () => {
  loading.value = true
  try {
    const res = await request({
      url: '/lab-course/page',
      method: 'get',
      params: {
        current: pagination.current,
        size: pagination.size,
        labId: filterLabId.value,
        courseId: filterCourseId.value
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

const loadCourseList = async () => {
  try {
    const res = await request({
      url: '/courses',
      method: 'get',
      params: { 
        pageNum: 1, 
        pageSize: 100,
        status: 1  // 只加载已通过的课程
      }
    })
    courseList.value = res.data.records || []
    
    // 构建教师ID到姓名的映射
    courseList.value.forEach(course => {
      if (course.teacherId && course.teacherName) {
        teacherMap.value[course.teacherId] = course.teacherName
      }
    })
  } catch (error) {
    console.error('加载课程列表失败:', error)
  }
}

const checkConflict = async () => {
  if (!formData.labId || !formData.dayOfWeek || !formData.slotId) {
    ElMessage.warning('请先选择实验室、星期和节次')
    return
  }
  
  checkingConflict.value = true
  try {
    const res = await request({
      url: '/lab-course/check-conflict',
      method: 'post',
      data: formData
    })
    
    if (res.data) {
      ElMessage.error('存在排课冲突!')
    } else {
      ElMessage.success('无排课冲突')
    }
  } catch (error) {
    console.error('检测冲突失败:', error)
  } finally {
    checkingConflict.value = false
  }
}

const handleAdd = () => {
  isEdit.value = false
  dialogTitle.value = '新增排课'
  dialogVisible.value = true
}

const handleEdit = (row) => {
  isEdit.value = true
  dialogTitle.value = '编辑排课'
  Object.assign(formData, {
    id: row.id,
    courseId: row.course?.courseId,
    labId: row.laboratory?.labId,
    dayOfWeek: row.dayOfWeek,
    slotId: row.timeSlot?.slotId,
    startWeek: row.startWeek,
    endWeek: row.endWeek
  })
  dialogVisible.value = true
}

const handleDelete = (row) => {
  ElMessageBox.confirm('确定要删除该排课吗?', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await request({
        url: `/lab-course/${row.id}`,
        method: 'delete'
      })
      ElMessage.success('删除成功')
      loadData()
    } catch (error) {
      console.error('删除失败:', error)
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
      if (isEdit.value) {
        await request({
          url: `/lab-course/${formData.id}`,
          method: 'put',
          data: formData
        })
        ElMessage.success('更新成功')
      } else {
        await request({
          url: '/lab-course',
          method: 'post',
          data: formData
        })
        ElMessage.success('新增成功')
      }
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
    courseId: null,
    labId: null,
    dayOfWeek: null,
    slotId: null,
    startWeek: 1,
    endWeek: 16
  })
}

onMounted(() => {
  loadData()
  loadLabList()
  loadCourseList()
})
</script>

<style scoped>
.lab-course-scheduling {
  padding: 0;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>
