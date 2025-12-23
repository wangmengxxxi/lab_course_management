export default {
    // API基础URL
    baseURL: import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080',

    // 超时时间
    timeout: 30000,

    // Token存储键名
    tokenKey: 'lab_token',

    // 当前角色存储键名
    currentRoleKey: 'current_role',

    // 用户角色列表存储键名
    rolesKey: 'user_roles',

    // 用户信息存储键名
    userInfoKey: 'user_info'
}
