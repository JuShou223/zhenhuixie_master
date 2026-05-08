<template>
  <view class="min-h-screen bg-white flex flex-col">
    <!-- Header -->
    <view
      id="scroll-header"
      class="bg-white border-b border-gray-100 flex items-center justify-center px-4 sticky top-0 z-10"
      style="padding-top: calc(var(--status-bar-height) + 12px); padding-bottom: 14px;"
    >
      <view class="absolute left-4 p-1 -ml-1" @tap="uni.navigateBack()">
        <text class="iconfont icon-lucide-chevron-left text-2xl text-ink"></text>
      </view>
      <text class="text-base font-medium text-gray-900 truncate px-10">全部评论 ({{ total }})</text>
    </view>

    <scroll-view scroll-y :style="{ height: scrollHeight + 'px', paddingBottom: '80px' }" @scroll="onScroll" @scrolltolower="loadMore">
      <view class="px-4 pt-4">
        <!-- Quoted Text -->
        <view class="bg-[#999999] rounded-md p-3 mb-6">
          <text class="text-white text-[13px] leading-relaxed font-medium block">
            原文：{{ quotedText }}
          </text>
        </view>

        <!-- Comments List -->
        <view class="flex flex-col gap-6">
          <view v-for="comment in list" :key="comment.commentId">
            <!-- Root Comment -->
            <view class="flex gap-3">
              <image
                :src="resolveAvatar(comment.authorAvatar)"
                class="w-10 h-10 rounded-full flex-shrink-0"
                mode="aspectFill"
              />
              <view class="flex-1 min-w-0">
                <text class="block text-[14px] text-gray-600 font-medium mb-1.5">{{ comment.authorNickName }}</text>
                <text class="block text-[15px] text-gray-800 leading-relaxed mb-3" style="color: #444;">{{ comment.content }}</text>
                <view class="flex justify-between items-center text-[12px] text-gray-400 font-medium">
                  <view class="flex gap-4">
                    <text>{{ timeAgo(comment.createTime) }}</text>
                    <text @tap="setReply(comment)">回复</text>
                  </view>
                  <view class="flex items-center gap-1.5" @tap="toggleCommentLike(comment)">
                    <text class="iconfont icon-lucide-heart text-sm"
                      :style="{ color: comment.liked ? '#FFC107' : '#999' }"
                    ></text>
                    <text>{{ comment.likeCount || 0 }}</text>
                  </view>
                </view>

                <!-- Replies -->
                <view v-if="comment.replies && comment.replies.length > 0" class="mt-4">
                  <view v-if="comment.showReplies" class="flex flex-col gap-4">
                    <view v-for="reply in comment.replies" :key="reply.commentId" class="flex gap-3">
                      <image
                        :src="resolveAvatar(reply.authorAvatar)"
                        class="w-8 h-8 rounded-full flex-shrink-0"
                        mode="aspectFill"
                      />
                      <view class="flex-1 min-w-0">
                        <view class="flex items-center flex-wrap gap-1.5 mb-1.5">
                          <text class="text-[14px] text-gray-600 font-medium">{{ reply.authorNickName }}</text>
                          <text v-if="reply.replyToNickName" class="text-[12px] text-gray-400">→ {{ reply.replyToNickName }}</text>
                        </view>
                        <text class="block text-[15px] text-gray-800 leading-relaxed mb-3" style="color: #444;">{{ reply.content }}</text>
                        <view class="flex justify-between items-center text-[12px] text-gray-400 font-medium">
                          <view class="flex gap-4">
                            <text>{{ timeAgo(reply.createTime) }}</text>
                            <text @tap="setReply(comment, reply)">回复</text>
                          </view>
                          <view class="flex items-center gap-1.5">
                            <text class="iconfont icon-lucide-heart text-sm text-gray-400"></text>
                            <text>{{ reply.likeCount || 0 }}</text>
                          </view>
                        </view>
                      </view>
                    </view>
                    <view class="text-[13px] text-gray-500 font-medium flex items-center gap-1 pl-11" @tap="toggleReplies(comment)">
                      <view class="bg-gray-300" style="width: 24px; height: 1px;"></view>
                      <text>收起</text>
                      <text class="iconfont icon-lucide-chevron-up text-xs"></text>
                    </view>
                  </view>
                  <view v-else class="text-[13px] text-gray-500 font-medium flex items-center gap-1 mt-2" @tap="toggleReplies(comment)">
                    <view class="bg-gray-300" style="width: 24px; height: 1px;"></view>
                    <text>展开{{ comment.replies.length }}条回复</text>
                    <text class="iconfont icon-lucide-chevron-right text-xs"></text>
                  </view>
                </view>
              </view>
            </view>
          </view>

          <view v-if="loading" class="text-center py-4">
            <text class="text-sm text-muted">加载中...</text>
          </view>
          <view v-else-if="noMore && list.length > 0" class="text-center py-4">
            <text class="text-sm text-muted">— 没有更多评论了 —</text>
          </view>
          <view v-if="list.length === 0 && !loading" class="text-center py-12">
            <text class="text-sm text-muted" style="font-style: italic;">暂无评论，来说点什么吧</text>
          </view>
        </view>
      </view>
      <view class="h-24"></view>
    </scroll-view>

    <!-- Scroll to Top -->
    <view
      v-if="showBackTop"
      class="fixed z-10 w-10 h-10 bg-black/40 text-white rounded-full flex items-center justify-center"
      style="bottom: 90px; right: 16px;"
      @tap="scrollToTop"
    >
      <text class="iconfont icon-lucide-chevron-up text-white" style="font-size: 20px; font-weight: bold;"></text>
    </view>

    <!-- Input Area -->
    <view
      class="bg-white border-t border-gray-100 flex flex-col gap-1 px-4 z-20"
      style="position: fixed; bottom: 0; left: 0; right: 0; padding-top: 10px; padding-bottom: calc(10px + env(safe-area-inset-bottom, 0px)); box-shadow: 0 -2px 10px rgba(0,0,0,0.02);"
    >
      <view v-if="replyTarget" class="flex items-center justify-between w-full mb-2">
        <text class="text-xs" style="color: #FFC107;">回复 {{ replyTarget.authorNickName }}</text>
        <text class="text-xs text-muted" @tap="replyTarget = null; replyRootComment = null">取消</text>
      </view>
      <view class="flex items-center gap-3 w-full">
        <image
          :src="resolveAvatar(userStore.profile?.avatar)"
          class="w-10 h-10 rounded-full flex-shrink-0"
          mode="aspectFill"
        />
        <view class="flex-1 bg-gray-100 rounded-full flex items-center h-10 px-4">
          <input
            v-model="inputContent"
            :placeholder="replyTarget ? `回复 ${replyTarget.authorNickName}...` : '请输入您的内容'"
            placeholder-style="color: #9ca3af;"
            class="w-full bg-transparent text-[14px] text-gray-900"
            :focus="inputFocus"
            @focus="inputFocus = true"
            @blur="inputFocus = false"
          />
        </view>
        <view
          class="h-10 px-5 rounded-full font-bold text-[14px] flex items-center justify-center flex-shrink-0"
          :style="{ backgroundColor: inputContent.trim() ? '#FFC107' : '#9ca3af', color: inputContent.trim() ? '#1A1A1A' : '#fff' }"
          @tap="submitComment"
        >
          <text>发送</text>
        </view>
      </view>
    </view>
    <login-modal ref="loginModalRef" />
  </view>
