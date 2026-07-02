<script setup>
import { ref, onMounted } from 'vue'
import { getAdminTemplates, insertAdminTemplate, updateAdminTemplate, deleteAdminTemplate, toggleAdminTemplate, uploadTemplateImage } from '@/api/admin'
import { formatDateTime, getErrorMessage, CATEGORY_LABELS } from '@/utils'

const templates = ref([])
const loading = ref(true)
const globalError = ref('')
const successMsg = ref('')

const showModal = ref(false)
const editMode = ref(false)
const form = ref({ id: null, name: '', description: '', category: 'professional', isPremium: false, isActive: true, previewImageUrl: '' })
const saving = ref(false)
const selectedFile = ref(null)

async function handleFileSelect(e) {
  const file = e.target.files[0]
  if (!file) return
  const formData = new FormData()
  formData.append('file', file)
  saving.value = true
  try {
    const { data } = await uploadTemplateImage(formData)
    if (data.url) { form.value.previewImageUrl = data.url }
  } catch (ex) { globalError.value = getErrorMessage(ex) }
  finally { saving.value = false }
}
const confirmDelete = ref(null)

const CATEGORIES = [
  { value: 'professional', label: '专业' },
  { value: 'creative', label: '创意' },
  { value: 'modern', label: '现代' },
  { value: 'minimalist', label: '极简' },
  { value: 'academic', label: '学术' }
]

async function loadTemplates() {
  loading.value = true
  globalError.value = ''
  try {
    const { data } = await getAdminTemplates()
    templates.value = data.templates || []
  } catch (e) {
    globalError.value = getErrorMessage(e)
  } finally {
    loading.value = false
  }
}

function openAdd() {
  editMode.value = false
  form.value = { id: null, name: '', description: '', category: 'professional', isPremium: false, isActive: true, previewImageUrl: '' }
  showModal.value = true
}

function openEdit(tpl) {
  editMode.value = true
  form.value = {
    id: tpl.id,
    name: tpl.name || '',
    description: tpl.description || '',
    category: tpl.category || 'professional',
    isPremium: tpl.isPremium || false,
    isActive: tpl.isActive ?? true,
    previewImageUrl: tpl.previewImageUrl || ''
  }
  showModal.value = true
}

async function handleSave() {
  if (!form.value.name) { globalError.value = '模板名称不能为空'; return }
  saving.value = true
  globalError.value = ''
  try {
    const payload = {
      name: form.value.name,
      description: form.value.description,
      category: form.value.category,
      isPremium: form.value.isPremium,
      isActive: form.value.isActive,
      previewImageUrl: form.value.previewImageUrl
    }
    if (editMode.value) {
      payload.id = form.value.id
      await updateAdminTemplate(payload)
    } else {
      await insertAdminTemplate(payload)
    }
    showModal.value = false
    successMsg.value = editMode.value ? '模板已更新' : '模板已创建'
    setTimeout(() => successMsg.value = '', 2000)
    await loadTemplates()
  } catch (e) {
    globalError.value = getErrorMessage(e)
  } finally {
    saving.value = false
  }
}

async function handleDelete(id) {
  try {
    await deleteAdminTemplate(id)
    confirmDelete.value = null
    successMsg.value = '模板已删除'
    setTimeout(() => successMsg.value = '', 2000)
    await loadTemplates()
  } catch (e) {
    globalError.value = getErrorMessage(e)
  }
}

async function handleToggle(tpl) {
  try {
    await toggleAdminTemplate(tpl.id, !tpl.isActive)
    await loadTemplates()
  } catch (e) {
    globalError.value = getErrorMessage(e)
  }
}

onMounted(loadTemplates)
</script>

