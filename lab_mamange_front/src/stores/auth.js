import { defineStore } from 'pinia'
import { login as loginApi, logout as logoutApi, getUserInfo as getUserInfoApi } from '@/api/auth'
import router from '@/router'

export const useAuthStore = defineStore('auth', {
    state: () => ({
        token: localStorage.getItem('lab_token') || '',
        userInfo: JSON.parse(localStorage.getItem('user_info') || 'null'),
        roles: JSON.parse(localStorage.getItem('user_roles') || '[]'),
        currentRole: localStorage.getItem('current_role') || ''
    }),

    getters: {
        // 是否已登录
        isLoggedIn: (state) => !!state.token,

        // 是否有多个角色
        hasMultipleRoles: (state) => state.roles.length > 1,

        // 是否已选择角色
        hasSelectedRole: (state) => !!state.currentRole,

        // 获取当前角色的仪表板路径
        dashboardPath: (state) => {
            const rolePathMap = {
                admin: '/admin/dashboard',
                teacher: '/teacher/dashboard',
                student: '/student/dashboard'
            }
            return rolePathMap[state.currentRole] || '/login'
        }
    },

    actions: {
        /**
         * 用户登录
         */
        async login(account, password) {
            try {
                const res = await loginApi(account, password)
                const { token, userInfo, roles } = res.data

                // 保存token和用户信息
                this.token = token
                this.userInfo = userInfo
                this.roles = roles || []

                // 持久化到localStorage
                localStorage.setItem('lab_token', token)
                localStorage.setItem('user_info', JSON.stringify(userInfo))
                localStorage.setItem('user_roles', JSON.stringify(roles || []))

                // 如果只有一个角色,直接设置为当前角色
                if (this.roles.length === 1) {
                    this.setCurrentRole(this.roles[0])
                }

                return { success: true, hasMultipleRoles: this.roles.length > 1 }
            } catch (error) {
                console.error('登录失败:', error)
                throw error
            }
        },

        /**
         * 设置当前角色
         */
        setCurrentRole(role) {
            this.currentRole = role
            localStorage.setItem('current_role', role)
        },

        /**
         * 切换角色
         */
        async switchRole(role) {
            if (!this.roles.includes(role)) {
                throw new Error('无效的角色')
            }

            this.setCurrentRole(role)

            // 跳转到对应角色的仪表板
            const rolePathMap = {
                admin: '/admin/dashboard',
                teacher: '/teacher/dashboard',
                student: '/student/dashboard'
            }

            const path = rolePathMap[role]
            if (path) {
                await router.push(path)
            }
        },

        /**
         * 用户登出
         */
        async logout() {
            try {
                await logoutApi()
            } catch (error) {
                console.error('登出API调用失败:', error)
            } finally {
                // 清除状态
                this.token = ''
                this.userInfo = null
                this.roles = []
                this.currentRole = ''

                // 清除localStorage
                localStorage.removeItem('lab_token')
                localStorage.removeItem('user_info')
                localStorage.removeItem('user_roles')
                localStorage.removeItem('current_role')

                // 跳转到登录页
                router.push('/login')
            }
        },

        /**
         * 获取用户信息
         */
        async fetchUserInfo() {
            try {
                const res = await getUserInfoApi()
                this.userInfo = res.data
                localStorage.setItem('user_info', JSON.stringify(res.data))
                return res.data
            } catch (error) {
                console.error('获取用户信息失败:', error)
                throw error
            }
        }
    }
})
