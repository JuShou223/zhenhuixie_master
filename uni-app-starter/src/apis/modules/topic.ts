export function getTopicList(): Promise<any> {
  return uni.$u.http.get('/zhw/topic/list')
}

export function getTopicDetail(topicId: number | string): Promise<any> {
  return uni.$u.http.get(`/zhw/topic/${topicId}`)
}

export function getTopicStories(topicId: number | string, params: Record<string, unknown>): Promise<any> {
  return uni.$u.http.get(`/zhw/topic/${topicId}/stories`, { params })
}

export function joinTopic(data: Record<string, unknown>): Promise<any> {
  return uni.$u.http.post('/zhw/topic/join', data)
}
