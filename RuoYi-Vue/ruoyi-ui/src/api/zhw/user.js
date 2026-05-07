import request from '@/utils/request'

export function listUsers(query) {
  return request({ url: '/admin/zhw/user/list', method: 'get', params: query })
}

export function getUser(userId) {
  return request({ url: '/admin/zhw/user/' + userId, method: 'get' })
}

export function updateUserStatus(userId, status) {
  return request({ url: `/admin/zhw/user/${userId}/status`, method: 'put', params: { status } })
}

export function batchUpdateUserStatus(ids, status) {
  return request({ url: '/admin/zhw/user/batch/status', method: 'put', data: { ids, status } })
}

export function resetUserPwd(userId) {
  return request({ url: `/admin/zhw/user/${userId}/reset-pwd`, method: 'put' })
}

export function exportUsers(query) {
  return request({ url: '/admin/zhw/user/export', method: 'get', params: query })
}
