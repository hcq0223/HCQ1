<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { getAdminUsers, getAdminResumes, getAdminStats } from '@/api/admin'
import { formatDateTime, getErrorMessage } from '@/utils'

const router = useRouter()
const loading = ref(true)
const stats = ref({ totalUsers: 0, totalResumes: 0, publishedResumes: 0, draftResumes: 0 })
const users = ref([])
const resumes = ref([])
const globalError = ref('')
const activeTab = ref('stats')

// modal state
const showUserModal = ref(false)
const selectedUser = ref(null)
const showUserResumes = ref([])

async function loadData() {
  loading.value = true
  globalError.value = ''
  try {
    const [statsRes, usersRes, resumesRes] = await Promise.all([
      getAdminStats(),
      getAdminUsers(),
      getAdminResumes()
    ])
    stats.value = statsRes.data.stats
    users.value = usersRes.data.users || []
    resumes.value = resumesRes.data.resumes || []
  } catch (e) {
    globalError.value = getErrorMessage(e)
  } finally {
    loading.value = false
  }
}

function viewUserResumes(user) {
  selectedUser.value = user
  showUserResumes.value = resumes.value.filter(r => r.userId === user.id)
  showUserModal.value = true
}

function getResumeCount(userId) {
  return resumes.value.filter(r => r.userId === userId).length
}

onMounted(loadData)
</script>

