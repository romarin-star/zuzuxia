<script setup>
import { reactive, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/stores/user'
import { register as registerApi } from '@/api/auth'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const mode = ref('login') // login | register
const loading = ref(false)
const formRef = ref()

const form = reactive({
  username: '',
  password: '',
  nickname: '',
  phone: ''
})

const rules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 20, message: '用户名长度 3-20 位', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 32, message: '密码长度 6-32 位', trigger: 'blur' }
  ]
}

async function handleSubmit() {
  try {
    await formRef.value.validate()
  } catch {
    return
  }

  loading.value = true
  try {
    if (mode.value === 'login') {
      await userStore.login({ username: form.username, password: form.password })
      ElMessage.success('登录成功')
      router.push(route.query.redirect || '/')
    } else {
      await registerApi({
        username: form.username,
        password: form.password,
        nickname: form.nickname || form.username,
        phone: form.phone
      })
      ElMessage.success('注册成功，请登录')
      mode.value = 'login'
    }
  } catch {
    // 错误提示已由 axios 响应拦截器统一处理
  } finally {
    loading.value = false
  }
}

function switchMode() {
  mode.value = mode.value === 'login' ? 'register' : 'login'
  formRef.value?.clearValidate()
}
</script>

<template>
  <div class="login-page">
    <el-card class="login-card" shadow="always">
      <div class="brand">
        <span class="brand-mark">租</span>
        <div>
          <div class="brand-title">租租侠</div>
          <div class="brand-sub">闲置物品租赁平台</div>
        </div>
      </div>

      <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        label-position="top"
        size="large"
        @submit.prevent="handleSubmit"
      >
        <el-form-item label="用户名" prop="username">
          <el-input v-model="form.username" placeholder="请输入用户名" clearable>
            <template #prefix><el-icon><User /></el-icon></template>
          </el-input>
        </el-form-item>

        <el-form-item label="密码" prop="password">
          <el-input
            v-model="form.password"
            type="password"
            placeholder="请输入密码"
            show-password
            @keyup.enter="handleSubmit"
          >
            <template #prefix><el-icon><Lock /></el-icon></template>
          </el-input>
        </el-form-item>

        <template v-if="mode === 'register'">
          <el-form-item label="昵称">
            <el-input v-model="form.nickname" placeholder="不填则默认与用户名相同" clearable />
          </el-form-item>
          <el-form-item label="手机号">
            <el-input v-model="form.phone" placeholder="选填" clearable />
          </el-form-item>
        </template>

        <el-button
          type="primary"
          size="large"
          class="submit-btn"
          :loading="loading"
          @click="handleSubmit"
        >
          {{ mode === 'login' ? '登 录' : '注 册' }}
        </el-button>
      </el-form>

      <div class="switch">
        <el-link type="primary" :underline="false" @click="switchMode">
          {{ mode === 'login' ? '还没有账号？去注册' : '已有账号？去登录' }}
        </el-link>
      </div>

      <el-alert type="info" :closable="false" class="demo-tip">
        <div>演示账号（需先导入 docs/db/data.sql）：</div>
        <div>管理员 <code>admin / admin123</code></div>
        <div>普通用户 <code>wujiahao / 123456</code></div>
      </el-alert>
    </el-card>
  </div>
</template>

<style scoped>
.login-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #eef3ff 0%, #f7f9fc 100%);
  padding: 20px;
}

.login-card {
  width: 100%;
  max-width: 400px;
  border-radius: 12px;
}

.brand {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 24px;
}

.brand-mark {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 44px;
  height: 44px;
  border-radius: 12px;
  background: var(--el-color-primary);
  color: #fff;
  font-size: 22px;
  font-weight: 700;
}

.brand-title {
  font-size: 20px;
  font-weight: 700;
}

.brand-sub {
  font-size: 12px;
  color: var(--el-text-color-secondary);
}

.submit-btn {
  width: 100%;
  margin-top: 4px;
}

.switch {
  text-align: center;
  margin-top: 14px;
}

.demo-tip {
  margin-top: 18px;
  font-size: 12px;
}

code {
  background: rgba(0, 0, 0, 0.06);
  padding: 1px 5px;
  border-radius: 3px;
}
</style>
