<template>
  <view class="min-h-screen bg-bg flex flex-col" style="padding-bottom: env(safe-area-inset-bottom, 0px);">
    <!-- Header -->
    <view class="bg-accent flex flex-col items-center relative"
      style="padding-top: calc(var(--status-bar-height) + 12px); padding-bottom: 32px; border-radius: 0 0 24px 24px;">
      <view class="absolute left-4" :style="{ top: `calc(var(--status-bar-height) + 8px)` }" @tap="uni.navigateBack()">
        <view class="w-8 h-8 rounded-full flex items-center justify-center active:scale-90 transition-transform duration-200"
          style="background: rgba(255,255,255,0.2); backdrop-filter: blur(8px);">
          <text class="iconfont icon-lucide-chevron-left text-white" style="font-size: 18px; font-weight: bold;"></text>
        </view>
      </view>
      <text class="text-white text-lg font-bold tracking-wider mb-4"
        style="font-family: 'Outfit', sans-serif; letter-spacing: 0.04em;">{{ storyTitle || '新内容更新' }}</text>
      <image
        v-if="authorAvatar"
        :src="resolveAvatar(authorAvatar)"
        class="rounded-full border-2 border-white/60"
        style="width: 56px; height: 56px;"
        mode="aspectFill"
      />
    </view>

    <!-- Context Card -->
    <view class="px-4 -mt-4 relative z-10">
      <view class="bg-white rounded-2xl shadow-card p-4">
        <view class="flex items-center gap-1.5 mb-2">
          <view class="w-1 h-4 rounded-full bg-accent"></view>
          <text class="font-bold text-ink text-sm" style="font-family: 'Outfit', sans-serif;">{{ contextLabel }}</text>
        </view>
        <view class="text-[14px] text-ink/70 leading-relaxed pl-2"
          :class="contextExpanded ? '' : 'line-clamp-2'"
          style="white-space: pre-wrap; font-family: 'Outfit', 'PingFang SC', sans-serif;">{{ parentContent }}</view>
        <text v-if="contextLong" class="text-accent-dark font-semibold text-sm mt-2 block pl-2 active:opacity-70 transition-opacity"
          @tap="contextExpanded = !contextExpanded">{{ contextExpanded ? '收起' : '展开' }}</text>
      </view>
    </view>

    <!-- Prompt + Unread -->
    <view id="scroll-header" class="mx-4 mt-4">
      <view class="bg-ink rounded-2xl text-white/90 text-xs font-semibold py-2.5 text-center"
        style="font-family: 'Outfit', sans-serif; letter-spacing: 0.02em;">
        无限分支创作模式，下面是其他小伙伴创作的不同分支剧情
      </view>
      <view v-if="newBranches.length > 0" class="text-center mt-3">
        <text class="text-accent-dark text-xs font-bold"
          style="font-family: 'Outfit', sans-serif;">新增{{ newBranches.length }}条未读内容</text>
      </view>
    </view>

    <!-- Updates List -->
    <scroll-view scroll-y class="mt-3" :style="{ height: (scrollHeight - 12) + 'px' }">
      <view class="px-4">
        <view v-if="displayBranches.length > 0" class="flex flex-col">
          <view
            v-for="branch in displayBranches"
            :key="branch.branchId"
            class="mb-5 pb-5 border-b border-line last:border-0"
            @tap="goBranchDetail(branch.branchId)"
          >
            <!-- Author & Like -->
            <view class="flex justify-between items-center mb-3">
              <view class="flex items-center gap-2.5">
                <image :src="resolveAvatar(branch.authorAvatar)" class="w-9 h-9 rounded-full" mode="aspectFill" />
                <view class="flex flex-col">
                  <text class="font-semibold text-ink text-sm"
                    style="font-family: 'Outfit', 'PingFang SC', sans-serif;">{{ branch.authorNickName }}</text>
                  <text class="text-[11px] text-muted">《{{ storyTitle || '某作品' }}》</text>
                </view>
              </view>
              <view class="flex items-center gap-1 text-xs font-semibold"
                :class="branch.liked ? 'text-accent' : 'text-muted'">
                <text class="iconfont icon-lucide-heart text-sm"></text>
                <text style="font-variant-numeric: tabular-nums;">{{ branch.likeCount || 0 }}</text>
              </view>
            </view>

            <!-- Chapter Label -->
            <view class="inline-flex items-center gap-1.5 mb-3">
              <view class="w-1 h-4 rounded-full bg-accent"></view>
              <text class="text-accent-dark font-bold text-xs" style="font-family: 'Outfit', sans-serif;">
                新开篇
              </text>
            </view>

            <!-- Content -->
            <view class="mb-1">
              <view class="relative">
                <view
                  class="text-[15px] text-ink/80 leading-relaxed"
                  :class="branch._expanded ? '' : 'line-clamp-5'"
                  style="white-space: pre-wrap; font-family: 'Outfit', 'PingFang SC', sans-serif;">{{ branch.content }}</view>
                <view
                  v-if="!branch._expanded && branch._contentLong"
                  class="absolute bottom-0 left-0 right-0 flex items-end justify-center"
                  style="height: 52px; background: linear-gradient(to bottom, transparent 0%, #FDFCF8 55%);">
                  <text class="text-accent-dark font-semibold text-sm pb-0.5 active:opacity-70 transition-opacity"
                    @tap.stop="branch._expanded = true">展开全文</text>
                </view>
              </view>
              <text v-if="branch._expanded && branch._contentLong"
                class="text-accent-dark font-semibold text-sm mt-1 block active:opacity-70 transition-opacity"
                @tap.stop="branch._expanded = false">收回</text>
            </view>

            <!-- Footer -->
            <view class="flex justify-between items-end mt-4">
              <view class="bg-bg text-muted px-3 py-1 rounded-full text-[11px] font-medium">
                {{ branch.commentCount || 0 }}条评论
              </view>
              <view class="flex items-center gap-1.5">
                <view v-if="branch.childCount > 0" class="flex">
                  <image
                    v-for="(c, idx) in (branch._childAvatars || []).slice(0, 3)"
                    :key="idx"
                    :src="resolveAvatar(c)"
                    class="w-5 h-5 rounded-full border-2 border-white"
                    :style="`margin-left: ${idx > 0 ? '-6px' : '0'};`"
                    mode="aspectFill"
                  />
                </view>
                <text class="text-[11px] px-2.5 py-1 rounded-full font-semibold bg-yellow-50 text-accent-dark">
                  {{ branch.childCount > 0 ? `${branch.childCount}个后续` : '暂无后续' }}
                </text>
              </view>
            </view>
          </view>

          <!-- View All -->
          <view v-if="newBranches.length > 5 && !showAll" class="mt-4 mb-8 flex justify-center">
            <view class="bg-ink text-accent text-[13px] font-semibold px-8 py-2.5 rounded-2xl active:scale-95 transition-transform duration-200"
              style="font-family: 'Outfit', sans-serif;" @tap="showAll = true">
              查看全部 {{ newBranches.length - 5 }} 条
            </view>
          </view>

          <view v-if="showAll" class="mt-6 mb-4 text-center">
            <text class="text-xs text-muted">没有更多了</text>
          </view>
        </view>

        <view v-if="newBranches.length === 0 && !loading" class="flex flex-col items-center justify-center py-20">
          <text class="iconfont icon-lucide-bookmark-check text-5xl text-gray-300 mb-3"></text>
          <text class="text-sm text-muted">暂无新内容</text>
        </view>
        <view v-if="loading" class="text-center py-8">
          <text class="text-sm text-muted">加载中...</text>
        </view>
      </view>
      <view class="h-8"></view>
    </scroll-view>
  </view>
