<template>
  <view class="min-h-screen bg-bg flex flex-col" style="padding-bottom: env(safe-area-inset-bottom, 0px);">
    <!-- Header -->
    <view id="scroll-header" class="flex items-center justify-between px-4 sticky top-0 z-10 bg-bg/95 backdrop-blur-xl"
      style="padding-top: calc(var(--status-bar-height) + 12px); padding-bottom: 14px; border-bottom: 1px solid #E8E3DC;">
      <view class="w-8 h-8 rounded-full flex items-center justify-center active:scale-90 transition-transform duration-200"
        style="background: rgba(0,0,0,0.05);" @tap="uni.navigateBack()">
        <text class="iconfont icon-lucide-chevron-left text-ink" style="font-size: 18px; font-weight: bold;"></text>
      </view>
      <text class="text-[17px] font-bold text-ink" style="font-family: 'Outfit', 'PingFang SC', sans-serif;">话题挑战</text>
      <view style="width: 32px;"></view>
    </view>

    <scroll-view scroll-y :style="{ height: scrollHeight + 'px' }" refresher-enabled :refresher-triggered="refreshing" @refresherrefresh="onRefresh">
      <view class="p-4 flex flex-col gap-3">
        <view
          v-for="topic in list"
          :key="topic.topicId"
          class="bg-white rounded-2xl shadow-card overflow-hidden active:scale-[0.98] transition-transform duration-200"
          @tap="goDetail(topic.topicId)"
        >
          <!-- Cover Banner -->
          <view class="bg-gray-200 relative" style="height: 140px;">
            <image v-if="topic.banner" :src="topic.banner" class="w-full h-full" mode="aspectFill" />
            <view v-else class="absolute inset-0 flex flex-col items-center justify-center">
              <text class="iconfont icon-lucide-image text-3xl text-gray-300 mb-1"></text>
              <text class="text-xs text-gray-400">暂无封面</text>
            </view>
            <!-- Status Badge -->
            <view class="absolute top-3 right-3 text-[10px] font-bold px-2.5 py-1 rounded-full"
              :style="topic.status === '0' ? 'background: rgba(26,26,26,0.75); color: #FFC107;' : 'background: rgba(140,138,132,0.6); color: #fff;'"
              style="backdrop-filter: blur(4px); font-family: 'Outfit', sans-serif;">
              {{ topic.status === '0' ? '进行中' : '已结束' }}
            </view>
          </view>

          <view class="p-3">
            <text class="font-bold text-ink text-[16px] mb-1.5 block"
              style="font-family: 'Outfit', 'PingFang SC', sans-serif;">#{{ topic.title }}</text>
            <text class="text-[13px] text-muted leading-snug line-clamp-2">
              {{ topic.announcement || (topic.storyCount || 0) + ' 个故事参与' }}
            </text>
            <view class="flex items-center gap-4 mt-2.5 pt-2.5 border-t border-line">
              <view class="flex items-center gap-1 text-xs text-muted">
                <text class="iconfont icon-lucide-file-text text-xs"></text>
                <text style="font-variant-numeric: tabular-nums;">{{ topic.storyCount || 0 }} 作品</text>
              </view>
              <view class="flex items-center gap-1 text-xs text-muted">
                <text class="iconfont icon-lucide-clock-4 text-xs"></text>
                <text>{{ (topic.startTime || '').substring(0, 10) }}</text>
              </view>
            </view>
          </view>
        </view>

        <view v-if="list.length === 0 && !loading" class="flex flex-col items-center justify-center py-20">
          <text class="iconfont icon-lucide-lightbulb text-5xl text-gray-300 mb-3"></text>
          <text class="text-sm font-semibold text-ink mb-1">暂无话题活动</text>
          <text class="text-xs text-muted">敬请期待新的创作挑战</text>
        </view>
        <view v-if="loading" class="text-center py-6">
          <text class="text-sm text-muted">加载中...</text>
        </view>
      </view>
      <view class="h-4"></view>
    </scroll-view>
  </view>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useScrollHeight } from '@/utils/useScrollHeight.js'
import { getTopicList } from '@/apis/modules/topic.js'

const { scrollHeight } = useScrollHeight('#scroll-header')
const list = ref([])
const loading = ref(false)
const refreshing = ref(false)

async function fetchList() {
  loading.value = true
  try {
    const res = await getTopicList()
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

function goDetail(topicId) {
  uni.navigateTo({ url: `/pages/topic/detail?topicId=${topicId}` })
}

onMounted(fetchList)
</script>
