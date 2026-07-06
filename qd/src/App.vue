<script setup>
import { computed, ref, watch } from 'vue'
import { useRoute } from 'vue-router'
import { useUserStore } from '@/stores/user'
import AppNavbar from '@/components/layout/AppNavbar.vue'
import AiFloatingButton from '@/components/layout/AiFloatingButton.vue'
import AiChatOverlay from '@/components/layout/AiChatOverlay.vue'

const route = useRoute()
const userStore = useUserStore()
const showChat = ref(false)

const showNavbar = computed(() => {
  return route.meta.requiresAuth && route.name !== 'Chat'
})

const showAiButton = computed(() => {
  return route.meta.requiresAuth
})

const isChatPage = computed(() => route.name === 'Chat')

function openChat() {
  showChat.value = true
}

function closeChat() {
  showChat.value = false
}

// 用户登出时自动关闭聊天面板
watch(() => userStore.isLoggedIn, (loggedIn) => {
  if (!loggedIn) {
    showChat.value = false
  }
})
</script>

<template>
  <div id="app-root">
    <AppNavbar v-if="showNavbar" />
    <router-view />
    <AiFloatingButton v-if="showAiButton && !isChatPage" @open-chat="openChat" />
    <AiChatOverlay :visible="showChat" @close="closeChat" />
  </div>
</template>

<style scoped>
#app-root {
  min-height: 100%;
}
</style>
