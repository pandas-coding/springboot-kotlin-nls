import { useAxios } from '@vueuse/integrations/useAxios'
import serviceAxios from '@/service/service-axios.ts'
import type { CommonRespData } from '@/service/service-types.ts'
import { createEventHook } from '@vueuse/core'
import type { FileTransferInfo } from '@/view/file-transfer/file-transfer-upload.ts'

export const useUploadDemo = () => {

  const uploadHook = createEventHook<FileTransferInfo>()

  const errorHook = createEventHook<undefined>()

  const {execute, isLoading, error} = useAxios<CommonRespData<FileTransferInfo>>(
    '/nls/web/vod/upload-demo',
    { method: 'GET' },
    serviceAxios,
  )

  const uploadDemo = async () => {
    const resp = await execute()
    if (!resp.data.value?.success) {
      return await triggerError(error.value)
    }

    const demo = resp.data.value.content!
    await uploadHook.trigger(demo)
    // const demoFileTransferInfo: FileTransferInfo = {
    //   name: demo.name,
    //   percent: 100,
    //   amount: demo.amount,
    //   lang: demo.lang,
    //   fileSign: demo.fileSign,
    //   audio: demo.audio,
    //   vod: demo.vod,
    //   channel: 'A',
    // }
  }

  const onError = errorHook.on
  const triggerError = errorHook.trigger

  return {
    isLoading,
    uploadDemo,
    onUploaded: uploadHook.on,
    onError,
  }
}