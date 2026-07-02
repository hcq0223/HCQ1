<script setup>
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'

const router = useRouter()
const userStore = useUserStore()
const showDropdown = ref(false)
const isAdmin = computed(() => userStore.isAdmin)

function toggleDropdown() { showDropdown.value = !showDropdown.value }

async function handleLogout() {
  showDropdown.value = false
  const wasAdmin = isAdmin.value
  await userStore.logout()
  router.push(wasAdmin ? '/admin/login' : '/login')
}

function goAccount() { showDropdown.value = false; router.push('/account') }
function goAdminDashboard() { showDropdown.value = false; router.push('/admin/dashboard') }
function goAdminTemplates() { showDropdown.value = false; router.push('/admin/templates') }

function closeDropdown(e) {
  if (!e.target.closest('.user-menu')) showDropdown.value = false
}

onMounted(() => document.addEventListener('click', closeDropdown))
onUnmounted(() => document.removeEventListener('click', closeDropdown))
</script>

<template>
  <header class="navbar">
    <div class="navbar-inner">
      <router-link to="/dashboard" class="logo">
        <svg width="28" height="28" viewBox="0 0 32 32" fill="none">
          <rect width="32" height="32" rx="6" fill="#2563eb"/>
          <path d="M8 10h16v2H8V10zm0 5h12v2H8v-2zm0 5h14v2H8v-2z" fill="white"/>
        </svg>
        <span>Resume Builder</span>
      </router-link>

      <nav class="nav-links">
        <router-link to="/dashboard" class="nav-link">我的简历</router-link>
        <router-link to="/trash" class="nav-link">回收站</router-link>
        <router-link v-if="isAdmin" to="/admin/dashboard" class="nav-link admin-link">管理后台</router-link>
        <router-link v-if="isAdmin" to="/admin/templates" class="nav-link admin-link">模板管理</router-link>
      </nav>

      <div class="user-menu">
        <button class="user-btn" @click="toggleDropdown">
          <div class="avatar">{{ (userStore.user?.username || 'U')[0].toUpperCase() }}</div>
          <span>{{ userStore.user?.username || '用户' }}</span>
          <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <path d="M6 9l6 6 6-6"/>
          </svg>
        </button>
        <div v-if="showDropdown" class="dropdown-menu">
          <button v-if="isAdmin" class="dropdown-item" @click="goAdminDashboard">
            <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <rect x="3" y="3" width="7" height="7"/><rect x="14" y="3" width="7" height="7"/><rect x="14" y="14" width="7" height="7"/><rect x="3" y="14" width="7" height="7"/>
            </svg>
            管理后台
          </button>
          <button v-if="isAdmin" class="dropdown-item" @click="goAdminTemplates">
            <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <path d="M14 2H6a2 2 0 00-2 2v16a2 2 0 002 2h12a2 2 0 002-2V8z"/><polyline points="14 2 14 8 20 8"/><line x1="16" y1="13" x2="8" y2="13"/><line x1="16" y1="17" x2="8" y2="17"/>
            </svg>
            模板管理
          </button>
          <button class="dropdown-item" @click="goAccount">
            <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <path d="M12 15a3 3 0 100-6 3 3 0 000 6z"/>
              <path d="M19.4 15a1.65 1.65 0 00.33 1.82l.06.06a2 2 0 01-2.83 2.83l-.06-.06a1.65 1.65 0 00-1.82-.33 1.65 1.65 0 00-1 1.51V21a2 2 0 01-4 0v-.09A1.65 1.65 0 009 19.4a1.65 1.65 0 00-1.82.33l-.06.06a2 2 0 01-2.83-2.83l.06-.06A1.65 1.65 0 004.68 15a1.65 1.65 0 00-1.51-1H3a2 2 0 010-4h.09A1.65 1.65 0 004.6 9a1.65 1.65 0 00-.33-1.82l-.06-.06a2 2 0 012.83-2.83l.06.06A1.65 1.65 0 009 4.68a1.65 1.65 0 001-1.51V3a2 2 0 014 0v.09a1.65 1.65 0 001 1.51 1.65 1.65 0 001.82-.33l.06-.06a2 2 0 012.83 2.83l-.06.06A1.65 1.65 0 0019.4 9a1.65 1.65 0 001.51 1H21a2 2 0 010 4h-.09a1.65 1.65 0 00-1.51 1z"/>
            </svg>
            修改账号
          </button>
          <button class="dropdown-item danger" @click="handleLogout">
            <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <path d="M9 21H5a2 2 0 01-2-2V5a2 2 0 012-2h4M16 17l5-5-5-5M21 12H9"/>
            </svg>
            退出登录
          </button>
        </div>
      </div>
    </div>
  </header>
</template>

<style scoped>
.navbar {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  height: var(--navbar-height);
  background: #fff;
  border-bottom: 1px solid var(--border);
  z-index: 100;
}
.navbar-inner {
  max-width: 1400px;
  margin: 0 auto;
  height: 100%;
  padding: 0 24px;
  display: flex;
  align-items: center;
}
.logo { display: flex; align-items: center; gap: 10px; font-weight: 700; font-size: 16px; color: var(--text); }
.nav-links { display: flex; gap: 24px; margin-left: 48px; }
.nav-link { color: var(--text-secondary); font-weight: 500; padding: 6px 0; border-bottom: 2px solid transparent; }
.nav-link.router-link-active { color: var(--primary); border-bottom-color: var(--primary); }
.nav-link.admin-link { color: #ca8a04; }
.nav-link.admin-link.router-link-active { border-bottom-color: #ca8a04; }
.user-menu { margin-left: auto; position: relative; }
.user-btn { display: flex; align-items: center; gap: 8px; background: none; border: none; cursor: pointer; padding: 4px 8px; border-radius: 6px; color: var(--text); }
.user-btn:hover { background: var(--bg); }
.avatar { width: 32px; height: 32px; border-radius: 50%; background: var(--primary); color: #fff; display: flex; align-items: center; justify-content: center; font-weight: 600; font-size: 14px; }
</style>