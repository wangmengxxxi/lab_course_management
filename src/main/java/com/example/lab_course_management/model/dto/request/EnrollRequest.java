package com.example.lab_course_management.model.dto.request;


import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 选课请求
 *
 * @author dddwmx
 */
@Data
public class EnrollRequest {

    /**
     * 课程ID
     */
    @NotNull(message = "课程ID不能为空")
    private Long courseId;
}