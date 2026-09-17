import { defineStore } from 'pinia'
import { login as loginApi, getMe } from '@/api/auth'

const TOKEN_KEY = 'token'
const USER_KEY = 'userInfo'

export const useUserStore = defineStore('user', {
  state: () => ({
    token: localStorage.getItem(TOKEN_KEY) || '',
    userInfo: safeParse(localStorage.getItem(USER_KEY))
  }),

  getters: {
    isLogin: (state) => !!state.token,
    isAdmin: (state) => state.userInfo?.role === 'ADMIN',
    nickname: (state) => state.userInfo?.nickname || state.userInfo?.username || '未登录',
    balance: (state) => Number(state.userInfo?.balance ?? 0),
    frozenBalance: (state) => Number(state.userInfo?.frozenBalance ?? 0)
  },

  actions: {
    /** 登录：成功后持久化 token 与用户信息 */
    async login(form) {
      const data = await loginApi(form)
      this.token = data.token
      this.userInfo = data.userInfo
      localStorage.setItem(TOKEN_KEY, data.token)
      localStorage.setItem(USER_KEY, JSON.stringify(data.userInfo))
      return data
    },

    /** 拉取最新用户信息（余额、信用分等会随业务变化，进入个人中心时应刷新） */
    async fetchMe() {
      const data = await getMe()
      this.userInfo = data
      localStorage.setItem(USER_KEY, JSON.stringify(data))
      return data
    },

    /** 仅更新本地缓存的用户信息，供业务成功后局部刷新使用 */
    patchUserInfo(patch) {
      this.userInfo = { ...(this.userInfo || {}), ...patch }
      localStorage.setItem(USER_KEY, JSON.stringify(this.userInfo))
    },

    logout() {
      this.token = ''
      this.userInfo = null
      localStorage.removeItem(TOKEN_KEY)
      localStorage.removeItem(USER_KEY)
    }
  }
})

function safeParse(raw) {
  if (!raw) return null
  try {
    return JSON.parse(raw)
  } catch {
    return null
  }
}
