<script setup>
import { ref, onMounted } from 'vue'
import {
  selectEducationByResumeId,
  insertEducation,
  updateEducation,
  deleteEducation
} from '@/api/education'
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
  schoolName: '', degree: '', major: '', gpa: '',
  startDate: '', endDate: '', description: '', achievements: ''
})
const form = ref(emptyForm())

async function load() {
  loading.value = true
  try {
    const { data } = await selectEducationByResumeId(props.resumeId)
    items.value = data.educations || []
  } catch (e) {
    globalError.value = getErrorMessage(e)
  } finally {
    loading.value = false
  }
}

function startAdd() {
  form.value = emptyForm()
  editingId.value = null
  showForm.value = true
}

function startEdit(item) {
  form.value = { ...item }
  editingId.value = item.id
  showForm.value = true
}

async function handleSave() {
  saving.value = true
  try {
    if (editingId.value) {
      await updateEducation({ id: editingId.value, resumeId: props.resumeId, ...form.value })
    } else {
      await insertEducation(form.value, props.resumeId)
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
  if (!confirm('确定删除此教育经历？')) return
  try {
    await deleteEducation(id, props.resumeId)
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
      <h2>教育经历</h2>
      <button class="btn btn-primary btn-sm" @click="startAdd">+ 添加</button>
    </div>

    <div v-if="globalError" class="form-alert error">{{ globalError }}</div>
    <div v-if="loading" class="loading-overlay"><div class="spinner"></div></div>

    <div v-else class="item-list">
      <div v-for="item in items" :key="item.id" class="item-card card">
        <div class="item-content">
          <div class="item-title">{{ item.schoolName }} · {{ item.degree }}</div>
          <div class="item-meta">{{ item.major }} · {{ formatDate(item.startDate) }} - {{ formatDate(item.endDate) }}</div>
          <p v-if="item.description" class="item-desc">{{ item.description }}</p>
        </div>
        <div class="item-actions">
          <button class="btn btn-ghost btn-sm" @click="startEdit(item)">编辑</button>
          <button class="btn btn-ghost btn-sm danger-text" @click="handleDelete(item.id)">删除</button>
        </div>
      </div>
    </div>

    <div v-if="showForm" class="form-card card">
      <div class="form-grid">
        <div class="form-group">
          <label class="form-label required">学校名称</label>
          <input v-model="form.schoolName" class="form-input" />
        </div>
        <div class="form-group">
          <label class="form-label">学位</label>
          <input v-model="form.degree" class="form-input" />
        </div>
        <div class="form-group">
          <label class="form-label">专业</label>
          <input v-model="form.major" class="form-input" />
        </div>
        <div class="form-group">
          <label class="form-label">GPA</label>
          <input v-model="form.gpa" class="form-input" type="number" step="0.01" min="0" max="4" />
        </div>
        <div class="form-group">
          <label class="form-label required">入学日期</label>
          <input v-model="form.startDate" type="date" class="form-input" />
        </div>
        <div class="form-group">
          <label class="form-label">毕业日期</label>
          <input v-model="form.endDate" type="date" class="form-input" />
        </div>
      </div>
      <div class="form-group">
        <label class="form-label">描述</label>
        <textarea v-model="form.description" class="form-textarea" />
      </div>
      <div class="form-group">
        <label class="form-label">学术成就</label>
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
.module-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.module-header h2 { font-size: 18px; }

.item-list { display: flex; flex-direction: column; gap: 12px; }

.item-card {
  display: flex;
  align-items: flex-start;
  gap: 12px;
  padding: 16px;
}

.item-content { flex: 1; }
.item-title { font-weight: 600; margin-bottom: 4px; }
.item-meta { font-size: 12px; color: var(--text-secondary); margin-bottom: 6px; }
.item-desc { font-size: 13px; color: var(--text-secondary); }
.item-actions { display: flex; gap: 4px; }

.form-card { margin-top: 16px; padding: 20px; }
.form-grid { display: grid; grid-template-columns: 1fr 1fr; gap: 0 16px; }
.form-actions { display: flex; justify-content: flex-end; gap: 8px; margin-top: 12px; }
.danger-text { color: var(--danger) !important; }
</style>
