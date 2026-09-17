import { createRouter, createWebHistory } from 'vue-router'

/**
 * 路由表。
 *
 * 约定：
 *   - meta.requiresAuth : 需要登录（默认 true，登录页显式设为 false）
 *   - meta.role         : 需要的角色，如 'ADMIN'
 *
 * 当前仅为底座骨架，各模块页面由对应负责人在自己的分支上按分工补充。
 */
const routes = [
  {
    path: '/login',
    name: 'login',
    component: () => import('@/views/Login.vue'),
    meta: { requiresAuth: false, title: '登录' }
  },
  {
    path: '/',
    component: () => import('@/layouts/UserLayout.vue'),
    children: [
      {
        path: '',
        name: 'home',
        component: () => import('@/views/user/Home.vue'),
        meta: { title: '首页' }
      }
      // TODO 何金泉：物品大厅 /items、物品详情 /items/:id
      //              → views/user/ItemList.vue、views/user/ItemDetail.vue
      // TODO 王浩名：发布物品 /publish、订单中心 /orders、钱包 /wallet、个人中心 /profile
      //              → views/user/Publish.vue、views/user/OrderList.vue 等
    ]
  },
  {
    path: '/admin',
    component: () => import('@/layouts/AdminLayout.vue'),
    meta: { requiresAuth: true, role: 'ADMIN' },
    children: [
      {
        path: '',
        name: 'admin-home',
        component: () => import('@/views/admin/Dashboard.vue'),
        meta: { title: '管理后台' }
      }
      // TODO 伍嘉豪：用户管理 /admin/users、物品管理 /admin/items、
      //              订单管理 /admin/orders、分类管理 /admin/categories
      //              → views/admin/UserList.vue、views/admin/ItemList.vue 等
    ]
  },
  {
    path: '/:pathMatch(.*)*',
    name: 'not-found',
    component: () => import('@/views/NotFound.vue'),
    meta: { requiresAuth: false, title: '页面不存在' }
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes,
  scrollBehavior: () => ({ top: 0 })
})

/**
 * 全局前置守卫。
 *
 * 只做"有没有 token / 角色够不够"的粗判，真正的权限校验以后端为准 ——
 * 前端守卫只是体验优化，绝不能当作安全边界。
 */
router.beforeEach((to) => {
  const token = localStorage.getItem('token')

  if (to.meta.requiresAuth !== false && !token) {
    return { path: '/login', query: { redirect: to.fullPath } }
  }

  if (to.meta.role) {
    let role = null
    try {
      role = JSON.parse(localStorage.getItem('userInfo') || '{}').role
    } catch {
      role = null
    }
    if (role !== to.meta.role) {
      return { path: '/' }
    }
  }

  return true
})

router.afterEach((to) => {
  document.title = to.meta.title ? `${to.meta.title} - 租租侠` : '租租侠租赁平台'
})

export default router
