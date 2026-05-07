import request from '@/utils/request'

export function listTopics() {
  return request({ url: '/admin/zhw/topic/list', method: 'get' })
}

export function getTopic(topicId) {
  return request({ url: '/admin/zhw/topic/' + topicId, method: 'get' })
}

export function addTopic(data) {
  return request({ url: '/admin/zhw/topic', method: 'post', data })
}

export function updateTopic(data) {
  return request({ url: '/admin/zhw/topic', method: 'put', data })
}

export function delTopic(topicId) {
  return request({ url: '/admin/zhw/topic/' + topicId, method: 'delete' })
}

export function batchDelTopics(ids) {
  return request({ url: '/admin/zhw/topic/batch/' + ids, method: 'delete' })
}
