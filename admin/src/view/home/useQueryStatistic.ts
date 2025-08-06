import { ref } from 'vue'
import { useAxios } from '@vueuse/integrations/useAxios'
import serviceAxios from '@/service/service-axios.ts'
import type { CommonRespData } from '@/service/service-types.ts'
import type { Statistic } from '@/view/home/home-welcome.ts'


export const useQueryStatistic = () => {
  const statistic = ref<Statistic>()

  const { execute } = useAxios<CommonRespData<Statistic>>(
    '/nls/admin/report/query-statistic',
    { method: 'GET' },
    serviceAxios,
    { immediate: false },
  )

  const queryStatistic = async () => {
    const resp = await execute()
    statistic.value = resp.data.value?.content
  }

  return {
    statistic,
    queryStatistic,
  }
}