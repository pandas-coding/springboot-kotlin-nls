<script setup lang="ts">

import { useRouter } from "vue-router";
import { ref } from "vue";
import { message } from "ant-design-vue";
import service from "@/utils/request.ts";
import {
  ArrowLeftOutlined,
  CheckCircleOutlined,
  LockOutlined,
  MessageOutlined,
  MobileOutlined,
  SafetyOutlined
} from "@ant-design/icons-vue";
import { uuid } from "@/utils/tool.ts";
import { hashPassword } from "@/utils/password.ts";

const router = useRouter();

const resetMember = ref({
  mobile: '',
  imageCode: '',
  code: '',
  password: '',
  passwordOri: '',
  passwordConfirm: ''
});
const reset = async (values: Object) => {
  console.log('开始重置密码:', values);
  if (resetMember.value.passwordOri !== resetMember.value.passwordConfirm) {
    message.error("密码和确认密码不一致!")
    return
  }
  resetMember.value.password = resetMember.value.passwordOri
  const response = await service.post("/nls/web/member/reset", {
    mobile: resetMember.value.mobile,
    code: resetMember.value.code,
    password: hashPassword(resetMember.value.password),
  })

  const data = response.data
  if (data.success) {
    message.success("重置密码成功！")
    await router.push("/login")
  } else {
    message.error(data.message)
  }
}

// <editor-fold desc="短信验证码">-->
const sendBtnLoading = ref(false)
const sendText = ref("获取验证码")
const COUNTDOWN = 5
let countdown = ref(COUNTDOWN)
const setTime = () => {
  if (countdown.value === 0) {
    sendBtnLoading.value = false;
    sendText.value = "获取验证码";
    countdown.value = COUNTDOWN;
    return;
  } else {
    sendBtnLoading.value = true;
    sendText.value = "重新发送(" + countdown.value + ")";
    countdown.value--;
  }
  setTimeout(function () {
    setTime();
  }, 1000);
}
const sendResetSmsCode = async () => {
  console.log('发送短信验证码:');
  sendBtnLoading.value = true;
  const response = await service.post("/nls/web/sms-code/send-for-reset", {
    mobile: resetMember.value.mobile,
    imageCode: resetMember.value.imageCode,
    imageCodeToken: imageCodeToken.value,
  })

  const data = response.data;
  if (!data.success) {
    sendBtnLoading.value = false
    message.error(data.message)
    return
  }

  setTime()
  message.success("短信发送成功！")
}
// </editor-fold>-->


const imageCodeToken = ref()
const imageCodeSrc = ref()
/**
 * 加载图形验证码
 */
const loadImageCode = () => {
  resetMember.value.imageCode = ""
  imageCodeToken.value = uuid(8)
  imageCodeSrc.value = import.meta.env.VITE_SERVER + '/nls/web/kaptcha/image-code/' + imageCodeToken.value
}
loadImageCode()

</script>

<template>
  <div class="reset">
    <a-row>
      <a-col :span="6" :offset="9" class="main">

        <div class="title">
          重置密码
        </div>

        <a-form
            :model="resetMember"
            name="basic"
            :wrapper-col="{ span: 24 }"
            @finish="reset"
        >
          <a-form-item
              name="mobile" class="form-item"
              :rules="[{ required: true, message: '请输入手机号', trigger: 'blur' }, { pattern: /^\d{11}$/, message: '手机号为11位数字', trigger: 'blur' }]"
          >
            <a-input v-model:value="resetMember.mobile" placeholder="手机号" size="large">
              <template #prefix>
                <MobileOutlined style="margin-left: 15px"/>
              </template>
            </a-input>
          </a-form-item>

          <a-form-item name="imageCode" class="form-item"
                       :rules="[{ required: true, message: '请输入图片验证码', trigger: 'blur' }]">
            <a-input v-model:value="resetMember.imageCode" placeholder="图片验证码">
              <template #prefix>
                <SafetyOutlined style="margin-left: 15px"/>
              </template>
              <template #suffix>
                <img v-show="!!imageCodeSrc" :src="imageCodeSrc" alt="验证码" v-on:click="loadImageCode()"/>
              </template>
            </a-input>
          </a-form-item>

          <a-form-item name="code" class="form-item"
                       :rules="[{ required: true, message: '请输入短信验证码', trigger: 'blur' }]">
            <a-input-search
                v-model:value="resetMember.code"
                placeholder="短信验证码"
                :enter-button="sendText"
                @search="sendResetSmsCode"
                :loading="sendBtnLoading"
            >
              <template #prefix>
                <MessageOutlined style="margin-left: 15px"/>
              </template>
            </a-input-search>
          </a-form-item>

          <a-form-item
              name="passwordOri" class="form-item"
              :rules="[{ required: true, message: '请输入密码', trigger: 'blur' }, { pattern: /^(?![0-9]+$)(?![a-zA-Z]+$)[0-9A-Za-z]{6,20}$/, message: '密码包含数字和英文，长度6-20', trigger: 'blur' }]"
          >
            <a-input-password v-model:value="resetMember.passwordOri" placeholder="密码" size="large">
              <template #prefix>
                <LockOutlined style="margin-left: 15px"/>
              </template>
            </a-input-password>
          </a-form-item>

          <a-form-item
              name="passwordConfirm" class="form-item"
              :rules="[{ required: true, message: '请输入确认密码' }]"
          >
            <a-input-password v-model:value="resetMember.passwordConfirm" placeholder="确认密码" size="large">
              <template #prefix>
                <CheckCircleOutlined style="margin-left: 15px"/>
              </template>
            </a-input-password>
          </a-form-item>

          <a-form-item class="form-item">
            <a-button type="primary" block html-type="submit" class="reset-btn" size="large">
              重置密码
            </a-button>
          </a-form-item>
        </a-form>

        <p class="footer">
          <router-link to="/login">
            <ArrowLeftOutlined/>
            返回登录
          </router-link>
        </p>

      </a-col>
    </a-row>
  </div>
</template>

<style scoped>

</style>