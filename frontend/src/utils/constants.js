/**
 * 全局常量与枚举。
 *
 * 与后端 ResultCode.java、数据库中的状态字符串保持一一对应。
 * 前端不要在各页面里手写 'PENDING' 这类字面量，一律引用这里。
 */

/** 订单状态 */
export const ORDER_STATUS = {
  PENDING: 'PENDING',
  REJECTED: 'REJECTED',
  CANCELLED: 'CANCELLED',
  RESERVED: 'RESERVED',
  RENTING: 'RENTING',
  RETURNING: 'RETURNING',
  FINISHED: 'FINISHED'
}

/** 状态中文名 + Element Plus 标签配色 */
export const ORDER_STATUS_META = {
  PENDING: { label: '待确认', type: 'warning' },
  REJECTED: { label: '已拒绝', type: 'info' },
  CANCELLED: { label: '已取消', type: 'info' },
  RESERVED: { label: '待取件', type: 'primary' },
  RENTING: { label: '租用中', type: 'success' },
  RETURNING: { label: '待归还确认', type: 'warning' },
  FINISHED: { label: '已完成', type: 'success' }
}

/** 物品状态 */
export const ITEM_STATUS_META = {
  ON_SHELF: { label: '已上架', type: 'success' },
  OFF_SHELF: { label: '已下架', type: 'info' },
  DELISTED: { label: '违规下架', type: 'danger' }
}

/** 用户状态 */
export const USER_STATUS_META = {
  NORMAL: { label: '正常', type: 'success' },
  BANNED: { label: '已封禁', type: 'danger' }
}

/** 钱包流水类型 */
export const WALLET_TYPE_META = {
  RECHARGE: '充值',
  RENT_PAY: '支付租金',
  RENT_REFUND: '退还租金',
  RENT_INCOME: '租金收入',
  DEPOSIT_FREEZE: '冻结押金',
  DEPOSIT_REFUND: '退还押金',
  DEPOSIT_DEDUCT: '扣除押金'
}

/** 订单操作类型 → 中文，用于按钮文案 */
export const ORDER_ACTION_LABEL = {
  confirm: '同意出租',
  reject: '拒绝',
  cancel: '取消申请',
  pick: '确认取件',
  return: '发起归还',
  finish: '确认归还'
}

/** 信用分区间 → 展示文案 */
export function creditLevel(score) {
  if (score >= 120) return { label: '信用极好', type: 'success' }
  if (score >= 100) return { label: '信用良好', type: 'primary' }
  if (score >= 80) return { label: '信用一般', type: 'warning' }
  return { label: '信用较差', type: 'danger' }
}
