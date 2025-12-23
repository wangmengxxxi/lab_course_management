import axios from 'axios'
import { ElMessage } from 'element-plus'
import config from '@/config'
import router from '@/router'

// 创建axios实例
const request = axios.create({
    baseURL: config.baseURL,
    timeout: config.timeout
})

// 请求拦截器
request.interceptors.request.use(
    (config) => {
        // 从localStorage获取token
        const token = localStorage.getItem('lab_token')
        if (token) {
            // 注意:不添加Bearer前缀,直接使用token
            config.headers.Authorization = token
        }
        return config
    },
    (error) => {
        console.error('请求错误:', error)
        return Promise.reject(error)
    }
)

// 响应拦截器
request.interceptors.response.use(
    (response) => {
        const res = response.data

        // 如果返回的状态码不是200,则认为是错误
        if (res.code !== 200 && res.code !== 0) {
            ElMessage.error(res.message || '请求失败')

            // 401: 未登录或token过期
            if (res.code === 401 || res.code === 40100) {
                ElMessage.error('登录已过期,请重新登录')
                // 清除本地存储
                localStorage.removeItem('lab_token')
                localStorage.removeItem('current_role')
                localStorage.removeItem('user_roles')
                localStorage.removeItem('user_info')
                // 跳转到登录页
                router.push('/login')
            }

            return Promise.reject(new Error(res.message || '请求失败'))
        }

        return res
    },
    (error) => {
        console.error('响应错误:', error)

        if (error.response) {
            const status = error.response.status

            if (status === 401) {
                ElMessage.error('登录已过期,请重新登录')
                localStorage.removeItem('lab_token')
                localStorage.removeItem('current_role')
                localStorage.removeItem('user_roles')
                localStorage.removeItem('user_info')
                router.push('/login')
            } else if (status === 403) {
                ElMessage.error('没有权限访问')
            } else if (status === 404) {
                ElMessage.error('请求的资源不存在')
            } else if (status === 500) {
                ElMessage.error('服务器错误')
            } else {
                ElMessage.error(error.response.data?.message || '请求失败')
            }
        } else if (error.message) {
            if (error.message.includes('timeout')) {
                ElMessage.error('请求超时')
            } else if (error.message.includes('Network Error')) {
                ElMessage.error('网络错误,请检查网络连接')
            } else {
                ElMessage.error(error.message)
            }
        } else {
            ElMessage.error('未知错误')
        }

        return Promise.reject(error)
    }
)

export default request
