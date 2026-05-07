<template>
  <view class="min-h-screen bg-bg flex flex-col">
    <!-- Sticky Mini Header (appears on scroll) -->
    <view v-show="showMiniHeader" class="fixed top-0 left-0 right-0 z-30 flex items-center gap-3 px-4"
      style="padding-top: calc(var(--status-bar-height) + 6px); padding-bottom: 10px; background: rgba(255,255,255,0.92); backdrop-filter: blur(16px); border-bottom: 1px solid #E8E3DC;">
      <view
        class="w-7 h-7 rounded-full flex items-center justify-center active:scale-90 transition-transform duration-200"
        style="background: rgba(0,0,0,0.06);" @tap="uni.navigateBack()">
        <text class="iconfont icon-lucide-chevron-left text-ink" style="font-size: 18px; font-weight: bold;"></text>
      </view>
      <text class="flex-1 text-ink font-semibold text-[15px] truncate"
        style="font-family: 'Outfit', 'PingFang SC', sans-serif;">{{ story.title }}</text>
      <view
        class="w-7 h-7 rounded-full flex items-center justify-center active:scale-90 transition-transform duration-200"
        style="background: rgba(0,0,0,0.06);" @tap="doShare">
        <text class="iconfont icon-lucide-share-2 text-ink" style="font-size: 16px;"></text>
      </view>
    </view>

    <!-- Cover Hero Header -->
    <view id="story-hero" class="relative overflow-hidden" style="height: 220px; background-color: #111;">
      <image v-if="story.cover" :src="story.cover" class="absolute inset-0 w-full h-full" mode="aspectFill" />
      <!-- Gradient overlay: dark at bottom, subtle at top -->
      <view class="absolute inset-0"
        style="background: linear-gradient(180deg, rgba(0,0,0,0.15) 0%, rgba(0,0,0,0.2) 30%, rgba(0,0,0,0.7) 100%);">
      </view>

      <!-- Back button -->
      <view class="absolute left-4" :style="{ top: `calc(var(--status-bar-height) + 8px)` }" @tap="uni.navigateBack()">
        <view
          class="w-8 h-8 rounded-full flex items-center justify-center active:scale-90 transition-transform duration-200"
          style="background: rgba(255,255,255,0.15); backdrop-filter: blur(8px);">
          <text class="iconfont icon-lucide-chevron-left text-white" style="font-size: 20px; font-weight: bold;"></text>
        </view>
      </view>

      <!-- Title + Stats -->
      <view class="absolute bottom-4 left-4 right-4">
        <text class="text-xl font-bold text-white tracking-tight block mb-2"
          style="font-family: 'Outfit', 'PingFang SC', sans-serif; text-shadow: 0 2px 8px rgba(0,0,0,0.5); letter-spacing: -0.01em;">{{
            story.title }}</text>
        <view class="flex gap-5 text-white/80 text-xs font-medium">
          <view class="flex items-center gap-1">
            <text class="iconfont icon-lucide-user text-white/70 text-xs"></text>
            <text style="font-variant-numeric: tabular-nums;">{{ story.participantCount || 0 }}</text>
          </view>
          <view class="flex items-center gap-1">
            <text class="iconfont icon-lucide-layers text-white/70 text-xs"></text>
            <text style="font-variant-numeric: tabular-nums;">{{ story.branchCount || 0 }}</text>
          </view>
          <view class="flex items-center gap-1">
            <text class="iconfont icon-lucide-eye text-white/70 text-xs"></text>
            <text style="font-variant-numeric: tabular-nums;">{{ story.viewCount || 0 }}</text>
          </view>
        </view>
      </view>
    </view>

    <scroll-view scroll-y :style="{ height: scrollHeight + 'px', paddingBottom: '80px' }" @scroll="onScroll" @scrolltolower="loadMoreBranches">
      <!-- Story Intro -->
      <view class="px-5 pt-5 pb-2">
        <!-- Hidden measurement element -->
        <view class="fixed opacity-0 pointer-events-none" style="left: -9999px; width: calc(100vw - 40px);">
          <text class="intro-measure text-[15px] leading-relaxed block"
            style="white-space: pre-wrap; font-family: 'Outfit', 'PingFang SC', sans-serif;">{{ story.settingContent
            }}</text>
        </view>
        <view class="relative">
          <view class="text-[15px] leading-relaxed text-ink/80" :class="settingExpanded ? '' : 'line-clamp-5'"
            style="white-space: pre-wrap; font-family: 'Outfit', 'PingFang SC', sans-serif;">{{ story.settingContent }}
          </view>
          <!-- Gradient fade + expand button -->
          <view v-if="!settingExpanded && settingOverflows"
            class="absolute bottom-0 left-0 right-0 flex items-end justify-center"
            style="height: 52px; background: linear-gradient(to bottom, transparent 0%, #FDFCF8 55%);">
            <text class="text-accent-dark font-semibold text-sm pb-0.5 active:opacity-70 transition-opacity"
              @tap="settingExpanded = true">展开全文</text>
          </view>
        </view>
        <text v-if="settingExpanded && settingOverflows"
          class="text-accent-dark font-semibold text-sm mt-1 block active:opacity-70 transition-opacity"
          @tap="settingExpanded = false">收起</text>

        <!-- Tags -->
        <view v-if="story.topicNames" class="flex flex-wrap gap-2 mt-3">
          <text v-for="t in story.topicNames.split(',')" :key="t"
            class="bg-yellow-50 text-accent-dark text-[11px] font-medium px-2.5 py-1 rounded-md"># {{ t }}</text>
        </view>
      </view>

      <!-- Prompt Bar -->
      <view class="mx-5 my-3">
        <view class="bg-ink rounded-2xl text-white/90 text-xs font-semibold py-2.5 text-center"
          style="font-family: 'Outfit', sans-serif; letter-spacing: 0.02em;">
          无限分支创作模式，下面是其他小伙伴创作的不同分支剧情
        </view>
      </view>

      <!-- Leaderboard Entry -->
      <view id="guide-story-ranking"
        class="flex items-center justify-between px-5 py-3 mx-4 bg-white rounded-xl shadow-card mb-1 active:scale-[0.98] transition-transform duration-200"
        @tap="goRanking">
        <view class="flex items-center gap-3">
          <view class="w-7 h-7 bg-ink rounded-lg flex items-center justify-center">
            <text class="text-white text-xs font-bold" style="font-family: 'Outfit', sans-serif;">榜</text>
          </view>
          <view class="flex">
            <image v-for="(user, idx) in ranking.slice(0, 5)" :key="user.userId" :src="resolveAvatar(user.avatar)"
              class="w-7 h-7 rounded-full border-2 border-white" :style="`margin-left: ${Number(idx) > 0 ? '-8px' : '0'};`"
              mode="aspectFill" />
          </view>
        </view>
        <view class="flex items-center gap-0.5">
          <text class="text-muted text-sm font-medium">排行</text>
          <text class="iconfont icon-lucide-chevron-right text-sm text-muted"></text>
        </view>
      </view>

      <!-- Branches List -->
      <view class="px-4 pt-2 flex-1">
        <!-- Skeleton -->
        <view v-if="branchLoading && branches.length === 0" class="flex flex-col gap-4">
          <view v-for="i in 3" :key="'sb' + i" class="mb-4 pb-4 border-b border-line">
            <view class="flex items-center gap-2.5 mb-3">
              <view class="skeleton w-9 h-9 rounded-full"></view>
              <view>
                <view class="skeleton mb-1" style="height: 13px; width: 80px;"></view>
                <view class="skeleton" style="height: 10px; width: 120px;"></view>
              </view>
            </view>
            <view class="skeleton mb-2.5" style="height: 20px; width: 140px;"></view>
            <view class="space-y-2">
              <view class="skeleton" style="height: 14px; width: 100%;"></view>
              <view class="skeleton" style="height: 14px; width: 90%;"></view>
              <view class="skeleton" style="height: 14px; width: 60%;"></view>
            </view>
          </view>
        </view>

        <view v-if="branches.length > 0" class="flex flex-col">
          <view v-for="(branch, idx) in branches" :key="branch.branchId"
            class="mb-5 pb-5 border-b border-line last:border-0 animate-branch-in"
            :style="{ animationDelay: `${idx * 80}ms` }">
            <!-- Author & Like -->
            <view class="flex justify-between items-center mb-3">
              <view class="flex items-center gap-2.5">
                <image :src="resolveAvatar(branch.authorAvatar)" class="w-10 h-10 rounded-full" mode="aspectFill" />
                <view class="flex flex-col">
                  <text class="font-semibold text-ink text-sm"
                    style="font-family: 'Outfit', 'PingFang SC', sans-serif;">{{ branch.authorNickName }}</text>
                  <text class="text-[11px] text-muted">《{{ story.title }}》</text>
                </view>
              </view>
              <view
                class="flex items-center gap-1 text-xs font-semibold active:scale-90 transition-transform duration-200"
                :class="branch.liked ? 'text-accent' : 'text-muted'" @tap.stop="toggleBranchLike(branch)">
                <text class="iconfont icon-lucide-heart text-sm" :class="{ 'like-bounce': branch._bouncing }"></text>
                <text style="font-variant-numeric: tabular-nums;">{{ branch.likeCount || 0 }}</text>
              </view>
            </view>

            <!-- Chapter Label -->
            <view class="inline-flex items-center gap-1.5 mb-3">
              <view class="w-1 h-4 rounded-full bg-accent"></view>
              <text class="text-accent-dark font-bold text-xs" style="font-family: 'Outfit', sans-serif;">
                第{{ branch.chapterNum }}章{{ branch.chapterTitle ? ' · ' + branch.chapterTitle : '' }}
              </text>
            </view>

            <!-- Content -->
            <view class="mb-1">
              <!-- #ifndef H5 -->
              <view class="branch-mirror text-[15px] leading-relaxed"
                style="position: absolute; visibility: hidden; pointer-events: none; white-space: pre-wrap;">{{ branch.content }}</view>
              <!-- #endif -->
              <view class="relative">
                <view class="branch-text text-[15px] text-ink/80 leading-relaxed"
                  :class="branch._expanded ? '' : 'line-clamp-5'"
                  style="white-space: pre-wrap; font-family: 'Outfit', 'PingFang SC', sans-serif;">{{ branch.content }}
                </view>
                <view v-if="!branch._expanded && branch._overflows"
                  class="absolute bottom-0 left-0 right-0 flex items-end justify-center"
                  style="height: 52px; background: linear-gradient(to bottom, transparent 0%, #FDFCF8 55%);">
                  <text class="text-accent-dark font-semibold text-sm pb-0.5 active:opacity-70 transition-opacity"
                    @tap.stop="branch._expanded = true">展开全文</text>
                </view>
              </view>
              <text v-if="branch._expanded && branch._overflows"
                class="text-accent-dark font-semibold text-sm mt-1 block active:opacity-70 transition-opacity"
                @tap.stop="branch._expanded = false">收回</text>
            </view>

            <!-- Footer -->
            <view class="flex justify-between items-end mt-4">
              <view
                class="bg-bg text-muted px-3 py-1 rounded-full text-[11px] font-medium active:bg-line transition-colors duration-200"
                @tap.stop="goComments(branch.branchId)">
                {{ branch.commentCount || 0 }}条评论
              </view>

              <view class="flex items-center gap-1.5"
                @tap.stop="branch.childCount > 0 ? goBranchDetail(branch.branchId) : goBranchCreate(branch.branchId)">
                <view v-if="branch.childCount > 0" class="flex">
                  <image v-for="(c, idx) in (branch.childAvatars || []).slice(0, 3)" :key="idx" :src="resolveAvatar(c)"
                    class="w-5 h-5 rounded-full border-2 border-white"
                    :style="`margin-left: ${Number(idx) > 0 ? '-6px' : '0'};`" mode="aspectFill" />
                </view>
                <view
                  class="text-[11px] px-2.5 py-1 rounded-full font-semibold active:scale-95 transition-transform duration-200"
                  :class="branch.childCount > 0 ? 'bg-yellow-50 text-accent-dark' : 'bg-yellow-50 text-accent-dark'">
                  {{ branch.childCount > 0 ? `${branch.childCount}个后续` : '创作后续' }}
                </view>
              </view>
            </view>
          </view>
        </view>

        <view v-if="branches.length === 0 && !branchLoading" class="text-center py-16">
          <text class="iconfont icon-lucide-layers text-4xl text-gray-300 mb-3 block"></text>
          <text class="text-sm text-muted">暂无续写分支</text>
          <text class="text-xs text-muted/70 mt-1 block">成为第一个续写这个故事的人</text>
        </view>

        <view v-if="branchLoading" class="text-center py-6">
          <text class="text-sm text-muted">加载中...</text>
        </view>
        <view v-else-if="branchNoMore && branches.length > 0" class="text-center py-6">
          <text class="text-xs text-muted">— 没有更多分支了 —</text>
        </view>
      </view>

      <view class="h-6"></view>
    </scroll-view>

    <!-- Scroll to Top -->
    <view v-if="showBackTop"
      class="fixed z-10 w-10 h-10 rounded-full flex items-center justify-center active:scale-90 transition-transform duration-200"
      style="bottom: 110px; right: 16px; background: rgba(26,26,26,0.5); backdrop-filter: blur(12px);"
      @tap="scrollToTop">
      <text class="iconfont icon-lucide-chevron-up text-white" style="font-size: 18px; font-weight: bold;"></text>
    </view>

    <!-- Fixed Bottom Bar -->
    <view class="bg-white/95 backdrop-blur-xl border-t px-4 flex items-center gap-3 z-20"
      style="position: fixed; bottom: 0; left: 0; right: 0; padding-top: 10px; padding-bottom: calc(10px + env(safe-area-inset-bottom, 0px)); border-color: #E8E3DC;">
      <view id="guide-write-branch"
        class="flex items-center justify-center gap-1.5 bg-ink rounded-2xl font-semibold text-[15px] active:scale-[0.98] transition-transform duration-200"
        style="flex: 1.2; height: 46px; color: #FFC107;" @tap="goBranchCreate(0)">
        <text class="iconfont icon-lucide-pencil-line text-base"></text>
        <text style="font-family: 'Outfit', sans-serif;">写新分支</text>
      </view>
      <view id="guide-mark-story"
        class="flex items-center justify-center gap-1.5 rounded-2xl font-semibold text-[15px] active:scale-[0.98] transition-all duration-200"
        style="flex: 0.9; height: 46px;"
        :style="story.marked ? 'background: #FFF8E1; color: #E5A800;' : 'background: #F5F5F0; color: #8C8A84;'"
        @tap="toggleMark">
        <text class="iconfont icon-lucide-bookmark text-base"
          :style="{ color: story.marked ? '#FFC107' : '#8C8A84' }"></text>
        <text style="font-family: 'Outfit', sans-serif;">{{ story.marked ? '已标记' : '标记' }}</text>
      </view>
    </view>

    <user-guide ref="guideRef" :guideConfig="GUIDE_STEPS_CONFIG" />
    <login-modal ref="loginModalRef" />

    <!-- Toast with fade transition -->
    <view v-if="toastVisible"
      class="fixed top-1/2 left-1/2 -translate-x-1/2 -translate-y-1/2 z-50 pointer-events-none text-white px-6 py-3 rounded-2xl text-sm font-semibold toast-anim"
      :class="toastShow ? 'toast-in' : 'toast-out'"
      style="background: rgba(26,26,26,0.85); backdrop-filter: blur(12px);">
      {{ toastMsg }}
    </view>
  </view>
