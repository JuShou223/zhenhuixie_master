import { ref, type Ref } from 'vue'
import type { TabbarItem } from '@/typings/models'

export const TABBAR_LIST: TabbarItem[] = [
  {
    label: '首页',
    icon: 'icon-lucide-layout-grid',
    value: 'home',
    path: '/pages/index/index',
  },
  {
    mode: 'midButton',
  },
  {
    label: '我的',
    icon: 'icon-lucide-user',
    value: 'my',
    path: '/pages/my/index',
  },
]

export function useTabbar(): {
  activeTabbar: Ref<string>
  handleSetTabbar: (value: string) => void
} {
  const activeTabbar = ref('home')

  const handleSetTabbar = (value: string): void => {
    activeTabbar.value = value
  }

  return {
    activeTabbar,
    handleSetTabbar,
  }
}
