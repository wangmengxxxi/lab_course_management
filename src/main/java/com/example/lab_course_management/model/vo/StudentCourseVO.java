package com.example.lab_course_management.model.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 学生选课响应
 *
 * @author dddwmx
 */
@Data
public class StudentCourseVO {

    /**
     * 选课记录ID
     */
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
     * 学生信息
     */
    private StudentInfoVO student;

    /**
     * 课程信息
     */
    private CourseInfoVO course;

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
     * 选课时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    @Data
    public static class StudentInfoVO {
        private Long userId;
        private String account;
        private String realName;
        private String email;
        private String phone;
    }

    @Data
    public static class CourseInfoVO {
        private Long courseId;
        private String courseName;
        private String semester;
        private String description;
        private Integer credit;
        private Long teacherId;
        private String teacherName;
        private Integer status;
        private Integer maxStudents;
    }
}