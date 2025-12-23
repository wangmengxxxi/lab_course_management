package com.example.lab_course_management.model.vo;

import lombok.Data;

/**
 * 系统概览统计VO
 *
 * @author dddwmx
 */
@Data
public class OverviewStatisticsVO {

    /**
     * 用户总数
     */
    private Long totalUsers;

    /**
     * 课程总数
     */
    private Long totalCourses;

    /**
     * 实验室总数
     */
    private Long totalLabs;

    /**
     * 公告总数
     */
    private Long totalAnnouncements;

    /**
     * 学生总数
     */
    private Long totalStudents;

    /**
     * 教师总数
     */
    private Long totalTeachers;

    /**
     * 已发布课程数
     */
    private Long publishedCourses;

    /**
     * 待审批课程数
     */
    private Long pendingCourses;
}
