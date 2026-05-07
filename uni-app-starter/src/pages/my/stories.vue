<template>
  <view class="min-h-screen bg-bg flex flex-col" style="padding-bottom: env(safe-area-inset-bottom, 0px);">
    <view id="scroll-header" class="flex items-center justify-between px-4 sticky top-0 z-10 bg-bg/95 backdrop-blur-xl"
      style="padding-top: calc(var(--status-bar-height) + 12px); padding-bottom: 14px; border-bottom: 1px solid #E8E3DC;">
      <view class="w-8 h-8 rounded-full flex items-center justify-center active:scale-90 transition-transform duration-200"
        style="background: rgba(0,0,0,0.05);" @tap="uni.navigateBack()">
        <text class="iconfont icon-lucide-chevron-left text-ink" style="font-size: 18px; font-weight: bold;"></text>
      </view>
      <text class="text-[17px] font-bold text-ink" style="font-family: 'Outfit', 'PingFang SC', sans-serif;">我发布的故事</text>
      <view style="width: 32px;"></view>
    </view>

    <scroll-view scroll-y :style="{ height: scrollHeight + 'px' }">
      <view class="px-4 pt-3 flex flex-col gap-3">
        <view v-for="story in list" :key="story.storyId"
          class="bg-white rounded-2xl shadow-card overflow-hidden active:scale-[0.98] transition-transform duration-200"
          @tap="goDetail(story.storyId)">
          <view class="flex p-3">
            <view class="bg-gray-200 flex-shrink-0 mr-3 rounded-xl overflow-hidden relative"
              style="width: 80px; height: 100px;">
              <image v-if="story.cover" :src="story.cover" class="w-full h-full" mode="aspectFill" />
              <text v-else class="iconfont icon-lucide-image text-xl text-gray-300 absolute top-1/2 left-1/2 -translate-x-1/2 -translate-y-1/2"></text>
            </view>
            <view class="flex-1 flex flex-col min-w-0">
              <text class="text-[15px] font-bold text-ink leading-snug line-clamp-2 mb-1.5"
                style="font-family: 'Outfit', 'PingFang SC', sans-serif;">{{ story.title }}</text>
              <text class="text-xs text-muted leading-relaxed line-clamp-2 mb-auto">{{ story.settingContent }}</text>
              <view class="flex items-center gap-4 mt-2 pt-2 border-t border-line">
                <view class="flex items-center gap-1 text-xs text-muted">
                  <text class="iconfont icon-lucide-eye text-xs"></text>
                  <text style="font-variant-numeric: tabular-nums;">{{ story.viewCount || 0 }}</text>
                </view>
                <view class="flex items-center gap-1 text-xs text-muted">
                  <text class="iconfont icon-lucide-layers text-xs"></text>
                  <text style="font-variant-numeric: tabular-nums;">{{ story.branchCount || 0 }}</text>
                </view>
                <view class="flex items-center gap-1 text-xs text-muted">
                  <text class="iconfont icon-lucide-heart text-xs"></text>
                  <text style="font-variant-numeric: tabular-nums;">{{ story.likeCount || 0 }}</text>
                </view>
              </view>
            </view>
          </view>
        </view>

        <view v-if="list.length === 0 && !loading" class="flex flex-col items-center justify-center py-20">
          <text class="iconfont icon-lucide-pencil-line text-5xl text-gray-300 mb-3"></text>
          <text class="text-sm text-muted">还没有发布故事</text>
        </view>
        <view v-if="loading" class="text-center py-6">
          <text class="text-sm text-muted">加载中...</text>
        </view>
      </view>
      <view class="h-4"></view>
    </scroll-view>
  </view>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useScrollHeight } from '@/utils/useScrollHeight'
import { getMyStories } from '@/apis/modules/user'

const { scrollHeight } = useScrollHeight('#scroll-header')
const list = ref([])
const loading = ref(false)

async function fetchList() {
  loading.value = true
  try {
    const res = await getMyStories()
    if (res && res.data) list.value = res.data
  } finally {
    loading.value = false
  }
}

function goDetail(storyId) {
  uni.navigateTo({ url: `/pages/story/detail?storyId=${storyId}` })
}

onMounted(fetchList)
</script>
