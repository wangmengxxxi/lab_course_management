package com.example.lab_course_management.model.vo;

import lombok.Data;

/**
 * 热门课程VO
 */
@Data
public class HotCourseVO {

    /**
     * 课程ID
     */
    private Long courseId;

    /**
     * 课程名称
     */
    private String courseName;

    /**
     * 选课人数
     */
    private Integer enrollmentCount;

    /**
     * 教师名称（需要关联查询）
     */
    private String teacherName;
}