<script setup>
defineProps({
  title: { type: String, default: '暂无数据' },
  description: { type: String, default: '还没有相关内容' }
})
</script>

<template>
  <div class="empty-state">
    <svg viewBox="0 0 120 120" fill="none">
      <rect x="20" y="15" width="80" height="90" rx="8" stroke="#d1d5db" stroke-width="2" fill="#f9fafb"/>
      <line x1="35" y1="35" x2="85" y2="35" stroke="#e5e7eb" stroke-width="2"/>
      <line x1="35" y1="50" x2="75" y2="50" stroke="#e5e7eb" stroke-width="2"/>
      <line x1="35" y1="65" x2="80" y2="65" stroke="#e5e7eb" stroke-width="2"/>
    </svg>
    <h3>{{ title }}</h3>
    <p>{{ description }}</p>
    <slot />
  </div>
</template>
