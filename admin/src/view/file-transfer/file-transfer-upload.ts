export type FileTransferInfo = {
  name: string
  percent: number
  amount: number
  lang: string
  audio: string
  fileSign: string
  vod: string
  channel: string
}

export type FileTransferUploadEmits = {
  (e: 'after-pay'): void
}