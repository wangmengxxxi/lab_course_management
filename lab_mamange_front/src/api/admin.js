import request from '@/utils/request'

/**
 * 分页查询用户列表
 */
export function getUserList(params) {
    return request({
        url: '/user',
        method: 'get',
        params
    })
}

/**
 * 获取用户详情
 */
export function getUserDetail(id) {
    return request({
        url: `/user/${id}`,
        method: 'get'
    })
}

/**
 * 新增用户
 */
export function addUser(data) {
    return request({
        url: '/user',
        method: 'post',
        data
    })
}

/**
 * 更新用户
 */
export function updateUser(id, data) {
    return request({
        url: `/user/${id}`,
        method: 'put',
        data
    })
}

/**
 * 删除用户
 */
export function deleteUser(id) {
    return request({
        url: `/user/${id}`,
        method: 'delete'
    })
}

// ==================== 实验室管理 ====================

/**
 * 分页查询实验室列表
 */
export function getLabList(params) {
    return request({
        url: '/labs',
        method: 'get',
        params
    })
}

/**
 * 新增实验室
 */
export function addLab(data) {
    return request({
        url: '/labs',
        method: 'post',
        data
    })
}

/**
 * 更新实验室
 */
export function updateLab(id, data) {
    return request({
        url: `/labs/${id}`,
        method: 'put',
        data
    })
}

/**
 * 删除实验室
 */
export function deleteLab(id) {
    return request({
        url: `/labs/${id}`,
        method: 'delete'
    })
}
