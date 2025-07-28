import { useAxios } from '@vueuse/integrations/useAxios'
import serviceAxios from '@/service/service-axios.ts'
import type { CommonRespData } from '@/service/service-types.ts'
import type { MaybeRefOrGetter } from '@vueuse/core'
import { toValue } from 'vue'

export const useGenSubtitleQuery = (params: { fileTransferId: MaybeRefOrGetter<string> }) => {

  const { data, execute, isLoading } = useAxios<CommonRespData<string>>(
    '/nls/web/file-transfer-subtitle/gen-subtitle',
    {
      method: 'GET',
    },
    serviceAxios,
  )

  const genSubtitle = async () => {
    const _fileTransferId = toValue(params.fileTransferId)
    await execute('/nls/web/file-transfer-subtitle/gen-subtitle', {
      params: {
        fileTransferId: _fileTransferId,
      },
    })
  }

  return {
    data,
    isLoading,
    genSubtitle,
  }
}