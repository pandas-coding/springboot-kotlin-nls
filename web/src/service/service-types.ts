import type {AxiosResponse} from 'axios'

export interface CommonRespData<T = any> { // T 是实际数据的类型
  success: boolean
  message?: string
  content?: T
}

export interface RestServiceRespData<T, D> extends CommonRespData<T> {
  success: boolean
  message?: string
  content?: T
  axiosResponse: AxiosResponse<CommonRespData<T>, D>
}
