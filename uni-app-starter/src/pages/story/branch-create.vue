<template>
  <view class="min-h-screen bg-bg flex flex-col" style="padding-bottom: env(safe-area-inset-bottom, 0px);">
    <!-- Header -->
    <view class="bg-accent flex items-center justify-between px-4 sticky top-0 z-10"
      style="padding-top: calc(var(--status-bar-height) + 8px); padding-bottom: 12px; border-radius: 0 0 20px 20px;">
      <view class="w-8 h-8 rounded-full flex items-center justify-center active:scale-90 transition-transform duration-200"
        style="background: rgba(255,255,255,0.2); backdrop-filter: blur(8px);" @tap="uni.navigateBack()">
        <text class="iconfont icon-lucide-x text-white" style="font-size: 16px; font-weight: bold;"></text>
      </view>
      <text class="text-white/80 text-sm font-semibold" style="font-family: 'Outfit', sans-serif;">{{ contentLen }}/300</text>
      <view id="guide-branch-publish" class="active:opacity-70 transition-opacity" @tap="doCreate">
        <text class="text-[15px] font-bold"
          :style="{ color: canPublish ? '#1A1A1A' : 'rgba(255,255,255,0.4)', fontFamily: 'Outfit, sans-serif' }">{{ submitting ? '发布中...' : '发布' }}</text>
      </view>
    </view>

    <!-- Context Card -->
    <view class="px-4 pt-4">
      <view class="bg-white rounded-2xl shadow-card p-4">
        <view v-if="parentType === 0">
          <view class="flex items-center gap-1.5 mb-2">
            <view class="w-1 h-4 rounded-full bg-accent"></view>
            <text class="font-bold text-ink text-sm" style="font-family: 'Outfit', sans-serif;">故事设定</text>
          </view>
        </view>
        <view v-else>
          <text class="text-xs font-bold text-muted uppercase tracking-wider block mb-2"
            style="font-family: 'Outfit', sans-serif; letter-spacing: 0.08em;">上文摘要</text>
          <view class="flex items-center gap-2.5 mb-3">
            <image :src="resolveAvatar(parentAuthorAvatar)" class="w-8 h-8 rounded-full" mode="aspectFill" />
            <view class="flex flex-col">
              <text class="font-semibold text-ink text-sm"
                style="font-family: 'Outfit', 'PingFang SC', sans-serif;">{{ parentAuthorName }}</text>
              <text class="text-[11px] text-muted">《{{ parentStoryTitle }}》</text>
            </view>
          </view>
          <view class="inline-flex items-center gap-1.5 mb-3">
            <view class="w-1 h-4 rounded-full bg-accent"></view>
            <text class="text-accent-dark font-bold text-xs" style="font-family: 'Outfit', sans-serif;">
              第{{ chapterNum - 1 }}章{{ parentChapterTitle ? ' · ' + parentChapterTitle : '' }}
            </text>
          </view>
        </view>

        <view class="relative">
          <view class="text-[15px] text-ink/80 leading-relaxed"
            :class="contextExpanded ? '' : 'line-clamp-3'"
            style="white-space: pre-wrap; font-family: 'Outfit', 'PingFang SC', sans-serif;">{{ parentContent || '加载中...' }}</view>
          <view v-if="!contextExpanded && contextLong"
            class="absolute bottom-0 left-0 right-0 flex items-end justify-center"
            style="height: 52px; background: linear-gradient(to bottom, transparent 0%, #FDFCF8 55%);">
            <text class="text-accent-dark font-semibold text-sm pb-0.5 active:opacity-70"
              @tap="contextExpanded = true">展开全文</text>
          </view>
        </view>
        <text v-if="contextExpanded && contextLong"
          class="text-accent-dark font-semibold text-sm mt-1 block active:opacity-70"
          @tap="contextExpanded = false">收起</text>
      </view>
    </view>

    <!-- Input Form -->
    <view class="px-4 pt-4 flex-1 flex flex-col">
      <view class="inline-flex items-center gap-1.5 mb-5">
        <view class="w-1 h-4 rounded-full bg-accent"></view>
        <text class="text-accent-dark font-bold text-xs" style="font-family: 'Outfit', sans-serif;">
          第{{ chapterNum }}章剧情续写
        </text>
      </view>

      <!-- Title -->
      <view id="guide-chapter-title">
        <input v-model="form.chapterTitle" placeholder="写一个亮眼的章节标题..."
          placeholder-style="color: #8C8A84; font-size: 14px;"
          class="text-[15px] text-ink w-full bg-transparent mb-3 font-semibold"
          style="border: none; outline: none; padding: 0; font-family: 'Outfit', 'PingFang SC', sans-serif;"
          maxlength="50" />
      </view>

      <view class="bg-line" style="height: 1px; margin-bottom: 16px;"></view>

      <!-- Content -->
      <view id="guide-branch-content" style="flex: 1; display: flex; flex-direction: column;">
        <textarea v-model="form.content" placeholder="围绕设定扩展你的剧情，上限300字"
          placeholder-style="color: #8C8A84; line-height: 1.6; font-size: 14px;"
          class="w-full text-[15px] text-ink/80 leading-relaxed bg-transparent"
          style="flex: 1; border: none; resize: none; outline: none; padding: 0; font-family: 'Outfit', 'PingFang SC', sans-serif;"
          maxlength="300" auto-height :focus="true" />
      </view>
    </view>

    <user-guide ref="guideRef" :guideConfig="GUIDE_STEPS_CONFIG" @action="handleGuideAction" />

    <!-- Bottom Hint -->
    <view class="pb-6 px-4 flex flex-col items-center">
      <view class="bg-ink text-accent text-[12px] font-bold px-5 py-2 rounded-2xl text-center"
        style="font-family: 'Outfit', sans-serif;">
        {{ parentType === 0 ? '注意：基于故事设定开启第一章' : '注意呼应上文摘要，别写错啦' }}
      </view>
    </view>
  </view>
