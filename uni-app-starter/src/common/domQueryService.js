/**
 * DOM 查询服务
 * 用于解决跨组件、跨 Shadow DOM 的元素查找问题
 */
class DomQueryService {
  constructor() {
    this.queryHandlers = new Map();
    this.activePageInstance = null;
    this.handlerCache = new Map();
  }

  static getInstance() {
    if (!DomQueryService.instance) {
      DomQueryService.instance = new DomQueryService();
    }
    return DomQueryService.instance;
  }

  setActivePageInstance(instance) {
    this.activePageInstance = instance;
    this.handlerCache.clear();
  }

  /**
   * 注册查询处理器
   * @param {string} namespace 命名空间，例如 'tabbar'
   * @param {Function|Object} handlerOrInstance
   * @param {number} priority 优先级
   */
  registerHandler(namespace, handlerOrInstance, priority = 0) {
    if (!this.queryHandlers.has(namespace)) {
      this.queryHandlers.set(namespace, []);
    }
    const handlers = this.queryHandlers.get(namespace);
    handlers.push({ item: handlerOrInstance, priority });
    handlers.sort((a, b) => b.priority - a.priority);
  }

  unregisterHandler(namespace, handlerOrInstance) {
    if (!this.queryHandlers.has(namespace)) return;
    const handlers = this.queryHandlers.get(namespace);
    const index = handlers.findIndex((h) => h.item === handlerOrInstance);
    if (index > -1) handlers.splice(index, 1);
  }

  async query(targetId) {
    if (!targetId) return null;

    const cacheKey = `${targetId}_${this.activePageInstance ? this.activePageInstance.uid || "page" : "global"}`;
    if (this.handlerCache.has(cacheKey)) return this.handlerCache.get(cacheKey);

    let result = null;

    if (targetId.includes(":")) {
      const [namespace, realTargetId] = targetId.split(":");
      result = await this.queryByNamespace(namespace, realTargetId);
    } else {
      result = await this.autoQuery(targetId);
    }

    if (result && result.width > 0) {
      this.handlerCache.set(cacheKey, result);
      setTimeout(() => this.handlerCache.delete(cacheKey), 1000);
    }
    return result;
  }

  async queryByNamespace(namespace, targetId) {
    const handlers = this.queryHandlers.get(namespace) || [];

    for (const { item } of handlers) {
      try {
        let result = null;

        if (typeof item === "function") {
          result = await item(targetId);
        } else if (item && typeof item === "object") {
          result = await this.globalQueryInContext(targetId, item);
        }

        if (result && result.width > 0 && result.height > 0) {
          return result;
        }
      } catch (error) {
        console.error(`[DomQueryService] Error querying namespace "${namespace}":`, error);
      }
    }

    return null;
  }

  async autoQuery(targetId) {
    if (this.activePageInstance) {
      const r = await this.globalQueryInContext(targetId, this.activePageInstance);
      if (r && r.width > 0) return r;
    }

    for (const [ns, handlers] of this.queryHandlers) {
      for (const { item } of handlers) {
        let res = null;
        if (typeof item === "function") {
          res = await item(targetId);
        } else if (item && typeof item === "object") {
          res = await this.globalQueryInContext(targetId, item);
        }
        if (res && res.width > 0 && res.height > 0) return res;
      }
    }

    return await this.globalQuery(targetId);
  }

  async globalQueryInContext(targetId, contextInstance) {
    return new Promise((resolve) => {
      let query = uni.createSelectorQuery();
      if (contextInstance) {
        query = query.in(contextInstance);
      }
      query.select("#" + targetId).boundingClientRect((data) => {
        resolve(data || null);
      }).exec();
    });
  }

  async globalQuery(targetId) {
    return new Promise((resolve) => {
      const query = uni.createSelectorQuery();
      query.select("#" + targetId).boundingClientRect((data) => {
        resolve(data || null);
      }).exec();
    });
  }
}

export default DomQueryService.getInstance();
