<script setup lang="ts">
import { nextTick, onMounted, ref, useTemplateRef } from 'vue'
import {
  useColumns,
  useFileTransferQuery,
} from '@/view/file-transfer/useFileTransferQuery.tsx'
import type { TablePaginationConfig } from 'ant-design-vue'
import FileTransferSubtitle from '@/view/file-transfer/FileTransferSubtitle.vue'

const fileTransferSubtitleModalRef = useTemplateRef<InstanceType<typeof FileTransferSubtitle>>('file-transfer-subtitle-modal')
const clickFileTransferId = ref('')
const fileTransferName = ref('')
const {columns} = useColumns({
  onClickColumnOperation: async (record) => {
    clickFileTransferId.value = record.id
    fileTransferName.value = record.name
    await nextTick()
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