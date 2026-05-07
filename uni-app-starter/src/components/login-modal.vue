<!--
 * @Date: 2026-05-07 17:22:03
 * @LastEditors: 徐一鸣
 * @LastEditTime: 2026-05-07 17:34:35
 * @Description:
-->
<template>
  <up-popup overlayStyle="background-color: rgba(26, 26, 26, 0.55); backdrop-filter: blur(6px);" :show="show" round="0"
    :safeAreaInsetBottom="false" mode="center" @close="handleClose">
    <view class="bg-white rounded-3xl px-6 py-8 box-border flex flex-col items-center animate-fade-in-up"
      style="width: 300px; box-shadow: 0 8px 32px rgba(26, 26, 26, 0.10);">
      <!-- 图标 -->
      <!-- <view class="w-14 h-14 rounded-full flex items-center justify-center mb-5"
        style="background: #FFF8E1;">
        <text class="iconfont icon-lucide-pencil-line text-2xl text-accent-dark"></text>
      </view> -->

      <!-- 标题 -->
      <text class="block text-[17px] font-bold text-ink text-center mb-2"
        style="font-family: 'Noto Serif SC', Georgia, 'Songti SC', serif; letter-spacing: -0.01em;">{{ title }}</text>

      <!-- 描述 -->
      <text class="block text-sm text-muted text-center leading-relaxed mb-7 px-1"
        style="font-family: 'Outfit', 'PingFang SC', sans-serif;">{{ description }}</text>

      <!-- 按钮区 -->
      <view class="flex flex-col gap-2 w-full">
        <button
          class="w-full font-bold rounded-full active:scale-95 transition-transform duration-150 flex items-center justify-center"
          style="height: 44px; background: #FFC107; color: #1A1A1A; border: none; font-family: 'Outfit', sans-serif; font-size: 14px; letter-spacing: 0.04em; box-shadow: 0 4px 16px rgba(229, 168, 0, 0.30);"
          @tap="handleLogin">登录 / 注册</button>

        <view class="flex items-center justify-center py-1 active:opacity-60" @tap="handleClose">
          <text class="text-xs text-muted" style="font-family: 'Outfit', sans-serif;">暂不登录，继续浏览</text>
        </view>
      </view>
    </view>
  </up-popup>
</template>

<script setup>
import { ref } from 'vue'

const emit = defineEmits(['close', 'login'])

const show = ref(false)
const title = ref('需要登录')
const description = ref('登录后才能点赞、续写和评论。加入真会写，和更多人共创故事。')
const onConfirm = ref(() => { })
const onCancel = ref(() => { })

const openModal = (config) => {
  show.value = true
  title.value = config.title || '需要登录'
  description.value = config.description || '登录后才能点赞、续写和评论。加入真会写，和更多人共创故事。'
  onConfirm.value = config.onConfirm || (() => { })
  onCancel.value = config.onCancel || (() => { })
}

const handleLogin = () => {
  if (typeof onConfirm.value === 'function') onConfirm.value()
  uni.navigateTo({ url: '/pages/login/index' })
  handleClose()
}

const handleClose = () => {
  show.value = false
}

defineExpose({ openModal })
</script>

<style scoped>
:deep(.u-popup__content) {
  background-color: transparent !important;
}

.animate-fade-in-up {
  animation: fadeInUp 0.25s cubic-bezier(0.22, 0.61, 0.36, 1) both;
}

@keyframes fadeInUp {
  from {
    opacity: 0;
    transform: translateY(16px) scale(0.97);
  }

  to {
    opacity: 1;
    transform: translateY(0) scale(1);
  }
}
</style>
