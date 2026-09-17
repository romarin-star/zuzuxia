<script setup>
import { computed } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useUserStore } from '@/stores/user'

const router = useRouter()
const userStore = useUserStore()

const menus = [
  { path: '/', label: '物品大厅', icon: 'HomeFilled' },
  { path: '/items', label: '全部物品', icon: 'Grid' },
  { path: '/publish', label: '发布闲置', icon: 'Plus' },
  { path: '/orders', label: '订单中心', icon: 'List' },
  { path: '/favorites', label: '我的收藏', icon: 'Star' }
]

const activeMenu = computed(() => router.currentRoute.value.path)

async function handleCommand(command) {
  if (command === 'logout') {
    try {
      await ElMessageBox.confirm('确定要退出登录吗？', '提示', { type: 'warning' })
    } catch {
      return
    }
    userStore.logout()
    ElMessage.success('已退出登录')
    router.push('/login')
  } else if (command === 'profile') {
    router.push('/profile')
  } else if (command === 'wallet') {
    router.push('/wallet')
  } else if (command === 'admin') {
    router.push('/admin')
  }
}
</script>

<template>
  <el-container class="user-layout">
    <el-header class="header">
      <div class="header-inner">
        <div class="logo" @click="router.push('/')">
          <span class="logo-mark">租</span>
          <span class="logo-text">租租侠</span>
          <span class="logo-sub">闲置物品租赁平台</span>
        </div>

        <el-menu
          :default-active="activeMenu"
          mode="horizontal"
          :ellipsis="false"
          class="nav-menu"
          router
        >
          <el-menu-item v-for="m in menus" :key="m.path" :index="m.path">
            <el-icon><component :is="m.icon" /></el-icon>
            <span>{{ m.label }}</span>
          </el-menu-item>
        </el-menu>

        <div class="header-right">
          <template v-if="userStore.isLogin">
            <span class="balance">
              <el-icon><Wallet /></el-icon>
              余额 ¥{{ userStore.balance.toFixed(2) }}
            </span>
            <el-dropdown @command="handleCommand">
              <span class="user-trigger">
                <el-avatar :size="30" :src="userStore.userInfo?.avatar">
                  {{ userStore.nickname.charAt(0) }}
                </el-avatar>
                <span class="nickname">{{ userStore.nickname }}</span>
                <el-icon><ArrowDown /></el-icon>
              </span>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item command="profile">个人中心</el-dropdown-item>
                  <el-dropdown-item command="wallet">我的钱包</el-dropdown-item>
                  <el-dropdown-item v-if="userStore.isAdmin" command="admin" divided>
                    管理后台
                  </el-dropdown-item>
                  <el-dropdown-item command="logout" divided>退出登录</el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </template>
          <template v-else>
            <el-button type="primary" @click="router.push('/login')">登录 / 注册</el-button>
          </template>
        </div>
      </div>
    </el-header>

    <el-main class="main">
      <router-view />
    </el-main>

    <el-footer class="footer">
      租租侠租赁平台 · 软件工程课程实践项目 · 2026
    </el-footer>
  </el-container>
</template>

<style scoped>
.user-layout {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

.header {
  padding: 0;
  border-bottom: 1px solid var(--el-border-color-light);
  background: #fff;
  position: sticky;
  top: 0;
  z-index: 100;
}

.header-inner {
  max-width: 1200px;
  margin: 0 auto;
  height: 60px;
  display: flex;
  align-items: center;
  gap: 24px;
  padding: 0 16px;
}

.logo {
  display: flex;
  align-items: baseline;
  gap: 8px;
  cursor: pointer;
  flex-shrink: 0;
}

.logo-mark {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 30px;
  height: 30px;
  border-radius: 8px;
  background: var(--el-color-primary);
  color: #fff;
  font-weight: 700;
  align-self: center;
}

.logo-text {
  font-size: 19px;
  font-weight: 700;
  letter-spacing: 1px;
}

.logo-sub {
  font-size: 12px;
  color: var(--el-text-color-secondary);
}

.nav-menu {
  flex: 1;
  border-bottom: none;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 16px;
  flex-shrink: 0;
}

.balance {
  display: inline-flex;
  align-items: center;
  gap: 4px;
  font-size: 14px;
  color: var(--el-color-danger);
  font-weight: 600;
}

.user-trigger {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  outline: none;
}

.nickname {
  font-size: 14px;
}

.main {
  flex: 1;
  max-width: 1200px;
  width: 100%;
  margin: 0 auto;
  padding: 20px 16px;
}

.footer {
  text-align: center;
  font-size: 13px;
  color: var(--el-text-color-secondary);
  line-height: 60px;
  border-top: 1px solid var(--el-border-color-lighter);
}
</style>
