import { ref } from 'vue'

export function useAliyunUpload() {
  const uploadInfoRef = ref()
  const uploadAuthRef = ref()
  const uploadAddressRef = ref()
  const videoIdRef = ref()

  const onUploadStartedCallback = ref()
  const onUploadSucceedCallback = ref()
  const onUploadFailedCallback = ref()
  const onUploadProgressCallback = ref()
  const onUploadTokenExpiredCallback = ref()
  const onUploadEndCallback = ref()

  const setUploadAuth = (uploadAuth: string, uploadAddress: string, videoId: String) => {
    uploadAuthRef.value = uploadAuth
    uploadAddressRef.value = uploadAddress
    videoIdRef.value = videoId
  }
  const setOnUploadStarted = (callback: (fileUrl: string) => void) => onUploadStartedCallback.value = callback
  const setOnUploadSucceed = (callback: (fileUrl: string) => void) => onUploadSucceedCallback.value = callback
  const setOnUploadFailed = (callback: () => void) => onUploadFailedCallback.value = callback
  const setOnUploadProgress = (callback: (loadPercent: number) => void) => onUploadProgressCallback.value = callback
  const setOnUploadTokenExpired = (callback: () => void) => onUploadTokenExpiredCallback.value = callback
  const setOnUploadEnd = (callback: (uploadInfo: any) => void) => onUploadEndCallback.value = callback

  const uploader =  new AliyunUpload.Vod({
    //userID，必填，只需有值即可。
    userId:"122",
    //分片大小默认1 MB (1048576)，不能小于100 KB
    partSize: 104858,
    //并行上传分片个数，默认5
    parallel: 5,
    //网络原因失败时，重新上传次数，默认为3
    retryCount: 3,
    //网络原因失败时，重新上传间隔时间，默认为2秒
    retryDuration: 2,
    //是否上报上传日志到视频点播，默认为true
    enableUploadProgress: true,
    //开始上传
    onUploadstarted: function(uploadInfo: any) {
      uploadInfoRef.value = uploadInfo
      uploader.setUploadAuthAndAddress(uploadInfo, uploadAuthRef.value, uploadAddressRef.value, videoIdRef.value)
      console.log("文件上传开始:" + uploadInfo.file.name + ", endpoint:" + uploadInfo.endpoint + ", bucket:" + uploadInfo.bucket + ", object:" + uploadInfo.object);
      //从视频点播服务获取的uploadAuth、uploadAddress和videoId，设置到SDK里
      uploader.setUploadAuthAndAddress(uploadInfo, uploadAuthRef.value, uploadAddressRef.value, videoIdRef.value);
      onUploadStartedCallback.value?.call()
    },
    //文件上传成功
    onUploadSucceed: function(uploadInfo: any) {
      console.log("文件上传成功: " + uploadInfo.file.name + ", endpoint:" + uploadInfo.endpoint + ", bucket:" + uploadInfo.bucket + ", object:" + uploadInfo.object);
      const fileUrl = uploadInfo.endpoint.replace("https://", "https://" + uploadInfo.bucket + ".") + "/" + uploadInfo.object;
      console.log("文件地址: " + fileUrl);
      onUploadSucceedCallback.value?.call(null, fileUrl)
    },
    //文件上传失败
    onUploadFailed: function(uploadInfo: any, code: string, message: string) {
      console.log("文件上传失败: file:" + uploadInfo.file.name + ",code:" + code + ", message:" + message);
      onUploadFailedCallback.value?.call(null)
    },
    //文件上传进度，单位：字节
    onUploadProgress: function(uploadInfo: any, totalSize: number, loadedPercent: number) {
      console.log("文件上传中 :file:" + uploadInfo.file.name + ", fileSize:" + totalSize + ", percent:" + Math.ceil(loadedPercent * 100) + "%");
      // 进度条
      onUploadProgressCallback.value?.call(null, loadedPercent)
    },
    //上传凭证超时
    onUploadTokenExpired: function(uploadInfo: any) {
      console.log("onUploadTokenExpired");
      //实现时，根据uploadInfo.videoId调用刷新视频上传凭证接口重新获取UploadAuth
      //从点播服务刷新的uploadAuth，设置到SDK里

      uploader.resumeUploadWithAuth(uploadAuthRef);
      onUploadTokenExpiredCallback.value?.call(null)
    },
    //全部文件上传结束
    onUploadEnd: function(uploadInfo: any) {
      console.log("文件上传结束");
      // 上传结束后，清空上传控件里的值，否则多次选择同一个文件会不触发change事件
      onUploadEndCallback.value?.call(null, uploadInfo)
    }
  })

  return {
    uploader,
    setUploadAuth,
    setOnUploadStarted,
    setOnUploadSucceed,
    setOnUploadFailed,
    setOnUploadProgress,
    setOnUploadTokenExpired,
    setOnUploadEnd,
  }
}
