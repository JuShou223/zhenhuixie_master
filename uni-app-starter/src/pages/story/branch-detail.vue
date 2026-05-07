<template>
  <view class="min-h-screen bg-bg flex flex-col">
    <!-- Header -->
    <view id="scroll-header" class="bg-accent flex items-center justify-between px-4 sticky top-0 z-10"
      style="padding-top: calc(var(--status-bar-height) + 8px); padding-bottom: 12px; border-radius: 0 0 20px 20px;">
      <view
        class="w-8 h-8 rounded-full flex items-center justify-center active:scale-90 transition-transform duration-200"
        style="background: rgba(255,255,255,0.2); backdrop-filter: blur(8px);" @tap="uni.navigateBack()">
        <text class="iconfont icon-lucide-chevron-left text-white" style="font-size: 18px; font-weight: bold;"></text>
      </view>
      <text class="text-white text-[15px] font-bold truncate mx-4"
        style="font-family: 'Outfit', 'PingFang SC', sans-serif; max-width: 200px;">{{ storyTitle || '未知故事' }}</text>
      <view
        class="w-8 h-8 rounded-full flex items-center justify-center active:scale-90 transition-transform duration-200"
        style="background: rgba(255,255,255,0.2); backdrop-filter: blur(8px);" @tap="doShare">
        <text class="iconfont icon-lucide-share-2 text-white" style="font-size: 16px;"></text>
      </view>
    </view>

    <scroll-view scroll-y :style="{ height: scrollHeight + 'px' }" @scrolltolower="loadMoreChildren" @scroll="onScroll">
      <!-- Context Card -->
      <view class="px-4 pt-4">
        <view id="guide-context-card" class="bg-white rounded-2xl shadow-card p-4">
          <text class="text-xs font-bold text-muted uppercase tracking-wider block mb-3"
            style="font-family: 'Outfit', sans-serif; letter-spacing: 0.08em;">上文摘要</text>

          <view class="flex items-center gap-2.5 mb-3">
            <image :src="resolveAvatar(branch.authorAvatar)" class="w-9 h-9 rounded-full" mode="aspectFill" />
            <view class="flex flex-col">
              <text class="font-semibold text-ink text-sm" style="font-family: 'Outfit', 'PingFang SC', sans-serif;">{{
                branch.authorNickName }}</text>
              <text class="text-[11px] text-muted">《{{ storyTitle || '某作品' }}》</text>
            </view>
            <view class="ml-auto flex items-center gap-1 text-xs font-semibold text-muted bg-bg px-2 py-0.5 rounded-lg">
              <text class="iconfont icon-lucide-heart text-xs"></text>
              <text style="font-variant-numeric: tabular-nums;">{{ branch.likeCount || 0 }}</text>
            </view>
          </view>

          <view class="relative">
            <view class="context-text text-[15px] text-ink/80 leading-relaxed" :class="contextExpanded ? '' : 'line-clamp-5'"
              style="white-space: pre-wrap; font-family: 'Outfit', 'PingFang SC', sans-serif;">{{ branch.content }}
            </view>
            <view v-if="!contextExpanded && contextLong"
              class="absolute bottom-0 left-0 right-0 flex items-end justify-center"
              style="height: 52px; background: linear-gradient(to bottom, transparent 0%, #FDFCF8 55%);">
              <text class="text-accent-dark font-semibold text-sm pb-0.5 active:opacity-70 transition-opacity"
                @tap="contextExpanded = true">展开全文</text>
            </view>
          </view>
          <text v-if="contextExpanded && contextLong"
            class="text-accent-dark font-semibold text-sm mt-1 block active:opacity-70 transition-opacity"
            @tap="contextExpanded = false">收起</text>
        </view>
      </view>

      <!-- Prompt Bar -->
      <view class="mx-4 mt-4">
        <view class="bg-ink rounded-2xl text-white/90 text-xs font-semibold py-2.5 text-center"
          style="font-family: 'Outfit', sans-serif; letter-spacing: 0.02em;">
          无限分支创作模式，下面是其他小伙伴创作的不同分支剧情
        </view>
      </view>

      <!-- Child Branches -->
      <view class="px-4 pt-3 flex-1">
        <view v-if="children.length > 0" class="flex flex-col">
          <view v-for="child in children" :key="child.branchId" class="mb-5 pb-5 border-b border-line last:border-0"
            @tap="goChildDetail(child.branchId)">
            <!-- Author & Like -->
            <view class="flex justify-between items-center mb-3">
              <view class="flex items-center gap-2.5">
                <image :src="resolveAvatar(child.authorAvatar)" class="w-9 h-9 rounded-full" mode="aspectFill" />
                <text class="font-semibold text-ink text-sm"
                  style="font-family: 'Outfit', 'PingFang SC', sans-serif;">{{ child.authorNickName }}</text>
              </view>
              <view
                class="flex items-center gap-1 text-xs font-semibold active:scale-90 transition-transform duration-200"
                :class="child._liked ? 'text-accent' : 'text-muted'" @tap.stop="toggleChildLike(child)">
                <text class="iconfont icon-lucide-heart text-sm" :class="{ 'like-bounce': child._bouncing }"></text>
                <text style="font-variant-numeric: tabular-nums;">{{ child.likeCount || 0 }}</text>
              </view>
            </view>

            <!-- Chapter Label -->
            <view class="inline-flex items-center gap-1.5 mb-3">
              <view class="w-1 h-4 rounded-full bg-accent"></view>
              <text class="text-accent-dark font-bold text-xs" style="font-family: 'Outfit', sans-serif;">
                第{{ child.chapterNum }}章{{ child.chapterTitle ? ' · ' + child.chapterTitle : '' }}
              </text>
            </view>

            <!-- Content -->
            <view class="mb-1">
              <view class="relative">
                <view class="child-text text-[15px] text-ink/80 leading-relaxed" :class="child._expanded ? '' : 'line-clamp-5'"
                  style="white-space: pre-wrap; font-family: 'Outfit', 'PingFang SC', sans-serif;">{{ child.content }}
                </view>
                <view v-if="!child._expanded && child._contentLong"
                  class="absolute bottom-0 left-0 right-0 flex items-end justify-center"
                  style="height: 52px; background: linear-gradient(to bottom, transparent 0%, #FDFCF8 55%);">
                  <text class="text-accent-dark font-semibold text-sm pb-0.5 active:opacity-70"
                    @tap.stop="child._expanded = true">展开全文</text>
                </view>
              </view>
              <text v-if="child._expanded && child._contentLong"
                class="text-accent-dark font-semibold text-sm mt-1 block active:opacity-70"
                @tap.stop="child._expanded = false">收回</text>
            </view>

            <!-- Footer -->
            <view class="flex justify-between items-end mt-4">
              <view
                class="bg-bg text-muted px-3 py-1 rounded-full text-[11px] font-medium active:bg-line transition-colors duration-200"
                @tap.stop="goComments(child.branchId)">
                {{ child.commentCount || 0 }}条评论
              </view>
              <view class="flex items-center gap-1.5">
                <view v-if="child.childCount > 0" class="flex">
                  <image v-for="(c, idx) in (child._childAvatars || []).slice(0, 3)" :key="idx" :src="resolveAvatar(c)"
                    class="w-5 h-5 rounded-full border-2 border-white"
                    :style="`margin-left: ${idx > 0 ? '-6px' : '0'};`" mode="aspectFill" />
                </view>
                <text class="text-[11px] px-2.5 py-1 rounded-full font-semibold bg-yellow-50 text-accent-dark">
                  {{ child.childCount > 0 ? `${child.childCount}个后续` : '暂无后续，点击创作' }}
                </text>
              </view>
            </view>
          </view>
        </view>

        <view v-if="children.length === 0 && !childLoading" class="flex flex-col items-center justify-center py-16">
          <text class="iconfont icon-lucide-layers text-4xl text-gray-300 mb-3"></text>
          <text class="text-sm text-muted">暂无后续展示</text>
        </view>
        <view v-if="childLoading" class="text-center py-6">
          <text class="text-sm text-muted">加载中...</text>
        </view>
      </view>
      <view class="h-4"></view>
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
      <view id="guide-create-followup"
        class="flex items-center justify-center gap-1.5 bg-ink rounded-2xl font-semibold text-[15px] active:scale-[0.98] transition-transform duration-200"
        style="flex: 1.2; height: 46px; color: #FFC107;" @tap="goBranchCreate">
        <text class="iconfont icon-lucide-pencil-line text-base"></text>
        <text style="font-family: 'Outfit', sans-serif;">创作后续</text>
      </view>
      <view id="guide-mark-branch"
        class="flex items-center justify-center gap-1.5 rounded-2xl font-semibold text-[15px] active:scale-[0.98] transition-all duration-200"
        style="flex: 0.9; height: 46px;"
        :style="branch.marked ? 'background: #FFF8E1; color: #E5A800;' : 'background: #F5F5F0; color: #8C8A84;'"
        @tap="toggleMark">
        <text class="iconfont icon-lucide-bookmark text-base"
          :style="{ color: branch.marked ? '#FFC107' : '#8C8A84' }"></text>
        <text style="font-family: 'Outfit', sans-serif;">{{ branch.marked ? '已标记' : '标记' }}</text>
      </view>
    </view>

    <user-guide ref="guideRef" :guideConfig="GUIDE_STEPS_CONFIG" />
    <login-modal ref="loginModalRef" />

    <!-- Toast -->
    <view v-if="toastVisible"
      class="fixed top-1/2 left-1/2 -translate-x-1/2 -translate-y-1/2 z-50 pointer-events-none text-white px-6 py-3 rounded-2xl text-sm font-semibold toast-anim"
      :class="toastShow ? 'toast-in' : 'toast-out'"
      style="background: rgba(26,26,26,0.85); backdrop-filter: blur(12px);">
      {{ toastMsg }}
    </view>
  </view>
