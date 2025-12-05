package com.example.lab_course_management.common;

public enum ErrorCode {
    // 成功
    SUCCESS(200, "success", ""),
    // 参数错误
    PARAMS_ERROR(40000, "参数错误", ""),
    // 请求数据为空
    NULL_ERROR(40001, "请求数据为空", ""),

    //未登录
    NOT_LOGIN(40100, "未登录", ""),
    // 无权限
    NO_AUTH(40101, "无权限", ""),

    //密码和校验密码不一致
    NO_PASSWORD_ERROR(40002, "密码和校验密码不一致", ""),

    //账号已经存在
    ACCOUNT_EXIST(40003, "账号已经存在", ""),

    // 账号不能包含特殊字符
    ACCOUNT_ERROR(40004, "账号不能包含特殊字符", ""),

    // 账号不能少于4位
    ACCOUNT_LENGTH_ERROR(40005, "账号不能少于4位", ""),
    //密码不能少于8位
    PASSWORD_LENGTH_ERROR(40006, "密码不能少于8位", ""),
    //注册失败
    REGISTER_ERROR(40007, "注册失败", ""),
    //密码错误
    PASSWORD_ERROR(40008, "密码错误", ""),
    //标签错误
    TAG_ERROR(40009, "标签错误", ""),
    //系统错误
    SYSTEM_ERROR(50000, "系统错误", ""),
    NO_AUTH_ERROR(40102, "无权限", ""),
    //未登录
    NOT_LOGIN_ERROR(40103, "用户未登录", ""),
    //禁止访问
    FORBIDDEN_ERROR(40300, "禁止访问", ""),
    //资源不存在
    NOT_FOUND_ERROR(40400, "资源不存在", "");




    private int code;
    private String message;
    private String description;

    ErrorCode(int code, String message, String description) {
        this.code = code;
        this.message = message;
        this.description = description;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
