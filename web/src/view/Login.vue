<script setup lang="ts">
import { useRouter } from "vue-router";
import { ref } from "vue";
import service from "@/utils/request.ts";
import { message } from "ant-design-vue";
import { LockOutlined, MobileOutlined, SafetyOutlined } from "@ant-design/icons-vue";


const router = useRouter()

const loginMember = ref({
  mobile: '',
  password: '',
  // imageCode: '',
})

const login = async (values: {}) => {
  console.info('开始登录: %o', values)
  const response = await service.post('/nls/web/member/login', {
    mobile: loginMember.value.mobile,
    // password: hexMd5Key(loginMember.value.password),
    // imageCode: loginMember.value.imageCode,
    // imageCodeToken: imageCodeToken.value,
  })
  const data = response.data
  if (data.success) {
    message.success('登录成功!')
    await router.push('/home/welcome')
  } else {
    message.error(data.message)
  }
}

</script>

<template>
  <div class="login">
    <a-row>
      <a-col class="main" :span="6" :offset="9">

        <div class="title">springboot-kotlin nls</div>

        <a-form
            name="basic"
            :model="loginMember"
            :wrapper-col="{ span: 24 }"
            @finish="login"
        >
          <a-form-item
              name="mobile" class="form-item"
              :rules="[{ required: true, message: '请输入手机号' }]"
          >
            <a-input v-model:value="loginMember.mobile" placeholder="手机号" size="large">
              <template #prefix>
                <MobileOutlined style="margin-left: 15px"/>
              </template>
            </a-input>
          </a-form-item>

          <a-form-item
              name="password" class="form-item"
              :rules="[{ required: true, message: '请输入密码' }]"
          >
            <a-input-password v-model:value="loginMember.password" placeholder="密码" size="large">
              <template #prefix>
                <LockOutlined style="margin-left: 15px"/>
              </template>
            </a-input-password>
          </a-form-item>

          <a-form-item name="imageCode" class="form-item"
                       :rules="[{ required: true, message: '请输入图片验证码', trigger: 'blur' }]">
            <a-input v-model:value="loginMember.imageCode" placeholder="图片验证码">
              <template #prefix>
                <SafetyOutlined style="margin-left: 15px"/>
              </template>
              <template #suffix>
                <!--<img v-show="!!imageCodeSrc" :src="imageCodeSrc" alt="验证码" v-on:click="loadImageCode()"/>-->
              </template>
            </a-input>
          </a-form-item>

          <a-form-item class="form-item">
            <a-button type="primary" block html-type="submit" class="login-btn" size="large">
              登&nbsp;录
            </a-button>
          </a-form-item>
        </a-form>

        <p class="footer">
          <router-link to="/register">注册</router-link>
          <router-link to="/reset" class="pull-right">忘记密码</router-link>
        </p>
      </a-col>
    </a-row>
  </div>
</template>

<style scoped>

</style>