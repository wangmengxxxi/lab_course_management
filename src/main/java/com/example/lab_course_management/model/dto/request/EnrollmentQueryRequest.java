package com.example.lab_course_management.model.dto.request;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 选课查询请求
 *
 * @author dddwmx
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class EnrollmentQueryRequest extends com.example.lab_course_management.model.dto.query.BasePageQuery {

    /**
     * 课程ID
     */
    private Long courseId;

    /**
     * 学生ID
     */
    private Long studentId;
}