import axios from 'axios'
import router from '@/router'

const AUTH_ERROR_PATTERN = /未登录|请先登录|会话已过期|会话失效/

const request = axios.create({
  baseURL: '',
  timeout: 30000,
  withCredentials: true,
  headers: {
    'Content-Type': 'application/json'
  }
})

function redirectToLogin() {
  localStorage.removeItem('isLoggedIn')
  localStorage.removeItem('username')
  const path = router.currentRoute.value.path
  const publicPaths = ['/login', '/register', '/forget-password', '/public']
  if (!publicPaths.some((p) => path.startsWith(p))) {
    router.push('/login')
  }
}

function isAuthError(message) {
  return typeof message === 'string' && AUTH_ERROR_PATTERN.test(message)
}

request.interceptors.request.use((config) => {
  if (config.data instanceof FormData) {
    if (config.headers?.delete) {
      config.headers.delete('Content-Type')
    } else if (config.headers) {
      delete config.headers['Content-Type']
    }
  }
  return config
})

request.interceptors.response.use(
  (response) => {
    const message = response.data?.message
    if (response.data?.success === false && isAuthError(message)) {
      redirectToLogin()
      return Promise.reject(new Error(message))
    }
    return response
  },
  (error) => {
    const message = error.response?.data?.message
    if (error.response?.status === 401 && isAuthError(message)) {
      redirectToLogin()
    }
    return Promise.reject(error)
  }
)

export function toFormData(params) {
  const form = new URLSearchParams()
  Object.entries(params).forEach(([key, value]) => {
    if (value !== undefined && value !== null) {
      form.append(key, value)
    }
  })
  return form
}

export default request
