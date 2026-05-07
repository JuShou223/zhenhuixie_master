import request from '@/utils/request'

export function collectStory(storyId, collected) {
  return request({ url: `/admin/zhw/collection/${storyId}/collect`, method: 'put', params: { collected } })
}

export function listCollection() {
  return request({ url: '/admin/zhw/collection/list', method: 'get' })
}

export function exportCollection() {
  return request({ url: '/admin/zhw/collection/export', method: 'get', responseType: 'blob' })
}

export function exportHot(minBranches = 3, limit = 50) {
  return request({ url: '/admin/zhw/collection/export/hot', method: 'get', params: { minBranches, limit }, responseType: 'blob' })
}

export function batchUncollect(ids) {
  return request({ url: '/admin/zhw/collection/batch/uncollect', method: 'put', data: { ids } })
}
