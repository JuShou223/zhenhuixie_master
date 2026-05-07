<template>
  <view class="h-screen flex flex-col" style="background-color: #FDFCF8;">
    <!-- Header -->
    <view class="px-4 flex items-center justify-between"
      style="padding-top: calc(var(--status-bar-height) + 12px); padding-bottom: 10px;">
      <text class="text-ink tracking-tight"
        style="font-family: 'Noto Serif SC', Georgia, 'Songti SC', STSong, serif; font-weight: 700; font-size: 24px; letter-spacing: -0.02em;">真会写.</text>
    </view>

    <!-- Tabs -->
    <scroll-view id="guide-tabs" scroll-x class="w-full bg-white sticky top-0 z-10">
      <view class="flex gap-8 py-4 px-4" style="white-space: nowrap;">
        <view v-for="tab in tabs" :key="tab.key" class="relative pb-2 shrink-0" @tap="switchTab(tab.key)">
          <text class="text-lg transition-colors duration-200"
            :class="activeTab === tab.key ? 'text-ink font-semibold' : 'text-muted'">{{ tab.label }}</text>
          <view v-if="activeTab === tab.key"
            class="absolute bottom-0 left-1/2 -translate-x-1/2 rounded-full bg-accent transition-all duration-200"
            style="width: 24px; height: 3px;"></view>
        </view>
      </view>
    </scroll-view>

    <!-- Story Grid -->
    <scroll-view scroll-y :style="{ height: scrollHeight + 'px' }" @scroll="onScroll" @scrolltolower="loadMore" refresher-enabled
      :refresher-triggered="refreshing" @refresherrefresh="onRefresh">
      <!-- 骨架屏 -->
      <view v-if="loading && list.length === 0" class="px-4 pt-2 grid grid-cols-2 gap-3">
        <view v-for="i in 4" :key="'s' + i"
          class="bg-white rounded-lg overflow-hidden shadow-card border border-gray-100">
          <view class="aspect-[4/3] skeleton"></view>
          <view class="p-2.5 space-y-2">
            <view class="skeleton" style="height: 14px; width: 80%;"></view>
            <view class="skeleton" style="height: 14px; width: 50%;"></view>
            <view class="flex justify-between mt-1">
              <view class="skeleton" style="height: 12px; width: 36px;"></view>
              <view class="skeleton" style="height: 12px; width: 24px;"></view>
            </view>
          </view>
        </view>
      </view>

      <view v-else class="px-4 pt-2 grid grid-cols-2 gap-3 max-w-screen-xl mx-auto">
        <view v-for="(story, index) in list" :key="story.storyId"
          :id="index === 0 ? 'guide-first-story' : undefined"
          class="bg-white rounded-lg overflow-hidden shadow-card border border-gray-100 active:scale-[0.98] transition-transform duration-200"
          @tap="goDetail(story.storyId)">
          <!-- Cover -->
          <view class="aspect-[4/3] bg-gray-200 flex items-center justify-center relative overflow-hidden">
            <image v-if="story.cover" :src="story.cover" class="absolute inset-0 w-full h-full" mode="aspectFill"
              @error="story.cover = ''" />
            <text v-if="!story.cover" class="iconfont icon-lucide-image text-2xl text-gray-400"></text>
          </view>

          <view class="p-2.5">
            <text class="text-sm text-gray-800 leading-snug line-clamp-2 mb-2 block font-medium"
              style="font-family: 'Outfit', -apple-system, 'PingFang SC', 'Hiragino Sans GB', sans-serif;">
              {{ story.title }}
            </text>

            <view class="flex items-center justify-between text-xs">
              <view class="flex items-center gap-1 text-accent">
                <text class="iconfont icon-lucide-zap text-xs"></text>
                <text class="font-medium" style="font-variant-numeric: tabular-nums;">{{ story.heatScore || 0 }}</text>
              </view>
              <view class="flex items-center gap-1 text-muted">
                <text class="iconfont icon-lucide-user text-xs"></text>
                <text style="font-variant-numeric: tabular-nums;">{{ story.participantCount || 0 }}</text>
              </view>
            </view>
          </view>
        </view>
      </view>

      <!-- 空状态 -->
      <view v-if="!loading && list.length === 0" class="flex flex-col items-center justify-center py-20 px-8">
        <text class="iconfont icon-lucide-pencil-line text-5xl text-gray-300 mb-4"></text>
        <text class="text-base font-medium text-gray-500 mb-1">还没有故事</text>
        <text class="text-sm text-gray-400 text-center">写下第一个故事设定，开启无限分支创作</text>
      </view>

      <!-- Load states -->
      <view v-if="list.length > 0" class="text-center py-4">
        <text v-if="loading" class="text-sm text-muted">加载中...</text>
        <text v-else-if="noMore" class="text-sm text-muted">— 没有更多了 —</text>
      </view>

      <view class="h-20"></view>
    </scroll-view>

    <!-- Scroll to Top -->
    <view v-if="showBackTop"
      class="fixed z-10 w-10 h-10 rounded-full flex items-center justify-center active:scale-90 transition-transform duration-200"
      style="bottom: 120px; right: 16px; background: rgba(26, 26, 26, 0.45); backdrop-filter: blur(8px);"
      @tap="scrollToTop">
      <text class="iconfont icon-lucide-chevron-up text-white" style="font-size: 20px; font-weight: bold;"></text>
    </view>

    <tab-bar defaultTab="home" />

    <user-guide ref="guideRef" :guideConfig="GUIDE_STEPS_CONFIG" />
  </view>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { onShow } from '@dcloudio/uni-app'
