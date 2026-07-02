<script setup>
import { ref, onMounted, onUnmounted, nextTick, watch } from 'vue'
import Sortable from 'sortablejs'

const props = defineProps({
  items: { type: Array, default: () => [] },
  sortable: { type: Boolean, default: true },
  itemKey: { type: String, default: 'id' }
})

const emit = defineEmits(['sort', 'update:items'])

const listRef = ref(null)
let sortableInstance = null

function initSortable() {
  if (!listRef.value || !props.sortable) return
  sortableInstance?.destroy()
  sortableInstance = Sortable.create(listRef.value, {
    handle: '.drag-handle',
    animation: 150,
    onEnd() {
      const ids = [...listRef.value.children].map((el) => Number(el.dataset.id))
      emit('sort', ids)
    }
  })
}

watch(() => props.items.length, () => nextTick(initSortable))

onMounted(() => nextTick(initSortable))
onUnmounted(() => sortableInstance?.destroy())
</script>

<template>
  <div ref="listRef" class="sortable-list">
    <div
      v-for="item in items"
      :key="item[itemKey]"
      class="sortable-item"
      :data-id="item[itemKey]"
    >
      <slot :item="item" />
    </div>
  </div>
</template>

<style scoped>
.sortable-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}
</style>
