<script setup>
import { ref, onMounted } from 'vue'
import SortableList from '@/components/common/SortableList.vue'
import {
  selectCustomSectionsByResumeId,
  insertCustomSection,
  updateCustomSection,
  deleteCustomSection,
  customSectionSort
} from '@/api/customSection'
import { getErrorMessage } from '@/utils'

const props = defineProps({ resumeId: Number })
const emit = defineEmits(['refresh'])

const items = ref([])
const loading = ref(true)
const editingId = ref(null)
const showForm = ref(false)
const expandedIds = ref([])
const globalError = ref('')
const saving = ref(false)

const emptyForm = () => ({ sectionTitle: '', content: '' })
const form = ref(emptyForm())

async function load() {
  loading.value = true
  try {
    const { data } = await selectCustomSectionsByResumeId(props.resumeId)
    items.value = data.sections || []
  } catch (e) {
    globalError.value = getErrorMessage(e)
  } finally {
    loading.value = false
  }
}

function toggleExpand(id) {
  const idx = expandedIds.value.indexOf(id)
  if (idx >= 0) expandedIds.value.splice(idx, 1)
  else expandedIds.value.push(id)
}

function startAdd() { form.value = emptyForm(); editingId.value = null; showForm.value = true }
function startEdit(item) { form.value = { sectionTitle: item.sectionTitle, content: item.content }; editingId.value = item.id; showForm.value = true }

async function handleSave() {
  saving.value = true
  try {
    if (editingId.value) {
      await updateCustomSection({ id: editingId.value, resumeId: props.resumeId, ...form.value })
    } else {
      await insertCustomSection(form.value, props.resumeId)
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
    await deleteCustomSection(id, props.resumeId)
    await load()
    emit('refresh')
  } catch (e) {
    globalError.value = getErrorMessage(e)
  }
}

async function handleSort(sortedIds) {
  try {
    await customSectionSort(props.resumeId, sortedIds)
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
      <h2>自定义模块</h2>
      <button class="btn btn-primary btn-sm" @click="startAdd">+ 添加</button>
    </div>
    <div v-if="globalError" class="form-alert error">{{ globalError }}</div>
    <div v-if="loading" class="loading-overlay"><div class="spinner"></div></div>

    <SortableList v-else :items="items" @sort="handleSort">
      <template #default="{ item }">
        <div class="item-card card">
          <div class="drag-handle">⠿</div>
          <div class="item-content" @click="toggleExpand(item.id)">
            <div class="item-header-row">
              <span class="item-title">{{ item.sectionTitle }}</span>
              <span class="expand-icon">{{ expandedIds.includes(item.id) ? '▼' : '▶' }}</span>
            </div>
            <p v-if="!expandedIds.includes(item.id)" class="item-summary">{{ (item.content || '').replace(/<[^>]+>/g, '').substring(0, 80) }}...</p>
            <div v-if="expandedIds.includes(item.id)" class="item-full" v-html="item.content"></div>
          </div>
          <div class="item-actions">
            <button class="btn btn-ghost btn-sm" @click.stop="startEdit(item)">编辑</button>
            <button class="btn btn-ghost btn-sm danger-text" @click.stop="handleDelete(item.id)">删除</button>
          </div>
        </div>
      </template>
    </SortableList>

    <div v-if="showForm" class="form-card card">
      <div class="form-group">
        <label class="form-label required">模块标题</label>
        <input v-model="form.sectionTitle" class="form-input" />
      </div>
      <div class="form-group">
        <label class="form-label">内容</label>
        <textarea v-model="form.content" class="form-textarea" rows="8" placeholder="支持 HTML 富文本" />
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
.item-content { flex: 1; cursor: pointer; }
.item-header-row { display: flex; justify-content: space-between; align-items: center; }
.item-title { font-weight: 600; }
.expand-icon { color: var(--text-secondary); font-size: 12px; }
.item-summary { font-size: 13px; color: var(--text-secondary); margin-top: 4px; }
.item-full { margin-top: 8px; font-size: 13px; }
.item-actions { display: flex; gap: 4px; }
.form-card { margin-top: 16px; padding: 20px; }
.form-actions { display: flex; justify-content: flex-end; gap: 8px; margin-top: 12px; }
.danger-text { color: var(--danger) !important; }
</style>