</template>

<script setup>
import { resolveAvatar } from '@/utils/avatar.js'
import { ref, computed, onMounted, nextTick } from 'vue'
import { useScrollHeight } from '@/utils/useScrollHeight.js'
import { onShow } from '@dcloudio/uni-app'
import { useUserStore } from '@/stores/user.js'
import { getBranchDetail, getBranchChildren } from '@/apis/modules/branch.js'
import { toggleLike as apiToggleLike, toggleMark as apiToggleMark } from '@/apis/modules/interaction.js'
import UserGuide from '@/components/user-guide.vue'
import { GUIDE_TYPES } from '@/hooks/useGlobalUserGuide'
import LoginModal from '@/components/login-modal.vue'

const { scrollHeight } = useScrollHeight('#scroll-header', 80)
const userStore = useUserStore()
const loginModalRef = ref(null)
const pages = getCurrentPages()
const query = pages[pages.length - 1]?.options || {}
const branchId = Number(query.branchId)
const storyId = Number(query.storyId)
const guideRef = ref(null)

const GUIDE_STEPS_CONFIG = {
  [GUIDE_TYPES.BRANCH_DETAIL]: [
    {
      targetId: 'guide-context-card',
      title: '📖 上文摘要',
      description: '这里展示当前分支的上文内容，续写前先读懂剧情走向，保持故事前后呼应。',
      position: 'bottom',
      buttonText: '下一步'
    },
    {
      targetId: 'guide-create-followup',
      title: '✍️ 创作后续',
      description: '在这个分支的基础上继续创作，你的续写会成为这条剧情线的下一章节。',
      position: 'top',
      buttonText: '下一步',
      disableScroll: true // 固定在底部，不用卷动
    },
    {
      targetId: 'guide-mark-branch',
      title: '🔖 标记分支',
      description: '标记精彩分支，在「我的标记」中查看更新动态，追踪后续剧情走向。',
      position: 'top',
      buttonText: '开始阅读',
      disableScroll: true // 固定在底部，不用卷动
    }
  ]
}

