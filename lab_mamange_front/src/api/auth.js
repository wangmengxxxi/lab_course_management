import request from '@/utils/request'

/**
 * 用户登录
 * @param {string} account - 账号
 * @param {string} password - 密码
 */
export function login(account, password) {
    return request({
        url: '/user/login',
        method: 'post',
        data: {
            account,
            password
        }
    })
}

/**
 * 用户登出
 */
export function logout() {
    return request({
        url: '/user/logout',
        method: 'post'
    })
}

/**
 * 获取当前用户信息
 */
export function getUserInfo() {
    return request({
        url: '/user/info',
        method: 'get'
    })
}

/**
 * 用户注册
 * @param {Object} data - 注册数据
 */
export function register(data) {
    return request({
        url: '/user/register',
        method: 'post',
        data
    })
}
