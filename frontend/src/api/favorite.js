import request from './request'

/** 收藏接口 */

export const listFavorites = (params) => request.get('/favorites', { params })

export const addFavorite = (itemId) => request.post(`/favorites/${itemId}`)

export const removeFavorite = (itemId) => request.delete(`/favorites/${itemId}`)
