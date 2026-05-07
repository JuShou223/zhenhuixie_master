import { defineStore } from 'pinia'

// 需要持久化到本地存储的字段
const PERSIST_KEYS: (keyof LifeData)[] = ['vuex_token', 'profile', 'isGuest']

interface LifeData {
  vuex_token?: string
  profile?: Record<string, unknown>
  isGuest?: boolean
}

function readLifeData(): Record<string, unknown> {
  try {
    const raw = uni.getStorageSync('lifeData')
    if (!raw) return {}
    if (typeof raw === 'string') return JSON.parse(raw)
    return raw as Record<string, unknown>
  } catch {
    return {}
  }
}

function writeLifeData(data: Record<string, unknown> | LifeData): void {
  uni.setStorageSync('lifeData', JSON.stringify(data))
}

export const useUserStore = defineStore('user', {
  state: () => {
    const lifeData = readLifeData()
    return {
      vuex_token: (lifeData.vuex_token as string) || '',
      profile: (lifeData.profile as Record<string, unknown>) || {},
      isGuest: lifeData.isGuest || false,
      unreadCount: 0,
    }
  },
  getters: {
    isLoggedIn: (state) => !!state.vuex_token,
    isValidUser: (state) => !!state.vuex_token || state.isGuest,
  },
  actions: {
    setToken(token: string) {
      this.vuex_token = token
      if (token) this.isGuest = false
    },
    setProfile(data: Record<string, unknown>) {
      this.profile = data
    },
    enableGuestMode() {
      this.isGuest = true
    },
    setUnreadCount(count: number) {
      this.unreadCount = count
    },
    logout() {
      this.vuex_token = ''
      this.profile = {}
      this.isGuest = false
      this.unreadCount = 0
    },
  },
})

export function setupUserStorePersistence(store: ReturnType<typeof useUserStore>): void {
  store.$subscribe((_mutation, state) => {
    const tmp: Record<string, unknown> = readLifeData()
    PERSIST_KEYS.forEach((key) => {
      if (state[key] !== undefined) tmp[key] = state[key]
    })
    writeLifeData(tmp)
  })
}
