package com.example.lab_course_management.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.lab_course_management.common.PageResult;
import com.example.lab_course_management.entity.User;
import com.example.lab_course_management.model.dto.query.BasePageQuery;
import com.example.lab_course_management.model.dto.query.UserPageQuery;
import com.example.lab_course_management.model.dto.request.UserAddRequest;
import com.example.lab_course_management.model.dto.request.UserLoginRequest;
import com.example.lab_course_management.model.dto.request.UserRegisterRequest;
import com.example.lab_course_management.model.dto.request.UserUpdateRequest;
import com.example.lab_course_management.model.vo.LoginVO;
import com.example.lab_course_management.model.vo.UserVO;

/**
 * 用户服务接口
 *
 * @author dddwmx
 */
public interface UserService extends IService<User> {

    /**
     * 用户注册
     *
     * @param userRegisterRequest 用户注册请求
     * @return 用户ID
     */
    Long userRegister(UserRegisterRequest userRegisterRequest);

    /**
     * 用户注册（别名方法）
     *
     * @param userRegisterRequest 用户注册请求
     * @return 用户ID
     */
    default Long register(UserRegisterRequest userRegisterRequest) {
        return userRegister(userRegisterRequest);
    }

    /**
     * 用户登录
     *
     * @param userLoginRequest 用户登录请求
     * @return 登录响应
     */
    LoginVO userLogin(UserLoginRequest userLoginRequest);

    /**
     * 获取当前登录用户信息
     *
     * @return 用户信息
     */
    User getLoginUser();

    /**
     * 用户登出
     */
    void userLogout();

    /**
     * 根据账号获取用户
     *
     * @param username 账号
     * @return 用户信息
     */
    User getUserByUsername(String username);

    /**
     * 根据用户ID获取用户实体
     *
     * @param userId 用户ID
     * @return 用户实体
     */
    User getUserById(Long userId);

    /**
     * 根据用户ID获取用户视图对象
     *
     * @param userId 用户ID
     * @return 用户视图对象
     */
    UserVO getUserVOById(Long userId);

    /**
     * 新增用户
     *
     * @param userAddRequest 用户添加请求
     * @return 用户ID
     */
    Long addUser(UserAddRequest userAddRequest);

    /**
     * 更新用户信息
     *
     * @param userId 用户ID
     * @param userUpdateRequest 用户更新请求
     * @return 是否成功
     */
    Boolean updateUser(Long userId, UserUpdateRequest userUpdateRequest);

    /**
     * 删除用户
     *
     * @param userId 用户ID
     * @return 是否成功
     */
    Boolean deleteUser(Long userId);

    /**
     * 分页查询用户列表
     *
     * @param userPageQuery 分页查询参数
     * @return 用户分页结果
     */
    PageResult<UserVO> listUsersByPage(UserPageQuery userPageQuery);
}