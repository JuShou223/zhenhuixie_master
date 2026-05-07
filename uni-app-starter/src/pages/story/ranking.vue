<template>
  <view class="min-h-screen bg-bg flex flex-col">
    <!-- Podium Area -->
    <view class="bg-accent flex flex-col items-center relative z-10"
      style="padding-top: calc(var(--status-bar-height) + 12px); padding-bottom: 40px;">

      <!-- Back button -->
      <view class="absolute left-4" :style="{ top: `calc(var(--status-bar-height) + 8px)` }" @tap="uni.navigateBack()">
        <view class="w-8 h-8 rounded-full flex items-center justify-center active:scale-90 transition-transform duration-200"
          style="background: rgba(255,255,255,0.2); backdrop-filter: blur(8px);">
          <text class="iconfont icon-lucide-chevron-left text-white" style="font-size: 18px; font-weight: bold;"></text>
        </view>
      </view>

      <text class="text-white text-xl font-bold tracking-widest mb-8"
        style="font-family: 'Outfit', sans-serif; letter-spacing: 0.06em;">人气榜</text>

      <!-- Podium: Top 3 -->
      <view v-if="top3.length > 0" class="flex items-end justify-center gap-5 w-full px-4 mt-2">
        <!-- 2nd Place -->
        <view v-if="top3[1]" class="flex flex-col items-center relative" style="transform: translateY(12px);">
          <view class="relative">
            <image
              :src="resolveAvatar(top3[1].avatar)"
              class="rounded-full border-2 border-white/80"
              style="width: 56px; height: 56px;"
              mode="aspectFill"
            />
            <view class="absolute -top-1.5 -right-1 w-5 h-5 rounded-full flex items-center justify-center"
              style="background: #C0C0C0; border: 2px solid #FFC107;">
              <text class="text-white text-[10px] font-black"
                style="font-family: 'Outfit', sans-serif;">2</text>
            </view>
          </view>
          <text class="text-white/90 text-xs font-semibold mt-2.5 text-center truncate"
            style="width: 60px; font-family: 'Outfit', 'PingFang SC', sans-serif;">{{ top3[1].nickName }}</text>
          <text class="text-ink/60 text-xs font-bold mt-0.5" style="font-family: 'Outfit', sans-serif; font-variant-numeric: tabular-nums;">{{ top3[1].score }}</text>
        </view>

        <!-- 1st Place -->
        <view v-if="top3[0]" class="flex flex-col items-center relative" style="transform: translateY(-12px);">
          <view class="relative">
            <!-- Crown glow -->
            <view class="absolute -top-4 left-1/2 -translate-x-1/2 w-10 h-10 rounded-full"
              style="background: radial-gradient(circle, rgba(255,255,255,0.5) 0%, transparent 70%);"></view>
            <image
              :src="resolveAvatar(top3[0].avatar)"
              class="rounded-full border-3 border-white relative z-10"
              style="width: 76px; height: 76px; box-shadow: 0 4px 20px rgba(0,0,0,0.15);"
              mode="aspectFill"
            />
            <view class="absolute -top-2 -right-1.5 w-6 h-6 rounded-full flex items-center justify-center z-20"
              style="background: #FFD700; border: 2px solid #FFC107; box-shadow: 0 2px 8px rgba(255,193,7,0.4);">
              <text class="text-white text-[11px] font-black"
                style="font-family: 'Outfit', sans-serif;">1</text>
            </view>
          </view>
          <text class="text-white text-sm font-bold mt-3 text-center truncate"
            style="width: 76px; font-family: 'Outfit', 'PingFang SC', sans-serif;">{{ top3[0].nickName }}</text>
          <text class="text-ink/60 text-sm font-black mt-0.5" style="font-family: 'Outfit', sans-serif; font-variant-numeric: tabular-nums;">{{ top3[0].score }}</text>
        </view>

        <!-- 3rd Place -->
        <view v-if="top3[2]" class="flex flex-col items-center relative" style="transform: translateY(12px);">
          <view class="relative">
            <image
              :src="resolveAvatar(top3[2].avatar)"
              class="rounded-full border-2 border-white/80"
              style="width: 56px; height: 56px;"
              mode="aspectFill"
            />
            <view class="absolute -top-1.5 -right-1 w-5 h-5 rounded-full flex items-center justify-center"
              style="background: #CD7F32; border: 2px solid #FFC107;">
              <text class="text-white text-[10px] font-black"
                style="font-family: 'Outfit', sans-serif;">3</text>
            </view>
          </view>
          <text class="text-white/90 text-xs font-semibold mt-2.5 text-center truncate"
            style="width: 60px; font-family: 'Outfit', 'PingFang SC', sans-serif;">{{ top3[2].nickName }}</text>
          <text class="text-ink/60 text-xs font-bold mt-0.5" style="font-family: 'Outfit', sans-serif; font-variant-numeric: tabular-nums;">{{ top3[2].score }}</text>
        </view>
      </view>
    </view>

    <!-- Score Rules -->
    <view id="scroll-header" class="mx-4 -mt-3 relative z-20">
      <view class="bg-white rounded-2xl px-4 py-3 shadow-card">
        <view class="flex flex-wrap gap-x-5 gap-y-1.5">
          <view class="flex items-center gap-1">
            <text class="text-[11px] text-muted">被赞</text>
            <text class="text-[11px] font-bold text-ink" style="font-family: 'Outfit', sans-serif;">×1</text>
          </view>
          <view class="flex items-center gap-1">
            <text class="text-[11px] text-muted">被评论</text>
            <text class="text-[11px] font-bold text-ink" style="font-family: 'Outfit', sans-serif;">×3</text>
          </view>
          <view class="flex items-center gap-1">
            <text class="text-[11px] text-muted">被标记</text>
            <text class="text-[11px] font-bold text-ink" style="font-family: 'Outfit', sans-serif;">×20</text>
          </view>
          <view class="flex items-center gap-1">
            <text class="text-[11px] text-muted">被续写</text>
            <text class="text-[11px] font-bold text-ink" style="font-family: 'Outfit', sans-serif;">×30</text>
          </view>
        </view>
      </view>
    </view>

    <!-- Rank List -->
    <scroll-view scroll-y class="mt-3" :style="{ height: (scrollHeight - 12) + 'px', paddingBottom: '90px' }">
      <view v-if="rest.length > 0" class="px-4">
        <view
          v-for="(user, idx) in rest"
          :key="user.userId"
          class="flex items-center px-3 py-3 rounded-xl mb-1.5 active:bg-yellow-50/50 transition-colors duration-150"
          :class="idx % 2 === 0 ? 'bg-white/60' : 'bg-white'"
        >
          <text class="text-sm font-semibold text-muted"
            style="width: 28px; text-align: center; font-family: 'Outfit', sans-serif; font-variant-numeric: tabular-nums;">{{ user.rank }}</text>
          <image
            :src="resolveAvatar(user.avatar)"
            class="w-9 h-9 rounded-full mx-3 flex-shrink-0"
            mode="aspectFill"
          />
          <text class="flex-1 text-sm text-ink font-medium truncate"
            style="font-family: 'Outfit', 'PingFang SC', sans-serif;">{{ user.nickName }}</text>
          <text class="text-sm text-muted font-semibold"
            style="font-family: 'Outfit', sans-serif; font-variant-numeric: tabular-nums;">{{ user.score }}</text>
        </view>
      </view>

      <view v-if="rest.length === 0 && top3.length === 0" class="flex flex-col items-center justify-center py-20 px-8">
        <text class="iconfont icon-lucide-trophy text-5xl text-gray-300 mb-4"></text>
        <text class="text-base font-semibold text-ink mb-1" style="font-family: 'Outfit', sans-serif;">还没有人上榜</text>
        <text class="text-sm text-muted text-center leading-relaxed mb-6">写下你的第一个分支，成为故事的第一位贡献者</text>
        <view class="bg-ink rounded-2xl px-8 py-2.5 active:scale-95 transition-transform duration-200"
          @tap="goCreate">
          <text class="text-accent text-sm font-semibold" style="font-family: 'Outfit', sans-serif;">写新分支</text>
        </view>
      </view>
    </scroll-view>

    <!-- Sticky My Score -->
    <view v-if="myScore" class="fixed bottom-0 left-0 right-0 z-30 px-4"
      style="padding-top: 12px; padding-bottom: calc(12px + env(safe-area-inset-bottom, 0px));">
      <view class="bg-white rounded-2xl shadow-fab px-4 py-3 flex items-center border border-line">
        <text class="text-sm font-bold text-muted"
          style="width: 28px; text-align: center; font-family: 'Outfit', sans-serif; font-variant-numeric: tabular-nums;">{{ myScore.rank || '—' }}</text>
        <image
          :src="resolveAvatar(myScore.avatar)"
          class="w-9 h-9 rounded-full mx-3 flex-shrink-0"
          mode="aspectFill"
        />
        <view class="flex flex-col flex-1 truncate justify-center">
          <text class="text-sm text-ink font-semibold truncate"
            style="font-family: 'Outfit', 'PingFang SC', sans-serif;">{{ myScore.nickName }}</text>
          <text class="text-[11px] text-muted mt-0.5">再积累{{ gapToTop }}分可上榜</text>
        </view>
        <text class="text-base text-accent-dark font-bold"
          style="font-family: 'Outfit', sans-serif; font-variant-numeric: tabular-nums;">{{ myScore.score }}</text>
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { resolveAvatar } from '@/utils/avatar'
import { ref, computed, onMounted } from 'vue'
import { onLoad } from '@dcloudio/uni-app'
import { useScrollHeight } from '@/utils/useScrollHeight'
import { useUserStore } from '@/stores/user'
import { getStoryRanking } from '@/apis/modules/story'

const { scrollHeight } = useScrollHeight('#scroll-header')
const userStore = useUserStore()

const list = ref([])
const myScoreData = ref(null)

const top3 = computed(() => list.value.slice(0, 3))
const rest = computed(() => list.value.slice(3))
const gapToTop = computed(() => {
  if (!myScoreData.value || list.value.length === 0) return '—'
  const last = list.value[list.value.length - 1]
  return last ? Math.max(1, (last.score || 0) - (myScoreData.value.score || 0) + 1) : '—'
})
const myScore = computed(() => {
  if (myScoreData.value) return myScoreData.value
  const uid = userStore.profile?.userId
  if (!uid) return null
  return list.value.find(u => u.userId === uid) || null
})

const storyId = ref(0)

function goCreate() {
  uni.navigateTo({ url: `/pages/story/branch-create?storyId=${storyId.value}&parentId=0&parentType=0` })
}

onLoad(async (options) => {
  storyId.value = Number(options?.storyId || 0)
  const res = await getStoryRanking(storyId.value)
  if (res && res.data) {
    list.value = res.data.ranking || []
    myScoreData.value = res.data.myScore || null
  }
})
</script>
