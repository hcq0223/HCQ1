 <script setup>
 import { ref, reactive } from 'vue'
 import { useRouter } from 'vue-router'
 import loginBg from '@/images/登录页背景图.png'
 import { login } from '@/api/auth'
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
    const { data } = await login(form.username, form.password)
    if (data.success) {
      userStore.setUser(data.user || { username: form.username })
      router.push('/dashboard')
    } else {
      globalError.value = data.message || '登录失败'
    }
  } catch (e) {
    globalError.value = getErrorMessage(e)
  } finally {
    loading.value = false
  }
}
</script>

 <template>
 <div class="auth-page" :style="{ backgroundImage: `url(${loginBg})` }">
  <div class="auth-card">
     <div class="auth-header">
        <svg width="40" height="40" viewBox="0 0 32 32" fill="none">
          <rect width="32" height="32" rx="6" fill="#2563eb"/>
          <path d="M8 10h16v2H8V10zm0 5h12v2H8v-2zm0 5h14v2H8v-2z" fill="white"/>
        </svg>
        <h1>Resume Builder</h1>
        <p class="tagline">打造你的专业个人简历</p>
      </div>

      <div v-if="globalError" class="form-alert error">{{ globalError }}</div>

      <form @submit.prevent="handleSubmit">
        <div class="form-group">
          <label class="form-label required">用户名</label>
          <input
            v-model="form.username"
            class="form-input"
            :class="{ error: errors.username }"
            placeholder="请输入用户名"
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
            placeholder="请输入密码"
            @blur="errors.password = validatePassword(form.password)"
          />
          <div v-if="errors.password" class="form-error">{{ errors.password }}</div>
        </div>

        <button type="submit" class="btn btn-primary auth-btn" :disabled="loading">
          {{ loading ? '登录中...' : '登录' }}
        </button>
      </form>

      <div class="auth-links">
        <router-link to="/forget-password">忘记密码？</router-link>
        <router-link to="/register">还没有账号？立即注册</router-link>
      </div>

      <div class="admin-link">
        <router-link to="/admin/login">管理员登录</router-link>
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
   background-size: cover;
   background-position: center;
   background-repeat: no-repeat;
   position: relative;
 }
 .auth-page::before {
   content: '';
   position: absolute;
   inset: 0;
   background: rgba(0, 0, 0, 0.45);
   z-index: 0;
 }
.auth-card {
  width: 420px;
  background: #fff;
  border-radius: 12px;
  padding: 40px;
  box-shadow: 0 4px 24px rgba(0, 0, 0, 0.08);
  position: relative;
  z-index: 1;
  transform: translateX(450px);
}
.auth-header { text-align: center; margin-bottom: 32px; }
.auth-header h1 { font-size: 22px; margin-top: 12px; }
.tagline { color: var(--text-secondary); font-size: 13px; margin-top: 4px; }
.auth-btn { width: 100%; height: 44px; margin-top: 8px; }
.auth-links { display: flex; justify-content: space-between; margin-top: 20px; font-size: 13px; }
.auth-links a { color: var(--text-secondary); }
.auth-links a:hover { color: var(--primary); }
.admin-link { text-align: center; margin-top: 12px; padding-top: 12px; border-top: 1px solid var(--border); font-size: 12px; }
.admin-link a { color: #9ca3af; }
.admin-link a:hover { color: var(--primary); }
</style>
