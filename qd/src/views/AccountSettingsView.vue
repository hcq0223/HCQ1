<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { updateUser } from '@/api/auth'
import { useUserStore } from '@/stores/user'
import { validateUsername, validateEmail, validatePassword, getErrorMessage } from '@/utils'

const router = useRouter()
const userStore = useUserStore()

const form = reactive({ username: '', email: '', password: '', confirmPassword: '' })
const errors = reactive({ username: '', email: '', password: '', confirmPassword: '' })
const globalError = ref('')
const successMsg = ref('')
const loading = ref(false)

onMounted(() => {
  // 初始框内为空
  form.username = ''
  form.email = ''
})

function validate() {
  errors.username = form.username.trim() ? validateUsername(form.username) : ''
  errors.email = form.email.trim() ? validateEmail(form.email) : ''
  if (form.password || form.confirmPassword) {
    errors.password = validatePassword(form.password)
    errors.confirmPassword = (!errors.password && form.password !== form.confirmPassword) ? '两次输入的密码不一致' : ''
  } else {
    errors.password = ''; errors.confirmPassword = ''
  }
  return !errors.username && !errors.email && !errors.password && !errors.confirmPassword
}

async function handleSubmit() {
  globalError.value = ''; successMsg.value = ''
  if (!validate()) return

  // 如果修改密码，验证输入的账号和邮箱是否正确
  if (form.password) {
    const currentUsername = userStore.user?.username || localStorage.getItem('username')
    const currentEmail = userStore.user?.email || ''
    if (form.username.trim() !== currentUsername) {
      globalError.value = '用户名与当前账号不匹配'
      return
    }
    if (form.email.trim() && form.email.trim() !== currentEmail) {
      globalError.value = '邮箱与当前账号不匹配'
      return
    }
  }

  loading.value = true
  try {
    const payload = {}
    if (form.username.trim()) payload.username = form.username.trim()
    if (form.email.trim()) payload.email = form.email.trim()
    if (form.password) payload.passwordHash = form.password
    if (Object.keys(payload).length === 0) {
      globalError.value = '没有需要更新的内容'; loading.value = false; return
    }
    const { data } = await updateUser(payload)
    if (data.success) {
      if (payload.username) {
        userStore.setUser({ ...userStore.user, username: payload.username })
        localStorage.setItem('username', payload.username)
      }
      if (payload.email) {
        userStore.setUser({ ...userStore.user, email: payload.email })
      }
      successMsg.value = '账号信息已更新'
      form.password = ''; form.confirmPassword = ''
      // 更新后清空验证字段
      form.username = ''; form.email = ''
    } else {
      globalError.value = data.message || '更新失败'
    }
  } catch (e) {
    globalError.value = getErrorMessage(e)
  } finally { loading.value = false }
}
</script>

<template>
  <div class="page-with-navbar">
    <div class="page-container account-page">
      <div class="page-header">
        <h1>修改账号</h1>
        <p class="page-desc">修改密码需先输入当前账号和邮箱验证身份，留空则不修改</p>
      </div>
      <div v-if="globalError" class="form-alert error">{{ globalError }}</div>
      <div v-if="successMsg" class="form-alert success">{{ successMsg }}</div>
      <div class="account-card card">
        <form @submit.prevent="handleSubmit">
          <div class="form-group">
            <label class="form-label">当前用户名</label>
            <input v-model="form.username" class="form-input" :class="{ error: errors.username }" @blur="errors.username = form.username.trim() ? validateUsername(form.username) : ''" />
            <div v-if="errors.username" class="form-error">{{ errors.username }}</div>
          </div>
          <div class="form-group">
            <label class="form-label">当前邮箱</label>
            <input v-model="form.email" type="email" class="form-input" :class="{ error: errors.email }" @blur="errors.email = form.email.trim() ? validateEmail(form.email) : ''" />
            <div v-if="errors.email" class="form-error">{{ errors.email }}</div>
          </div>
          <div class="form-divider">修改密码（不修改请留空）</div>
          <div class="form-group">
            <label class="form-label">新密码</label>
            <input v-model="form.password" type="password" class="form-input" :class="{ error: errors.password }" />
            <div v-if="errors.password" class="form-error">{{ errors.password }}</div>
          </div>
          <div class="form-group">
            <label class="form-label">确认新密码</label>
            <input v-model="form.confirmPassword" type="password" class="form-input" :class="{ error: errors.confirmPassword }" />
            <div v-if="errors.confirmPassword" class="form-error">{{ errors.confirmPassword }}</div>
          </div>
          <div class="form-actions">
            <button type="button" class="btn btn-secondary" @click="router.push('/dashboard')">返回</button>
            <button type="submit" class="btn btn-primary" :disabled="loading">{{ loading ? '保存中...' : '保存修改' }}</button>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>

<style scoped>
.account-page { max-width: 560px; }
.page-header { margin-bottom: 24px; }
.page-header h1 { font-size: 28px; font-weight: 700; }
.page-desc { color: var(--text-secondary); font-size: 14px; margin-top: 4px; }
.account-card { padding: 32px; }
.form-divider { margin: 24px 0 16px; padding-bottom: 8px; border-bottom: 1px solid var(--border); font-size: 13px; color: var(--text-secondary); }
.form-actions { display: flex; justify-content: flex-end; gap: 8px; margin-top: 24px; padding-top: 16px; border-top: 1px solid var(--border); }
</style>