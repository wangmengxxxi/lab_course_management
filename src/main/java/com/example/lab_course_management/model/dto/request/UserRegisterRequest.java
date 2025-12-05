package com.example.lab_course_management.model.dto.request;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 用户注册请求
 *
 * @author dddwmx
 */
@Data
public class UserRegisterRequest {

    /**
     * 账号
     */
    @NotBlank(message = "账号不能为空")
    @Size(min = 4, max = 50, message = "账号长度必须在4-50字符之间")
    @Pattern(regexp = "^[a-zA-Z0-9_]+$", message = "账号只能包含字母、数字和下划线")
    private String account;

    /**
     * 密码
     */
    @NotBlank(message = "密码不能为空")
    @Size(min = 8, max = 100, message = "密码长度必须在8-100字符之间")
    private String password;

    /**
     * 校验密码
     */
    @NotBlank(message = "校验密码不能为空")
    private String checkPassword;

    /**
     * 真实姓名
     */
    private String realName;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 手机号
     */
    private String phone;
}