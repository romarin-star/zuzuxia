package com.zuzuxia.security;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

@DisplayName("JWT 工具")
class JwtUtilTest {

    private static final String SECRET =
            "zuzuxia-secret-key-for-course-project-please-change-in-production-2026";

    private final JwtUtil jwtUtil = new JwtUtil(SECRET, 24);

    @Test
    @DisplayName("生成后解析应得到相同用户信息")
    void generate_then_parse_should_return_same_user() {
        String token = jwtUtil.generate(1L, "alice", "USER");
        assertNotNull(token);
        assertEquals(1L, jwtUtil.getUserId(token));
        assertEquals("alice", jwtUtil.getUsername(token));
        assertEquals("USER", jwtUtil.getRole(token));
    }

    @Test
    @DisplayName("管理员角色应能正确往返")
    void admin_role_should_round_trip() {
        String token = jwtUtil.generate(9L, "admin", "ADMIN");
        assertEquals(9L, jwtUtil.getUserId(token));
        assertEquals("ADMIN", jwtUtil.getRole(token));
    }

    @Test
    @DisplayName("篡改或格式错误的 token 应返回 null 而非抛异常")
    void parse_tampered_token_should_return_null() {
        assertNull(jwtUtil.getUserId("not.a.valid.token"));
        assertNull(jwtUtil.getUserId(""));
        assertNull(jwtUtil.getUserId(null));
    }

    @Test
    @DisplayName("用其他密钥签发的 token 应被拒绝")
    void token_signed_with_other_secret_should_be_rejected() {
        JwtUtil other = new JwtUtil("another-secret-key-that-is-long-enough-for-hs256-algorithm", 24);
        String foreignToken = other.generate(1L, "mallory", "ADMIN");
        assertNull(jwtUtil.getUserId(foreignToken));
    }
}
