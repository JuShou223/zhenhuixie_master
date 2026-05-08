<template>
  <view class="min-h-screen flex flex-col"
    style="background: #FDFCF8; padding-bottom: env(safe-area-inset-bottom, 0px);">

    <!-- Header -->
    <view id="scroll-header" class="bg-accent flex items-center justify-between px-4 sticky top-0 z-10"
      style="padding-top: calc(var(--status-bar-height) + 8px); padding-bottom: 12px; border-radius: 0 0 20px 20px;">
      <view
        class="w-8 h-8 rounded-full flex items-center justify-center active:scale-90 transition-transform duration-200"
        style="background: rgba(255,255,255,0.2); backdrop-filter: blur(8px);" @tap="uni.navigateBack()">
        <text class="iconfont icon-lucide-chevron-left text-ink" style="font-size: 18px;"></text>
      </view>

      <text class="text-ink font-bold text-base"
        style="font-family: 'Outfit', sans-serif; letter-spacing: -0.01em;">发布故事</text>

      <view id="guide-publish-btn" class="active:opacity-60 transition-opacity" @tap="doCreate">
        <text class="text-[15px] font-bold"
          :style="{ color: canPublish ? '#1A1A1A' : 'rgba(26,26,26,0.3)', fontFamily: 'Outfit, sans-serif' }">{{
            submitting ? '发布中…' : '发布' }}</text>
      </view>
    </view>

    <scroll-view scroll-y :style="{ height: scrollHeight + 'px' }">
      <view class="p-4 flex flex-col gap-4">

        <!-- Cover Upload -->
        <view id="guide-cover" class="bg-white rounded-2xl overflow-hidden shadow-card relative" style="height: 180px;"
          @tap="chooseCover">
          <image v-if="form.cover" :src="form.cover" class="absolute inset-0 w-full h-full" mode="aspectFill" />
          <view v-if="!form.cover" class="absolute inset-0 flex flex-col items-center justify-center gap-2">
            <view class="w-12 h-12 rounded-2xl flex items-center justify-center"
              style="background: #FFF8E1; border: 1.5px dashed #FFD54F;">
              <text class="iconfont icon-lucide-plus text-accent" style="font-size: 22px;"></text>
            </view>
            <text class="text-sm font-medium" style="font-family: 'Outfit', sans-serif; color: #1A1A1A;">添加封面</text>
            <text class="text-xs text-muted" style="font-family: 'Outfit', sans-serif;">必填，吸引读者点击</text>
          </view>
          <view v-if="form.cover" class="absolute bottom-0 left-0 right-0 flex items-center justify-center py-2"
            style="background: linear-gradient(to top, rgba(0,0,0,0.45) 0%, transparent 100%);">
            <text class="text-white text-xs" style="font-family: 'Outfit', sans-serif; font-weight: 500;">点击更换封面</text>
          </view>
        </view>

        <!-- Title + Content Card -->
        <view class="bg-white rounded-2xl shadow-card p-4">

          <!-- Title -->
          <view id="guide-story-title">
            <view class="flex items-center gap-1.5 mb-3">
              <view class="w-1 h-4 rounded-full bg-accent"></view>
              <text class="font-bold text-ink text-sm" style="font-family: 'Outfit', sans-serif;">故事标题</text>
            </view>
            <view class="flex items-start gap-2 mb-1">
              <input v-model="form.title" placeholder="用一句话吸引读者点进来…"
                placeholder-style="color: #8C8A84; font-size: 16px; font-family: 'Outfit', 'PingFang SC', sans-serif;"
                class="flex-1 text-[16px] text-ink font-semibold bg-transparent"
                style="border: none; outline: none; padding: 0; font-family: 'Outfit', 'PingFang SC', sans-serif;"
                maxlength="100" />
              <text class="text-xs text-muted flex-shrink-0 mt-1" style="font-family: 'Outfit', sans-serif;">{{
                form.title.length }}/100</text>
            </view>
          </view>

          <view class="bg-line" style="height: 1px; margin: 14px 0;"></view>

          <!-- Setting Content -->
          <view id="guide-story-content">
            <view class="flex items-center gap-1.5 mb-3">
              <view class="w-1 h-4 rounded-full bg-accent"></view>
              <text class="font-bold text-ink text-sm" style="font-family: 'Outfit', sans-serif;">故事设定</text>
            </view>
            <textarea v-model="form.settingContent" placeholder="写下世界观、主角与开端。设定越有趣，续写的人就越多…"
              placeholder-style="color: #8C8A84; line-height: 1.6; font-size: 15px; font-family: 'Outfit', 'PingFang SC', sans-serif;"
              class="w-full text-[15px] text-ink leading-relaxed bg-transparent"
              style="border: none; resize: none; outline: none; padding: 0; min-height: 160px; font-family: 'Outfit', 'PingFang SC', sans-serif; opacity: 0.85;"
              maxlength="300" auto-height />
          </view>
          <view class="flex justify-between items-center mt-2">
            <text class="text-xs text-muted" style="font-family: 'Outfit', sans-serif;">至少 15 字</text>
            <text class="text-xs" :style="{
              color: form.settingContent.length >= 15 ? '#E5A800' : '#8C8A84',
              fontFamily: 'Outfit, sans-serif'
            }">{{ form.settingContent.length }}/300</text>
          </view>
        </view>

        <!-- Topic Card -->
        <view v-if="topics.length" class="bg-white rounded-2xl shadow-card p-4">
          <view class="flex items-center gap-1.5 mb-3">
            <view class="w-1 h-4 rounded-full bg-accent"></view>
            <text class="font-bold text-ink text-sm" style="font-family: 'Outfit', sans-serif;">参与话题</text>
            <text class="text-xs text-muted ml-1" style="font-family: 'Outfit', sans-serif;">可选</text>
          </view>
          <scroll-view scroll-x class="whitespace-nowrap">
            <view class="flex gap-2">
              <view v-for="topic in topics" :key="topic.topicId"
                class="px-3 py-1.5 text-xs rounded-xl active:scale-95 transition-transform duration-150" :style="{
                  borderWidth: '1px',
                  borderStyle: 'solid',
                  borderColor: selectedTopicId === topic.topicId ? '#FFC107' : '#E8E3DC',
                  color: selectedTopicId === topic.topicId ? '#E5A800' : '#8C8A84',
                  backgroundColor: selectedTopicId === topic.topicId ? '#FFF8E1' : '#FDFCF8',
                  fontFamily: 'Outfit, sans-serif',
                  fontWeight: selectedTopicId === topic.topicId ? '600' : '400'
                }" @tap="selectedTopicId = selectedTopicId === topic.topicId ? null : topic.topicId">#{{ topic.title }}
              </view>
            </view>
          </scroll-view>
        </view>

        <view style="height: 16px;"></view>
      </view>
    </scroll-view>

    <!-- Toast -->
    <view v-if="showToast" class="fixed inset-0 flex items-center justify-center z-50 pointer-events-none">
      <view class="px-6 py-3 rounded-2xl" style="background: rgba(26,26,26,0.85); backdrop-filter: blur(8px);">
        <text class="text-accent text-sm font-bold" style="font-family: 'Outfit', sans-serif;">发布成功</text>
      </view>
    </view>

    <user-guide ref="guideRef" :guideConfig="GUIDE_STEPS_CONFIG" @action="handleGuideAction" />
    <login-modal ref="loginModalRef" />
  </view>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { onShow } from '@dcloudio/uni-app'
