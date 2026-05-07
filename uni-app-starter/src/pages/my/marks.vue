<template>
  <view class="min-h-screen bg-bg flex flex-col" style="padding-bottom: env(safe-area-inset-bottom, 0px);">
    <!-- Header -->
    <view id="scroll-header" class="flex items-center justify-between px-4 sticky top-0 z-10 bg-bg/95 backdrop-blur-xl"
      style="padding-top: calc(var(--status-bar-height) + 12px); padding-bottom: 14px; border-bottom: 1px solid #E8E3DC;">
      <view class="w-8 h-8 rounded-full flex items-center justify-center active:scale-90 transition-transform duration-200"
        style="background: rgba(0,0,0,0.05);" @tap="uni.navigateBack()">
        <text class="iconfont icon-lucide-chevron-left text-ink" style="font-size: 18px; font-weight: bold;"></text>
      </view>
      <text class="text-[17px] font-bold text-ink" style="font-family: 'Outfit', 'PingFang SC', sans-serif;">标记更新</text>
      <view style="width: 32px;"></view>
    </view>

    <scroll-view scroll-y :style="{ height: scrollHeight + 'px' }" refresher-enabled :refresher-triggered="refreshing" @refresherrefresh="onRefresh">
      <view v-if="list.length > 0" class="px-4 pt-3 flex flex-col gap-3">
        <view
          v-for="item in list"
          :key="item.markId"
          class="bg-white rounded-2xl shadow-card overflow-hidden active:scale-[0.98] transition-transform duration-200"
          @tap="goMarkUpdate(item)"
        >
          <view class="flex p-3">
            <!-- Cover -->
            <view class="bg-gray-200 flex-shrink-0 mr-3 rounded-xl overflow-hidden relative"
              style="width: 90px; height: 110px;">
              <image v-if="item.cover" :src="item.cover" class="w-full h-full" mode="aspectFill" />
              <text v-else class="iconfont icon-lucide-image text-2xl text-gray-300 absolute top-1/2 left-1/2 -translate-x-1/2 -translate-y-1/2"></text>
            </view>

            <!-- Info -->
            <view class="flex-1 flex flex-col min-w-0">
              <view class="flex items-start justify-between gap-2">
                <text class="text-[15px] font-bold text-ink leading-snug line-clamp-2 flex-1"
                  style="font-family: 'Outfit', 'PingFang SC', sans-serif;">{{ item.storyTitle }}</text>
                <view class="border border-accent text-accent-dark text-[11px] font-semibold px-2.5 py-1 rounded-full flex-shrink-0 active:bg-yellow-50 transition-colors duration-150"
                  style="font-family: 'Outfit', sans-serif;"
                  @tap.stop="handleUnmark(item)">
                  取订
                </view>
              </view>

              <view class="flex items-center gap-1.5 mt-2">
                <image :src="resolveAvatar(item.targetAuthorAvatar)" class="w-4 h-4 rounded-full" mode="aspectFill" />
                <text class="text-xs text-muted">{{ item.targetAuthorNickName }}</text>
              </view>

              <text v-if="item.chapterName" class="text-xs font-semibold text-ink mt-2 line-clamp-1"
                style="font-family: 'Outfit', sans-serif;">{{ item.chapterName }}</text>

              <text class="text-xs text-muted leading-relaxed line-clamp-2 mt-1">{{ item.targetContentPreview }}</text>

              <view v-if="item.hasUpdate" class="mt-auto pt-2">
                <text class="text-red-500 text-[11px] font-bold" style="font-family: 'Outfit', sans-serif;">
                  增加{{ item.newCount || 1 }}条新内容
                </text>
              </view>
            </view>
          </view>
        </view>
      </view>

      <view v-if="list.length === 0 && !loading" class="flex flex-col items-center justify-center py-20">
        <text class="iconfont icon-lucide-bookmark text-5xl text-gray-300 mb-3"></text>
        <text class="text-sm font-semibold text-ink mb-1">还没有标记任何内容</text>
        <text class="text-xs text-muted">去故事详情页点击"标记"，追踪后续更新</text>
      </view>
      <view v-if="loading" class="text-center py-6">
        <text class="text-sm text-muted">加载中...</text>
      </view>
      <view class="h-4"></view>
    </scroll-view>
  </view>
</template>

<script setup lang="ts">
import { resolveAvatar } from '@/utils/avatar'
import { ref, onMounted } from 'vue'
import { useScrollHeight } from '@/utils/useScrollHeight'
import { getMarkList, toggleMark } from '@/apis/modules/interaction'

const { scrollHeight } = useScrollHeight('#scroll-header')
const list = ref([])
const loading = ref(false)
const refreshing = ref(false)

async function fetchList() {
  loading.value = true
  try {
    const res = await getMarkList()
    if (res && res.data) list.value = res.data
  } finally {
    loading.value = false
    refreshing.value = false
  }
}

function onRefresh() {
  refreshing.value = true
  fetchList()
}

async function handleUnmark(mark) {
  const res = await toggleMark({ storyId: mark.storyId, branchId: mark.branchId || 0 })
  if (res) {
    list.value = list.value.filter(m => m.markId !== mark.markId)
  }
}

function goMarkUpdate(mark) {
  if (mark.chapterName) {
    uni.navigateTo({
      url: `/pages/my/mark-updates?storyId=${mark.storyId}&branchId=${mark.branchId}&markId=${mark.markId}`
    })
  } else {
    uni.navigateTo({ url: `/pages/story/detail?storyId=${mark.storyId}` })
  }
}

onMounted(fetchList)
</script>
