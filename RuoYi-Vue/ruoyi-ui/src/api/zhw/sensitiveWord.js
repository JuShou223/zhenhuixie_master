import request from '@/utils/request'

export function listSensitiveWords(query) {
  return request({ url: '/admin/zhw/sensitive-word/list', method: 'get', params: query })
}

export function getSensitiveWord(id) {
  return request({ url: '/admin/zhw/sensitive-word/' + id, method: 'get' })
}

export function addSensitiveWord(data) {
  return request({ url: '/admin/zhw/sensitive-word', method: 'post', data })
}

export function updateSensitiveWord(data) {
  return request({ url: '/admin/zhw/sensitive-word', method: 'put', data })
}

export function delSensitiveWord(id) {
  return request({ url: '/admin/zhw/sensitive-word/' + id, method: 'delete' })
}

export function reloadSensitiveWords() {
  return request({ url: '/admin/zhw/sensitive-word/reload', method: 'post' })
}

export function batchDelSensitiveWords(ids) {
  return request({ url: '/admin/zhw/sensitive-word/batch/' + ids, method: 'delete' })
}
