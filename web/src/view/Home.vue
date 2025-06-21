<script lang="ts" setup>
import ViewHeader from "@/components/ViewHeader.vue";
import ViewSider from "@/components/ViewSider.vue";

</script>

<template>
  <a-layout>
    <!-- page common view header -->
    <view-header></view-header>

    <a-layout>
      <!-- page common view sider -->
      <view-sider></view-sider>

      <!-- page common content -->
      <a-layout-content
          :style="{ background: '#fff', padding: '24px', margin: 0, minHeight: '280px' }"
      >
        <router-view></router-view>
      </a-layout-content>
    </a-layout>
  </a-layout>
</template>

<style scoped>

/*
.site-layout-background {
  background: #fff;
}
*/

</style>
