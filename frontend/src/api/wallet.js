import request from './request'

/** 钱包接口 */

/** 我的余额（含冻结押金） */
export const getWallet = () => request.get('/wallet')

/** 充值（演示用，直接加钱） */
export const recharge = (amount) => request.post('/wallet/recharge', null, { params: { amount } })

/** 交易流水（分页） */
export const listTransactions = (params) => request.get('/wallet/transactions', { params })
