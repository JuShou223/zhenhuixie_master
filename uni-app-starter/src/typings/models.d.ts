// ====== 用户 ======
export interface UserProfile {
  userId?: number
  nickName?: string
  avatar?: string
  remark?: string
  score?: number
  storyCount?: number
  branchCount?: number
  [key: string]: unknown
}

// ====== 故事 ======
export interface Story {
  storyId: number
  title: string
  cover: string
  content?: string
  heatScore?: number
  participantCount?: number
  authorName?: string
  authorAvatar?: string
  createTime?: string
  [key: string]: unknown
}

// ====== 分支 ======
export interface Branch {
  branchId: number
  storyId?: number
  content?: string
  authorNickName?: string
  authorAvatar?: string
  createTime?: string
  likeCount?: number
  [key: string]: unknown
}

// ====== 评论 ======
export interface Comment {
  commentId: number
  content: string
  authorNickName: string
  authorAvatar: string
  createTime: string
  likeCount?: number
  liked?: boolean
  replyTo?: string
  [key: string]: unknown
}

// ====== 话题 ======
export interface Topic {
  topicId: number
  name?: string
  description?: string
  [key: string]: unknown
}

// ====== 通知 ======
export interface Notification {
  id: number
  type: string
  content: string
  isRead: boolean
  createTime: string
  [key: string]: unknown
}

// ====== 引导 ======
export interface GuideConfig {
  key: string
  title: string
  version?: string
  priority?: number
  maxShowCount: number
  showInterval: number
  dependencies?: string[]
  triggerConditions?: string[]
}

export interface GuideStep {
  targetId: string
  title: string
  description: string
  position?: 'top' | 'bottom' | 'center'
  buttonText?: string
  skipText?: string
  action?: string
  disableScroll?: boolean
  disableTargetClick?: boolean
}

// ====== Tabbar ======
export interface TabbarItem {
  label?: string
  icon?: string
  value?: string
  path?: string
  mode?: 'midButton'
}
