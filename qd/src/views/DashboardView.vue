<script setup>
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import AppModal from '@/components/common/AppModal.vue'
import EmptyState from '@/components/common/EmptyState.vue'
import ShareModal from '@/components/resume/ShareModal.vue'
import { selectResumeByUserId,
  selectResumeByTemplateId,
  selectResumeByStatus,
  insertResume,
  isDeleteResume,
  updateStatusResume,
  updateResume
} from '@/api/resume'
import { getAvailableTemplates } from '@/api/template'
import { formatDateTime, getErrorMessage, normalizeResumes, CATEGORY_LABELS } from '@/utils'

const router = useRouter()

const resumes = ref([])
const loading = ref(true)
const searchText = ref('')
const filterStatus = ref('')
const filterTemplateId = ref('')
const templates = ref([])
const globalError = ref('')

const showCreateModal = ref(false)
const createForm = ref({ title: '', fullName: '' })
const createLoading = ref(false)

const showRenameModal = ref(false)
const renameForm = ref({ id: null, title: '' })
const renameLoading = ref(false)

const showShareModal = ref(false)
const shareResumeId = ref(null)

const activeMenu = ref(null)

const filteredResumes = computed(() => {
  let list = resumes.value
  if (searchText.value) {
    const kw = searchText.value.toLowerCase()
    list = list.filter((r) => r?.title?.toLowerCase().includes(kw))
  }
  return list
})

const templateMap = computed(() => {
  const map = {}
  templates.value.forEach(t => { map[t.id] = t })
  return map
})

function getTemplateImageUrl(tpl) {
  if (!tpl?.previewImageUrl) return ""
  if (tpl.previewImageUrl.startsWith("http")) return tpl.previewImageUrl
  return "http://localhost:8080" + tpl.previewImageUrl
}

async function loadTemplates() {
  try {
    const { data } = await getAvailableTemplates()
    templates.value = data.templates || []
  } catch {}
}

async function loadResumes() {
  loading.value = true
  globalError.value = ''
  try {
    let res
    if (filterTemplateId.value) {
      res = await selectResumeByTemplateId(Number(filterTemplateId.value))
    } else if (filterStatus.value) {
      res = await selectResumeByStatus(filterStatus.value)
    } else {
      res = await selectResumeByUserId()
    }
    resumes.value = normalizeResumes(res.data)
  } catch (e) {
    globalError.value = getErrorMessage(e)
    resumes.value = []
  } finally {
    loading.value = false
  }
}

async function handleCreate() {
  if (!createForm.value.title || !createForm.value.fullName) return
  createLoading.value = true
  try {
    await insertResume({
      title: createForm.value.title,
      fullName: createForm.value.fullName,
      status: 'draft'
    })
    showCreateModal.value = false
    createForm.value = { title: '', fullName: '' }
    await loadResumes()
  } catch (e) {
    globalError.value = getErrorMessage(e)
  } finally {
    createLoading.value = false
  }
}

async function handleDelete(id) {
  if (!confirm('确定将此简历移入回收站？')) return
  try {
    await isDeleteResume(id)
    await loadResumes()
  } catch (e) {
    globalError.value = getErrorMessage(e)
  }
}

async function handleToggleStatus(resume) {
  const newStatus = resume.status === 'published' ? 'draft' : 'published'
  try {
    await updateStatusResume(resume.id, newStatus)
    await loadResumes()
  } catch (e) {
    globalError.value = getErrorMessage(e)
  }
}

async function handleCopy(resume) {
  try {
    await insertResume({
      title: `${resume.title} (副本)`,
      fullName: resume.fullName,
      email: resume.email,
      phone: resume.phone,
      location: resume.location,
      templateId: resume.templateId,
      status: 'draft'
    })
    await loadResumes()
  } catch (e) {
    globalError.value = getErrorMessage(e)
  }
}

async function handleRename() {
  if (!renameForm.value.title) return
  renameLoading.value = true
  try {
    await updateResume({ id: renameForm.value.id, title: renameForm.value.title })
    showRenameModal.value = false
    await loadResumes()
  } catch (e) {
    globalError.value = getErrorMessage(e)
  } finally {
    renameLoading.value = false
  }
}

