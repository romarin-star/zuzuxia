package com.zuzuxia.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * 连通性接口测试。
 *
 * <p>刻意使用 standaloneSetup 而非 @WebMvcTest：本用例只验证 Controller 的返回结构，
 * 不应被拦截器、数据源等外部依赖牵连，这样在 MySQL 未启动时也能跑。
 */
@DisplayName("连通性接口 /api/ping")
class PingControllerTest {

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(new PingController()).build();
    }

    @Test
    @DisplayName("应返回 code=200 且 data.app=zuzuxia")
    void ping_should_return_code_200_and_app_name() throws Exception {
        mockMvc.perform(get("/api/ping"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("success"))
                .andExpect(jsonPath("$.data.app").value("zuzuxia"))
                .andExpect(jsonPath("$.data.time").exists());
    }
}
