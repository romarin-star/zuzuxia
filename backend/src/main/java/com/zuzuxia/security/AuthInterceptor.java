package com.zuzuxia.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zuzuxia.common.Result;
import com.zuzuxia.common.ResultCode;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.IOException;

/**
 * 登录拦截器：解析 {@code Authorization: Bearer <token>} 并写入 {@link UserContext}。
 *
 * <p>哪些路径需要登录由 {@code WebMvcConfig} 的 excludePathPatterns 决定，
 * 本类不做路径判断。
 */
@Component
@RequiredArgsConstructor
public class AuthInterceptor implements HandlerInterceptor {

    private static final String BEARER_PREFIX = "Bearer ";

    private final JwtUtil jwtUtil;
    private final ObjectMapper objectMapper;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws IOException {
        // 浏览器跨域预检请求不带 token，必须放行
        if (HttpMethod.OPTIONS.matches(request.getMethod())) {
            return true;
        }

        String header = request.getHeader("Authorization");
        if (header == null || !header.startsWith(BEARER_PREFIX)) {
            writeUnauthorized(response);
            return false;
        }

        String token = header.substring(BEARER_PREFIX.length()).trim();
        Long userId = jwtUtil.getUserId(token);
        if (userId == null) {
            writeUnauthorized(response);
            return false;
        }

        UserContext.set(userId, jwtUtil.getUsername(token), jwtUtil.getRole(token));
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response,
                                Object handler, Exception ex) {
        // 必须清理，否则线程池复用会串号
        UserContext.clear();
    }

    private void writeUnauthorized(HttpServletResponse response) throws IOException {
        // 统一返回 HTTP 200 + body 中的业务码，前端只在响应拦截器里判一次 code
        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(objectMapper.writeValueAsString(Result.error(ResultCode.UNAUTHORIZED)));
    }
}