</template>

<script setup lang="ts">
import { resolveAvatar } from '@/utils/avatar'
import { ref, computed, onMounted, nextTick, getCurrentInstance } from 'vue'
import { useScrollHeight } from '@/utils/useScrollHeight'
import { onShow } from '@dcloudio/uni-app'
import { useUserStore } from '@/stores/user'
import { getStoryDetail, getStoryBranches, getStoryRanking } from '@/apis/modules/story'
import { toggleLike as apiToggleLike, toggleMark as apiToggleMark } from '@/apis/modules/interaction'
import UserGuide from '@/components/user-guide.vue'
import { GUIDE_TYPES } from '@/hooks/useGlobalUserGuide'
import LoginModal from '@/components/login-modal.vue'

const { scrollHeight } = useScrollHeight('#story-hero', 80)
const userStore = useUserStore()
const loginModalRef = ref(null)
const pages = getCurrentPages()
const query = pages[pages.length - 1]?.options || {}
const storyId = Number(query.storyId)
const guideRef = ref(null)

const GUIDE_STEPS_CONFIG = {
  [GUIDE_TYPES.STORY_DETAIL]: [
    {
      targetId: 'guide-story-ranking',
      title: '🏆 续写排行榜',
      description: '点击这里查看谁的续写最受欢迎，也许能从中找到创作灵感。',
      position: 'bottom',
      buttonText: '下一步'
    },
    {
      targetId: 'guide-write-branch',
      title: '✍️ 写新分支',
      description: '从故事设定出发，创作你自己版本的第一章剧情，让更多人来续写你的世界。',
      position: 'top',
      buttonText: '下一步',
      disableScroll: true // 固定在底部，不用卷动
    },
    {
      targetId: 'guide-mark-story',
      title: '🔖 标记故事',
      description: '标记感兴趣的故事，在「我的标记」中随时追更，不错过任何新分支。',
      position: 'top',
      buttonText: '开始探索',
      disableScroll: true // 固定在底部，不用卷动
    }
  ]
}

