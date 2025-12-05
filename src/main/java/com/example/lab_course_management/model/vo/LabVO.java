package com.example.lab_course_management.model.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 实验室视图对象
 *
 * @author dddwmx
 */
@Data
public class LabVO {

    /**
     * 实验室ID
     */
    private Long labId;

    /**
     * 实验室名称
     */
    private String labName;

    /**
     * 位置
     */
    private String location;

    /**
     * 容量
     */
    private Integer capacity;

    /**
     * 管理员姓名
     */
    private String managerName;

    /**
     * 描述
     */
    private String description;

    /**
     * 状态 0=正常 1=维护中
     */
    private Integer status;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}