<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()
const pos = ref({ x: 0, y: 0 })
const dragging = ref(false)
const moved = ref(false)
const offset = ref({ x: 0, y: 0 })

onMounted(() => {
  const saved = localStorage.getItem('aiButtonPos')
  if (saved) {
    pos.value = JSON.parse(saved)
  } else {
    pos.value = { x: window.innerWidth - 80, y: window.innerHeight - 120 }
  }
})

function onMouseDown(e) {
  dragging.value = true
  moved.value = false
  offset.value = { x: e.clientX - pos.value.x, y: e.clientY - pos.value.y }
  document.addEventListener('mousemove', onMouseMove)
  document.addEventListener('mouseup', onMouseUp)
}

function onMouseMove(e) {
  if (!dragging.value) return
  moved.value = true
  pos.value = {
    x: Math.max(0, Math.min(window.innerWidth - 56, e.clientX - offset.value.x)),
    y: Math.max(0, Math.min(window.innerHeight - 56, e.clientY - offset.value.y))
  }
}

function onMouseUp() {
  dragging.value = false
  localStorage.setItem('aiButtonPos', JSON.stringify(pos.value))
  document.removeEventListener('mousemove', onMouseMove)
  document.removeEventListener('mouseup', onMouseUp)
}

function goChat() {
  if (moved.value) return
  router.push('/chat')
}
</script>

<template>
  <div
    class="ai-float-btn"
    :style="{ left: pos.x + 'px', top: pos.y + 'px' }"
    @mousedown="onMouseDown"
    @click="goChat"
    title="AI 助手"
  >
    <img src="/ai-assistant.svg" alt="AI" draggable="false" />
  </div>
</template>

<style scoped>
.ai-float-btn {
  position: fixed;
  width: 56px;
  height: 56px;
  border-radius: 50%;
  cursor: grab;
  z-index: 999;
  box-shadow: 0 4px 16px rgba(37, 99, 235, 0.35);
  transition: transform 0.2s, box-shadow 0.2s;
  user-select: none;
}

.ai-float-btn:hover {
  transform: scale(1.08);
  box-shadow: 0 6px 24px rgba(37, 99, 235, 0.45);
}

.ai-float-btn:active {
  cursor: grabbing;
}

.ai-float-btn img {
  width: 100%;
  height: 100%;
  border-radius: 50%;
}
</style>