onShow(() => {
  setTimeout(() => {
    if (guideRef.value) guideRef.value.check()
  }, 800)
})

const story = ref<Record<string, any>>({})
const ranking = ref([])
const branches = ref([])
const settingExpanded = ref(false)
const settingOverflows = ref(false)
const branchPage = ref(1)
const branchLoading = ref(false)
const branchNoMore = ref(false)
const toastMsg = ref('')
const toastVisible = ref(false)
const toastShow = ref(false)
let toastTimer = null
const showBackTop = ref(false)
const showMiniHeader = ref(false)
const instance = getCurrentInstance()

async function loadDetail() {
  const res = await getStoryDetail(storyId)
  if (res && res.data) story.value = res.data
  await nextTick()
  measureIntroOverflow()
}

function measureIntroOverflow() {
  // Measure the hidden unclamped text to get full height
  const query = uni.createSelectorQuery().in(instance)
  query.select('.intro-measure').boundingClientRect(data => {
    if (data && data.height > 0) {
      // 5 lines × leading-relaxed (~1.625) × 15px ≈ 122px
      const max5Lines = 5 * 24.4  // 15px * 1.625 leading-relaxed
      settingOverflows.value = data.height > max5Lines + 4
    }
  }).exec()
}

async function loadRanking() {
  const res = await getStoryRanking(storyId)
  if (res && res.data) ranking.value = res.data.ranking || res.data
}

