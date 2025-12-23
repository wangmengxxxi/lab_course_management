package com.example.lab_course_management.model.dto.query;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 用户分页查询请求
 *
 * @author dddwmx
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class UserPageQuery extends BasePageQuery {

    /**
     * 账号(模糊查询)
     */
    private String account;

    /**
     * 真实姓名(模糊查询)
     */
    private String realName;

    /**
     * 手机号(模糊查询)
     */
    private String phone;
}
