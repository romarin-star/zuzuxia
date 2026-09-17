import request from './request'

/**
 * 订单接口，对应 openapi.yaml 的 /orders 分组。
 *
 * 状态机（详见设计文档 7.3）：
 *   PENDING --confirm--> RESERVED --pick--> RENTING --return--> RETURNING --finish--> FINISHED
 *           --reject---> REJECTED
 *           --cancel---> CANCELLED
 */

/** 提交租借申请 → PENDING */
export const createOrder = (data) => request.post('/orders', data)

/** 我的订单。type: 'rented' 我租入的 / 'lent' 我租出的 */
export const listOrders = (params) => request.get('/orders', { params })

export const getOrder = (id) => request.get(`/orders/${id}`)

/** 出租人同意 → RESERVED（扣租金 + 冻结押金） */
export const confirmOrder = (id) => request.put(`/orders/${id}/confirm`)

/** 出租人拒绝 → REJECTED */
export const rejectOrder = (id, reason) =>
  request.put(`/orders/${id}/reject`, null, { params: { reason } })

/** 租用人取消 → CANCELLED */
export const cancelOrder = (id) => request.put(`/orders/${id}/cancel`)

/** 租用人确认取件 → RENTING */
export const pickOrder = (id) => request.put(`/orders/${id}/pick`)

/** 租用人发起归还 → RETURNING */
export const returnOrder = (id) => request.put(`/orders/${id}/return`)

/** 出租人确认归还 → FINISHED（押金退还，租金入账） */
export const finishOrder = (id) => request.put(`/orders/${id}/finish`)
