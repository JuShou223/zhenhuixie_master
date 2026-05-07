<template>
  <view v-if="showGuide" class="fixed inset-0 z-[999] overflow-hidden pointer-events-auto">

    <!-- 1. 背景遮罩 / 聚光灯效果 -->
    <view v-if="!targetRect" class="absolute inset-0 transition-opacity duration-500" style="background: rgba(26,26,26,0.72);"></view>

    <view v-else class="absolute rounded-xl transition-all duration-300 ease-out animate-pulse-border"
      :style="spotlightStyle"></view>

    <!-- 高亮区域隐形点击层 -->
    <view v-if="targetRect && !isScrolling && !shouldDelayShow" class="absolute z-[1000] cursor-pointer" :style="{
      top: targetRect.top + 'px',
      left: targetRect.left + 'px',
      width: targetRect.width + 'px',
      height: targetRect.height + 'px'
    }" @click="handleTargetClick"></view>

    <!-- 2. 提示卡片 (Tooltip) -->
    <!-- 🌟 增加了 z-[1010] 确保当它侵占目标区域时，能盖住目标的点击层 -->
    <view v-if="currentStepData && !shouldDelayShow && !isScrolling"
      class="absolute transition-all duration-300 ease-out flex flex-col box-border z-[1010]"
      style="width: 280px; background: #FDFCF8; border: 1px solid #E8E3DC; border-radius: 18px; padding: 20px; box-shadow: 0 8px 32px rgba(26,26,26,0.14), 0 2px 8px rgba(26,26,26,0.06);"
      :style="tooltipStyle">

      <!-- 顶部：进度点 + 关闭 -->
      <view class="flex justify-between items-center w-full shrink-0" style="margin-bottom: 14px;">
        <!-- 胶囊进度点 -->
        <view class="flex items-center" style="gap: 5px;">
          <view
            v-for="(_, i) in steps"
            :key="i"
            class="transition-all duration-250"
            style="height: 5px; border-radius: 3px;"
            :style="{
              width: i === currentStepIndex ? '18px' : '5px',
              backgroundColor: i === currentStepIndex ? '#FFC107' : '#E8E3DC'
            }"
          ></view>
        </view>
        <view @click="handleSkip" class="cursor-pointer" style="padding: 4px; color: #C4BFB9; line-height: 1;">
          <text style="font-size: 18px; line-height: 1;">×</text>
        </view>
      </view>

      <!-- 内容区 -->
      <view style="margin-bottom: 2px;">
        <text class="block font-bold text-ink" style="font-family: 'Outfit', 'PingFang SC', sans-serif; font-size: 15px; letter-spacing: -0.01em; line-height: 1.4; margin-bottom: 7px;">{{ currentStepData.title }}</text>
        <text class="block whitespace-pre-wrap" style="font-family: 'Outfit', 'PingFang SC', sans-serif; font-size: 12px; color: #8C8A84; line-height: 1.65;">{{ currentStepData.description }}</text>
      </view>

      <!-- 底部按钮区 -->
      <view class="flex justify-between items-center w-full shrink-0" style="padding-top: 14px; margin-top: 4px; border-top: 1px solid #E8E3DC;">
        <view @click="handleSkip" class="cursor-pointer active:opacity-60 transition-opacity" style="font-size: 12px; color: #C4BFB9; padding: 8px 4px; font-family: 'Outfit', sans-serif;">
          {{ currentStepData.skipText || '跳过' }}
        </view>
        <button @click="handleNext"
          class="flex items-center gap-1 active:scale-95 transition-transform border-none m-0 leading-normal"
          style="background-color: #FFC107; color: #1A1A1A; padding: 8px 18px; border-radius: 10px; font-size: 12px; font-weight: 700; font-family: 'Outfit', sans-serif; box-shadow: 0 4px 12px rgba(229,168,0,0.30); letter-spacing: 0.01em;">
          <text>{{ currentStepData.buttonText || (isLastStep ? '开始体验' : '下一步') }}</text>
        </button>
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref, computed, watch, nextTick, onUnmounted } from 'vue';
import { useGlobalGuideManager } from '@/hooks/useGlobalUserGuide';
import { useGuideManager } from '@/hooks/useGuideManager';
import domQueryService from '@/common/domQueryService';

