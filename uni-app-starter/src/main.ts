import { createSSRApp } from 'vue'
import * as Pinia from 'pinia'
import App from './App.vue'
import './tailwind.css'

import uviewPlus from 'uview-plus'
import bus from '@/common/bus'
import tools from '@/common/tools'
import '@/common/extend'

import httpInterceptor from '@/apis/http.interceptor'
import httpApi from '@/apis/http.api'

import { useUserStore, setupUserStorePersistence } from '@/stores/user'

// ── 安卓 MQTT 修复（不用 MQTT 可删掉这段）──────────────────
// #ifdef APP-PLUS
if (uni.getSystemInfoSync().platform === 'android') {
  const originalConnectSocket = uni.connectSocket
  uni.connectSocket = function (options: any) {
    options.complete = options.complete || function () {}
    return originalConnectSocket(options)
  }
  if (typeof globalThis !== 'undefined') (globalThis as any).wx = uni
  else if (typeof window !== 'undefined') (window as any).wx = uni
  else if (typeof global !== 'undefined') (global as any).wx = uni
}
// #endif
// ─────────────────────────────────────────────────────────────

export function createApp() {
  const app = createSSRApp(App)

  // #ifdef MP-WEIXIN
  app.mixin({
    onShareAppMessage() { return {} },
    onShareTimeline() { return {} },
  })
  // #endif

  const store = Pinia.createPinia()
  app.use(store)
  app.use(uviewPlus)

  app.config.globalProperties.$bus = bus
  app.use(tools)
  app.use(httpInterceptor)
  app.use(httpApi)

  const userStore = useUserStore(store)
  setupUserStorePersistence(userStore)

  return { app, Pinia }
}