</template>

<script setup>
import { resolveAvatar } from '@/utils/avatar.js'
import { ref, computed, onMounted } from 'vue'
import { useScrollHeight } from '@/utils/useScrollHeight.js'
import { getBranchChildren } from '@/apis/modules/branch.js'
import { getStoryBranches, getStoryDetail } from '@/apis/modules/story.js'
import { updateMarkRead } from '@/apis/modules/interaction.js'

const { scrollHeight } = useScrollHeight('#scroll-header')
const pages = getCurrentPages()
const query = pages[pages.length - 1]?.options || {}
const storyId = Number(query.storyId || 0)
const branchId = Number(query.branchId || 0)

const newBranches = ref([])
const showAll = ref(false)
const loading = ref(false)
const storyTitle = ref('')
const authorAvatar = ref('')
const parentContent = ref('')
const contextExpanded = ref(false)
const contextLabel = ref('设定')

const displayBranches = computed(() =>
  showAll.value ? newBranches.value : newBranches.value.slice(0, 5)
)
const contextLong = computed(() => (parentContent.value || '').length > 150)

async function loadContext() {
  if (branchId > 0) {
    contextLabel.value = '上文摘要'
  }
  const res = await getStoryDetail(storyId)
  if (res && res.data) {
    parentContent.value = res.data.settingContent
    storyTitle.value = res.data.title
    authorAvatar.value = res.data.authorAvatar
  }
}

async function loadNewBranches() {
  loading.value = true
  try {
    let res
    if (branchId > 0) {
      res = await getBranchChildren(branchId, { pageNum: 1, pageSize: 100 })
    } else {
      res = await getStoryBranches(storyId, { pageNum: 1, pageSize: 100 })
    }
    if (res && res.rows) {
      newBranches.value = res.rows.map(b => ({
        ...b,
        _expanded: false,
        _contentLong: (b.content || '').length > 250,
      }))
    }
    updateMarkRead({ storyId, branchId }).catch(() => {})
  } finally {
    loading.value = false
  }
}

function goBranchDetail(bid) {
  uni.navigateTo({ url: `/pages/story/branch-detail?branchId=${bid}&storyId=${storyId}` })
}

onMounted(() => {
  loadContext()
  loadNewBranches()
})
</script>
