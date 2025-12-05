package com.example.lab_course_management.model.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 实验室排课响应
 *
 * @author dddwmx
 */
@Data
public class LabCourseVO {

    /**
     * 排课ID
     */
    private Long id;

    /**
     * 课程信息
     */
    private CourseInfoVO course;

    /**
     * 实验室信息
     */
    private LabInfoVO laboratory;

    /**
     * 时间段信息
     */
    private TimeSlotInfoVO timeSlot;

    /**
     * 开始周次
     */
    private Integer startWeek;

    /**
     * 结束周次
     */
    private Integer endWeek;

    /**
     * 星期几
     */
    private Integer dayOfWeek;

    /**
     * 星期几文本
     */
    private String dayOfWeekText;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    @Data
    public static class CourseInfoVO {
        private Long courseId;
        private String courseName;
        private String semester;
        private String description;
        private Integer credit;
        private Long teacherId;
    }

    @Data
    public static class LabInfoVO {
        private Long labId;
        private String labName;
        private String location;
        private Integer capacity;
        private String description;
    }

    @Data
    public static class TimeSlotInfoVO {
        private Long slotId;
        private String slotName;
        private String startTime;
        private String endTime;
    }
}