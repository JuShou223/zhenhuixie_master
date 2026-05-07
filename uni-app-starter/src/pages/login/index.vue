<template>
  <view
    class="min-h-screen flex flex-col px-8"
    style="
      background: #FDFCF8;
      background-image: radial-gradient(ellipse 120% 45% at 50% 0%, rgba(255,193,7,0.08) 0%, transparent 65%);
      padding-top: calc(var(--status-bar-height) + 16px);
      padding-bottom: env(safe-area-inset-bottom, 24px);
    "
  >
    <!-- 返回按钮 -->
    <view v-if="canGoBack"
      class="w-9 h-9 rounded-full flex items-center justify-center mb-10 active:scale-90 transition-transform duration-150"
      style="background: #F0EDE6;"
      @tap="goBack">
      <text class="iconfont icon-lucide-chevron-left text-base text-ink"></text>
    </view>
    <view v-else style="height: 52px;"></view>

    <!-- Brand -->
    <view class="mb-14">
      <view class="flex items-end gap-2 mb-3">
        <text
          class="text-5xl text-ink"
          style="font-family: 'Noto Serif SC', Georgia, 'Songti SC', STSong, serif; font-weight: 700; letter-spacing: -0.02em; line-height: 1;"
        >真会写</text>
        <view style="width: 8px; height: 8px; border-radius: 50%; background: #FFC107; margin-bottom: 6px; flex-shrink: 0;"></view>
      </view>
      <text
        class="block text-sm text-muted"
        style="font-style: italic; font-family: 'Outfit', sans-serif;"
      >多人共创故事社区</text>
    </view>

    <!-- 表单 -->
    <view class="flex flex-col gap-1">
      <view class="border-b border-line flex items-center gap-3 py-4">
        <text class="iconfont icon-lucide-user text-base text-muted"></text>
        <input
          v-model="form.username"
          class="flex-1 text-ink text-[15px]"
          placeholder="用户名"
          placeholder-style="color: #8C8A84; font-family: 'Outfit', 'PingFang SC', sans-serif;"
          :disabled="loading"
          style="font-family: 'Outfit', 'PingFang SC', sans-serif;"
        />
      </view>

      <view class="border-b border-line flex items-center gap-3 py-4">
        <text class="iconfont icon-lucide-lock text-base text-muted"></text>
        <input
          v-model="form.password"
          class="flex-1 text-ink text-[15px]"
          placeholder="密码"
          placeholder-style="color: #8C8A84; font-family: 'Outfit', 'PingFang SC', sans-serif;"
          password
          :disabled="loading"
          style="font-family: 'Outfit', 'PingFang SC', sans-serif;"
        />
      </view>
    </view>

    <!-- 登录按钮 -->
    <view
      class="mt-10 flex items-center justify-center rounded-full active:scale-95 transition-transform duration-150"
      style="height: 52px;"
      :style="{
        backgroundColor: btnActive ? '#FFC107' : '#EDEBE6',
        boxShadow: btnActive ? '0 4px 16px rgba(229, 168, 0, 0.35)' : 'none',
      }"
      @tap="doLogin"
    >
      <text
        class="font-bold"
        style="font-family: 'Outfit', sans-serif; font-size: 15px; letter-spacing: 0.12em;"
        :style="{ color: btnActive ? '#1A1A1A' : '#B0ADA6' }"
      >{{ loading ? '登录中…' : '登 录' }}</text>
    </view>

    <!-- 去注册 -->
    <view class="flex items-center justify-center gap-2 mt-8">
      <text class="text-sm text-muted" style="font-family: 'Outfit', sans-serif;">没有账号？</text>
      <view
        class="px-3 py-1 rounded-full active:opacity-60 transition-opacity"
        style="background: rgba(255, 193, 7, 0.12);"
        @tap="uni.navigateTo({ url: '/pages/register/index' })"
      >
        <text class="text-sm font-semibold" style="color: #E5A800; font-family: 'Outfit', sans-serif;">立即注册</text>
      </view>
    </view>

    <!-- 暂不登录 -->
    <view class="flex items-center justify-center mt-5 active:opacity-50 transition-opacity" @tap="skipLogin">
      <text class="text-sm text-muted" style="font-family: 'Outfit', sans-serif;">暂不登录，继续浏览</text>
    </view>
  </view>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useUserStore } from '@/stores/user.js'
import { login } from '@/apis/modules/common.js'

const userStore = useUserStore()

const canGoBack = computed(() => getCurrentPages().length > 1)

function goBack() {
  uni.navigateBack()
}

function skipLogin() {
  const pages = getCurrentPages()
  if (pages.length > 1) {
    uni.navigateBack()
  } else {
    uni.reLaunch({ url: '/pages/index/index' })
  }
}

const form = ref({ username: '', password: '' })
const loading = ref(false)

const canSubmit = computed(() => form.value.username && form.value.password)

const btnActive = computed(() => canSubmit.value && !loading.value)
const btnBgStyle = computed(() => ({
  backgroundColor: btnActive.value ? '#FFC107' : '#e5e7eb',
}))
const btnTextStyle = computed(() => ({
  color: btnActive.value ? '#1A1A1A' : '#9ca3af',
  letterSpacing: '0.1em',
}))

async function doLogin() {
  if (!canSubmit.value || loading.value) return
  loading.value = true
  try {
    const res = await login({
      username: form.value.username,
      password: form.value.password,
    })
    const token = res?.data || res?.token
    if (token) {
      userStore.setToken(token)
      const pages = getCurrentPages()
      if (pages.length > 1) {
        uni.navigateBack()
      } else {
        uni.reLaunch({ url: '/pages/index/index' })
      }
    } else {
      uni.showToast({ title: res?.msg || '用户名或密码错误', icon: 'none' })
    }
  } finally {
    loading.value = false
  }
}
</script>
