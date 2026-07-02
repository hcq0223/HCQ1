<script setup>
import { computed } from 'vue'
import { useRoute } from 'vue-router'
import AppNavbar from '@/components/layout/AppNavbar.vue'
import AiFloatingButton from '@/components/layout/AiFloatingButton.vue'

const route = useRoute()

const showNavbar = computed(() => {
  return route.meta.requiresAuth && route.name !== 'Chat'
})

const showAiButton = computed(() => {
  return route.meta.requiresAuth
})

const isChatPage = computed(() => route.name === 'Chat')
</script>

<template>
  <div id="app-root">
    <AppNavbar v-if="showNavbar" />
    <router-view />
    <AiFloatingButton v-if="showAiButton && !isChatPage" />
  </div>
</template>

<style scoped>
#app-root {
  min-height: 100%;
}
</style>
