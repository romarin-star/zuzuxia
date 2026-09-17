package com.zuzuxia.common;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@DisplayName("统一响应体 Result")
class ResultTest {

    @Test
    @DisplayName("success 应携带 code=200 与数据")
    void success_should_carry_code_200_and_data() {
        Result<String> r = Result.success("hello");
        assertEquals(200, r.getCode());
        assertEquals("success", r.getMessage());
        assertEquals("hello", r.getData());
    }

    @Test
    @DisplayName("success 无参时应返回 data=null")
    void success_without_data_should_have_null_data() {
        Result<Void> r = Result.success();
        assertEquals(200, r.getCode());
        assertNull(r.getData());
    }

    @Test
    @DisplayName("error 应携带业务码与枚举默认文案")
    void error_should_carry_business_code_and_message() {
        Result<Void> r = Result.error(ResultCode.BALANCE_NOT_ENOUGH);
        assertEquals(1003, r.getCode());
        assertEquals("余额不足", r.getMessage());
        assertNull(r.getData());
    }

    @Test
    @DisplayName("error 可覆盖默认文案")
    void error_should_allow_custom_message() {
        Result<Void> r = Result.error(ResultCode.BALANCE_NOT_ENOUGH, "余额不足，还差 120.00 元");
        assertEquals(1003, r.getCode());
        assertEquals("余额不足，还差 120.00 元", r.getMessage());
    }
}
