<script lang="ts" setup>
import ViewHeader from "@/components/ViewHeader.vue";
import ViewSider from "@/components/ViewSider.vue";
import axios from "axios"


async function queryMobile() {
  const resp = await axios.get("http://localhost:18000/nls/query", {
    params: {
      mobile: '111111',
    }
  }).catch(console.error)
  if (!resp) return

  console.log(resp)
}

queryMobile()
</script>

<template>
  <a-layout>
    <!-- page common view header -->
    <view-header></view-header>

    <a-layout>
      <!-- page common view sider -->
      <view-sider></view-sider>
      <a-layout style="padding: 0 24px 24px">
        <a-breadcrumb style="margin: 16px 0">
          <a-breadcrumb-item>Home</a-breadcrumb-item>
          <a-breadcrumb-item>List</a-breadcrumb-item>
          <a-breadcrumb-item>App</a-breadcrumb-item>
        </a-breadcrumb>
        <a-layout-content
            :style="{ background: '#fff', padding: '24px', margin: 0, minHeight: '280px' }"
        >
          Content
        </a-layout-content>
      </a-layout>
    </a-layout>
  </a-layout>
</template>

<style scoped>

.site-layout-background {
  background: #fff;
}

</style>
