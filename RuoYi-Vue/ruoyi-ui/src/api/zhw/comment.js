import request from '@/utils/request'

export function listComments(query) {
  return request({ url: '/admin/zhw/comment/list', method: 'get', params: query })
}

export function delComment(commentId) {
  return request({ url: '/admin/zhw/comment/' + commentId, method: 'delete' })
}

export function batchDelComments(ids) {
  return request({ url: '/admin/zhw/comment/batch/' + ids, method: 'delete' })
}
