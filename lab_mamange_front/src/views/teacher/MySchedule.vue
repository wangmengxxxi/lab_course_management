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
                <div class="course-info">学生: {{ row[day.value].enrolledStudents }}人</div>
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
import { ref, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { useAuthStore } from '@/stores/auth'
import request from '@/utils/request'

const authStore = useAuthStore()
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

  console.log('教师课表 - 当前周次:', currentWeek.value)
  console.log('教师课表 - 我的课程:', myCourses.value)

  // 填充课程数据
  myCourses.value.forEach(course => {
    console.log('处理课程:', course)
    
    if (!course.scheduleInfo) {
      console.warn('课程无排课信息:', course.courseName)
      return
    }
    
    const { dayOfWeek, slotId, startWeek, endWeek, labName } = course.scheduleInfo
    
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
        courseName: course.courseName,
        labName: labName,
        enrolledStudents: course.enrolledStudents || 0,
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
    // 获取教师的所有课程
    const coursesRes = await request({
      url: '/courses',
      method: 'get',
      params: {
        pageNum: 1,
        pageSize: 100,
        teacherId: authStore.userInfo.userId
      }
    })
    
    const courses = coursesRes.data.records || []
    
    console.log('获取到的课程:', courses)
    
    // 为每个课程获取排课信息
    const coursesWithSchedule = []
    for (const course of courses) {
      try {
        const scheduleRes = await request({
          url: '/lab-course/page',
          method: 'get',
          params: {
            current: 1,
            size: 1,
            courseId: course.courseId
          }
        })
        
        console.log(`课程${course.courseName}的排课响应:`, scheduleRes.data)
        
        const schedules = scheduleRes.data.records || []
        if (schedules.length > 0) {
          const schedule = schedules[0]
          console.log('排课详情:', schedule)
          
          // 使用正确的字段名
          course.scheduleInfo = {
            labId: schedule.lab?.labId || schedule.labId,
            labName: schedule.lab?.labName || schedule.labName,
            dayOfWeek: schedule.dayOfWeek,
            slotId: schedule.timeSlot?.slotId || schedule.slotId,
            startWeek: schedule.startWeek,
            endWeek: schedule.endWeek
          }
          
          console.log('解析后的scheduleInfo:', course.scheduleInfo)
        }
        coursesWithSchedule.push(course)
      } catch (error) {
        console.error('获取排课信息失败:', error)
        coursesWithSchedule.push(course)
      }
    }
    
    myCourses.value = coursesWithSchedule
    console.log('最终课程列表:', coursesWithSchedule)
  } catch (error) {
    console.error('加载课程失败:', error)
    ElMessage.error('加载课程失败')
  } finally {
    loading.value = false
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
  background-color: #e1f3d8;
  border-radius: 4px;
  min-height: 80px;
}

.course-name {
  font-weight: bold;
  color: #67c23a;
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
