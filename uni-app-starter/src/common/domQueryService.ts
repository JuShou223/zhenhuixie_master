import type { ComponentInternalInstance } from 'vue'

interface HandlerEntry {
  item: ((targetId: string) => Promise<UniApp.NodeInfo | null>) | Record<string, unknown>
  priority: number
}

class DomQueryService {
  private static instance: DomQueryService
  private queryHandlers: Map<string, HandlerEntry[]>
  private activePageInstance: ComponentInternalInstance | null
  private handlerCache: Map<string, UniApp.NodeInfo | null>

  constructor() {
    this.queryHandlers = new Map()
    this.activePageInstance = null
    this.handlerCache = new Map()
  }

  static getInstance(): DomQueryService {
    if (!DomQueryService.instance) {
      DomQueryService.instance = new DomQueryService()
    }
    return DomQueryService.instance
  }

  setActivePageInstance(instance: ComponentInternalInstance | null): void {
    this.activePageInstance = instance
    this.handlerCache.clear()
  }

  registerHandler(
    namespace: string,
    handlerOrInstance: ((targetId: string) => Promise<UniApp.NodeInfo | null>) | Record<string, unknown>,
    priority = 0
  ): void {
    if (!this.queryHandlers.has(namespace)) {
      this.queryHandlers.set(namespace, [])
    }
    const handlers = this.queryHandlers.get(namespace)!
    handlers.push({ item: handlerOrInstance, priority })
    handlers.sort((a, b) => b.priority - a.priority)
  }

  unregisterHandler(
    namespace: string,
    handlerOrInstance: ((targetId: string) => Promise<UniApp.NodeInfo | null>) | Record<string, unknown>
  ): void {
    if (!this.queryHandlers.has(namespace)) return
    const handlers = this.queryHandlers.get(namespace)!
    const index = handlers.findIndex((h) => h.item === handlerOrInstance)
    if (index > -1) handlers.splice(index, 1)
  }

  async query(targetId: string): Promise<UniApp.NodeInfo | null> {
    if (!targetId) return null

    const cacheKey = `${targetId}_${this.activePageInstance ? (this.activePageInstance as any).uid || 'page' : 'global'}`
    if (this.handlerCache.has(cacheKey)) return this.handlerCache.get(cacheKey)!

    let result: UniApp.NodeInfo | null = null

    if (targetId.includes(':')) {
      const [namespace, realTargetId] = targetId.split(':')
      result = await this.queryByNamespace(namespace, realTargetId)
    } else {
      result = await this.autoQuery(targetId)
    }

    if (result && (result as any).width > 0) {
      this.handlerCache.set(cacheKey, result)
      setTimeout(() => this.handlerCache.delete(cacheKey), 1000)
    }
    return result
  }

  private async queryByNamespace(namespace: string, targetId: string): Promise<UniApp.NodeInfo | null> {
    const handlers = this.queryHandlers.get(namespace) || []

    for (const { item } of handlers) {
      try {
        let result: UniApp.NodeInfo | null = null

        if (typeof item === 'function') {
          result = await item(targetId)
        } else if (item && typeof item === 'object') {
          result = await this.globalQueryInContext(targetId, item as unknown as ComponentInternalInstance)
        }

        if (result && ((result as any).width > 0) && ((result as any).height > 0)) {
          return result
        }
      } catch (error) {
        console.error(`[DomQueryService] Error querying namespace "${namespace}":`, error)
      }
    }

    return null
  }

  private async autoQuery(targetId: string): Promise<UniApp.NodeInfo | null> {
    if (this.activePageInstance) {
      const r = await this.globalQueryInContext(targetId, this.activePageInstance)
      if (r && (r as any).width > 0) return r
    }

    for (const [, handlers] of this.queryHandlers) {
      for (const { item } of handlers) {
        let res: UniApp.NodeInfo | null = null
        if (typeof item === 'function') {
          res = await item(targetId)
        } else if (item && typeof item === 'object') {
          res = await this.globalQueryInContext(targetId, item as unknown as ComponentInternalInstance)
        }
        if (res && (res as any).width > 0 && (res as any).height > 0) return res
      }
    }

    return await this.globalQuery(targetId)
  }

  private async globalQueryInContext(
    targetId: string,
    contextInstance: ComponentInternalInstance
  ): Promise<UniApp.NodeInfo | null> {
    return new Promise((resolve) => {
      let query = uni.createSelectorQuery()
      if (contextInstance) {
        query = query.in(contextInstance as any)
      }
      query.select('#' + targetId).boundingClientRect((data: UniApp.NodeInfo | null) => {
        resolve(data || null)
      }).exec()
    })
  }

  private async globalQuery(targetId: string): Promise<UniApp.NodeInfo | null> {
    return new Promise((resolve) => {
      const query = uni.createSelectorQuery()
      query.select('#' + targetId).boundingClientRect((data: UniApp.NodeInfo | null) => {
        resolve(data || null)
      }).exec()
    })
  }
}

export default DomQueryService.getInstance()
