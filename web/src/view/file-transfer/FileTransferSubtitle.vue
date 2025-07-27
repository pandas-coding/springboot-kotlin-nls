<script setup lang="ts">
import { FileTextOutlined } from '@ant-design/icons-vue'
import type { FileTransferSubtitleProps } from '@/view/file-transfer/file-transfer-subtitle.ts'
import { useFileTransferSubtitleTable } from '@/view/file-transfer/useFileTransferSubtitleTable.ts'
import { useConfirmDialog } from '@vueuse/core';
import { useGenSubtitleQuery } from '@/view/file-transfer/useGenSubtitleQuery.ts'
import { useGenTextQuery } from '@/view/file-transfer/useGenTextQuery.ts'
import { computed, ref } from 'vue'

const props = defineProps<FileTransferSubtitleProps>()

const downloadUrl = ref('')

const {
  isRevealed,
  reveal,
  confirm,
  cancel,
  onReveal,
} = useConfirmDialog()
onReveal(() => {
  querySubtitleList(props.fileTransferId)
})

const {
  subtitleList,
  columns,
  pagination,
  loading: isSubtitleListLoading,
  querySubtitleList,
  handleTableChange,
} = useFileTransferSubtitleTable({fileTransferId: () => props.fileTransferId})

const {
  data: subtitleUrl,
  isLoading: isGenSubtitleQueryLoading,
  genSubtitle,
} = useGenSubtitleQuery({fileTransferId: props.fileTransferId})

const {
  data: textUrl,
  isLoading: isGenTextQueryLoading,
  genText,
} = useGenTextQuery({fileTransferId: props.fileTransferId})

const isTableLoading = computed(() => isSubtitleListLoading.value || isGenSubtitleQueryLoading.value || isGenTextQueryLoading.value)

defineExpose({
  showModal: () => reveal(),
})
</script>

<template>
  <a-modal
    v-model:open="isRevealed"
    title=""
    footer=""
    style="width: 800px; top: 20px"
    @cancel="cancel"
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
      :loading="isTableLoading"
      @change="handleTableChange"
    ></a-table>
  </a-modal>

  <download-anchor
    ref="downloadAnchorRef"
    :download-url="downloadUrl"
  />
</template>

<style scoped>

</style>