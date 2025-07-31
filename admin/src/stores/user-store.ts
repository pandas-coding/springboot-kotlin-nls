import { defineStore } from 'pinia'
import { useSessionStorage } from '@vueuse/core'
import type { SessionUser } from '@/stores/types/member-store-types.ts'

const STORAGE_KEY = 'user'

// 用户的信息
export const useUserStore = defineStore('user-store', () => {
  const user = useSessionStorage<SessionUser | null>(STORAGE_KEY, null)

  const setUser = (newUser: SessionUser) => {
    user.value = newUser
  }

  return {
    user,
    setUser,
  }
})