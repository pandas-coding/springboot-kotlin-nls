<script setup lang="ts">
import { computed, onMounted, useTemplateRef, watchEffect } from 'vue'
import { formatSecond } from '@/utils/tool.ts'
import { useQueryStatistic } from '@/view/home/useQueryStatistic.ts'
import { useStatisticChart } from '@/view/home/useStatisticChart.tsx'

const { statistic, queryStatistic } = useQueryStatistic()

const registerCountListRef = useTemplateRef('registerCountListRef')
const fileTransferCountListRef = useTemplateRef('fileTransferCountListRef')
const fileTransferSecondListRef = useTemplateRef('fileTransferSecondListRef')
const orderCountListRef = useTemplateRef('orderCountListRef')
const orderAmountListRef = useTemplateRef('orderAmountListRef')

const formattedSecond = computed(() => formatSecond((statistic.value?.fileTransferSecond ?? 0).toString()))

const { mountChart: mountRegisterCountListChart } = useStatisticChart({
  title: '注册人数',
  chartContainer: registerCountListRef,
  statisticDateList: () => statistic.value?.registerCountList,
})
const { mountChart: mountFileTransferCountListChart } = useStatisticChart({
  title: '语音识别次数',
  chartContainer: fileTransferCountListRef,
  statisticDateList: () => statistic.value?.fileTransferCountList,
})
const { mountChart: mountFileTransferSecondListChart } = useStatisticChart({
  title: '语音识别时长',
  chartContainer: fileTransferSecondListRef,
  statisticDateList: () => statistic.value?.fileTransferSecondList,
})
const { mountChart: mountOrderCountListChart } = useStatisticChart({
  title: '订单数',
  chartContainer: orderCountListRef,
  statisticDateList: () => statistic.value?.orderCountList,
})
const { mountChart: mountOrderAmountListChart } = useStatisticChart({
  title: '订单金额',
  chartContainer: orderAmountListRef,
  statisticDateList: () => statistic.value?.orderAmountList,
})

watchEffect(() => {
  mountRegisterCountListChart(() => statistic.value?.registerCountList)
  mountFileTransferCountListChart(() => statistic.value?.fileTransferCountList)
  mountFileTransferSecondListChart(() => statistic.value?.fileTransferSecondList)
  mountOrderCountListChart(() => statistic.value?.orderCountList)
  mountOrderAmountListChart(() => statistic.value?.orderAmountList)
})

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
  <a-row>
    <a-col :span="8">
      <div ref="registerCountListRef" class="chart-container"></div>
    </a-col>
    <a-col :span="8">
      <div ref="fileTransferCountListRef" class="chart-container"></div>
    </a-col>
    <a-col :span="8">
      <div ref="fileTransferSecondListRef" class="chart-container"></div>
    </a-col>
    <a-col :span="8">
      <div ref="orderCountListRef" class="chart-container"></div>
    </a-col>
    <a-col :span="8">
      <div ref="orderAmountListRef" class="chart-container"></div>
    </a-col>
  </a-row>
</template>

<style scoped>

.chart-container {
  width: 100%;
  height: 250px;
}

</style>