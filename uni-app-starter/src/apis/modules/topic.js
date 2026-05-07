// 话题挑战接口

export function getTopicList() {
  return uni.$u.http.get('/zhw/topic/list')
}

export function getTopicDetail(topicId) {
  return uni.$u.http.get(`/zhw/topic/${topicId}`)
}

export function getTopicStories(topicId, params) {
  return uni.$u.http.get(`/zhw/topic/${topicId}/stories`, { params })
}

export function joinTopic(data) {
  return uni.$u.http.post('/zhw/topic/join', data)
}
