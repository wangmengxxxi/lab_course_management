package com.example.lab_course_management.model.vo;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 课程详情视图对象
 *
 * @author dddwmx
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class CourseDetailVO extends CourseVO {

    /**
     * 实验室信息列表
     */
    private List<LabScheduleVO> labSchedules;

    /**
     * 是否已选课（学生视角）
     */
    private Boolean isEnrolled;

    /**
     * 剩余可选名额
     */
    private Integer remainingSlots;

    /**
     * 课程状态描述
     */
    private String statusDescription;

    /**
     * 实验室排课信息
     */
    @Data
    public static class LabScheduleVO {
        /**
         * 实验室ID
         */
        private Long labId;

        /**
         * 实验室名称
         */
        private String labName;

        /**
         * 实验室位置
         */
        private String labLocation;

        /**
         * 星期几 (1-7)
         */
        private Integer dayOfWeek;

        /**
         * 星期几描述
         */
        private String dayOfWeekDescription;

        /**
         * 教师ID
         */
        private Long teacherId;

        /**
         * 时间段ID
         */
        private Long slotId;

        /**
         * 起始周
         */
        private Integer startWeek;

        /**
         * 结束周
         */
        private Integer endWeek;

        /**
         * 时间段描述
         */
        private String timeSlotDescription;
    }
}