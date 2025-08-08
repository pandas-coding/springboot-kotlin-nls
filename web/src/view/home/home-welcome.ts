
export interface Statistic {
  onLineCount: number
  registerCount: number
  fileTransferCount: number
  fileTransferSecond: number
  orderCount: number
  orderAmount: number
  registerCountList: StatisticDate[]
  fileTransferCountList: StatisticDate[]
  fileTransferSecondList: StatisticDate[]
  orderCountList: StatisticDate[]
  orderAmountList: StatisticDate[]
}

export interface StatisticDate {
  date: string
  num: number
}