</template>

<script setup>
import { resolveAvatar } from '@/utils/avatar.js'
import { ref, onMounted } from 'vue'
import { useScrollHeight } from '@/utils/useScrollHeight.js'
import { useUserStore } from '@/stores/user.js'
import { getCommentList, getCommentReplies, addComment } from '@/apis/modules/comment.js'
import { toggleLike as apiToggleLike } from '@/apis/modules/interaction.js'
import { getBranchDetail } from '@/apis/modules/branch.js'
import { timeAgo } from '@/utils/timeago.js'
import LoginModal from '@/components/login-modal.vue'

const { scrollHeight } = useScrollHeight('#scroll-header')
const userStore = useUserStore()
const loginModalRef = ref(null)
const pages = getCurrentPages()
const query = pages[pages.length - 1]?.options || {}
const storyId = Number(query.storyId)
const branchId = Number(query.branchId || 0)

const list = ref([])
const total = ref(0)
const quotedText = ref('')
const page = ref(1)
const loading = ref(false)
const noMore = ref(false)
const inputContent = ref('')
const inputFocus = ref(false)
const replyTarget = ref(null)
const replyRootComment = ref(null)
const showBackTop = ref(false)

async function fetchList() {
  if (loading.value || noMore.value) return
  loading.value = true
  try {
    const res = await getCommentList({ storyId, branchId, pageNum: page.value, pageSize: 20 })
    if (res && res.rows) {
      for (const c of res.rows) {
        c.showReplies = false
        c.replies = []
        if (c.commentCount > 0) {
          const rr = await getCommentReplies(c.commentId, {}).catch(() => null)
          if (rr && rr.rows) c.replies = rr.rows
        }
      }
      list.value = [...list.value, ...res.rows]
      total.value = res.total
      noMore.value = list.value.length >= res.total
      page.value++
    }
  } finally {
    loading.value = false
  }
}

