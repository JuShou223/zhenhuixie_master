<template>
  <view class="min-h-screen bg-bg flex flex-col">
    <!-- Header -->
    <view id="scroll-header" class="flex items-center justify-between px-4 sticky top-0 z-10 bg-bg/95 backdrop-blur-xl"
      style="padding-top: calc(var(--status-bar-height) + 12px); padding-bottom: 14px; border-bottom: 1px solid #E8E3DC;">
      <view class="w-8 h-8 rounded-full flex items-center justify-center active:scale-90 transition-transform duration-200"
        style="background: rgba(0,0,0,0.05);" @tap="uni.navigateBack()">
        <text class="iconfont icon-lucide-chevron-left text-ink" style="font-size: 18px; font-weight: bold;"></text>
      </view>
      <view class="w-8 h-8 rounded-full flex items-center justify-center active:scale-90 transition-transform duration-200"
        style="background: rgba(0,0,0,0.05);" @tap="doShare">
        <text class="iconfont icon-lucide-share-2 text-ink" style="font-size: 16px;"></text>
      </view>
    </view>

    <scroll-view scroll-y :style="{ height: scrollHeight + 'px', paddingBottom: '80px' }">
      <view class="px-5 pt-4">
        <!-- Title -->
        <text class="text-2xl font-bold text-ink block mb-6"
          style="font-family: 'Outfit', 'PingFang SC', sans-serif; letter-spacing: -0.01em;">#{{ topic.title }}</text>

        <!-- Time Badge -->
        <view class="flex items-center gap-2 mb-6">
          <view class="w-2 h-2 rounded-full" :class="topic.status === '0' ? 'bg-green-500' : 'bg-muted'"></view>
          <text class="text-sm text-muted" style="font-family: 'Outfit', sans-serif;">
            {{ formatDate(topic.startTime) }} — {{ formatDate(topic.endTime) }}
          </text>
          <text class="text-xs font-semibold px-2 py-0.5 rounded-full ml-auto"
            :class="topic.status === '0' ? 'bg-green-50 text-green-600' : 'bg-gray-100 text-gray-400'"
            style="font-family: 'Outfit', sans-serif;">
            {{ topic.status === '0' ? '进行中' : '已结束' }}
          </text>
        </view>

        <!-- Activity Rules Card -->
        <view class="bg-white rounded-2xl shadow-card p-5 mb-6">
          <text class="text-xs font-bold text-muted uppercase tracking-wider mb-4 block"
            style="font-family: 'Outfit', sans-serif; letter-spacing: 0.08em;">活动规则</text>

          <view class="space-y-4">
            <view>
              <text class="text-sm font-semibold text-ink block mb-1"
                style="font-family: 'Outfit', sans-serif;">活动简介</text>
              <text class="text-[14px] text-ink/70 leading-relaxed block">{{ topic.announcement || '暂无活动公告' }}</text>
            </view>

            <view class="border-t border-line pt-4">
              <text class="text-sm font-semibold text-ink block mb-1"
                style="font-family: 'Outfit', sans-serif;">参与方式</text>
              <text class="text-[14px] text-ink/70 leading-relaxed block">
                创建与话题相关的故事设定，或参与已有故事的分支接龙撰写
              </text>
            </view>

            <view class="border-t border-line pt-4">
              <text class="text-sm font-semibold text-ink block mb-1"
                style="font-family: 'Outfit', sans-serif;">内容要求</text>
              <text class="text-[14px] text-ink/70 leading-relaxed block">
                故事主题须紧扣活动话题，否则视为无效，可能被移除活动区或删除处理
              </text>
            </view>
          </view>
        </view>

        <!-- Stats -->
        <view class="flex items-center gap-6 mb-8 px-1">
          <view class="flex flex-col items-center">
            <text class="text-lg font-bold text-ink" style="font-family: 'Outfit', sans-serif; font-variant-numeric: tabular-nums;">{{ topic.storyCount || 0 }}</text>
            <text class="text-xs text-muted">参与作品</text>
          </view>
          <view class="w-px h-8 bg-line"></view>
          <view class="flex flex-col items-center">
            <text class="text-lg font-bold text-ink" style="font-family: 'Outfit', sans-serif;">{{ topic.status === '0' ? '开放中' : '已结束' }}</text>
            <text class="text-xs text-muted">活动状态</text>
          </view>
        </view>
      </view>

      <view class="h-20"></view>
    </scroll-view>

    <!-- Bottom Button -->
    <view class="px-4 z-20 fixed bottom-0 left-0 right-0 bg-bg/95 backdrop-blur-xl"
      style="padding-top: 12px; padding-bottom: calc(12px + env(safe-area-inset-bottom, 0px));">
      <view
        class="w-full bg-ink text-accent font-bold text-[16px] flex items-center justify-center rounded-2xl active:scale-[0.98] transition-transform duration-200"
        style="height: 50px; font-family: 'Outfit', sans-serif;"
        @tap="goSquare"
      >
        <text class="iconfont icon-lucide-layout-grid text-base mr-2"></text>
        活动作品专区
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useScrollHeight } from '@/utils/useScrollHeight'
import { getTopicDetail } from '@/apis/modules/topic'

const { scrollHeight } = useScrollHeight('#scroll-header', 80)
const pages = getCurrentPages()
const topicId = Number(pages[pages.length - 1]?.options?.topicId || 0)

const topic = ref<Record<string, any>>({})

function formatDate(dateStr) {
  if (!dateStr) return '--'
  return dateStr.substring(0, 10)
}

function goSquare() {
  uni.navigateTo({ url: `/pages/topic/square?topicId=${topicId}` })
}

function doShare() {
  // #ifndef MP-WEIXIN
  const url = `${location.origin}/#/pages/topic/detail?topicId=${topicId}`
  uni.setClipboardData({ data: url, success: () => uni.showToast({ title: '链接已复制', icon: 'success' }) })
  // #endif
}

onMounted(async () => {
  const res = await getTopicDetail(topicId)
  if (res && res.data) topic.value = res.data
})
</script>

<script lang="ts">
export default {
  onShareAppMessage(this: any) {
    return {
      title: this.topic?.title ? `#${this.topic.title}` : '真会写 - 话题挑战',
      path: `/pages/topic/detail?topicId=${this.topicId}`
    }
  }
}
</script>
