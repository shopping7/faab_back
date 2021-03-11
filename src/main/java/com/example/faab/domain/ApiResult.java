package com.example.faab.domain;

import lombok.Data;

@Data
public class ApiResult<T> {
    private String code;
    private String msg;
    private T data;

    public ApiResult(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }
}





