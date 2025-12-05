package com.example.lab_course_management.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 实验室实体
 *
 * @author dddwmx
 */
@Data
@TableName("laboratory")
public class Laboratory {

    /**
     * 实验室ID
     */
    @TableId(type = IdType.AUTO)
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
     * 管理员ID
     */
    private Long managerId;

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
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    /**
     * 是否删除 0=未删除 1=已删除
     */
    @TableLogic
    @TableField(fill = FieldFill.INSERT)
    private Integer isDelete;
}