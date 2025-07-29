
export type PayInfo = {
  orderNo: string,
  qrcode: string,
  desc: string,
  amount: number,
}

export type AlipayModelEmits = {
  'after-pay': [payStatus: 'S' | 'F']
}