import { useScrollHeight } from '@/utils/useScrollHeight.js'
import { useUserStore } from '@/stores/user.js'
import { getStoryList } from '@/apis/modules/story.js'
import TabBar from '@/components/tab-bar.vue'
import UserGuide from '@/components/user-guide.vue'
import { GUIDE_TYPES } from '@/hooks/useGlobalUserGuide'
import { useTabbar } from '@/hooks/useTabbar'
const { handleSetTabbar } = useTabbar()
const { scrollHeight } = useScrollHeight('#guide-tabs')

const userStore = useUserStore()
const guideRef = ref(null)

const GUIDE_STEPS_CONFIG = {
  [GUIDE_TYPES.STORY_HOME]: [
    {
      targetId: 'guide-tabs',
      title: '📖 发现故事',
      description: '「推荐」展示精选故事，「热门」按热度排序，轻划切换分类。',
      position: 'bottom',
      buttonText: '下一步'
    },
    {
      targetId: 'guide-first-story',
      title: '点击进入故事',
      description: '点击任意故事卡片，阅读完整设定，查看所有分支续写，也可以留言评论或创作你自己的续写剧情。',
      position: 'bottom',
      buttonText: '下一步'
    },
    {
      targetId: 'tabbar:guide-create-button',
      disableScroll: true,
      title: '✍️ 开始创作',
      description: '点击底部中央的 + 按钮，发布你的第一个故事设定，邀请大家一起续写你的世界。',
      position: 'center',
      buttonText: '开始体验'
    }
  ]
}

let guideChecked = false
onShow(() => {
  fetchList()
  handleSetTabbar('home')
})

const tabs = [
  { key: 'recommend', label: '推荐' },
  { key: 'hot', label: '热门' },
]
const activeTab = ref('recommend')
const list = ref([])
const page = ref(1)
const loading = ref(false)
const noMore = ref(false)
const refreshing = ref(false)
const showBackTop = ref(false)
let lastScrollTop = 0

function switchTab(key) {
  if (activeTab.value === key) return
  activeTab.value = key
  list.value = []
  page.value = 1
  noMore.value = false
  fetchList()
}

async function fetchList(isRefresh = false) {
  if (loading.value) return
  loading.value = true
  try {
    const res = await getStoryList({ queryType: activeTab.value, pageNum: page.value, pageSize: 10 })
    if (res && res.rows) {
      if (isRefresh) {
        list.value = res.rows
      } else {
        list.value = [...list.value, ...res.rows]
      }
      noMore.value = list.value.length >= res.total
      page.value++
    }
  } finally {
    loading.value = false
    refreshing.value = false
    if (!guideChecked) {
      guideChecked = true
      setTimeout(() => { if (guideRef.value) guideRef.value.check() }, 300)
    }
  }
}

function loadMore() {
  if (!noMore.value) fetchList()
}

function onRefresh() {
  refreshing.value = true
  list.value = []
  page.value = 1
  noMore.value = false
  fetchList(true)
}

function goDetail(storyId) {
  uni.navigateTo({ url: `/pages/story/detail?storyId=${storyId}` })
}

function onScroll(e) {
  const s = e.detail.scrollTop
  showBackTop.value = s > 600
  lastScrollTop = s
}

function scrollToTop() {
  uni.pageScrollTo({ scrollTop: 0, duration: 300 })
}

</script>
