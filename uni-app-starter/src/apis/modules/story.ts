export function getStoryList(params: Record<string, unknown>): Promise<any> {
  return uni.$u.http.get('/zhw/story/list', { params })
}

export function getStoryDetail(storyId: number | string): Promise<any> {
  return uni.$u.http.get(`/zhw/story/${storyId}`)
}

export function createStory(data: Record<string, unknown>): Promise<any> {
  return uni.$u.http.post('/zhw/story', data)
}

export function getStoryBranches(storyId: number | string, params: Record<string, unknown>): Promise<any> {
  return uni.$u.http.get(`/zhw/story/${storyId}/branches`, { params })
}

export function getStoryRanking(storyId: number | string): Promise<any> {
  return uni.$u.http.get(`/zhw/story/${storyId}/ranking`)
}

export function getFeaturedStories(params: Record<string, unknown>): Promise<any> {
  return uni.$u.http.get('/zhw/featured/list', { params })
}
