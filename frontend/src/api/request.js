import axios from 'axios'
import { ElMessage } from 'element-plus'
import router from '@/router'

/**
 * 全局唯一的 axios 实例。
 *
 * 约定（详见设计文档 8.1 / 9.2）：
 *   - 所有请求走 /api 前缀，由 Vite proxy 转发到后端 :8080
 *   - 后端统一返回 { code, message, data }
 *   - 业务成功时，拦截器直接返回 data，业务代码拿到的就是数据本体
 *   - 业务失败时，拦截器统一弹错误提示并 reject，业务代码只需 try/catch
 */
const request = axios.create({
  baseURL: '/api',
  timeout: 15000
})

/* ---------------- 请求拦截器：统一带 token ---------------- */
request.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('token')
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  (error) => Promise.reject(error)
)

/* ---------------- 响应拦截器：统一判 code ---------------- */
request.interceptors.response.use(
  (response) => {
    const res = response.data

    // 后端未按约定返回（例如直接返回了文件流），原样透传
    if (res == null || typeof res.code === 'undefined') {
      return response.data
    }

    if (res.code === 200) {
      return res.data
    }

    if (res.code === 401) {
      handleUnauthorized()
      return Promise.reject(new Error(res.message || '登录已过期'))
    }

    // 其他业务错误：后端返回的 message 就是给用户看的中文提示
    ElMessage.error(res.message || '操作失败')
    return Promise.reject(new Error(res.message || '操作失败'))
  },
  (error) => {
    if (error.response?.status === 401) {
      handleUnauthorized()
      return Promise.reject(error)
    }

    const msg = error.code === 'ECONNABORTED'
      ? '请求超时，请检查后端服务是否已启动'
      : error.message || '网络异常'
    ElMessage.error(msg)
    return Promise.reject(error)
  }
)

let redirecting = false

/** 401 统一处理：清登录态并跳登录页，同时防止多个并发请求弹出多次提示 */
function handleUnauthorized() {
  if (redirecting) return
  redirecting = true

  localStorage.removeItem('token')
  localStorage.removeItem('userInfo')
  ElMessage.error('登录已过期，请重新登录')

  const current = router.currentRoute.value
  router
    .push({ path: '/login', query: { redirect: current.fullPath } })
    .finally(() => {
      redirecting = false
    })
}

export default request
