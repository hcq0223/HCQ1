<script setup>
import { ref, onMounted } from 'vue'
import SortableList from '@/components/common/SortableList.vue'
import {
  selectProjectsByResumeId,
  insertProject,
  updateProject,
  deleteProject,
  projectSort
} from '@/api/project'
import { formatDate, getErrorMessage } from '@/utils'

const props = defineProps({ resumeId: Number })
const emit = defineEmits(['refresh'])

const items = ref([])
const loading = ref(true)
const editingId = ref(null)
const showForm = ref(false)
const globalError = ref('')
const saving = ref(false)

const emptyForm = () => ({
  projectName: '', role: '', projectUrl: '',
  startDate: '', endDate: '', description: '', technologiesUsed: '', achievements: ''
})
const form = ref(emptyForm())

async function load() {
  loading.value = true
  try {
    const { data } = await selectProjectsByResumeId(props.resumeId)
    items.value = data.projects || []
  } catch (e) {
    globalError.value = getErrorMessage(e)
  } finally {
    loading.value = false
  }
}

function startAdd() { form.value = emptyForm(); editingId.value = null; showForm.value = true }
function startEdit(item) { form.value = { ...item }; editingId.value = item.id; showForm.value = true }

async function handleSave() {
  saving.value = true
  try {
    if (editingId.value) {
      await updateProject({ id: editingId.value, resumeId: props.resumeId, ...form.value })
    } else {
      await insertProject(form.value, props.resumeId)
    }
    showForm.value = false
    await load()
    emit('refresh')
  } catch (e) {
    globalError.value = getErrorMessage(e)
  } finally {
    saving.value = false
  }
}

async function handleDelete(id) {
  if (!confirm('确定删除？')) return
  try {
    await deleteProject(id, props.resumeId)
    await load()
    emit('refresh')
  } catch (e) {
    globalError.value = getErrorMessage(e)
  }
}

async function handleSort(sortedIds) {
  try {
    await projectSort(props.resumeId, sortedIds)
    await load()
    emit('refresh')
  } catch (e) {
    globalError.value = getErrorMessage(e)
  }
}

onMounted(load)
</script>

<template>
  <div class="module">
    <div class="module-header">
      <h2>项目经历</h2>
      <button class="btn btn-primary btn-sm" @click="startAdd">+ 添加</button>
    </div>
    <div v-if="globalError" class="form-alert error">{{ globalError }}</div>
    <div v-if="loading" class="loading-overlay"><div class="spinner"></div></div>

    <SortableList v-else :items="items" @sort="handleSort">
      <template #default="{ item }">
        <div class="item-card card">
          <div class="drag-handle">⠿</div>
          <div class="item-content">
            <div class="item-title">{{ item.projectName }} · {{ item.role }}</div>
            <div class="item-meta">{{ formatDate(item.startDate) }} - {{ formatDate(item.endDate) }}</div>
            <p v-if="item.description" class="item-desc">{{ item.description }}</p>
          </div>
          <div class="item-actions">
            <button class="btn btn-ghost btn-sm" @click="startEdit(item)">编辑</button>
            <button class="btn btn-ghost btn-sm danger-text" @click="handleDelete(item.id)">删除</button>
          </div>
        </div>
      </template>
    </SortableList>

    <div v-if="showForm" class="form-card card">
      <div class="form-grid">
        <div class="form-group">
          <label class="form-label required">项目名称</label>
          <input v-model="form.projectName" class="form-input" />
        </div>
        <div class="form-group">
          <label class="form-label">角色</label>
          <input v-model="form.role" class="form-input" />
        </div>
        <div class="form-group">
          <label class="form-label">项目链接</label>
          <input v-model="form.projectUrl" class="form-input" />
        </div>
        <div class="form-group">
          <label class="form-label">开始日期</label>
          <input v-model="form.startDate" type="date" class="form-input" />
        </div>
        <div class="form-group">
          <label class="form-label">结束日期</label>
          <input v-model="form.endDate" type="date" class="form-input" />
        </div>
      </div>
      <div class="form-group">
        <label class="form-label">项目描述</label>
        <textarea v-model="form.description" class="form-textarea" />
      </div>
      <div class="form-group">
        <label class="form-label">技术栈</label>
        <input v-model="form.technologiesUsed" class="form-input" placeholder="React, Node.js, MySQL..." />
      </div>
      <div class="form-group">
        <label class="form-label">项目成果</label>
        <textarea v-model="form.achievements" class="form-textarea" />
      </div>
      <div class="form-actions">
        <button class="btn btn-secondary" @click="showForm = false">取消</button>
        <button class="btn btn-primary" :disabled="saving" @click="handleSave">保存</button>
      </div>
    </div>
  </div>
</template>

<style scoped>
.module-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 16px; }
.module-header h2 { font-size: 18px; }
.item-card { display: flex; gap: 12px; padding: 16px; }
.drag-handle { cursor: grab; color: var(--text-secondary); font-size: 18px; }
.item-content { flex: 1; }
.item-title { font-weight: 600; margin-bottom: 4px; }
.item-meta { font-size: 12px; color: var(--text-secondary); }
.item-desc { font-size: 13px; color: var(--text-secondary); margin-top: 4px; }
.item-actions { display: flex; gap: 4px; }
.form-card { margin-top: 16px; padding: 20px; }
.form-grid { display: grid; grid-template-columns: 1fr 1fr; gap: 0 16px; }
.form-actions { display: flex; justify-content: flex-end; gap: 8px; margin-top: 12px; }
.danger-text { color: var(--danger) !important; }
</style>
