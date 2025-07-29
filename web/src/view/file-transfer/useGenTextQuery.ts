import { useAxios } from '@vueuse/integrations/useAxios'
import serviceAxios from '@/service/service-axios.ts'
import type { MaybeRefOrGetter } from '@vueuse/core'
import { ref, toValue } from 'vue'
import type { CommonRespData } from '@/service/service-types.ts'

export const useGenTextQuery = (params: { fileTransferId: MaybeRefOrGetter<string> }) => {
  const mediaUrl = ref<string>()

  const { execute, isLoading } = useAxios<CommonRespData<string>>(
    '/nls/web/file-transfer-subtitle/gen-text',
    {
      method: 'GET',
    },
    serviceAxios,
  )

  const genText = async () => {
    const _fileTransferId = toValue(params.fileTransferId)
    const axiosReturn = await execute('/nls/web/file-transfer-subtitle/gen-text',
      {
        params: {
          fileTransferId: _fileTransferId,
        },
      })
    mediaUrl.value = axiosReturn.data.value.content
  }

  return {
    mediaUrl,
    isLoading,
    genText,
  }
}