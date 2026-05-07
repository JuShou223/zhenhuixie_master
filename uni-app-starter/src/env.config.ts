interface ProjectConfig {
  baseUrl: string
}

const CONFIG: Record<string, ProjectConfig> = {
  development: {
    baseUrl: '/prod-api',
  },
  production: {
    baseUrl: '/prod-api',
  },
}

export default CONFIG[process.env.NODE_ENV || 'development'] as ProjectConfig
