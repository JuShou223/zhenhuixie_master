/*
 * @Date: 2026-04-13 20:50:06
 * @LastEditors: 徐一鸣
 * @LastEditTime: 2026-04-15 16:04:05
 * @Description:
 */
// 环境配置
// 修改 baseUrl 为你自己的后端地址即可

const CONFIG = {
  development: {
    baseUrl: "/prod-api",
  },
  production: {
    baseUrl: "/prod-api",
  },
};

export default CONFIG[process.env.NODE_ENV];
