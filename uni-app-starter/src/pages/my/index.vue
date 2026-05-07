<template>
  <view class="min-h-screen flex flex-col" style="background-color: #FDFCF8; padding-bottom: 24px;">

    <!-- ===== 已登录 ===== -->
    <template v-if="userStore.isLoggedIn">
      <!-- Amber Hero Header -->
      <view class="bg-accent relative" style="padding-bottom: 40px; border-radius: 0 0 28px 28px;">
        <view class="px-6 flex items-center justify-between"
          style="padding-top: calc(var(--status-bar-height) + 20px); padding-bottom: 24px;">
          <view class="flex items-center gap-4" @tap="goEdit">
            <image :src="resolveAvatar(profile.avatar)" class="w-16 h-16 rounded-full border-2 border-white"
              mode="aspectFill" />
            <view class="flex flex-col">
              <text class="text-xl font-bold text-ink">{{ profile.nickName || '未设置昵称' }}</text>
              <text class="text-sm text-ink/60 mt-0.5">{{ profile.remark || '暂无个性签名' }}</text>
            </view>
          </view>
          <view
            class="bg-white/20 text-ink text-sm font-semibold px-3 py-1.5 rounded-full active:scale-95 transition-transform duration-200"
            @tap="goEdit">
            编辑资料
          </view>
        </view>
      </view>

      <!-- Stats Card -->
      <view class="px-4 -mt-6 relative z-10">
        <view class="bg-white rounded-2xl py-4 flex justify-around text-center shadow-card">
          <view class="flex-1 active:scale-95 transition-transform duration-200">
            <text class="block text-lg font-semibold text-ink tabular-nums"
              style="font-family: 'Outfit', -apple-system, sans-serif;">{{ profile.score || 0 }}</text>
            <text class="text-sm text-muted mt-0.5 block">获赞</text>
          </view>
          <view class="flex-1 active:scale-95 transition-transform duration-200" @tap="goMyStories">
            <text class="block text-lg font-semibold text-ink tabular-nums"
              style="font-family: 'Outfit', -apple-system, sans-serif;">{{ profile.storyCount || 0 }}</text>
            <text class="text-sm text-muted mt-0.5 block">发布接龙</text>
          </view>
          <view class="flex-1 active:scale-95 transition-transform duration-200" @tap="goMarks">
            <text class="block text-lg font-semibold text-ink tabular-nums"
              style="font-family: 'Outfit', -apple-system, sans-serif;">{{ profile.branchCount || 0 }}</text>
            <text class="text-sm text-muted mt-0.5 block">收到后续</text>
          </view>
        </view>
      </view>

      <!-- Menu Card -->
      <view class="px-4 mt-4">
        <view class="bg-white rounded-2xl shadow-card">
          <view
            class="flex items-center justify-between py-4 px-4 border-b border-line active:bg-bg transition-colors duration-150"
            @tap="goTopicList">
            <text class="text-[15px] text-ink font-medium">话题挑战</text>
            <view class="flex items-center gap-2">
              <view v-if="badge.topic > 0" class="bg-red-500 text-white text-[11px] font-bold px-2 py-0.5 rounded-full">
                new</view>
              <text class="iconfont icon-lucide-chevron-right text-base text-muted"></text>
            </view>
          </view>
          <view
            class="flex items-center justify-between py-4 px-4 border-b border-line active:bg-bg transition-colors duration-150"
            @tap="goMessages">
            <text class="text-[15px] text-ink font-medium">互动消息</text>
            <view class="flex items-center gap-2">
              <view v-if="badge.interact > 0"
                class="bg-red-500 text-white text-xs font-bold w-5 h-5 flex items-center justify-center rounded-full"
                style="font-family: 'Outfit', sans-serif;">
                {{ badge.interact > 99 ? '99+' : badge.interact }}
              </view>
              <text class="iconfont icon-lucide-chevron-right text-base text-muted"></text>
            </view>
          </view>
          <view
            class="flex items-center justify-between py-4 px-4 border-b border-line active:bg-bg transition-colors duration-150"
            @tap="goMarks">
            <text class="text-[15px] text-ink font-medium">标记更新</text>
            <view class="flex items-center gap-2">
              <view v-if="badge.markUpdate > 0"
                class="bg-red-500 text-white text-xs font-bold w-5 h-5 flex items-center justify-center rounded-full"
                style="font-family: 'Outfit', sans-serif;">
                {{ badge.markUpdate }}
              </view>
              <text class="iconfont icon-lucide-chevron-right text-base text-muted"></text>
            </view>
          </view>
        </view>

        <!-- Logout -->
        <view class="bg-white rounded-2xl shadow-card px-4 mt-3">
          <view class="flex items-center justify-between py-4 active:bg-bg transition-colors duration-150"
            @tap="doLogout">
            <text class="text-[15px] text-muted font-medium">退出登录</text>
            <text class="iconfont icon-lucide-chevron-right text-base text-muted"></text>
          </view>
        </view>
      </view>
    </template>

    <!-- ===== 未登录 ===== -->
    <template v-else>
      <!-- Header 占位 -->
      <view class="bg-accent relative" style="padding-bottom: 40px; border-radius: 0 0 28px 28px;">
        <view class="px-6 flex items-center"
          style="padding-top: calc(var(--status-bar-height) + 20px); padding-bottom: 24px;">
          <view class="w-16 h-16 rounded-full flex items-center justify-center mr-4 flex-shrink-0"
            style="background: rgba(255,255,255,0.25);">
            <text class="iconfont icon-lucide-user text-white" style="font-size: 28px;"></text>
          </view>
          <view class="flex flex-col">
            <text class="text-xl font-bold text-ink">登录 / 注册</text>
            <text class="text-sm mt-0.5" style="color: rgba(26,26,26,0.55);">登录后解锁全部功能</text>
          </view>
        </view>
      </view>

      <!-- Stats 占位 -->
      <view class="px-4 -mt-6 relative z-10">
        <view class="bg-white rounded-2xl py-4 flex justify-around text-center shadow-card">
          <view class="flex-1">
            <text class="block text-lg font-semibold text-line" style="font-family: 'Outfit', sans-serif;">—</text>
            <text class="text-sm text-muted mt-0.5 block">获赞</text>
          </view>
          <view class="flex-1">
            <text class="block text-lg font-semibold text-line" style="font-family: 'Outfit', sans-serif;">—</text>
            <text class="text-sm text-muted mt-0.5 block">发布接龙</text>
          </view>
          <view class="flex-1">
            <text class="block text-lg font-semibold text-line" style="font-family: 'Outfit', sans-serif;">—</text>
            <text class="text-sm text-muted mt-0.5 block">收到后续</text>
          </view>
        </view>
      </view>

      <!-- 登录引导卡片 -->
      <view class="px-4 mt-5">
        <view class="bg-white rounded-2xl shadow-card px-5 py-5"
          style="background: linear-gradient(135deg, #FFFDF5 0%, #FFF8E1 100%);">
          <view class="flex items-center gap-3 mb-4">
            <view class="w-10 h-10 rounded-full flex items-center justify-center flex-shrink-0"
              style="background: #FFF0C2;">
              <text class="iconfont icon-lucide-pencil-line text-accent-dark" style="font-size: 18px;"></text>
            </view>
            <view>
              <text class="block text-[15px] font-bold text-ink">加入真会写</text>
              <text class="block text-xs text-muted mt-0.5">和更多人共创无限分支故事</text>
            </view>
          </view>
          <view class="flex flex-col gap-2">
            <view
              class="flex items-center justify-center rounded-full active:scale-95 transition-transform duration-150"
              style="height: 46px; background: #FFC107; box-shadow: 0 4px 16px rgba(229,168,0,0.30);" @tap="goLogin">
              <text class="font-bold text-ink"
                style="font-family: 'Outfit', sans-serif; font-size: 15px; letter-spacing: 0.06em;">登录 / 注册</text>
            </view>
          </view>
        </view>
      </view>

      <!-- 公开菜单 -->
      <view class="px-4 mt-4">
        <view class="bg-white rounded-2xl shadow-card">
          <view
            class="flex items-center justify-between py-4 px-4 border-b border-line active:bg-bg transition-colors duration-150"
            @tap="goTopicList">
            <text class="text-[15px] text-ink font-medium">话题挑战</text>
            <text class="iconfont icon-lucide-chevron-right text-base text-muted"></text>
          </view>
          <view
            class="flex items-center justify-between py-4 px-4 border-b border-line active:bg-bg transition-colors duration-150"
            @tap="requireLoginMenu">
            <text class="text-[15px] text-muted font-medium">互动消息</text>
            <view class="flex items-center gap-2">
              <text class="iconfont icon-lucide-lock text-xs text-line"></text>
              <text class="iconfont icon-lucide-chevron-right text-base text-line"></text>
            </view>
          </view>
          <view
            class="flex items-center justify-between py-4 px-4 border-b border-line active:bg-bg transition-colors duration-150"
            @tap="requireLoginMenu">
            <text class="text-[15px] text-muted font-medium">标记更新</text>
            <view class="flex items-center gap-2">
              <text class="iconfont icon-lucide-lock text-xs text-line"></text>
              <text class="iconfont icon-lucide-chevron-right text-base text-line"></text>
            </view>
          </view>
        </view>
      </view>
    </template>

    <tab-bar defaultTab="my" />
    <modal ref="modalRef" />
    <login-modal ref="loginModalRef" />
  </view>
