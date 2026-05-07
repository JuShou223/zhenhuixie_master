export function toggleLike(data: Record<string, unknown>): Promise<any> {
  return uni.$u.http.post('/zhw/like', data)
}

export function toggleMark(data: Record<string, unknown>): Promise<any> {
  return uni.$u.http.post('/zhw/mark', data)
}

export function getMarkList(): Promise<any> {
  return uni.$u.http.get('/zhw/mark/list')
}

export function updateMarkRead(data: Record<string, unknown>): Promise<any> {
  return uni.$u.http.put('/zhw/mark/read', data)
}
