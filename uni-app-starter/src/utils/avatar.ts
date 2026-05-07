import projectConfig from '@/env.config'

export function resolveAvatar(avatar: string | null | undefined): string {
  if (!avatar) return '/static/avatar-default.png'
  if (avatar.startsWith('http://') || avatar.startsWith('https://'))
    return avatar
  console.log(projectConfig.baseUrl + avatar)
  return projectConfig.baseUrl + avatar
}
