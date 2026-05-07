import { ref, computed } from 'vue'
import bus from '@/common/bus'

export const GUIDE_TYPES: Record<string, string> = {
  HOME_PAGE: 'home_page',
  DIAGNOSIS: 'diagnosis',
  TREATMENT: 'treatment',
  SENSOR: 'sensor_connect',
  PLANT_ADD: 'plant_add',
  HISTORY_CENTER: 'history_center',
  ABNORMAL_PLANTS: 'abnormal_plants',
  ACTIVE_TREATMENTS: 'active_treatments',
  PLANT_DETAIL: 'plant_detail',
  SENSOR_CONFIG: 'sensor_config',
  STORY_HOME: 'story_home',
  STORY_DETAIL: 'story_detail',
  BRANCH_DETAIL: 'branch_detail',
}

interface GuideConfig {
  key: string
  title: string
  version?: string
  priority?: number
  maxShowCount: number
  showInterval: number
  dependencies?: string[]
  triggerConditions?: string[]
}

interface GuideStatus extends GuideConfig {
  isCompleted: boolean
  lastShowTime: string | null
  showCount: number
}

export const PAGE_GUIDES: Record<string, GuideConfig> = {
  [GUIDE_TYPES.HOME_PAGE]: {
    key: GUIDE_TYPES.HOME_PAGE,
    title: '首页功能引导',
    version: '1.0.0',
    priority: 1,
    maxShowCount: 1,
    showInterval: 0,
    dependencies: [],
  },
  [GUIDE_TYPES.ABNORMAL_PLANTS]: {
    key: GUIDE_TYPES.ABNORMAL_PLANTS,
    title: '异常植物提醒引导',
    version: '1.0.0',
    priority: 1,
    maxShowCount: 1,
    showInterval: 0,
    triggerConditions: ['abnormalCount > 0'],
    dependencies: [],
  },
  [GUIDE_TYPES.ACTIVE_TREATMENTS]: {
    key: GUIDE_TYPES.ACTIVE_TREATMENTS,
    title: '进行中治疗引导',
    version: '1.0.0',
    priority: 3,
    maxShowCount: 1,
    showInterval: 0,
    triggerConditions: ['activeTreatmentCount > 0'],
    dependencies: [],
  },
  [GUIDE_TYPES.TREATMENT]: {
    key: GUIDE_TYPES.TREATMENT,
    title: '治疗方案引导',
    version: '1.0.0',
    maxShowCount: 1,
    showInterval: 0,
    triggerConditions: [],
    dependencies: [],
  },
  [GUIDE_TYPES.PLANT_DETAIL]: {
    key: GUIDE_TYPES.PLANT_DETAIL,
    title: '植物详情页功能引导',
    version: '1.0.0',
    maxShowCount: 1,
    showInterval: 0,
    priority: 10,
    dependencies: [],
  },
  [GUIDE_TYPES.SENSOR_CONFIG]: {
    key: GUIDE_TYPES.SENSOR_CONFIG,
    title: '智能预警配置',
    version: '1.0.0',
    maxShowCount: 1,
    showInterval: 0,
    priority: 11,
    dependencies: [],
    triggerConditions: ['deviceCount > 0'],
  },
  [GUIDE_TYPES.STORY_HOME]: {
    key: GUIDE_TYPES.STORY_HOME,
    title: '首页功能引导',
    version: '1.0.0',
    priority: 1,
    maxShowCount: 1,
    showInterval: 0,
    dependencies: [],
  },
  [GUIDE_TYPES.STORY_DETAIL]: {
    key: GUIDE_TYPES.STORY_DETAIL,
    title: '故事详情功能引导',
    version: '1.0.0',
    priority: 2,
    maxShowCount: 1,
    showInterval: 0,
    dependencies: [],
  },
  [GUIDE_TYPES.BRANCH_DETAIL]: {
    key: GUIDE_TYPES.BRANCH_DETAIL,
    title: '分支详情功能引导',
    version: '1.0.0',
    priority: 3,
    maxShowCount: 1,
    showInterval: 0,
    dependencies: [],
  },
}

interface GuideReport {
  totalGuides: number
  completedGuides: number
  completionRate: string
  guides: Record<string, GuideStatus>
}

