<script setup lang="ts">
import {reactive, ref, useTemplateRef} from 'vue'
import {UploadOutlined} from '@ant-design/icons-vue'
import {notification} from 'ant-design-vue'

import {base64MD5String} from '@/utils/password.ts'
import restService from '@/service'
import { useAliyunUpload } from '@/hooks/aliyun-upload.ts'
import type { FileTransferInfo } from '@/view/home/types/file-transfer-upload-types.ts'

const {
  uploader,
  setUploadAuth,
  setOnUploadSucceed,
  setOnUploadProgress,
  setOnUploadEnd,
} = useAliyunUpload()
setOnUploadSucceed((fileUrl) => fileTransfer.audio = fileUrl)
setOnUploadProgress(loadedPercent => fileTransfer.percent = loadedPercent * 100)
setOnUploadEnd(() => fileUploadInputRef.value!!.value = '')

const INIT_FILE_TRANSFER: FileTransferInfo = Object.freeze({
  name: '',
  percent: 0,
  amount: 0,
  lang: '',
  audio: '',
  fileSign: '',
  vod: '',
  channel: '',
})

const open = ref(false)
const fileTransfer: FileTransferInfo = reactive({...INIT_FILE_TRANSFER})
const FILE_TRANSFER_LANG_ARRAY = ref(window.FILE_TRANSFER_LANG_ARRAY)
const fileUploadInputRef = useTemplateRef<InstanceType<typeof HTMLInputElement>>('file-upload-input')


const showModal = () => {
  resetFileTransfer()
  open.value = true
}

// 重置上传文件信息的变量
const resetFileTransfer = () => {
  Object.assign(fileTransfer, INIT_FILE_TRANSFER)
}

// 选中文件
const selectFile = () => {
  fileUploadInputRef.value!.value = ''
  fileUploadInputRef.value?.click()
}

// 选中后上传文件
const uploadFile = async () => {
  const file = fileUploadInputRef.value?.files?.[0]
  if (!file) {
    notification.warning({
      message: '请选择文件',
    })
    return
  }
  console.info('selected upload file: ', file)

  const max = 500 * 1024 * 1024
  const size = file.size
  if (size > max) {
    notification.warning({
      message: '系统提示',
      description: '文件大小超过最大限制, 最大500M',
    })
    return
  }

  // 初始化上传文件信息
  fileTransfer.name = file.name
  fileTransfer.percent = 0
  fileTransfer.amount = 0
  fileTransfer.channel = 'A'

  const key = base64MD5String(file.name + file.type + file.size + file.lastModified)
  // 请求后端接口获取上传凭证
  const respData = await restService.post('/nls/web/vod/get-upload-auth', {
    name: file.name,
    key,
  })

  if (!respData.success) {
    notification.error({
      message: '系统提示',
      description: '上传文件失败',
    })
    return
  }
  const content = respData.content
  if (null != content.fileUrl) {
    console.info('文件已上传过, 地址:', content.fileUrl)
    fileTransfer.percent = 100
    fileTransfer.vod = content.videoId
    fileTransfer.audio = content.fileUrl
    return
  }
  console.info('获取上传文件凭证成功:', content)
  fileTransfer.vod = content.videoId
  uploadAuth = content.uploadAuth
  uploadAddress = content.uploadAddress
  videoId = content.videoId

  setUploadAuth(content.uploadAuth, content.uploadAddress, content.videoId)
  uploader.addFile(file, null, null, null, null)
  uploader.startUpload()
}

let uploadAuth: string
let uploadAddress: string
let videoId: string

defineExpose({
  showModal,
})
</script>

<template>
  <a-modal v-model:open="open" title="">
    <p>
      <a-space>
        <a-button type="primary" @click="selectFile">
          <span>
            <UploadOutlined/>
            选择音频
          </span>
        </a-button>
        <a-button type="primary">
          没有音频? 使用示例音频
        </a-button>
        <input
            ref="file-upload-input"
            type="file"
            accept=".mp3,.wav,.m4a"
            style="display: none;"
            @change="uploadFile"
        />
      </a-space>
    </p>
    <p>
      已选择文件: {{ fileTransfer.name }}<span v-show="fileTransfer.amount > 0">，金额: <b
        style="color:red; font-size:18px;">{{ fileTransfer.amount }}</b> 元</span>
    </p>
    <p>
      <a-progress :percent="Number(fileTransfer.percent.toFixed(1))"></a-progress>
    </p>
    <p>
      音频语言：
      <a-select v-model:value="fileTransfer.lang" style="width: 120px;">
        <a-select-option v-for="op in FILE_TRANSFER_LANG_ARRAY" :value="op.code">{{ op.desc }}</a-select-option>
      </a-select>
    </p>
  </a-modal>

</template>

<style scoped>

</style>