package com.example.lab_course_management.auth;

import cn.dev33.satoken.stp.StpInterface;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.lab_course_management.entity.Permission;
import com.example.lab_course_management.entity.Role;
import com.example.lab_course_management.entity.UserRole;
import com.example.lab_course_management.entity.RolePermission;
import com.example.lab_course_management.service.PermissionService;
import com.example.lab_course_management.service.RoleService;
import com.example.lab_course_management.service.UserRoleService;
import com.example.lab_course_management.service.RolePermissionService;
import org.springframework.stereotype.Component;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Sa-Token 权限接口实现
 *
 * @author dddwmx
 */
@Component
@RequiredArgsConstructor
public class StpInterfaceImpl implements StpInterface {

    private final UserRoleService userRoleService;
    private final RoleService roleService;
    private final RolePermissionService rolePermissionService;
    private final PermissionService permissionService;

    /**
     * 返回一个账号所拥有的权限码集合
     */
    @Override
    public List<String> getPermissionList(Object loginId, String loginType) {
        Long userId = Long.valueOf(loginId.toString());

        // 查询用户角色
        QueryWrapper<UserRole> userRoleQuery = new QueryWrapper<>();
        userRoleQuery.eq("user_id", userId);
        List<UserRole> userRoles = userRoleService.list(userRoleQuery);

        if (userRoles.isEmpty()) {
            return new ArrayList<>();
        }

        List<Long> roleIds = userRoles.stream()
                .map(UserRole::getRoleId)
                .collect(Collectors.toList());

        // 查询角色权限关联
        QueryWrapper<RolePermission> rolePermissionQuery = new QueryWrapper<>();
        rolePermissionQuery.in("role_id", roleIds);
        List<RolePermission> rolePermissions = rolePermissionService.list(rolePermissionQuery);

        if (rolePermissions.isEmpty()) {
            return new ArrayList<>();
        }

        List<Long> permissionIds = rolePermissions.stream()
                .map(RolePermission::getPermissionId)
                .collect(Collectors.toList());

        // 查询权限
        QueryWrapper<Permission> permissionQuery = new QueryWrapper<>();
        permissionQuery.in("permission_id", permissionIds);
        List<Permission> permissions = permissionService.list(permissionQuery);

        return permissions.stream()
                .map(Permission::getPermCode)
                .collect(Collectors.toList());
    }

    /**
     * 返回一个账号
     *
     */
    @Override
    public List<String> getRoleList(Object loginId, String loginType) {
        Long userId = Long.valueOf(loginId.toString());

        // 查询用户角色
        QueryWrapper<UserRole> userRoleQuery = new QueryWrapper<>();
        userRoleQuery.eq("user_id", userId);
        List<UserRole> userRoles = userRoleService.list(userRoleQuery);

        if (userRoles.isEmpty()) {
            return new ArrayList<>();
        }

        List<Long> roleIds = userRoles.stream()
                .map(UserRole::getRoleId)
                .collect(Collectors.toList());

        // 查询角色
        QueryWrapper<Role> roleQuery = new QueryWrapper<>();
        roleQuery.in("role_id", roleIds);
        List<Role> roles = roleService.list(roleQuery);

        return roles.stream()
                .map(Role::getRoleName)
                .collect(Collectors.toList());
    }
}