<template>
  <el-dropdown v-if="authStore.hasMultipleRoles" trigger="click" @command="handleRoleSwitch">
    <span class="role-switcher">
      <el-icon><Switch /></el-icon>
      <span class="role-name">{{ currentRoleName }}</span>
      <el-icon class="el-icon--right"><ArrowDown /></el-icon>
    </span>
    <template #dropdown>
      <el-dropdown-menu>
        <el-dropdown-item
          v-for="role in roleList"
          :key="role.value"
          :command="role.value"
          :class="{ 'is-active': role.value === authStore.currentRole }"
        >
          <el-icon><component :is="role.icon" /></el-icon>
          <span>{{ role.label }}</span>
          <el-icon v-if="role.value === authStore.currentRole" class="check-icon">
            <Check />
          </el-icon>
        </el-dropdown-item>
      </el-dropdown-menu>
    </template>
  </el-dropdown>
</template>

<script setup>
import { computed } from 'vue'
import { ElMessage } from 'element-plus'
import { Switch, ArrowDown, Check, UserFilled, Reading, User } from '@element-plus/icons-vue'
import { useAuthStore } from '@/stores/auth'

const authStore = useAuthStore()

// 角色配置
const roleConfig = {
  admin: {
    value: 'admin',
    label: '管理员',
    icon: UserFilled
  },
  teacher: {
    value: 'teacher',
    label: '教师',
    icon: Reading
  },
  student: {
    value: 'student',
    label: '学生',
    icon: User
  }
}

// 当前角色名称
const currentRoleName = computed(() => {
  return roleConfig[authStore.currentRole]?.label || '选择角色'
})

// 角色列表
const roleList = computed(() => {
  return authStore.roles.map(role => roleConfig[role]).filter(Boolean)
})

// 处理角色切换
const handleRoleSwitch = async (role) => {
  if (role === authStore.currentRole) return
  
  try {
    await authStore.switchRole(role)
    ElMessage.success(`已切换到${roleConfig[role].label}角色`)
  } catch (error) {
    console.error('切换角色失败:', error)
    ElMessage.error('切换角色失败')
  }
}
</script>

<style scoped>
.role-switcher {
  display: inline-flex;
  align-items: center;
  padding: 8px 12px;
  cursor: pointer;
  border-radius: 4px;
  transition: background-color 0.3s;
}

.role-switcher:hover {
  background-color: rgba(0, 0, 0, 0.05);
}

.role-name {
  margin: 0 8px;
  font-size: 14px;
}

:deep(.el-dropdown-menu__item) {
  display: flex;
  align-items: center;
  gap: 8px;
}

:deep(.el-dropdown-menu__item.is-active) {
  color: var(--el-color-primary);
  font-weight: bold;
}

.check-icon {
  margin-left: auto;
  color: var(--el-color-primary);
}
</style>
