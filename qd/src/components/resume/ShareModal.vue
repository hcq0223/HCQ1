<script setup>
import { ref, watch } from 'vue'
import AppModal from '@/components/common/AppModal.vue'
import {
  getSharesByResumeId,
  createShare,
  disableShare,
  enableShare,
  updateExpiresAt,
  deleteShare
} from '@/api/share'
import { formatDateTime, getShareLink, getErrorMessage } from '@/utils'

const props = defineProps({ show: Boolean, resumeId: Number })
const emit = defineEmits(['close'])

const shares = ref([])
const loading = ref(false)
const globalError = ref('')
const currentShare = ref(null)
const expireOption = ref('forever')
const creating = ref(false)

const expireOptions = [
  { value: 'forever', label: '永久有效', days: null },
  { value: '7', label: '7 天', days: 7 },
  { value: '30', label: '30 天', days: 30 },
  { value: '90', label: '90 天', days: 90 }
]

async function loadShares() {
  if (!props.resumeId) return
  loading.value = true
  try {
    const { data } = await getSharesByResumeId(props.resumeId)
    shares.value = data.shares || []
    if (shares.value.length > 0) {
      currentShare.value = shares.value[0]
    } else {
      currentShare.value = null
    }
  } catch (e) {
    globalError.value = getErrorMessage(e)
  } finally { loading.value = false }
}

function calcExpiresAt() {
  const opt = expireOptions.find((o) => o.value === expireOption.value)
  if (!opt?.days) return null
  const d = new Date()
  d.setDate(d.getDate() + opt.days)
  const pad = (n) => String(n).padStart(2, '0')
  return `${d.getFullYear()}-${pad(d.getMonth() + 1)}-${pad(d.getDate())} ${pad(d.getHours())}:${pad(d.getMinutes())}:${pad(d.getSeconds())}`
}

async function handleCreate() {
  creating.value = true
  try {
    const expiresAt = calcExpiresAt()
    const { data } = await createShare(props.resumeId, expiresAt)
    if (data.share) {
      currentShare.value = data.share
      await loadShares()
    }
  } catch (e) {
    globalError.value = getErrorMessage(e)
  } finally { creating.value = false }
}

async function copyLink(token) {
  try {
    await navigator.clipboard.writeText(getShareLink(token))
    alert('链接已复制到剪贴板')
  } catch { alert('复制失败，请手动复制') }
}

async function toggleShare(share) {
  try {
    if (share.isActive) { await disableShare(share.id, props.resumeId) }
    else { await enableShare(share.id, props.resumeId) }
    await loadShares()
  } catch (e) { globalError.value = getErrorMessage(e) }
}

async function handleDeleteShare(share) {
  if (!confirm('确定删除此分享链接？')) return
  try {
    await deleteShare(share.id, props.resumeId)
    await loadShares()
  } catch (e) { globalError.value = getErrorMessage(e) }
}

async function handleUpdateExpire(share) {
  try {
    const expiresAt = calcExpiresAt()
    await updateExpiresAt(share.id, props.resumeId, expiresAt)
    await loadShares()
  } catch (e) { globalError.value = getErrorMessage(e) }
}

watch(() => props.show, (val) => { if (val) { globalError.value = ''; loadShares() } })
</script>

<template>
  <AppModal :show="show" title="分享管理" width="700px" @close="emit('close')">
    <div v-if="globalError" class="form-alert error">{{ globalError }}</div>
    <div v-if="loading" class="loading-overlay"><div class="spinner"></div></div>
    <template v-else>
      <div class="share-section">
        <div class="share-status">
          <span class="status-dot" :class="{ active: currentShare?.isActive }"></span>
          {{ currentShare?.isActive ? '分享已启用' : '暂无有效分享' }}
        </div>

        <div v-if="currentShare" class="share-link-row">
          <input :value="getShareLink(currentShare.shareToken)" class="form-input" readonly />
          <button class="btn btn-primary btn-sm" @click="copyLink(currentShare.shareToken)">复制</button>
        </div>
        <p v-else class="no-share-hint">暂无分享链接，点击下方按钮创建</p>

        <div class="expire-options">
          <label v-for="opt in expireOptions" :key="opt.value" class="expire-option">
            <input v-model="expireOption" type="radio" :value="opt.value" /> {{ opt.label }}
          </label>
        </div>

        <div class="share-actions">
          <button class="btn btn-primary btn-sm" :disabled="creating" @click="handleCreate">
            {{ creating ? '创建中...' : '创建新链接' }}
          </button>
          <button v-if="currentShare" class="btn btn-secondary btn-sm" @click="handleUpdateExpire(currentShare)">
            更新有效期
          </button>
        </div>

        <div v-if="currentShare" class="view-count">
          浏览次数：{{ currentShare.viewCount || 0 }}
        </div>
      </div>

      <h4 class="history-title">分享历史</h4>
      <table class="share-table">
        <thead>
          <tr><th>令牌</th><th>创建时间</th><th>状态</th><th>浏览次数</th><th>操作</th></tr>
        </thead>
        <tbody>
          <tr v-for="share in shares" :key="share.id">
            <td class="token">{{ share.shareToken?.substring(0, 8) }}...</td>
            <td>{{ formatDateTime(share.createdAt) }}</td>
            <td><span :class="share.isActive ? 'tag-active' : 'tag-inactive'">{{ share.isActive ? '有效' : '已停用' }}</span></td>
            <td>{{ share.viewCount || 0 }}</td>
            <td class="actions">
              <button class="btn btn-ghost btn-sm" @click="copyLink(share.shareToken)">复制</button>
              <button class="btn btn-ghost btn-sm" @click="toggleShare(share)">{{ share.isActive ? '停用' : '启用' }}</button>
              <button class="btn btn-ghost btn-sm danger-text" @click="handleDeleteShare(share)">删除</button>
            </td>
          </tr>
          <tr v-if="shares.length === 0">
            <td colspan="5" class="empty-row">暂无分享记录</td>
          </tr>
        </tbody>
      </table>
    </template>
  </AppModal>
</template>

<style scoped>
.share-section { margin-bottom: 24px; }
.share-status { display: flex; align-items: center; gap: 8px; margin-bottom: 16px; font-weight: 500; }
.status-dot { width: 8px; height: 8px; border-radius: 50%; background: #9ca3af; }
.status-dot.active { background: var(--success); }
.share-link-row { display: flex; gap: 8px; margin-bottom: 16px; }
.no-share-hint { color: var(--text-secondary); font-size: 13px; margin-bottom: 16px; }
.expire-options { display: flex; gap: 16px; margin-bottom: 16px; }
.expire-option { display: flex; align-items: center; gap: 4px; cursor: pointer; font-size: 13px; }
.share-actions { display: flex; gap: 8px; margin-bottom: 12px; }
.view-count { font-size: 13px; color: var(--text-secondary); }
.history-title { font-size: 14px; margin-bottom: 12px; color: var(--text-secondary); }
.share-table { width: 100%; border-collapse: collapse; font-size: 13px; }
.share-table th, .share-table td { padding: 8px 12px; text-align: left; border-bottom: 1px solid var(--border); }
.share-table th { background: var(--bg); font-weight: 500; color: var(--text-secondary); }
.token { font-family: monospace; }
.tag-active { color: var(--success); }
.tag-inactive { color: var(--text-secondary); }
.actions { display: flex; gap: 4px; }
.danger-text { color: var(--danger) !important; }
.empty-row { text-align: center; color: var(--text-secondary); padding: 20px !important; }
</style>