async function loadBranches() {
  if (branchLoading.value || branchNoMore.value) return
  branchLoading.value = true
  try {
    const res = await getStoryBranches(storyId, { pageNum: branchPage.value, pageSize: 10 })
    if (res && res.rows) {
      const newBranches = res.rows.map(b => ({
        ...b,
        _expanded: false,
        _overflows: false,
      }))
      branches.value = [...branches.value, ...newBranches]
      branchNoMore.value = branches.value.length >= res.total
      branchPage.value++
    }
  } finally {
    branchLoading.value = false
    await nextTick()
    detectBranchOverflows()
  }
}

function detectBranchOverflows() {
  // #ifdef H5
  const els = document.querySelectorAll('.branch-text')
  branches.value.forEach((b, i) => {
    if (els[i] && els[i].scrollHeight > els[i].clientHeight + 2) {
      b._overflows = true
    }
  })
  // #endif
  // #ifndef H5
  // 小程序：用 createSelectorQuery 测未截断的镜像元素高度
  const query = uni.createSelectorQuery().in(instance)
  const max5Lines = 5 * 15 * 1.625
  query.selectAll('.branch-mirror').boundingClientRect(data => {
    if (!Array.isArray(data)) return
    branches.value.forEach((b, i) => {
      if (data[i] && data[i].height > max5Lines + 4) b._overflows = true
    })
  }).exec()
  // #endif
}

