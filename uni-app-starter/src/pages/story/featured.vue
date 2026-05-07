<template>
  <view class="min-h-screen" style="background-color: #FDFCF8;">
    <view class="px-4 flex items-center gap-3"
      style="padding-top: calc(var(--status-bar-height) + 12px); padding-bottom: 10px;">
      <view class="w-8 h-8 rounded-full flex items-center justify-center active:scale-90"
        style="background: rgba(0,0,0,0.06);" @tap="uni.navigateBack()">
        <text class="iconfont icon-lucide-chevron-left text-ink" style="font-size:18px;font-weight:bold;"></text>
      </view>
      <text class="text-ink font-bold text-xl" style="font-family:'Noto Serif SC',serif;">✦ 精选故事</text>
    </view>

    <view class="px-4 pb-2">
      <text class="text-gray-400 text-sm">编辑精选 · 每篇都值得续写</text>
    </view>

    <view v-if="loading" class="flex justify-center py-16">
      <u-loading-icon />
    </view>

    <view v-else>
      <view v-for="story in stories" :key="story.storyId"
        class="mx-4 mb-3 bg-white rounded-2xl p-4 shadow-sm active:scale-[0.98] transition-transform"
        @tap="goDetail(story.storyId)">
        <view class="flex items-start gap-3">
          <image v-if="story.cover" :src="story.cover" class="w-14 h-14 rounded-xl flex-shrink-0" mode="aspectFill" />
          <view class="flex-1 min-w-0">
            <text class="text-ink font-semibold text-base line-clamp-1"
              style="font-family:'Noto Serif SC',serif;">{{ story.title }}</text>
            <text class="text-gray-500 text-sm mt-1 line-clamp-2">{{ story.settingContent }}</text>
            <view class="flex items-center gap-3 mt-2">
              <text class="text-gray-400 text-xs">{{ story.authorNickName }}</text>
              <text class="text-accent text-xs font-medium">{{ story.branchCount }} 条续写</text>
              <text class="text-gray-400 text-xs">{{ story.participantCount }} 人参与</text>
            </view>
          </view>
        </view>
      </view>

      <view v-if="stories.length === 0" class="flex flex-col items-center py-20 gap-3">
        <text class="text-4xl">📭</text>
        <text class="text-gray-400 text-sm">暂无精选故事</text>
      </view>

      <view v-if="hasMore" class="flex justify-center py-4">
        <u-loading-icon text="加载中..." />
      </view>
    </view>
  </view>
</template>

<script>
import { getFeaturedStories } from '@/apis/modules/story'

export default {
  data() {
    return {
      stories: [],
      loading: true,
      pageNum: 1,
      pageSize: 15,
      hasMore: true
    }
  },
  onLoad() {
    this.loadStories()
  },
  onReachBottom() {
    if (this.hasMore && !this.loading) this.loadMore()
  },
  methods: {
    loadStories() {
      this.loading = true
      getFeaturedStories({ pageNum: this.pageNum, pageSize: this.pageSize }).then(res => {
        const rows = res?.rows || []
        this.stories = rows
        this.hasMore = rows.length >= this.pageSize
        this.loading = false
      })
    },
    loadMore() {
      this.loading = true
      this.pageNum++
      getFeaturedStories({ pageNum: this.pageNum, pageSize: this.pageSize }).then(res => {
        const rows = res?.rows || []
        this.stories.push(...rows)
        this.hasMore = rows.length >= this.pageSize
        this.loading = false
      })
    },
    goDetail(storyId) {
      uni.navigateTo({ url: `/pages/story/detail?storyId=${storyId}` })
    }
  }
}
</script>