const props = defineProps({
  guideConfig: { type: Object, default: () => ({}) },
  context: { type: Object, default: () => ({}) }
});

const emit = defineEmits(['open', 'close', 'next', 'complete', 'skip', 'action']);

// ==================== 逻辑层 ====================
const globalMgr = useGlobalGuideManager();
let activeCtrl = null;
let guideSegments = [];

const showGuide = ref(false);
const currentStepIndex = ref(0);
const steps = ref([]);
const currentGuideKey = ref(null);
const guideQueue = ref([]);

// 🔵 Fix6: 防重复点击标志
const isHandlingNext = ref(false);

const check = async () => {
  if (showGuide.value) return;
  await nextTick();
  resetLocalState();

  const pendingGuides = globalMgr.getPendingGuides(props.context);
  if (pendingGuides.length > 0) {
    let combinedSteps = [];
    let segments = [];
    let startIndex = 0;

    for (const guide of pendingGuides) {
      const guideSteps = props.guideConfig[guide.key];
      if (guideSteps && guideSteps.length > 0) {
        const ctrl = useGuideManager(guide.key, {
          maxShowCount: guide.maxShowCount,
          version: guide.version,
          showInterval: guide.showInterval
        });

        if (ctrl.shouldShowGuide()) {
          segments.push({
            key: guide.key,
            ctrl: ctrl,
            startIndex: startIndex,
            endIndex: startIndex + guideSteps.length - 1,
            stepsCount: guideSteps.length,
            originalSteps: guideSteps
          });
          combinedSteps.push(...guideSteps);
          startIndex += guideSteps.length;
        } else {
          if (ctrl.destroy) ctrl.destroy();
        }
      }
    }

    if (combinedSteps.length > 0) {
      steps.value = combinedSteps;
      guideSegments = segments;
      activateSegment(0);
      showGuide.value = true;
      emit('open');
    } else {
      showGuide.value = false;
      emit('close');
    }
  } else {
    showGuide.value = false;
    emit('close');
  }
};

const startGuide = async (guideKey) => {
  console.log('Attempting to start guide:', guideKey);
  if (showGuide.value) {
    if (!guideQueue.value.includes(guideKey)) {
      guideQueue.value.push(guideKey);
    }
    return;
  }

  await nextTick();

  // 🔴 Fix2: 每次 startGuide 前先 reset，防止旧 ctrl 泄漏
  // 原来在 showGuide 为 false 时才 reset，但队列里取出执行时已经是 false，
  // 上一轮遗留的 guideSegments 里的 ctrl 没有被清理
  resetLocalState();

  const guideSteps = props.guideConfig[guideKey];
  if (!guideSteps || guideSteps.length === 0) {
    processNext();
    return;
  }

  const ctrl = useGuideManager(guideKey, {
    maxShowCount: 3,
    version: "1.0.0",
    showInterval: 0
  });

  if (ctrl.shouldShowGuide()) {
    guideSegments = [{
      key: guideKey,
      ctrl: ctrl,
      startIndex: 0,
      endIndex: guideSteps.length - 1,
      stepsCount: guideSteps.length,
      originalSteps: guideSteps
    }];
    steps.value = [...guideSteps];
    activateSegment(0);
    showGuide.value = true;
    emit('open');
  } else {
    if (ctrl.destroy) ctrl.destroy();
    processNext();
  }
};

const processNext = () => {
  resetLocalState();
  showGuide.value = false;

  if (guideQueue.value.length > 0) {
    const nextKey = guideQueue.value.shift();
    startGuide(nextKey);
  } else {
    check();
  }
};

const activateSegment = (segmentIndex) => {
  const segment = guideSegments[segmentIndex];
  if (!segment) return;
  segment.ctrl.startGuide(segment.stepsCount);
  activeCtrl = segment.ctrl;
  currentGuideKey.value = segment.key;
  if (segmentIndex === 0) {
    currentStepIndex.value = 0;
  }
};

