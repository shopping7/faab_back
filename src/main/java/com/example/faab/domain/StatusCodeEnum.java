package com.example.faab.domain;


public enum  StatusCodeEnum {
    UNKNOWN_ERROR("-1", "未知错误"),

    SUCCESS("200", "成功"),
    SUCCESS_UPDATE("201", "用户新建或修改数据成功"),
    SUCCESS_ACCEPT("202", "请求已经进入后台排队"),
    SUCCESS_DELETE("204", "用户删除数据成功"),

    PARAM_ERROR("400", "参数不合法"),
    AUTHORIZED_ERROR("401", "Error:"),
    FORBIDDEN_ERROR("403", "用户得到授权，但是访问被禁止"),
    NOTFOUND_ERROR("404", "资源不存在"),
    NOT_ACCEPTABLE_ERROR("406", "用户请求的格式错误"),
    GONE_ERROR("410", "资源已被删除"),
    INTERNAL_SERVER_ERROR("500", "服务器内部错误");


    private String code;
    private String message;

    StatusCodeEnum(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}