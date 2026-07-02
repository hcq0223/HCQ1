<script setup>
import { ref, computed, onMounted } from 'vue'
import {
  selectSkillsByResumeId,
  insertSkill,
  updateSkill,
  deleteSkill,
  skillBatch,
  skillSort
} from '@/api/skill'
import { SKILL_LEVELS, getErrorMessage } from '@/utils'

const props = defineProps({ resumeId: Number })
const emit = defineEmits(['refresh'])

const skills = ref([])
const loading = ref(true)
const globalError = ref('')
const editingSkill = ref(null)
const showAddForm = ref(false)
const addForm = ref({ skillName: '', category: '', proficiencyLevel: 'intermediate' })
const selectedIds = ref([])

const grouped = computed(() => {
  const map = {}
  skills.value.forEach((s) => {
    const cat = s.category || '未分类'
    if (!map[cat]) map[cat] = []
    map[cat].push(s)
  })
  return map
})

async function load() {
  loading.value = true
  try {
    const { data } = await selectSkillsByResumeId(props.resumeId)
    skills.value = data.skills || []
  } catch (e) {
    globalError.value = getErrorMessage(e)
  } finally {
    loading.value = false
  }
}

async function handleAdd() {
  if (!addForm.value.skillName) return
  try {
    await insertSkill({ ...addForm.value, resumeId: props.resumeId }, props.resumeId)
    showAddForm.value = false
    addForm.value = { skillName: '', category: '', proficiencyLevel: 'intermediate' }
    await load()
    emit('refresh')
  } catch (e) {
    globalError.value = getErrorMessage(e)
  }
}

async function handleUpdate(skill) {
  try {
    await updateSkill(skill)
    editingSkill.value = null
    await load()
    emit('refresh')
  } catch (e) {
    globalError.value = getErrorMessage(e)
  }
}

async function handleDelete(id) {
  try {
    await deleteSkill(id, props.resumeId)
    await load()
    emit('refresh')
  } catch (e) {
    globalError.value = getErrorMessage(e)
  }
}

async function handleBatchDelete() {
  if (selectedIds.value.length === 0) return
  try {
    await skillBatch(props.resumeId, selectedIds.value)
    selectedIds.value = []
    await load()
    emit('refresh')
  } catch (e) {
    globalError.value = getErrorMessage(e)
  }
}

function toggleSelect(id) {
  const idx = selectedIds.value.indexOf(id)
  if (idx >= 0) selectedIds.value.splice(idx, 1)
  else selectedIds.value.push(id)
}

onMounted(load)
</script>

<template>
  <div class="module">
    <div class="module-header">
      <h2>技能</h2>
      <button class="btn btn-primary btn-sm" @click="showAddForm = true">+ 添加技能</button>
    </div>

    <div v-if="globalError" class="form-alert error">{{ globalError }}</div>
    <div v-if="loading" class="loading-overlay"><div class="spinner"></div></div>

    <div v-else>
      <div v-for="(items, category) in grouped" :key="category" class="skill-group">
        <h3 class="category-title">{{ category }}</h3>
        <div class="skill-tags">
          <div v-for="skill in items" :key="skill.id" class="skill-tag" :class="{ editing: editingSkill?.id === skill.id }">
            <input
              v-if="selectedIds.length > 0 || selectedIds.includes(skill.id)"
              type="checkbox"
              :checked="selectedIds.includes(skill.id)"
              @change="toggleSelect(skill.id)"
              class="skill-check"
            />
            <template v-if="editingSkill?.id === skill.id">
              <input v-model="editingSkill.skillName" class="inline-input" />
              <select v-model="editingSkill.proficiencyLevel" class="inline-select">
                <option v-for="(label, key) in SKILL_LEVELS" :key="key" :value="key">{{ label }}</option>
              </select>
              <button class="tag-btn" @click="handleUpdate(editingSkill)">✓</button>
              <button class="tag-btn" @click="editingSkill = null">✕</button>
            </template>
            <template v-else>
              <span @click="editingSkill = { ...skill }">{{ skill.skillName }}</span>
              <button class="tag-remove" @click="handleDelete(skill.id)">×</button>
            </template>
          </div>
          <button class="skill-tag add-tag" @click="showAddForm = true; addForm.category = category">+ 添加</button>
        </div>
      </div>
    </div>

    <div v-if="showAddForm" class="form-card card">
      <div class="form-grid">
        <div class="form-group">
          <label class="form-label required">技能名称</label>
          <input v-model="addForm.skillName" class="form-input" />
        </div>
        <div class="form-group">
          <label class="form-label">分类</label>
          <input v-model="addForm.category" class="form-input" placeholder="如：编程语言" />
        </div>
        <div class="form-group">
          <label class="form-label">熟练度</label>
          <select v-model="addForm.proficiencyLevel" class="form-select">
            <option v-for="(label, key) in SKILL_LEVELS" :key="key" :value="key">{{ label }}</option>
          </select>
        </div>
      </div>
      <div class="form-actions">
        <button class="btn btn-secondary" @click="showAddForm = false">取消</button>
        <button class="btn btn-primary" @click="handleAdd">添加</button>
      </div>
    </div>

    <div v-if="selectedIds.length > 0" class="batch-bar">
      已选 {{ selectedIds.length }} 项
      <button class="btn btn-danger btn-sm" @click="handleBatchDelete">批量删除</button>
    </div>
  </div>
</template>

<style scoped>
.module-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 16px; }
.module-header h2 { font-size: 18px; }
.skill-group { margin-bottom: 20px; }
.category-title { font-size: 14px; color: var(--text-secondary); margin-bottom: 8px; }
.skill-tags { display: flex; flex-wrap: wrap; gap: 8px; }
.skill-tag {
  display: inline-flex; align-items: center; gap: 4px;
  background: var(--primary-light); color: var(--primary);
  padding: 4px 12px; border-radius: 16px; font-size: 13px; cursor: pointer;
}
.skill-tag.editing { background: #fff; border: 1px solid var(--primary); }
.tag-remove { background: none; border: none; cursor: pointer; color: var(--primary); font-size: 16px; padding: 0 2px; }
.tag-btn { background: none; border: none; cursor: pointer; padding: 0 4px; }
.add-tag { background: #fff; border: 1px dashed var(--primary); }
.inline-input { border: none; background: transparent; width: 80px; font-size: 13px; outline: none; }
.inline-select { border: none; background: transparent; font-size: 12px; outline: none; }
.form-card { margin-top: 16px; padding: 20px; }
.form-grid { display: grid; grid-template-columns: 1fr 1fr 1fr; gap: 0 16px; }
.form-actions { display: flex; justify-content: flex-end; gap: 8px; margin-top: 12px; }
.batch-bar {
  position: sticky; bottom: 0; background: #fff; border-top: 1px solid var(--border);
  padding: 12px 16px; display: flex; align-items: center; gap: 12px; margin-top: 16px;
}
.skill-check { margin-right: 4px; }
</style>
