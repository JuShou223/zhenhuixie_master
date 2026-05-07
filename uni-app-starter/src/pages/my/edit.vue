<template>
  <view class="min-h-screen bg-[#fcfcfc] flex flex-col" style="padding-bottom: env(safe-area-inset-bottom, 0px);">
    <!-- Header -->
    <view
      id="scroll-header"
      class="flex items-center justify-between px-4 sticky top-0 z-10 bg-[#fcfcfc]"
      style="padding-top: calc(var(--status-bar-height) + 12px); padding-bottom: 14px;"
    >
      <view class="p-1 -ml-1" @tap="uni.navigateBack()">
        <text class="iconfont icon-lucide-chevron-left text-2xl text-gray-800"></text>
      </view>
      <view
        class="bg-accent text-gray-900 text-[13px] font-bold px-4 py-1.5 rounded-md"
        @tap="doSave"
      >
        <text>{{ saving ? '保存中' : '保存' }}</text>
      </view>
    </view>

    <scroll-view scroll-y :style="{ height: scrollHeight + 'px' }">
      <view class="flex-1 flex flex-col items-center pt-16 px-6">
        <!-- Avatar -->
        <view
          class="relative rounded-full border-3 border-white shadow-sm bg-gray-200 mb-6 overflow-hidden flex items-center justify-center"
          style="width: 112px; height: 112px;"
          @tap="changeAvatar"
        >
          <image
            v-if="form.avatar"
            :src="resolveAvatar(form.avatar)"
            class="w-full h-full"
            mode="aspectFill"
          />
          <text v-else class="iconfont icon-lucide-image text-3xl text-gray-400"></text>
          <view class="absolute inset-0 bg-black/20 flex items-center justify-center opacity-0 active:opacity-100" style="transition: opacity 0.2s;">
            <text class="iconfont icon-lucide-image text-white text-xl"></text>
          </view>
        </view>

        <!-- Username -->
        <view class="flex items-center justify-center relative w-full mb-12">
          <view class="relative inline-flex items-center">
            <input
              v-model="form.nickName"
              class="text-[18px] font-bold text-gray-900 text-center bg-transparent"
              style="border: none; outline: none; padding: 0; min-width: 120px; max-width: 200px;"
              maxlength="20"
              placeholder="昵称"
              placeholder-style="color: #9ca3af;"
            />
            <text class="iconfont icon-lucide-pen text-gray-400 absolute -right-5 top-1/2 -translate-y-1/2" style="font-size: 14px;"></text>
          </view>
        </view>

        <!-- Signature -->
        <view class="w-full">
          <input
            v-model="form.remark"
            placeholder="个性签名"
            placeholder-style="color: #9ca3af;"
            class="w-full text-[15px] text-gray-600 bg-transparent"
            style="border: none; border-bottom: 1px solid #d1d5db; padding: 0; padding-bottom: 8px; outline: none;"
            maxlength="100"
          />
        </view>
      </view>
    </scroll-view>

    <!-- Toast -->
    <view
      v-if="toastMsg"
      class="fixed top-1/2 left-1/2 -translate-x-1/2 -translate-y-1/2 bg-black/75 text-white px-6 py-3 rounded text-[14px] font-medium z-50 pointer-events-none"
    >
      {{ toastMsg }}
    </view>
  </view>
</template>

<script setup lang="ts">
import { resolveAvatar } from '@/utils/avatar'
import { ref, onMounted } from 'vue'
import { useScrollHeight } from '@/utils/useScrollHeight'
import { useUserStore } from '@/stores/user'
import { getMyProfile, updateMyProfile } from '@/apis/modules/user'
import upload from '@/utils/upload'

const { scrollHeight } = useScrollHeight('#scroll-header')
const userStore = useUserStore()
const form = ref({ nickName: '', avatar: '', remark: '' })
const saving = ref(false)
const toastMsg = ref('')

async function changeAvatar() {
  uni.chooseImage({
    count: 1,
    sizeType: ['compressed'],
    sourceType: ['album', 'camera'],
    success: async ({ tempFilePaths }) => {
      try {
        const res = await upload({ url: '/common/upload', filePath: tempFilePaths[0] })
        form.value.avatar = res.url || res.fileName
      } catch (e) {
        uni.showToast({ title: '上传失败', icon: 'none' })
      }
    },
  })
}

async function doSave() {
  if (saving.value) return

  saving.value = true
  try {
    const res = await updateMyProfile({
      nickName: form.value.nickName.trim(),
      avatar: form.value.avatar,
      remark: form.value.remark.trim(),
    })
    if (res) {
      userStore.setProfile({ ...userStore.profile, ...form.value })
      toastMsg.value = '保存成功'
      setTimeout(() => {
        toastMsg.value = ''
        uni.navigateBack()
      }, 1500)
    }
  } catch (e) {
    uni.showToast({ title: e?.data?.msg || e?.msg || '保存失败', icon: 'none' })
  } finally {
    saving.value = false
  }
}

onMounted(async () => {
  const res = await getMyProfile()
  if (res && res.data && res.data.user) {
    const u = res.data.user
    form.value.nickName = u.nickName || ''
    form.value.avatar = u.avatar || ''
    form.value.remark = u.remark || ''
  }
})
</script>
