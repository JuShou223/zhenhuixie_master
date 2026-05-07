export function getBranchDetail(branchId: number | string): Promise<any> {
  return uni.$u.http.get(`/zhw/branch/${branchId}`)
}

export function getBranchChildren(branchId: number | string, params: Record<string, unknown>): Promise<any> {
  return uni.$u.http.get(`/zhw/branch/${branchId}/children`, { params })
}

export function createBranch(data: Record<string, unknown>): Promise<any> {
  return uni.$u.http.post('/zhw/branch', data)
}
