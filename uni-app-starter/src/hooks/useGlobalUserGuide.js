// hooks/useGlobalGuideManager.js
import { ref, computed } from "vue";
import bus from "@/common/bus.js";
// import { uni.getStorageSync, uni.setStorageSync } from "@dcloudio/uni-h5";

// 引导配置类型定义
export const GUIDE_TYPES = {
  HOME_PAGE: "home_page", // 首页引导
  DIAGNOSIS: "diagnosis", // AI诊断引导
  TREATMENT: "treatment", // 治疗方案引导
  SENSOR: "sensor_connect", // 传感器连接引导
  PLANT_ADD: "plant_add", // 添加植物引导
  HISTORY_CENTER: "history_center", // 历史中心引导
  ABNORMAL_PLANTS: "abnormal_plants", // 异常植物引导
  ACTIVE_TREATMENTS: "active_treatments", // 进行中治疗引导
  PLANT_DETAIL: "plant_detail",
  SENSOR_CONFIG: "sensor_config",
  // 真会写
  STORY_HOME: "story_home",     // 首页功能引导
  STORY_DETAIL: "story_detail", // 故事详情页引导
  BRANCH_DETAIL: "branch_detail", // 分支详情页引导
};

// 页面引导配置
export const PAGE_GUIDES = {
  [GUIDE_TYPES.HOME_PAGE]: {
    key: GUIDE_TYPES.HOME_PAGE,
    title: "首页功能引导",
    version: "1.0.0",
    priority: 1, // 🌟 优先级：数值越小越靠前
    maxShowCount: 1,
    showInterval: 0, // 30天后可再次显示
    dependencies: [], // 依赖的其他引导
  },
  // [GUIDE_TYPES.DIAGNOSIS]: {
  //   key: GUIDE_TYPES.DIAGNOSIS,
  //   title: "AI诊断功能引导",
  //   version: "1.0.0",
  //   priority: 110,
  //   maxShowCount: 1,
  //   showInterval: 0,
  //   dependencies: [GUIDE_TYPES.HOME_PAGE],
  // },
  // [GUIDE_TYPES.HISTORY_CENTER]: {
  //   key: GUIDE_TYPES.HISTORY_CENTER,
  //   title: "历史记录中心引导",
  //   version: "1.0.0",
  //   priority: 120,
  //   maxShowCount: 1,
  //   showInterval: 0,
  //   dependencies: [GUIDE_TYPES.DIAGNOSIS, GUIDE_TYPES.TREATMENT],
  // },
  [GUIDE_TYPES.ABNORMAL_PLANTS]: {
    key: GUIDE_TYPES.ABNORMAL_PLANTS,
    title: "异常植物提醒引导",
    version: "1.0.0",
    priority: 1, // 🌟 紧急/重要引导，优先级设高（数值小）
    maxShowCount: 1,
    showInterval: 0,
    triggerConditions: ["abnormalCount > 0"], // 触发条件
    dependencies: [],
  },
  [GUIDE_TYPES.ACTIVE_TREATMENTS]: {
    key: GUIDE_TYPES.ACTIVE_TREATMENTS,
    title: "进行中治疗引导",
    version: "1.0.0",
    priority: 3, // 🌟 次重要
    maxShowCount: 1,
    showInterval: 0,
    triggerConditions: ["activeTreatmentCount > 0"], // 触发条件
    dependencies: [],
  },
  [GUIDE_TYPES.TREATMENT]: {
    key: GUIDE_TYPES.TREATMENT,
    title: "治疗方案引导",
    version: "1.0.0",
    maxShowCount: 1,
    showInterval: 0,
    triggerConditions: [], // 触发条件
    dependencies: [],
  },
  // 🌟 新增：植物详情页引导配置
  [GUIDE_TYPES.PLANT_DETAIL]: {
    key: GUIDE_TYPES.PLANT_DETAIL,
    title: "植物详情页功能引导",
    version: "1.0.0",
    maxShowCount: 1, // 只显示一次
    showInterval: 0,
    priority: 10,
    // 只有当用户完成了首页引导后，才在详情页显示这个引导
    dependencies: [],
    // dependencies: [GUIDE_TYPES.HOME_PAGE],
  },
  [GUIDE_TYPES.SENSOR_CONFIG]: {
    key: GUIDE_TYPES.SENSOR_CONFIG,
    title: "智能预警配置",
    version: "1.0.0",
    maxShowCount: 1, // 只显示一次
    showInterval: 0,
    priority: 11,
    dependencies: [],
    triggerConditions: ["deviceCount > 0"], // 触发条件
    // dependencies: [GUIDE_TYPES.HOME_PAGE],
  },
  // 真会写：首页引导（自动触发，只展示一次）
  [GUIDE_TYPES.STORY_HOME]: {
    key: GUIDE_TYPES.STORY_HOME,
    title: "首页功能引导",
    version: "1.0.0",
    priority: 1,
    maxShowCount: 1,
    showInterval: 0,
    dependencies: [],
  },
  // 真会写：故事详情页引导
  [GUIDE_TYPES.STORY_DETAIL]: {
    key: GUIDE_TYPES.STORY_DETAIL,
    title: "故事详情功能引导",
    version: "1.0.0",
    priority: 2,
    maxShowCount: 1,
    showInterval: 0,
    dependencies: [],
  },
  // 真会写：分支详情页引导
  [GUIDE_TYPES.BRANCH_DETAIL]: {
    key: GUIDE_TYPES.BRANCH_DETAIL,
    title: "分支详情功能引导",
    version: "1.0.0",
    priority: 3,
    maxShowCount: 1,
    showInterval: 0,
    dependencies: [],
  },
};

