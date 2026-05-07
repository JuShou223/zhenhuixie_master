export function captchaImage(): Promise<any> {
  return uni.$u.http.get('/captchaImage')
}

export function login(params: Record<string, unknown>): Promise<any> {
  return uni.$u.http.post('/zhw/login', params)
}

export function register(params: Record<string, unknown>): Promise<any> {
  return uni.$u.http.post('/zhw/register', params)
}

export function logout(): Promise<any> {
  return uni.$u.http.post('/logout')
}

export function getProfile(): Promise<any> {
  return uni.$u.http.get('/zhw/user/profile')
}

export function uploadFile(filePath: string): Promise<any> {
  return uni.$u.http.upload('/common/upload', {
    filePath,
    name: 'file',
  })
}
