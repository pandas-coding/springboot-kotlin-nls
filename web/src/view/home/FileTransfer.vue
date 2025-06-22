<script setup lang="ts">
import { useTemplateRef } from 'vue'
import FileTransferUpload from "@/view/home/FileTransferUpload.vue";

const fileTransferUploadModalRef = useTemplateRef<InstanceType<typeof FileTransferUpload>>('file-transfer-upload-modal')


const showModal = () => {
  fileTransferUploadModalRef.value?.showModal()
}
</script>

<template>
  <file-transfer-upload ref="file-transfer-upload-modal"></file-transfer-upload>

  <a-alert :message="null" type="info">
    <template #description>
      <h6>1. 上传音频，生成srt字幕文件、txt文本文件，支持多国语言</h6>
      <h6>2. <b>用户上传的音频将在10天后系统自动删除</b></h6>
    </template>
  </a-alert>
  <br/>
  <p>
    <a-space>
      <a-button
          type="primary"
          @click="showModal"
      >开始上传音频</a-button>
      <a-button type="default">刷新列表</a-button>
    </a-space>
  </p>

</template>

<style scoped>

</style>