/**
 * 全局引导管理器
 */
export function useGlobalGuideManager() {
  // 所有引导的状态
  const allGuides = ref({});

  // 初始化所有引导
  const initializeGuides = () => {
    Object.keys(PAGE_GUIDES).forEach((key) => {
      const config = PAGE_GUIDES[key];

      // 🌟 新增逻辑：版本检查与自动重置
      const storageKeyVersion = `guide_${key}_version`;
      const savedVersion = uni.getStorageSync(storageKeyVersion);

      // 如果配置中有版本号，且与本地存储的版本不一致
      if (config.version && savedVersion !== config.version) {
        console.log(
          `[Guide] Version mismatch for ${key}. Resetting status. Old: ${savedVersion}, New: ${config.version}`,
        );
        // 重置该引导的所有状态
        uni.removeStorageSync(`guide_${key}_completed`);
        uni.removeStorageSync(`guide_${key}_show_count`);
        uni.removeStorageSync(`guide_${key}_last_show_date`);
        // 更新本地版本号
        uni.setStorageSync(storageKeyVersion, config.version);
      }

      // 正常读取状态 (此时如果是新版本，状态已被重置为初始值)
      allGuides.value[key] = {
        ...config,
        isCompleted: uni.getStorageSync(`guide_${key}_completed`) || false,
        lastShowTime: uni.getStorageSync(`guide_${key}_last_show_date`) || null,
        showCount: uni.getStorageSync(`guide_${key}_show_count`) || 0,
      };
    });
  };

  // 获取引导状态
  const getGuideStatus = (guideKey) => {
    return allGuides.value[guideKey] || null;
  };

  // 检查引导依赖是否满足
  const checkDependencies = (guideKey) => {
    const guide = PAGE_GUIDES[guideKey];
    if (!guide || !guide.dependencies) return true;
    return guide.dependencies.every((depKey) => {
      const depGuide = allGuides.value[depKey];
      return depGuide && depGuide.isCompleted;
    });
  };

  // 检查触发条件
  const checkTriggerConditions = (guideKey, context = {}) => {
    const guide = PAGE_GUIDES[guideKey];
    if (!guide.triggerConditions) return true;

    try {
      return guide.triggerConditions.every((condition) => {
        // 简单的条件判断，实际可以更复杂
        if (condition.includes(">")) {
          const [key, value] = condition.split(">").map((s) => s.trim());
          return Number(context[key]) > Number(value);
        }
        if (condition.includes("===")) {
          const [key, value] = condition.split("===").map((s) => s.trim());
          return Number(context[key]) === Number(value);
        }
        return context[condition] === true;
      });
    } catch (error) {
      console.error("检查引导触发条件失败:", error);
      return false;
    }
  };

  // 获取待显示的引导
  const getPendingGuides = (context = {}) => {
    return Object.values(PAGE_GUIDES)
      .filter((guide) => {
        const status = allGuides.value[guide.key];
        // console.log("检查引导:", guide.key, status);
        if (!status) return false;
        // 已完成的不显示
        if (status.isCompleted) return false;
        // 检查显示次数限制
        if (status.showCount >= guide.maxShowCount) return false;

        // 检查显示间隔
        if (status.lastShowTime) {
          const daysSinceLastShow = Math.floor(
            (Date.now() - new Date(status.lastShowTime).getTime()) /
              (1000 * 60 * 60 * 24),
          );
          if (daysSinceLastShow < guide.showInterval) return false;
        }

        // 检查依赖
        if (!checkDependencies(guide.key)) return false;
        // 检查触发条件
        if (!checkTriggerConditions(guide.key, context)) return false;

        return true;
      })
      .sort((a, b) => {
        // 🌟 排序逻辑：数值越小越靠前
        // 如果没有定义 priority，默认为 999（排在最后）
        const p1 = typeof a.priority === "number" ? a.priority : 999;
        const p2 = typeof b.priority === "number" ? b.priority : 999;
        return p1 - p2;
      });
  };

  // 记录引导显示
  const recordGuideShown = (guideKey) => {
    if (allGuides.value[guideKey]) {
      allGuides.value[guideKey].showCount++;
      allGuides.value[guideKey].lastShowTime = new Date().toISOString();

      uni.setStorageSync(
        `guide_${guideKey}_show_count`,
        allGuides.value[guideKey].showCount,
      );
      uni.setStorageSync(
        `guide_${guideKey}_last_show_date`,
        allGuides.value[guideKey].lastShowTime,
      );
    }
  };

  // 标记引导完成
  const markGuideCompleted = (guideKey) => {
    if (allGuides.value[guideKey]) {
      allGuides.value[guideKey].isCompleted = true;
      uni.setStorageSync(`guide_${guideKey}_completed`, true);
      bus.emit("guide:global:completed", { guideKey });
    }
  };

  // 重置所有引导
  const resetAllGuides = () => {
    Object.keys(PAGE_GUIDES).forEach((key) => {
      const storageKeys = [
        `guide_${key}_completed`,
        `guide_${key}_show_count`,
        `guide_${key}_last_show_date`,
        `guide_${key}_version`,
        `guide_${key}_step_progress`,
        `guide_${key}_skip_count`,
        `guide_${key}_viewed_steps`,
      ];

      storageKeys.forEach((storageKey) => {
        uni.removeStorageSync(storageKey);
      });
    });

    initializeGuides();
  };

  // 获取统计报告
  const getGuideReport = () => {
    const totalGuides = Object.keys(PAGE_GUIDES).length;
    const completedGuides = Object.values(allGuides.value).filter(
      (g) => g.isCompleted,
    ).length;
    const completionRate =
      totalGuides > 0 ? ((completedGuides / totalGuides) * 100).toFixed(1) : 0;

    return {
      totalGuides,
      completedGuides,
      completionRate: `${completionRate}%`,
      guides: allGuides.value,
    };
  };

  // 导出引导数据（用于分析）
  const exportGuideData = () => {
    return {
      timestamp: new Date().toISOString(),
      report: getGuideReport(),
      detail: Object.keys(PAGE_GUIDES).reduce((acc, key) => {
        acc[key] = {
          config: PAGE_GUIDES[key],
          status: allGuides.value[key],
          storage: {
            completed: uni.getStorageSync(`guide_${key}_completed`),
            showCount: uni.getStorageSync(`guide_${key}_show_count`),
            lastShowDate: uni.getStorageSync(`guide_${key}_last_show_date`),
            stepProgress: uni.getStorageSync(`guide_${key}_step_progress`),
          },
        };
        return acc;
      }, {}),
    };
  };

  // 初始化
  initializeGuides();

  return {
    // 状态
    allGuides: computed(() => allGuides.value),

    // 操作
    getGuideStatus,
    getPendingGuides,
    recordGuideShown,
    markGuideCompleted,
    resetAllGuides,

    // 报告
    getGuideReport,
    exportGuideData,

    // 常量
    GUIDE_TYPES,
    PAGE_GUIDES,
  };
}
