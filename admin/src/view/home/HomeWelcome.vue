<script setup lang="ts">
import { computed, reactive } from "vue";
import { formatSecond } from "@/utils/tool.ts";

const statistic = reactive({
  onlineCount: 0,
  registerCount: 0,
  orderCount: 0,
  orderAmount: 0,
  filetransCount: 0,
  filetransSecond: 0,
})

const formattedSecond = computed(() => formatSecond(statistic.filetransSecond.toString()))

</script>

<template>
  <a-card>
    <a-row :gutter="[0, 20]">
      <a-col :span="4">
        <a-statistic title="在线人数" :value="statistic.onlineCount"/>
      </a-col>
      <a-col :span="4">
        <a-statistic title="注册人数" :value="statistic.registerCount"/>
      </a-col>
      <a-col :span="4">
        <a-statistic title="订单数" :value="statistic.orderCount"/>
      </a-col>
      <a-col :span="4">
        <a-statistic title="订单金额" :value="statistic.orderAmount"/>
      </a-col>
      <a-col :span="4">
        <a-statistic title="语音识别次数" :value="statistic.filetransCount"/>
      </a-col>
      <a-col :span="4">
        <a-statistic title="语音识别时长" :value="formattedSecond"/>
      </a-col>
    </a-row>
  </a-card>
  <br/>
  <br/>
  <a-row></a-row>
</template>

<style scoped>

</style>