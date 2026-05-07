<!--
 * @Date: 2026-05-06 12:52:49
 * @LastEditors: 徐一鸣
 * @LastEditTime: 2026-05-07 15:47:04
 * @Description:
-->
<template>
  <view>
    <!-- Placeholder -->
    <view class="box-content" :style="{ height: barHeight, paddingBottom: safeBottom }"></view>

    <!-- TabBar -->
    <view class="box-content fixed bottom-0 left-0 right-0 z-40 bg-white/95 backdrop-blur-xl border-t pb-safe"
      :style="{ height: barHeight, paddingBottom: safeBottom, borderColor: '#E8E3DC' }">
      <view class="flex justify-around items-center relative h-full">
        <template v-for="(tab, idx) in TABBAR_LIST" :key="idx">
          <!-- Middle create button -->
          <view v-if="tab.mode === 'midButton'" class="relative -top-3 flex-1 flex justify-center">
            <view id="guide-create-button"
              class="guide-create-button w-12 h-12 bg-accent rounded-full flex items-center shadow-fab justify-center border-4 border-white active:scale-90 transition-transform duration-200"
              @tap="goCreate">
              <text class="iconfont icon-lucide-plus text-white" style="font-size: 22px; font-weight: bold;"></text>
            </view>
          </view>

          <!-- Normal tab -->
          <view v-else
            class="flex flex-col items-center flex-1 h-full justify-center relative active:scale-95 transition-transform duration-200"
            @tap="handleTabBarClick(tab)">
            <!-- Active indicator bar -->
            <view v-if="activeTab === tab.value"
              class="absolute top-0 left-1/2 -translate-x-1/2 rounded-full bg-accent transition-all duration-250"
              style="width: 20px; height: 3px;"></view>

            <view class="relative">
              <text :class="[tab.icon, activeTab === tab.value ? 'text-accent' : 'text-muted']"
                class="iconfont text-xl leading-none transition-colors duration-200"></text>
              <view
                v-if="tab.value === 'my' && unreadCount > 0"
                class="absolute flex items-center justify-center rounded-full bg-red-500 text-white font-bold"
                style="top: -4px; right: -7px; min-width: 16px; height: 16px; font-size: 10px; font-family: 'Outfit', sans-serif; padding: 0 3px; border: 1.5px solid white;"
              >{{ unreadCount > 99 ? '99+' : unreadCount }}</view>
            </view>
            <text class="text-[11px] font-semibold mt-0.5 transition-colors duration-200"
              :class="activeTab === tab.value ? 'text-accent' : 'text-muted'"
              style="font-family: 'Outfit', -apple-system, sans-serif;">{{ tab.label }}</text>
          </view>
        </template>
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, getCurrentInstance } from 'vue'
import { onShow } from '@dcloudio/uni-app'
import { TABBAR_LIST, useTabbar } from '@/hooks/useTabbar'
import { useUserStore } from '@/stores/user'
import { getUnreadBadge } from '@/apis/modules/notification'
import domQueryService from '@/common/domQueryService';
const instance = getCurrentInstance()

const props = defineProps({
  defaultTab: { type: String, default: 'home' },
})

const userStore = useUserStore()
const { activeTabbar, handleSetTabbar } = useTabbar()
const barHeight = ref('50px')
const safeBottom = ref('0px')

const activeTab = computed(() => activeTabbar.value || props.defaultTab)
const unreadCount = computed(() => userStore.unreadCount)

async function refreshBadge() {
  if (!userStore.isLoggedIn) return
  const res = await getUnreadBadge().catch(() => null)
  if (res && res.data) userStore.setUnreadCount(res.data.interact || 0)
}

onShow(() => { refreshBadge() })

function handleTabBarClick(tab) {
  handleSetTabbar(tab.value)
  uni.switchTab({ url: tab.path })
}

function goCreate() {
  uni.navigateTo({ url: '/pages/story/create' })
}

onMounted(() => {
  domQueryService.registerHandler('tabbar', instance as any);

  handleSetTabbar(props.defaultTab)
  try {
    const sys = uni.getSystemInfoSync()
    if (sys.safeAreaInsets?.bottom) {
      safeBottom.value = sys.safeAreaInsets.bottom + 'px'
    }
  } catch { }
})
</script>
