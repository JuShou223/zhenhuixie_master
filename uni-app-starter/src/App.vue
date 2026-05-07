<!--
 * @Date: 2026-04-13 20:50:34
 * @LastEditors: 徐一鸣
 * @LastEditTime: 2026-04-16 13:35:58
 * @Description:
-->
<script setup lang="ts">
// App.vue 全局样式和生命周期
</script>

<template>
  <page />
</template>

<style lang="scss">
@import '@/static/css/iconfont.css';

/* 隐藏原生 tabbar — 使用自定义 tab-bar 组件替代 */
uni-tabbar { display: none !important; }

/* 全局样式 */
page {
  background-color: #FDFCF8;
  font-family: "Outfit", -apple-system, BlinkMacSystemFont, "PingFang SC", "Hiragino Sans GB", "Microsoft YaHei", sans-serif;
}

/* 针对安卓 App 端 WebView 的全局重置 */
page,
uni-page-body,
html,
body {
  margin: 0 !important;
  padding: 0 !important;
  width: 100% !important;
  height: 100% !important;
}

.-translate-x-1\/2,
.-translate-x-1_f2 {
  transform: translateX(-50%) !important;
}

.-translate-y-1\/2,
.-translate-y-1_f2 {
  transform: translateY(-50%) !important;
}

.-translate-x-1\/2.-translate-y-1\/2,
.-translate-x-1_f2.-translate-y-1_f2 {
  transform: translate(-50%, -50%) !important;
}

.inset-0 {
  top: 0;
  right: 0;
  bottom: 0;
  left: 0;
  /* 保留 inset 以备后用，但前面的 top/right... 已经能确保定位生效了 */
  inset: 0;
}

/* 如果你用了 inset-x-0 (水平铺满) */
.inset-x-0 {
  left: 0;
  right: 0;
  inset-inline: 0;
}

/* 如果你用了 inset-y-0 (垂直铺满) */
.inset-y-0 {
  top: 0;
  bottom: 0;
  inset-block: 0;
}

button {
  cursor: pointer;
  outline: none;
  border: none;
  background: none;
  margin: 0;
  line-height: 1 !important;
}

button:after {
  content: "";
  border: none;
  outline: none;
}

uni-button:after {
  content: "";
  border: none !important;
  outline: none !important;
}


/*每个页面公共css */
/* 定义动画 */
@keyframes fade-in {
  from {
    opacity: 0;
    transform: translateY(10px) scale(0.95);
  }

  to {
    opacity: 1;
    transform: translateY(0) scale(1);
  }
}

@keyframes fade-out {
  from { opacity: 1; transform: scale(1); }
  to   { opacity: 0; transform: scale(0.9); }
}

/* Data Wave Animation */
@keyframes data-wave {

  0%,
  100% {
    transform: scaleY(0.4);
    opacity: 0.5;
  }

  50% {
    transform: scaleY(1);
    opacity: 1;
  }
}

.animate-data-wave {
  animation: data-wave 1s ease-in-out infinite;
}

.animate-fade-in {
  animation: fade-in 0.6s ease-out forwards;
}

.border-t {
  border-top-style: solid;
}

.border-b {
  border-bottom-style: solid;
}

.border-r {
  border-right-style: solid;
}

.border-l {
  border-left-style: solid;
}

.border {
  border-style: solid;
}

.pb-safe {
  padding-bottom: calc(env(safe-area-inset-bottom));
}

/* Skeleton shimmer */
@keyframes skeleton-shimmer {
  0%, 100% { opacity: 1; }
  50% { opacity: 0.4; }
}

.skeleton {
  animation: skeleton-shimmer 1.4s ease-in-out infinite;
  background-color: #e5e7eb;
  border-radius: 4px;
}

@media (prefers-reduced-motion: reduce) {
  .skeleton {
    animation: none;
    opacity: 0.6;
  }
}

/* Like heart bounce — spring physics */
@keyframes like-bounce {
  0%   { transform: scale(1); }
  20%  { transform: scale(1.35); }
  45%  { transform: scale(0.85); }
  70%  { transform: scale(1.08); }
  100% { transform: scale(1); }
}

.like-bounce {
  animation: like-bounce 0.45s cubic-bezier(0.34, 1.56, 0.64, 1);
}

/* Branch card staggered fade-in */
@keyframes branch-in {
  from {
    opacity: 0;
    transform: translateY(16px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.animate-branch-in {
  animation: branch-in 0.4s cubic-bezier(0.22, 0.61, 0.36, 1) both;
}

@media (prefers-reduced-motion: reduce) {
  .like-bounce,
  .animate-branch-in {
    animation: none;
  }
}
</style>
