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
      messages.value.push({ role: 'ai', content: data.result })
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
</script>

<template>
  <div class="chat-page">
    <div class="chat-header">
      <button class="btn btn-ghost btn-sm" @click="router.back()">← 返回</button>
      <h1>AI 简历助手</h1>
      <span class="session-id">会话 ID: {{ conversationId.slice(-8) }}</span>
    </div>

    <div ref="chatContainer" class="chat-messages">
      <div
        v-for="(msg, idx) in messages"
        :key="idx"
        class="message"
        :class="msg.role"
      >
        <div class="message-avatar">
          {{ msg.role === 'user' ? '我' : 'AI' }}
        </div>
        <div class="message-bubble">{{ msg.content }}</div>
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

.send-btn {
  height: 44px;
  min-width: 80px;
}
</style>
