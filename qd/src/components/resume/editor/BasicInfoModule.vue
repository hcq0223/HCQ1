<script setup>
import { ref, reactive, watch } from 'vue'
import { updateResume, updateResumeWithAvatar, updateStatusResume } from '@/api/resume'
import { getAvatarUrl, getErrorMessage } from '@/utils'

const props = defineProps({
  resumeId: Number,
  data: Object
})

const emit = defineEmits(['refresh'])

const form = reactive({
  fullName: '',
  email: '',
  phone: '',
  location: '',
  linkedinUrl: '',
  githubUrl: '',
  personalWebsite: '',
  professionalSummary: '',
  status: 'draft'
})

const saving = ref(false)
const globalError = ref('')
const avatarPreview = ref('')

watch(() => props.data, (val) => {
  if (val) {
    Object.assign(form, {
      fullName: val.fullName || '',
      email: val.email || '',
      phone: val.phone || '',
      location: val.location || '',
      linkedinUrl: val.linkedinUrl || '',
      githubUrl: val.githubUrl || '',
      personalWebsite: val.personalWebsite || '',
      professionalSummary: val.professionalSummary || '',
      status: val.status || 'draft'
    })
    avatarPreview.value = val.avatarUrl ? getAvatarUrl(val.avatarUrl) : ''
  }
}, { immediate: true })

async function handleSave() {
  saving.value = true
  globalError.value = ''
  try {
    await updateResume({ id: props.resumeId, ...form })
    emit('refresh')
  } catch (e) {
    globalError.value = getErrorMessage(e)
  } finally {
    saving.value = false
  }
}

async function handleAvatarChange(e) {
  const file = e.target.files?.[0]
  if (!file) return
  avatarPreview.value = URL.createObjectURL(file)
  try {
    await updateResumeWithAvatar({ id: props.resumeId, ...form }, file)
    emit('refresh')
  } catch (e) {
    globalError.value = getErrorMessage(e)
  }
}

async function toggleStatus() {
  const newStatus = form.status === 'published' ? 'draft' : 'published'
  try {
    await updateStatusResume(props.resumeId, newStatus)
    form.status = newStatus
    emit('refresh')
  } catch (e) {
    globalError.value = getErrorMessage(e)
  }
}
</script>

<template>
  <div class="basic-module">
    <div v-if="globalError" class="form-alert error">{{ globalError }}</div>

    <div class="basic-layout">
      <div class="avatar-section">
        <div class="avatar-circle">
          <img v-if="avatarPreview" :src="avatarPreview" alt="avatar" />
          <span v-else class="avatar-placeholder">{{ (form.fullName || 'U')[0] }}</span>
        </div>
        <label class="btn btn-secondary btn-sm avatar-upload">
          上传头像
          <input type="file" accept="image/*" hidden @change="handleAvatarChange" />
        </label>
      </div>

      <div class="form-grid">
        <div class="form-group">
          <label class="form-label required">姓名</label>
          <input v-model="form.fullName" class="form-input" />
        </div>
        <div class="form-group">
          <label class="form-label">邮箱</label>
          <input v-model="form.email" class="form-input" />
        </div>
        <div class="form-group">
          <label class="form-label">电话</label>
          <input v-model="form.phone" class="form-input" />
        </div>
        <div class="form-group">
          <label class="form-label">地址</label>
          <input v-model="form.location" class="form-input" />
        </div>
      </div>
    </div>

    <div class="form-group">
      <label class="form-label">LinkedIn</label>
      <input v-model="form.linkedinUrl" class="form-input" placeholder="https://linkedin.com/in/..." />
    </div>
    <div class="form-group">
      <label class="form-label">GitHub</label>
      <input v-model="form.githubUrl" class="form-input" placeholder="https://github.com/..." />
    </div>
    <div class="form-group">
      <label class="form-label">个人网站</label>
      <input v-model="form.personalWebsite" class="form-input" />
    </div>
    <div class="form-group">
      <label class="form-label">个人简介</label>
      <textarea v-model="form.professionalSummary" class="form-textarea" rows="5" />
    </div>

    <div class="module-actions">
      <button class="btn btn-secondary" @click="toggleStatus">
        {{ form.status === 'published' ? '设为草稿' : '发布简历' }}
      </button>
      <button class="btn btn-primary" :disabled="saving" @click="handleSave">
        {{ saving ? '保存中...' : '保存' }}
      </button>
    </div>
  </div>
</template>

<style scoped>
.basic-layout {
  display: flex;
  gap: 32px;
  margin-bottom: 16px;
}

.avatar-section {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 12px;
}

.avatar-circle {
  width: 120px;
  height: 120px;
  border-radius: 50%;
  overflow: hidden;
  background: var(--primary-light);
  display: flex;
  align-items: center;
  justify-content: center;
}

.avatar-circle img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.avatar-placeholder {
  font-size: 40px;
  font-weight: 700;
  color: var(--primary);
}

.form-grid {
  flex: 1;
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 0 16px;
}

.module-actions {
  display: flex;
  justify-content: flex-end;
  gap: 8px;
  margin-top: 16px;
  padding-top: 16px;
  border-top: 1px solid var(--border);
}
</style>
