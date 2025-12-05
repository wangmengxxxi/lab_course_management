package com.example.lab_course_management.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 学生课程成绩VO
 *
 * @author dddwmx
 */
@Data
@Schema(description = "学生课程成绩信息")
public class StudentCourseGradeVO {

    /**
     * 选课记录ID
     */
    @Schema(description = "选课记录ID", example = "1")
    private Long enrollmentId;

    /**
     * 课程ID
     */
    @Schema(description = "课程ID", example = "1")
    private Long courseId;

    /**
     * 课程名称
     */
    @Schema(description = "课程名称", example = "Java程序设计")
    private String courseName;

    /**
     * 学生ID
     */
    @Schema(description = "学生ID", example = "1001")
    private Long studentId;

    /**
     * 学生姓名
     */
    @Schema(description = "学生姓名", example = "张三")
    private String studentName;

    /**
     * 学生学号
     */
    @Schema(description = "学生学号", example = "2021001")
    private String studentNumber;

    /**
     * 平时成绩
     */
    @Schema(description = "平时成绩", example = "85.5")
    private BigDecimal usualGrade;

    /**
     * 期末成绩
     */
    @Schema(description = "期末成绩", example = "90.0")
    private BigDecimal finalGrade;

    /**
     * 总成绩
     */
    @Schema(description = "总成绩", example = "88.5")
    private BigDecimal grade;

    /**
     * 选课时间
     */
    @Schema(description = "选课时间", example = "2024-01-15 10:30:00")
    private String enrollmentTime;

    /**
     * 成绩等级（优秀/良好/中等/及格/不及格）
     */
    @Schema(description = "成绩等级（优秀/良好/中等/及格/不及格）",
           allowableValues = {"优秀", "良好", "中等", "及格", "不及格", "未录入"},
           example = "良好")
    private String gradeLevel;
}