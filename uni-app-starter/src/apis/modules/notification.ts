export function getNotificationList(params: Record<string, unknown>): Promise<any> {
  return uni.$u.http.get('/zhw/notification/list', { params })
}

export function getUnreadCount(): Promise<any> {
  return uni.$u.http.get('/zhw/notification/unread/count')
}

export function getUnreadBadge(): Promise<any> {
  return uni.$u.http.get('/zhw/notification/unread/badge')
}

export function markAsRead(data: Record<string, unknown>): Promise<any> {
  return uni.$u.http.put('/zhw/notification/read', data)
}