onShow(() => {
  setTimeout(() => {
    if (guideRef.value) guideRef.value.check()
  }, 800)
})

const branch = ref({})
const children = ref([])
const contextExpanded = ref(false)
const childPage = ref(1)
const childLoading = ref(false)
const childNoMore = ref(false)
const toastMsg = ref('')
const toastVisible = ref(false)
const toastShow = ref(false)
let toastTimer = null
const showBackTop = ref(false)
const storyTitle = ref('')

const contextLong = ref(false)

async function loadBranch() {
  const res = await getBranchDetail(branchId)
  if (res && res.data) {
    branch.value = res.data
    storyTitle.value = res.data.storyTitle || ''
    await nextTick()
    // #ifdef H5
    const el = document.querySelector('.context-text')
    if (el) contextLong.value = el.scrollHeight > el.clientHeight + 2
    // #endif
  }
}

async function loadChildren() {
  if (childLoading.value || childNoMore.value) return
  childLoading.value = true
  try {
    const res = await getBranchChildren(branchId, { pageNum: childPage.value, pageSize: 5 })
    if (res && res.rows) {
      const newChildren = res.rows.map(b => ({
        ...b,
        _expanded: false,
        _liked: false,
        _contentLong: false,
      }))
      children.value = [...children.value, ...newChildren]
      childNoMore.value = children.value.length >= res.total
      childPage.value++
    }
  } finally {
    childLoading.value = false
    await nextTick()
    // #ifdef H5
    const els = document.querySelectorAll('.child-text')
    children.value.forEach((c, i) => {
      if (els[i] && els[i].scrollHeight > els[i].clientHeight + 2) c._contentLong = true
    })
    // #endif
  }
}

