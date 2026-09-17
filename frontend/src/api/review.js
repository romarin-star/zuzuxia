import request from './request'

/** 双向评价接口 */

/** 提交评价。订单完成后，出租人与租用人各可评一次 */
export const createReview = (data) => request.post('/reviews', data)

/** 某物品收到的评价 */
export const listItemReviews = (itemId, params) =>
  request.get(`/reviews/item/${itemId}`, { params })

/** 某用户收到的评价（信用档案页用） */
export const listUserReviews = (userId, params) =>
  request.get(`/reviews/user/${userId}`, { params })
