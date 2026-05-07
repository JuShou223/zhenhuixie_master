import request from '@/utils/request'

export function getPlatformStats() {
  return request({ url: '/admin/zhw/dashboard/stats', method: 'get' })
}

export function getStoryTrend(days) {
  return request({ url: '/admin/zhw/dashboard/trend', method: 'get', params: { days } })
}

export function getHotStories() {
  return request({ url: '/admin/zhw/dashboard/hot', method: 'get' })
}
