<script setup lang="ts">
import { computed, onMounted } from 'vue'
import { formatSecond } from '@/utils/tool.ts'
import { useQueryStatistic } from '@/view/home/useQueryStatistic.ts'

const { statistic, queryStatistic } = useQueryStatistic()

const formattedSecond = computed(() => formatSecond((statistic.value?.fileTransferSecond ?? 0).toString()))

onMounted(() => {
  queryStatistic()
})
</script>

<template>
  <a-card>
    <a-row :gutter="[0, 20]">
      <a-col :span="4">
        <a-statistic title="在线人数" :value="statistic?.onLineCount ?? 0"/>
      </a-col>
      <a-col :span="4">
        <a-statistic title="注册人数" :value="statistic?.registerCount ?? 0"/>
      </a-col>
      <a-col :span="4">
        <a-statistic title="订单数" :value="statistic?.orderCount ?? 0"/>
      </a-col>
      <a-col :span="4">
        <a-statistic title="订单金额" :value="statistic?.orderAmount ?? 0"/>
      </a-col>
      <a-col :span="4">
        <a-statistic title="语音识别次数" :value="statistic?.fileTransferCount ?? 0"/>
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