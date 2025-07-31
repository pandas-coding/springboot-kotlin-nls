<script setup lang="ts">
import { ref, watch } from 'vue';
import { useRouter } from "vue-router";
import { CoffeeOutlined, QuestionCircleOutlined, VideoCameraOutlined, } from '@ant-design/icons-vue'
import { storeToRefs } from "pinia";
import { useUserStore } from '@/stores/user-store.ts'

const router = useRouter()
const userStore = useUserStore()
const {user} = storeToRefs(userStore)

const selectedKeys = ref(['/home/welcome'])

watch(() => router.currentRoute.value.path, (newValue: any, oldValue: any) => {
  console.info(`watch route change:`, newValue, oldValue)
  selectedKeys.value = []
  selectedKeys.value.push(newValue)
}, { immediate: true })

</script>

<template>
  <a-layout-header class="header">
    <div class="logo"/>

    <div style="float: right; color: white;">
      Hello ~: {{ user?.name }}

      <router-link to="/login" style="color: white;">
        退出登录
      </router-link>
    </div>
    <a-menu
        v-model:selectedKeys="selectedKeys"
        theme="dark"
        mode="horizontal"
        :style="{ lineHeight: '64px' }"
    >
      <a-menu-item key="/home/welcome">
        <router-link to="/home/welcome">
          <CoffeeOutlined/>
          <span>欢迎使用</span>
        </router-link>
      </a-menu-item>

      <a-menu-item key="/home/file-transfer">
        <router-link to="/home/file-transfer">
          <VideoCameraOutlined/>
          <span>语音识别</span>
        </router-link>
      </a-menu-item>

      <a-menu-item key="/home/help">
        <router-link to="/home/help">
          <QuestionCircleOutlined/>
          <span>帮助文档</span>
        </router-link>
      </a-menu-item>
    </a-menu>
  </a-layout-header>
</template>

<style scoped>
.logo {
  float: left;
  width: 120px;
  height: 31px;
  margin: 16px 24px 16px 0;
  background: rgba(255, 255, 255, 0.3);
}

.ant-row-rtl .logo {
  float: right;
  margin: 16px 0 16px 24px;
}

</style>