package com.example.lab_course_management.model.dto.request;

import lombok.Data;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

/**
 * 实验室更新请求
 *
 * @author dddwmx
 */
@Data
public class LabUpdateRequest {

    /**
     * 实验室名称
     */
    @Size(max = 100, message = "实验室名称长度不能超过100字符")
    private String labName;

    /**
     * 位置
     */
    @Size(max = 200, message = "位置长度不能超过200字符")
    private String location;

    /**
     * 容量
     */
    @Min(value = 1, message = "容量必须大于0")
    @Max(value = 1000, message = "容量不能超过1000")
    private Integer capacity;

    /**
     * 管理员ID
     */
    private Long managerId;

    /**
     * 描述
     */
    @Size(max = 500, message = "描述长度不能超过500字符")
    private String description;

    /**
     * 状态 0=正常 1=维护中
     */
    private Integer status;
}