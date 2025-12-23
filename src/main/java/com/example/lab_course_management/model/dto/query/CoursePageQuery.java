package com.example.lab_course_management.model.dto.query;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 课程分页查询请求
 *
 * @author dddwmx
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class CoursePageQuery extends BasePageQuery {

    /**
     * 课程状态(0-待审批, 1-已通过, 2-已拒绝)
     */
    private Integer status;

    /**
     * 教师ID
     */
    private Long teacherId;
}
