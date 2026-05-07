export function tansParams(params: Record<string, unknown>): string {
  let result = ''
  for (const key of Object.keys(params)) {
    const value = params[key]
    if (value === null || value === '' || typeof value === 'undefined') continue
    if (typeof value === 'object') {
      for (const subKey of Object.keys(value as Record<string, unknown>)) {
        const subVal = (value as Record<string, unknown>)[subKey]
        if (subVal === null || subVal === '' || typeof subVal === 'undefined') continue
        result += encodeURIComponent(`${key}[${subKey}]`) + '=' + encodeURIComponent(String(subVal)) + '&'
      }
    } else {
      result += encodeURIComponent(key) + '=' + encodeURIComponent(String(value)) + '&'
    }
  }
  return result
}

export function navigateBackTo(url: string): void {
  const backUrl = url.startsWith('/') ? url.slice(1) : url
  const pages = getCurrentPages()
  const index = pages.findIndex((p) => p.route === backUrl)
  if (index === -1) {
    uni.reLaunch({ url: '/' + backUrl })
    return
  }
  uni.navigateBack({ delta: pages.length - index - 1 })
}