export function useGlobalGuideManager() {
  const allGuides = ref<Record<string, GuideStatus>>({})

  const initializeGuides = (): void => {
    Object.keys(PAGE_GUIDES).forEach((key) => {
      const config = PAGE_GUIDES[key]

      const storageKeyVersion = `guide_${key}_version`
      const savedVersion = uni.getStorageSync(storageKeyVersion)

      if (config.version && savedVersion !== config.version) {
        console.log(
          `[Guide] Version mismatch for ${key}. Resetting status. Old: ${savedVersion}, New: ${config.version}`
        )
        uni.removeStorageSync(`guide_${key}_completed`)
        uni.removeStorageSync(`guide_${key}_show_count`)
        uni.removeStorageSync(`guide_${key}_last_show_date`)
        uni.setStorageSync(storageKeyVersion, config.version)
      }

      allGuides.value[key] = {
        ...config,
        isCompleted: uni.getStorageSync(`guide_${key}_completed`) || false,
        lastShowTime: uni.getStorageSync(`guide_${key}_last_show_date`) || null,
        showCount: uni.getStorageSync(`guide_${key}_show_count`) || 0,
      }
    })
  }

  const getGuideStatus = (guideKey: string): GuideStatus | null => {
    return allGuides.value[guideKey] || null
  }

  const checkDependencies = (guideKey: string): boolean => {
    const guide = PAGE_GUIDES[guideKey]
    if (!guide || !guide.dependencies) return true
    return guide.dependencies.every((depKey: string) => {
      const depGuide = allGuides.value[depKey]
      return depGuide && depGuide.isCompleted
    })
  }

  const checkTriggerConditions = (guideKey: string, context: Record<string, number> = {}): boolean => {
    const guide = PAGE_GUIDES[guideKey]
    if (!guide.triggerConditions) return true

    try {
      return guide.triggerConditions.every((condition: string) => {
        if (condition.includes('>')) {
          const [key, value] = condition.split('>').map((s) => s.trim())
          return Number(context[key]) > Number(value)
        }
        if (condition.includes('===')) {
          const [key, value] = condition.split('===').map((s) => s.trim())
          return Number(context[key]) === Number(value)
        }
        return context[condition] === 1
      })
    } catch (error) {
      console.error('检查引导触发条件失败:', error)
      return false
    }
  }

  const getPendingGuides = (context: Record<string, number> = {}): GuideConfig[] => {
    return Object.values(PAGE_GUIDES)
      .filter((guide) => {
        const status = allGuides.value[guide.key]
        if (!status) return false
        if (status.isCompleted) return false
        if (status.showCount >= guide.maxShowCount) return false

        if (status.lastShowTime) {
          const daysSinceLastShow = Math.floor(
            (Date.now() - new Date(status.lastShowTime).getTime()) /
              (1000 * 60 * 60 * 24)
          )
          if (daysSinceLastShow < guide.showInterval) return false
        }

        if (!checkDependencies(guide.key)) return false
        if (!checkTriggerConditions(guide.key, context)) return false

        return true
      })
      .sort((a, b) => {
        const p1 = typeof a.priority === 'number' ? a.priority : 999
        const p2 = typeof b.priority === 'number' ? b.priority : 999
        return p1 - p2
      })
  }

  const recordGuideShown = (guideKey: string): void => {
    if (allGuides.value[guideKey]) {
      allGuides.value[guideKey].showCount++
      allGuides.value[guideKey].lastShowTime = new Date().toISOString()

      uni.setStorageSync(`guide_${guideKey}_show_count`, allGuides.value[guideKey].showCount)
      uni.setStorageSync(`guide_${guideKey}_last_show_date`, allGuides.value[guideKey].lastShowTime)
    }
  }

  const markGuideCompleted = (guideKey: string): void => {
    if (allGuides.value[guideKey]) {
      allGuides.value[guideKey].isCompleted = true
      uni.setStorageSync(`guide_${guideKey}_completed`, true)
      bus.emit('guide:global:completed', { guideKey })
    }
  }

  const resetAllGuides = (): void => {
    Object.keys(PAGE_GUIDES).forEach((key) => {
      const storageKeys = [
        `guide_${key}_completed`,
        `guide_${key}_show_count`,
        `guide_${key}_last_show_date`,
        `guide_${key}_version`,
        `guide_${key}_step_progress`,
        `guide_${key}_skip_count`,
        `guide_${key}_viewed_steps`,
      ]

      storageKeys.forEach((storageKey) => {
        uni.removeStorageSync(storageKey)
      })
    })

    initializeGuides()
  }

  const getGuideReport = (): GuideReport => {
    const totalGuides = Object.keys(PAGE_GUIDES).length
    const completedGuides = Object.values(allGuides.value).filter(
      (g) => g.isCompleted
    ).length
    const completionRate =
      totalGuides > 0 ? ((completedGuides / totalGuides) * 100).toFixed(1) : '0'

    return {
      totalGuides,
      completedGuides,
      completionRate: `${completionRate}%`,
      guides: allGuides.value,
    }
  }

  const exportGuideData = (): Record<string, unknown> => {
    return {
      timestamp: new Date().toISOString(),
      report: getGuideReport(),
      detail: Object.keys(PAGE_GUIDES).reduce((acc: Record<string, unknown>, key) => {
        acc[key] = {
          config: PAGE_GUIDES[key],
          status: allGuides.value[key],
          storage: {
            completed: uni.getStorageSync(`guide_${key}_completed`),
            showCount: uni.getStorageSync(`guide_${key}_show_count`),
            lastShowDate: uni.getStorageSync(`guide_${key}_last_show_date`),
            stepProgress: uni.getStorageSync(`guide_${key}_step_progress`),
          },
        }
        return acc
      }, {}),
    }
  }

  initializeGuides()

  return {
    allGuides: computed(() => allGuides.value),
    getGuideStatus,
    getPendingGuides,
    recordGuideShown,
    markGuideCompleted,
    resetAllGuides,
    getGuideReport,
    exportGuideData,
    GUIDE_TYPES,
    PAGE_GUIDES,
  }
}
