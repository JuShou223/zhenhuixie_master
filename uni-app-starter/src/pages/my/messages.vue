<template>
  <view class="min-h-screen bg-white flex flex-col" style="padding-bottom: env(safe-area-inset-bottom, 0px);">
    <!-- Header + Tabs -->
    <view
      id="scroll-header"
      class="bg-white border-b border-gray-200 sticky top-0 z-10"
      style="padding-top: calc(var(--status-bar-height) + 12px);"
    >
      <view class="flex items-center justify-between px-4 pb-3">
        <view class="p-1 -ml-1" @tap="uni.navigateBack()">
          <text class="iconfont icon-lucide-chevron-left text-2xl text-gray-500"></text>
        </view>
        <text class="text-[17px] font-medium text-gray-800">互动消息</text>
        <view style="width: 28px;"></view>
      </view>
      <!-- Type Tabs -->
      <scroll-view scroll-x class="whitespace-nowrap px-4">
        <view class="flex gap-5">
          <view
            v-for="tab in tabs"
            :key="tab.type"
            class="relative pb-2 flex-shrink-0"
            @tap="switchTab(tab.type)"
          >
            <text
              class="text-sm"
              :class="activeType === tab.type ? 'text-ink font-bold' : 'text-muted'"
            >{{ tab.label }}</text>
            <view
              v-if="activeType === tab.type"
              class="absolute bottom-0 left-1/2 -translate-x-1/2 rounded-full"
              style="width: 24px; height: 2px; background-color: #FFC107;"
            ></view>
          </view>
        </view>
      </scroll-view>
    </view>

    <scroll-view scroll-y :style="{ height: scrollHeight + 'px' }" @scrolltolower="loadMore">
      <view v-if="list.length > 0">
        <view
          v-for="(notif, idx) in list"
          :key="notif.notifId"
          class="flex px-4 py-5 border-b border-gray-100"
          :class="idx === list.length - 1 ? 'border-0' : ''"
          @tap="goTarget(notif)"
        >
          <!-- Avatar -->
          <image
            :src="notif.senderAvatar || '/static/avatar-default.png'"
            class="w-10 h-10 rounded-full flex-shrink-0"
            mode="aspectFill"
          />

          <!-- Middle Content -->
          <view class="ml-3 flex-1 flex flex-col gap-1 pr-3">
            <text class="text-[16px] text-gray-800 font-medium">{{ notif.senderNickName }}</text>
            <view class="flex items-center gap-3 text-[13px] text-gray-400 mt-1">
              <text>{{ actionText(notif.notifType) }}</text>
              <text v-if="notif.createTime">{{ timeAgo(notif.createTime) }}</text>
            </view>

            <!-- Comment/Reply/Branch Content -->
            <view v-if="notif.notifType === 1 || notif.notifType === 2 || notif.notifType === 3" class="mt-2 text-[14px] text-gray-800 leading-relaxed">
              <text>{{ notif.content || '' }}</text>
              <view class="mt-3">
                <view class="bg-gray-100 text-gray-500 text-[11px] font-medium px-3 py-1 rounded-full inline-block" @tap.stop="goTarget(notif)">
                  去查看
                </view>
              </view>
            </view>
          </view>

          <!-- Right Target Content Block -->
          <view v-if="notif.targetContentPreview" class="bg-gray-100 p-2 text-[12px] text-gray-500 leading-tight flex-shrink-0 overflow-hidden line-clamp-4"
            style="width: 96px; max-height: 72px;">
            <text>{{ notif.targetContentPreview }}</text>
          </view>
        </view>
      </view>

      <view v-if="loading" class="text-center py-4">
        <text class="text-sm text-muted">加载中...</text>
      </view>
      <view v-else-if="list.length === 0" class="text-center py-16">
        <text class="text-sm text-muted" style="font-style: italic;">暂无消息</text>
      </view>
      <view class="h-4"></view>
    </scroll-view>
  </view>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { getNotificationList, markAsRead } from '@/apis/modules/notification'
import { useScrollHeight } from '@/utils/useScrollHeight'
import { timeAgo } from '@/utils/timeago'

const { scrollHeight } = useScrollHeight('#scroll-header')
const tabs = [
  { type: -1, label: '全部' },
  { type: 0, label: '被赞' },
  { type: 1, label: '被评论' },
  { type: 2, label: '被回复' },
  { type: 3, label: '被续写' },
]

const activeType = ref(-1)
const list = ref([])
const page = ref(1)
const loading = ref(false)
const noMore = ref(false)

function actionText(type) {
  const map = { 0: '赞了你的内容', 1: '评论了你', 2: '回复了你', 3: '对你的内容进行了续写' }
  return map[type] || ''
}

function switchTab(type) {
  if (activeType.value === type) return
  activeType.value = type
  list.value = []
  page.value = 1
  noMore.value = false
  fetchList()
}

async function fetchList() {
  if (loading.value || noMore.value) return
  loading.value = true
  try {
    const res = await getNotificationList({ notifType: activeType.value, pageNum: page.value, pageSize: 20 })
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

function goTarget(notif) {
  if (notif.isRead === 0) {
    markAsRead({ notifId: notif.notifId }).then(() => { notif.isRead = 1 })
  }
  if (notif.storyId) {
    if (notif.targetType === 1) {
      uni.navigateTo({ url: `/pages/story/branch-detail?branchId=${notif.targetId}&storyId=${notif.storyId}` })
    } else {
      uni.navigateTo({ url: `/pages/story/detail?storyId=${notif.storyId}` })
    }
  }
}

onMounted(() => {
  fetchList()
  markAsRead({ notifId: -1 })
})
</script>
