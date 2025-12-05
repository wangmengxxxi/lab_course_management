package com.example.lab_course_management.model.vo;

import lombok.Data;

/**
 * 登录响应
 *
 * @author dddwmx
 */
@Data
public class LoginVO {

    /**
     * Token
     */
    private String token;

    /**
     * 用户信息
     */
    private UserInfoVO userInfo;

    @Data
    public static class UserInfoVO {
        /**
         * 用户ID
         */
        private Long userId;

        /**
         * 账号
         */
        private String account;

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

        /**
         * 状态
         */
        private Integer status;
    }
}