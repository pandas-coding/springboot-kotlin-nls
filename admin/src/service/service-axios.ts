import axios, {type AxiosResponse} from 'axios'
import type {CommonRespData} from '@/service/service-types.ts'
import router from '@/router'
import { useUserStore } from '@/stores/user-store.ts'

const service = axios.create({
  baseURL: import.meta.env.VITE_SERVER,
})

service.interceptors.request.use(
  (config) => {
    console.info('请求参数: %o', config)

    const userToken = useUserStore().user?.token
    // 如果登陆过, 才有token
    if (userToken) {
      config.headers.token = userToken
      console.info('请求headers添加token: %o', userToken)
    }

    return config
  },
  (err) => {
    console.info('请求错误: %o', err)
    return Promise.reject(err)
  },
)

service.interceptors.response.use(
  <V, D = any>(response: AxiosResponse<CommonRespData<V>, D>) => {
    console.info('返回结果: %o', response)
    return response
  },
  (err) => {
    console.info('返回错误: %o', err)
    const response = err.response
    const status = response.status
    if (status === 401) {
      console.info('未登录, 即将跳转登录页面...')
      router.push('/login')
    }
    return Promise.reject(err)
  }
)

export default service