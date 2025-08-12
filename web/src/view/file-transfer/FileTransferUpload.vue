<script setup lang="ts">
import {reactive, ref, useTemplateRef} from 'vue'
import {UploadOutlined} from '@ant-design/icons-vue'
import {notification} from 'ant-design-vue'

import {base64MD5String} from '@/utils/password.ts'
import restService from '@/service'
import { useAliyunUpload } from '@/hooks/aliyun-upload.ts'
import type { FileTransferInfo, FileTransferUploadEmits } from '@/view/file-transfer/file-transfer-upload.ts'
import { FILE_TRANSFER_LANG_ARRAY } from "../../../public/js/enums.ts";
import { isEmpty } from 'radash'
import type AlipayModel from '@/components/payment/AlipayModel.vue'
import { useUploadDemo } from '@/view/file-transfer/useUploadDemo.ts'

const emit = defineEmits<FileTransferUploadEmits>()

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
// const FILE_TRANSFER_LANG_ARRAY = ref(FILE_TRANSFER_LANG_ARRAY)
const fileUploadInputRef = useTemplateRef<InstanceType<typeof HTMLInputElement>>('file-upload-input')
const alipayModelRef = useTemplateRef<InstanceType<typeof AlipayModel>>('alipay-model')


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

    setTimeout(calcAmount, 500)
    return
  }
  console.info('获取上传文件凭证成功:', content)
  fileTransfer.vod = content.videoId

  setUploadAuth(content.uploadAuth, content.uploadAddress, content.videoId)
  uploader.addFile(file, null, null, null, null)
  uploader.startUpload()

  setTimeout(calcAmount, 500)
}

// -------------- 计算收费金额 ---------------
const calcAmount = async () => {
  const respData = await restService.get(`/nls/web/vod/calc-amount/${fileTransfer.vod}`)
  if (!respData.success) {
    notification.error({
      message: '系统提示',
      description: '计算收费金额异常',
    })
    return
  }
  fileTransfer.amount = respData.content
}

//<editor-fold desc="结算">
const pay = async (_ev: Event) => {
  console.info('开始结算: ', JSON.stringify(fileTransfer))
  if (isEmpty(fileTransfer.audio)) {
    notification.error({
      message: '系统提示',
      description: '请先上传音频文件',
    })
    return
  }
  if (isEmpty(fileTransfer.lang)) {
    notification.error({
      message: '系统提示',
      description: '请选择 音频语言',
    })
    return
  }
  if (fileTransfer.amount <= 0) {
    notification.error({
      message: '系统提示',
      description: '金额计算异常',
    })
    return
  }

  const respData = await restService.post<{
    channelResult: string,
    orderNo: string,
  }>('/nls/web/file-transfer/pay', {
    ...fileTransfer,
  })
  if (!respData.success) {
    notification.error({
      message: '系统提示',
      description: '下单失败',
    })
    return
  }
  notification.success({
    message: '系统提示',
    description: `下单成功, 订单号: ${respData.content?.orderNo}`,
  })

  // 下单成功, 如果是支付宝渠道, 打开支付宝二维码弹框
  switch (fileTransfer.channel) {
    case 'A': {
      const payInfo = {
        amount: fileTransfer.amount,
        desc: '语音识别结算',
        qrcode: respData.content!.channelResult,
        orderNo: respData.content!.orderNo,
      }
      alipayModelRef.value?.handleOpen(payInfo)
      break
    }
  }
}
//</editor-fold>

// <editor-fold desc="扫码支付">
const handleAfterPay = () => {
  open.value = false
  emit('after-pay')
}
// </editor-fold>

const {
  isLoading: uploadLoading,
  uploadDemo: selectDemoFile,
  onUploaded,
  onError,
} = useUploadDemo()
onUploaded((demo) => {
  Object.assign(fileTransfer, demo, {percent: 100})
})
onError(() => {
  notification.error({
    message: '系统提示',
    description: '示例文件上传失败',
  })
})

defineExpose({
  showModal,
})
</script>

<template>
  <a-modal
    v-model:open="open"
    title=""
    ok-text="结算"
    cancel-text="取消"
    @ok="pay"
  >
    <p>
      <a-space>
        <a-button type="primary" @click="selectFile">
          <span>
            <UploadOutlined/>
            选择音频
          </span>
        </a-button>
        <a-button
          type="primary"
          :loading="uploadLoading"
          @click="selectDemoFile"
        >
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
    <!-- 支付方式 -->
    <p>
      支付方式：
      <a-radio-group name="pay-channel-group" v-model:value="fileTransfer.channel">
        <a-radio value="A">
          <img src="../../assets/image/alipay.jpg" alt="支付宝" style="height: 30px;">
        </a-radio>
      </a-radio-group>
    </p>
  </a-modal>

  <alipay-model ref="alipay-model" @after-pay="handleAfterPay"></alipay-model>
</template>

<style scoped>

</style>