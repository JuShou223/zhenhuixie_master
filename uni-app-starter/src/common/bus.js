// 全局事件总线（基于 mitt）
// 用法：
//   import bus from '@/common/bus.js'
//   bus.emit('事件名', 数据)       // 发送
//   bus.on('事件名', (data) => {}) // 监听
//   bus.off('事件名')              // 移除

import mitt from "mitt";
export default mitt();
