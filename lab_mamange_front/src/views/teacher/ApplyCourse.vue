<template>
  <div class="apply-course">
    <el-card>
      <template #header>
        <span>申请开设课程</span>
      </template>

      <el-alert
        title="提示"
        type="info"
        :closable="false"
        style="margin-bottom: 20px"
      >
        提交课程申请后,需等待管理员审批。审批通过后,管理员会为课程安排实验室和上课时间。
      </el-alert>

      <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        label-width="120px"
      >
        <el-form-item label="课程名称" prop="courseName">
          <el-input
            v-model="form.courseName"
            placeholder="请输入课程名称"
            maxlength="100"
            show-word-limit
          />
        </el-form-item>

        <el-form-item label="学期" prop="semester">
          <el-select
            v-model="form.semester"
            placeholder="请选择学期"
            style="width: 100%"
          >
            <el-option label="2024年春季学期" value="2024-Spring" />
            <el-option label="2024年秋季学期" value="2024-Fall" />
            <el-option label="2025年春季学期" value="2025-Spring" />
            <el-option label="2025年秋季学期" value="2025-Fall" />
            <el-option label="2026年春季学期" value="2026-Spring" />
            <el-option label="2026年秋季学期" value="2026-Fall" />
          </el-select>
        </el-form-item>

        <el-form-item label="学分" prop="credit">
          <el-input-number
            v-model="form.credit"
            :min="1"
            :max="10"
            placeholder="请输入学分"
          />
        </el-form-item>

        <el-form-item label="最大选课人数" prop="maxStudents">
          <el-input-number
            v-model="form.maxStudents"
            :min="1"
            :max="200"
            placeholder="请输入最大选课人数"
          />
        </el-form-item>

        <el-form-item label="课程描述" prop="description">
          <el-input
            v-model="form.description"
            type="textarea"
            :rows="4"
            placeholder="请输入课程描述"
            maxlength="255"
            show-word-limit
          />
        </el-form-item>

        <el-form-item>
          <el-button type="primary" @click="submitForm" :loading="loading">
            提交申请
          </el-button>
          <el-button @click="resetForm">重置</el-button>
          <el-button @click="$router.back()">返回</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import request from '@/utils/request'

const router = useRouter()
const formRef = ref(null)
const loading = ref(false)

const form = reactive({
  courseName: '',
  semester: '',
  credit: 4,
  maxStudents: 50,
  description: ''
})

const rules = {
  courseName: [
    { required: true, message: '请输入课程名称', trigger: 'blur' },
    { min: 2, max: 100, message: '长度在 2 到 100 个字符', trigger: 'blur' }
  ],
  semester: [
    { required: true, message: '请选择学期', trigger: 'change' }
  ],
  credit: [
    { required: true, message: '请输入学分', trigger: 'blur' }
  ],
  maxStudents: [
    { required: true, message: '请输入最大选课人数', trigger: 'blur' }
  ],
  description: [
    { required: true, message: '请输入课程描述', trigger: 'blur' }
  ]
}

const submitForm = async () => {
  if (!formRef.value) return

  await formRef.value.validate(async (valid) => {
    if (!valid) return

    loading.value = true
    try {
      await request({
        url: '/courses',
        method: 'post',
        data: form
      })
      ElMessage.success('课程申请提交成功,等待管理员审批')
      router.push('/teacher/courses')
    } catch (error) {
      console.error('提交课程申请失败:', error)
      ElMessage.error(error.response?.data?.message || '提交课程申请失败')
    } finally {
      loading.value = false
    }
  })
}

const resetForm = () => {
  if (!formRef.value) return
  formRef.value.resetFields()
}
</script>

<style scoped>
.apply-course {
  padding: 0;
}

:deep(.el-form-item__label) {
  font-weight: 500;
}
</style>