const handleNext = () => {
  // 🔵 Fix6: 防重复点击，避免快速连点导致 index 越界
  if (!activeCtrl || isHandlingNext.value) return;
  isHandlingNext.value = true;

  try {
    if (currentStepData.value && currentStepData.value.action) {
      emit('action', currentStepData.value.action, currentStepData.value);
    }

    const currentSegmentIndex = guideSegments.findIndex(
      s => currentStepIndex.value >= s.startIndex && currentStepIndex.value <= s.endIndex
    );
    const currentSegment = guideSegments[currentSegmentIndex];

    if (!currentSegment) return;

    if (currentStepIndex.value === steps.value.length - 1) {
      completeCurrentSegment(currentSegment);
      handleCompleteAll();
      return;
    }

    if (currentStepIndex.value === currentSegment.endIndex) {
      completeCurrentSegment(currentSegment);
      const nextSegmentIndex = currentSegmentIndex + 1;
      if (guideSegments[nextSegmentIndex]) {
        activateSegment(nextSegmentIndex);
      }
    } else {
      activeCtrl.nextStep();
    }

    currentStepIndex.value++;
    emit('next', currentStepIndex.value);
  } finally {
    // 用 nextTick 解锁，确保 UI 更新后才允许下次点击
    nextTick(() => { isHandlingNext.value = false; });
  }
};

const handleTargetClick = () => {
  if (currentStepData.value && currentStepData.value.disableTargetClick) return;
  handleNext();
};

const completeCurrentSegment = (segment) => {
  if (segment && segment.ctrl) {
    segment.ctrl.completeGuide();
    globalMgr.markGuideCompleted(segment.key);
    emit('complete', segment.key);
  }
};

const handleCompleteAll = () => {
  processNext();
};

// 🔴 Fix3: handleSkip 时将所有未完成的中间 segment 也标记为 completed
// 原来只调用了 activeCtrl.skipGuide()，中间跳过的 segment 不会被记录完成状态，
// 导致下次进入时这些 segment 会重复展示
const handleSkip = () => {
  if (!activeCtrl) return;

  // 找到当前激活的 segment 索引
  const currentSegmentIndex = guideSegments.findIndex(
    s => currentStepIndex.value >= s.startIndex && currentStepIndex.value <= s.endIndex
  );

  // 将当前及之前所有已激活但未完成的 segment 都标记完成
  // （之后的 segment 从未开始，不需要记录）
  for (let i = 0; i <= currentSegmentIndex; i++) {
    const seg = guideSegments[i];
    if (seg && seg.ctrl) {
      seg.ctrl.skipGuide();
      // 同时在全局管理器中也标记，防止下次重复展示
      globalMgr.markGuideCompleted(seg.key);
    }
  }

  emit('skip', currentGuideKey.value);
  processNext();
};

const resetLocalState = () => {
  if (guideSegments && guideSegments.length > 0) {
    guideSegments.forEach(seg => {
      if (seg.ctrl && seg.ctrl.destroy) {
        seg.ctrl.destroy();
      }
    });
  }

  guideSegments = [];
  activeCtrl = null;
  steps.value = [];
  currentStepIndex.value = 0;
  targetRect.value = null;
  currentGuideKey.value = null;

  hasScrolled.value = false;
  isScrolling.value = false;
  shouldDelayShow.value = false;
  isHandlingNext.value = false; // 🔵 Fix6: 一起重置
};

defineExpose({ check, startGuide });

// ==================== UI层 ====================

// 🔵 Fix7: windowInfo 提升到模块级别缓存，避免每次实例化都同步阻塞
const windowInfo = uni.getSystemInfoSync();

const targetRect = ref(null);
let updateInterval = null;
let safetyTimer = null; // 🔵 Fix1&Fix6: 兜底超时句柄
let hasScrolled = ref(false);
let isScrolling = ref(true);
let shouldDelayShow = ref(false);

const currentStepData = computed(() => steps.value[currentStepIndex.value] || null);
const isLastStep = computed(() => currentStepIndex.value === steps.value.length - 1);

