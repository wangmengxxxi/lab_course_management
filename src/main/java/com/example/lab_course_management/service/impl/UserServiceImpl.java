package com.example.lab_course_management.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.lab_course_management.common.ErrorCode;
import com.example.lab_course_management.common.PageResult;
import com.example.lab_course_management.entity.User;
import com.example.lab_course_management.entity.UserRole;
import com.example.lab_course_management.entity.Role;
import com.example.lab_course_management.exception.BussniesException;
import com.example.lab_course_management.mapper.UserMapper;
import com.example.lab_course_management.model.dto.query.BasePageQuery;
import com.example.lab_course_management.model.dto.request.UserAddRequest;
import com.example.lab_course_management.model.dto.request.UserLoginRequest;
import com.example.lab_course_management.model.dto.request.UserRegisterRequest;
import com.example.lab_course_management.model.dto.request.UserUpdateRequest;
import com.example.lab_course_management.model.vo.LoginVO;
import com.example.lab_course_management.model.vo.UserVO;
import com.example.lab_course_management.service.UserService;
import com.example.lab_course_management.service.UserRoleService;
import com.example.lab_course_management.service.RoleService;
import org.apache.commons.lang3.StringUtils;
import static org.springframework.util.StringUtils.hasText;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.lab_course_management.utils.DigestUtils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * 用户服务实现类
 *
 * @author dddwmx
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    /**
     * 盐值，用于密码加密
     */
    private static final String SALT = "wmx";

    /**
     * 用户名正则表达式（只能包含字母、数字和下划线）
     */
    private static final Pattern USERNAME_PATTERN = Pattern.compile("^[a-zA-Z0-9_]+$");

    @Override
    public Long userRegister(UserRegisterRequest userRegisterRequest) {
        // 1. 校验参数
        String account = userRegisterRequest.getAccount();
        String password = userRegisterRequest.getPassword();
        String checkPassword = userRegisterRequest.getCheckPassword();

        if (StringUtils.isAnyBlank(account, password, checkPassword)) {
            throw new BussniesException(ErrorCode.PARAMS_ERROR, "请求参数不能为空");
        }

        if (account.length() < 4) {
            throw new BussniesException(ErrorCode.PARAMS_ERROR, "账号不能少于4位");
        }

        if (password.length() < 8) {
            throw new BussniesException(ErrorCode.PARAMS_ERROR, "密码不能少于8位");
        }

        if (!USERNAME_PATTERN.matcher(account).matches()) {
            throw new BussniesException(ErrorCode.PARAMS_ERROR, "账号只能包含字母、数字和下划线");
        }

        if (!password.equals(checkPassword)) {
            throw new BussniesException(ErrorCode.PARAMS_ERROR, "两次输入的密码不一致");
        }

        // 2. 检查账号是否已存在
        synchronized (account.intern()) {
            QueryWrapper<User> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("account", account);
            long count = this.count(queryWrapper);
            if (count > 0) {
                throw new BussniesException(ErrorCode.PARAMS_ERROR, "账号已存在");
            }

            // 3. 加密密码
            String encryptPassword = DigestUtils.md5DigestAsHexWithSalt(password);

            // 4. 插入用户数据
            User user = new User();
            user.setAccount(account);
            user.setPassword(encryptPassword);
            user.setRealName(userRegisterRequest.getRealName());
            user.setEmail(userRegisterRequest.getEmail());
            user.setPhone(userRegisterRequest.getPhone());
            user.setStatus(1); // 默认正常状态
            user.setIsDelete(0); // 显式设置未删除状态，避免null覆盖数据库默认值

            boolean saveResult = this.save(user);
            if (!saveResult) {
                throw new BussniesException(ErrorCode.SYSTEM_ERROR, "注册失败，数据库错误");
            }

            return user.getUserId();
        }
    }

    @Override
    public LoginVO userLogin(UserLoginRequest userLoginRequest) {
        // 1. 校验参数
        String account = userLoginRequest.getAccount();
        String password = userLoginRequest.getPassword();

        if (StringUtils.isAnyBlank(account, password)) {
            throw new BussniesException(ErrorCode.PARAMS_ERROR, "账号或密码不能为空");
        }

        // 2. 加密密码
        String encryptPassword = DigestUtils.md5DigestAsHexWithSalt(password);

        // 3. 查询用户
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("account", account);
        User user = this.getOne(queryWrapper);

        if (user == null) {
            throw new BussniesException(ErrorCode.PARAMS_ERROR, "用户不存在");
        }

        if (!user.getPassword().equals(encryptPassword)) {
            throw new BussniesException(ErrorCode.PARAMS_ERROR, "密码错误");
        }

        if (user.getStatus() == 0) {
            throw new BussniesException(ErrorCode.FORBIDDEN_ERROR, "账号已被禁用");
        }

        // 4. 记录登录会话
        StpUtil.login(user.getUserId());

        // 5. 构造返回结果
        LoginVO loginVO = new LoginVO();
        loginVO.setToken(StpUtil.getTokenValue());

        LoginVO.UserInfoVO userInfoVO = new LoginVO.UserInfoVO();
        BeanUtils.copyProperties(user, userInfoVO);
        loginVO.setUserInfo(userInfoVO);

        return loginVO;
    }

    @Override
    public User getLoginUser() {
        // 检查是否已登录
        if (!StpUtil.isLogin()) {
            throw new BussniesException(ErrorCode.NOT_LOGIN_ERROR, "用户未登录");
        }

        // 获取当前登录用户ID
        Long userId = StpUtil.getLoginIdAsLong();

        // 查询用户信息
        User user = this.getById(userId);
        if (user == null) {
            throw new BussniesException(ErrorCode.NOT_FOUND_ERROR, "用户不存在");
        }

        if (user.getStatus() == 0) {
            throw new BussniesException(ErrorCode.FORBIDDEN_ERROR, "账号已被禁用");
        }

        return user;
    }

    @Override
    public void userLogout() {
        StpUtil.logout();
    }

    @Override
    public User getUserByUsername(String username) {
        if (StringUtils.isBlank(username)) {
            return null;
        }

        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("account", username);
        return this.getOne(queryWrapper);
    }

    @Override
    public User getUserById(Long userId) {
        if (userId == null) {
            return null;
        }
        return this.getById(userId);
    }

    @Override
    public UserVO getUserVOById(Long userId) {
        if (userId == null) {
            return null;
        }

        User user = this.getById(userId);
        if (user == null) {
            return null;
        }

        // 转换为UserVO
        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(user, userVO);


        try {
            List<String> roles = this.getUserRoles(userId);
            userVO.setRoles(roles);
        } catch (Exception e) {
            log.warn("获取用户角色失败，用户ID: {}", userId, e);
            userVO.setRoles(new ArrayList<>());
        }

        return userVO;
    }

    /**
     * 获取用户角色列表
     */
    private List<String> getUserRoles(Long userId) {
        try {

            QueryWrapper<UserRole> userRoleQuery = new QueryWrapper<>();
            userRoleQuery.eq("user_id", userId);
            List<UserRole> userRoles = userRoleService.list(userRoleQuery);

            if (userRoles.isEmpty()) {
                return new ArrayList<>();
            }
            List<Long> roleIds = userRoles.stream()
                    .map(UserRole::getRoleId)
                    .collect(Collectors.toList());
            List<Role> roles = roleService.listByIds(roleIds);

            return roles.stream()
                    .map(Role::getRoleName)
                    .collect(Collectors.toList());

        } catch (Exception e) {
            log.error("获取用户角色失败，用户ID: {}", userId, e);
            return new ArrayList<>();
        }
    }

  private final UserRoleService userRoleService;
  private final RoleService roleService;

    @Override
    @Transactional
    public Long addUser(UserAddRequest userAddRequest) {
        // 1. 校验参数
        String account = userAddRequest.getAccount();
        String password = userAddRequest.getPassword();

        if (StringUtils.isAnyBlank(account, password)) {
            throw new BussniesException(ErrorCode.PARAMS_ERROR, "账号或密码不能为空");
        }

        if (account.length() < 4) {
            throw new BussniesException(ErrorCode.PARAMS_ERROR, "账号不能少于4位");
        }

        if (password.length() < 8) {
            throw new BussniesException(ErrorCode.PARAMS_ERROR, "密码不能少于8位");
        }

        if (!USERNAME_PATTERN.matcher(account).matches()) {
            throw new BussniesException(ErrorCode.PARAMS_ERROR, "账号只能包含字母、数字和下划线");
        }

        // 2. 检查账号是否已存在
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("account", account);
        long count = this.count(queryWrapper);
        if (count > 0) {
            throw new BussniesException(ErrorCode.PARAMS_ERROR, "账号已存在");
        }

        // 3. 加密密码
        String encryptPassword = DigestUtils.md5DigestAsHexWithSalt(password);

        // 4. 插入用户数据
        User user = new User();
        user.setAccount(account);
        user.setPassword(encryptPassword);
        user.setRealName(userAddRequest.getRealName());
        user.setEmail(userAddRequest.getEmail());
        user.setPhone(userAddRequest.getPhone());
        user.setStatus(1);
        user.setIsDelete(0);

        boolean saveResult = this.save(user);
        if (!saveResult) {
            throw new BussniesException(ErrorCode.SYSTEM_ERROR, "新增用户失败，数据库错误");
        }

        // 5. 分配角色
        List<Long> roleIds = userAddRequest.getRoleIds();
        if (roleIds != null && !roleIds.isEmpty()) {
            for (Long roleId : roleIds) {
                UserRole userRole = new UserRole();
                userRole.setUserId(user.getUserId());
                userRole.setRoleId(roleId);
                userRoleService.save(userRole);
            }
        }

        return user.getUserId();
    }

    @Override
    @Transactional
    public Boolean updateUser(Long userId, UserUpdateRequest userUpdateRequest) {
        if (userId == null) {
            throw new BussniesException(ErrorCode.PARAMS_ERROR, "用户ID不能为空");
        }

        // 1. 查询用户
        User user = this.getById(userId);
        if (user == null) {
            throw new BussniesException(ErrorCode.NOT_FOUND_ERROR, "用户不存在");
        }

        // 2. 更新用户信息
        if (hasText(userUpdateRequest.getRealName())) {
            user.setRealName(userUpdateRequest.getRealName());
        }
        if (hasText(userUpdateRequest.getEmail())) {
            user.setEmail(userUpdateRequest.getEmail());
        }
        if (hasText(userUpdateRequest.getPhone())) {
            user.setPhone(userUpdateRequest.getPhone());
        }
        if (userUpdateRequest.getStatus() != null) {
            user.setStatus(userUpdateRequest.getStatus());
        }

        boolean updateResult = this.updateById(user);
        if (!updateResult) {
            throw new BussniesException(ErrorCode.SYSTEM_ERROR, "更新用户失败");
        }

        // 3. 更新角色（如果提供角色ID）
        List<Long> roleIds = userUpdateRequest.getRoleIds();
        if (roleIds != null) {
            // 删除原有角色
            QueryWrapper<UserRole> deleteWrapper = new QueryWrapper<>();
            deleteWrapper.eq("user_id", userId);
            userRoleService.remove(deleteWrapper);

            // 添加新角色
            for (Long roleId : roleIds) {
                UserRole userRole = new UserRole();
                userRole.setUserId(userId);
                userRole.setRoleId(roleId);
                userRoleService.save(userRole);
            }
        }

        return true;
    }

    @Override
    @Transactional
    public Boolean deleteUser(Long userId) {
        if (userId == null) {
            throw new BussniesException(ErrorCode.PARAMS_ERROR, "用户ID不能为空");
        }

        // 1. 删除用户角色关联
        QueryWrapper<UserRole> userRoleQuery = new QueryWrapper<>();
        userRoleQuery.eq("user_id", userId);
        userRoleService.remove(userRoleQuery);

        // 2. 逻辑删除用户
        boolean deleteResult = this.removeById(userId);
        if (!deleteResult) {
            throw new BussniesException(ErrorCode.SYSTEM_ERROR, "删除用户失败");
        }

        return true;
    }

    @Override
    public PageResult<UserVO> listUsersByPage(BasePageQuery basePageQuery) {
        // 1. 分页查询用户
        Page<User> page = new Page<>(basePageQuery.getPageNum(), basePageQuery.getPageSize());
        Page<User> userPage = this.page(page);

        // 2. 转换为UserVO
        List<UserVO> userVOList = userPage.getRecords().stream()
                .map(user -> {
                    UserVO userVO = new UserVO();
                    BeanUtils.copyProperties(user, userVO);
                    // 查询用户角色信息
                    try {
                        List<String> roles = this.getUserRoles(user.getUserId());
                        userVO.setRoles(roles);
                    } catch (Exception e) {
                        log.warn("获取用户角色失败，用户ID: {}", user.getUserId(), e);
                        userVO.setRoles(new ArrayList<>());
                    }
                    return userVO;
                })
                .collect(Collectors.toList());

        // 3. 构建分页结果
        return PageResult.of(
                userVOList,
                userPage.getTotal(),
                userPage.getCurrent(),
                userPage.getSize()
        );
    }
}