package com.example.lab_course_management.model.dto.request;

import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

/**
 * 用户更新请求
 *
 * @author dddwmx
 */
@Data
public class UserUpdateRequest {

    /**
     * 真实姓名
     */
    @Size(max = 50, message = "真实姓名长度不能超过50字符")
    private String realName;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 手机号
     */
    private String phone;

    /**
     * 状态 1=正常 0=禁用
     */
    private Integer status;

    /**
     * 角色ID列表
     */
    private List<Long> roleIds;
}