const spotlightStyle = computed(() => {
  if (!targetRect.value) return {};
  const padding = 6;
  return {
    top: `${targetRect.value.top - padding}px`,
    left: `${targetRect.value.left - padding}px`,
    width: `${targetRect.value.width + padding * 2}px`,
    height: `${targetRect.value.height + padding * 2}px`,
    boxShadow: '0 0 0 9999px rgba(26, 26, 26, 0.72)'
  };
});

const tooltipStyle = computed(() => {
  if (!targetRect.value || currentStepData.value.position === 'center') {
    return { top: '50%', left: '50%', transform: 'translate(-50%, -50%)' };
  }

  const rect = targetRect.value;
  const margin = 16;
  const tooltipW = 280;

  let left = rect.left + (rect.width / 2) - (tooltipW / 2);
  if (left < 16) left = 16;
  if (left + tooltipW > windowInfo.windowWidth - 16) {
    left = windowInfo.windowWidth - tooltipW - 16;
  }

  const safeTop = windowInfo.safeAreaInsets ? windowInfo.safeAreaInsets.top + 44 : 88;
  const safeBottom = windowInfo.safeAreaInsets ? windowInfo.safeAreaInsets.bottom + 20 : 40;

  const spaceAbove = rect.top - safeTop;
  const spaceBelow = windowInfo.windowHeight - rect.bottom - safeBottom;

  const estimatedHeight = 220;

  let placeOnTop = currentStepData.value.position === 'top';

  if (placeOnTop) {
    if (spaceAbove < estimatedHeight && spaceBelow > spaceAbove) placeOnTop = false;
  } else {
    if (spaceBelow < estimatedHeight && spaceAbove > spaceBelow) placeOnTop = true;
  }

  if (placeOnTop) {
    if (spaceAbove >= estimatedHeight) {
      return { bottom: `${windowInfo.windowHeight - rect.top + margin}px`, left: `${left}px` };
    } else {
      return { top: `${safeTop}px`, left: `${left}px` };
    }
  } else {
    if (spaceBelow >= estimatedHeight) {
      return { top: `${rect.bottom + margin}px`, left: `${left}px` };
    } else {
      return { bottom: `${safeBottom}px`, left: `${left}px` };
    }
  }
});

const isElementInViewport = (rect) => {
  if (!rect) return false;
  const elementCenter = rect.top + rect.height / 2;
  const screenCenter = windowInfo.windowHeight / 2;
  const distance = Math.abs(elementCenter - screenCenter);
  return distance < windowInfo.windowHeight / 4;
};

// 🟡 Fix4: 将两次独立的 scrollOffset 查询合并为一次，消除异步时序不一致
// 原来 calculateScrolledRect 和 scrollToElement 各自查询一次 scrollTop，
// 两次异步之间若页面有滚动，预测位置和实际滚动目标会不一致导致聚光灯偏移
const scrollToElementAndPredict = (rect, onPredicted) => {
  if (!rect) {
    onPredicted && onPredicted(null);
    return;
  }

  uni.createSelectorQuery().selectViewport().scrollOffset(res => {
    const elementCenter = rect.top + rect.height / 2;
    const screenCenter = windowInfo.windowHeight / 2;
    const scrollOffset = elementCenter - screenCenter;
    const currentScrollTop = res.scrollTop || 0;
    let targetScrollTop = currentScrollTop + scrollOffset;

    const scrollHeight = res.scrollHeight || 0;
    const maxScrollTop = Math.max(0, scrollHeight - windowInfo.windowHeight);
    targetScrollTop = Math.max(0, Math.min(targetScrollTop, maxScrollTop));

    const actualScrollOffset = targetScrollTop - currentScrollTop;
    const scrolledRect = {
      ...rect,
      top: rect.top - actualScrollOffset,
      bottom: rect.bottom - actualScrollOffset
    };

    // 先回调预测位置，再执行滚动 —— 同一次查询，数据一致
    onPredicted && onPredicted(scrolledRect);

    isScrolling.value = true;
    uni.pageScrollTo({
      scrollTop: targetScrollTop,
      duration: 300,
      success: () => {
        setTimeout(() => {
          isScrolling.value = false;
          shouldDelayShow.value = false;
        }, 100);
      },
      fail: () => {
        isScrolling.value = false;
        shouldDelayShow.value = false;
      }
    });
  }).exec();
};

