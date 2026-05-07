import projectConfig from "@/env.config.js";
import { useUserStore } from "@/stores/user.js";

// 不需要携带 Token 的接口白名单
const NO_TOKEN_URLS = ["/captchaImage", "/login", "/zhw/login", "/zhw/register"];

const install = (app) => {
  uni.$u.http.setConfig((config) => {
    config.baseURL = projectConfig.baseUrl;
    config.header = {
      "Content-Type": config.header?.["Content-Type"] || "application/json",
    };
    config.dataType = "json";
    config.responseType = "text";
    config.timeout = 60000;
    config.custom = {
      showLoading: false,
      loadingText: "正在加载",
    };
    // #ifdef H5
    config.withCredentials = false;
    // #endif
    config.validateStatus = (statusCode) => statusCode >= 200 && statusCode < 300;
    return config;
  });

  // 请求拦截：自动注入 Token
  uni.$u.http.interceptors.request.use(
    (config) => {
      if (config.custom?.showLoading) {
        uni.showLoading({ title: config.custom.loadingText || "正在加载", mask: true });
      }

      const isNoToken = NO_TOKEN_URLS.some((url) => config.url === url);
      if (!isNoToken) {
        const userStore = useUserStore();
        const token = userStore.vuex_token;
        if (token) {
          config.header.Authorization = "Bearer " + token;
        }
      }
      return config;
    },
    (error) => Promise.reject(error)
  );

  // 响应拦截：统一处理 401、错误提示
  uni.$u.http.interceptors.response.use(
    (res) => {
      if (res.config.custom?.showLoading) uni.hideLoading();

      if (res.statusCode === 200) {
        const result = res.data;

        if (result.code === 200) return result;

        if (result.code === 401) {
          const userStore = useUserStore();
          const wasLoggedIn = userStore.isLoggedIn;
          userStore.logout();
          if (wasLoggedIn) {
            uni.showToast({ title: '登录已过期，请重新登录', icon: 'none', duration: 1500 });
            setTimeout(() => uni.reLaunch({ url: '/pages/login/index' }), 1500);
          }
          return false;
        }

        // 其他业务错误
        uni.showToast({ title: result.msg || "请求失败", icon: "none", duration: 2000 });
        return false;
      }

      uni.showToast({ title: res.data?.msg || "网络错误", icon: "none" });
      return false;
    },
    (error) => {
      if (error.config?.custom?.showLoading) uni.hideLoading();
      const msg = error.errMsg || ''
      if (msg.includes('timeout') || msg.includes('超时')) {
        uni.showToast({ title: '请求超时，请重试', icon: 'none', duration: 2000 })
      } else if (msg.includes('fail') || !msg) {
        uni.showToast({ title: '网络异常，请检查连接', icon: 'none', duration: 2000 })
      } else {
        uni.showToast({ title: '请求失败，请稍后重试', icon: 'none', duration: 2000 })
      }
      return Promise.reject(error);
    }
  );
};

export default { install };
