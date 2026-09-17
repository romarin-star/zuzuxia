<script setup>
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useUserStore } from '@/stores/user'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const menus = [
  { path: '/admin', label: '概览', icon: 'DataLine' },
  { path: '/admin/users', label: '用户管理', icon: 'User' },
  { path: '/admin/items', label: '物品管理', icon: 'Goods' },
  { path: '/admin/orders', label: '订单管理', icon: 'Tickets' },
  { path: '/admin/categories', label: '分类管理', icon: 'Menu' }
]

const activeMenu = computed(() => route.path)

async function handleLogout() {
  try {
    await ElMessageBox.confirm('确定要退出登录吗？', '提示', { type: 'warning' })
  } catch {
    return
  }
  userStore.logout()
  ElMessage.success('已退出登录')
  router.push('/login')
}
</script>

<template>
  <el-container class="admin-layout">
    <el-aside width="200px" class="aside">
      <div class="brand">
        <span class="brand-mark">租</span>
        <span class="brand-text">租租侠 · 管理后台</span>
      </div>

      <el-menu :default-active="activeMenu" router class="aside-menu">
        <el-menu-item v-for="m in menus" :key="m.path" :index="m.path">
          <el-icon><component :is="m.icon" /></el-icon>
          <span>{{ m.label }}</span>
        </el-menu-item>
      </el-menu>
    </el-aside>

    <el-container>
      <el-header class="header">
        <div class="header-left">
          <el-breadcrumb separator="/">
            <el-breadcrumb-item>管理后台</el-breadcrumb-item>
            <el-breadcrumb-item>{{ route.meta.title || '概览' }}</el-breadcrumb-item>
          </el-breadcrumb>
        </div>
        <div class="header-right">
          <el-button link @click="router.push('/')">
            <el-icon><Back /></el-icon>
            返回前台
          </el-button>
          <el-divider direction="vertical" />
          <span class="admin-name">{{ userStore.nickname }}</span>
          <el-button link type="danger" @click="handleLogout">退出</el-button>
        </div>
      </el-header>

      <el-main class="main">
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<style scoped>
.admin-layout {
  min-height: 100vh;
}

.aside {
  background: #1f2d3d;
  display: flex;
  flex-direction: column;
}

.brand {
  height: 60px;
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 0 16px;
  color: #fff;
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
}

.brand-mark {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 26px;
  height: 26px;
  border-radius: 6px;
  background: var(--el-color-primary);
  font-weight: 700;
  flex-shrink: 0;
}

.brand-text {
  font-size: 14px;
  font-weight: 600;
  white-space: nowrap;
}

.aside-menu {
  border-right: none;
  background: transparent;
  flex: 1;
}

.aside-menu :deep(.el-menu-item) {
  color: rgba(255, 255, 255, 0.75);
}

.aside-menu :deep(.el-menu-item:hover) {
  background: rgba(255, 255, 255, 0.08);
  color: #fff;
}

.aside-menu :deep(.el-menu-item.is-active) {
  background: var(--el-color-primary);
  color: #fff;
}

.header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  background: #fff;
  border-bottom: 1px solid var(--el-border-color-light);
}

.header-right {
  display: flex;
  align-items: center;
  gap: 8px;
}

.admin-name {
  font-size: 14px;
  color: var(--el-text-color-regular);
}

.main {
  background: #f5f7fa;
  padding: 20px;
}
</style>
