/*
 * @Date: 2026-05-06 12:52:01
 * @LastEditors: 徐一鸣
 * @LastEditTime: 2026-05-08 10:22:44
 * @Description:
 */
import bus from "@/common/bus.js";
import { ref } from "vue";

export const TABBAR_LIST = [
  {
    label: "首页",
    icon: "icon-lucide-layout-grid",
    value: "home",
    path: "/pages/index/index",
  },
  {
    mode: "midButton",
  },
  {
    label: "我的",
    icon: "icon-lucide-user",
    value: "my",
    path: "/pages/my/index",
  },
];

export function useTabbar() {
  const activeTabbar = ref("");

  const loadTabbarChangeListener = () => {
    bus.on("setActiveBar", (value) => {
      activeTabbar.value = value;
    });
  };

  const handleSetTabbar = (value) => {
    if (activeTabbar.value !== value) {
      bus.emit("setActiveBar", value);
    }
  };

  return {
    activeTabbar,
    loadTabbarChangeListener,
    handleSetTabbar,
  };
}
