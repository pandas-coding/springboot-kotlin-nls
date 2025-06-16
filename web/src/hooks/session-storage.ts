
export function useSessionStorage () {

  const getSessionItem = (key: string) => {
    const v = sessionStorage.getItem(key)
    if (v && typeof v !== 'undefined' && v !== 'undefined') {
      return JSON.parse(v)
    }
  }

  const setSessionItem = (key: string, data: any) => {
    sessionStorage.setItem(key, JSON.stringify(data))
  }

  const removeSessionItem = (key: string) => {
    sessionStorage.removeItem(key)
  }

  const clearAllSession = () => {
    sessionStorage.clear()
  }

  return {
    getSessionItem,
    setSessionItem,
    removeSessionItem,
    clearAllSession,
  }
}