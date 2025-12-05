package com.example.lab_course_management.common;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 统一分页响应结果类
 *
 * @author dddwmx
 */
@Data
public class PageResult<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 数据列表
     */
    private List<T> records;

    /**
     * 总记录数
     */
    private Long total;

    /**
     * 当前页码
     */
    private Long current;

    /**
     * 每页大小
     */
    private Long size;

    /**
     * 总页数
     */
    private Long pages;

    /**
     * 是否有上一页
     */
    private Boolean hasPrevious;

    /**
     * 是否有下一页
     */
    private Boolean hasNext;

    public PageResult() {
    }

    public PageResult(List<T> records, Long total, Long current, Long size) {
        this.records = records;
        this.total = total;
        this.current = current;
        this.size = size;
        this.pages = (total + size - 1) / size; // 计算总页数
        this.hasPrevious = current > 1;
        this.hasNext = current < pages;
    }

    /**
     * 静态方法创建PageResult
     *
     * @param records 数据列表
     * @param total 总记录数
     * @param current 当前页码
     * @param size 每页大小
     * @param <T> 泛型类型
     * @return PageResult实例
     */
    public static <T> PageResult<T> of(List<T> records, Long total, Long current, Long size) {
        return new PageResult<>(records, total, current, size);
    }

    /**
     * 空分页结果
     *
     * @param <T> 泛型类型
     * @return 空的PageResult实例
     */
    public static <T> PageResult<T> empty() {
        return new PageResult<>(null, 0L, 1L, 10L);
    }
}