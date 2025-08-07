import { render, toValue } from 'vue'
import type { MaybeRefOrGetter } from '@vueuse/core'
import * as echarts from 'echarts/core'
import { BarChart, type BarSeriesOption, LineChart, type LineSeriesOption } from 'echarts/charts'
import type { ComposeOption } from 'echarts/core'
import {
  type DatasetComponentOption, GridComponent,
  type GridComponentOption, TitleComponent,
  type TitleComponentOption,
  type TooltipComponentOption,
} from 'echarts/components'
import { CanvasRenderer } from 'echarts/renderers'
import type { StatisticDate } from '@/view/home/home-welcome.ts'

echarts.use([
  TitleComponent,
  GridComponent,
  BarChart,
  LineChart,
  CanvasRenderer,
])

export const useStatisticChart = (params: {
  title: string,
  chartContainer: MaybeRefOrGetter<HTMLElement | null | undefined>,
  statisticDateList: MaybeRefOrGetter<StatisticDate[] | undefined>,
}) => {

  const mountChart = (statisticDateList: MaybeRefOrGetter<StatisticDate[] | undefined>) => {
    const containerElement = toValue(params.chartContainer)
    const _statisticDateList = toValue(statisticDateList)
    if (!containerElement || !_statisticDateList) return

    const xAxis = _statisticDateList.map(record => record.date)
    const yAxis = _statisticDateList.map(record => record.num)

    // containerElement.querySelectorAll('[data-chart-element]')
    //   .forEach(el => el.remove())
    render(null, containerElement)
    const chartElement = (<div style={{ width: '100%', height: '250px' }} data-chart-element></div>)
    render(chartElement, containerElement)

    const chartInstance = echarts.init(containerElement.querySelector('[data-chart-element]') as HTMLElement)
    chartInstance.setOption({
      title: {
        text: params.title,
      },
      xAxis: {
        data: xAxis,
      },
      yAxis: {},
      series: [
        {
          data: yAxis,
          type: 'bar',
        },
      ],
    } as EChartsBarSeriesOption)
  }

  return {
    mountChart,
  }
}

type EChartsBarSeriesOption = ComposeOption<
  | BarSeriesOption
  | LineSeriesOption
  | TitleComponentOption
  | TooltipComponentOption
  | GridComponentOption
  | DatasetComponentOption
>
