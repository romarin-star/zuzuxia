package com.zuzuxia.common;

import lombok.Getter;

/**
 * 全局业务状态码。
 *
 * <p>约定：业务码从 1001 起，全部集中在此枚举，禁止在业务代码里写裸数字。
 *
 * @see <a href="../../../../../../docs/specs/2026-09-17-zuzuxia-底座设计.md">设计文档 8.4 节</a>
 */
@Getter
public enum ResultCode {

    /* ---------- 通用 ---------- */
    SUCCESS(200, "success"),
    PARAM_ERROR(400, "参数校验失败"),
    UNAUTHORIZED(401, "未登录或登录已过期"),
    FORBIDDEN(403, "无权限访问"),
    NOT_FOUND(404, "资源不存在"),
    CONFLICT(409, "操作冲突"),
    SYSTEM_ERROR(500, "系统异常，请稍后重试"),

    /* ---------- 业务 1xxx ---------- */
    USERNAME_EXISTS(1001, "用户名已存在"),
    LOGIN_FAILED(1002, "用户名或密码错误"),
    BALANCE_NOT_ENOUGH(1003, "余额不足"),
    PERIOD_OCCUPIED(1004, "该时段已被租用"),
    ORDER_STATUS_INVALID(1005, "当前订单状态不允许此操作"),
    RENT_OWN_ITEM(1006, "请勿租借自己的物品"),
    ORDER_NOT_FINISHED(1007, "订单未完成，无法评价"),
    ITEM_OFF_SHELF(1008, "物品已下架"),
    USER_BANNED(1009, "账号已被封禁，请联系管理员"),
    ITEM_NOT_FOUND(1010, "物品不存在"),
    ORDER_NOT_FOUND(1011, "订单不存在"),
    NOT_PERIOD_OWNER(1012, "该时段不属于你发布的物品"),
    REVIEW_ALREADY_EXISTS(1013, "你已经评价过该订单"),
    NO_PERMISSION_FOR_ORDER(1014, "你不是该订单的当事人"),
    ITEM_HAS_ACTIVE_ORDER(1015, "物品有进行中的订单，无法删除");

    private final int code;
    private final String message;

    ResultCode(int code, String message) {
        this.code = code;
        this.message = message;
    }
}
