import { defineStore } from 'pinia'
import { useAxios } from '@vueuse/integrations/useAxios'
import { service } from '@/service'
import { ref } from 'vue'

/**
 * 用户登录后执行心跳请求的store
 */
export const useMemberLoginHeartStore = defineStore('member-login-heart-store', () => {
  const internal = ref()

  const startHeart = () => {
    internal.value = setInterval(heart, 5000)
  }

  const stopHeart = () => {
    clearInterval(internal.value)
  }

  const heart = () => {
    useAxios('/nls/web/member/heart', {
      method: 'GET',
    }, service)
  }

  return {
    startHeart,
    stopHeart,
  }
})