<template>
  <div class="my-schedule">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>我的课表</span>
          <el-select v-model="currentWeek" placeholder="选择周次" style="width: 150px">
            <el-option
              v-for="week in 20"
              :key="week"
              :label="`第${week}周`"
              :value="week"
            />
          </el-select>
        </div>
      </template>

      <!-- 课表 -->
      <div class="schedule-table">
        <el-table
          :data="scheduleData"
          border
          style="width: 100%"
          :span-method="objectSpanMethod"
        >
          <el-table-column prop="timeSlot" label="时间" width="150" fixed />
          <el-table-column
            v-for="day in weekDays"
            :key="day.value"
            :label="day.label"
            min-width="150"
          >
            <template #default="{ row }">
              <div v-if="row[day.value]" class="course-cell">
                <div class="course-name">{{ row[day.value].courseName }}</div>
                <div class="course-info">{{ row[day.value].labName }}</div>
                <div class="course-info">{{ row[day.value].teacherName }}</div>
                <div class="course-weeks">{{ row[day.value].weeks }}</div>
              </div>
            </template>
          </el-table-column>
        </el-table>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted, watch } from 'vue'
import { ElMessage } from 'element-plus'
import request from '@/utils/request'

const loading = ref(false)
const currentWeek = ref(1)
const myCourses = ref([])

const weekDays = [
  { label: '周一', value: 'monday' },
  { label: '周二', value: 'tuesday' },
  { label: '周三', value: 'wednesday' },
  { label: '周四', value: 'thursday' },
  { label: '周五', value: 'friday' },
  { label: '周六', value: 'saturday' },
  { label: '周日', value: 'sunday' }
]

const timeSlots = [
  { id: 1, name: '第1-2节', time: '08:00-09:40' },
  { id: 2, name: '第3-4节', time: '10:00-11:40' },
  { id: 3, name: '第5-6节', time: '14:00-15:40' },
  { id: 4, name: '第7-8节', time: '16:00-17:40' },
  { id: 5, name: '第9-10节', time: '19:00-20:40' }
]

const scheduleData = computed(() => {
  const data = timeSlots.map(slot => ({
    timeSlot: `${slot.name}\n${slot.time}`,
    slotId: slot.id,
    monday: null,
    tuesday: null,
    wednesday: null,
    thursday: null,
    friday: null,
    saturday: null,
    sunday: null
  }))

  console.log('当前周次:', currentWeek.value)
  console.log('我的课程数据:', myCourses.value)

  // 填充课程数据
  myCourses.value.forEach(enrollment => {
    console.log('处理课程:', enrollment)
    
    // 从course对象获取课程名和教师名
    const courseName = enrollment.course?.courseName || enrollment.courseName
    const teacherName = enrollment.course?.teacherName || enrollment.teacherName
    
    if (!enrollment.scheduleInfo) {
      console.warn('课程无排课信息:', courseName)
      return
    }
    
    const { dayOfWeek, slotId, startWeek, endWeek, labName } = enrollment.scheduleInfo
    
    console.log('排课信息:', { dayOfWeek, slotId, startWeek, endWeek, labName })
    
    // 检查当前周次是否在课程周次范围内
    if (currentWeek.value < startWeek || currentWeek.value > endWeek) {
      console.log('当前周次不在范围内')
      return
    }

    const dayKey = ['', 'monday', 'tuesday', 'wednesday', 'thursday', 'friday', 'saturday', 'sunday'][dayOfWeek]
    const slotIndex = slotId - 1

    console.log('映射到:', { dayKey, slotIndex })

    if (slotIndex >= 0 && slotIndex < data.length && dayKey) {
      data[slotIndex][dayKey] = {
        courseName: courseName,
        labName: labName,
        teacherName: teacherName,
        weeks: `${startWeek}-${endWeek}周`
      }
      console.log('成功添加课程到课表')
    } else {
      console.warn('无效的slotIndex或dayKey:', { slotIndex, dayKey })
    }
  })

  console.log('最终课表数据:', data)
  return data
})

const loadMyCourses = async () => {
  loading.value = true
  try {
    const res = await request({
      url: '/enrollments/my',
      method: 'get',
      params: {
        pageNum: 1,
        pageSize: 100
      }
    })
    myCourses.value = res.data.records || []
  } catch (error) {
    console.error('加载课程失败:', error)
    ElMessage.error('加载课程失败')
  } finally {
    loading.value = false
  }
}

const objectSpanMethod = () => {
  return {
    rowspan: 1,
    colspan: 1
  }
}

onMounted(() => {
  loadMyCourses()
})
</script>

<style scoped>
.my-schedule {
  padding: 0;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.schedule-table {
  margin-top: 20px;
}

.course-cell {
  padding: 8px;
  background-color: #ecf5ff;
  border-radius: 4px;
  min-height: 80px;
}

.course-name {
  font-weight: bold;
  color: #409eff;
  margin-bottom: 4px;
}

.course-info {
  font-size: 12px;
  color: #606266;
  margin-bottom: 2px;
}

.course-weeks {
  font-size: 11px;
  color: #909399;
  margin-top: 4px;
}

:deep(.el-table__cell) {
  padding: 4px 0;
}
</style>
