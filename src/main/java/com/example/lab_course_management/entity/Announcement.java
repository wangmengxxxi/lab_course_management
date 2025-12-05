package com.example.lab_course_management.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 公告实体
 *
 * @author dddwmx
 */
@Data
@TableName("announcement")
public class Announcement {

    /**
     * 公告ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 标题
     */
    private String title;

    /**
     * 内容
     */
    private String content;

    /**
     * 发布者ID
     */
    private Long publisherId;

    /**
     * 实际发布时间
     */
    private LocalDateTime publishTime;

    /**
     * 预约发布时间
     */
    private LocalDateTime scheduleTime;

    /**
     * 状态 0=草稿 1=已发布
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