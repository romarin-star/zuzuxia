<script setup>
import { onMounted, ref } from 'vue'
import request from '@/api/request'
import { useUserStore } from '@/stores/user'

/**
 * 底座连通性验证页。
 *
 * 本页面的存在意义：让"Vite proxy → Spring Boot → 统一响应体 → axios 拦截器"
 * 这条链路有一个可视化的确认点。前后端刚拉起来时先看这里，
 * 能连上就说明骨架没问题，可以开始写业务了。
 */

const userStore = useUserStore()

const loading = ref(true)
const ok = ref(false)
const errorMsg = ref('')
const pingData = ref(null)

async function check() {
  loading.value = true
  ok.value = false
  errorMsg.value = ''
  try {
    pingData.value = await request.get('/ping')
    ok.value = true
  } catch (e) {
    errorMsg.value = e.message || '未知错误'
  } finally {
    loading.value = false
  }
}

onMounted(check)
</script>

<template>
  <div class="home">
    <el-card shadow="never" class="hero">
      <h1>欢迎使用租租侠</h1>
      <p class="sub">让闲置物品流动起来 —— 物品大厅、订单中心、信用档案三大模块</p>
    </el-card>

    <el-card shadow="never" class="check-card">
      <template #header>
        <div class="check-header">
          <span>环境自检</span>
          <el-button size="small" :loading="loading" @click="check">
            <el-icon><Refresh /></el-icon>
            重新检测
          </el-button>
        </div>
      </template>

      <el-skeleton v-if="loading" :rows="2" animated />

      <template v-else>
        <el-result
          v-if="ok"
          icon="success"
          title="后端连通成功 ✅"
          :sub-title="`应用：${pingData.app}　版本：${pingData.version}`"
        >
          <template #extra>
            <el-descriptions :column="1" border size="small" class="ping-detail">
              <el-descriptions-item label="接口">GET /api/ping</el-descriptions-item>
              <el-descriptions-item label="后端时间">{{ pingData.time }}</el-descriptions-item>
              <el-descriptions-item label="登录状态">
                {{ userStore.isLogin ? `已登录（${userStore.nickname}）` : '未登录' }}
              </el-descriptions-item>
            </el-descriptions>
          </template>
        </el-result>

        <el-result
          v-else
          icon="error"
          title="后端连接失败 ❌"
          :sub-title="errorMsg"
        >
          <template #extra>
            <el-alert type="info" :closable="false" class="tips">
              <p>请依次检查：</p>
              <ol>
                <li>后端是否已启动（<code>cd backend &amp;&amp; mvnw.cmd spring-boot:run</code>）</li>
                <li>是否监听在 <code>http://localhost:8080</code></li>
                <li>MySQL 服务是否已启动（后端启动依赖数据库连接）</li>
              </ol>
            </el-alert>
          </template>
        </el-result>
      </template>
    </el-card>

    <el-card shadow="never" class="next-card">
      <template #header>接下来做什么</template>
      <el-alert type="success" :closable="false" class="mb">
        骨架已跑通。接口契约见 <code>docs/api/openapi.yaml</code>，
        前后端可据此并行开发。
      </el-alert>
      <el-descriptions :column="2" border size="small">
        <el-descriptions-item label="物品大厅 / 详情">何金泉（前端A）</el-descriptions-item>
        <el-descriptions-item label="发布 / 订单 / 钱包 / 个人中心">王浩名（前端B）</el-descriptions-item>
        <el-descriptions-item label="用户 / 物品 / 分类接口">张明亮（后端A）</el-descriptions-item>
        <el-descriptions-item label="订单 / 评价 / 状态机">梁兴宇（后端B）</el-descriptions-item>
        <el-descriptions-item label="接口规范 / 底座 / 管理端">伍嘉豪（组长）</el-descriptions-item>
        <el-descriptions-item label="演示账号">admin/admin123　其他用户/123456</el-descriptions-item>
      </el-descriptions>
    </el-card>
  </div>
</template>

<style scoped>
.home {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.hero h1 {
  margin: 0 0 8px;
  font-size: 24px;
}

.hero .sub {
  margin: 0;
  color: var(--el-text-color-secondary);
}

.check-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.ping-detail {
  max-width: 460px;
  margin: 0 auto;
  text-align: left;
}

.tips {
  text-align: left;
}

.tips ol {
  margin: 8px 0 0;
  padding-left: 20px;
}

.mb {
  margin-bottom: 12px;
}

code {
  background: var(--el-fill-color-light);
  padding: 1px 5px;
  border-radius: 3px;
  font-size: 12px;
}
</style>
