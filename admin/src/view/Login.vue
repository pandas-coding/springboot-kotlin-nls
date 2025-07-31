<script setup lang="ts">
import { useRouter } from 'vue-router'
import { reactive, ref, watch } from 'vue'
import { service } from '@/service/index.ts'
import { message } from 'ant-design-vue'
import { LockOutlined, MobileOutlined, SafetyOutlined } from '@ant-design/icons-vue'
import { hashPassword } from '@/utils/password.ts'
import { uuid } from '@/utils/tool.ts'
import { useUserStore } from '@/stores/user-store.ts'


const router = useRouter()

const {setUser} = useUserStore()

const loginUser = reactive({
  loginName: '',
  password: '',
  imageCode: '',
})

const login = async (values: {}) => {
  console.info('开始登录: %o', values)
  const response = await service.post('/nls/admin/user/login', {
    loginName: loginUser.loginName,
    password: hashPassword(loginUser.password),
    imageCode: loginUser.imageCode,
    imageCodeToken: imageCodeToken.value,
  })

  const data = response.data
  if (!data.success) {
    message.error(data.message)
    return
  }

  message.success('登录成功!')
  setUser(data.content)
  await router.push('/home/welcome')
}

// ----------- 图形验证码 --------------------
const imageCodeToken = ref()
const imageCodeSrc = ref()
/**
 * 加载图形验证码
 */
const loadImageCode = () => {
  loginUser.imageCode = ''
  imageCodeToken.value = uuid(8)
  imageCodeSrc.value = `${import.meta.env.VITE_SERVER}/nls/admin/kaptcha/image-code/${imageCodeToken.value}`
}
loadImageCode()

</script>

<template>
  <div class="login">
    <a-row>
      <a-col class="main" :span="6" :offset="9">

        <div class="title">springboot-kotlin nls</div>

        <a-form
            name="basic"
            :model="loginUser"
            :wrapper-col="{ span: 24 }"
            @finish="login"
        >
          <!-- 用户名 -->
          <a-form-item
              name="loginName" class="form-item"
              :rules="[{ required: true, message: '请输入用户名' }]"
          >
            <a-input v-model:value="loginUser.loginName" placeholder="用户名" size="large">
              <template #prefix>
                <MobileOutlined style="margin-left: 15px"/>
              </template>
            </a-input>
          </a-form-item>

          <!-- 密码框 -->
          <a-form-item
              name="password" class="form-item"
              :rules="[{ required: true, message: '请输入密码' }]"
          >
            <a-input-password v-model:value="loginUser.password" placeholder="密码" size="large">
              <template #prefix>
                <LockOutlined style="margin-left: 15px"/>
              </template>
            </a-input-password>
          </a-form-item>

          <!-- 图片验证码 -->
          <a-form-item name="imageCode" class="form-item"
                       :rules="[{ required: true, message: '请输入图片验证码', trigger: 'blur' }]">
            <a-input v-model:value="loginUser.imageCode" placeholder="图片验证码">
              <template #prefix>
                <SafetyOutlined style="margin-left: 15px"/>
              </template>
              <template #suffix>
                <img v-show="!!imageCodeSrc" :src="imageCodeSrc" alt="验证码" v-on:click="loadImageCode()"/>
              </template>
            </a-input>
          </a-form-item>

          <!-- 登录按钮 -->
          <a-form-item class="form-item">
            <a-button type="primary" block html-type="submit" class="login-btn" size="large">
              登&nbsp;录
            </a-button>
          </a-form-item>
        </a-form>
      </a-col>
    </a-row>
  </div>
</template>

<style scoped>

</style>