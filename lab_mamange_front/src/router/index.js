import { createRouter, createWebHistory } from 'vue-router'
import { useAuthStore } from '@/stores/auth'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      redirect: '/login'
    },
    {
      path: '/login',
      name: 'Login',
      component: () => import('@/views/Login.vue'),
      meta: { requiresAuth: false }
    },
    {
      path: '/role-select',
      name: 'RoleSelect',
      component: () => import('@/views/RoleSelect.vue'),
      meta: { requiresAuth: true, requiresRole: false }
    },
    // 管理员路由
    {
      path: '/admin',
      component: () => import('@/layouts/AdminLayout.vue'),
      meta: { requiresAuth: true, role: 'admin' },
      redirect: '/admin/dashboard',
      children: [
        {
          path: 'dashboard',
          name: 'AdminDashboard',
          component: () => import('@/views/admin/Dashboard.vue'),
          meta: { requiresAuth: true, role: 'admin' }
        },
        {
          path: 'users',
          name: 'UserManagement',
          component: () => import('@/views/admin/UserManagement.vue'),
          meta: { requiresAuth: true, role: 'admin' }
        },
        {
          path: 'labs',
          name: 'LabManagement',
          component: () => import('@/views/admin/LabManagement.vue'),
          meta: { requiresAuth: true, role: 'admin' }
        },
        {
          path: 'courses',
          name: 'AdminCourseManagement',
          component: () => import('@/views/admin/CourseManagement.vue'),
          meta: { requiresAuth: true, role: 'admin' }
        },
        {
          path: 'lab-courses',
          name: 'LabCourseScheduling',
          component: () => import('@/views/admin/LabCourseScheduling.vue'),
          meta: { requiresAuth: true, role: 'admin' }
        },
        {
          path: 'announcements',
          name: 'AnnouncementManagement',
          component: () => import('@/views/admin/AnnouncementManagement.vue'),
          meta: { requiresAuth: true, role: 'admin' }
        },
        {
          path: 'student-enrollments',
          name: 'StudentEnrollmentManagement',
          component: () => import('@/views/admin/StudentEnrollmentManagement.vue'),
          meta: { requiresAuth: true, role: 'admin' }
        },
        {
          path: 'enroll-student',
          name: 'AdminEnrollStudent',
          component: () => import('@/views/admin/AdminEnrollStudent.vue'),
          meta: { requiresAuth: true, role: 'admin' }
        }
      ]
    },
    // 教师路由
    {
      path: '/teacher',
      component: () => import('@/layouts/TeacherLayout.vue'),
      meta: { requiresAuth: true, role: 'teacher' },
      redirect: '/teacher/dashboard',
      children: [
        {
          path: 'dashboard',
          name: 'TeacherDashboard',
          component: () => import('@/views/teacher/Dashboard.vue'),
          meta: { requiresAuth: true, role: 'teacher' }
        },
        {
          path: 'courses',
          name: 'TeacherCourseManagement',
          component: () => import('@/views/teacher/MyCourses.vue'),
          meta: { requiresAuth: true, role: 'teacher' }
        },
        {
          path: 'apply-course',
          name: 'ApplyCourse',
          component: () => import('@/views/teacher/ApplyCourse.vue'),
          meta: { requiresAuth: true, role: 'teacher' }
        },
        {
          path: 'course/:courseId/students',
          name: 'CourseStudents',
          component: () => import('@/views/teacher/CourseStudents.vue'),
          meta: { requiresAuth: true, role: 'teacher' }
        },
        {
          path: 'grades',
          name: 'GradeManagement',
          component: () => import('@/views/teacher/GradeInput.vue'),
          meta: { requiresAuth: true, role: 'teacher' }
        },
        {
          path: 'schedule',
          name: 'TeacherScheduleView',
          component: () => import('@/views/teacher/MySchedule.vue'),
          meta: { requiresAuth: true, role: 'teacher' }
        }
      ]
    },
    // 学生路由
    {
      path: '/student',
      component: () => import('@/layouts/StudentLayout.vue'),
      meta: { requiresAuth: true, role: 'student' },
      redirect: '/student/dashboard',
      children: [
        {
          path: 'dashboard',
          name: 'StudentDashboard',
          component: () => import('@/views/student/Dashboard.vue'),
          meta: { requiresAuth: true, role: 'student' }
        },
        {
          path: 'courses',
          name: 'BrowseCourses',
          component: () => import('@/views/student/BrowseCourses.vue'),
          meta: { requiresAuth: true, role: 'student' }
        },
        {
          path: 'my-courses',
          name: 'MyCourses',
          component: () => import('@/views/student/MyCourses.vue'),
          meta: { requiresAuth: true, role: 'student' }
        },
        {
          path: 'schedule',
          name: 'MySchedule',
          component: () => import('@/views/student/MySchedule.vue'),
          meta: { requiresAuth: true, role: 'student' }
        },
        {
          path: 'grades',
          name: 'MyGrades',
          component: () => import('@/views/student/MyGrades.vue'),
          meta: { requiresAuth: true, role: 'student' }
        },
        {
          path: 'announcements',
          name: 'Announcements',
          component: () => import('@/views/student/Announcements.vue'),
          meta: { requiresAuth: true, role: 'student' }
        }
      ]
    }
  ]
})

// 路由守卫
router.beforeEach((to, from, next) => {
  const authStore = useAuthStore()

  // 如果路由不需要认证,直接放行
  if (to.meta.requiresAuth === false) {
    // 如果已登录,访问登录页时重定向到对应仪表板
    if (to.path === '/login' && authStore.isLoggedIn) {
      if (authStore.hasSelectedRole) {
        next(authStore.dashboardPath)
      } else if (authStore.hasMultipleRoles) {
        next('/role-select')
      } else {
        next()
      }
    } else {
      next()
    }
    return
  }

  // 检查是否已登录
  if (!authStore.isLoggedIn) {
    next('/login')
    return
  }

  // 如果是角色选择页面,检查是否有多个角色
  if (to.path === '/role-select') {
    if (!authStore.hasMultipleRoles) {
      next(authStore.dashboardPath)
    } else {
      next()
    }
    return
  }

  // 检查是否已选择角色
  if (!authStore.hasSelectedRole && authStore.hasMultipleRoles) {
    next('/role-select')
    return
  }

  // 检查角色权限
  if (to.meta.role) {
    if (authStore.currentRole !== to.meta.role) {
      // 角色不匹配,重定向到当前角色的仪表板
      next(authStore.dashboardPath)
      return
    }
  }

  next()
})

export default router
