// 确保 uni 全局 API 在 Vue 模板中被 vue-tsc 识别
// 此文件不包含 import/export，以 ambient script 模式生效
declare const uni: any

// 增强 PageInstance 以支持 .options 属性（getCurrentPages()[x].options 读取页面参数）
declare namespace Page {
  interface PageInstance<D extends AnyObject = AnyObject, T extends AnyObject = AnyObject> {
    options: Record<string, string>
  }
}

interface AnyObject {
  [key: string]: any
}
