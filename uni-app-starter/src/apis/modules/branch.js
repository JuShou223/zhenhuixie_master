// 分支相关接口

export function getBranchDetail(branchId) {
  return uni.$u.http.get(`/zhw/branch/${branchId}`)
}

export function getBranchChildren(branchId, params) {
  return uni.$u.http.get(`/zhw/branch/${branchId}/children`, { params })
}

export function createBranch(data) {
  return uni.$u.http.post('/zhw/branch', data)
}
