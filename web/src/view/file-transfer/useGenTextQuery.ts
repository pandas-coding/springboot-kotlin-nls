import { useAxios } from '@vueuse/integrations/useAxios'
import serviceAxios from '@/service/service-axios.ts'

export const useGenTextQuery = (params: { fileTransferId: string }) => {
  const {data, execute, isLoading} = useAxios(
    '/nls/web/file-transfer-subtitle/gen-text',
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
    genText: () => execute(),
  }
}