function loadMoreBranches() {
  if (!branchNoMore.value) loadBranches()
}

function onScroll(e) {
  const s = e.detail.scrollTop
  showBackTop.value = s > 600
  showMiniHeader.value = s > 180
}

function scrollToTop() {
  uni.pageScrollTo({ scrollTop: 0, duration: 300 })
}

async function toggleBranchLike(branch) {
  if (!userStore.isLoggedIn) { loginModalRef.value.openModal({ title: '点赞需要登录', description: '登录后才能为喜欢的故事点赞。' }); return }
  const res = await apiToggleLike({ targetId: branch.branchId, targetType: 1 })
  if (res) {
    branch.liked = res.data
    branch.likeCount = (branch.likeCount || 0) + (res.data ? 1 : -1)
    if (res.data) {
      branch._bouncing = true
      setTimeout(() => { branch._bouncing = false }, 350)
    }
  }
}

async function toggleMark() {
  if (!userStore.isLoggedIn) { loginModalRef.value.openModal({ title: '标记需要登录', description: '登录后才能标记故事，追踪续写进展。' }); return }
  const res = await apiToggleMark({ storyId, branchId: 0 })
  if (res) {
    story.value.marked = res.data
    toastMsg.value = res.data ? '标记成功' : '已取消'
    // Fade in
    if (toastTimer) clearTimeout(toastTimer)
    toastVisible.value = true
    requestAnimationFrame(() => { toastShow.value = true })
    toastTimer = setTimeout(() => {
      toastShow.value = false
      setTimeout(() => { toastVisible.value = false }, 300)
    }, 2000)
  }
}

