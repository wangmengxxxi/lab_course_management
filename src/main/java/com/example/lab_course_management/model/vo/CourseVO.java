package com.example.lab_course_management.model.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 课程视图对象
 *
 * @author dddwmx
 */
@Data
public class CourseVO {

    /**
     * 课程ID
     */
    private Long courseId;

    /**
     * 课程名称
     */
    private String courseName;

    /**
     * 教师姓名
     */
    private String teacherName;

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
     * 已选学生数
     */
    private Integer enrolledStudents;

    /**
     * 状态 0=待审批 1=已发布
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