import type { UserProfile, Story, Branch, Comment, Topic, Notification } from './models'

export interface ApiResponse<T = unknown> {
  code: number
  msg?: string
  data?: T
  total?: number
  rows?: T[]
  token?: string
}

export interface LoginResponse {
  data?: string
  token?: string
  msg?: string
}

export type StoryListResponse = ApiResponse<Story[]>
export type StoryDetailResponse = ApiResponse<Story>
export type BranchDetailResponse = ApiResponse<Branch>
export type CommentListResponse = ApiResponse<Comment[]>
export type NotificationListResponse = ApiResponse<Notification[]>
export type ProfileResponse = ApiResponse<UserProfile>
