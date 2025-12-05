package com.example.lab_course_management.common;

import lombok.Data;

import lombok.Data;

import java.io.Serializable;

/**
 * 统一返回结果类
 *
 * @author dddwmx
 */
@Data
public class Result<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 响应码
     */
    private int code;

    /**
     * 数据
     */
    private T data;

    /**
     * 消息
     */
    private String message;

    /**
     * 详细描述
     */
    private String description;

    public Result() {
    }

    public Result(int code, T data, String message, String description) {
        this.code = code;
        this.data = data;
        this.message = message;
        this.description = description;
    }

    /**
     * 成功返回结果
     *
     * @param data 获取的数据
     */
    public static <T> Result<T> success(T data) {
        return new Result<>(0, data, "成功", "");
    }

    /**
     * 成功返回结果
     *
     * @param data    获取的数据
     * @param message 提示信息
     */
    public static <T> Result<T> success(T data, String message) {
        return new Result<>(0, data, message, "");
    }

    /**
     * 成功返回结果（无数据）
     */
    public static <T> Result<T> success() {
        return new Result<>(0, null, "成功", "");
    }

    /**
     * 成功返回结果（无数据）
     *
     * @param message 提示信息
     */
    public static <T> Result<T> success(String message) {
        return new Result<>(0, null, message, "");
    }

    /**
     * 失败返回结果
     *
     * @param errorCode 错误码
     */
    public static <T> Result<T> error(ErrorCode errorCode) {
        return new Result<>(errorCode.getCode(), null, errorCode.getMessage(), errorCode.getDescription());
    }

    /**
     * 失败返回结果
     *
     * @param code        响应码
     * @param message     错误信息
     * @param description 详细描述
     */
    public static <T> Result<T> error(int code, String message, String description) {
        return new Result<>(code, null, message, description);
    }

    /**
     * 失败返回结果
     *
     * @param errorCode 错误码
     * @param message   错误信息
     */
    public static <T> Result<T> error(ErrorCode errorCode, String message) {
        return new Result<>(errorCode.getCode(), null, message, errorCode.getDescription());
    }

    /**
     * 失败返回结果
     *
     * @param code    响应码
     * @param message 错误信息
     */
    public static <T> Result<T> error(int code, String message) {
        return new Result<>(code, null, message, "");
    }

    /**
     * 失败返回结果
     *
     * @param message 错误信息
     */
    public static <T> Result<T> error(String message) {
        return new Result<>(400, null, message, "");
    }
}