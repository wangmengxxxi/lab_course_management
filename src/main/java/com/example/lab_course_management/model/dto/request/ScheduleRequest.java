package com.example.lab_course_management.model.dto.request;


import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 排课审批请求
 *
 * @author dddwmx
 */
@Data
public class ScheduleRequest {

    /**
     * 实验室ID
     */
    @NotNull(message = "实验室ID不能为空")
    private Long labId;

    /**
     * 星期几 (1-7)
     */
    @NotNull(message = "星期几不能为空")
    private Integer dayOfWeek;

    /**
     * 教师ID（该时间段的授课老师）
     */
    private Long teacherId;

    /**
     * 时间段ID
     */
    @NotNull(message = "时间段ID不能为空")
    private Long slotId;

    /**
     * 起始周
     */
    @NotNull(message = "起始周不能为空")
    private Integer startWeek;

    /**
     * 结束周
     */
    @NotNull(message = "结束周不能为空")
    private Integer endWeek;
}