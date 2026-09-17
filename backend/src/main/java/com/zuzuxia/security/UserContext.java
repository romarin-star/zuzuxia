package com.zuzuxia.security;

/**
 * 当前登录用户上下文。
 *
 * <p>由 {@link AuthInterceptor} 在 preHandle 中写入、afterCompletion 中清理。
 * <b>务必成对</b>，否则 Tomcat 线程池复用线程时会串号，把 A 的操作记到 B 头上。
 */
public final class UserContext {

    private UserContext() {
    }

    private record LoginUser(Long userId, String username, String role) {
    }

    private static final ThreadLocal<LoginUser> HOLDER = new ThreadLocal<>();

    public static void set(Long userId, String username, String role) {
        HOLDER.set(new LoginUser(userId, username, role));
    }

    public static Long getUserId() {
        LoginUser u = HOLDER.get();
        return u == null ? null : u.userId();
    }

    public static String getUsername() {
        LoginUser u = HOLDER.get();
        return u == null ? null : u.username();
    }

    public static String getRole() {
        LoginUser u = HOLDER.get();
        return u == null ? null : u.role();
    }

    public static boolean isAdmin() {
        return "ADMIN".equals(getRole());
    }

    /** 取当前用户 id，未登录直接抛 401（供 Service 层便捷调用） */
    public static Long requireUserId() {
        Long id = getUserId();
        if (id == null) {
            throw com.zuzuxia.common.BizException.of(com.zuzuxia.common.ResultCode.UNAUTHORIZED);
        }
        return id;
    }

    public static void clear() {
        HOLDER.remove();
    }
}
