package com.example.lab_course_management.model.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 学生查询自己的课表视图对象
 *
 * @author dddwmx
 */
@Data
public class MyCourseVO {

    /**
     * 选课记录ID（用于退课）
     */
    private Long enrollmentId;

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
     * 选课时间
     */
    private LocalDateTime enrollTime;

    /**
     * 实验室名称
     */
    private String labName;

    /**
     * 实验室位置
     */
    private String labLocation;

    /**
     * 星期几（1-7，1表示周一）
     */
    private Integer dayOfWeek;

    /**
     * 时间段名称
     */
    private String timeSlotName;

    /**
     * 起始周
     */
    private Integer startWeek;

    /**
     * 结束周
     */
    private Integer endWeek;
}