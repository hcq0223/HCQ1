import { defineStore } from 'pinia'
import { ref } from 'vue'
import { logout as logoutApi } from '@/api/auth'

export const useUserStore = defineStore('user', () => {
  const user = ref(null)
  const isLoggedIn = ref(false)
  const isAdmin = ref(false)

  function setUser(userData) {
    user.value = { ...user.value, ...userData }
    isLoggedIn.value = true
    localStorage.setItem('isLoggedIn', 'true')
    if (userData?.username) localStorage.setItem('username', userData.username)
    if (userData?.email) localStorage.setItem('email', userData.email)
    if (userData?.role != null) {
      if (userData.role === 'admin') {
        isAdmin.value = true; localStorage.setItem('isAdmin', 'true')
      } else {
        isAdmin.value = false; localStorage.removeItem('isAdmin')
      }
    }
  }

  function clearUser() {
    user.value = null
    isLoggedIn.value = false
    isAdmin.value = false
    localStorage.removeItem('isLoggedIn')
    localStorage.removeItem('username')
    localStorage.removeItem('email')
    localStorage.removeItem('isAdmin')
  }

  async function logout() {
    try { await logoutApi() } catch { /* ignore */ }
    clearUser()
  }

  function initFromStorage() {
    isLoggedIn.value = localStorage.getItem('isLoggedIn') === 'true'
    isAdmin.value = localStorage.getItem('isAdmin') === 'true'
    const username = localStorage.getItem('username')
    const email = localStorage.getItem('email')
    if (username || email) user.value = { username, email }
  }

  return { user, isLoggedIn, isAdmin, setUser, clearUser, logout, initFromStorage }
})