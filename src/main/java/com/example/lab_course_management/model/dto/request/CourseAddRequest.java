package com.example.lab_course_management.model.dto.request;


import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 课程添加请求
 *
 * @author dddwmx
 */
@Data
public class CourseAddRequest {

    /**
     * 课程名称
     */
    @NotBlank(message = "课程名称不能为空")
    @Size(max = 100, message = "课程名称长度不能超过100字符")
    private String courseName;

    /**
     * 教师ID
     */
    private Long teacherId;

    /**
     * 学期
     */
    @NotBlank(message = "学期不能为空")
    @Size(max = 50, message = "学期长度不能超过50字符")
    private String semester;

    /**
     * 描述
     */
    @Size(max = 500, message = "描述长度不能超过500字符")
    private String description;

    /**
     * 学分
     */
    @Min(value = 1, message = "学分必须大于0")
    @Max(value = 10, message = "学分不能超过10")
    private Integer credit;

    /**
     * 最大学生数
     */
    @Min(value = 1, message = "最大学生数必须大于0")
    @Max(value = 1000, message = "最大学生数不能超过1000")
    private Integer maxStudents;
}