import { useScrollHeight } from '@/utils/useScrollHeight.js'
import { createStory } from '@/apis/modules/story.js'
import { joinTopic, getTopicList } from '@/apis/modules/topic.js'
import upload from '@/utils/upload.js'
import UserGuide from '@/components/user-guide.vue'
import LoginModal from '@/components/login-modal.vue'
import { useUserStore } from '@/stores/user.js'

const { scrollHeight } = useScrollHeight('#scroll-header')
const userStore = useUserStore()
const loginModalRef = ref(null)
const form = ref({ cover: '', title: '', settingContent: '' })
const topics = ref([])
const selectedTopicId = ref(null)
const submitting = ref(false)
const showToast = ref(false)
const guideRef = ref(null)

const STORY_CREATE_GUIDE = 'story_create_guide'
const GUIDE_STEPS_CONFIG = {
  [STORY_CREATE_GUIDE]: [
    {
      targetId: 'guide-cover',
      title: '🖼️ 上传封面图片',
      description: '封面是读者对你故事的第一印象，好看的封面能让点击率翻倍。点击这里选张图片吧！',
      position: 'bottom',
      buttonText: '下一步'
    },
    {
      targetId: 'guide-story-title',
      title: '📝 起个好标题',
      description: '用一句话吸引读者，让他们忍不住想点进来。标题是故事的第一印象。',
      position: 'bottom',
      buttonText: '下一步'
    },
    {
      targetId: 'guide-story-content',
      title: '📖 写下故事设定',
      description: '这里是你的世界观、角色和开端。设定越有趣，吸引来续写的人就越多！',
      position: 'top',
      buttonText: '下一步'
    },
    {
      targetId: 'guide-publish-btn',
      title: '🚀 发布故事',
      description: '准备好了就发布吧！发布后故事会出现在推荐列表，等待更多人来续写你的世界。',
      position: 'bottom',
      // action: 'publish',
      buttonText: '去发布'
    }
  ]
}

