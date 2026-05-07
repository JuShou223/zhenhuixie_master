// 点赞 & 标记接口

export function toggleLike(data) {
  return uni.$u.http.post('/zhw/like', data)
}

export function toggleMark(data) {
  return uni.$u.http.post('/zhw/mark', data)
}

export function getMarkList() {
  return uni.$u.http.get('/zhw/mark/list')
}

export function updateMarkRead(data) {
  return uni.$u.http.put('/zhw/mark/read', data)
}
