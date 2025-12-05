package com.example.lab_course_management.model.vo;

import lombok.Data;
import java.util.List;

/**
 * 实验室使用统计VO
 */
@Data
public class LabUsageStatisticsVO {

    /**
     * 实验室使用率列表
     */
    private List<LabUsageVO> labUsageList;

    /**
     * 平均使用率
     */
    private Double averageUsageRate;

    /**
     * 空闲率
     */
    private Double idleRate;

    @Data
    public static class LabUsageVO {
        /**
         * 实验室ID
         */
        private Long labId;

        /**
         * 实验室名称
         */
        private String labName;

        /**
         * 实验室容量
         */
        private Integer capacity;

        /**
         * 实际使用人数
         */
        private Integer actualUsage;

        /**
         * 使用率（百分比）
         */
        private Double usageRate;

        /**
         * 空闲容量
         */
        private Integer idleCapacity;
    }
}