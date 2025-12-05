package com.example.lab_course_management.model.dto.request;


import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 用户登录请求
 *
 * @author dddwmx
 */
@Data
public class UserLoginRequest {

    /**
     * 账号
     */
    @NotBlank(message = "账号不能为空")
    private String account;

    /**
     * 密码
     */
    @NotBlank(message = "密码不能为空")
    private String password;
}