import type {AxiosRequestConfig} from 'axios'
import type {CommonRespData, RestServiceRespData} from '@/service/service-types.ts'
import service from '@/service/service-axios.ts'

const restService = {
  get: async <T = any, D = any>(url: string, config?: AxiosRequestConfig<D>): Promise<RestServiceRespData<T, D>> => {
    const response = await service.get(url, config)
    const responseData: CommonRespData<T> = response.data
    return {
      ...responseData,
      axiosResponse: response,
    }
  },
  post: async <T = any, D = any>(url: string, data?: D, config?: AxiosRequestConfig<D>): Promise<RestServiceRespData<T, D>> => {
    const response = await service.post(url, data, config)
    const responseData: CommonRespData<T> = response.data
    return {
      ...responseData,
      axiosResponse: response,
    }
  },
  put: async <T = any, D = any>(url: string, data?: D, config?: AxiosRequestConfig<D>): Promise<RestServiceRespData<T, D>> => {
    const response = await service.put(url, data, config)
    const responseData: CommonRespData<T> = response.data
    return {
      ...responseData,
      axiosResponse: response,
    }
  },
  delete: async <T = any, D = any>(url: string, config?: AxiosRequestConfig<D>): Promise<RestServiceRespData<T, D>> => {
    const response = await service.delete(url, config)
    const responseData: CommonRespData<T> = response.data
    return {
      ...responseData,
      axiosResponse: response,
    }
  }
}

export default restService
