package com.zuzuxia.common;

import lombok.Data;

/**
 * 统一响应体。所有接口一律返回此结构。
 *
 * <pre>
 * { "code": 200, "message": "success", "data": { ... } }
 * </pre>
 */
@Data
public class Result<T> {

    private int code;
    private String message;
    private T data;

    public static <T> Result<T> success(T data) {
        Result<T> r = new Result<>();
        r.code = ResultCode.SUCCESS.getCode();
        r.message = ResultCode.SUCCESS.getMessage();
        r.data = data;
        return r;
    }

    public static <T> Result<T> success() {
        return success(null);
    }

    public static <T> Result<T> error(ResultCode resultCode) {
        Result<T> r = new Result<>();
        r.code = resultCode.getCode();
        r.message = resultCode.getMessage();
        return r;
    }

    /** 覆盖默认提示语，用于携带更具体的业务信息（如"余额不足，还差 120.00 元"） */
    public static <T> Result<T> error(ResultCode resultCode, String message) {
        Result<T> r = error(resultCode);
        r.message = message;
        return r;
    }
}
