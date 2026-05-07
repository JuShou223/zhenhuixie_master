// 故事相关接口

export function getStoryList(params) {
  return uni.$u.http.get('/zhw/story/list', { params })
}

export function getStoryDetail(storyId) {
  return uni.$u.http.get(`/zhw/story/${storyId}`)
}

export function createStory(data) {
  return uni.$u.http.post('/zhw/story', data)
}

export function getStoryBranches(storyId, params) {
  return uni.$u.http.get(`/zhw/story/${storyId}/branches`, { params })
}

export function getStoryRanking(storyId) {
  return uni.$u.http.get(`/zhw/story/${storyId}/ranking`)
}

export function getFeaturedStories(params) {
  return uni.$u.http.get('/zhw/featured/list', { params })
}
