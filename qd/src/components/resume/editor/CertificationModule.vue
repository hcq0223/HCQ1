<script setup>
import { ref, onMounted } from 'vue'
import SortableList from '@/components/common/SortableList.vue'
import {
  selectCertificationsByResumeId,
  insertCertification,
  updateCertification,
  deleteCertification,
  certificationSort
} from '@/api/certification'
import { formatDate, getErrorMessage } from '@/utils'

const props = defineProps({ resumeId: Number })
const emit = defineEmits(['refresh'])

const items = ref([])
const loading = ref(true)
const editingId = ref(null)
const showForm = ref(false)
const globalError = ref('')
const saving = ref(false)
const permanent = ref(false)

const emptyForm = () => ({
  name: '', issuingOrganization: '', issueDate: '', expiryDate: '',
  credentialId: '', credentialUrl: ''
})
const form = ref(emptyForm())

async function load() {
  loading.value = true
  try {
    const { data } = await selectCertificationsByResumeId(props.resumeId)
    items.value = data.certifications || []
  } catch (e) {
    globalError.value = getErrorMessage(e)
  } finally {
    loading.value = false
  }
}

function startAdd() { form.value = emptyForm(); permanent.value = false; editingId.value = null; showForm.value = true }
function startEdit(item) {
  form.value = { ...item }
  permanent.value = !item.expiryDate
  editingId.value = item.id
  showForm.value = true
}

async function handleSave() {
  saving.value = true
  const data = { ...form.value, resumeId: props.resumeId }
  if (permanent.value) data.expiryDate = null
  try {
    if (editingId.value) {
      await updateCertification({ id: editingId.value, ...data })
    } else {
      await insertCertification(data, props.resumeId)
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
    await deleteCertification(id, props.resumeId)
    await load()
    emit('refresh')
  } catch (e) {
    globalError.value = getErrorMessage(e)
  }
}

async function handleSort(sortedIds) {
  try {
    await certificationSort(props.resumeId, sortedIds)
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
      <h2>证书与奖项</h2>
      <button class="btn btn-primary btn-sm" @click="startAdd">+ 添加</button>
    </div>
    <div v-if="globalError" class="form-alert error">{{ globalError }}</div>
    <div v-if="loading" class="loading-overlay"><div class="spinner"></div></div>

    <SortableList v-else :items="items" @sort="handleSort">
      <template #default="{ item }">
        <div class="item-card card">
          <div class="drag-handle">⠿</div>
          <div class="item-content">
            <div class="item-title">{{ item.name }}</div>
            <div class="item-meta">{{ item.issuingOrganization }} · {{ formatDate(item.issueDate) }}</div>
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
          <label class="form-label required">名称</label>
          <input v-model="form.name" class="form-input" />
        </div>
        <div class="form-group">
          <label class="form-label">颁发机构</label>
          <input v-model="form.issuingOrganization" class="form-input" />
        </div>
        <div class="form-group">
          <label class="form-label">获得日期</label>
          <input v-model="form.issueDate" type="date" class="form-input" />
        </div>
        <div class="form-group">
          <label class="form-label">有效期至</label>
          <input v-model="form.expiryDate" type="date" class="form-input" :disabled="permanent" />
          <label class="checkbox-label"><input v-model="permanent" type="checkbox" /> 永久有效</label>
        </div>
        <div class="form-group">
          <label class="form-label">证书编号</label>
          <input v-model="form.credentialId" class="form-input" />
        </div>
        <div class="form-group">
          <label class="form-label">验证链接</label>
          <input v-model="form.credentialUrl" class="form-input" />
        </div>
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
.item-actions { display: flex; gap: 4px; }
.form-card { margin-top: 16px; padding: 20px; }
.form-grid { display: grid; grid-template-columns: 1fr 1fr; gap: 0 16px; }
.form-actions { display: flex; justify-content: flex-end; gap: 8px; margin-top: 12px; }
.checkbox-label { display: flex; align-items: center; gap: 6px; margin-top: 6px; font-size: 13px; cursor: pointer; }
.danger-text { color: var(--danger) !important; }
</style>
