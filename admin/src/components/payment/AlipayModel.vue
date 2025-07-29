<script setup lang="ts">
import { reactive, ref } from 'vue'
import {CheckOutlined} from '@ant-design/icons-vue'
import type { AlipayModelEmits, PayInfo } from '@/components/payment/alipay-payment.ts'
import restService from '@/service/rest-service.ts'
import { message, notification } from 'ant-design-vue'

const emit = defineEmits<AlipayModelEmits>()

const payInfo = reactive({
  desc: '',
  amount: 0,
})
const orderNo = ref('')

const open = ref(false)
const modalLoading = ref(false)


const handleOpen = (info: PayInfo) => {
  console.log('payInfo: %o', info)
  Object.assign(payInfo, info)
  open.value = true

  queryPayResult(info.orderNo)

  // 需要放到异步，才能在iframe里显示支付宝二维码，否则浏览器会弹出新tab
  setTimeout(() => {
    // 先移除已有的divform元素
    const divForm = document.getElementsByTagName('divform')
    if (divForm.length) {
      Array.from(divForm).forEach(divFormItem => divFormItem.remove())
    }
    const div = document.createElement('divform');
    div.innerHTML = info.qrcode; // 支付宝返回的form
    document.body.appendChild(div);
    document.forms[0].setAttribute('target', 'alipay-qrcode');
    document.forms[0].submit();
  }, 100)
}

const handleModalOk = () => {
  queryOrder(orderNo.value)
}

/**
 * 取消
 */
const handleCancel = () => {
  open.value = false
  clearInterval(queryPayInterval.value)
}

/**
 * 关闭弹框回调
 */
const afterClose = () => {
  console.log('afterClose')
  clearInterval(queryPayInterval.value)
}

const queryPayInterval = ref()
const queryPayResult = (queryOrderNo: string) => {
  orderNo.value = queryOrderNo
  queryPayInterval.value = setInterval(() => {
    queryOrder(queryOrderNo)
  }, 2000)
}

const queryOrder = async (queryOrderNo: string) => {
  const respData = await restService.get(`/nls/web/order-info/query-order-status/${queryOrderNo}`)
  if (!respData.success) {
    message.error(respData.message)
    return
  }
  const payStatus = respData.content
  switch (payStatus) {
    case 'I': {
      console.info(`用户未支付, 支付状态: %o`, respData)
      break
    }
    case 'S': {
      clearInterval(queryPayInterval.value)
      notification.success({
        message: '支付宝支付提示',
        description: '支付成功',
      })
      open.value = false
      emit('after-pay', 'S')
      break
    }
    case 'F': {
      clearInterval(queryPayInterval.value)
      notification.error({
        message: '支付宝支付失败',
        description: '支付失败',
      })
      emit('after-pay', 'F')
      break
    }
    default: {
      message.error(respData.message)
    }
  }
}

defineExpose({
  handleOpen
})
</script>

<template>
  <a-modal
    :title="payInfo.desc"
    :open="open"
    :confirm-loading="modalLoading"
    :after-close="afterClose"
    :closable="false"
    style="top: 20px;"
    width="400px"
  >
    <div class="pay-info">
      <div style="font-size: 25px; margin: 20px;">
        <img style="width: 35px" src="@/assets/image/alipay-icon.jpg" alt=""/>
        支付宝扫码支付
      </div>
      <iframe name="alipay-qrcode" align="middle" class="alipay-qrcode-iframe"></iframe>
      <div style="font-size: 16px;">
        打开手机支付宝，扫码支付
        <span style="color: red">{{payInfo.amount}}</span>
        元
      </div>
    </div>

    <template #footer>
      <a-button key="back" size="large" @click="handleCancel" >取消支付</a-button>
      <a-button key="submit" type="primary" size="large" :loading="modalLoading" @click="handleModalOk">
        <CheckOutlined />
        我已支付
      </a-button>
    </template>
  </a-modal>

</template>

<style scoped>
.pay-info {
  text-align: center;
  margin-bottom: 20px;
}

.alipay-qrcode-iframe {
  height: 240px;
  border: none;
  width: 200px;
  text-align: center;
}

</style>