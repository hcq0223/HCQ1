<script setup>
import { ref, onMounted } from 'vue'
import SortableList from '@/components/common/SortableList.vue'
import {
  selectLanguageByResumeId,
  insertLanguage,
  updateLanguage,
  deleteLanguage,
  languageSort
} from '@/api/language'
import { LANGUAGE_LEVELS, getErrorMessage } from '@/utils'

const props = defineProps({ resumeId: Number })
const emit = defineEmits(['refresh'])

const items = ref([])
const loading = ref(true)
const editingId = ref(null)
const showForm = ref(false)
const globalError = ref('')
const saving = ref(false)

const emptyForm = () => ({ languageName: '', proficiencyLevel: 'professional', resumeId: props.resumeId })
const form = ref(emptyForm())

async function load() {
  loading.value = true
  try {
    const { data } = await selectLanguageByResumeId(props.resumeId)
    items.value = data.languages || []
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
      await updateLanguage({ id: editingId.value, ...form.value, resumeId: props.resumeId })
    } else {
      await insertLanguage({ ...form.value, resumeId: props.resumeId })
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
    await deleteLanguage(id, props.resumeId)
    await load()
    emit('refresh')
  } catch (e) {
    globalError.value = getErrorMessage(e)
  }
}

async function handleSort(sortedIds) {
  try {
    await languageSort(props.resumeId, sortedIds)
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
      <h2>语言能力</h2>
      <button class="btn btn-primary btn-sm" @click="startAdd">+ 添加</button>
    </div>
    <div v-if="globalError" class="form-alert error">{{ globalError }}</div>
    <div v-if="loading" class="loading-overlay"><div class="spinner"></div></div>

    <SortableList v-else :items="items" @sort="handleSort">
      <template #default="{ item }">
        <div class="item-card card compact">
          <div class="drag-handle">⠿</div>
          <div class="item-content">
            <span class="lang-name">{{ item.languageName }}</span>
            <span class="lang-level">{{ LANGUAGE_LEVELS[item.proficiencyLevel] || item.proficiencyLevel }}</span>
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
          <label class="form-label required">语言名称</label>
          <input v-model="form.languageName" class="form-input" />
        </div>
        <div class="form-group">
          <label class="form-label">熟练度</label>
          <select v-model="form.proficiencyLevel" class="form-select">
            <option v-for="(label, key) in LANGUAGE_LEVELS" :key="key" :value="key">{{ label }}</option>
          </select>
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
.item-card.compact { display: flex; align-items: center; gap: 12px; padding: 12px 16px; }
.drag-handle { cursor: grab; color: var(--text-secondary); }
.item-content { flex: 1; display: flex; gap: 12px; }
.lang-name { font-weight: 600; }
.lang-level { color: var(--text-secondary); font-size: 13px; }
.item-actions { display: flex; gap: 4px; }
.form-card { margin-top: 16px; padding: 20px; }
.form-grid { display: grid; grid-template-columns: 1fr 1fr; gap: 0 16px; }
.form-actions { display: flex; justify-content: flex-end; gap: 8px; margin-top: 12px; }
.danger-text { color: var(--danger) !important; }
</style>
