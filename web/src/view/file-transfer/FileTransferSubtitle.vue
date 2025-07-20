<script setup lang="ts">
import { FileTextOutlined } from '@ant-design/icons-vue'
import type { FileTransferSubtitleProps } from '@/view/file-transfer/file-transfer-subtitle.ts'
import { useFileTransferSubtitleTable } from '@/view/file-transfer/useFileTransferSubtitleTable.ts'
import { useConfirmDialog } from '@vueuse/core';

const props = defineProps<FileTransferSubtitleProps>()

const {
  isRevealed,
  reveal,
  confirm,
  onReveal,
} = useConfirmDialog()
onReveal(() => {
  querySubtitleList(props.id)
})

const {
  subtitleList,
  columns,
  pagination,
  loading,
  querySubtitleList,
  handleTableChange,
} = useFileTransferSubtitleTable({fileTransferId: props.id})

defineExpose({
  showModal: reveal,
})

</script>

<template>
  <a-modal
    v-model:open(v-model)="isRevealed"
    title=""
    footer=""
    style="width: 800px; top: 20px"
    @ok="confirm"
  >
    <p>
      <a-space>
        <a-button type="primary" @click="genSubtitle">
          <span>
            <FileTextOutlined/>
            下载字幕
          </span>
        </a-button>
        <a-button type="primary" @click="genText">
          <span>
            <FileTextOutlined/>
            下载纯文本
          </span>
        </a-button>
      </a-space>
    </p>

    <a-table
      :data-source="subtitleList"
      :columns="columns"
      :pagination="pagination"
      :loading="loading"
      @change="handleTableChange"
    ></a-table>
  </a-modal>
</template>

<style scoped>

</style>