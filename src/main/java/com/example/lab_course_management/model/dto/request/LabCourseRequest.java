package com.example.lab_course_management.model.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;



/**
 * 实验室排课请求
 *
 * @author dddwmx
 */
@Data
public class LabCourseRequest {

    /**
     * 课程ID
     */
    @NotNull(message = "课程ID不能为空")
    private Long courseId;

    /**
     * 实验室ID
     */
    @NotNull(message = "实验室ID不能为空")
    private Long labId;

    /**
     * 时间段ID
     */
    @NotNull(message = "时间段ID不能为空")
    private Long slotId;

    /**
     * 开始周次
     */
    @NotNull(message = "开始周次不能为空")
    @Min(value = 1, message = "开始周次不能小于1")
    @Max(value = 20, message = "开始周次不能大于20")
    private Integer startWeek;

    /**
     * 结束周次
     */
    @NotNull(message = "结束周次不能为空")
    @Min(value = 1, message = "结束周次不能小于1")
    @Max(value = 20, message = "结束周次不能大于20")
    private Integer endWeek;

    /**
     * 星期几 1=周一 7=周日
     */
    @NotNull(message = "星期几不能为空")
    @Min(value = 1, message = "星期几必须在1-7之间")
    @Max(value = 7, message = "星期几必须在1-7之间")
    private Integer dayOfWeek;
}