function loadMoreChildren() {
  if (!childNoMore.value) loadChildren()
}

function onScroll(e) {
  showBackTop.value = e.detail.scrollTop > 500
}

async function toggleChildLike(child) {
  if (!userStore.isLoggedIn) { loginModalRef.value.openModal({ title: '点赞需要登录', description: '登录后才能为喜欢的故事点赞。' }); return }
  const res = await apiToggleLike({ targetId: child.branchId, targetType: 1 })
  if (res) {
    child._liked = res.data
    child.likeCount = (child.likeCount || 0) + (res.data ? 1 : -1)
    if (res.data) {
      child._bouncing = true
      setTimeout(() => { child._bouncing = false }, 450)
    }
  }
}

async function toggleMark() {
  if (!userStore.isLoggedIn) { loginModalRef.value.openModal({ title: '标记需要登录', description: '登录后才能标记故事，追踪续写进展。' }); return }
  const res = await apiToggleMark({ storyId, branchId })
  if (res) {
    branch.value.marked = res.data
    toastMsg.value = res.data ? '标记成功' : '已取消'
    if (toastTimer) clearTimeout(toastTimer)
    toastVisible.value = true
    requestAnimationFrame(() => { toastShow.value = true })
    toastTimer = setTimeout(() => {
      toastShow.value = false
      setTimeout(() => { toastVisible.value = false }, 300)
    }, 2000)
  }
}

function scrollToTop() {
  uni.pageScrollTo({ scrollTop: 0, duration: 300 })
}

function goComments(bid) {
  uni.navigateTo({ url: `/pages/story/comments?storyId=${storyId}&branchId=${bid}` })
}

function goBranchCreate() {
  if (!userStore.isLoggedIn) { loginModalRef.value.openModal({ title: '续写需要登录', description: '登录后才能续写故事，和大家共创世界。' }); return }
  uni.navigateTo({ url: `/pages/story/branch-create?storyId=${storyId}&parentId=${branchId}&parentType=1` })
}

function goChildDetail(childBranchId) {
  uni.navigateTo({ url: `/pages/story/branch-detail?branchId=${childBranchId}&storyId=${storyId}` })
}

function doShare() {
  // #ifndef MP-WEIXIN
  const url = `${location.origin}/#/pages/story/branch-detail?branchId=${branchId}&storyId=${storyId}`
  uni.setClipboardData({ data: url, success: () => uni.showToast({ title: '链接已复制', icon: 'success' }) })
  // #endif
}

onMounted(() => {
  loadBranch()
  loadChildren()
})
</script>

<script>
export default {
  onShareAppMessage() {
    return {
      title: this.branch?.chapterTitle || '真会写 - 分支详情',
      path: `/pages/story/branch-detail?branchId=${this.branchId}&storyId=${this.storyId}`
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
