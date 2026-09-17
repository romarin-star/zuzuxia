import request from './request'

/** 分类接口（公开） */

export const listCategories = () => request.get('/categories')
