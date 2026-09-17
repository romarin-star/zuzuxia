import request from './request'

/** 认证相关接口，对应 openapi.yaml 的 /auth 分组 */

export const login = (data) => request.post('/auth/login', data)

export const register = (data) => request.post('/auth/register', data)

export const getMe = () => request.get('/auth/me')
