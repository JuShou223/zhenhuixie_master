/*
 * @Date: 2026-05-06 12:52:01
 * @LastEditors: 徐一鸣
 * @LastEditTime: 2026-05-07 16:08:09
 * @Description:
 */
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
  const activeTabbar = ref("home");

  const handleSetTabbar = (value) => {
    activeTabbar.value = value;
  };

  return {
    activeTabbar,
    handleSetTabbar,
  };
}
