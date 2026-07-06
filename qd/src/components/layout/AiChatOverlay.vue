<script setup>
import { ref, onMounted, nextTick, watch } from 'vue'
import { chatWithMemory } from '@/api/share'
import { generateConversationId, getErrorMessage } from '@/utils'
import { useUserStore } from '@/stores/user'

const props = defineProps({
  visible: Boolean
})
const emit = defineEmits(['close'])

const userStore = useUserStore()
let lastUserId = null

const conversationId = ref('')
const messages = ref([])
const inputText = ref('')
const sending = ref(false)
const chatBody = ref(null)

function resetConversation() {
  conversationId.value = generateConversationId()
  messages.value = []
  messages.value.push({
    role: 'ai',
    content: '你好！我是你的 AI 简历助手，可以帮你优化简历内容、撰写个人简介、润色工作经历等。有什么我可以帮你的吗？'
  })
  nextTick(() => scrollToBottom())
}

watch(() => userStore.user, (user) => {
  const currentId = user?.id || user?.username || null
  if (currentId !== null && lastUserId !== null && currentId !== lastUserId) {
    resetConversation()
  }
  lastUserId = currentId
}, { deep: true })

watch(() => userStore.isLoggedIn, (loggedIn) => {
  if (!loggedIn) {
    lastUserId = null
    if (conversationId.value) {
      resetConversation()
    }
  }
})

onMounted(() => {
  const user = userStore.user
  lastUserId = user?.id || user?.username || null
  conversationId.value = generateConversationId()
  messages.value.push({
    role: 'ai',
    content: '你好！我是你的 AI 简历助手，可以帮你优化简历内容、撰写个人简介、润色工作经历等。有什么我可以帮你的吗？'
  })
})

watch(() => props.visible, (val) => {
  if (val) {
    nextTick(() => scrollToBottom())
  }
})

async function scrollToBottom() {
  await nextTick()
  if (chatBody.value) {
    chatBody.value.scrollTop = chatBody.value.scrollHeight
  }
}

async function sendMessage() {
  const text = inputText.value.trim()
  if (!text || sending.value) return

  messages.value.push({ role: 'user', content: text })
  inputText.value = ''
  sending.value = true
  await scrollToBottom()

  try {
    const { data } = await chatWithMemory(text, conversationId.value, null, userStore.user?.id)
    if (data.success) {
      messages.value.push({ role: 'ai', content: data.result })
      window.dispatchEvent(new CustomEvent('resume-data-changed'))
    } else {
      messages.value.push({ role: 'ai', content: data.message || '抱歉，出现了错误' })
    }
  } catch (e) {
    messages.value.push({ role: 'ai', content: getErrorMessage(e) })
  } finally {
    sending.value = false
    await scrollToBottom()
  }
}

function handleKeydown(e) {
  if (e.key === 'Enter' && !e.shiftKey) {
    e.preventDefault()
    sendMessage()
  }
}

function close() {
  emit('close')
}

function onBackdropClick(e) {
  if (e.target === e.currentTarget) {
    close()
  }
}
</script>

<template>
  <Transition name="chat-fade">
    <div v-if="visible" class="chat-backdrop" @click="onBackdropClick">
      <div class="chat-overlay" @click.stop>
        <!-- Header -->
        <div class="chat-header">
          <div class="header-left">
            <div class="header-avatar">
              <svg width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                <path d="M12 2a4 4 0 0 0-4 4v1a4 4 0 0 0 8 0V6a4 4 0 0 0-4-4z"/>
                <path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"/>
                <circle cx="12" cy="7" r="4"/>
              </svg>
            </div>
            <div class="header-info">
              <span class="header-title">AI 智能助手</span>
              <span class="header-status">在线</span>
              <span class="header-userid" v-if="userStore.user?.id">ID: {{ userStore.user.id }}</span>
            </div>
          </div>
          <button class="close-btn" @click="close" title="关闭">
            <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
              <line x1="18" y1="6" x2="6" y2="18"/>
              <line x1="6" y1="6" x2="18" y2="18"/>
            </svg>
          </button>
        </div>

        <!-- Messages -->
        <div ref="chatBody" class="chat-body">
          <div
            v-for="(msg, idx) in messages"
            :key="idx"
            class="msg-row"
            :class="msg.role"
          >
            <div v-if="msg.role === 'ai'" class="msg-avatar ai-avatar">
              <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                <path d="M12 2a4 4 0 0 0-4 4v1a4 4 0 0 0 8 0V6a4 4 0 0 0-4-4z"/>
                <path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"/>
                <circle cx="12" cy="7" r="4"/>
              </svg>
            </div>
            <div class="msg-bubble" :class="msg.role">{{ msg.content }}</div>
            <div v-if="msg.role === 'user'" class="msg-avatar user-avatar">我</div>
          </div>

          <!-- Typing indicator -->
          <div v-if="sending" class="msg-row ai">
            <div class="msg-avatar ai-avatar">
              <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                <path d="M12 2a4 4 0 0 0-4 4v1a4 4 0 0 0 8 0V6a4 4 0 0 0-4-4z"/>
                <path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"/>
                <circle cx="12" cy="7" r="4"/>
              </svg>
            </div>
            <div class="msg-bubble ai typing-indicator">
              <span></span><span></span><span></span>
            </div>
          </div>
        </div>

        <!-- Input -->
        <div class="chat-footer">
          <textarea
            v-model="inputText"
            class="chat-input"
            placeholder="输入消息，Enter 发送..."
            rows="1"
            @keydown="handleKeydown"
          />
          <button
            class="send-btn"
            :disabled="sending || !inputText.trim()"
            @click="sendMessage"
          >
            <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
              <line x1="22" y1="2" x2="11" y2="13"/>
              <polygon points="22 2 15 22 11 13 2 9 22 2"/>
            </svg>
          </button>
        </div>
      </div>
    </div>
  </Transition>
