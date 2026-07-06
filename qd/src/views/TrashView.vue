<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import { selectTrashResume, noIsDeleteResume, deleteResume } from '@/api/resume'
import EmptyState from '@/components/common/EmptyState.vue'
import { formatDateTime, getErrorMessage, normalizeResumes } from '@/utils'

const resumes = ref([])
const loading = ref(true)
const globalError = ref('')

async function loadTrash() {
  loading.value = true
  try {
    const { data } = await selectTrashResume()
    resumes.value = normalizeResumes(data)
  } catch (e) {
    globalError.value = getErrorMessage(e)
    resumes.value = []
  } finally {
    loading.value = false
  }
}

async function handleRestore(id) {
  try {
    await noIsDeleteResume(id)
    await loadTrash()
  } catch (e) {
    globalError.value = getErrorMessage(e)
  }
}

async function handlePermanentDelete(id) {
  if (!confirm('确定永久删除此简历？此操作不可恢复。')) return
  try {
    await deleteResume(id)
    await loadTrash()
  } catch (e) {
    globalError.value = getErrorMessage(e)
  }
}

onMounted(() => {
  window.addEventListener('resume-data-changed', loadTrash)
  loadTrash()
})
onUnmounted(() => {
  window.removeEventListener('resume-data-changed', loadTrash)
})
</script>

<template>
  <div class="page-with-navbar">
    <div class="page-container">
      <div class="page-header">
        <h1>回收站</h1>
        <router-link to="/dashboard" class="back-link">← 返回我的简历</router-link>
      </div>

      <div v-if="globalError" class="form-alert error">{{ globalError }}</div>

      <div v-if="loading" class="loading-overlay"><div class="spinner"></div></div>

      <EmptyState v-else-if="resumes.length === 0" title="回收站为空" description="已删除的简历会出现在这里" />

      <div v-else class="trash-list card">
        <div v-for="resume in resumes" :key="resume.id" class="trash-item">
          <div class="trash-info">
            <h3>{{ resume.title }}</h3>
            <span class="time">删除时间：{{ formatDateTime(resume.updatedAt) }}</span>
          </div>
          <div class="trash-actions">
            <button class="btn btn-secondary btn-sm" @click="handleRestore(resume.id)">恢复</button>
            <button class="btn btn-danger btn-sm" @click="handlePermanentDelete(resume.id)">彻底删除</button>
          </div>
        </div>
      </div>
    </div>
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

.back-link {
  color: var(--text-secondary);
  font-size: 14px;
}

.back-link:hover {
  color: var(--primary);
}

.trash-list {
  overflow: hidden;
}

.trash-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 16px 20px;
  border-bottom: 1px solid var(--border);
}

.trash-item:last-child {
  border-bottom: none;
}

.trash-info h3 {
  font-size: 15px;
  margin-bottom: 4px;
}

.time {
  font-size: 12px;
  color: var(--text-secondary);
}

.trash-actions {
  display: flex;
  gap: 8px;
}
</style>
