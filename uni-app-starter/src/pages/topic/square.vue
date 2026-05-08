<template>
  <view class="min-h-screen bg-bg flex flex-col">
    <!-- Header -->
    <view class="flex items-center justify-between px-4 sticky top-0 z-10 bg-bg/95 backdrop-blur-xl"
      style="padding-top: calc(var(--status-bar-height) + 12px); padding-bottom: 14px; border-bottom: 1px solid #E8E3DC;">
      <view class="w-8 h-8 rounded-full flex items-center justify-center active:scale-90 transition-transform duration-200"
        style="background: rgba(0,0,0,0.05);" @tap="uni.navigateBack()">
        <text class="iconfont icon-lucide-chevron-left text-ink" style="font-size: 18px; font-weight: bold;"></text>
      </view>
      <text class="text-[17px] font-bold text-ink" style="font-family: 'Outfit', 'PingFang SC', sans-serif;">#{{ topicTitle }}</text>
      <view style="width: 32px;"></view>
    </view>

    <!-- Sort Tabs -->
    <view id="scroll-header" class="px-4 py-3 bg-bg">
      <view class="flex items-center gap-4">
        <view v-for="tab in sortTabs" :key="tab.key" class="relative pb-2"
          @tap="switchSort(tab.key)">
          <text class="text-[15px] transition-colors duration-200"
            :class="sortType === tab.key ? 'text-ink font-bold' : 'text-muted'"
            style="font-family: 'Outfit', sans-serif;">{{ tab.label }}</text>
          <view v-if="sortType === tab.key"
            class="absolute bottom-0 left-1/2 -translate-x-1/2 rounded-full bg-accent"
            style="width: 20px; height: 3px;"></view>
        </view>
        <text class="text-xs text-muted ml-auto">{{ totalViews }}次浏览</text>
      </view>
    </view>

    <!-- Works Grid -->
    <scroll-view scroll-y :style="{ height: scrollHeight + 'px' }" @scrolltolower="loadMore">
      <view class="px-3 pt-1 grid grid-cols-2 gap-3">
        <view
          v-for="(work, idx) in list"
          :key="work.storyId"
          class="bg-white rounded-2xl shadow-card overflow-hidden active:scale-[0.98] transition-transform duration-200"
          @tap="goDetail(work.storyId)"
        >
          <!-- Cover -->
          <view class="bg-gray-200 relative w-full" style="aspect-ratio: 4/3;">
            <image v-if="work.cover" :src="work.cover" class="w-full h-full" mode="aspectFill" />
            <view v-else class="absolute inset-0 flex items-center justify-center">
              <text class="iconfont icon-lucide-image text-2xl text-gray-300"></text>
            </view>
            <!-- Rank Badge -->
            <view v-if="sortType === 'hot' && idx < 3"
              class="absolute top-2 left-2 text-[10px] font-bold px-2 py-0.5 rounded-md z-10"
              style="font-family: 'Outfit', sans-serif;"
              :style="{
                background: idx === 0 ? '#FFC107' : idx === 1 ? '#B0B0B0' : '#CD7F32',
                color: idx === 1 ? '#fff' : '#1A1A1A'
              }">
              TOP{{ idx + 1 }}
            </view>
          </view>

          <view class="p-2.5">
            <text class="text-[13px] font-semibold text-ink line-clamp-1 mb-2 block"
              style="font-family: 'Outfit', 'PingFang SC', sans-serif;">{{ work.title }}</text>
            <view class="flex items-center justify-between">
              <view class="flex items-center gap-1 text-accent-dark">
                <text class="iconfont icon-lucide-zap text-xs"></text>
                <text class="text-[11px] font-bold" style="font-variant-numeric: tabular-nums;">{{ work.viewCount || 0 }}</text>
              </view>
              <view class="flex items-center gap-1 text-muted">
                <text class="iconfont icon-lucide-layers text-xs"></text>
                <text class="text-[11px]" style="font-variant-numeric: tabular-nums;">{{ work.branchCount || 0 }}</text>
              </view>
            </view>
          </view>
        </view>
      </view>

      <view class="text-center py-6">
        <text v-if="loading" class="text-sm text-muted">加载中...</text>
        <text v-else-if="noMore && list.length > 0" class="text-xs text-muted">— 没有更多了 —</text>
        <view v-else-if="list.length === 0 && !loading" class="flex flex-col items-center py-16">
          <text class="iconfont icon-lucide-pencil-line text-4xl text-gray-300 mb-3"></text>
          <text class="text-sm text-muted">暂无作品，快来参与吧</text>
        </view>
      </view>
      <view class="h-20"></view>
    </scroll-view>

    <!-- FAB -->
    <view class="fixed bottom-6 left-0 right-0 pointer-events-none flex justify-center z-20"
      style="padding-bottom: env(safe-area-inset-bottom, 0px);">
      <view
        class="w-13 h-13 bg-ink rounded-full flex items-center justify-center shadow-fab border-3 border-bg pointer-events-auto active:scale-90 transition-transform duration-200"
        style="width: 52px; height: 52px;"
        @tap="goCreate">
        <text class="iconfont icon-lucide-plus text-accent" style="font-size: 24px; font-weight: bold;"></text>
      </view>
    </view>
    <login-modal ref="loginModalRef" />
  </view>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useScrollHeight } from '@/utils/useScrollHeight.js'
import { useUserStore } from '@/stores/user.js'
import LoginModal from '@/components/login-modal.vue'
import { getTopicDetail, getTopicStories } from '@/apis/modules/topic.js'

const { scrollHeight } = useScrollHeight('#scroll-header')
const userStore = useUserStore()
const loginModalRef = ref(null)
const pages = getCurrentPages()
const topicId = Number(pages[pages.length - 1]?.options?.topicId || 0)

const sortTabs = [
  { key: 'hot', label: '最热' },
  { key: 'new', label: '最新' },
]
const sortType = ref('hot')
const topicTitle = ref('')
const totalViews = ref('0')
const list = ref([])
const page = ref(1)
const loading = ref(false)
const noMore = ref(false)

function switchSort(key) {
  if (sortType.value === key) return
  sortType.value = key
  list.value = []
  page.value = 1
  noMore.value = false
  fetchList()
}

async function fetchList() {
  if (loading.value || noMore.value) return
  loading.value = true
  try {
    const res = await getTopicStories(topicId, { sortType: sortType.value, pageNum: page.value, pageSize: 10 })
    if (res && res.rows) {
      list.value = [...list.value, ...res.rows]
      noMore.value = list.value.length >= res.total
      page.value++
    }
  } finally {
    loading.value = false
  }
}

function loadMore() {
  if (!noMore.value) fetchList()
}

function goDetail(storyId) {
  uni.navigateTo({ url: `/pages/story/detail?storyId=${storyId}` })
}

function goCreate() {
  if (!userStore.isLoggedIn) { loginModalRef.value.openModal({ title: '参与需要登录', description: '登录后才能发布故事，参与话题挑战。' }); return }
  uni.navigateTo({ url: `/pages/story/create?topicId=${topicId}` })
}

onMounted(async () => {
  try {
    const res = await getTopicDetail(topicId)
    if (res && res.data) {
      topicTitle.value = res.data.title
      totalViews.value = res.data.viewCount || '0'
    }
  } catch {
    uni.showToast({ title: '加载失败，请返回重试', icon: 'none' })
  }
  fetchList()
})
</script>
