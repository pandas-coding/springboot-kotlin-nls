<script setup lang="ts">
import { FileTextOutlined } from '@ant-design/icons-vue'
import type { FileTransferSubtitleProps } from '@/view/file-transfer/file-transfer-subtitle.ts'
import { useFileTransferSubtitleTable } from '@/view/file-transfer/useFileTransferSubtitleTable.ts'
import { useConfirmDialog } from '@vueuse/core';
import { useGenSubtitleQuery } from '@/view/file-transfer/useGenSubtitleQuery.ts'
import { useGenTextQuery } from '@/view/file-transfer/useGenTextQuery.ts'
import { computed, ref, useTemplateRef } from 'vue'
import type DownloadLink from '@/components/DownloadLink.vue'

const props = defineProps<FileTransferSubtitleProps>()

const downloadUrl = ref('')
const downloadLinkRef = useTemplateRef<InstanceType<typeof DownloadLink>>('downloadLinkRef')

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
} = useGenSubtitleQuery({fileTransferId: () => props.fileTransferId})

const {
  data: textUrl,
  isLoading: isGenTextQueryLoading,
  genText,
} = useGenTextQuery({fileTransferId: props.fileTransferId})

const isTableLoading = computed(() => isSubtitleListLoading.value || isGenSubtitleQueryLoading.value || isGenTextQueryLoading.value)

const onClickGenSubtitle = async () => {
  await genSubtitle()
  downloadUrl.value = subtitleUrl.value.content ?? ''
  downloadLinkRef.value?.downloadItem(downloadUrl.value, `${props.name}-${props.fileTransferId}.srt`)
}

const onClickGenText = async () => {
  await genText()
  downloadUrl.value = textUrl.value.content ?? ''
  downloadLinkRef.value?.downloadItem(downloadUrl.value, `${props.name}-${props.fileTransferId}.txt`)
}

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
        <a-button type="primary" @click="onClickGenSubtitle">
          <span>
            <FileTextOutlined/>
            下载字幕
          </span>
        </a-button>
        <a-button type="primary" @click="onClickGenText">
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

  <download-link
    ref="downloadLinkRef"
    :download-url="downloadUrl"
  />
</template>

<style scoped>

</style>