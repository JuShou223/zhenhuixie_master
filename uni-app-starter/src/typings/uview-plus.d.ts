// uview-plus 的 uni.$u.http 类型声明

interface UViewHttpConfig {
  baseURL?: string
  header?: Record<string, string>
  dataType?: string
  responseType?: string
  timeout?: number
  custom?: {
    showLoading?: boolean
    loadingText?: string
  }
  withCredentials?: boolean
  validateStatus?: (statusCode: number) => boolean
  url?: string
  method?: string
  data?: unknown
  params?: Record<string, unknown>
  [key: string]: unknown
}

interface UViewHttpResponse<T = unknown> {
  config: UViewHttpConfig
  data: T
  statusCode: number
  errMsg: string
  headers: Record<string, string>
}

interface UViewHttpInterceptorRequest {
  use(
    onFulfilled?: (config: UViewHttpConfig) => UViewHttpConfig | Promise<UViewHttpConfig>,
    onRejected?: (error: unknown) => unknown
  ): void
}

interface UViewHttpInterceptorResponse {
  use(
    onFulfilled?: (response: UViewHttpResponse) => UViewHttpResponse | Promise<UViewHttpResponse>,
    onRejected?: (error: unknown) => unknown
  ): void
}

interface UViewHttp {
  setConfig(handler: (config: UViewHttpConfig) => UViewHttpConfig): void
  get<T = unknown>(url: string, config?: Partial<UViewHttpConfig>): Promise<UViewHttpResponse<T>>
  post<T = unknown>(url: string, data?: unknown, config?: Partial<UViewHttpConfig>): Promise<UViewHttpResponse<T>>
  put<T = unknown>(url: string, data?: unknown, config?: Partial<UViewHttpConfig>): Promise<UViewHttpResponse<T>>
  delete<T = unknown>(url: string, config?: Partial<UViewHttpConfig>): Promise<UViewHttpResponse<T>>
  upload<T = unknown>(url: string, config?: Partial<UViewHttpConfig> & {
    filePath: string
    name: string
  }): Promise<UViewHttpResponse<T>>
  interceptors: {
    request: UViewHttpInterceptorRequest
    response: UViewHttpInterceptorResponse
  }
}

// 扩展 Uni 接口，添加 $u 属性
declare global {
  interface Uni {
    $u: {
      http: UViewHttp
    }
  }
}

export {}
