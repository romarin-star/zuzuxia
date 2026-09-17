package com.zuzuxia.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zuzuxia.common.Result;
import com.zuzuxia.common.ResultCode;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.IOException;

/**
 * 管理员拦截器。仅作用于 {@code /api/admin/**}。
 *
 * <p>必须在 {@link AuthInterceptor} 之后注册，依赖其写入的 {@link UserContext}。
 */
@Component
@RequiredArgsConstructor
public class AdminInterceptor implements HandlerInterceptor {

    private final ObjectMapper objectMapper;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws IOException {
        if (UserContext.isAdmin()) {
            return true;
        }
        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(objectMapper.writeValueAsString(Result.error(ResultCode.FORBIDDEN)));
        return false;
    }
}