<template>
  <div class="page-with-navbar">
    <div class="page-container admin-page">
      <div class="page-header">
        <h1>管理后台</h1>
        <p class="page-desc">平台概览与用户管理</p>
      </div>

      <div v-if="globalError" class="form-alert error">{{ globalError }}</div>

      <!-- Stats Cards -->
      <div v-if="!loading" class="stats-grid">
        <div class="stat-card card">
          <div class="stat-icon user-icon">
            <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <path d="M17 21v-2a4 4 0 00-4-4H5a4 4 0 00-4 4v2"/><circle cx="9" cy="7" r="4"/><path d="M23 21v-2a4 4 0 00-3-3.87M16 3.13a4 4 0 010 7.75"/>
            </svg>
          </div>
          <div class="stat-body">
            <span class="stat-value">{{ stats.totalUsers }}</span>
            <span class="stat-label">注册用户</span>
          </div>
        </div>
        <div class="stat-card card">
          <div class="stat-icon resume-icon">
            <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <path d="M14 2H6a2 2 0 00-2 2v16a2 2 0 002 2h12a2 2 0 002-2V8z"/><polyline points="14 2 14 8 20 8"/><line x1="16" y1="13" x2="8" y2="13"/><line x1="16" y1="17" x2="8" y2="17"/>
            </svg>
          </div>
          <div class="stat-body">
            <span class="stat-value">{{ stats.totalResumes }}</span>
            <span class="stat-label">总简历数</span>
          </div>
        </div>
        <div class="stat-card card">
          <div class="stat-icon pub-icon">
            <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <path d="M22 11.08V12a10 10 0 11-5.93-9.14"/><polyline points="22 4 12 14.01 9 11.01"/>
            </svg>
          </div>
          <div class="stat-body">
            <span class="stat-value">{{ stats.publishedResumes }}</span>
            <span class="stat-label">已发布</span>
          </div>
        </div>
        <div class="stat-card card">
          <div class="stat-icon draft-icon">
            <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <path d="M11 4H4a2 2 0 00-2 2v14a2 2 0 002 2h14a2 2 0 002-2v-7"/><path d="M18.5 2.5a2.121 2.121 0 013 3L12 15l-4 1 1-4 9.5-9.5z"/>
            </svg>
          </div>
          <div class="stat-body">
            <span class="stat-value">{{ stats.draftResumes }}</span>
            <span class="stat-label">草稿</span>
          </div>
        </div>
      </div>

      <!-- Tab Navigation -->
      <div class="tab-bar card">
        <button class="tab-btn" :class="{ active: activeTab === 'stats' }" @click="activeTab = 'stats'">
          <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <line x1="18" y1="20" x2="18" y2="10"/><line x1="12" y1="20" x2="12" y2="4"/><line x1="6" y1="20" x2="6" y2="14"/>
          </svg>
          数据概览
        </button>
        <button class="tab-btn" :class="{ active: activeTab === 'users' }" @click="activeTab = 'users'">
          <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <path d="M17 21v-2a4 4 0 00-4-4H5a4 4 0 00-4 4v2"/><circle cx="9" cy="7" r="4"/>
          </svg>
          用户管理
        </button>
        <button class="tab-btn" :class="{ active: activeTab === 'resumes' }" @click="activeTab = 'resumes'">
          <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <path d="M14 2H6a2 2 0 00-2 2v16a2 2 0 002 2h12a2 2 0 002-2V8z"/><polyline points="14 2 14 8 20 8"/>
          </svg>
          简历列表
        </button>
      </div>

      <!-- Loading -->
      <div v-if="loading" class="loading-overlay"><div class="spinner"></div></div>

      <!-- Stats Tab -->
      <div v-if="!loading && activeTab === 'stats'" class="stats-detail card">
        <h3>平台数据概览</h3>
        <div class="stats-table">
          <div class="stat-row">
            <span>注册用户总数</span>
            <strong>{{ stats.totalUsers }}</strong>
          </div>
          <div class="stat-row">
            <span>简历总数</span>
            <strong>{{ stats.totalResumes }}</strong>
          </div>
          <div class="stat-row">
            <span>已发布简历</span>
            <strong>{{ stats.publishedResumes }}</strong>
          </div>
          <div class="stat-row">
            <span>草稿简历</span>
            <strong>{{ stats.draftResumes }}</strong>
          </div>
          <div class="stat-row">
            <span>人均简历数</span>
            <strong>{{ stats.totalUsers > 0 ? (stats.totalResumes / stats.totalUsers).toFixed(1) : 0 }}</strong>
          </div>
        </div>
      </div>

      <!-- Users Tab -->
      <div v-if="!loading && activeTab === 'users'" class="table-card card">
        <h3>注册用户 ({{ users.length }})</h3>
        <div class="table-wrapper">
          <table class="data-table">
            <thead>
              <tr>
                <th>ID</th>
                <th>用户名</th>
                <th>邮箱</th>
                <th>角色</th>
                <th>简历数</th>
                <th>注册时间</th>
                <th>操作</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="user in users" :key="user.id">
                <td>{{ user.id }}</td>
                <td>{{ user.username }}</td>
                <td>{{ user.email }}</td>
                <td>
                  <span class="role-tag" :class="{ admin: user.role === 'admin' }">
                    {{ user.role === 'admin' ? '管理员' : '用户' }}
                  </span>
                </td>
                <td>{{ getResumeCount(user.id) }}</td>
                <td>{{ formatDateTime(user.createdAt) }}</td>
                <td>
                  <button class="btn btn-secondary btn-sm" @click="viewUserResumes(user)">
                    查看简历
                  </button>
                </td>
              </tr>
              <tr v-if="users.length === 0">
                <td colspan="7" class="empty-cell">暂无用户</td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>

      <!-- Resumes Tab -->
      <div v-if="!loading && activeTab === 'resumes'" class="table-card card">
        <h3>全部简历 ({{ resumes.length }})</h3>
        <div class="table-wrapper">
          <table class="data-table">
            <thead>
              <tr>
                <th>ID</th>
                <th>标题</th>
                <th>姓名</th>
                <th>用户ID</th>
                <th>状态</th>
                <th>更新时间</th>
                <th>操作</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="resume in resumes" :key="resume.id">
                <td>{{ resume.id }}</td>
                <td>{{ resume.title }}</td>
                <td>{{ resume.fullName }}</td>
                <td>{{ resume.userId }}</td>
                <td>
                  <span class="status-tag" :class="{ published: resume.status === 'published' }">
                    <span class="dot"></span>
                    {{ resume.status === 'published' ? '已发布' : '草稿' }}
                  </span>
                </td>
                <td>{{ formatDateTime(resume.updatedAt) }}</td>
                <td>
                  <button class="btn btn-secondary btn-sm" @click="router.push(`/preview/${resume.id}`)">
                    预览
                  </button>
                </td>
              </tr>
              <tr v-if="resumes.length === 0">
                <td colspan="7" class="empty-cell">暂无简历</td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>

      <!-- User Resumes Modal -->
      <div v-if="showUserModal" class="modal-backdrop" @click.self="showUserModal = false">
        <div class="modal-card card">
          <div class="modal-header">
            <h3>{{ selectedUser?.username }} 的简历</h3>
            <button class="modal-close-btn" @click="showUserModal = false">
              <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <path d="M18 6L6 18M6 6l12 12"/>
              </svg>
            </button>
          </div>
          <div class="modal-body">
            <div v-if="showUserResumes.length === 0" class="empty-cell">该用户暂无简历</div>
            <div v-for="r in showUserResumes" :key="r.id" class="resume-item">
              <div class="resume-info">
                <strong>{{ r.title }}</strong>
                <span class="status-tag" :class="{ published: r.status === 'published' }">
                  <span class="dot"></span>
                  {{ r.status === 'published' ? '已发布' : '草稿' }}
                </span>
              </div>
              <div class="resume-actions">
                <span class="time">{{ formatDateTime(r.updatedAt) }}</span>
                <button class="btn btn-secondary btn-sm" @click="router.push(`/preview/${r.id}`)">预览</button>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.admin-page {
  max-width: 1200px;
}

