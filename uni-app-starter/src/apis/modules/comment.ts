export function getCommentList(params: Record<string, unknown>): Promise<any> {
  return uni.$u.http.get('/zhw/comment/list', { params })
}

export function getCommentReplies(commentId: number | string, params: Record<string, unknown>): Promise<any> {
  return uni.$u.http.get(`/zhw/comment/${commentId}/replies`, { params })
}

export function addComment(data: Record<string, unknown>): Promise<any> {
  return uni.$u.http.post('/zhw/comment', data)
}

export function deleteComment(commentId: number | string): Promise<any> {
  return uni.$u.http.delete(`/zhw/comment/${commentId}`)
}
