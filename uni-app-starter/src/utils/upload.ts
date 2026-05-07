import config from '@/env.config'
import { tansParams } from './common'

const baseUrl = config.baseUrl

interface UploadOptions {
  url: string
  filePath: string
  name?: string
  header?: Record<string, string>
  params?: Record<string, unknown>
  formData?: Record<string, unknown>
  timeout?: number
}

const upload = (options: UploadOptions): Promise<any> => {
  const header = options.header || {}

  const raw = uni.getStorageSync('lifeData')
  const lifeData: Record<string, unknown> = typeof raw === 'string' ? JSON.parse(raw) : (raw || {})
  const token = (lifeData.vuex_token as string) || ''
  if (token && header['isToken'] !== 'false') {
    header['Authorization'] = 'Bearer ' + token
  }

  let url = baseUrl + options.url
  if (options.params) {
    url = url + '?' + tansParams(options.params).slice(0, -1)
  }

  return new Promise((resolve, reject) => {
    uni.uploadFile({
      timeout: options.timeout || 30000,
      url,
      filePath: options.filePath,
      name: options.name || 'file',
      header,
      formData: options.formData,
      success: (res) => {
        let result: Record<string, unknown> = {}
        try {
          result = JSON.parse(res.data)
        } catch (e) {
          reject('JSON 解析失败')
          return
        }

        if (result.code === 200) {
          resolve(result)
        } else if (result.code === 401) {
          uni.showToast({ title: '登录已过期，请重新登录', icon: 'none', duration: 1500 })
          setTimeout(() => uni.reLaunch({ url: '/pages/login/index' }), 1500)
          reject('登录已过期')
        } else {
          uni.showToast({ title: (result.msg as string) || '上传失败', icon: 'none' })
          reject(result.msg)
        }
      },
      fail: (err) => {
        uni.showToast({ title: '上传失败，请检查网络', icon: 'none' })
        reject(err)
      },
    })
  })
}

export default upload
