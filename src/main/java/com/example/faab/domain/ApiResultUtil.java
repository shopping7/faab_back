package com.example.faab.domain;

public class ApiResultUtil<T> {

    // 200
    public static ApiResult successReturn(Object data) {
        ApiResult apiResult = new ApiResult(StatusCodeEnum.SUCCESS.getCode(), StatusCodeEnum.SUCCESS.getMessage());
        apiResult.setData(data);
        return apiResult;
    }

    public static ApiResult success() {
        return new ApiResult(StatusCodeEnum.SUCCESS.getCode(), StatusCodeEnum.SUCCESS.getMessage());
    }

    // 201
    public static ApiResult successUpdate() {
        ApiResult apiResult = new ApiResult(StatusCodeEnum.SUCCESS_UPDATE.getCode(), StatusCodeEnum.SUCCESS_UPDATE.getMessage());
        return apiResult;
    }

    public static ApiResult successAccept() {
        return  new ApiResult(StatusCodeEnum.SUCCESS_ACCEPT.getCode(), StatusCodeEnum.SUCCESS_ACCEPT.getMessage());
    }

    public static ApiResult successDelete() {
        return  new ApiResult(StatusCodeEnum.SUCCESS_DELETE.getCode(), StatusCodeEnum.SUCCESS_DELETE.getMessage());
    }

    public static ApiResult errorParam() {
        return  new ApiResult(StatusCodeEnum.PARAM_ERROR.getCode(), StatusCodeEnum.PARAM_ERROR.getMessage());
    }

    public static ApiResult errorParam(String msg) {
        ApiResult result = new ApiResult(StatusCodeEnum.PARAM_ERROR.getCode(), StatusCodeEnum.PARAM_ERROR.getMessage());
        result.setMsg(result.getMsg()+msg);
        return  result;
    }

    // 401
    public static ApiResult errorAuthorized() {
        return  new ApiResult(StatusCodeEnum.AUTHORIZED_ERROR.getCode(), StatusCodeEnum.AUTHORIZED_ERROR.getMessage());
    }

    public static ApiResult errorAuthorized(String msg) {
        ApiResult result = new ApiResult(StatusCodeEnum.AUTHORIZED_ERROR.getCode(), StatusCodeEnum.AUTHORIZED_ERROR.getMessage());
        result.setMsg(result.getMsg() + msg);
        return  result;
    }

    public static ApiResult errorAuthorized(Object data) {
        ApiResult result = new ApiResult(StatusCodeEnum.AUTHORIZED_ERROR.getCode(), StatusCodeEnum.AUTHORIZED_ERROR.getMessage());
        result.setData(data);
        return  result;
    }


    public static ApiResult errorForbidden() {
        return  new ApiResult(StatusCodeEnum.FORBIDDEN_ERROR.getCode(), StatusCodeEnum.FORBIDDEN_ERROR.getMessage());
    }

    public static ApiResult errorNotFound() {
        return  new ApiResult(StatusCodeEnum.NOTFOUND_ERROR.getCode(), StatusCodeEnum.NOTFOUND_ERROR.getMessage());
    }

    public static ApiResult errorNotAcceptable() {
        return  new ApiResult(StatusCodeEnum.NOT_ACCEPTABLE_ERROR.getCode(), StatusCodeEnum.NOT_ACCEPTABLE_ERROR.getMessage());
    }

    public static ApiResult errorGone() {
        return  new ApiResult(StatusCodeEnum.GONE_ERROR.getCode(), StatusCodeEnum.GONE_ERROR.getMessage());
    }

    public static ApiResult errorInternalServer() {
        return  new ApiResult(StatusCodeEnum.INTERNAL_SERVER_ERROR.getCode(), StatusCodeEnum.INTERNAL_SERVER_ERROR.getMessage());
    }

    public static ApiResult errorInternalServer(Object data) {
        ApiResult apiResult = new ApiResult(StatusCodeEnum.INTERNAL_SERVER_ERROR.getCode(), StatusCodeEnum.INTERNAL_SERVER_ERROR.getMessage());
        apiResult.setData(data);
        return  apiResult;
    }

}
