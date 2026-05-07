export function getMyProfile(): Promise<any> {
  return uni.$u.http.get('/zhw/user/profile')
}

export function updateMyProfile(data: Record<string, unknown>): Promise<any> {
  return uni.$u.http.put('/zhw/user/profile', data)
}

export function getMyStories(): Promise<any> {
  return uni.$u.http.get('/zhw/user/stories')
}