// 🔵 Fix1: 统一的兜底超时清理函数
const clearSafetyTimer = () => {
  if (safetyTimer) {
    clearTimeout(safetyTimer);
    safetyTimer = null;
  }
};

const startSafetyTimer = (ms = 800) => {
  clearSafetyTimer();
  safetyTimer = setTimeout(() => {
    // 超时后强制解除 loading 状态，防止提示框永久消失
    if (isScrolling.value || shouldDelayShow.value) {
      isScrolling.value = false;
      shouldDelayShow.value = false;
    }
  }, ms);
};

const updateTargetByData = (data, step) => {
  if (data && data.width > 0) {
    if (!hasScrolled.value) {
      const needScroll = step.disableScroll ? false : !isElementInViewport(data);

      if (needScroll) {
        shouldDelayShow.value = true;
        // 🟡 Fix4: 用合并后的单次查询替代原来两次独立查询
        scrollToElementAndPredict(data, (scrolledRect) => {
          targetRect.value = scrolledRect;
        });
      } else {
        shouldDelayShow.value = false;
        targetRect.value = data;
        isScrolling.value = false;
        clearSafetyTimer(); // 不需要滚动时立即清除兜底计时器
      }
      hasScrolled.value = true;
    } else if (!isScrolling.value) {
      targetRect.value = data;
      isScrolling.value = false;
    }
  } else {
    targetRect.value = null;
    isScrolling.value = false;
    // 🔴 Fix1: 原来这里缺少 shouldDelayShow 的重置，导致提示框消失但遮罩残留
    shouldDelayShow.value = false;
    clearSafetyTimer();
  }
};

const updateTargetRect = async () => {
  const step = currentStepData.value;
  if (!step || !step.targetId) {
    targetRect.value = null;
    isScrolling.value = false;
    shouldDelayShow.value = false; // 🔴 Fix1: 同样补上
    return;
  }

  if (step.targetId.includes(":")) {
    const data = await domQueryService.query(step.targetId);
    updateTargetByData(data, step);
  } else {
    const query = uni.createSelectorQuery();
    query.select('#' + step.targetId).boundingClientRect(data => {
      updateTargetByData(data, step);
    }).exec();
  }
};

watch(currentStepIndex, () => {
  if (!showGuide.value) return;
  hasScrolled.value = false;
  isScrolling.value = true;
  shouldDelayShow.value = false;

  // 🔵 Fix6: 步骤切换时启动兜底超时，防止 isScrolling 卡住永不恢复
  startSafetyTimer(1000);

  nextTick(() => updateTargetRect());
});

watch(showGuide, (val) => {
  if (val) {
    hasScrolled.value = false;
    isScrolling.value = true;
    shouldDelayShow.value = false;

    startSafetyTimer(1000); // 🔵 Fix6: 初次展示也加兜底

    nextTick(() => updateTargetRect());

    // 🟡 Fix5: 先清除旧 interval 再建新的，防止覆盖导致幽灵定时器
    if (updateInterval) clearInterval(updateInterval);
    updateInterval = setInterval(() => {
      if (isScrolling.value) return;
      updateTargetRect();
    }, 1000);
  } else {
    if (updateInterval) {
      clearInterval(updateInterval);
      updateInterval = null; // 清除后置 null，避免重复 clear
    }
    clearSafetyTimer(); // 关闭引导时也清除兜底计时器
  }
});

onUnmounted(() => {
  if (updateInterval) clearInterval(updateInterval);
  clearSafetyTimer();
});
</script>

<style scoped>
@keyframes pulse-border {

  0%,
  100% {
    box-shadow: 0 0 0 9999px rgba(26, 26, 26, 0.72), 0 0 0 0px rgba(255, 193, 7, 0.55);
  }

  50% {
    box-shadow: 0 0 0 9999px rgba(26, 26, 26, 0.72), 0 0 0 9px rgba(255, 193, 7, 0);
  }
}

.animate-pulse-border {
  animation: pulse-border 2.2s ease-in-out infinite;
}
</style>