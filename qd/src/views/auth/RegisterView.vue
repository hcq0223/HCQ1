<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { register } from '@/api/auth'
import { validateUsername, validatePassword, validateEmail, getErrorMessage } from '@/utils'

const router = useRouter()

const form = reactive({ username: '', email: '', password: '' })
const errors = reactive({ username: '', email: '', password: '' })
const globalError = ref('')
const loading = ref(false)

function validate() {
  errors.username = validateUsername(form.username)
  errors.email = validateEmail(form.email)
  errors.password = validatePassword(form.password)
  return !errors.username && !errors.email && !errors.password
}

async function handleSubmit() {
  globalError.value = ''
  if (!validate()) return

  loading.value = true
  try {
    const { data } = await register(form.username, form.password, form.email)
    if (data.success) {
      router.push('/login')
    } else {
      globalError.value = data.message || '注册失败'
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
          <rect width="32" height="32" rx="6" fill="#2563eb"/>
          <path d="M8 10h16v2H8V10zm0 5h12v2H8v-2zm0 5h14v2H8v-2z" fill="white"/>
        </svg>
        <h1>创建账号</h1>
        <p class="tagline">注册 Resume Builder 账号</p>
      </div>

      <div v-if="globalError" class="form-alert error">{{ globalError }}</div>

      <form @submit.prevent="handleSubmit">
        <div class="form-group">
          <label class="form-label required">用户名</label>
          <input v-model="form.username" class="form-input" :class="{ error: errors.username }" placeholder="3-50 个字符" />
          <div v-if="errors.username" class="form-error">{{ errors.username }}</div>
        </div>

        <div class="form-group">
          <label class="form-label required">邮箱</label>
          <input v-model="form.email" type="email" class="form-input" :class="{ error: errors.email }" placeholder="your@email.com" />
          <div v-if="errors.email" class="form-error">{{ errors.email }}</div>
        </div>

        <div class="form-group">
          <label class="form-label required">密码</label>
          <input v-model="form.password" type="password" class="form-input" :class="{ error: errors.password }" placeholder="6-255 个字符" />
          <div v-if="errors.password" class="form-error">{{ errors.password }}</div>
        </div>

        <button type="submit" class="btn btn-primary auth-btn" :disabled="loading">
          {{ loading ? '注册中...' : '注册' }}
        </button>
      </form>

      <div class="auth-links">
        <router-link to="/login">已有账号？返回登录</router-link>
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
  background: linear-gradient(135deg, #f5f6f8 0%, #e8ecf1 100%);
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
  text-align: center;
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
