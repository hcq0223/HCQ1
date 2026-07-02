<script setup>
import { ref, computed } from 'vue'
import TemplateModal from '@/components/resume/TemplateModal.vue'
import { getAvailableTemplates } from '@/api/template'

const props = defineProps({
  resumeId: { type: Number, required: true },
  data: { type: Object, default: () => ({}) }
})

const emit = defineEmits(['refresh'])

const showTemplateModal = ref(false)
const templates = ref([])

async function loadTemplates() {
  try {
    const { data } = await getAvailableTemplates()
    templates.value = data.templates || []
  } catch {}
}
loadTemplates()

const currentTemplate = computed(() => {
  const id = props.data?.templateId
  if (!id || !templates.value.length) return null
  return templates.value.find(t => t.id === id) || null
})

const currentTemplateName = computed(() => currentTemplate.value?.name || '未选择')
const currentTemplateDesc = computed(() => {
  const t = currentTemplate.value
  if (!t) return ''
  return t.description || ''
})
const currentTemplateCat = computed(() => currentTemplate.value?.category || '')

function handleSelect() {
  emit('refresh')
  showTemplateModal.value = false
}
</script>

<template>
  <div class="template-section">
    <div class="section-header">
      <h3>选择简历模板</h3>
      <p class="section-desc">选择不同风格的模板，右侧实时预览会立即更新样式</p>
    </div>

    <div class="current-template card">
      <div class="template-preview-icon" :class="currentTemplateCat">
        <svg viewBox="0 0 60 80" fill="none" width="60" height="80">
          <rect width="60" height="80" rx="4" fill="#f3f4f6"/>
          <rect x="10" y="12" width="40" height="6" rx="2" fill="#d1d5db"/>
          <rect x="10" y="24" width="28" height="3" rx="1" fill="#e5e7eb"/>
          <rect x="10" y="32" width="35" height="3" rx="1" fill="#e5e7eb"/>
          <rect x="10" y="42" width="40" height="3" rx="1" fill="#e5e7eb"/>
          <rect x="10" y="48" width="32" height="3" rx="1" fill="#e5e7eb"/>
        </svg>
      </div>
      <div class="template-info">
        <div class="template-name">{{ currentTemplateName }}</div>
        <div class="template-category">{{ currentTemplateDesc }}</div>
      </div>
    </div>

    <button class="btn btn-primary change-btn" @click="showTemplateModal = true">
      <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
        <path d="M19 21H5a2 2 0 01-2-2V5a2 2 0 012-2h11l5 5v11a2 2 0 01-2 2z"/><polyline points="17 21 17 13 7 13 7 21"/><polyline points="7 3 7 8 15 8"/>
      </svg>
      切换模板
    </button>

    <p class="tip">提示：模板只影响简历的展示样式，不会影响简历内容</p>

    <div class="template-previews">
      <h4>其他模板预览</h4>
      <div class="preview-grid">
        <div v-for="tpl in templates" :key="tpl.id" class="mini-preview" :class="tpl.category">
          <div class="mini-bar" :class="tpl.category"></div>
          <span>{{ tpl.name }}</span>
        </div>
      </div>
    </div>

    <TemplateModal
      :show="showTemplateModal"
      :resume-id="resumeId"
      :current-template-id="data?.templateId"
      @close="showTemplateModal = false"
      @select="handleSelect"
    />
  </div>
</template>

<style scoped>
.template-section { max-width: 600px; }
.section-header { margin-bottom: 20px; }
.section-header h3 { font-size: 18px; font-weight: 600; }
.section-desc { color: var(--text-secondary); font-size: 13px; margin-top: 4px; }
.current-template { display: flex; align-items: center; gap: 16px; padding: 16px; margin-bottom: 16px; }
.template-preview-icon { width: 80px; height: 100px; border-radius: 6px; overflow: hidden; border: 1px solid var(--border); display: flex; align-items: center; justify-content: center; background: #fff; flex-shrink: 0; }
.template-preview-icon svg rect:first-child { fill: #f0f0f0; }
.template-preview-icon.professional svg rect:nth-child(2) { fill: #2563eb; }
.template-preview-icon.creative svg rect:nth-child(2) { fill: #7c3aed; }
.template-preview-icon.modern svg rect:nth-child(2) { fill: #0ea5e9; }
.template-preview-icon.minimalist svg rect:nth-child(2) { fill: #6b7280; }
.template-preview-icon.academic svg rect:nth-child(2) { fill: #1e293b; }
.template-name { font-weight: 600; font-size: 15px; margin-bottom: 4px; }
.template-category { font-size: 12px; color: var(--text-secondary); }
.change-btn { width: 100%; margin-bottom: 8px; }
.tip { font-size: 12px; color: var(--text-secondary); text-align: center; margin-bottom: 24px; }
.template-previews h4 { font-size: 13px; color: var(--text-secondary); margin-bottom: 12px; }
.preview-grid { display: grid; grid-template-columns: repeat(5, 1fr); gap: 8px; }
.mini-preview { border: 1px solid var(--border); border-radius: 6px; padding: 8px; text-align: center; font-size: 11px; color: var(--text-secondary); cursor: default; }
.mini-preview .mini-bar { height: 20px; border-radius: 3px; margin-bottom: 6px; }
.mini-bar.professional { background: linear-gradient(135deg, #eff6ff, #2563eb); }
.mini-bar.creative { background: linear-gradient(135deg, #f3e8ff, #7c3aed); }
.mini-bar.modern { background: linear-gradient(135deg, #f0f9ff, #0ea5e9); }
.mini-bar.minimalist { background: #f3f4f6; }
.mini-bar.academic { background: linear-gradient(135deg, #f1f5f9, #1e293b); }
</style>