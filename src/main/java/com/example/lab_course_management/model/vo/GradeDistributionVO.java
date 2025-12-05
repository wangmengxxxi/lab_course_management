package com.example.lab_course_management.model.vo;

import lombok.Data;
import java.util.List;

/**
 * 成绩分布VO
 */
@Data
public class GradeDistributionVO {

    /**
     * 课程ID
     */
    private Long courseId;

    /**
     * 课程名称
     */
    private String courseName;

    /**
     * 总成绩分布
     */
    private List<GradeRangeVO> totalGradeDistribution;

    /**
     * 平时成绩分布
     */
    private List<GradeRangeVO> usualGradeDistribution;

    /**
     * 期末成绩分布
     */
    private List<GradeRangeVO> finalGradeDistribution;

    /**
     * 统计学生总数
     */
    private Integer totalStudents;

    /**
     * 平均分
     */
    private Double averageGrade;

    /**
     * 最高分
     */
    private Double maxGrade;

    /**
     * 最低分
     */
    private Double minGrade;

    @Data
    public static class GradeRangeVO {
        /**
         * 成绩等级（优秀、良好、中等、及格、不及格）
         */
        private String gradeLevel;

        /**
         * 人数
         */
        private Integer count;

        /**
         * 百分比
         */
        private Double percentage;

        /**
         * 分数范围
         */
        private String scoreRange;
    }
}