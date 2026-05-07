import type { App } from 'vue'
import * as common from './modules/common'
import * as story from './modules/story'
import * as branch from './modules/branch'
import * as comment from './modules/comment'
import * as interaction from './modules/interaction'
import * as notification from './modules/notification'
import * as topic from './modules/topic'
import * as user from './modules/user'

const install = (app: App): void => {
  app.config.globalProperties.$api = {
    common,
    story,
    branch,
    comment,
    interaction,
    notification,
    topic,
    user,
  }
}

export default { install }