.page-header {
  margin-bottom: 24px;
}

.page-header h1 {
  font-size: 28px;
  font-weight: 700;
}

.page-desc {
  color: var(--text-secondary);
  font-size: 14px;
  margin-top: 4px;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 16px;
  margin-bottom: 24px;
}

.stat-card {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 20px;
}

.stat-icon {
  width: 48px;
  height: 48px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.stat-icon.user-icon { background: #eff6ff; color: #2563eb; }
.stat-icon.resume-icon { background: #f0fdf4; color: #16a34a; }
.stat-icon.pub-icon { background: #fefce8; color: #ca8a04; }
.stat-icon.draft-icon { background: #f5f3ff; color: #7c3aed; }

.stat-body {
  display: flex;
  flex-direction: column;
}

.stat-value {
  font-size: 28px;
  font-weight: 700;
  line-height: 1.2;
}

.stat-label {
  font-size: 13px;
  color: var(--text-secondary);
}

.tab-bar {
  display: flex;
  gap: 4px;
  padding: 6px;
  margin-bottom: 20px;
}

.tab-btn {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 8px 16px;
  border: none;
  background: transparent;
  border-radius: 6px;
  cursor: pointer;
  color: var(--text-secondary);
  font-weight: 500;
  transition: all 0.2s;
}

.tab-btn:hover {
  color: var(--text);
  background: var(--bg);
}

.tab-btn.active {
  color: var(--primary);
  background: var(--primary-light);
}

.stats-detail, .table-card {
  padding: 24px;
}

.stats-detail h3, .table-card h3 {
  font-size: 16px;
  margin-bottom: 16px;
}

.stats-table {
  display: flex;
  flex-direction: column;
  gap: 0;
}

.stat-row {
  display: flex;
  justify-content: space-between;
  padding: 12px 0;
  border-bottom: 1px solid var(--border);
}

.stat-row:last-child {
  border-bottom: none;
}

.stat-row strong {
  font-size: 16px;
}

.table-wrapper {
  overflow-x: auto;
}

.data-table {
  width: 100%;
  border-collapse: collapse;
  font-size: 13px;
}

.data-table th {
  text-align: left;
  padding: 10px 12px;
  font-weight: 600;
  color: var(--text-secondary);
  border-bottom: 1px solid var(--border);
  white-space: nowrap;
}

.data-table td {
  padding: 10px 12px;
  border-bottom: 1px solid var(--border);
}

.data-table tbody tr:hover {
  background: #f9fafb;
}

.empty-cell {
  text-align: center;
  color: var(--text-secondary);
  padding: 24px !important;
}

.role-tag {
  display: inline-block;
  padding: 2px 8px;
  border-radius: 4px;
  font-size: 12px;
  background: #f3f4f6;
  color: var(--text-secondary);
}

.role-tag.admin {
  background: #fefce8;
  color: #ca8a04;
}

.modal-backdrop {
  position: fixed;
  inset: 0;
  background: rgba(0, 0, 0, 0.45);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}

.modal-card {
  width: 520px;
  max-height: 80vh;
  display: flex;
  flex-direction: column;
}

.modal-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 16px 20px;
  border-bottom: 1px solid var(--border);
}

.modal-header h3 {
  font-size: 16px;
  font-weight: 600;
}

.modal-close-btn {
  background: none;
  border: none;
  cursor: pointer;
  color: var(--text-secondary);
  padding: 4px;
  border-radius: 4px;
}

.modal-close-btn:hover {
  background: var(--bg);
  color: var(--text);
}

.modal-body {
  padding: 16px 20px;
  overflow-y: auto;
  flex: 1;
}

.resume-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 12px 0;
  border-bottom: 1px solid var(--border);
}

.resume-item:last-child {
  border-bottom: none;
}

.resume-info {
  display: flex;
  align-items: center;
  gap: 8px;
}

.resume-actions {
  display: flex;
  align-items: center;
  gap: 12px;
}

.resume-actions .time {
  font-size: 12px;
  color: var(--text-secondary);
}
</style>