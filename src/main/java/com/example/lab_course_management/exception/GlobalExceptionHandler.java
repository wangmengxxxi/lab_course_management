package com.example.lab_course_management.exception;

import cn.dev33.satoken.exception.NotLoginException;
import cn.dev33.satoken.exception.NotPermissionException;
import com.example.lab_course_management.common.ErrorCode;
import com.example.lab_course_management.common.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import jakarta.servlet.http.HttpServletRequest;

/**
 * 全局异常处理器
 *
 * @author dddwmx
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * 自定义业务异常处理
     */
    @ExceptionHandler(BussniesException.class)
    public Result<?> bussniesExceptionHandler(BussniesException e) {
        log.error("BussniesException: {}, {}", e.getCode(), e.getDescription(), e);
        return Result.error(e.getCode(), e.getMessage(), e.getDescription());
    }

    /**
     * Sa-Token 未登录异常处理
     */
    @ExceptionHandler(NotLoginException.class)
    public Result<?> notLoginExceptionHandler(NotLoginException e, HttpServletRequest request) {
        // 检查是否是Knife4j/Swagger的探测请求
        String userAgent = request.getHeader("User-Agent");
        String uri = request.getRequestURI();

        if (isKnife4jRequest(userAgent, uri)) {
            // Knife4j探测请求，记录debug日志但不返回错误
            log.debug("[DEBUG] Knife4j探测请求跳过登录验证: {}", uri);
            // 返回一个友好的响应，让Knife4j能够正常工作
            return Result.success("API文档探测请求处理成功");
        }

        // 正常的未登录异常处理，改为debug级别
        log.debug("NotLoginException: {}", e.getMessage());
        return Result.error(ErrorCode.NOT_LOGIN);
    }

    /**
     * 判断是否是Knife4j/Swagger的探测请求
     */
    private boolean isKnife4jRequest(String userAgent, String uri) {
        if (userAgent == null) {
            return false;
        }

        // 检查User-Agent中是否包含Knife4j或Swagger相关标识
        String lowerUserAgent = userAgent.toLowerCase();
        return lowerUserAgent.contains("knife4j")
                || lowerUserAgent.contains("swagger")
                || lowerUserAgent.contains("openapi")
                || lowerUserAgent.contains("postman")
                || lowerUserAgent.contains("httpclient")
                || lowerUserAgent.contains("curl")
                || uri.contains("/v3/api-docs")
                || uri.contains("/doc.html");
    }

    /**
     * Sa-Token 无权限异常处理
     */
    @ExceptionHandler(NotPermissionException.class)
    public Result<?> notPermissionExceptionHandler(NotPermissionException e) {
        log.error("NotPermissionException: {}", e.getMessage(), e);
        return Result.error(ErrorCode.NO_AUTH);
    }

    /**
     * 参数校验异常处理
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result<?> methodArgumentNotValidExceptionHandler(MethodArgumentNotValidException e) {
        log.error("MethodArgumentNotValidException: {}", e.getMessage(), e);
        String message = e.getBindingResult().getFieldError() != null ?
                e.getBindingResult().getFieldError().getDefaultMessage() : "参数校验失败";
        return Result.error(ErrorCode.PARAMS_ERROR.getCode(), message);
    }

    /**
     * 系统异常处理
     */
    @ExceptionHandler(RuntimeException.class)
    public Result<?> runtimeExceptionHandler(RuntimeException e) {
        log.error("RuntimeException: ", e);
        return Result.error(ErrorCode.SYSTEM_ERROR);
    }

    /**
     * 其他异常处理
     */
    @ExceptionHandler(Exception.class)
    public Result<?> exceptionHandler(Exception e) {
        log.error("Exception: ", e);
        return Result.error(ErrorCode.SYSTEM_ERROR);
    }
}