</template>

<script setup>
import { resolveAvatar } from '@/utils/avatar.js'
import { ref, onMounted } from 'vue'
import { useUserStore } from '@/stores/user.js'
import TabBar from '@/components/tab-bar.vue'
import Modal from '@/components/modal.vue'
import LoginModal from '@/components/login-modal.vue'
import { getMyProfile } from '@/apis/modules/user.js'
import { getMarkList } from '@/apis/modules/interaction.js'
import { logout as apiLogout } from '@/apis/modules/common.js'
import { onShow } from '@dcloudio/uni-app'
import { useTabbar } from '@/hooks/useTabbar'
const { handleSetTabbar } = useTabbar()

const userStore = useUserStore()
const modalRef = ref(null)
const loginModalRef = ref(null)

const profile = ref({})
const badge = ref({ interact: 0, markUpdate: 0, topic: 0 })

async function loadProfile() {
  if (!userStore.isLoggedIn) return
  const res = await getMyProfile()
  if (res && res.data) {
    profile.value = res.data.user || {}
    badge.value = { ...badge.value, ...(res.data.badge || {}) }
    userStore.setProfile(res.data.user)
  }
}

async function loadMarkBadge() {
  if (!userStore.isLoggedIn) return
  const res = await getMarkList().catch(() => null)
  if (res && res.data) {
    badge.value.markUpdate = res.data.filter(m => m.hasUpdate).length
  }
}

function goLogin() {
  uni.navigateTo({ url: '/pages/login/index' })
}

function requireLoginMenu() {
  loginModalRef.value.openModal({
    title: '需要登录',
    description: '登录后才能查看互动消息和标记更新。'
  })
}

function goEdit() {
  uni.navigateTo({ url: '/pages/my/edit' })
}

function goTopicList() {
  uni.navigateTo({ url: '/pages/topic/list' })
}

function goMessages() {
  uni.navigateTo({ url: '/pages/my/messages' })
}

function goMyStories() {
  uni.navigateTo({ url: '/pages/my/stories' })
}

function goMarks() {
  uni.navigateTo({ url: '/pages/my/marks' })
}

function doLogout() {
  modalRef.value.openModal({
    type: 'delete',
    title: '退出登录',
    content: '确定要退出当前账号吗？',
    confirmText: '确认退出',
    onConfirm: async () => {
      await apiLogout().catch(() => { })
      userStore.logout()
      uni.reLaunch({ url: '/pages/login/index' })
    }
  })
}

onShow(() => {
  handleSetTabbar('my')
  loadProfile()
  loadMarkBadge()
})

onMounted(() => {
  loadProfile()
  loadMarkBadge()
})
</script>
