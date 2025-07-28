<script setup lang="ts">
import { onMounted, ref, useTemplateRef } from 'vue'
import FileTransferUpload from "@/view/file-transfer/FileTransferUpload.vue";
import {
  useColumns,
  useFileTransferQuery,
} from '@/view/file-transfer/useFileTransferQuery.tsx'
import type { TablePaginationConfig } from 'ant-design-vue'
import FileTransferSubtitle from '@/view/file-transfer/FileTransferSubtitle.vue'

const fileTransferUploadModalRef = useTemplateRef<InstanceType<typeof FileTransferUpload>>('file-transfer-upload-modal')
const fileTransferSubtitleModalRef = useTemplateRef<InstanceType<typeof FileTransferSubtitle>>('file-transfer-subtitle-modal')
const clickFileTransferId = ref('')
const fileTransferName = ref('')

const showModal = () => {
  fileTransferUploadModalRef.value?.showModal()
}

const {columns} = useColumns({
  onClickColumnOperation: (record) => {
    debugger
    clickFileTransferId.value = record.id
    fileTransferName.value = record.name
    fileTransferSubtitleModalRef.value?.showModal()
  }
})

const {
  fileTransferList,
  pagination,
  loading,
  queryFileTransferList,
} = useFileTransferQuery()

const handleTableChange = (pagination: TablePaginationConfig) => {
  queryFileTransferList({
    page: pagination.current ?? 1,
    size: pagination.pageSize ?? 10,
  })
}


onMounted(() => {
  queryFileTransferList()
})
</script>

<template>
  <file-transfer-upload
    ref="file-transfer-upload-modal"
  />
  <file-transfer-subtitle
    :file-transfer-id="clickFileTransferId"
    :name="fileTransferName"
    ref="file-transfer-subtitle-modal"
  />

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
      <a-button
        type="default"
        @click="queryFileTransferList()"
      >刷新列表</a-button>
    </a-space>
  </p>

  <a-table
    :data-source="fileTransferList"
    :columns="columns"
    :pagination="pagination"
    :loading="loading"
    @change="handleTableChange"
  >

  </a-table>

</template>

<style scoped>

</style>