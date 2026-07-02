<script setup>
import { ref, watch } from 'vue'
import AppModal from '@/components/common/AppModal.vue'
import { getAvailableTemplates } from '@/api/template'
import { updateResume } from '@/api/resume'
import { TEMPLATE_CATEGORIES, CATEGORY_LABELS, getErrorMessage } from '@/utils'

const props = defineProps({
  show: Boolean,
  resumeId: Number,
  currentTemplateId: Number
})

const emit = defineEmits(['close', 'select'])

const templates = ref([])
const loading = ref(false)
const activeCategory = ref('')
const previewTemplate = ref(null)
const applying = ref(false)
const globalError = ref('')

async function loadTemplates() {
  loading.value = true
  try {
    const { data } = await getAvailableTemplates(activeCategory.value || undefined)
    templates.value = data.templates || []
  } catch (e) {
    globalError.value = getErrorMessage(e)
  } finally {
    loading.value = false
  }
}

async function handleUse(template) {
  applying.value = true
  try {
    await updateResume({ id: props.resumeId, templateId: template.id })
    emit('select', template)
    emit('close')
  } catch (e) {
    globalError.value = getErrorMessage(e)
  } finally {
    applying.value = false
  }
}

watch(() => props.show, (val) => {
  if (val) {
    globalError.value = ''
    loadTemplates()
  }
})

watch(activeCategory, loadTemplates)
</script>

<template>
  <AppModal :show="show" title="选择模板" width="900px" @close="emit('close')">
    <div v-if="globalError" class="form-alert error">{{ globalError }}</div>

    <div class="category-tabs">
      <button
        v-for="cat in TEMPLATE_CATEGORIES"
        :key="cat.value"
        class="tab-btn"
        :class="{ active: activeCategory === cat.value }"
        @click="activeCategory = cat.value"
      >
        {{ cat.label }}
      </button>
    </div>

    <div v-if="loading" class="loading-overlay"><div class="spinner"></div></div>

    <div v-else class="template-grid">
      <div v-for="tpl in templates" :key="tpl.id" class="template-card">
        <div class="template-preview" @click="previewTemplate = tpl">
          <img v-if="tpl.previewImageUrl" :src="(tpl.previewImageUrl && !tpl.previewImageUrl.startsWith('http')) ? 'http://localhost:8080' + tpl.previewImageUrl : tpl.previewImageUrl" :alt="tpl.name" />
          <div v-else class="preview-placeholder">
            <svg viewBox="0 0 60 80" fill="none">
              <rect width="60" height="80" rx="3" fill="#f3f4f6"/>
              <rect x="8" y="10" width="44" height="4" rx="1" fill="#d1d5db"/>
              <rect x="8" y="18" width="30" height="3" rx="1" fill="#e5e7eb"/>
            </svg>
          </div>
        </div>
        <div class="template-info">
          <span class="template-name">{{ tpl.name }}</span>
          <span class="template-badge" :class="{ premium: tpl.isPremium }">
            {{ tpl.isPremium ? '付费' : '免费' }}
          </span>
        </div>
        <div class="template-actions">
          <button class="btn btn-secondary btn-sm" @click="previewTemplate = tpl">预览</button>
          <button
            class="btn btn-primary btn-sm"
            :disabled="applying"
            @click="handleUse(tpl)"
          >
            {{ tpl.id === currentTemplateId ? '当前' : '使用' }}
          </button>
        </div>
      </div>
    </div>

    <AppModal
      :show="!!previewTemplate"
      :title="previewTemplate?.name"
      width="700px"
      @close="previewTemplate = null"
    >
      <div class="preview-large">
        <img v-if="previewTemplate?.previewImageUrl" :src="(previewTemplate.previewImageUrl && !previewTemplate.previewImageUrl.startsWith('http')) ? 'http://localhost:8080' + previewTemplate.previewImageUrl : previewTemplate.previewImageUrl" />
        <div v-else class="preview-placeholder large">
          <p>{{ previewTemplate?.name }}</p>
          <p class="desc">{{ previewTemplate?.description || CATEGORY_LABELS[previewTemplate?.category] }}</p>
        </div>
      </div>
      <template #footer>
        <button class="btn btn-secondary" @click="previewTemplate = null">关闭</button>
        <button class="btn btn-primary" :disabled="applying" @click="handleUse(previewTemplate); previewTemplate = null">
          使用此模板
        </button>
      </template>
    </AppModal>
  </AppModal>
</template>

<style scoped>
.category-tabs {
  display: flex;
  gap: 4px;
  margin-bottom: 20px;
  border-bottom: 1px solid var(--border);
  padding-bottom: 12px;
}

.tab-btn {
  padding: 6px 16px;
  border: none;
  background: none;
  cursor: pointer;
  border-radius: 6px;
  color: var(--text-secondary);
  font-size: 13px;
}

.tab-btn.active {
  background: var(--primary-light);
  color: var(--primary);
  font-weight: 500;
}

.template-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 16px;
}

.template-card {
  border: 1px solid var(--border);
  border-radius: var(--radius);
  overflow: hidden;
  transition: box-shadow 0.2s;
}

.template-card:hover {
  box-shadow: var(--shadow-hover);
}

.template-preview {
  height: 160px;
  background: var(--bg);
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  overflow: hidden;
}

.template-preview img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.preview-placeholder {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 100%;
}

.preview-placeholder svg {
  height: 80%;
}

.template-info {
  padding: 10px 12px;
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.template-name {
  font-weight: 500;
  font-size: 13px;
}

.template-badge {
  font-size: 11px;
  padding: 2px 8px;
  border-radius: 10px;
  background: #f0fdf4;
  color: #16a34a;
}

.template-badge.premium {
  background: #fef3c7;
  color: #d97706;
}

.template-actions {
  padding: 0 12px 12px;
  display: flex;
  gap: 6px;
}

.preview-large {
  text-align: center;
}

.preview-large img {
  max-width: 100%;
  border-radius: var(--radius);
}

.preview-placeholder.large {
  padding: 60px;
  background: var(--bg);
  border-radius: var(--radius);
}

.desc {
  color: var(--text-secondary);
  margin-top: 8px;
}
</style>
