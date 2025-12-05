package com.example.lab_course_management.model.dto.request;


import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 学生选课请求
 *
 * @author dddwmx
 */
@Data
public class StudentCourseRequest {

    /**
     * 课程ID
     */
    @NotNull(message = "课程ID不能为空")
    private Long courseId;

    /**
     * 学生ID（可选，如果不传则使用当前登录用户ID）
     */
    private Long studentId;
}