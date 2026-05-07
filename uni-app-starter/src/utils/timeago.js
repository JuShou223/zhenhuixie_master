/**
 * 抖音风格时间格式化
 */
export function timeAgo(dateStr) {
  if (!dateStr) return ''
  const date = new Date(dateStr.replace(/-/g, '/'))
  const now = new Date()
  const diff = Math.floor((now - date) / 1000)

  if (diff < 60) return '刚刚'
  if (diff < 3600) return Math.floor(diff / 60) + '分钟前'
  if (diff < 86400) return Math.floor(diff / 3600) + '小时前'
  if (diff < 604800) return Math.floor(diff / 86400) + '天前'

  const y = date.getFullYear()
  const m = String(date.getMonth() + 1).padStart(2, '0')
  const d = String(date.getDate()).padStart(2, '0')
  if (y === now.getFullYear()) return `${m}-${d}`
  return `${y}-${m}-${d}`
}
