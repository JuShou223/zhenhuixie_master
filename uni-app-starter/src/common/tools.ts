import type { App } from 'vue'

const install = (app: App): void => {
  app.config.globalProperties.$t = {
    parseTime(time: Date | string | number | null | undefined, pattern = '{y}-{m}-{d} {h}:{i}:{s}'): string {
      if (!time) return ''
      let date: Date
      if (typeof time === 'object') {
        date = time
      } else {
        if (typeof time === 'string' && /^[0-9]+$/.test(time)) time = parseInt(time)
        if (typeof time === 'string') {
          time = time.replace(/-/g, '/').replace('T', ' ').replace(/\.\d{3}/g, '')
        }
        if (typeof time === 'number' && time.toString().length === 10) time *= 1000
        date = new Date(time)
      }
      const map: Record<string, number | string> = {
        y: date.getFullYear(),
        m: date.getMonth() + 1,
        d: date.getDate(),
        h: date.getHours(),
        i: date.getMinutes(),
        s: date.getSeconds(),
        a: date.getDay(),
      }
      return pattern.replace(/{(y|m|d|h|i|s|a)}/g, (_, key: string) => {
        const val = map[key]
        if (key === 'a') return ['日', '一', '二', '三', '四', '五', '六'][val as number]
        return (val as number) < 10 ? '0' + val : String(val)
      })
    },

    praseStrEmpty(str: unknown): string {
      if (!str || str === 'undefined' || str === 'null') return ''
      return String(str)
    },

    wait(ms = 500): Promise<void> {
      return new Promise((resolve) => setTimeout(resolve, ms))
    },
  }
}

export default { install }
