// hooks/useGuideManager.js
import bus from "@/common/bus.js";
import { ref, computed, onMounted, onUnmounted, getCurrentInstance } from "vue";

/**
 * 用户引导管理器Hook
 * @param {string} guideKey - 引导的唯一标识符
 * @param {Object} options - 配置选项
 */
export function useGuideManager(guideKey, options = {}) {
  const {
    version = "1.0.0", // 引导版本，用于版本升级后重置
    maxShowCount = 3, // 最大显示次数
    showInterval = 7, // 显示间隔（天）
    autoResetOnVersionChange = true, // 版本变化时自动重置
    storagePrefix = "guide_", // 存储前缀
  } = options;

  // 状态
  const showGuide = ref(false);
  const currentStep = ref(0);
  const totalSteps = ref(0);
  const isCompleted = ref(false);

  // 存储键名
  const storageKeys = {
    completed: `${storagePrefix}${guideKey}_completed`,
    showCount: `${storagePrefix}${guideKey}_show_count`,
    lastShowDate: `${storagePrefix}${guideKey}_last_show_date`,
    version: `${storagePrefix}${guideKey}_version`,
    stepProgress: `${storagePrefix}${guideKey}_step_progress`,
  };

  // 初始化
  const initialize = () => {
    // 检查版本是否变化
    if (autoResetOnVersionChange) {
      const savedVersion = uni.getStorageSync(storageKeys.version);
      if (savedVersion !== version) {
        resetGuide();
        uni.setStorageSync(storageKeys.version, version);
      }
    }

    // 加载状态
    isCompleted.value = uni.getStorageSync(storageKeys.completed) || false;
    currentStep.value = uni.getStorageSync(storageKeys.stepProgress) || 0;
  };

  // 重置引导状态
  const resetGuide = () => {
    uni.removeStorageSync(storageKeys.completed);
    uni.removeStorageSync(storageKeys.showCount);
    uni.removeStorageSync(storageKeys.lastShowDate);
    uni.removeStorageSync(storageKeys.stepProgress);
    isCompleted.value = false;
    currentStep.value = 0;
    showGuide.value = false;
  };

  // 检查是否应该显示引导
  const shouldShowGuide = () => {
    if (isCompleted.value) return false;

    const showCount = uni.getStorageSync(storageKeys.showCount) || 0;
    if (showCount >= maxShowCount) return false;

    const lastShowDate = uni.getStorageSync(storageKeys.lastShowDate);
    if (lastShowDate) {
      const daysSinceLastShow = Math.floor(
        (Date.now() - new Date(lastShowDate).getTime()) / (1000 * 60 * 60 * 24)
      );
      if (daysSinceLastShow < showInterval) return false;
    }

    return true;
  };

  // 内部辅助：记录有效显示（增加计数并更新时间）
  const recordShow = () => {
    // console.log(128912891729831);
    const newShowCount = (uni.getStorageSync(storageKeys.showCount) || 0) + 1;
    uni.setStorageSync(storageKeys.showCount, newShowCount);
    uni.setStorageSync(storageKeys.lastShowDate, new Date().toISOString());
  };

  // 开始引导
  const startGuide = (steps = 1) => {
    if (!shouldShowGuide()) return false;

    totalSteps.value = steps;
    showGuide.value = true;

    // 修改：此处不再立即记录显示次数，改为在 complete/skip 时记录
    // 这样只有用户产生了有效交互才会计数

    return true;
  };

  // 下一步
  const nextStep = () => {
    if (currentStep.value < totalSteps.value - 1) {
      currentStep.value++;
      uni.setStorageSync(storageKeys.stepProgress, currentStep.value);
    } else {
      completeGuide();
    }
  };

  // 上一步
  const prevStep = () => {
    if (currentStep.value > 0) {
      currentStep.value--;
      uni.setStorageSync(storageKeys.stepProgress, currentStep.value);
    }
  };

  // 跳转到指定步骤
  const gotoStep = (step) => {
    if (step >= 0 && step < totalSteps.value) {
      currentStep.value = step;
      uni.setStorageSync(storageKeys.stepProgress, currentStep.value);
    }
  };

  // 完成引导
  const completeGuide = () => {
    showGuide.value = false;
    isCompleted.value = true;
    uni.setStorageSync(storageKeys.completed, true);

    // 记录一次有效展示
    recordShow();

    // 触发完成事件
    bus.emit(`guide:${guideKey}:completed`, {
      guideKey,
      steps: totalSteps.value,
    });
  };

  // 跳过引导
  const skipGuide = () => {
    showGuide.value = false;
    // 可以选择记录跳过次数
    const skipCount =
      uni.getStorageSync(`${storagePrefix}${guideKey}_skip_count`) || 0;
    uni.setStorageSync(`${storagePrefix}${guideKey}_skip_count`, skipCount + 1);

    // 记录一次有效展示
    recordShow();

    bus.emit(`guide:${guideKey}:skipped`, {
      guideKey,
      step: currentStep.value,
    });
  };

  // 手动重新显示（忽略限制）
  const forceShowGuide = (steps = 1) => {
    totalSteps.value = steps;
    showGuide.value = true;
    currentStep.value = 0;
    uni.setStorageSync(storageKeys.stepProgress, 0);
  };

  // 获取引导统计信息
  const getGuideStats = () => {
    return {
      guideKey,
      version,
      isCompleted: isCompleted.value,
      showCount: uni.getStorageSync(storageKeys.showCount) || 0,
      lastShowDate: uni.getStorageSync(storageKeys.lastShowDate),
      currentStep: currentStep.value,
      totalSteps: totalSteps.value,
      skipCount:
        uni.getStorageSync(`${storagePrefix}${guideKey}_skip_count`) || 0,
    };
  };

  // 检查特定步骤是否已查看
  const isStepViewed = (step) => {
    const viewedSteps =
      uni.getStorageSync(`${storagePrefix}${guideKey}_viewed_steps`) || [];
    return viewedSteps.includes(step);
  };

  // 标记步骤为已查看
  const markStepViewed = (step) => {
    const viewedSteps =
      uni.getStorageSync(`${storagePrefix}${guideKey}_viewed_steps`) || [];
    if (!viewedSteps.includes(step)) {
      viewedSteps.push(step);
      uni.setStorageSync(
        `${storagePrefix}${guideKey}_viewed_steps`,
        viewedSteps
      );
    }
  };

  // ------------------------------------------
  // 生命周期管理修正：支持异步/Lazy调用
  // ------------------------------------------

  const bindEvents = () => {
    bus.on(`guide:${guideKey}:reset`, resetGuide);
    bus.on(`guide:${guideKey}:forceShow`, ({ steps }) => forceShowGuide(steps));
  };

  const unbindEvents = () => {
    bus.off(`guide:${guideKey}:reset`);
    bus.off(`guide:${guideKey}:forceShow`);
  };

  // 关键：检测当前是否在组件 setup 上下文中
  const instance = getCurrentInstance();

  if (instance) {
    // 情况A: 在 setup() 中直接调用 -> 使用 Vue 生命周期
    onMounted(() => {
      initialize();
      bindEvents();
    });

    onUnmounted(() => {
      unbindEvents();
    });
  } else {
    // 情况B: 在 async/await 或 callback 中调用 (Lazy模式) -> 立即执行，需手动销毁
    // 修复 "[Vue warn]: onMounted is called when there is no active component instance"
    // 因为是异步调用，组件肯定已经 Mount 了，所以直接运行初始化逻辑是安全的。
    initialize();
    bindEvents();
  }

  return {
    // 状态
    showGuide,
    currentStep,
    totalSteps,
    isCompleted,

    // 操作
    startGuide,
    nextStep,
    prevStep,
    gotoStep,
    completeGuide,
    skipGuide,
    resetGuide,
    forceShowGuide,

    // 检查
    shouldShowGuide,
    getGuideStats,
    isStepViewed,
    markStepViewed,

    // 销毁 (建议在父组件手动销毁时调用)
    destroy: unbindEvents,
  };
}
