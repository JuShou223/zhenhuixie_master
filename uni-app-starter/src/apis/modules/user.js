// 用户相关接口

export function getMyProfile() {
  return uni.$u.http.get('/zhw/user/profile')
}

export function updateMyProfile(data) {
  return uni.$u.http.put('/zhw/user/profile', data)
}

export function getMyStories() {
  return uni.$u.http.get('/zhw/user/stories')
}
