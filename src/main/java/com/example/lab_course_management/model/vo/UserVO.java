package com.example.lab_course_management.model.vo;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 用户视图对象
 *
 * @author dddwmx
 */
@Data
public class UserVO {

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
     * 状态 1=正常 0=禁用
     */
    private Integer status;

    /**
     * 角色列表
     */
    private List<String> roles;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    /**
     * 角色视图对象
     */
    @Data
    public static class RoleVO {
        /**
         * 角色ID
         */
        private Long roleId;

        /**
         * 角色名称
         */
        private String roleName;

        /**
         * 角色描述
         */
        private String description;
    }
}