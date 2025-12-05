package com.example.lab_course_management.model.dto.query;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 课程成绩查询请求
 *
 * @author dddwmx
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(title = "课程成绩查询", description = "用于查询指定课程的学生成绩列表，支持分页、搜索和排序")
public class CourseGradeQuery extends BasePageQuery {

    /**
     * 课程ID
     */
    @NotNull(message = "课程ID不能为空")
    @Schema(description = "课程ID", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long courseId;

    /**
     * 学生姓名（模糊查询）
     */
    @Schema(description = "学生姓名（支持模糊查询，如输入'张'可以查询姓张的学生）",
           example = "张三", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String studentName;

    /**
     * 成绩排序字段
     */
    @Schema(description = "成绩排序字段",
           allowableValues = {"grade", "usual_grade", "final_grade"},
           example = "grade", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String sortBy;

    /**
     * 排序方式
     */
    @Schema(description = "排序方式",
           allowableValues = {"asc", "desc"},
           example = "desc", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String sortOrder = "desc";
}