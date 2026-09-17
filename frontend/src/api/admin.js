import request from './request'

/**
 * 管理端接口。所有路径均在 /api/admin/** 下，后端 AdminInterceptor 会校验 ADMIN 角色。
 */

/* ---------- 用户管理 ---------- */
export const listUsers = (params) => request.get('/admin/users', { params })

/** 封禁 / 解封用户。banned = true 表示封禁 */
export const setUserBanned = (userId, banned) =>
  request.put(`/admin/users/${userId}/status`, null, { params: { banned } })

/* ---------- 物品管理 ---------- */
export const listAdminItems = (params) => request.get('/admin/items', { params })

/** 违规下架 / 恢复上架 */
export const setItemDelisted = (itemId, delisted) =>
  request.put(`/admin/items/${itemId}/status`, null, { params: { delisted } })

/* ---------- 订单管理 ---------- */
export const listAdminOrders = (params) => request.get('/admin/orders', { params })

/** 介入处理：从冻结押金中扣款赔付出租人 */
export const deductDeposit = (orderId, data) =>
  request.put(`/admin/orders/${orderId}/deduct`, data)

/* ---------- 分类管理 ---------- */
export const listAdminCategories = () => request.get('/admin/categories')

export const createCategory = (data) => request.post('/admin/categories', data)

export const updateCategory = (id, data) => request.put(`/admin/categories/${id}`, data)

export const deleteCategory = (id) => request.delete(`/admin/categories/${id}`)

/* ---------- 概览统计 ---------- */
export const getStats = () => request.get('/admin/stats')
