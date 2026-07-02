import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import { fileURLToPath, URL } from 'node:url'

export default defineConfig({
  plugins: [vue()],
  resolve: {
    alias: {
      '@': fileURLToPath(new URL('./src', import.meta.url))
    }
  },
  server: {
    port: 5173,
    proxy: {
      '/hcq': {
        target: 'http://localhost:8080',
        changeOrigin: true,
        timeout: 60000
      },
      '/upload': {
        target: 'http://localhost:8080',
        changeOrigin: true,
        timeout: 60000
      },
      '/avatars': {
        target: 'http://localhost:8080',
        changeOrigin: true,
        timeout: 60000
      },
      '/template-images': {
        target: 'http://localhost:8080',
        changeOrigin: true,
        timeout: 60000
      }
    }
  }
})