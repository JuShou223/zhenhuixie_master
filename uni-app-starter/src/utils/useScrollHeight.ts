import { ref, onMounted, type Ref } from 'vue'

export function useScrollHeight(aboveSelector: string | null, bottomOffset = 0): { scrollHeight: Ref<number> } {
  const scrollHeight = ref(0)

  onMounted(() => {
    const { windowHeight } = uni.getSystemInfoSync()
    if (!aboveSelector) {
      scrollHeight.value = windowHeight - bottomOffset
      return
    }
    uni.createSelectorQuery()
      .select(aboveSelector)
      .boundingClientRect((rect: UniApp.NodeInfo | null) => {
        scrollHeight.value = windowHeight - (rect ? rect.bottom : 0) - bottomOffset
      })
      .exec()
  })

  return { scrollHeight }
}
