<script setup lang="ts">
import { CoffeeOutlined, QuestionCircleOutlined, VideoCameraOutlined } from "@ant-design/icons-vue";
import { ref, watch } from "vue";
import { useRouter } from "vue-router";

const router = useRouter();
const selectedKeys = ref(['/home/welcome']);

watch(() => router.currentRoute.value.path, (newValue, oldValue) => {
  console.info('watch route changed: ', newValue, oldValue)
  selectedKeys.value = []
  selectedKeys.value.push(newValue)
}, { immediate: true })

</script>

<template>
  <a-layout-sider width="200" style="background: #fff">
    <a-menu
        v-model:selectedKeys="selectedKeys"
        mode="inline"
        :style="{ height: '100%', borderRight: 0 }"
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
  </a-layout-sider>
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