function openRename(resume) {
  renameForm.value = { id: resume.id, title: resume.title }
  showRenameModal.value = true
  activeMenu.value = null
}

function openShare(resume) {
  if (resume.status !== 'published') return
  shareResumeId.value = resume.id
  showShareModal.value = true
}

function toggleMenu(id) {
  activeMenu.value = activeMenu.value === id ? null : id
}

function closeMenu() {
  activeMenu.value = null
}

onMounted(() => {
  window.addEventListener('resume-data-changed', loadResumes)
  loadTemplates()
  loadResumes()
  document.addEventListener('click', closeMenu)
})

onUnmounted(() => {
  window.removeEventListener('resume-data-changed', loadResumes)
  document.removeEventListener('click', closeMenu)
})
</script>

<template>
  <div class="page-with-navbar">
    <div class="page-container">
      <div class="page-header">
        <h1>我的简历</h1>
        <button class="btn btn-primary" @click="showCreateModal = true">
          <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <path d="M12 5v14M5 12h14"/>
          </svg>
          创建简历
        </button>
      </div>

      <div v-if="globalError" class="form-alert error">{{ globalError }}</div>

      <div class="filter-bar card">
        <input v-model="searchText" class="form-input search-input" placeholder="搜索简历标题..." />
        <select v-model="filterStatus" class="form-select filter-select" @change="loadResumes">
          <option value="">全部状态</option>
          <option value="draft">草稿</option>
          <option value="published">已发布</option>
        </select>
        <select v-model="filterTemplateId" class="form-select filter-select" @change="loadResumes">
          <option value="">全部模板</option>
          <option v-for="tpl in templates" :key="tpl.id" :value="tpl.id">{{ tpl.name }}</option>
        </select>
        <button class="btn btn-secondary btn-sm" @click="filterStatus = ''; filterTemplateId = ''; loadResumes()">重置</button>
      </div>

      <div v-if="loading" class="loading-overlay"><div class="spinner"></div></div>

      <EmptyState v-else-if="filteredResumes.length === 0" title="还没有简历" description="点击右上角创建你的第一份简历">
        <button class="btn btn-primary" style="margin-top:16px" @click="showCreateModal = true">创建简历</button>
      </EmptyState>

      <div v-else class="resume-grid">
        <div v-for="resume in filteredResumes" :key="resume.id" class="resume-card card" :class="{ 'menu-open': activeMenu === resume.id }">
          <div class="card-thumb">
            <img v-if="templateMap[resume.templateId]?.previewImageUrl"
                 :src="getTemplateImageUrl(templateMap[resume.templateId])"
                 :alt="templateMap[resume.templateId]?.name"
                 class="thumb-img" />
            <svg v-else viewBox="0 0 80 100" fill="none">
              <rect width="80" height="100" rx="4" fill="#f3f4f6"/>
              <rect x="12" y="12" width="56" height="6" rx="2" fill="#d1d5db"/>
              <rect x="12" y="24" width="40" height="4" rx="1" fill="#e5e7eb"/>
              <rect x="12" y="32" width="48" height="4" rx="1" fill="#e5e7eb"/>
              <rect x="12" y="44" width="56" height="3" rx="1" fill="#e5e7eb"/>
              <rect x="12" y="50" width="50" height="3" rx="1" fill="#e5e7eb"/>
            </svg>
          </div>
          <div class="card-body">
            <h3>{{ resume.title }} <span class="resume-id">#{{ resume.id }}</span></h3>
            <div class="card-meta">
              <span class="status-tag" :class="{ published: resume.status === 'published' }">
                <span class="dot"></span>
                {{ resume.status === 'published' ? '已发布' : '草稿' }}
              </span>
              <span class="time">{{ formatDateTime(resume.updatedAt) }}</span>
            </div>
            <div class="card-actions">
              <button class="btn btn-secondary btn-sm" @click="router.push(`/editor/${resume.id}`)">编辑</button>
              <button class="btn btn-secondary btn-sm" @click="router.push(`/preview/${resume.id}`)">预览</button>
              <button
                class="btn btn-secondary btn-sm"
                :class="{ 'btn-share-disabled': resume.status !== 'published' }"
                :disabled="resume.status !== 'published'"
                :title="resume.status !== 'published' ? '请先发布简历后再分享' : '分享简历'"
                @click="openShare(resume)"
              >分享</button>
              <div class="more-menu" @click.stop>
                <button class="btn btn-ghost btn-sm" @click="toggleMenu(resume.id)">更多</button>
                <div v-if="activeMenu === resume.id" class="dropdown-menu">
                  <button class="dropdown-item" @click="openRename(resume)">重命名</button>
                  <button class="dropdown-item" @click="handleCopy(resume); activeMenu = null">复制</button>
                  <button class="dropdown-item" @click="handleToggleStatus(resume); activeMenu = null">
                    {{ resume.status === 'published' ? '设为草稿' : '发布' }}
                  </button>
                  <button class="dropdown-item danger" @click="handleDelete(resume.id); activeMenu = null">删除</button>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>

      <router-link to="/trash" class="trash-link card">
        <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
          <path d="M3 6h18M8 6V4a2 2 0 012-2h4a2 2 0 012 2v2m3 0v14a2 2 0 01-2 2H7a2 2 0 01-2-2V6h14"/>
        </svg>
        回收站
      </router-link>
    </div>

    <AppModal :show="showCreateModal" title="创建简历" @close="showCreateModal = false">
      <div class="form-group">
        <label class="form-label required">简历标题</label>
        <input v-model="createForm.title" class="form-input" placeholder="如：前端开发工程师简历" />
      </div>
      <div class="form-group">
        <label class="form-label required">姓名</label>
        <input v-model="createForm.fullName" class="form-input" placeholder="您的真实姓名" />
      </div>
      <template #footer>
        <button class="btn btn-secondary" @click="showCreateModal = false">取消</button>
        <button class="btn btn-primary" :disabled="createLoading" @click="handleCreate">
          {{ createLoading ? '创建中...' : '创建' }}
        </button>
      </template>
    </AppModal>

    <AppModal :show="showRenameModal" title="重命名简历" @close="showRenameModal = false">
      <div class="form-group">
        <label class="form-label required">新标题</label>
        <input v-model="renameForm.title" class="form-input" />
      </div>
      <template #footer>
        <button class="btn btn-secondary" @click="showRenameModal = false">取消</button>
        <button class="btn btn-primary" :disabled="renameLoading" @click="handleRename">保存</button>
      </template>
    </AppModal>

    <ShareModal :show="showShareModal" :resume-id="shareResumeId" @close="showShareModal = false" />
  </div>
