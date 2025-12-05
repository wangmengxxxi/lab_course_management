package com.example.lab_course_management.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 课程实体
 *
 * @author dddwmx
 */
@Data
@TableName("course")
public class Course {

    /**
     * 课程ID
     */
    @TableId(type = IdType.AUTO)
    private Long courseId;

    /**
     * 课程名称
     */
    private String courseName;

    /**
     * 教师ID
     */
    private Long teacherId;

    /**
     * 学期
     */
    private String semester;

    /**
     * 描述
     */
    private String description;

    /**
     * 学分
     */
    private Integer credit;

    /**
     * 最大学生数
     */
    private Integer maxStudents;

    /**
     * 状态 0=待审批 1=已发布
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