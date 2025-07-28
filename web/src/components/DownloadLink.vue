<script setup lang="ts">

import { ref, useTemplateRef } from 'vue'
import { useAxios } from '@vueuse/integrations/useAxios'

const props = defineProps<{
  downloadUrl: string,
}>()

const downloadUrl = ref(props.downloadUrl)

const downloadAnchorRef = useTemplateRef('downloadAnchorRef')

const download = () => {
  if (!downloadAnchorRef.value) return
  console.log("调用下载组件下载")

  downloadUrl.value = props.downloadUrl
  const url = props.downloadUrl
  downloadAnchorRef.value.download = url.substring(url.lastIndexOf('/') + 1)
  setTimeout(() => downloadAnchorRef.value?.click(), 50)
}

const {
  execute,
} = useAxios<string>({ responseType: 'blob', })

const downloadItem = async (url: string, name: null | string = null) => {
  if (!downloadAnchorRef.value) return
  console.log("调用下载组件下载")

  const resp = await execute(url)
  const blob = new Blob([resp.data])
  downloadUrl.value = URL.createObjectURL(blob)

  if (name == null) {
    downloadAnchorRef.value.download = url.substring(url.lastIndexOf('/') + 1)
  } else {
    downloadAnchorRef.value.download = name
  }

  setTimeout(() => downloadAnchorRef.value?.click(), 50)
}

defineExpose({
  download,
  downloadItem,
})
</script>

<template>
  <a
    ref="downloadAnchorRef"
    target="_blank"
    :href="downloadUrl"
    download
    style="display: none"
  />
</template>

<style scoped>

</style>