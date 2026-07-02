<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getFullResume } from '@/api/resume'
import ResumePreview from '@/components/resume/ResumePreview.vue'
import ShareModal from '@/components/resume/ShareModal.vue'
import { getErrorMessage } from '@/utils'

const route = useRoute()
const router = useRouter()

const resumeId = Number(route.params.id)
const resume = ref(null)
const loading = ref(true)
const showShareModal = ref(false)
const globalError = ref('')

async function load() {
  loading.value = true
  try {
    const { data } = await getFullResume(resumeId)
    resume.value = data.resume
  } catch (e) {
    globalError.value = getErrorMessage(e)
  } finally {
    loading.value = false
  }
}

function handlePrint() {
  window.print()
}

function handleDownload() {
  window.print()
}

onMounted(load)
</script>

<template>
  <div class="preview-page">
    <div class="preview-toolbar">
      <div class="toolbar-left">
        <button class="btn btn-secondary btn-sm" @click="router.push('/dashboard')">
          <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <path d="M19 12H5M12 19l-7-7 7-7"/>
          </svg>
          返回列表
        </button>
        <button class="btn btn-ghost btn-sm" @click="router.push(`/editor/${resumeId}`)">返回编辑</button>
      </div>
      <div class="toolbar-actions">
        <button class="btn btn-secondary btn-sm" @click="handleDownload">下载 PDF</button>
        <button class="btn btn-secondary btn-sm" @click="handlePrint">打印</button>
        <button
          class="btn btn-sm"
          :class="resume?.status === 'published' ? 'btn-primary' : 'btn-share-disabled'"
          :disabled="resume?.status !== 'published'"
          :title="resume?.status !== 'published' ? '请先发布简历后再分享' : '分享简历'"
          @click="showShareModal = true"
        >分享</button>
      </div>
    </div>

    <div v-if="globalError" class="form-alert error page-error">{{ globalError }}</div>
    <div v-if="loading" class="loading-overlay"><div class="spinner"></div></div>

    <div v-else class="preview-container">
      <div class="a4-paper">
        <ResumePreview :resume="resume" />
      </div>
    </div>

    <ShareModal :show="showShareModal" :resume-id="resumeId" @close="showShareModal = false" />
  </div>
</template>

<style scoped>
.preview-page {
  min-height: 100vh;
  background: var(--bg);
}

.preview-toolbar {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  height: 56px;
  background: rgba(255, 255, 255, 0.92);
  backdrop-filter: blur(8px);
  border-bottom: 1px solid var(--border);
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 24px;
  z-index: 50;
}

.toolbar-left {
  display: flex;
  align-items: center;
  gap: 8px;
}

.toolbar-actions {
  display: flex;
  gap: 8px;
}

.page-error {
  max-width: 600px;
  margin: 80px auto 0;
}

.preview-container {
  padding: 80px 24px 40px;
  display: flex;
  justify-content: center;
}

.a4-paper {
  width: 210mm;
  min-height: 297mm;
  background: #fff;
  box-shadow: var(--shadow-hover);
  overflow: hidden;
}

@media print {
  .preview-toolbar { display: none; }
  .preview-container { padding: 0; }
  .a4-paper { box-shadow: none; width: 100%; }
}
</style>
