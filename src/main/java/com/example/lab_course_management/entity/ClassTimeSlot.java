package com.example.lab_course_management.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * 时间段实体
 *
 * @author dddwmx
 */
@Data
@TableName("class_time_slot")
public class ClassTimeSlot {

    /**
     * 时间段ID
     */
    @TableId(type = IdType.AUTO)
    private Long slotId;

    /**
     * 时间段名称
     */
    private String slotName;

    /**
     * 开始时间
     */
    private LocalTime startTime;

    /**
     * 结束时间
     */
    private LocalTime endTime;

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