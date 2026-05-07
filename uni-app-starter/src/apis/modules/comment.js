// 评论相关接口

export function getCommentList(params) {
  return uni.$u.http.get('/zhw/comment/list', { params })
}

export function getCommentReplies(commentId, params) {
  return uni.$u.http.get(`/zhw/comment/${commentId}/replies`, { params })
}

export function addComment(data) {
  return uni.$u.http.post('/zhw/comment', data)
}

export function deleteComment(commentId) {
  return uni.$u.http.delete(`/zhw/comment/${commentId}`)
}
