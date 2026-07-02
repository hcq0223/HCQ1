<script setup>
import { ref, onMounted } from 'vue'
import SortableList from '@/components/common/SortableList.vue'
import {
  selectWorkExperienceByResumeId,
  insertWorkExperience,
  updateWorkExperience,
  deleteWorkExperience,
  sortExperiences
} from '@/api/workExperience'
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
  companyName: '', position: '', location: '',
  startDate: '', endDate: '', description: '', achievements: ''
})
const form = ref(emptyForm())

async function load() {
  loading.value = true
  try {
    const { data } = await selectWorkExperienceByResumeId(props.resumeId)
    items.value = data.workExperiences || []
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
      await updateWorkExperience({ id: editingId.value, resumeId: props.resumeId, ...form.value })
    } else {
      await insertWorkExperience(form.value, props.resumeId)
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
  if (!confirm('确定删除此工作经历？')) return
  try {
    await deleteWorkExperience(id, props.resumeId)
    await load()
    emit('refresh')
  } catch (e) {
    globalError.value = getErrorMessage(e)
  }
}

async function handleSort(sortedIds) {
  try {
    await sortExperiences(props.resumeId, sortedIds)
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
      <h2>工作经历</h2>
      <button class="btn btn-primary btn-sm" @click="startAdd">+ 添加</button>
    </div>

    <div v-if="globalError" class="form-alert error">{{ globalError }}</div>
    <div v-if="loading" class="loading-overlay"><div class="spinner"></div></div>

    <SortableList v-else :items="items" @sort="handleSort">
      <template #default="{ item }">
        <div class="item-card card">
          <div class="drag-handle" title="拖拽排序">⠿</div>
          <div class="item-content">
            <div class="item-title">{{ item.companyName }} · {{ item.position }}</div>
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
          <label class="form-label required">公司名称</label>
          <input v-model="form.companyName" class="form-input" />
        </div>
        <div class="form-group">
          <label class="form-label required">职位</label>
          <input v-model="form.position" class="form-input" />
        </div>
        <div class="form-group">
          <label class="form-label">地点</label>
          <input v-model="form.location" class="form-input" />
        </div>
        <div class="form-group">
          <label class="form-label required">开始日期</label>
          <input v-model="form.startDate" type="date" class="form-input" />
        </div>
        <div class="form-group">
          <label class="form-label">结束日期</label>
          <input v-model="form.endDate" type="date" class="form-input" />
        </div>
      </div>
      <div class="form-group">
        <label class="form-label">工作描述</label>
        <textarea v-model="form.description" class="form-textarea" />
      </div>
      <div class="form-group">
        <label class="form-label">主要成就</label>
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

.module-header h2 {
  font-size: 18px;
}

.item-card {
  display: flex;
  align-items: flex-start;
  gap: 12px;
  padding: 16px;
}

.drag-handle {
  cursor: grab;
  color: var(--text-secondary);
  font-size: 18px;
  padding: 4px;
  user-select: none;
}

.item-content {
  flex: 1;
}

.item-title {
  font-weight: 600;
  margin-bottom: 4px;
}

.item-meta {
  font-size: 12px;
  color: var(--text-secondary);
  margin-bottom: 6px;
}

.item-desc {
  font-size: 13px;
  color: var(--text-secondary);
}

.item-actions {
  display: flex;
  gap: 4px;
}

.form-card {
  margin-top: 16px;
  padding: 20px;
}

.form-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 0 16px;
}

.form-actions {
  display: flex;
  justify-content: flex-end;
  gap: 8px;
  margin-top: 12px;
}

.danger-text {
  color: var(--danger) !important;
}
</style>
