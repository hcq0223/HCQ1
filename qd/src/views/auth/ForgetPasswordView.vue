<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { forgetPassword } from '@/api/auth'
import { validateEmail, validatePassword, getErrorMessage } from '@/utils'

const router = useRouter()

const form = reactive({ email: '', password: '' })
const errors = reactive({ email: '', password: '' })
const globalError = ref('')
const successMsg = ref('')
const loading = ref(false)

function validate() {
  errors.email = validateEmail(form.email)
  errors.password = validatePassword(form.password)
  return !errors.email && !errors.password
}

async function handleSubmit() {
  globalError.value = ''
  successMsg.value = ''
  if (!validate()) return

  loading.value = true
  try {
    const { data } = await forgetPassword(form.email, form.password)
    if (data.success) {
      successMsg.value = '密码重置成功，请前往登录'
      setTimeout(() => router.push('/login'), 2000)
    } else {
      globalError.value = data.message || '重置失败'
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
        <h1>找回密码</h1>
        <p class="tagline">通过邮箱重置您的密码</p>
      </div>

      <div v-if="globalError" class="form-alert error">{{ globalError }}</div>
      <div v-if="successMsg" class="form-alert success">{{ successMsg }}</div>

      <form @submit.prevent="handleSubmit">
        <div class="form-group">
          <label class="form-label required">邮箱</label>
          <input v-model="form.email" type="email" class="form-input" :class="{ error: errors.email }" placeholder="注册时使用的邮箱" />
          <div v-if="errors.email" class="form-error">{{ errors.email }}</div>
        </div>

        <div class="form-group">
          <label class="form-label required">新密码</label>
          <input v-model="form.password" type="password" class="form-input" :class="{ error: errors.password }" placeholder="6-255 个字符" />
          <div v-if="errors.password" class="form-error">{{ errors.password }}</div>
        </div>

        <button type="submit" class="btn btn-primary auth-btn" :disabled="loading">
          {{ loading ? '提交中...' : '重置密码' }}
        </button>
      </form>

      <div class="auth-links">
        <router-link to="/login">返回登录</router-link>
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
