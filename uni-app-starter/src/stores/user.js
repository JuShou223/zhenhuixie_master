import { defineStore } from "pinia";

// 需要持久化到本地存储的字段
const PERSIST_KEYS = ["vuex_token", "profile", "isGuest"];

function readLifeData() {
  try {
    const raw = uni.getStorageSync("lifeData");
    if (!raw) return {};
    if (typeof raw === "string") return JSON.parse(raw);
    return raw;
  } catch {
    return {};
  }
}

function writeLifeData(data) {
  uni.setStorageSync("lifeData", JSON.stringify(data));
}

export const useUserStore = defineStore("user", {
  state: () => {
    const lifeData = readLifeData();
    return {
      vuex_token: lifeData.vuex_token || "",
      profile: lifeData.profile || {},
      isGuest: lifeData.isGuest || false,
      unreadCount: 0,
    };
  },
  getters: {
    isLoggedIn: (state) => !!state.vuex_token,
    // 已登录 或 游客 都算有效用户
    isValidUser: (state) => !!state.vuex_token || state.isGuest,
  },
  actions: {
    setToken(token) {
      this.vuex_token = token;
      if (token) this.isGuest = false;
    },
    setProfile(data) {
      this.profile = data;
    },
    enableGuestMode() {
      this.isGuest = true;
    },
    setUnreadCount(count) {
      this.unreadCount = count;
    },
    logout() {
      this.vuex_token = "";
      this.profile = {};
      this.isGuest = false;
      this.unreadCount = 0;
    },
  },
});

// 在 main.js 里调用一次，之后 store 变化自动同步到本地存储
export function setupUserStorePersistence(store) {
  store.$subscribe((mutation, state) => {
    const tmp = readLifeData();
    PERSIST_KEYS.forEach((key) => {
      if (state[key] !== undefined) tmp[key] = state[key];
    });
    writeLifeData(tmp);
  });
}