</template>

<script setup>
import { resolveAvatar } from '@/utils/avatar.js'
import { ref, computed, onMounted } from 'vue'
import { onShow } from '@dcloudio/uni-app'
import { getStoryDetail } from '@/apis/modules/story.js'
import { getBranchDetail, createBranch } from '@/apis/modules/branch.js'
import UserGuide from '@/components/user-guide.vue'

const pages = getCurrentPages()
const query = pages[pages.length - 1]?.options || {}
const storyId = Number(query.storyId)
const parentId = Number(query.parentId || 0)
const parentType = Number(query.parentType || 0)

const guideRef = ref(null)

const BRANCH_CREATE_GUIDE = 'branch_create_guide'
const GUIDE_STEPS_CONFIG = {
  [BRANCH_CREATE_GUIDE]: [
    {
      targetId: 'guide-chapter-title',
      title: '✏️ 起个章节标题',
      description: '一个醒目的标题能让读者在分支列表中快速被吸引，也方便后续创作者理解你的剧情走向。',
      position: 'bottom',
      buttonText: '下一步'
    },
    {
      targetId: 'guide-branch-content',
      title: '📝 续写剧情',
      description: '围绕设定展开你的剧情，15 ~ 300 字。注意呼应上文，给下一位创作者留下发挥空间。',
      position: 'top',
      buttonText: '下一步'
    },
    {
      targetId: 'guide-branch-publish',
      title: '🚀 发布分支',
      description: '写好后点击右上角「发布」，你的续写将加入故事分支树，等待更多人继续创作。',
      position: 'bottom',
      action: 'publish',
      buttonText: '去发布'
    }
  ]
}

onShow(() => {
  setTimeout(() => {
    if (guideRef.value) guideRef.value.startGuide(BRANCH_CREATE_GUIDE)
  }, 800)
})

function handleGuideAction(actionName) {
  if (actionName === 'publish') doCreate()
}

const form = ref({ chapterTitle: '', content: '' })
const parentContent = ref('')
const parentAuthorName = ref('')
const parentAuthorAvatar = ref('')
const parentStoryTitle = ref('')
const parentChapterTitle = ref('')
const chapterNum = ref(1)
const submitting = ref(false)
const contextExpanded = ref(false)

const contentLen = computed(() => form.value.content.length)
const canPublish = computed(() => contentLen.value >= 15 && contentLen.value <= 300 && !submitting.value)
const contextLong = computed(() => (parentContent.value || '').length > 200)

async function loadParent() {
  if (parentType === 0) {
    const res = await getStoryDetail(storyId)
    if (res && res.data) {
      parentContent.value = res.data.settingContent
      chapterNum.value = 1
    }
  } else {
    const res = await getBranchDetail(parentId)
    if (res && res.data) {
      parentContent.value = res.data.content
      chapterNum.value = (res.data.chapterNum || 1) + 1
      parentAuthorName.value = res.data.authorNickName || ''
      parentAuthorAvatar.value = res.data.authorAvatar || ''
      parentStoryTitle.value = res.data.storyTitle || ''
      parentChapterTitle.value = res.data.chapterTitle || ''
    }
  }
}

async function doCreate() {
  if (!canPublish.value || submitting.value) return

  submitting.value = true
  try {
    const res = await createBranch({
      storyId,
      parentId,
      parentType,
      chapterTitle: form.value.chapterTitle.trim(),
      content: form.value.content.trim(),
    })
    if (res && res.data) {
      uni.showToast({ title: '发布成功', icon: 'success' })
      setTimeout(() => {
        uni.redirectTo({ url: `/pages/story/branch-detail?branchId=${res.data}&storyId=${storyId}` })
      }, 600)
    }
  } catch (e) {
    uni.showToast({ title: e?.data?.msg || e?.msg || '发布失败', icon: 'none' })
  } finally {
    submitting.value = false
  }
}

onMounted(loadParent)
</script>