function loadMore() {
  if (!noMore.value) fetchList()
}

function onScroll(e) {
  showBackTop.value = e.detail.scrollTop > 600
}

function scrollToTop() {
  uni.pageScrollTo({ scrollTop: 0, duration: 300 })
}

function setReply(rootComment, reply = null) {
  if (!userStore.isLoggedIn) { loginModalRef.value.openModal({ title: '评论需要登录', description: '登录后才能发表评论，参与故事讨论。' }); return }
  replyRootComment.value = rootComment
  replyTarget.value = reply || rootComment
  inputFocus.value = true
}

function toggleReplies(comment) {
  comment.showReplies = !comment.showReplies
}

async function toggleCommentLike(comment) {
  if (!userStore.isLoggedIn) { loginModalRef.value.openModal({ title: '点赞需要登录', description: '登录后才能为喜欢的评论点赞。' }); return }
  const res = await apiToggleLike({ targetId: comment.commentId, targetType: 2 })
  if (res) {
    comment.liked = res.data
    comment.likeCount = (comment.likeCount || 0) + (res.data ? 1 : -1)
  }
}

async function submitComment() {
  if (!inputContent.value.trim()) return
  if (!userStore.isLoggedIn) { loginModalRef.value.openModal({ title: '评论需要登录', description: '登录后才能发表评论，参与故事讨论。' }); return }

  const data = {
    storyId,
    branchId,
    content: inputContent.value.trim(),
    parentId: replyRootComment.value?.commentId || 0,
    replyToUserId: replyTarget.value?.userId || 0,
  }
  try {
    const res = await addComment(data)
    if (res && res.data) {
      inputContent.value = ''
      replyTarget.value = null
      replyRootComment.value = null
      total.value++
      list.value = []
      page.value = 1
      noMore.value = false
      fetchList()
    }
  } catch (e) {
    uni.showToast({ title: e?.data?.msg || e?.msg || '评论失败', icon: 'none' })
  }
}

onMounted(async () => {
  if (branchId > 0) {
    const res = await getBranchDetail(branchId).catch(() => null)
    if (res && res.data && res.data.content) {
      const text = res.data.content.trim()
      quotedText.value = text.length > 80 ? text.substring(0, 80) + '...' : text
    }
  }
  fetchList()
})
</script>
