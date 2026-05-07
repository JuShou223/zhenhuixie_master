import config from "@/env.config.js";
import { tansParams } from "./common.js";

const baseUrl = config.baseUrl;

/**
 * 文件上传封装
 * 用法：
 *   import upload from '@/utils/upload.js'
 *   const res = await upload({ url: '/common/upload', filePath: tempFilePath })
 *   if (res.code === 200) console.log(res.fileName)
 */
const upload = (options) => {
  const header = options.header || {};

  // 从本地存储读 Token（兼容 H5 返回字符串的情况）
  const raw = uni.getStorageSync("lifeData");
  const lifeData = typeof raw === "string" ? JSON.parse(raw) : (raw || {});
  const token = lifeData.vuex_token || "";
  if (token && header.isToken !== false) {
    header["Authorization"] = "Bearer " + token;
  }

  // 支持 params 转 query string
  let url = baseUrl + options.url;
  if (options.params) {
    url = url + "?" + tansParams(options.params).slice(0, -1);
  }

  return new Promise((resolve, reject) => {
    uni.uploadFile({
      timeout: options.timeout || 30000,
      url,
      filePath: options.filePath,
      name: options.name || "file",
      header,
      formData: options.formData,
      success: (res) => {
        let result = {};
        try {
          result = JSON.parse(res.data);
        } catch (e) {
          reject("JSON 解析失败");
          return;
        }

        if (result.code === 200) {
          resolve(result);
        } else if (result.code === 401) {
          uni.showToast({ title: "登录已过期，请重新登录", icon: "none", duration: 1500 });
          setTimeout(() => uni.reLaunch({ url: "/pages/login/index" }), 1500);
          reject("登录已过期");
        } else {
          uni.showToast({ title: result.msg || "上传失败", icon: "none" });
          reject(result.msg);
        }
      },
      fail: (err) => {
        uni.showToast({ title: "上传失败，请检查网络", icon: "none" });
        reject(err);
      },
    });
  });
};

export default upload;
