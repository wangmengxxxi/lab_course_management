package com.example.lab_course_management.controller;

import cn.dev33.satoken.stp.StpUtil;
import com.example.lab_course_management.common.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 测试控制器
 *
 * @author dddwmx
 */
@RestController
@RequestMapping("/test")
@Tag(name = "测试接口", description = "用于测试系统功能")
@Slf4j
public class TestController {

    /**
     * 测试无需登录的接口
     *
     * @return 测试结果
     */
    @GetMapping("/public")
    @Operation(summary = "公共测试接口", description = "无需登录即可访问")
    public Result<String> publicTest() {
        return Result.success("这是一个公开接口，任何人都可以访问");
    }

    /**
     * 测试需要登录的接口
     *
     * @return 测试结果
     */
    @GetMapping("/auth")
    @Operation(summary = "认证测试接口", description = "需要登录才能访问")
    public Result<String> authTest() {
        long loginId = StpUtil.getLoginIdAsLong();
        return Result.success("登录成功，当前用户ID: " + loginId);
    }

    /**
     * 测试Redis连接
     *
     * @return 测试结果
     */
    @GetMapping("/redis")
    @Operation(summary = "Redis测试接口", description = "测试Redis连接是否正常")
    public Result<String> redisTest() {
        try {
            StpUtil.getTokenSession().set("test_key", "test_value");
            String value = (String) StpUtil.getTokenSession().get("test_key");
            return Result.success("Redis连接正常，测试值: " + value);
        } catch (Exception e) {
            return Result.error(500, "Redis连接失败: " + e.getMessage());
        }
    }

    /**
     * 获取当前用户角色信息（调试用）
     *
     * @return 当前用户角色信息
     */
    @GetMapping("/current-user-roles")
    @Operation(summary = "获取当前用户角色", description = "用于调试查看当前登录用户的角色信息")
    public Result<UserRoleInfo> getCurrentUserRoles() {
        try {
            // 获取当前用户ID
            Long userId = StpUtil.getLoginIdAsLong();

            // 获取用户角色列表
            List<String> roleList = StpUtil.getRoleList();

            // 获取用户权限列表
            List<String> permissionList = StpUtil.getPermissionList();

            // 测试角色检查
            boolean isAdmin = StpUtil.hasRole("admin");
            boolean isTeacher = StpUtil.hasRole("teacher");
            boolean isStudent = StpUtil.hasRole("student");

            log.info("当前用户ID: {}", userId);
            log.info("用户角色列表: {}", roleList);
            log.info("用户权限列表: {}", permissionList);
            log.info("角色检查 - admin: {}, teacher: {}, student: {}", isAdmin, isTeacher, isStudent);

            UserRoleInfo userInfo = new UserRoleInfo();
            userInfo.setUserId(userId);
            userInfo.setRoles(roleList);
            userInfo.setPermissions(permissionList);
            userInfo.setHasAdmin(isAdmin);
            userInfo.setHasTeacher(isTeacher);
            userInfo.setHasStudent(isStudent);
            userInfo.setTokenInfo(StpUtil.getTokenInfo().toString());

            return Result.success(userInfo, "获取当前用户角色成功");

        } catch (Exception e) {
            log.error("获取当前用户角色失败", e);
            return Result.error("获取当前用户角色失败: " + e.getMessage());
        }
    }

    @Data
    public static class UserRoleInfo {
        private Long userId;
        private List<String> roles;
        private List<String> permissions;
        private boolean hasAdmin;
        private boolean hasTeacher;
        private boolean hasStudent;
        private String tokenInfo;
    }
}