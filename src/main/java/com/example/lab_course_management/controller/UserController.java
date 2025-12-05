package com.example.lab_course_management.controller;

import cn.dev33.satoken.stp.StpUtil;
import com.example.lab_course_management.common.PageResult;
import com.example.lab_course_management.common.Result;
import com.example.lab_course_management.model.dto.query.BasePageQuery;
import com.example.lab_course_management.model.dto.request.UserAddRequest;
import com.example.lab_course_management.model.dto.request.UserLoginRequest;
import com.example.lab_course_management.model.dto.request.UserRegisterRequest;
import com.example.lab_course_management.model.dto.request.UserUpdateRequest;
import com.example.lab_course_management.model.vo.LoginVO;
import com.example.lab_course_management.model.vo.UserVO;
import com.example.lab_course_management.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

/**
 * 用户控制器
 *
 * @author dddwmx
 */
@RestController
@RequestMapping("/user")
@Tag(name = "用户管理", description = "用户CRUD及认证接口")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;

    @PostMapping("/login")
    @Operation(summary = "用户登录", description = "用户登录接口")
    public Result<LoginVO> login(@Valid @RequestBody UserLoginRequest userLoginRequest) {
        log.info("用户登录: {}", userLoginRequest.getAccount());
        try {
            LoginVO loginVO = userService.userLogin(userLoginRequest);
            log.info("用户 {} 登录成功", userLoginRequest.getAccount());
            return Result.success(loginVO, "登录成功");
        } catch (Exception e) {
            log.error("用户 {} 登录失败: {}", userLoginRequest.getAccount(), e.getMessage());
            throw e;
        }
    }

    @PostMapping("/register")
    @Operation(summary = "用户注册", description = "用户注册接口")
    public Result<Long> register(@Valid @RequestBody UserRegisterRequest userRegisterRequest) {
        log.info("用户注册: {}", userRegisterRequest.getAccount());

        try {
            Long userId = userService.register(userRegisterRequest);
            log.info("用户 {} 注册成功, userId: {}", userRegisterRequest.getAccount(), userId);
            return Result.success(userId, "注册成功");
        } catch (Exception e) {
            log.error("用户 {} 注册失败: {}", userRegisterRequest.getAccount(), e.getMessage());
            throw e;
        }
    }

    @PostMapping("/logout")
    @Operation(summary = "用户登出", description = "用户登出接口")
    public Result<Void> logout() {
        try {
            StpUtil.logout();
            log.info("用户登出成功");
            return Result.success("登出成功");
        } catch (Exception e) {
            log.error("用户登出失败: {}", e.getMessage());
            throw e;
        }
    }

    @GetMapping("/info")
    @Operation(summary = "获取当前用户信息", description = "获取当前登录用户信息")
    public Result<UserVO> getCurrentUserInfo() {
        try {
            Long userId = StpUtil.getLoginIdAsLong();
            UserVO userInfo = userService.getUserVOById(userId);
            return Result.success(userInfo, "获取成功");
        } catch (Exception e) {
            log.error("获取当前用户信息失败: {}", e.getMessage());
            throw e;
        }
    }

    @PostMapping
    @Operation(summary = "新增用户", description = "新增用户接口")
    public Result<Long> addUser(@Valid @RequestBody UserAddRequest userAddRequest) {
        log.info("新增用户: {}", userAddRequest.getAccount());

        try {
            Long userId = userService.addUser(userAddRequest);
            log.info("新增用户成功, userId: {}", userId);
            return Result.success(userId, "新增用户成功");
        } catch (Exception e) {
            log.error("新增用户失败: {}", e.getMessage());
            throw e;
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除用户", description = "删除用户接口")
    public Result<Void> deleteUser(@PathVariable Long id) {
        log.info("删除用户: {}", id);

        try {
            Boolean result = userService.deleteUser(id);
            if (result) {
                log.info("删除用户成功: {}", id);
                return Result.success("删除用户成功");
            } else {
                log.warn("删除用户失败: {}", id);
                return Result.error("删除用户失败");
            }
        } catch (Exception e) {
            log.error("删除用户异常: {}", id, e);
            throw e;
        }
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新用户", description = "更新用户信息接口")
    public Result<Void> updateUser(@PathVariable Long id, @Valid @RequestBody UserUpdateRequest userUpdateRequest) {
        log.info("更新用户: {}", id);

        try {
            Boolean result = userService.updateUser(id, userUpdateRequest);
            if (result) {
                log.info("更新用户成功: {}", id);
                return Result.success("更新用户成功");
            } else {
                log.warn("更新用户失败: {}", id);
                return Result.error("更新用户失败");
            }
        } catch (Exception e) {
            log.error("更新用户异常: {}", id, e);
            throw e;
        }
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取用户详情", description = "根据ID获取用户详情")
    public Result<UserVO> getUserById(@PathVariable Long id) {
        try {
            UserVO userVO = userService.getUserVOById(id);
            return Result.success(userVO, "查询成功");
        } catch (Exception e) {
            log.error("获取用户详情失败: {}", id, e);
            throw e;
        }
    }

    @GetMapping
    @Operation(summary = "分页查询用户", description = "分页查询用户列表")
    public Result<PageResult<UserVO>> listUsersByPage(@Valid BasePageQuery basePageQuery) {
        log.info("分页查询用户: page={}, size={}", basePageQuery.getPageNum(), basePageQuery.getPageSize());

        try {
            PageResult<UserVO> pageResult = userService.listUsersByPage(basePageQuery);
            log.info("分页查询用户成功: total={}, current={}", pageResult.getTotal(), pageResult.getCurrent());
            return Result.success(pageResult, "查询成功");
        } catch (Exception e) {
            log.error("分页查询用户失败: {}", e.getMessage());
            throw e;
        }
    }
}