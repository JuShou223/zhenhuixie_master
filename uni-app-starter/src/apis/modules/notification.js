// 消息通知接口

export function getNotificationList(params) {
  return uni.$u.http.get('/zhw/notification/list', { params })
}

export function getUnreadCount() {
  return uni.$u.http.get('/zhw/notification/unread/count')
}

export function getUnreadBadge() {
  return uni.$u.http.get('/zhw/notification/unread/badge')
}

export function markAsRead(data) {
  return uni.$u.http.put('/zhw/notification/read', data)
}
