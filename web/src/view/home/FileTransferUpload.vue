<script setup lang="ts">
import { ref } from "vue";

const open = ref(false)

const showModal = () => {
  open.value = true
}

defineExpose({
  showModal,
})
</script>

<template>
  <a-modal v-model:open="open" title="">

  </a-modal>

</template>

<style scoped>

</style>