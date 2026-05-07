// hooks/useScrollToElement.js
import { ref } from "vue";

/**
 * 元素滚动到屏幕中间的可复用 Hook
 * @param {Object} options 配置项
 * @param {number} options.duration 滚动动画时长，默认 300ms
 * @param {number} options.viewportThreshold 判断元素是否在可视区域的阈值（屏幕高度的比例），默认 0.25
 * @returns {Object} 返回滚动相关的方法和状态
 */
export function useScrollToElement(options = {}) {
  const { duration = 300, viewportThreshold = 0.25 } = options;

  const windowInfo = uni.getSystemInfoSync();
  const isScrolling = ref(false);

  /**
   * 判断元素是否在可视区域内（接近屏幕中心）
   * @param {Object} rect 元素的位置信息
   * @returns {boolean} 是否在可视区域内
   */
  const isElementInViewport = (rect) => {
    if (!rect) return false;

    const elementCenter = rect.top + rect.height / 2;
    const screenCenter = windowInfo.windowHeight / 2;
    const distance = Math.abs(elementCenter - screenCenter);

    // 如果距离小于阈值，认为已经足够接近中心
    return distance < windowInfo.windowHeight * viewportThreshold;
  };

  /**
   * 计算滚动后元素的位置
   * @param {Object} rect 元素当前位置信息
   * @param {Function} callback 回调函数，返回计算后的位置
   */
  const calculateScrolledRect = (rect, callback) => {
    if (!rect) {
      callback && callback(null);
      return;
    }

    uni
      .createSelectorQuery()
      .selectViewport()
      .scrollOffset((res) => {
        const elementCenter = rect.top + rect.height / 2;
        const screenCenter = windowInfo.windowHeight / 2;
        const scrollOffset = elementCenter - screenCenter;
        const currentScrollTop = res.scrollTop || 0;

        let targetScrollTop = currentScrollTop + scrollOffset;

        // 限制滚动范围
        const scrollHeight = res.scrollHeight || 0;
        const maxScrollTop = Math.max(
          0,
          scrollHeight - windowInfo.windowHeight
        );
        targetScrollTop = Math.max(0, Math.min(targetScrollTop, maxScrollTop));

        // 计算实际会滚动的距离
        const actualScrollOffset = targetScrollTop - currentScrollTop;

        // 计算滚动后元素的新位置
        const scrolledRect = {
          ...rect,
          top: rect.top - actualScrollOffset,
          bottom: rect.bottom - actualScrollOffset,
        };

        callback && callback(scrolledRect, actualScrollOffset);
      })
      .exec();
  };

  /**
   * 滚动到指定元素（使其居中）
   * @param {Object} rect 元素位置信息
   * @returns {Promise} 返回 Promise，滚动完成后 resolve
   */
  const scrollToElement = (rect) => {
    return new Promise((resolve, reject) => {
      if (!rect) {
        reject(new Error("无效的元素位置信息"));
        return;
      }

      uni
        .createSelectorQuery()
        .selectViewport()
        .scrollOffset((res) => {
          const elementCenter = rect.top + rect.height / 2;
          const screenCenter = windowInfo.windowHeight / 2;
          const scrollOffset = elementCenter - screenCenter;
          const currentScrollTop = res.scrollTop || 0;

          let targetScrollTop = currentScrollTop + scrollOffset;

          // 限制滚动范围
          const scrollHeight = res.scrollHeight || 0;
          const maxScrollTop = Math.max(
            0,
            scrollHeight - windowInfo.windowHeight
          );
          targetScrollTop = Math.max(
            0,
            Math.min(targetScrollTop, maxScrollTop)
          );

          isScrolling.value = true;

          uni.pageScrollTo({
            scrollTop: targetScrollTop,
            duration,
            success: () => {
              setTimeout(() => {
                isScrolling.value = false;
                resolve({
                  scrollTop: targetScrollTop,
                  scrollOffset: targetScrollTop - currentScrollTop,
                });
              }, 100);
            },
            fail: (err) => {
              isScrolling.value = false;
              reject(err);
            },
          });
        })
        .exec();
    });
  };

  /**
   * 根据元素 ID 滚动到该元素
   * @param {string} elementId 元素的 ID
   * @returns {Promise} 返回 Promise
   */
  const scrollToElementById = (elementId) => {
    return new Promise((resolve, reject) => {
      if (!elementId) {
        reject(new Error("元素 ID 不能为空"));
        return;
      }

      const query = uni.createSelectorQuery();
      query
        .select("#" + elementId)
        .boundingClientRect((data) => {
          if (data && data.width > 0) {
            scrollToElement(data).then(resolve).catch(reject);
          } else {
            reject(new Error(`未找到元素: #${elementId}`));
          }
        })
        .exec();
    });
  };

  /**
   * 智能滚动：只在需要时滚动
   * @param {string|Object} target 元素 ID 或位置信息
   * @returns {Promise} 返回 Promise，resolve 时返回 { needScroll, scrollInfo }
   */
  const smartScroll = (target) => {
    return new Promise((resolve, reject) => {
      const handleRect = (rect) => {
        if (!rect || rect.width === 0) {
          reject(new Error("无效的元素"));
          return;
        }

        const needScroll = !isElementInViewport(rect);

        if (needScroll) {
          scrollToElement(rect)
            .then((scrollInfo) => {
              resolve({ needScroll: true, scrollInfo, rect });
            })
            .catch(reject);
        } else {
          resolve({ needScroll: false, rect });
        }
      };

      if (typeof target === "string") {
        // 传入的是 ID
        const query = uni.createSelectorQuery();
        query
          .select("#" + target)
          .boundingClientRect(handleRect)
          .exec();
      } else {
        // 传入的是 rect 对象
        handleRect(target);
      }
    });
  };

  return {
    isScrolling,
    isElementInViewport,
    calculateScrolledRect,
    scrollToElement,
    scrollToElementById,
    smartScroll,
  };
}
