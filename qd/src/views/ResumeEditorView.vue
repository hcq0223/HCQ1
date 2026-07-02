<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useResumeStore } from '@/stores/resume'
import { EDITOR_MODULES } from '@/utils'
import ResumePreview from '@/components/resume/ResumePreview.vue'
import TemplateModal from '@/components/resume/TemplateModal.vue'
import BasicInfoModule from '@/components/resume/editor/BasicInfoModule.vue'
import WorkExperienceModule from '@/components/resume/editor/WorkExperienceModule.vue'
import EducationModule from '@/components/resume/editor/EducationModule.vue'
import SkillModule from '@/components/resume/editor/SkillModule.vue'
import ProjectModule from '@/components/resume/editor/ProjectModule.vue'
import CertificationModule from '@/components/resume/editor/CertificationModule.vue'
import LanguageModule from '@/components/resume/editor/LanguageModule.vue'
import TemplateModule from '@/components/resume/editor/TemplateModule.vue'
import CustomSectionModule from '@/components/resume/editor/CustomSectionModule.vue'

const route = useRoute()
const router = useRouter()
const resumeStore = useResumeStore()

const resumeId = computed(() => Number(route.params.id))
const activeModule = ref('basic')
const loading = ref(true)
const showTemplateModal = ref(false)

const resumeDetail = computed(() => resumeStore.resumeDetail)

const moduleComponents = {
  basic: BasicInfoModule,
  work: WorkExperienceModule,
  education: EducationModule,
  skill: SkillModule,
  project: ProjectModule,
  certification: CertificationModule,
  language: LanguageModule,
  template: TemplateModule,
  custom: CustomSectionModule
}

const currentComponent = computed(() => moduleComponents[activeModule.value])

async function loadResume() {
  loading.value = true
  try {
    await resumeStore.loadResumeDetail(resumeId.value)
  } finally {
    loading.value = false
  }
}

async function refreshPreview() {
  await resumeStore.loadResumeDetail(resumeId.value)
}

function handleTemplateSelect() {
  refreshPreview()
}

onMounted(loadResume)
</script>

<template>
  <div class="editor-page">
    <div class="editor-toolbar">
      <button class="btn btn-secondary btn-sm" @click="router.push('/dashboard')">
        <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
          <path d="M19 12H5M12 19l-7-7 7-7"/>
        </svg>
        返回列表
      </button>
      <span class="editor-title">{{ resumeDetail?.title || '编辑简历' }}</span>
      <div class="toolbar-actions">
        <button class="btn btn-secondary btn-sm" @click="router.push(`/preview/${resumeId}`)">预览</button>
      </div>
    </div>

    <div class="editor-layout">
      <aside class="editor-nav">
        <div
          v-for="mod in EDITOR_MODULES"
          :key="mod.key"
          class="nav-item"
          :class="{ active: activeModule === mod.key }"
          @click="activeModule = mod.key"
        >
          {{ mod.label }}
        </div>
      </aside>

      <main class="editor-main">
        <div v-if="loading" class="loading-overlay"><div class="spinner"></div></div>
        <div v-else class="editor-card card">
          <component
            :is="currentComponent"
            :resume-id="resumeId"
            :data="resumeDetail"
            @refresh="refreshPreview"
          />
        </div>
      </main>

      <aside class="editor-preview">
        <div class="preview-label">实时预览</div>
        <div class="preview-scroll">
          <ResumePreview v-if="resumeDetail" :resume="resumeDetail" />
        </div>
      </aside>
    </div>
  </div>
</template>

<style scoped>
.editor-page {
  height: 100vh;
  display: flex;
  flex-direction: column;
  background: var(--bg);
}

.editor-toolbar {
  height: 48px;
  background: #fff;
  border-bottom: 1px solid var(--border);
  display: flex;
  align-items: center;
  padding: 0 16px;
  gap: 12px;
  flex-shrink: 0;
}

.editor-title {
  font-weight: 600;
  flex: 1;
}

.toolbar-actions {
  display: flex;
  gap: 8px;
}

.editor-layout {
  flex: 1;
  display: flex;
  overflow: hidden;
}

.editor-nav {
  width: 240px;
  background: #fff;
  border-right: 1px solid var(--border);
  overflow-y: auto;
  flex-shrink: 0;
}

.nav-item {
  padding: 12px 20px;
  cursor: pointer;
  border-left: 3px solid transparent;
  color: var(--text-secondary);
  transition: all 0.2s;
}

.nav-item:hover {
  background: var(--primary-light);
  color: var(--primary);
}

.nav-item.active {
  background: var(--primary-light);
  color: var(--primary);
  border-left-color: var(--primary);
  font-weight: 500;
}

.editor-main {
  flex: 1;
  overflow-y: auto;
  padding: 20px;
}

.editor-card {
  padding: 24px;
  min-height: 100%;
}

.editor-preview {
  width: 360px;
  background: var(--bg);
  border-left: 1px solid var(--border);
  display: flex;
  flex-direction: column;
  flex-shrink: 0;
}

.preview-label {
  padding: 12px 16px;
  font-size: 13px;
  font-weight: 500;
  color: var(--text-secondary);
  border-bottom: 1px solid var(--border);
  background: #fff;
}

.preview-scroll {
  flex: 1;
  overflow-y: auto;
  padding: 16px;
}

.preview-scroll :deep(.resume-preview-paper) {
  box-shadow: var(--shadow-hover);
  min-height: 500px;
  transform: scale(0.85);
  transform-origin: top center;
}
</style>