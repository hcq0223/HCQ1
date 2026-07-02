<script setup>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { viewSharedResume, getPublicResume } from '@/api/share'
import ResumePreview from '@/components/resume/ResumePreview.vue'

const route = useRoute()
const token = route.params.token
const resume = ref(null)
const loading = ref(true)
const error = ref('')

async function load() {
  loading.value = true
  try {
    const viewRes = await viewSharedResume(token)
    if (!viewRes.data.success) {
      error.value = viewRes.data.message || '分享链接无效或已过期'
      return
    }
    const resumeId = viewRes.data.share?.resumeId
    if (!resumeId) { error.value = '分享记录不存在'; return }
    const detailRes = await getPublicResume(resumeId)
    if (detailRes.data.success) {
      resume.value = detailRes.data.resume
    } else {
      error.value = detailRes.data.message || '简历不存在或未公开'
    }
  } catch (e) {
    error.value = e.response?.data?.message || '分享链接无效或已过期'
  } finally { loading.value = false }
}

onMounted(load)
</script>

<template>
  <div class="public-page">
    <div v-if="loading" class="loading-overlay"><div class="spinner"></div></div>
    <div v-else-if="error" class="error-page">
      <svg width="80" height="80" viewBox="0 0 24 24" fill="none" stroke="#ef4444" stroke-width="1.5">
        <circle cx="12" cy="12" r="10"/><path d="M12 8v4M12 16h.01"/>
      </svg>
      <h2>无法查看简历</h2>
      <p>{{ error }}</p>
    </div>
    <template v-else>
      <div class="preview-container">
        <div class="a4-paper">
          <ResumePreview :resume="resume" />
        </div>
      </div>
      <footer class="public-footer">由 Resume Builder 生成</footer>
    </template>
  </div>
</template>

<style scoped>
.public-page { min-height: 100vh; background: var(--bg); display: flex; flex-direction: column; }
.preview-container { flex: 1; display: flex; justify-content: center; padding: 40px 24px; }
.a4-paper { width: 210mm; min-height: 297mm; background: #fff; box-shadow: var(--shadow-hover); }
.public-footer { text-align: center; padding: 20px; color: var(--text-secondary); font-size: 13px; }
.error-page { flex: 1; display: flex; flex-direction: column; align-items: center; justify-content: center; gap: 12px; color: var(--text-secondary); }
.error-page h2 { color: var(--text); font-size: 20px; }
</style>