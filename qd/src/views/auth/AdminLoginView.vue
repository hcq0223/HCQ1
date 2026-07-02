<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { adminLogin } from '@/api/auth'
import { useUserStore } from '@/stores/user'
import { validateUsername, validatePassword, getErrorMessage } from '@/utils'

const router = useRouter()
const userStore = useUserStore()

const form = reactive({ username: '', password: '' })
const errors = reactive({ username: '', password: '' })
const globalError = ref('')
const loading = ref(false)

function validate() {
  errors.username = validateUsername(form.username)
  errors.password = validatePassword(form.password)
  return !errors.username && !errors.password
}

async function handleSubmit() {
  globalError.value = ''
  if (!validate()) return

  loading.value = true
  try {
    const { data } = await adminLogin(form.username, form.password)
    if (data.success) {
      userStore.setUser({ username: form.username, role: 'admin' })
      router.push('/admin/dashboard')
    } else {
      globalError.value = data.message || '管理员登录失败'
    }
  } catch (e) {
    globalError.value = getErrorMessage(e)
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <div class="auth-page">
    <div class="auth-card">
      <div class="auth-header">
        <svg width="40" height="40" viewBox="0 0 32 32" fill="none">
          <rect width="32" height="32" rx="6" fill="#1e40af"/>
          <path d="M16 6l-8 4v4c0 5.05 3.41 9.78 8 11 4.59-1.22 8-5.95 8-11v-4l-8-4zm0 2.18L21.5 11v3c0 3.77-2.54 7.3-5.5 8.54-2.96-1.24-5.5-4.77-5.5-8.54v-3L16 8.18zM15 14h-2l3-4 3 4h-2v4h-2v-4z" fill="white"/>
        </svg>
        <h1>管理员登录</h1>
        <p class="tagline">Resume Builder 管理后台</p>
      </div>

      <div v-if="globalError" class="form-alert error">{{ globalError }}</div>

      <form @submit.prevent="handleSubmit">
        <div class="form-group">
          <label class="form-label required">管理员账号</label>
          <input
            v-model="form.username"
            class="form-input"
            :class="{ error: errors.username }"
            placeholder="请输入管理员用户名"
            @blur="errors.username = validateUsername(form.username)"
          />
          <div v-if="errors.username" class="form-error">{{ errors.username }}</div>
        </div>

        <div class="form-group">
          <label class="form-label required">密码</label>
          <input
            v-model="form.password"
            type="password"
            class="form-input"
            :class="{ error: errors.password }"
            placeholder="请输入管理员密码"
            @blur="errors.password = validatePassword(form.password)"
          />
          <div v-if="errors.password" class="form-error">{{ errors.password }}</div>
        </div>

        <button type="submit" class="btn btn-primary auth-btn" :disabled="loading">
          {{ loading ? '登录中...' : '管理员登录' }}
        </button>
      </form>

      <div class="auth-links">
        <router-link to="/login">返回用户登录</router-link>
      </div>
    </div>
  </div>
</template>

<style scoped>
.auth-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #eef2ff 0%, #dbeafe 100%);
}

.auth-card {
  width: 420px;
  background: #fff;
  border-radius: 12px;
  padding: 40px;
  box-shadow: 0 4px 24px rgba(0, 0, 0, 0.08);
}

.auth-header {
  text-align: center;
  margin-bottom: 32px;
}

.auth-header h1 {
  font-size: 22px;
  margin-top: 12px;
}

.tagline {
  color: var(--text-secondary);
  font-size: 13px;
  margin-top: 4px;
}

.auth-btn {
  width: 100%;
  height: 44px;
  margin-top: 8px;
}

.auth-links {
  display: flex;
  justify-content: center;
  margin-top: 20px;
  font-size: 13px;
}

.auth-links a {
  color: var(--text-secondary);
}

.auth-links a:hover {
  color: var(--primary);
}
</style>