import request from './request'

/** 物品接口，对应 openapi.yaml 的 /items 分组 */

/** 物品大厅：分页 + 分类 + 关键词 + 排序 */
export const listItems = (params) => request.get('/items', { params })

/** 物品详情 */
export const getItem = (id) => request.get(`/items/${id}`)

/** 热度榜 */
export const listHotItems = (params) => request.get('/items/hot', { params })

/** 推荐榜 */
export const listRecommendItems = (params) => request.get('/items/recommend', { params })

/** 我发布的物品 */
export const listMyItems = (params) => request.get('/items/mine', { params })

export const createItem = (data) => request.post('/items', data)

export const updateItem = (id, data) => request.put(`/items/${id}`, data)

export const deleteItem = (id) => request.delete(`/items/${id}`)

/** 上架 / 下架 */
export const toggleShelf = (id, onShelf) =>
  request.put(`/items/${id}/shelf`, null, { params: { onShelf } })

/* ---------- 可租时间段 ---------- */

export const listAvailablePeriods = (itemId) => request.get(`/items/${itemId}/available-periods`)

export const addAvailablePeriod = (itemId, data) =>
  request.post(`/items/${itemId}/available-periods`, data)

export const removeAvailablePeriod = (itemId, periodId) =>
  request.delete(`/items/${itemId}/available-periods/${periodId}`)

/* ---------- 图片 ---------- */

/** 上传图片，file 为 File 对象；后端返回图片 URL */
export const uploadItemImage = (itemId, file) => {
  const form = new FormData()
  form.append('file', file)
  return request.post(`/items/${itemId}/images`, form, {
    headers: { 'Content-Type': 'multipart/form-data' }
  })
}
