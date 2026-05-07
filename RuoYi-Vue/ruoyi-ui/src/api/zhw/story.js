import request from '@/utils/request'

export function listStories(query) {
  return request({ url: '/admin/zhw/story/list', method: 'get', params: query })
}

export function updateStoryStatus(storyId, status) {
  return request({ url: `/admin/zhw/story/${storyId}/status`, method: 'put', params: { status } })
}

export function delStory(storyId) {
  return request({ url: '/admin/zhw/story/' + storyId, method: 'delete' })
}

export function batchDelStories(ids) {
  return request({ url: '/admin/zhw/story/batch/' + ids, method: 'delete' })
}

export function batchUpdateStoryStatus(ids, status) {
  return request({ url: '/admin/zhw/story/batch/status', method: 'put', data: { ids, status } })
}