</template>

<style scoped>
.page-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 24px;
}

.page-header h1 {
  font-size: 28px;
  font-weight: 700;
}

.filter-bar {
  display: flex;
  gap: 12px;
  padding: 16px;
  margin-bottom: 24px;
  align-items: center;
}

.search-input {
  flex: 1;
  max-width: 300px;
}

.filter-select {
  width: 140px;
}

.resume-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 20px;
}

.resume-card {
  position: relative;
  transition: box-shadow 0.2s;
}

.resume-card.menu-open {
  z-index: 20;
}

.resume-card:hover {
  box-shadow: var(--shadow-hover);
}

.card-thumb {
  height: 140px;
  background: var(--bg);
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 20px;
  overflow: hidden;
  border-radius: var(--radius) var(--radius) 0 0;
}

.card-thumb img {
  height: 100%;
  width: 100%;
  object-fit: contain;
  border-radius: 4px;
}

.card-thumb svg {
  height: 100%;
  width: auto;
}

.card-body {
  padding: 16px;
}

.resume-id {
  font-size: 12px;
  color: var(--text-secondary);
  font-weight: 400;
  margin-left: 6px;
}

.card-body h3 {
  font-size: 16px;
  margin-bottom: 8px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.card-meta {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 12px;
}

.time {
  font-size: 12px;
  color: var(--text-secondary);
}

.card-actions {
  display: flex;
  gap: 6px;
  flex-wrap: wrap;
  position: relative;
  overflow: visible;
}

.more-menu {
  position: relative;
  margin-left: auto;
}

.more-menu .dropdown-menu {
  z-index: 200;
}

.trash-link {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  margin-top: 32px;
  padding: 12px 20px;
  color: var(--text-secondary);
  opacity: 0.7;
}

.trash-link:hover {
  opacity: 1;
  color: var(--primary);
}
</style>



