package com.zuzuxia.common;

import lombok.Getter;

/**
 * 业务异常。Service 层遇到业务规则不满足时一律抛此异常，
 * 由 {@link GlobalExceptionHandler} 统一转换为响应体。
 *
 * <p>Controller 层不要写业务 if 判断，也不要自己拼错误响应。
 */
@Getter
public class BizException extends RuntimeException {

    private final ResultCode resultCode;

    public BizException(ResultCode resultCode) {
        super(resultCode.getMessage());
        this.resultCode = resultCode;
    }

    public BizException(ResultCode resultCode, String message) {
        super(message);
        this.resultCode = resultCode;
    }

    /** 便捷抛出的静态方法，让调用处读起来更顺：throw BizException.of(BALANCE_NOT_ENOUGH); */
    public static BizException of(ResultCode resultCode) {
        return new BizException(resultCode);
    }

    public static BizException of(ResultCode resultCode, String message) {
        return new BizException(resultCode, message);
    }
}
