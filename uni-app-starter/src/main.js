import { createSSRApp } from "vue";
import * as Pinia from "pinia";
import App from "./App.vue";
import "./tailwind.css";

import uviewPlus from "uview-plus";
import bus from "@/common/bus.js";
import tools from "@/common/tools.js";
import "@/common/extend.js";

import httpInterceptor from "@/apis/http.interceptor.js";
import httpApi from "@/apis/http.api.js";

import { useUserStore, setupUserStorePersistence } from "@/stores/user.js";

// ── 安卓 MQTT 修复（不用 MQTT 可删掉这段）──────────────────
// #ifdef APP-PLUS
if (uni.getSystemInfoSync().platform === "android") {
  const originalConnectSocket = uni.connectSocket;
  uni.connectSocket = function (options) {
    options.complete = options.complete || function () {};
    return originalConnectSocket(options);
  };
  if (typeof globalThis !== "undefined") globalThis.wx = uni;
  else if (typeof window !== "undefined") window.wx = uni;
  else if (typeof global !== "undefined") global.wx = uni;
}
// #endif
// ─────────────────────────────────────────────────────────────

export function createApp() {
  const app = createSSRApp(App);

  // #ifdef MP-WEIXIN
  app.mixin({
    onShareAppMessage() { return {}; },
    onShareTimeline() { return {}; },
  });
  // #endif

  const store = Pinia.createPinia();
  app.use(store);
  app.use(uviewPlus);

  app.config.globalProperties.$bus = bus;
  app.use(tools);
  app.use(httpInterceptor);
  app.use(httpApi);

  const userStore = useUserStore(store);
  setupUserStorePersistence(userStore);

  return { app, Pinia };
}
