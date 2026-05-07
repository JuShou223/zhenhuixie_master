<template>
  <view
    class="min-h-screen flex flex-col px-8"
    style="
      background: #FDFCF8;
      padding-top: calc(var(--status-bar-height) + 20px);
      padding-bottom: env(safe-area-inset-bottom, 24px);
    "
  >
    <!-- Back -->
    <view class="mb-10">
      <view
        class="w-9 h-9 rounded-full flex items-center justify-center active:scale-90 transition-transform duration-150"
        style="background: #F0EDE6;"
        @tap="uni.navigateBack()"
      >
        <text class="iconfont icon-lucide-chevron-left text-ink" style="font-size: 18px;"></text>
      </view>
    </view>

    <!-- Title -->
    <view class="mb-10">
      <text
        class="block text-4xl text-ink mb-2"
        style="font-family: 'Noto Serif SC', Georgia, 'Songti SC', STSong, serif; font-weight: 700; letter-spacing: -0.02em; line-height: 1.2;"
      >创建账号</text>
      <text class="block text-sm text-muted" style="font-style: italic; font-family: 'Outfit', sans-serif;">加入真会写，开始你的创作</text>
    </view>

    <!-- 表单 -->
    <view class="flex flex-col gap-1">
      <!-- 用户名 -->
      <view class="border-b border-line flex items-center gap-3 py-4">
        <text class="iconfont icon-lucide-user text-base text-muted"></text>
        <input
          v-model="form.username"
          class="flex-1 text-ink text-[15px]"
          placeholder="用户名（4~20位字母或数字）"
          placeholder-style="color: #8C8A84; font-size: 14px; font-family: 'Outfit', 'PingFang SC', sans-serif;"
          :disabled="loading"
          maxlength="20"
          style="font-family: 'Outfit', 'PingFang SC', sans-serif;"
        />
      </view>
      <view v-if="errors.username" class="px-1 py-1.5">
        <text class="text-xs" style="color: #C0503A; font-family: 'Outfit', sans-serif;">{{ errors.username }}</text>
      </view>

      <!-- 密码 -->
      <view class="border-b border-line flex items-center gap-3 py-4">
        <text class="iconfont icon-lucide-lock text-base text-muted"></text>
        <input
          v-model="form.password"
          class="flex-1 text-ink text-[15px]"
          placeholder="密码（6~20位）"
          placeholder-style="color: #8C8A84; font-size: 14px; font-family: 'Outfit', 'PingFang SC', sans-serif;"
          password
          :disabled="loading"
          maxlength="20"
          style="font-family: 'Outfit', 'PingFang SC', sans-serif;"
        />
      </view>
      <view v-if="errors.password" class="px-1 py-1.5">
        <text class="text-xs" style="color: #C0503A; font-family: 'Outfit', sans-serif;">{{ errors.password }}</text>
      </view>

      <!-- 确认密码 -->
      <view class="border-b border-line flex items-center gap-3 py-4">
        <text class="iconfont icon-lucide-lock text-base text-muted"></text>
        <input
          v-model="form.confirmPassword"
          class="flex-1 text-ink text-[15px]"
          placeholder="再次输入密码"
          placeholder-style="color: #8C8A84; font-size: 14px; font-family: 'Outfit', 'PingFang SC', sans-serif;"
          password
          :disabled="loading"
          maxlength="20"
          style="font-family: 'Outfit', 'PingFang SC', sans-serif;"
        />
      </view>
      <view v-if="errors.confirmPassword" class="px-1 py-1.5">
        <text class="text-xs" style="color: #C0503A; font-family: 'Outfit', sans-serif;">{{ errors.confirmPassword }}</text>
      </view>
    </view>

    <!-- 注册按钮 -->
    <view
      class="mt-10 flex items-center justify-center rounded-full active:scale-95 transition-transform duration-150"
      style="height: 52px;"
      :style="{
        backgroundColor: btnActive ? '#FFC107' : '#EDEBE6',
        boxShadow: btnActive ? '0 4px 16px rgba(229, 168, 0, 0.35)' : 'none',
      }"
      @tap="doRegister"
    >
      <text
        class="font-bold"
        style="font-family: 'Outfit', sans-serif; font-size: 15px; letter-spacing: 0.12em;"
        :style="{ color: btnActive ? '#1A1A1A' : '#B0ADA6' }"
      >{{ loading ? '注册中…' : '注 册' }}</text>
    </view>

    <!-- 去登录 -->
    <view class="flex items-center justify-center gap-2 mt-8">
      <text class="text-sm text-muted" style="font-family: 'Outfit', sans-serif;">已有账号？</text>
      <view
        class="px-3 py-1 rounded-full active:opacity-60 transition-opacity"
        style="background: rgba(255, 193, 7, 0.12);"
        @tap="uni.navigateBack()"
      >
        <text class="text-sm font-semibold" style="color: #E5A800; font-family: 'Outfit', sans-serif;">立即登录</text>
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import { register } from '@/apis/modules/common'

const form = ref({ username: '', password: '', confirmPassword: '' })
const loading = ref(false)
const errors = ref({ username: '', password: '', confirmPassword: '' })

const canSubmit = computed(() =>
  form.value.username &&
  form.value.password &&
  form.value.confirmPassword
)

const btnActive = computed(() => canSubmit.value && !loading.value)
const btnBgStyle = computed(() => ({
  backgroundColor: btnActive.value ? '#FFC107' : '#e5e7eb',
}))
const btnTextStyle = computed(() => ({
  color: btnActive.value ? '#1A1A1A' : '#9ca3af',
  letterSpacing: '0.1em',
}))

function validate() {
  errors.value = { username: '', password: '', confirmPassword: '' }
  let ok = true
  if (!form.value.username || form.value.username.length < 4) {
    errors.value.username = '用户名至少 4 位'
    ok = false
  }
  if (!form.value.password || form.value.password.length < 6) {
    errors.value.password = '密码至少 6 位'
    ok = false
  }
  if (form.value.password !== form.value.confirmPassword) {
    errors.value.confirmPassword = '两次密码不一致'
    ok = false
  }
  return ok
}

async function doRegister() {
  if (!canSubmit.value || loading.value) return
  if (!validate()) return
  loading.value = true
  try {
    const res = await register({
      username: form.value.username,
      password: form.value.password,
    })
    if (res && (res.code === 200 || res.code === 0)) {
      uni.showToast({ title: '注册成功，请登录', icon: 'success' })
      setTimeout(() => uni.navigateBack(), 1200)
    } else {
      uni.showToast({ title: res?.msg || '注册失败', icon: 'none' })
    }
  } finally {
    loading.value = false
  }
}
</script>
