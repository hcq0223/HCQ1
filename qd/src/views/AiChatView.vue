<script setup>
import { ref, onMounted, nextTick } from 'vue'
import { useRouter } from 'vue-router'
import { chatWithMemory } from '@/api/share'
import { generateConversationId, getErrorMessage } from '@/utils'

const router = useRouter()

const conversationId = ref('')
const messages = ref([])
const inputText = ref('')
const sending = ref(false)
const showScrollBtn = ref(false)
const chatContainer = ref(null)

onMounted(() => {
  conversationId.value = generateConversationId()
  messages.value.push({
    role: 'ai',
    content: '你好！我是你的 AI 简历助手，可以帮你优化简历内容、撰写个人简介、润色工作经历等。有什么我可以帮你的吗？'
  })
})

async function scrollToBottom() {
  await nextTick()
  if (chatContainer.value) {
    chatContainer.value.scrollTop = chatContainer.value.scrollHeight
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
    const { data } = await chatWithMemory(text, conversationId.value)
    if (data.success) {
      messages.value.push({ role: 'ai', content: data.result, steps: data.toolSteps || [], showSteps: false })
    } else {
      messages.value.push({ role: 'ai', content: data.message || '抱歉，出现了错误', steps: [] })
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
</script>

<template>
  <div class="chat-page">
    <div class="chat-header">
      <button class="btn btn-ghost btn-sm" @click="router.back()">← 返回</button>
      <h1>AI 简历助手</h1>
      <span class="session-id">会话 ID: {{ conversationId.slice(-8) }}</span>
    </div>

    <div ref="chatContainer" class="chat-messages" @scroll="handleChatScroll">
      <div
        v-for="(msg, idx) in messages"
        :key="idx"
        class="message"
        :class="msg.role"
      >
        <div class="message-avatar">
          {{ msg.role === 'user' ? '我' : 'AI' }}
        </div>
        <div class="msg-body">
            <div v-if="msg.steps && msg.steps.length" class="msg-steps-toggle">
          <button class="steps-toggle-btn" @click.stop="msg.showSteps = !msg.showSteps">
            <span class="steps-toggle-icon">
              <svg v-if="msg.steps.every(s => s.status === 'completed')" width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="#22c55e" stroke-width="2.5"><polyline points="20 6 9 17 4 12"/></svg>
              <span v-else-if="msg.steps.some(s => s.status === 'failed')" style="color:#ef4444;font-size:12px;font-weight:bold">&#33;</span>
              <svg v-else class="step-spin" width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="#888" stroke-width="2.5"><circle cx="12" cy="12" r="10" stroke-dasharray="31.4 31.4" stroke-linecap="round"/></svg>
            </span>
            <span class="steps-toggle-label">执行 {{ msg.steps.length }} 个操作</span>
            <svg class="steps-toggle-arrow" :class="{ open: msg.showSteps }" width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><polyline points="6 9 12 15 18 9"/></svg>
          </button>
          <div v-if="msg.showSteps" class="msg-steps">
            <div v-for="(step, si) in msg.steps" :key="si" class="msg-step" :class="step.status">
              <span class="step-icon">
                <svg v-if="step.status === 'completed'" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="#22c55e" stroke-width="2.5"><polyline points="20 6 9 17 4 12"/></svg>
                <span v-else-if="step.status === 'failed'" class="step-fail">&#10007;</span>
                <svg v-else class="step-spin" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="#888" stroke-width="2.5"><circle cx="12" cy="12" r="10" stroke-dasharray="31.4 31.4" stroke-linecap="round"/></svg>
              </span>
              <span class="step-body">
                <span class="step-label">{{ step.description || step.toolName }}</span>
                <span v-if="step.status === 'failed'" class="step-err">{{ step.result }}</span>
              </span>
            </div>
          </div>
        </div>
        <div class="message-bubble">{{ msg.content }}</div>
        </div>
      </div>
      <div v-if="sending" class="message ai">
        <div class="message-avatar">AI</div>
        <div class="message-bubble typing">
          <span></span><span></span><span></span>
        </div>
      </div>
    </div>

    <div class="chat-input-area">
      <textarea
        v-model="inputText"
        class="chat-input"
        placeholder="输入你的问题，Enter 发送，Shift+Enter 换行..."
        rows="2"
        @keydown="handleKeydown"
      />
      <button class="btn btn-primary send-btn" :disabled="sending || !inputText.trim()" @click="sendMessage">
        发送
      </button>
    </div>
  </div>
</template>

<style scoped>
.chat-page {
  height: 100vh;
  display: flex;
  flex-direction: column;
  background: var(--bg);
}

.chat-header {
  height: 56px;
  background: #fff;
  border-bottom: 1px solid var(--border);
  display: flex;
  align-items: center;
  padding: 0 20px;
  gap: 16px;
  flex-shrink: 0;
}

.chat-header h1 {
  font-size: 16px;
  flex: 1;
}

.session-id {
  font-size: 12px;
  color: var(--text-secondary);
  font-family: monospace;
}

.chat-messages {
  flex: 1;
  overflow-y: auto;
  padding: 20px;
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.message {
  display: flex;
  gap: 10px;
  max-width: 75%;
}

.message.user {
  align-self: flex-end;
  flex-direction: row-reverse;
  gap: 4px;
}
.message.user .msg-body {
  display: flex;
  flex-direction: column;
  min-width: 120px;
  flex: 1;
  max-width: 100%;
}

.message-avatar {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 12px;
  font-weight: 600;
  flex-shrink: 0;
}

.message.ai .message-avatar {
  background: var(--primary-light);
  color: var(--primary);
}

.message.user .message-avatar {
  background: var(--primary);
  color: #fff;
}

.message-bubble {
  padding: 10px 14px;
  border-radius: 12px;
  font-size: 14px;
  line-height: 1.6;
  white-space: pre-wrap;
  word-break: break-word;
  max-width: 100%;
}

.message.ai .message-bubble {
  background: #fff;
  border: 1px solid var(--border);
  border-top-left-radius: 4px;
}

.message.user .message-bubble {
  background: var(--primary);
  color: #fff;
  border-top-right-radius: 4px;
}

.typing {
  display: flex;
  gap: 4px;
  padding: 14px 18px !important;
}

.typing span {
  width: 6px;
  height: 6px;
  border-radius: 50%;
  background: var(--text-secondary);
  animation: bounce 1.4s infinite;
}

.typing span:nth-child(2) { animation-delay: 0.2s; }
.typing span:nth-child(3) { animation-delay: 0.4s; }

@keyframes bounce {
  0%, 80%, 100% { transform: translateY(0); }
  40% { transform: translateY(-6px); }
}

.chat-input-area {
  padding: 16px 20px;
  background: #fff;
  border-top: 1px solid var(--border);
  display: flex;
  gap: 12px;
  align-items: flex-end;
}

.chat-input {
  flex: 1;
  border: 1px solid var(--border);
  border-radius: 8px;
  padding: 10px 14px;
  resize: none;
  outline: none;
  font-size: 14px;
  line-height: 1.5;
}

.chat-input:focus {
  border-color: var(--primary);
  box-shadow: 0 0 0 3px rgba(37, 99, 235, 0.15);
}


/* Scroll to bottom button */
.scroll-down-btn {
  position: absolute;
  bottom: 70px;
  left: 50%;
  transform: translateX(-50%);
  width: 36px;
  height: 36px;
  border-radius: 50%;
  background: #fff;
  border: none;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.15);
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #666;
  z-index: 10;
  transition: background 0.2s, box-shadow 0.2s;
}
.scroll-down-btn:hover {
  background: #f5f5f5;
  box-shadow: 0 4px 14px rgba(0, 0, 0, 0.2);
  color: var(--primary);
}
.scroll-btn-enter-active,
.scroll-btn-leave-active {
  transition: opacity 0.2s, transform 0.2s;
}
.scroll-btn-enter-from,
.scroll-btn-leave-to {
  opacity: 0;
  transform: translateX(-50%) translateY(8px);
}


.send-btn {
  height: 44px;
  min-width: 80px;
}



/* Message body wrapper (steps + bubble) */
.msg-body {
  display: flex;
  flex-direction: column;
  min-width: 120px;
  flex: 1;
  max-width: 100%;
}

/* Steps Toggle */
.msg-steps-toggle {
  margin-bottom: 8px;
}

.steps-toggle-btn {
  display: flex;
  align-items: center;
  gap: 6px;
  width: 100%;
  border: none;
  background: transparent;
  cursor: pointer;
  padding: 4px 0;
  color: #666;
  font-size: 13px;
  transition: color 0.2s;
}

.steps-toggle-btn:hover {
  color: #333;
}

.steps-toggle-icon {
  flex-shrink: 0;
  width: 18px;
  height: 18px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.steps-toggle-label {
  flex: 1;
  text-align: left;
}

.steps-toggle-arrow {
  flex-shrink: 0;
  transition: transform 0.2s;
}

.steps-toggle-arrow.open {
  transform: rotate(180deg);
}
/* Agent Steps */
.msg-steps {
  display: flex;
  flex-direction: column;
  gap: 6px;
  margin-bottom: 8px;
  max-width: 100%;
}

.msg-step {
  display: flex;
  align-items: flex-start;
  gap: 8px;
  animation: stepIn 0.3s ease both;
}

@keyframes stepIn {
  from { opacity: 0; transform: translateY(6px); }
  to   { opacity: 1; transform: translateY(0); }
}

.msg-step:nth-child(1) { animation-delay: 0s; }
.msg-step:nth-child(2) { animation-delay: 0.12s; }
.msg-step:nth-child(3) { animation-delay: 0.24s; }
.msg-step:nth-child(4) { animation-delay: 0.36s; }
.msg-step:nth-child(5) { animation-delay: 0.48s; }
.msg-step:nth-child(6) { animation-delay: 0.6s; }

.step-icon {
  flex-shrink: 0;
  width: 20px;
  height: 20px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-top: 1px;
}

.step-fail {
  color: #ef4444;
  font-size: 14px;
  font-weight: bold;
}

@keyframes spin {
  to { transform: rotate(360deg); }
}

.step-spin {
  animation: spin 1s linear infinite;
}

.step-body {
  display: flex;
  flex-direction: column;
  min-width: 0;
  font-size: 13px;
}

.step-label {
  color: #555;
  line-height: 1.4;
}

.step-err {
  color: #ef4444;
  font-size: 12px;
  margin-top: 2px;
}
</style>