function goComments(branchId) {
  uni.navigateTo({ url: `/pages/story/comments?storyId=${storyId}&branchId=${branchId}` })
}

function goBranchCreate(parentId) {
  if (!userStore.isLoggedIn) { loginModalRef.value.openModal({ title: '续写需要登录', description: '登录后才能续写故事，和大家共创世界。' }); return }
  const parentType = parentId === 0 ? 0 : 1
  uni.navigateTo({ url: `/pages/story/branch-create?storyId=${storyId}&parentId=${parentId}&parentType=${parentType}` })
}

function goBranchDetail(branchId) {
  uni.navigateTo({ url: `/pages/story/branch-detail?branchId=${branchId}&storyId=${storyId}` })
}

function goRanking() {
  uni.navigateTo({ url: `/pages/story/ranking?storyId=${storyId}` })
}

function doShare() {
  // #ifndef MP-WEIXIN
  const url = `${location.origin}/#/pages/story/detail?storyId=${storyId}`
  uni.setClipboardData({ data: url, success: () => uni.showToast({ title: '链接已复制', icon: 'success' }) })
  // #endif
}

onMounted(() => {
  loadDetail()
  loadRanking()
  loadBranches()
})
</script>

<script lang="ts">
export default {
  onShareAppMessage(this: any) {
    return {
      title: this.story?.title || '真会写 - 故事社区',
      path: `/pages/story/detail?storyId=${this.storyId}`
    }
  }
}
</script>

<style scoped>
.toast-anim {
  transition: opacity 0.25s cubic-bezier(0.22, 0.61, 0.36, 1);
  opacity: 0;
}

.toast-anim.toast-in {
  opacity: 1;
}

.toast-anim.toast-out {
  opacity: 0;
}
</style>
