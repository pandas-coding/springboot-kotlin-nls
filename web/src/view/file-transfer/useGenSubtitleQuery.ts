import { useAxios } from '@vueuse/integrations/useAxios'
import serviceAxios from '@/service/service-axios.ts'
import type { CommonRespData } from '@/service/service-types.ts'

export const useGenSubtitleQuery = (params: { fileTransferId: string }) => {
  const { data, execute, isLoading } = useAxios<CommonRespData>(
    '/nls/web/file-transfer-subtitle/gen-subtitle',
    {
      method: 'GET',
      params: {
        fileTransferId: params.fileTransferId,
      },
    },
    serviceAxios,
  )

  return {
    data,
    isLoading,
    genSubtitle: () => execute(),
  }
}