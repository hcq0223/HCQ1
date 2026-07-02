const EMAIL_REGEX = /^[^\s@]+@[^\s@]+\.[^\s@]+$/

export function validateUsername(username) {
  if (!username || !username.trim()) return '用户名不能为空'
  if (username.length < 3 || username.length > 50) return '用户名长度应为3-50个字符'
  return ''
}

export function validatePassword(password) {
  if (!password) return '密码不能为空'
  if (password.length < 6 || password.length > 255) return '密码长度应为6-255个字符'
  return ''
}

export function validateEmail(email) {
  if (!email || !email.trim()) return '邮箱不能为空'
  if (!EMAIL_REGEX.test(email)) return '邮箱格式不正确'
  return ''
}

export function formatDate(dateStr) {
  if (!dateStr) return '至今'
  return dateStr.substring(0, 10)
}

export function formatDateTime(dateStr) {
  if (!dateStr) return '-'
  return dateStr.replace('T', ' ').substring(0, 19)
}

export function getAvatarUrl(url) {
  if (!url) return ''
  if (url.startsWith('http')) return url
  return `http://localhost:8080${url.startsWith('/') ? '' : '/'}${url}`
}

export function generateConversationId() {
  return `chat_${Date.now()}_${Math.random().toString(36).slice(2, 9)}`
}

export const SKILL_LEVELS = {
  beginner: '入门',
  intermediate: '中级',
  advanced: '高级',
  expert: '专家'
}

export const LANGUAGE_LEVELS = {
  basic: '基础',
  conversational: '会话',
  professional: '专业',
  native: '母语'
}

export const TEMPLATE_CATEGORIES = [
  { value: '', label: '全部' },
  { value: 'professional', label: '专业' },
  { value: 'creative', label: '创意' },
  { value: 'modern', label: '现代' },
  { value: 'minimalist', label: '极简' },
  { value: 'academic', label: '学术' }
]

export const CATEGORY_LABELS = {
  professional: '专业',
  creative: '创意',
  modern: '现代',
  minimalist: '极简',
  academic: '学术'
}

export const EDITOR_MODULES = [
  { key: 'basic', label: '基本信息', icon: 'user' },
  { key: 'work', label: '工作经历', icon: 'briefcase' },
  { key: 'education', label: '教育经历', icon: 'academic' },
  { key: 'skill', label: '技能', icon: 'code' },
  { key: 'project', label: '项目经历', icon: 'folder' },
  { key: 'certification', label: '证书与奖项', icon: 'badge' },
  { key: 'language', label: '语言能力', icon: 'globe' },
  { key: 'template', label: '模板选择', icon: 'layout' },
  { key: 'custom', label: '自定义模块', icon: 'edit' }
]

export function getShareLink(token) {
  return `${window.location.origin}/public/${token}`
}

export function normalizeResumes(data) {
  const raw = data?.resumes ?? data
  if (!raw) return []
  if (Array.isArray(raw)) return raw.filter((r) => r && r.id != null)
  if (typeof raw === 'object' && raw.id != null) return [raw]
  return []
}

export function getErrorMessage(error) {
  return error.response?.data?.message || error.message || '操作失败'
}