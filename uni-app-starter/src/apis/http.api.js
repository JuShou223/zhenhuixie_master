import * as common from './modules/common.js'
import * as story from './modules/story.js'
import * as branch from './modules/branch.js'
import * as comment from './modules/comment.js'
import * as interaction from './modules/interaction.js'
import * as notification from './modules/notification.js'
import * as topic from './modules/topic.js'
import * as user from './modules/user.js'

const install = (app) => {
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