<template>
  <div class="page-with-navbar">
    <div class="page-container admin-page">
      <div class="page-header">
        <div>
          <h1>模板管理</h1>
          <p class="page-desc">管理简历模板的名称、描述、分类、启用状态和预览图</p>
        </div>
        <button class="btn btn-primary" @click="openAdd">新增模板</button>
      </div>

      <div v-if="globalError" class="form-alert error">{{ globalError }}</div>
      <div v-if="successMsg" class="form-alert success">{{ successMsg }}</div>

      <div v-if="loading" class="loading-overlay"><div class="spinner"></div></div>

      <div v-else class="table-card card">
        <h3>全部模板 ({{ templates.length }})</h3>
        <div class="table-wrapper">
          <table class="data-table">
            <thead>
              <tr>
                <th>ID</th>
                <th>名称</th>
                <th>描述</th>
                <th>分类</th>
                <th>付费</th>
                <th>预览图</th>
                <th>状态</th>
                <th>创建时间</th>
                <th>操作</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="tpl in templates" :key="tpl.id">
                <td>{{ tpl.id }}</td>
                <td><strong>{{ tpl.name }}</strong></td>
                <td class="desc-cell">{{ tpl.description || '-' }}</td>
                <td><span class="category-tag" :class="tpl.category">{{ CATEGORY_LABELS[tpl.category] || tpl.category }}</span></td>
                <td>{{ tpl.isPremium ? '付费' : '免费' }}</td>
                <td>
                  <span v-if="tpl.previewImageUrl" class="img-preview-link">有</span>
                  <span v-else class="img-missing">无</span>
                </td>
                <td>
                  <span class="status-tag" :class="{ published: tpl.isActive }">
                    <span class="dot"></span>
                    {{ tpl.isActive ? '启用' : '禁用' }}
                  </span>
                </td>
                <td>{{ formatDateTime(tpl.createdAt) }}</td>
                <td class="actions-cell">
                  <button class="btn btn-ghost btn-sm" @click="openEdit(tpl)">编辑</button>
                  <button class="btn btn-ghost btn-sm" @click="handleToggle(tpl)">
                    {{ tpl.isActive ? '禁用' : '启用' }}
                  </button>
                  <button class="btn btn-ghost btn-sm danger" @click="confirmDelete = tpl.id">删除</button>
                </td>
              </tr>
              <tr v-if="templates.length === 0">
                <td colspan="9" class="empty-cell">暂无模板</td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>
    </div>

    <!-- Add/Edit Modal -->
    <div v-if="showModal" class="modal-backdrop" @click.self="showModal = false">
      <div class="modal-card card">
        <div class="modal-header">
          <h3>{{ editMode ? '编辑模板' : '新增模板' }}</h3>
          <button class="modal-close-btn" @click="showModal = false">
            <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <path d="M18 6L6 18M6 6l12 12"/>
            </svg>
          </button>
        </div>
        <div class="modal-body">
          <div class="form-group">
            <label class="form-label required">模板名称</label>
            <input v-model="form.name" class="form-input" placeholder="如：简约专业" />
          </div>
          <div class="form-group">
            <label class="form-label">描述</label>
            <textarea v-model="form.description" class="form-textarea" rows="2" placeholder="模板的简要描述"></textarea>
          </div>
          <div class="form-row">
            <div class="form-group">
              <label class="form-label required">分类</label>
              <select v-model="form.category" class="form-select">
                <option v-for="c in CATEGORIES" :key="c.value" :value="c.value">{{ c.label }}</option>
              </select>
            </div>
            <div class="form-group form-check-group">
              <label class="form-label">付费</label>
              <label class="form-checkbox">
                <input type="checkbox" v-model="form.isPremium" />
                <span>{{ form.isPremium ? '付费模板' : '免费模板' }}</span>
              </label>
            </div>
            <div class="form-group form-check-group">
              <label class="form-label">状态</label>
              <label class="form-checkbox">
                <input type="checkbox" v-model="form.isActive" />
                <span>{{ form.isActive ? '启用中' : '已禁用' }}</span>
              </label>
            </div>
          </div>
          <div class="form-group">
            <label class="form-label">预览图</label>
            <div v-if="form.previewImageUrl" class="img-preview">
              <img :src="(form.previewImageUrl && !form.previewImageUrl.startsWith('http')) ? 'http://localhost:8080' + form.previewImageUrl : form.previewImageUrl" style="max-width:200px;max-height:150px;border-radius:6px;margin-bottom:8px" />
              <button type="button" class="btn btn-ghost btn-sm" @click="form.previewImageUrl = ''; selectedFile = null">remove</button>
            </div>
            <input type="file" accept="image/*" @change="handleFileSelect" />
          </div>
        </div>
        <div class="modal-footer">
          <button class="btn btn-secondary" @click="showModal = false">取消</button>
          <button class="btn btn-primary" :disabled="saving" @click="handleSave">
            {{ saving ? '保存中...' : '保存' }}
          </button>
        </div>
      </div>
    </div>

    <!-- Delete Confirm Modal -->
    <div v-if="confirmDelete" class="modal-backdrop" @click.self="confirmDelete = null">
      <div class="modal-card card" style="width: 400px;">
        <div class="modal-header">
          <h3>确认删除</h3>
          <button class="modal-close-btn" @click="confirmDelete = null"><svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M18 6L6 18M6 6l12 12"/></svg></button>
        </div>
        <div class="modal-body">
          <p>确定要删除此模板吗？此操作不可恢复。</p>
        </div>
        <div class="modal-footer">
          <button class="btn btn-secondary" @click="confirmDelete = null">取消</button>
          <button class="btn btn-danger" @click="handleDelete(confirmDelete)">确认删除</button>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.admin-page {
  max-width: 1200px;
}
.page-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 24px;
}
.page-header h1 { font-size: 28px; font-weight: 700; }
.page-desc { color: var(--text-secondary); font-size: 14px; margin-top: 4px; }
.table-card { padding: 24px; }
.table-card h3 { font-size: 16px; margin-bottom: 16px; }
.table-wrapper { overflow-x: auto; }
.data-table { width: 100%; border-collapse: collapse; font-size: 13px; }
.data-table th { text-align: left; padding: 10px 12px; font-weight: 600; color: var(--text-secondary); border-bottom: 1px solid var(--border); white-space: nowrap; }
.data-table td { padding: 10px 12px; border-bottom: 1px solid var(--border); }
.data-table tbody tr:hover { background: #f9fafb; }
.empty-cell { text-align: center; color: var(--text-secondary); padding: 24px !important; }
.desc-cell { max-width: 200px; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; }
.actions-cell { white-space: nowrap; }
.btn-ghost.danger { color: var(--danger); }
.btn-ghost.danger:hover { background: #fef2f2; }
.category-tag { display: inline-block; padding: 2px 8px; border-radius: 4px; font-size: 12px; background: #f3f4f6; color: var(--text-secondary); }
.img-preview-link { color: var(--success); }
.img-missing { color: var(--text-secondary); font-size: 12px; }
.modal-backdrop { position: fixed; inset: 0; background: rgba(0,0,0,0.45); display: flex; align-items: center; justify-content: center; z-index: 1000; }
.modal-card { width: 560px; max-height: 80vh; display: flex; flex-direction: column; }
.modal-header { display: flex; align-items: center; justify-content: space-between; padding: 16px 20px; border-bottom: 1px solid var(--border); }
.modal-header h3 { font-size: 16px; font-weight: 600; }
.modal-close-btn { background: none; border: none; cursor: pointer; color: var(--text-secondary); padding: 4px; border-radius: 4px; }
.modal-close-btn:hover { background: var(--bg); color: var(--text); }
.modal-body { padding: 20px; overflow-y: auto; flex: 1; }
.modal-footer { padding: 12px 20px; border-top: 1px solid var(--border); display: flex; justify-content: flex-end; gap: 8px; }
.form-row { display: flex; gap: 12px; }
.form-row .form-group { flex: 1; }
.form-checkbox { display: flex; align-items: center; gap: 8px; cursor: pointer; }
.form-checkbox input { width: 16px; height: 16px; }
</style>