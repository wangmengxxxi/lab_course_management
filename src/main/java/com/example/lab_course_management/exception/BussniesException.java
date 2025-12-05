package com.example.lab_course_management.exception;


import com.example.lab_course_management.common.ErrorCode;

public class BussniesException extends RuntimeException {
   private int code;

   private String description;

   // 构造函数传入错误码、错误信息、错误描述
   public BussniesException(int code, String message, String description) {
       super(message);
       this.code = code;
       this.description = description;
   }

   // 构造函数传入errorCode
    public BussniesException(ErrorCode errorCode) {
       super(errorCode.getMessage());
       this.code = errorCode.getCode();
       this.description = errorCode.getDescription();
    }

    // 构造函数传入errorCode和错误描述
    public BussniesException(ErrorCode errorCode, String description) {
        super(errorCode.getMessage());
        this.code = errorCode.getCode();
        this.description = description;
    }
    //生成getter和setter方法
    public int getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }
    public void setCode(int code) {
        this.code = code;
    }
    public void setDescription(String description) {
        this.description = description;
    }
}