</template>

<style scoped>
.chat-backdrop {
  position: fixed;
  inset: 0;
  background: rgba(0, 0, 0, 0.3);
  z-index: 10000;
  display: flex;
  align-items: flex-end;
  justify-content: flex-end;
  padding: 20px;
}

.chat-overlay {
  width: 400px;
  height: 580px;
  background: #f5f5f5;
  border-radius: 12px;
  display: flex;
  flex-direction: column;
  overflow: hidden;
  box-shadow: 0 8px 40px rgba(0, 0, 0, 0.2);
  animation: slideUp 0.25s ease-out;
}

@keyframes slideUp {
  from { opacity: 0; transform: translateY(30px) scale(0.95); }
  to { opacity: 1; transform: translateY(0) scale(1); }
}

/* Header */
.chat-header {
  background: #2b2b2b;
  padding: 14px 18px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  flex-shrink: 0;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 10px;
}

.header-avatar {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  background: #07c160;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
}

.header-info {
  display: flex;
  flex-direction: column;
}

.header-title {
  font-size: 15px;
  font-weight: 600;
  color: #fff;
}

.header-userid {
  font-size: 11px;
  color: rgba(255, 255, 255, 0.5);
  font-family: monospace;
}

.header-status {
  font-size: 11px;
  color: rgba(255, 255, 255, 0.6);
}

.close-btn {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  border: none;
  background: transparent;
  color: rgba(255, 255, 255, 0.7);
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: background 0.2s;
}

.close-btn:hover {
  background: rgba(255, 255, 255, 0.15);
  color: #fff;
}

/* Chat body */
.chat-body {
  flex: 1;
  overflow-y: auto;
  padding: 16px 14px;
  display: flex;
  flex-direction: column;
  gap: 12px;
  background: #ededed;
}

.msg-row {
  display: flex;
  align-items: flex-start;
  gap: 8px;
  max-width: 100%;
}

.msg-row.user {
  flex-direction: row;
  justify-content: flex-end;
  align-self: flex-end;
}

.msg-avatar {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 13px;
  font-weight: 600;
  flex-shrink: 0;
}

.ai-avatar {
  background: #07c160;
  color: #fff;
}

.user-avatar {
  background: #95ec69;
  color: #1a1a1a;
}

.msg-bubble {
  padding: 10px 14px;
  border-radius: 8px;
  font-size: 14px;
  line-height: 1.6;
  white-space: pre-wrap;
  word-break: break-word;
  max-width: 70%;
  position: relative;
}

.msg-bubble.ai {
  background: #fff;
  color: #1a1a1a;
  border-top-left-radius: 2px;
}

.msg-bubble.user {
  background: #95ec69;
  color: #1a1a1a;
  border-top-right-radius: 2px;
}

/* Typing indicator */
.typing-indicator {
  display: flex;
  align-items: center;
  gap: 4px;
  padding: 14px 18px !important;
}

.typing-indicator span {
  width: 6px;
  height: 6px;
  border-radius: 50%;
  background: #888;
  animation: bounce 1.4s infinite;
}

.typing-indicator span:nth-child(2) { animation-delay: 0.2s; }
.typing-indicator span:nth-child(3) { animation-delay: 0.4s; }

@keyframes bounce {
  0%, 80%, 100% { transform: translateY(0); }
  40% { transform: translateY(-6px); }
}

/* Footer */
.chat-footer {
  padding: 10px 12px;
  background: #fff;
  border-top: 1px solid #e0e0e0;
  display: flex;
  gap: 8px;
  align-items: flex-end;
  flex-shrink: 0;
}

.chat-input {
  flex: 1;
  border: none;
  border-radius: 6px;
  padding: 10px 12px;
  resize: none;
  outline: none;
  font-size: 14px;
  line-height: 1.5;
  background: #f5f5f5;
  max-height: 80px;
}

.chat-input:focus {
  background: #ebebeb;
}

.chat-input::placeholder {
  color: #aaa;
}

.send-btn {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  border: none;
  background: #07c160;
  color: #fff;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
  transition: background 0.2s;
}

.send-btn:hover:not(:disabled) {
  background: #06ad56;
}

.send-btn:disabled {
  background: #ccc;
  cursor: not-allowed;
}

/* Transition */
.chat-fade-enter-active,
.chat-fade-leave-active {
  transition: opacity 0.2s ease;
}

.chat-fade-enter-from,
.chat-fade-leave-to {
  opacity: 0;
}
</style>







