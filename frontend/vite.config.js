import { fileURLToPath, URL } from 'node:url'
import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'

// https://vite.dev/config/
export default defineConfig({
  plugins: [vue()],

  resolve: {
    alias: {
      '@': fileURLToPath(new URL('./src', import.meta.url))
    }
  },

  server: {
    port: 5173,
    open: false,
    proxy: {
      // 前端代码里统一写 /api/xxx，由这里转发到后端，避免跨域与写死后端地址
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true
      },
      // 上传的图片同样走后端
      '/uploads': {
        target: 'http://localhost:8080',
        changeOrigin: true
      }
    }
  }
})
