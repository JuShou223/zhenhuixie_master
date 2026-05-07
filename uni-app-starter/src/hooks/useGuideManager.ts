import bus from '@/common/bus'
import { ref, onMounted, onUnmounted, getCurrentInstance } from 'vue'

interface UseGuideManagerOptions {
  version?: string
  maxShowCount?: number
  showInterval?: number
  autoResetOnVersionChange?: boolean
  storagePrefix?: string
}

interface GuideStats {
  guideKey: string
  version: string
  isCompleted: boolean
  showCount: number
  lastShowDate: string | null
  currentStep: number
  totalSteps: number
  skipCount: number
}

export function useGuideManager(guideKey: string, options: UseGuideManagerOptions = {}) {
  const {
    version = '1.0.0',
    maxShowCount = 3,
    showInterval = 7,
    autoResetOnVersionChange = true,
    storagePrefix = 'guide_',
  } = options

  const showGuide = ref(false)
  const currentStep = ref(0)
  const totalSteps = ref(0)
  const isCompleted = ref(false)

  const storageKeys = {
    completed: `${storagePrefix}${guideKey}_completed`,
    showCount: `${storagePrefix}${guideKey}_show_count`,
    lastShowDate: `${storagePrefix}${guideKey}_last_show_date`,
    version: `${storagePrefix}${guideKey}_version`,
    stepProgress: `${storagePrefix}${guideKey}_step_progress`,
  }

  const initialize = (): void => {
    if (autoResetOnVersionChange) {
      const savedVersion = uni.getStorageSync(storageKeys.version)
      if (savedVersion !== version) {
        resetGuide()
        uni.setStorageSync(storageKeys.version, version)
      }
    }

    isCompleted.value = uni.getStorageSync(storageKeys.completed) || false
    currentStep.value = uni.getStorageSync(storageKeys.stepProgress) || 0
  }

  const resetGuide = (): void => {
    uni.removeStorageSync(storageKeys.completed)
    uni.removeStorageSync(storageKeys.showCount)
    uni.removeStorageSync(storageKeys.lastShowDate)
    uni.removeStorageSync(storageKeys.stepProgress)
    isCompleted.value = false
    currentStep.value = 0
    showGuide.value = false
  }

  const shouldShowGuide = (): boolean => {
    if (isCompleted.value) return false

    const showCount: number = uni.getStorageSync(storageKeys.showCount) || 0
    if (showCount >= maxShowCount) return false

    const lastShowDate: string | null = uni.getStorageSync(storageKeys.lastShowDate)
    if (lastShowDate) {
      const daysSinceLastShow = Math.floor(
        (Date.now() - new Date(lastShowDate).getTime()) / (1000 * 60 * 60 * 24)
      )
      if (daysSinceLastShow < showInterval) return false
    }

    return true
  }

  const recordShow = (): void => {
    const newShowCount: number = (uni.getStorageSync(storageKeys.showCount) || 0) + 1
    uni.setStorageSync(storageKeys.showCount, newShowCount)
    uni.setStorageSync(storageKeys.lastShowDate, new Date().toISOString())
  }

  const startGuide = (steps = 1): boolean => {
    if (!shouldShowGuide()) return false
    totalSteps.value = steps
    showGuide.value = true
    return true
  }

  const nextStep = (): void => {
    if (currentStep.value < totalSteps.value - 1) {
      currentStep.value++
      uni.setStorageSync(storageKeys.stepProgress, currentStep.value)
    } else {
      completeGuide()
    }
  }

  const prevStep = (): void => {
    if (currentStep.value > 0) {
      currentStep.value--
      uni.setStorageSync(storageKeys.stepProgress, currentStep.value)
    }
  }

  const gotoStep = (step: number): void => {
    if (step >= 0 && step < totalSteps.value) {
      currentStep.value = step
      uni.setStorageSync(storageKeys.stepProgress, currentStep.value)
    }
  }

  const completeGuide = (): void => {
    showGuide.value = false
    isCompleted.value = true
    uni.setStorageSync(storageKeys.completed, true)
    recordShow()
    bus.emit(`guide:${guideKey}:completed`, {
      guideKey,
      steps: totalSteps.value,
    })
  }

  const skipGuide = (): void => {
    showGuide.value = false
    const skipCount: number =
      uni.getStorageSync(`${storagePrefix}${guideKey}_skip_count`) || 0
    uni.setStorageSync(`${storagePrefix}${guideKey}_skip_count`, skipCount + 1)
    recordShow()
    bus.emit(`guide:${guideKey}:skipped`, {
      guideKey,
      step: currentStep.value,
    })
  }

  const forceShowGuide = (steps = 1): void => {
    totalSteps.value = steps
    showGuide.value = true
    currentStep.value = 0
    uni.setStorageSync(storageKeys.stepProgress, 0)
  }

  const getGuideStats = (): GuideStats => {
    return {
      guideKey,
      version,
      isCompleted: isCompleted.value,
      showCount: uni.getStorageSync(storageKeys.showCount) || 0,
      lastShowDate: uni.getStorageSync(storageKeys.lastShowDate),
      currentStep: currentStep.value,
      totalSteps: totalSteps.value,
      skipCount: uni.getStorageSync(`${storagePrefix}${guideKey}_skip_count`) || 0,
    }
  }

  const isStepViewed = (step: number): boolean => {
    const viewedSteps: number[] =
      uni.getStorageSync(`${storagePrefix}${guideKey}_viewed_steps`) || []
    return viewedSteps.includes(step)
  }

  const markStepViewed = (step: number): void => {
    const viewedSteps: number[] =
      uni.getStorageSync(`${storagePrefix}${guideKey}_viewed_steps`) || []
    if (!viewedSteps.includes(step)) {
      viewedSteps.push(step)
      uni.setStorageSync(`${storagePrefix}${guideKey}_viewed_steps`, viewedSteps)
    }
  }

  const bindEvents = (): void => {
    bus.on(`guide:${guideKey}:reset`, resetGuide)
    bus.on(`guide:${guideKey}:forceShow`, ({ steps }: { steps: number }) => forceShowGuide(steps))
  }

  const unbindEvents = (): void => {
    bus.off(`guide:${guideKey}:reset`)
    bus.off(`guide:${guideKey}:forceShow`)
  }

  const instance = getCurrentInstance()

  if (instance) {
    onMounted(() => {
      initialize()
      bindEvents()
    })
    onUnmounted(() => {
      unbindEvents()
    })
  } else {
    initialize()
    bindEvents()
  }

  return {
    showGuide,
    currentStep,
    totalSteps,
    isCompleted,
    startGuide,
    nextStep,
    prevStep,
    gotoStep,
    completeGuide,
    skipGuide,
    resetGuide,
    forceShowGuide,
    shouldShowGuide,
    getGuideStats,
    isStepViewed,
    markStepViewed,
    destroy: unbindEvents,
  }
}