onShow(() => {
  setTimeout(() => {
    if (guideRef.value) guideRef.value.startGuide(STORY_CREATE_GUIDE)
  }, 800)
})

function handleGuideAction(actionName) {
  if (actionName === 'publish') doCreate()
}

const canPublish = computed(() =>
  form.value.cover &&
  form.value.title.trim() &&
  form.value.settingContent.length >= 15 &&
  form.value.settingContent.length <= 300 &&
  !submitting.value
)

async function chooseCover() {
  uni.chooseImage({
    count: 1,
    sizeType: ['compressed'],
    sourceType: ['album', 'camera'],
    success: async ({ tempFilePaths }) => {
      try {
        const res = await upload({ url: '/common/upload', filePath: tempFilePaths[0] })
        form.value.cover = res.url || res.fileName
      } catch (e) {
        uni.showToast({ title: '图片上传失败', icon: 'none' })
      }
    },
  })
}

async function doCreate() {
  if (!userStore.isLoggedIn) {
    loginModalRef.value.openModal({
      title: '登录后才能发布',
      description: '发布故事需要登录。登录后即可开始创作，邀请大家续写你的世界。'
    })
    return
  }
  if (!form.value.cover) {
    uni.showToast({ title: '请先添加封面', icon: 'none' })
    return
  }
  if (!canPublish.value || submitting.value) return

  submitting.value = true
  try {
    const res = await createStory({
      title: form.value.title.trim(),
      cover: form.value.cover,
      settingContent: form.value.settingContent.trim(),
    })
    if (res && res.data) {
      const storyId = res.data
      if (selectedTopicId.value) {
        await joinTopic({ storyId, topicId: selectedTopicId.value }).catch(() => { })
      }
      showToast.value = true
      setTimeout(() => {
        showToast.value = false
        uni.redirectTo({ url: `/pages/story/detail?storyId=${storyId}` })
      }, 1500)
    }
  } catch (e) {
    uni.showToast({ title: e?.data?.msg || e?.msg || '发布失败', icon: 'none' })
  } finally {
    submitting.value = false
  }
}

onMounted(async () => {
  try {
    const res = await getTopicList()
    if (res && res.data) topics.value = res.data
  } catch { /* 话题列表加载失败不阻断发布流程 */ }
  const pages = getCurrentPages()
  const topicIdFromQuery = Number(pages[pages.length - 1]?.options?.topicId || 0)
  if (topicIdFromQuery) selectedTopicId.value = topicIdFromQuery
})
</script>
