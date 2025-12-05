package com.example.lab_course_management.model.dto.query;


import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;

/**
 * 基础分页查询请求
 *
 * @author dddwmx
 */
@Data
public class BasePageQuery {

    /**
     * 页码（从1开始）
     */
    @Min(value = 1, message = "页码必须大于0")
    private Integer pageNum = 1;

    /**
     * 每页大小
     */
    @Min(value = 1, message = "每页大小必须大于0")
    @Max(value = 100, message = "每页大小不能超过100")
    private Integer pageSize = 10;
}