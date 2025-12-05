package com.example.lab_course_management.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 学生选课实体
 *
 * @author dddwmx
 */
@Data
@TableName("student_course")
public class StudentCourse {

    /**
     * 选课记录ID
     */
    @TableId(type = IdType.AUTO)
    private Long enrollmentId;

    /**
     * 课程ID
     */
    private Long courseId;

    /**
     * 学生ID
     */
    private Long studentId;

    /**
     * 平时成绩
     */
    private BigDecimal usualGrade;

    /**
     * 期末成绩
     */
    private BigDecimal finalGrade;

    /**
     * 总成绩
     */
    private BigDecimal grade;

    /**
     * 创建时间（选课时间）
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}