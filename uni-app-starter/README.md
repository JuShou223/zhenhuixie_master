# uni-app-starter

开箱即用的 UniApp 项目模板，支持微信小程序 / H5 / App 三端。

## 技术栈

- **UniApp + Vue 3** — 跨端框架
- **Tailwind CSS + weapp-tailwindcss** — 原子化样式，小程序可用
- **uview-plus** — UI 组件库
- **Pinia** — 状态管理（含本地持久化）
- **uview-plus http** — 请求封装

## 快速开始

```bash
npm install

# 微信小程序
npm run dev:mp-weixin

# H5
npm run dev:h5

# App
npm run dev:app
```

## 第一步：改域名

修改 `src/env.config.js`：

```js
development: {
  baseUrl: "http://localhost:8080",  // 改成你本地后端地址
},
production: {
  baseUrl: "https://your-domain.com/prod-api",  // 改成线上地址
},
```

## 目录说明

```
src/
├── apis/
│   ├── http.interceptor.js   # 请求拦截器（Token注入、401处理）
│   ├── http.api.js           # API模块统一注册
│   └── modules/
│       └── common.js         # 通用接口（登录、上传等），按需增删
├── common/
│   ├── bus.js                # 事件总线（mitt）
│   ├── extend.js             # Date.Format 扩展
│   └── tools.js              # 全局工具方法（$t.parseTime等）
├── stores/
│   └── user.js               # 用户状态 + Token持久化
├── utils/
│   ├── upload.js             # 文件上传封装
│   ├── common.js             # tansParams等工具函数
│   └── image2Base64.js       # 图片转base64（三端兼容）
├── pages/
│   └── index/index.vue       # 示例首页
├── env.config.js             # 环境配置（开发/生产baseUrl）
├── main.js                   # 入口，注册插件
└── pages.json                # 页面路由配置
```

## 新增接口

在 `src/apis/modules/` 下新建文件，例如 `order.js`：

```js
export function getOrderList(params) {
  return uni.$u.http.get("/order/list", { params });
}
```

然后在 `http.api.js` 里注册（可选，也可直接按需 import）：

```js
import * as order from "./modules/order.js";
app.config.globalProperties.$api = { common, order };
```

## 请求示例

```js
// 方式1：直接 import（推荐）
import { getOrderList } from '@/apis/modules/order.js'
const res = await getOrderList({ pageNum: 1, pageSize: 10 })
if (res) console.log(res.rows)

// 方式2：通过 $api 全局对象
const res = await this.$api.common.login({ username, password })
```

## 文件上传示例

```js
import upload from '@/utils/upload.js'

const res = await upload({
  url: '/common/upload',
  filePath: tempFilePath,
})
if (res.code === 200) {
  const fileUrl = res.fileName
}
```
