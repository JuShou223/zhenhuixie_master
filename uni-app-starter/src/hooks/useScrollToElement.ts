import { ref, type Ref } from 'vue'

interface ScrollOptions {
  duration?: number
  viewportThreshold?: number
}

interface ScrollRect {
  top: number
  bottom: number
  left: number
  right: number
  width: number
  height: number
}

interface ScrollResult {
  scrollTop: number
  scrollOffset: number
}

interface SmartScrollResult {
  needScroll: boolean
  scrollInfo?: ScrollResult
  rect: ScrollRect | null
}

export function useScrollToElement(options: ScrollOptions = {}) {
  const { duration = 300, viewportThreshold = 0.25 } = options

  const windowInfo = uni.getSystemInfoSync()
  const isScrolling: Ref<boolean> = ref(false)

  const isElementInViewport = (rect: ScrollRect | null): boolean => {
    if (!rect) return false
    const elementCenter = rect.top + rect.height / 2
    const screenCenter = windowInfo.windowHeight / 2
    const distance = Math.abs(elementCenter - screenCenter)
    return distance < windowInfo.windowHeight * viewportThreshold
  }

  const scrollToElement = (rect: ScrollRect): Promise<ScrollResult> => {
    return new Promise((resolve, reject) => {
      if (!rect) {
        reject(new Error('无效的元素位置信息'))
        return
      }

      uni
        .createSelectorQuery()
        .selectViewport()
        .scrollOffset((res: UniApp.NodeInfo) => {
          const elementCenter = rect.top + rect.height / 2
          const screenCenter = windowInfo.windowHeight / 2
          const scrollOffset = elementCenter - screenCenter
          const currentScrollTop = (res.scrollTop as number) || 0

          let targetScrollTop = currentScrollTop + scrollOffset

          const scrollHeight = (res.scrollHeight as number) || 0
          const maxScrollTop = Math.max(0, scrollHeight - windowInfo.windowHeight)
          targetScrollTop = Math.max(0, Math.min(targetScrollTop, maxScrollTop))

          isScrolling.value = true

          uni.pageScrollTo({
            scrollTop: targetScrollTop,
            duration,
            success: () => {
              setTimeout(() => {
                isScrolling.value = false
                resolve({
                  scrollTop: targetScrollTop,
                  scrollOffset: targetScrollTop - currentScrollTop,
                })
              }, 100)
            },
            fail: (err: unknown) => {
              isScrolling.value = false
              reject(err)
            },
          })
        })
        .exec()
    })
  }

  const scrollToElementById = (elementId: string): Promise<ScrollResult> => {
    return new Promise((resolve, reject) => {
      if (!elementId) {
        reject(new Error('元素 ID 不能为空'))
        return
      }

      const query = uni.createSelectorQuery()
      query
        .select('#' + elementId)
        .boundingClientRect((data: UniApp.NodeInfo | null) => {
          if (data && (data as ScrollRect).width > 0) {
            scrollToElement(data as ScrollRect).then(resolve).catch(reject)
          } else {
            reject(new Error(`未找到元素: #${elementId}`))
          }
        })
        .exec()
    })
  }

  const smartScroll = (target: string | ScrollRect): Promise<SmartScrollResult> => {
    return new Promise((resolve, reject) => {
      const handleRect = (rect: ScrollRect | null) => {
        if (!rect || rect.width === 0) {
          reject(new Error('无效的元素'))
          return
        }

        const needScroll = !isElementInViewport(rect)

        if (needScroll) {
          scrollToElement(rect)
            .then((scrollInfo) => {
              resolve({ needScroll: true, scrollInfo, rect })
            })
            .catch(reject)
        } else {
          resolve({ needScroll: false, rect })
        }
      }

      if (typeof target === 'string') {
        const query = uni.createSelectorQuery()
        query
          .select('#' + target)
          .boundingClientRect((data: UniApp.NodeInfo | null) => {
            handleRect(data as ScrollRect | null)
          })
          .exec()
      } else {
        handleRect(target)
      }
    })
  }

  return {
    isScrolling,
    isElementInViewport,
    scrollToElement,
    scrollToElementById,
    smartScroll,
  }
}
