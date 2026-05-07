/// <reference types="vite/client" />

declare module '*.vue' {
  import type { DefineComponent } from 'vue'
  const component: DefineComponent<object, object, unknown>
  export default component
}

declare module '*.css?inline' {
  const content: string
  export default content
}

declare module 'vue' {
  interface ComponentCustomProperties {
    uni: typeof uni
  }
}

declare global {
  const wx: any
  const plus: any
  const uni: any
}

export {}
