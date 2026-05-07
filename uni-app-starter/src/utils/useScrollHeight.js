import { ref, onMounted } from 'vue'

/**
 * 计算 scroll-view 的可用高度，解决 flex-1 在 UniApp scroll-view 上不生效的问题。
 * @param {string|null} aboveSelector  scroll-view 上方最后一个固定元素的 CSS 选择器（如 '#scroll-header'），null 则不测量
 * @param {number} bottomOffset        底部固定 bar 高度（px），如底部操作栏 80
 */
export function useScrollHeight(aboveSelector, bottomOffset = 0) {
  const scrollHeight = ref(0)

  onMounted(() => {
    const { windowHeight } = uni.getSystemInfoSync()
    if (!aboveSelector) {
      scrollHeight.value = windowHeight - bottomOffset
      return
    }
    uni.createSelectorQuery()
      .select(aboveSelector)
      .boundingClientRect(rect => {
        scrollHeight.value = windowHeight - (rect ? rect.bottom : 0) - bottomOffset
      })
      .exec()
  })

  return { scrollHeight }
}
