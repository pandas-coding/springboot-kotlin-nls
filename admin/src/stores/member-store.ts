import { defineStore } from "pinia";
import { ref } from "vue";
import { useSessionStorage } from "@/hooks/session-storage.ts";
import type { SessionMember } from "@/stores/types/member-store-types.ts";

const MEMBER = 'member'

export const useMemberStore = defineStore('member-store', () => {
  const { getSessionItem, setSessionItem } = useSessionStorage()

  const member = ref<SessionMember>(getSessionItem(MEMBER))

  const setMember = (newMember: SessionMember) => {
    member.value = newMember
    setSessionItem(MEMBER, newMember)
  }

  return {
    member,
    setMember,
  }
})